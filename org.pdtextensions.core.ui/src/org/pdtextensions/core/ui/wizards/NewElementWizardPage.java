package org.pdtextensions.core.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.ui.dialogs.OpenTypeSelectionDialog2;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ITreeListAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.dltk.ui.wizards.NewSourceModulePage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.ui.PHPUILanguageToolkit;
import org.eclipse.php.internal.ui.PHPUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.core.ui.ExtensionManager;
import org.pdtextensions.core.ui.ast.Formatter;
import org.pdtextensions.core.ui.extension.INamespaceResolver;
import org.pdtextensions.core.ui.util.PHPContentProposalAdapter;
import org.pdtextensions.core.ui.util.PHPContentProposalProvider;
import org.pdtextensions.core.ui.util.PHPTypeContentProposal;
import org.pdtextensions.core.ui.util.PHPTypeSelectionExtension;

@SuppressWarnings("restriction")
public abstract class NewElementWizardPage extends NewSourceModulePage {

	protected static final int IS_ABSTRACT_INDEX = 0;
	protected static final int IS_FINAL_INDEX = 1;

	private static final int INTERFACES = 1;
	private static final int CLASSES = 2;

	protected static final String NAME = "ClassName";
	private static final String FILENAME = "NewElementWizardPage.filename";

	private Composite container;
	private int nColumns;

	protected ISelection selection;
	protected StatusInfo status;
	protected String initialClassName = null;
	protected String initialNamespace = null;
	protected String initialFilename = null;
	protected IScriptFolder initialFolder = null;
	private StringDialogField classNameField;
	private StringDialogField fileNameField;
	private StringButtonDialogField superClassField;
	private IType superclass;
	private StringDialogField namespaceField;
	private SelectionButtonDialogFieldGroup classModifierField;
	private TreeListDialogField interfaceDialog;
	private SelectionButtonDialogFieldGroup methodStubButtons;
	private SelectionButtonDialogField commentsButton;
	private AutoCompleteField acField2;

	public NewElementWizardPage() {
		super();
	}

	private OpenTypeSelectionDialog2 getDialog(int type, String title, String message, boolean multi) {
		int falseFlags = 0;
		int trueFlags = 0;

		IDLTKSearchScope scope = SearchEngine.createSearchScope(getScriptFolder().getScriptProject());

		falseFlags = (type == NewElementWizardPage.CLASSES ? PHPFlags.AccTrait | PHPFlags.AccInterface
				| PHPFlags.AccNameSpace : 0);
		trueFlags = (type == NewElementWizardPage.INTERFACES ? PHPFlags.AccInterface : 0);

		OpenTypeSelectionDialog2 dialog = new OpenTypeSelectionDialog2(DLTKUIPlugin.getActiveWorkbenchShell(), multi,
				PlatformUI.getWorkbench().getProgressService(), scope, IDLTKSearchConstants.TYPE,
				new PHPTypeSelectionExtension(trueFlags, falseFlags), PHPUILanguageToolkit.getInstance());

		dialog.setTitle(title);
		dialog.setMessage(message);

		return dialog;
	}

	@Override
	protected void createContentControls(Composite composite, int nColumns) {

		container = composite;
		this.nColumns = nColumns;

		createContainerControls(composite, nColumns);

		createControls();

		if (initialFolder != null) {
			setScriptFolder(initialFolder, true);
		}

	}
	
	abstract protected void createControls();

	protected void createMethodStubControls() {

		GridData gd;
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = nColumns;
		Label label = new Label(container, SWT.None);
		label.setText("Which method stubs would you like to create?");
		label.setLayoutData(gd);

		setMethodStubButtons(new SelectionButtonDialogFieldGroup(SWT.CHECK, new String[] { "Su&perclass constructor",
				"In&herited abstract methods" }, 1));
		methodStubButtons.doFillIntoGrid(container, nColumns - 1);
		DialogField.createEmptySpace(container);

		methodStubButtons.setSelection(1, true);

		// methodStubButtons.setEnabled(false);
	}

