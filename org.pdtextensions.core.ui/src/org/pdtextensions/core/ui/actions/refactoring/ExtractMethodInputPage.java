package org.pdtextensions.core.ui.actions.refactoring;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.php.internal.core.search.Messages;
import org.eclipse.php.internal.ui.editor.PHPStructuredTextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pdtextensions.core.ui.refactoring.RefactoringMessages;

@SuppressWarnings("restriction")
public class ExtractMethodInputPage extends UserInputWizardPage {

	
	public static final String PAGE_NAME = RefactoringMessages.ExtractMethod_name;
	public static final String LABEL_METHOD_NAME = RefactoringMessages.ExtractMethodPage_LabelMethod;
	
	private static final String DESCRIPTION = RefactoringMessages.ExtractMethodInputPage_description;
	private static final String THROW_RUNTIME_EXCEPTIONS= "ThrowRuntimeExceptions"; //$NON-NLS-1$
	private static final String GENERATE_PHPDOC= "GenerateJavadoc";  //$NON-NLS-1$
	private static final String ACCESS_MODIFIER= "AccessModifier"; //$NON-NLS-1$
	
	private IDialogSettings fSettings;
	private Text fTextField;
	private ExtractMethodRefactoring fRefactoring;
	private PHPStructuredTextViewer fPreviewViewer;
	
	public ExtractMethodInputPage() {
		super(PAGE_NAME);
	}

	@Override
	public void createControl(Composite parent) {
		fRefactoring= (ExtractMethodRefactoring) getRefactoring();
		loadSettings();
		
		Composite result = setUpTwoColumns(parent);
		
		initializeDialogUnits(result);
		
		addTextinputForMethodName(result);
		
		addInputForAccessModifiers(result);
		
		addParameterInput(result);
		
		addCheckboxForGeneratingPHPComment(result);
		
		addCheckboxForAddingTypeHint(result);
		
		addCheckboxForReplacingDuplicates(result);
		
		addCheckboxForReturningMultipleVariables(result);
		
		addSeparator(result);
		
		addMethodSignaturePreview(result);
		
		updatePreview();
	}

