/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.PHPCoreConstants;
import org.eclipse.php.internal.core.PHPCorePlugin;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.format.FormatPreferencesSupport;
import org.eclipse.php.internal.ui.actions.SelectionHandler;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.pdtextensions.core.ui.dialog.GetterSetterDialog;
import org.pdtextensions.core.ui.util.GetterSetterUtil;
import org.pdtextensions.core.util.PDTFormatterUtils;


/**
 * 
 * Generates getters for private fields.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class GenerateGettersHandler extends SelectionHandler implements IHandler {
	
	private IEditorPart editorPart;
	private PHPStructuredEditor textEditor;
	private IDocument document;
	
    private Map options;
	private SourceType type;
	private String lineDelim;
	private boolean insertFirst = false;
	private IModelElement insertAfter = null;
	
	
	@SuppressWarnings("rawtypes")
	private static class GetterSetterContentProvider implements ITreeContentProvider {

		private Map fields;
		private static final Object[] EMPTY = new Object[0];
		
		public GetterSetterContentProvider(Map fields) {

			this.fields = fields;
		}

		@Override
		public void dispose() {
			fields.clear();
			fields = null;			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
						
		}

		@Override
		public Object[] getElements(Object inputElement) {

			return fields.keySet().toArray();
			

		}

		@Override
		public Object[] getChildren(Object parentElement) {
			
			if (parentElement instanceof IField) {
			    
			    Object[] elems = (Object[]) fields.get(parentElement);

			    for (Object elem : elems) {
			        GetterSetterEntry entry = (GetterSetterEntry) elem;
			    }
				return elems;
			}
			
			return EMPTY;
		}

		@Override
		public Object getParent(Object element) {

			if (element instanceof IMember) {
				return ((IMember) element).getDeclaringType();
			}
			if (element instanceof GetterSetterEntry)
				return ((GetterSetterEntry) element).field;
			
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
 
			return getChildren(element).length > 0;

		}
	}
	
	private class GetterSetterLabelProvider extends LabelProvider {

		
		@Override
		public Image getImage(Object element) {
			
			if (element instanceof GetterSetterEntry) {				
				return DLTKUIPlugin.getImageDescriptorRegistry().get(DLTKPluginImages.DESC_FIELD_PUBLIC);
			} else if (element instanceof IField){
				return DLTKUIPlugin.getImageDescriptorRegistry().get(DLTKPluginImages.DESC_FIELD_PRIVATE);
				
			}
			return super.getImage(element);

		}
		@Override
		public String getText(Object element) {

			if (element instanceof GetterSetterEntry) {
				GetterSetterEntry entry = (GetterSetterEntry) element;
				return entry.getName();
			} else if (element instanceof IField){
				return GetterSetterUtil.getFieldName(((IField)element));
				
			}
			
			return super.getText(element);
		}
	}
	
	protected Object exitMessage()
	{
	    final Shell p = DLTKUIPlugin.getActiveWorkbenchShell();
	    MessageDialog.openInformation(p, "Getter/Setter generation unavailable", "No fields to generate getters/setters found.");
	    return null;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IModelElement element = getCurrentModelElement(event);
		
		if (element == null) {			
		    return exitMessage();
		}
		
		if (!(element instanceof SourceType)) {			
			while(element.getParent() != null) {				
				element = element.getParent();				
				if (element instanceof SourceType) {
					break;
				}
			}
		}
		
		if (element == null || !(element instanceof SourceType)) {
			return exitMessage();
		}
		
		type = (SourceType) element;
		
		try {

			if (type.getFields().length == 0) {
			    return exitMessage();
			}

			initialize(event, element);
			
			Map fields = getFields();			
			final Shell p = DLTKUIPlugin.getActiveWorkbenchShell();
			GetterSetterContentProvider cp = new GetterSetterContentProvider(fields);
			GetterSetterDialog dialog = new GetterSetterDialog(p, new GetterSetterLabelProvider(), cp, type);
			
			dialog.setContainerMode(true);
			dialog.setInput(type);
			dialog.setTitle("Generate Getters and Setters");
			
			if (fields.size() == 0) {
			    return exitMessage();
			}
			
			if (dialog.open() == Window.OK) {
				
				List<GetterSetterEntry> entries = new ArrayList<GetterSetterEntry>();				
				Object[] dialogResult = dialog.getResult();
				
				for (Object o : dialogResult) {					
					if (o instanceof GetterSetterEntry) {				
						entries.add((GetterSetterEntry) o);						
					}
				}
				
				insertFirst = false;
				insertAfter = null;
				
				if (dialog.insertAsFirstMember())
					insertFirst = true;
				else if (dialog.insertAsLastMember()) {
				} else insertAfter = dialog.getInsertionPoint();
				
				generate(entries, dialog.getModifier(), dialog.doGenerateComments(), dialog.hasFluentInterface());
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	private void generate(final List<GetterSetterEntry> entries, final int modifier, final boolean generateComments, final boolean fluent) throws Exception {

		ISourceModule source = type.getSourceModule();			
		String name = type.getElementName().replace("$", "");			
		StringBuffer buffer = new StringBuffer(name);			
		buffer.replace(0, 1, Character.toString(Character.toUpperCase(name.charAt(0))));			
		name = buffer.toString();			
					
		ASTParser parser = ASTParser.newParser(source);
		parser.setSource(document.get().toCharArray());
		
		Program program = parser.createAST(new NullProgressMonitor());			
//		program.recordModifications();		
//		AST ast = program.getAST();		

		ISourceRange range = type.getSourceRange();		
		ASTNode node = program.getElementAt(range.getOffset());

		if (!(node instanceof ClassDeclaration)) {
			return ;				
		}
		
		char indentChar = FormatPreferencesSupport.getInstance().getIndentationChar(document);
		String indent = String.valueOf(indentChar);
		
		ClassDeclaration clazz = (ClassDeclaration) node;
		Block body = clazz.getBody();
		List<Statement> bodyStatements = body.statements();
				

		int end = bodyStatements.get(bodyStatements.size()-1).getEnd();
		
		if (insertFirst) {
			end = bodyStatements.get(0).getStart() - 1;
		} else if (insertAfter != null) {
			
			boolean found = false;
			
			for (IMethod method : type.getMethods()) {				
				if (method == insertAfter) {
					ISourceRange r = method.getSourceRange();
					end = r.getOffset() + r.getLength();
					found = true;
				}
			}
			
			if (!found) {				
				for (IField field : type.getFields()) {
					ISourceRange r = field.getSourceRange();
					end = r.getOffset() + r.getLength() + 1;
				}
			}
		}
		
		lineDelim = TextUtilities.getDefaultLineDelimiter(document);
		String methods = "";
		
		int i = 0;
		for (GetterSetterEntry entry : entries) {
			
			String code = "";
			
			if (!entry.isGetter) {
				code = GetterSetterUtil.getSetterStub(entry.field, 
						entry.getIdentifier(), entry.getType(), generateComments, modifier, indent, fluent);
				
			} else {
				code = GetterSetterUtil.getGetterStub(entry.field, 
						entry.getIdentifier(), generateComments, modifier, indent);				
			}		
			
			code = lineDelim + code;
			String formatted = indentPattern(code, indent, lineDelim);
			
			if (i++ == 0) {
				formatted = lineDelim + formatted;
			}
			
			methods+= formatted;

			
		}						
		
		document.replace(end, 0, methods);
		PDTFormatterUtils.format(document, new Region(end, methods.length()), type.getScriptProject().getProject());
	}
	
	private String indentPattern(String originalPattern, String indentation,
			String lineDelim) {
		
		String delimPlusIndent = lineDelim + indentation;
		String indentedPattern = originalPattern.replaceAll(lineDelim,delimPlusIndent) + delimPlusIndent;

		return indentedPattern;
	}
	


	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupOptions() {

		options = new HashMap(PHPCorePlugin.getOptions());
		
		IScopeContext[] contents = new IScopeContext[] {
				new ProjectScope(type
						.getScriptProject()
						.getProject()),
						InstanceScope.INSTANCE, DefaultScope.INSTANCE };
		
		for (int i = 0; i < contents.length; i++) {
			
			IScopeContext scopeContext = contents[i];
			IEclipsePreferences inode = scopeContext.getNode(PHPCorePlugin.ID);
			
			if (inode != null) {
				
				if (!options.containsKey(PHPCoreConstants.FORMATTER_USE_TABS)) {
					
					String useTabs = inode.get(PHPCoreConstants.FORMATTER_USE_TABS,null);
					if (useTabs != null) {
						options.put(PHPCoreConstants.FORMATTER_USE_TABS, useTabs);
					}
				}
				
				if (!options.containsKey(PHPCoreConstants.FORMATTER_INDENTATION_SIZE)) {
					
					String size = inode.get(PHPCoreConstants.FORMATTER_INDENTATION_SIZE,null);
					
					if (size != null) {
						options.put(PHPCoreConstants.FORMATTER_INDENTATION_SIZE,size);
					}
				}
			}
		}
	}
	
	
	private void initialize(ExecutionEvent event, IModelElement element) throws InvalidElementException {
		
		editorPart = HandlerUtil.getActiveEditor(event);
		textEditor = null;
		if (editorPart instanceof PHPStructuredEditor)
			textEditor = (PHPStructuredEditor) editorPart;
		else {
			Object o = editorPart.getAdapter(ITextEditor.class);
			if (o != null)
				textEditor = (PHPStructuredEditor) o;
		}
		
		document = textEditor.getDocument();
		
		setupOptions();

	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getFields() throws ModelException {
		
		IField[] fields = type.getFields();
		Map result = new LinkedHashMap();
		for (int i = 0; i < fields.length; i++) {
		    		    
			IField field = fields[i];
			
			int flags = field.getFlags();
			if (PHPFlags.isStatic(flags) || PHPFlags.isConstant(flags)) {
			    continue;
			}
			
			String name = field.getElementName().replace("$", "");
            String getter = "get" + name.toLowerCase();
            String setter = "set" + name.toLowerCase();
			
			List l = new ArrayList(2);
			List<GetterSetterEntry> entries = new ArrayList<GetterSetterEntry>();
			
			boolean getterExists = false;
			boolean setterExists = false;
			
            for (IMethod method : type.getMethods()) {
                String methodName = method.getElementName().toLowerCase();
                if (methodName.startsWith("get")) {
                    getterExists = methodName.equals(getter);    
                } else if (methodName.startsWith("set")) {
                    setterExists = methodName.equals(setter);    
                }
            }
			
            if (!getterExists) {
                entries.add(new GetterSetterEntry(field, true));
            }
            
            if (!setterExists) {
                entries.add(new GetterSetterEntry(field, false));
            }
            
            if (!getterExists || !setterExists) {
                l.addAll(entries);    
            }
            
			if (!l.isEmpty())
				result.put(field, l.toArray(new GetterSetterEntry[l.size()]));

		}

		return result;
		
	}
	
	private class InvalidElementException extends Exception {

		private static final long serialVersionUID = 1L;
		
	}
	
	public static class GetterSetterEntry {
		
		
		public final IField field;
		public final boolean isGetter;
		private String name = null;
		private String identifier = null;
		private String type = null;
		private String raw = null;
		
		public GetterSetterEntry(IField field, boolean isGetterEntry) {
		
			this.field = field;
			this.isGetter = isGetterEntry;
		}
		
		public String getIdentifier() {
			
			if (identifier != null)
				return identifier;
			
			if (isGetter)
				return identifier = GetterSetterUtil.getGetterName(field);
			else return identifier = GetterSetterUtil.getSetterName(field);
			
		}
		
		public String getType() {

			if (type != null)
				return type;
			
			return type = GetterSetterUtil.getTypeReference(field);
			
		}
		
		public String getName() {

			if (name != null)
				return name;
			
			if (isGetter) {
				return name = String.format("%s()", getIdentifier());
			} else {
			    String elemName = field.getElementName();
			    String type = getType();
			    String param = "";
			    if (type != null && type != "") {			       
			        param += type +" ";
			    }
			    param += elemName;
				return name = String.format("%s(%s)", getIdentifier(), param);
			}
		}


		public String getRawFieldName() {

			if (raw != null)
				return raw;
			
			return raw = field.getElementName().replace("$", "");
			
		}
		
	}
}
