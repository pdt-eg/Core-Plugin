/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.ui.preferences.validation;

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
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.PreferenceConstants;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Validator;

@SuppressWarnings("restriction")
public class SemanticAnalysisConfigurationBlock extends AbstractOptionsConfigurationBlock {

	@Inject
	private IValidatorManager manager;

	private Composite fieldEditorParent;
	private Combo[] fields;
	private Button analysisEnabled;
	private PixelConverter pixelConverter;

	protected final static Key getSKey(String key) {
		return getKey(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID, key);
	}

	public SemanticAnalysisConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	private static Key[] getKeys() {
		Validator[] validators = PEXAnalysisPlugin.getDefault().getValidatorManager().getValidators();
		Key[] keys = new Key[validators.length + 1];
		keys[0] = getSKey(PreferenceConstants.ENABLED);
		for (int i = 0; i < validators.length; i++) {
			keys[i+1] = getSKey(validators[i].getId());
		}
		return keys;
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

		fields = new Combo[manager.getValidators().length];
		int i = 0;
		boolean first = true;
		for (Category category : manager.getCategories()) {
			ExpandableComposite group = createGroup(1, fieldEditorParent, category.getLabel());
			group.setExpanded(first);
			first = false;
			Composite inner = new Composite(group, SWT.NONE);
			inner.setFont(parent.getFont());
			inner.setLayout( new GridLayout(2, false));
			inner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setClient(inner);

			for (Validator v : category.getValidators()) {
				fields[i] = addComboBox(inner, v.getLabel(), fAllKeys[i+1], getSeverityValues(), getSeverityLabels());
			}
		}

		return fieldEditorParentWrap;
	}

	protected ExpandableComposite createGroup(int numColumns, Composite parent, String label) {
		ExpandableComposite excomposite= new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		excomposite.setLayout(new FillLayout(SWT.VERTICAL));
		excomposite.setExpanded(false);
		excomposite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});

		return excomposite;
	}

	protected void updateFieldVisibility() {
		for (int i = 0; i < fields.length; i++) {
			fields[i].setEnabled(analysisEnabled.getSelection());
		}
	}
}