	protected void createCommentsControls() {
		GridData gd;
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = nColumns;
		Label commentLabel = new Label(container, SWT.NONE);
		commentLabel.setText("Do you want to add comments?");
		commentLabel.setLayoutData(gd);

		DialogField.createEmptySpace(container);
		setCommentsButton(new SelectionButtonDialogField(SWT.CHECK));
		commentsButton.setLabelText("&Generate element comments");
		commentsButton.doFillIntoGrid(container, nColumns - 1);
	}

	protected void createInterfaceControls(String label) {

		String[] buttons = { "&Add...", "&Remove" };
		interfaceDialog = new TreeListDialogField(new ITreeListAdapter() {

			@Override
			public void selectionChanged(TreeListDialogField field) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(TreeListDialogField field, KeyEvent event) {
			}

			@Override
			public boolean hasChildren(TreeListDialogField field, Object element) {
				return false;
			}

			@Override
			public Object getParent(TreeListDialogField field, Object element) {
				return null;
			}

			@Override
			public Object[] getChildren(TreeListDialogField field, Object element) {
				return null;
			}

			@Override
			public void doubleClicked(TreeListDialogField field) {

			}

			@Override
			public void customButtonPressed(TreeListDialogField field, int index) {
				if (index == 0) {
					OpenTypeSelectionDialog2 dialog = getDialog(NewElementWizardPage.INTERFACES,
						"Interface selection",
						"Select interface",
						true);
					int result = dialog.open();
					if (result != IDialogConstants.OK_ID)
						return;
					Object[] types = dialog.getResult();
					for (Object type : types) {
						field.addElement((IType) type);
					}
				}

			}
		}, buttons, new ILabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void dispose() {

			}

			@Override
			public void addListener(ILabelProviderListener listener) {
			}

			@Override
			public String getText(Object element) {
				return ((IType) element).getFullyQualifiedName().replace("$", "\\");
			}

			@Override
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		interfaceDialog.setRemoveButtonIndex(1);
		interfaceDialog.setLabelText(label);

		interfaceDialog.doFillIntoGrid(container, nColumns);
	}

	protected void createClassModifierControls() {
		String[] buttonsName = { "abs&tract", "fina&l" };
		classModifierField = new SelectionButtonDialogFieldGroup(SWT.CHECK, buttonsName, nColumns);
		classModifierField.setLabelText("Modifiers:");
		classModifierField.doFillIntoGrid(container, nColumns);

		classModifierField.setDialogFieldListener(new IDialogFieldListener() {

			@Override
			public void dialogFieldChanged(DialogField field) {

				handleFieldChanged("");
			}
		});
	}

	protected StatusInfo classModifiedChanged() {
		StatusInfo status = new StatusInfo();
		if (classModifierField.getSelectionButton(IS_ABSTRACT_INDEX).getSelection()
				&& classModifierField.getSelectionButton(IS_FINAL_INDEX).getSelection()) {
			status.setError("A class cannot be abstract and final at the same time");
		}
		return status;
	}

	protected void createFileNameControls() {

		fileNameField = new StringDialogField();
		fileNameField.setLabelText("F&ilename:");
		fileNameField.doFillIntoGrid(container, nColumns - 1);
		DialogField.createEmptySpace(container);

		if (initialFilename != null) {
			fileNameField.setText(initialFilename);
		}
		
		fileNameField.setDialogFieldListener(new IDialogFieldListener() {

			@Override
			public void dialogFieldChanged(DialogField field) {
				status = filenameChanged();
				handleFieldChanged(FILENAME);
			}
		});
		
	}

	private StatusInfo filenameChanged() {
		StatusInfo status = new StatusInfo();
		if (!fileNameField.equals("") && getScriptFolder().getSourceModule(fileNameField.getText()).exists()) { //$NON-NLS-1$
			status.setError("The specified class already exists");
			return status;
		}

		int dotIndex = fileNameField.getText().lastIndexOf('.');
		if ((fileNameField.getText().length() == 0 || dotIndex == 0) && fileNameField.getText().length() > 0) {
			status.setError(PHPUIMessages.PHPFileCreationWizardPage_15);
			return status;
		}

		if (dotIndex != -1) {
			String fileNameWithoutExtention = fileNameField.getText().substring(0, dotIndex);
			for (int i = 0; i < fileNameWithoutExtention.length(); i++) {
				char ch = fileNameWithoutExtention.charAt(i);
				if (!(Character.isJavaIdentifierPart(ch) || ch == '.' || ch == '-')) {
					status.setError(PHPUIMessages.PHPFileCreationWizardPage_16); //$NON-NLS-1$
					return status;
				}
			}
		}
		return status;
	}

	protected void createNamespaceControls() {

		// TODO: Create dialog to get namespases.

		namespaceField = new StringDialogField();
		namespaceField.setLabelText("Na&mespace:");
		// namespaceField.setButtonLabel("Bro&wse...");
		namespaceField.doFillIntoGrid(container, nColumns - 1);
		DialogField.createEmptySpace(container);

		acField2 = new AutoCompleteField(namespaceField.getTextControl(), new TextContentAdapter(), null);

		namespaceField.setDialogFieldListener(new IDialogFieldListener() {

			@Override
			public void dialogFieldChanged(DialogField field) {
				List<String> props = new ArrayList<String>();
				IDLTKSearchScope scope = SearchEngine.createSearchScope(getScriptFolder().getScriptProject());
				IType[] types = PhpModelAccess.getDefault().findTypes(((StringDialogField) field).getText(),
					MatchRule.PREFIX,
					PHPFlags.AccNameSpace,
					0,
					scope,
					null);

				for (IType type : types) {
					props.add(type.getFullyQualifiedName());
				}

				acField2.setProposals((String[]) props.toArray(new String[props.size()]));
			}
		});

		
		if (initialNamespace != null && initialNamespace.length() > 0) {
			namespaceField.setText(initialNamespace);
			return;
		}
		
		List<INamespaceResolver> resolvers = ExtensionManager.getDefault().getNamespaceResolvers();

		IScriptFolder folder = getScriptFolder();

		// try to find a namespaceresolver and inject the resolved namespace
		for (INamespaceResolver resolver : resolvers) {
			try {
				String ns = resolver.resolve(folder);
				if (ns != null && ns.length() > 0) {
					namespaceField.setText(ns);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Logger.logException(e);
			}
		}
	}

	/**
	 * TODO: Create autocomplete on field. TODO: Add image decorator (small icon
	 * next to label pointing that autocomplete is avaiable)
	 */
	protected void createSuperClassControls() {

		superClassField = new StringButtonDialogField(new IStringButtonAdapter() {

			@Override
			public void changeControlPressed(DialogField field) {

				OpenTypeSelectionDialog2 dialog = getDialog(NewElementWizardPage.CLASSES,
					"Superclass selection",
					"Select superclass",
					false);
				int result = dialog.open();
				if (result != IDialogConstants.OK_ID)
					return;

				Object searchedObject[] = dialog.getResult();
				setSuperclass((IType) searchedObject[0]);
				((StringDialogField) field).setText(getSuperclass().getFullyQualifiedName().replace("$", "\\"));
			}
		});

		superClassField.setLabelText("&Superclass:");
		superClassField.setButtonLabel("Brows&e...");
		superClassField.doFillIntoGrid(container, nColumns);

		PHPContentProposalAdapter contentProposalAdapter = new PHPContentProposalAdapter(
				superClassField.getTextControl(), new TextContentAdapter(), new PHPContentProposalProvider(
						getScriptFolder().getScriptProject()), null, null);

		contentProposalAdapter.addContentProposalListener(new IContentProposalListener() {
			@Override
			public void proposalAccepted(IContentProposal proposal) {
				setSuperclass(((PHPTypeContentProposal) proposal).getObject());
			}
		});

		contentProposalAdapter.setProposalAcceptanceStyle(PHPContentProposalAdapter.PROPOSAL_REPLACE);
		contentProposalAdapter.setLabelProvider(new ILabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getText(Object element) {

				return ((IContentProposal) element).getContent();
			}

			@Override
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	protected void createNameControls() {

		classNameField = new StringDialogField();
		classNameField.setLabelText("&Name:");
		classNameField.doFillIntoGrid(container, nColumns - 1);
		DialogField.createEmptySpace(container);

		if (initialClassName != null && initialClassName.length() > 0) {
			classNameField.setText(initialClassName);
		}
		
		classNameField.setDialogFieldListener(new IDialogFieldListener() {

			@Override
			public void dialogFieldChanged(DialogField field) {

				status = nameChanged();
				handleFieldChanged(NAME);
			}
		});
		
	}

	protected StatusInfo nameChanged() {
		StatusInfo status = new StatusInfo();
		if (classNameField.getText().length() == 0) {
			status.setError("Enter name of class.");
			fileNameField.setTextWithoutUpdate("");
		} else {
			fileNameField.setText(classNameField.getText() + ".php");
		}

		if (classNameField.getText().length() > 0 && Character.isLowerCase(classNameField.getText().charAt(0))) {
			status.setWarning("Classes starting with lowercase letters are discouraged");
		}

		return status;
	}

	@Override
	protected String getRequiredNature() {

		return PHPNature.ID;

	}

	@Override
	protected String getFileName() {
		return fileNameField.getText();
		// return super.getFileName();
	}

	@Override
	protected void handleFieldChanged(String fieldName) {

		status.isOK();

		super.handleFieldChanged(fieldName);

		final List<IStatus> statuses = new ArrayList<IStatus>();
		if (containerStatus != null) {
			statuses.add(containerStatus);
		}

		// TODO: Check why few field aren't initialized here (during construct
		// object?)
//		if (fieldName != CONTAINER) {
//			statuses.add(classModifiedChanged());
//		}

		if (fieldName != CONTAINER && fieldName != FILENAME) {
			statuses.add(nameChanged());
		}
		statuses.add(status);

		updateStatus(statuses.toArray(new IStatus[statuses.size()]));
	}

	@Override
	protected void setFocus() {
		classNameField.setFocus();
	}

	public String getElementname() {
		return classNameField.getText();
	}

	protected boolean isAbstract() {
		return classModifierField.isSelected(IS_ABSTRACT_INDEX);
	}

	protected boolean isFinal() {
		return classModifierField.isSelected(IS_FINAL_INDEX);
	}

	public String getNamespace() {
		return namespaceField.getText();
	}

	public List<IType> getInterfaces() {
		return interfaceDialog.getElements();
	}

	public void setInterfaceDialog(TreeListDialogField interfaceDialog) {
		this.interfaceDialog = interfaceDialog;
	}

	public IType getSuperclass() {
		return superclass;
	}

	public void setSuperclass(IType superclass) {
		this.superclass = superclass;
	}

	protected boolean isGenerateConstructorStubs() {
		return methodStubButtons.isSelected(0);
	}
	
	public boolean isGenerateMethodStubs() {
		return methodStubButtons.isSelected(1);
	}

	public void setMethodStubButtons(SelectionButtonDialogFieldGroup methodStubButtons) {
		this.methodStubButtons = methodStubButtons;
	}

	public boolean isGenerateComments() {
		return commentsButton.isSelected();
	}

	public void setCommentsButton(SelectionButtonDialogField commentsButton) {
		this.commentsButton = commentsButton;
	}

	@Override
	protected String getFileContent(ISourceModule module) {
	
		IDocument doc = Formatter.createPHPDocument();
		Formatter formatter = new Formatter();
		doc.set(generateFileContent());
		formatter.format(doc);
	
		return doc.get();
	}

	abstract protected String generateFileContent();
	
	@Override
	public IScriptFolder getScriptFolder() {

		IScriptFolder folder = super.getScriptFolder();
		
		if (folder == null && initialFolder != null) {
			setScriptFolder(initialFolder, true);
			folder = initialFolder;
		}
		
		
		return folder;
	}

}