	private void addCheckboxForAddingTypeHint(Composite result) {
		GridData gridLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		gridLayoutData.horizontalSpan= 2;
		
		Button checkBox = new Button(result, SWT.CHECK);
		
		checkBox.setLayoutData(gridLayoutData);
		checkBox.setText(RefactoringMessages.ExtractMethodInputPage_addTypeHint);
		checkBox.setSelection(fRefactoring.getTypeHint());
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fRefactoring.setTypeHint(((Button)e.widget).getSelection());
				updatePreview();
			}
		});
	}

	public void addParameterInput(Composite result) {
		if (!fRefactoring.getParameterInfos().isEmpty()) {
			
			/*ChangeParametersControl cp= new ChangeParametersControl(result, SWT.NONE,
				RefactoringMessages.ExtractMethodInputPage_parameters,
				new IParameterListChangeListener() {
				public void parameterChanged(ParameterInfo parameter) {
					parameterModified();
				}
				public void parameterListChanged() {
					parameterModified();
				}
				public void parameterAdded(ParameterInfo parameter) {
					updatePreview(getText());
				}
			}, ChangeParametersControl.Mode.EXTRACT_METHOD);
			GridData gd= new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan= 2;
			cp.setLayoutData(gd);
			cp.setInput(fRefactoring.getParameterInfos());
			*/
		}
	}

	public Composite setUpTwoColumns(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);
		setControl(result);
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		result.setLayout(layout);
		return result;
	}

	public void addSeparator(Composite result) {
		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan= 2;
		
		Label label= new Label(result, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(gd);
	}

	public void addInputForAccessModifiers(Composite result) {
		Label label= new Label(result, SWT.NONE);
		label.setText(RefactoringMessages.ExtractMethodInputPage_access_Modifiers);
		
		Composite group= new Composite(result, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout= new GridLayout();
		layout.numColumns= 3; layout.marginWidth= 0;
		group.setLayout(layout);

		String[] labels= new String[] {
			RefactoringMessages.ExtractMethodInputPage_access_public,
			RefactoringMessages.ExtractMethodInputPage_access_protected,
			RefactoringMessages.ExtractMethodInputPage_access_private
		};
		Integer[] data= new Integer[] {new Integer(Modifiers.AccPublic), new Integer(Modifiers.AccProtected), new Integer(Modifiers.AccPrivate)};
		Integer selectionOfAccessModifiers = fRefactoring.getAccessOfModifiers();
		for (int i= 0; i < labels.length; i++) {
			Button radio= new Button(group, SWT.RADIO);
			radio.setText(labels[i]);
			radio.setData(data[i]);
			if ( (data[i] & selectionOfAccessModifiers) != 0)
				radio.setSelection(true);
			
			radio.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					final Integer selectedModifier= (Integer)event.widget.getData();
					//fSettings.put(ACCESS_MODIFIER, selectedModifier.intValue());
					fRefactoring.setAccessOfModifiers(selectedModifier);
					updatePreview();
				}
			});
		}
	}

	public void addTextinputForMethodName(Composite result) {
		Label label = new Label(result, SWT.NONE);
		label.setText(LABEL_METHOD_NAME);
		
		fTextField = new Text(result, SWT.BORDER);
		
		fTextField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fRefactoring.setMethodName(fTextField.getText());
				updatePreview();
				
				if(fTextField.getText().trim().length() == 0) {
					setPageComplete(false);
				} else {
					setPageComplete(true);
				}
			}			
		});
		
		fTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTextField.setText("");
	}
	
	public void addCheckboxForReplacingDuplicates(Composite result) {
		
		GridData gridLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		gridLayoutData.horizontalSpan= 2;
		
		int duplicates= fRefactoring.getNumberOfDuplicates();
		Button checkBox= new Button(result, SWT.CHECK);
		
		checkBox.setLayoutData(gridLayoutData);
		
		if (duplicates == 0) {
			checkBox.setText(RefactoringMessages.ExtractMethodInputPage_duplicates_none);
		} else  if (duplicates == 1) {
			checkBox.setText(RefactoringMessages.ExtractMethodInputPage_duplicates_single);
		} else {
			checkBox.setText(Messages.format(RefactoringMessages.ExtractMethodInputPage_duplicates_multi, duplicates));
		}
		
		checkBox.setSelection(fRefactoring.getReplaceDuplicates());
		checkBox.setEnabled(duplicates > 0);
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fRefactoring.setReplaceDuplicates(((Button)e.widget).getSelection());
			}
		});
	}
	

	private void addCheckboxForReturningMultipleVariables(Composite result) {
		GridData gridLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		gridLayoutData.horizontalSpan= 2;
		
		Button checkBox= new Button(result, SWT.CHECK);
		
		checkBox.setLayoutData(gridLayoutData);
		checkBox.setText(RefactoringMessages.ExtractMethodInputPage_returnMultipleVariables);
		
		checkBox.setToolTipText("Passing arguments by reference is discouraged");
		checkBox.setSelection(fRefactoring.getMethodReturnsMultipleVariables());
		checkBox.setEnabled(fRefactoring.getMethodReturnVariablesCount() > 1);
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fRefactoring.setMethodReturnsMultipleVariables(((Button)e.widget).getSelection());
				updatePreview();
			}
		});
	}

	public void addCheckboxForGeneratingPHPComment(Composite result) {
		
		GridData gridLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		gridLayoutData.horizontalSpan= 2;
		
		Button checkBox = new Button(result, SWT.CHECK);
		
		checkBox.setLayoutData(gridLayoutData);
		checkBox.setText(RefactoringMessages.ExtractMethodInputPage_generatePhpdocComment);
		checkBox.setSelection(false);// fRefactoring.getGenerateDocu());
		checkBox.setEnabled(false);
		/*checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setGenerateJavadoc(((Button)e.widget).getSelection());
			}
		});*/
	}
	
	private void addMethodSignaturePreview(Composite composite) {
		Label previewLabel= new Label(composite, SWT.NONE);
		previewLabel.setText(RefactoringMessages.ExtractMethodInputPage_signature_preview);

		
		fPreviewViewer = new PHPStructuredTextViewer(composite, null, null, false, SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		fPreviewViewer.setDocument(new Document());
		//fPreviewViewer.adaptBackgroundColor(composite);
		
		StyledText textWidget = fPreviewViewer.getTextWidget();
		
		textWidget.setAlwaysShowScrollBars(false);
		
		GridData gdata= new GridData(GridData.FILL_BOTH);
		gdata.widthHint= new PixelConverter(textWidget).convertWidthInCharsToPixels(50);
		gdata.heightHint= textWidget.getLineHeight() * 2;
		gdata.horizontalSpan=2;
		textWidget.setLayoutData(gdata);
		
		textWidget.setBackground(composite.getBackground());		
	}
	
	private void updatePreview() {
		if (fPreviewViewer == null)
			return;

		int top = fPreviewViewer.getTextWidget().getTopPixel();
		
		String signature;
		try {
			signature = fRefactoring.getMethodSignature();
		} catch (IllegalArgumentException e) {
			signature = ""; //$NON-NLS-1$
		}
		
		if(signature == null) {
			signature = "";
		}
		
		fPreviewViewer.getDocument().set(signature);
		fPreviewViewer.getTextWidget().setTopPixel(top);
	}
		
	private void loadSettings() {
		
		/*
		fSettings = getDialogSettings().getSection(ExtractMethodWizard.DIALOG_SETTING_SECTION);
		if (fSettings == null) {
			fSettings = getDialogSettings().addNewSection(ExtractMethodWizard.DIALOG_SETTING_SECTION);
			fSettings.put(THROW_RUNTIME_EXCEPTIONS, false);
			fSettings.put(ACCESS_MODIFIER, Modifiers.AccPrivate);
		}
		
		final String accessModifier = fSettings.get(ACCESS_MODIFIER);
		
		if (accessModifier != null) {
			fRefactoring.setAccessOfModifiers(Integer.parseInt(accessModifier));
		}*/
	}
	

}
