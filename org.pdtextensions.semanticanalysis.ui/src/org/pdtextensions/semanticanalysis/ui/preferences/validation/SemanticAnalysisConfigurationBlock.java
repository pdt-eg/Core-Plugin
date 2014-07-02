/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.ui.preferences.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.pdtextensions.core.ui.preferences.AbstractOptionsConfigurationBlock;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.PreferenceConstants;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Type;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.validation.IValidatorManager;

@SuppressWarnings("restriction")
public class SemanticAnalysisConfigurationBlock extends AbstractOptionsConfigurationBlock {

	@Inject
	private IValidatorManager manager;

	private Composite fieldEditorParent;
	private Combo[] fields;
	private Button analysisEnabled;
	private PixelConverter pixelConverter;

	public SemanticAnalysisConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	private static Key[] getKeys() {
		Validator[] validators = PEXAnalysisPlugin.getDefault().getValidatorManager().getValidators();
		List<Key >res = new ArrayList<Key>();
		res.add(getKey(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID, PreferenceConstants.ENABLED));
		for (Validator v : validators) {
			for (Type t : v.getTypes()) {
				
				res.add(getKey(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID + "/" + v.getId(), t.getId()));
			}
		}
		return res.toArray(new Key[res.size()]);
	}

	@Override
	public Control createBlockContents(Composite parent) {
		if (pixelConverter == null) {
			pixelConverter = new PixelConverter(parent);
			ContextInjectionFactory.inject(this, PEXAnalysisPlugin.getEclipseContext());
		}

		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new FillLayout(SWT.VERTICAL));

		createFields(content);
		updateFieldVisibility();

		return content;
	}
	
	protected String[] getSeverityLabels() {
		return new String[] {
				"Error", "Warning", "Info", "Ignore"
		};
	}
	
	protected String[] getSeverityValues() {
		return new String[] {
				ProblemSeverity.ERROR.toString(), 
				ProblemSeverity.WARNING.toString(), 
				ProblemSeverity.INFO.toString(), 
				ProblemSeverity.IGNORE.toString()
		};
	}

	private Composite createFields(Composite parent) {

		Composite fieldEditorParentWrap = new Composite(parent, SWT.NULL);
		GridLayout wrapLayout = new GridLayout();
		wrapLayout.marginHeight = 0;
		wrapLayout.marginWidth = 0;
		fieldEditorParentWrap.setLayout(wrapLayout);
		fieldEditorParentWrap.setFont(parent.getFont());
		
		fieldEditorParent = new Composite(fieldEditorParentWrap, SWT.NULL);
		fieldEditorParent.setLayout(new GridLayout());
		fieldEditorParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fieldEditorParent.setFont(parent.getFont());
		
		
		
		analysisEnabled = addCheckBox(fieldEditorParent, "Enable semantic analysis", fAllKeys[0], new String[] {"true", "false"}, 0); //$NON-NLS-2$ $NON-NLS-3$
		analysisEnabled.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFieldVisibility();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateFieldVisibility();
			}
		});
		
		final Label horizontalLine = new Label(fieldEditorParent, SWT.SEPARATOR | SWT.HORIZONTAL);
		horizontalLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		horizontalLine.setFont(fieldEditorParent.getFont());

		fields = new Combo[fAllKeys.length -1];
		int i = 0;
		for (Category category : manager.getCategories()) {
			ExpandableComposite group = createGroup(1, fieldEditorParent, category.getLabel());
			Composite inner = new Composite(group, SWT.NONE);
			inner.setFont(parent.getFont());
			inner.setLayout( new GridLayout(3, false));
			inner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setClient(inner);

			for (Validator v : category.getValidators()) {
				for (Type t : v.getTypes()) {
					Combo combo = fields[i] = addComboBox(inner, t.getLabel(), new Key(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID + "/" + v.getId(), t.getId()), getSeverityValues(), getSeverityLabels());
					Label object = (Label)fLabels.get(combo);
					object.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
					i++;
				}
			}
		}

		return fieldEditorParentWrap;
	}

	protected ExpandableComposite createGroup(int numColumns, Composite parent, String label) {
		ExpandableComposite excomposite= new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));
		excomposite.setExpanded(true);
		excomposite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});

		return excomposite;
	}

	protected void updateFieldVisibility() {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null) {
				fields[i].setEnabled(analysisEnabled.getSelection());
			}
		}
	}
}
