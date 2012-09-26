package org.pdtextensions.server.ui.internal.lhttpd;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.fragment.LicenseWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.pdtextensions.server.IPEXInstallableRuntime;
import org.pdtextensions.server.PEXServerPlugin;
import org.pdtextensions.server.lhttpd.ILHttpdRuntimeWorkingCopy;
import org.pdtextensions.server.ui.PEXServerUiPlugin;

@SuppressWarnings("restriction")
public class LHttpdRuntimeComposite extends Composite {

	private IWizardHandle wizard;
	private IRuntimeWorkingCopy runtimeWC;
	private ILHttpdRuntimeWorkingCopy runtime;
	private IPEXInstallableRuntime ir;
	protected Label installLabel;
	protected Button install;
	private Text name;
	private Text installDir;
	private Job installRuntimeJob;
	private JobChangeAdapter jobListener;

	public LHttpdRuntimeComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		
		wizard.setTitle(Messages.LHttpdRuntimeComposite_Title);
		wizard.setDescription(Messages.LHttpdRuntimeComposite_Description);
		// TODO
		/*wizard.setImageDescriptor(TomcatUIPlugin.getImageDescriptor(TomcatUIPlugin.IMG_WIZ_TOMCAT));*/
		
		createControl();
	}
	
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		setLayout(layout);
		setLayoutData(new GridData(GridData.FILL_BOTH));
		/*PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ContextIds.RUNTIME_COMPOSITE);*/
		
		Label label = new Label(this, SWT.NONE);
		label.setText(Messages.LHttpdRuntimeComposite_Name);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		
		name = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		name.setLayoutData(data);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtimeWC.setName(name.getText());
				validate();
			}
		});
	
		label = new Label(this, SWT.NONE);
		label.setText(Messages.LHttpdRuntimeComposite_InstallDir);
		data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
	
		installDir = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		installDir.setLayoutData(data);
		installDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtimeWC.setLocation(new Path(installDir.getText()));
				validate();
			}
		});
		
		Button browse = SWTUtil.createButton(this, Messages.LHttpdRuntimeComposite_Browse);
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(LHttpdRuntimeComposite.this.getShell());
				dialog.setMessage(Messages.LHttpdRuntimeComposite_SelectApacheXamppInstallDir);
				dialog.setFilterPath(installDir.getText());
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null)
					installDir.setText(selectedDirectory);
			}
		});
		
		installLabel = new Label(this, SWT.RIGHT);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalIndent = 10;
		installLabel.setLayoutData(data);
		
		install = SWTUtil.createButton(this, Messages.LHttpdRuntimeComposite_Install);
		install.setEnabled(false);
		install.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				String license = null;
				try {
					license = ir.getLicense(new NullProgressMonitor());
				} catch (CoreException e) {
					PEXServerPlugin.logError("Error getting license", e); //$NON-NLS-1$
				}
				TaskModel taskModel = new TaskModel();
				taskModel.putObject(LicenseWizardFragment.LICENSE, license);
				TaskWizard wizard2 = new TaskWizard(Messages.LHttpdRuntimeComposite_TitleDownloadAndInstall, new WizardFragment() {
					protected void createChildFragments(List<WizardFragment> list) {
						list.add(new LicenseWizardFragment());
					}
				}, taskModel);
				
				WizardDialog dialog2 = new WizardDialog(getShell(), wizard2);
				if (dialog2.open() == Window.CANCEL)
					return;
				
				DirectoryDialog dialog = new DirectoryDialog(LHttpdRuntimeComposite.this.getShell());
				dialog.setMessage(Messages.LHttpdRuntimeComposite_SelectXamppInstallDir);
				dialog.setFilterPath(installDir.getText());
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null) {
					final IPath installPath = new Path(selectedDirectory);
					installRuntimeJob = new Job(Messages.LHttpdRuntimeComposite_TaskInstallingRuntime) {
						public boolean belongsTo(Object family) {
							return PEXServerUiPlugin.PLUGIN_ID.equals(family);
						}
						
						protected IStatus run(IProgressMonitor monitor) {
							try {
								ir.install(installPath, monitor);
							} catch (CoreException ce) {
								return ce.getStatus();
							}
							
							return Status.OK_STATUS;
						}
					};
					
					installDir.setText(selectedDirectory);
					jobListener = new JobChangeAdapter() {
						public void done(IJobChangeEvent event) {
							installRuntimeJob.removeJobChangeListener(this);
							installRuntimeJob = null;
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									if (!isDisposed()) {
										validate();
									}
								}
					        });
						}
					};
					installRuntimeJob.addJobChangeListener(jobListener);
					installRuntimeJob.schedule();
				}
			}
		});
		
		init();
		validate();
		
		Dialog.applyDialogFont(this);
		
		name.forceFocus();
	}

	public void dispose() {
		super.dispose();
		if (installRuntimeJob != null) {
			installRuntimeJob.removeJobChangeListener(jobListener);
		}
	}

	public void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			runtimeWC = null;
			runtime = null;
		} else {
			runtimeWC = newRuntime;
			runtime = (ILHttpdRuntimeWorkingCopy) newRuntime.loadAdapter(ILHttpdRuntimeWorkingCopy.class, null);
		}
		
		install.setEnabled(false);
		installLabel.setText(""); //$NON-NLS-1$
		if (runtimeWC == null) {
			ir = null;
		} else {
			ir = runtime.getInstallableRuntime();
			if (ir != null) {
				install.setEnabled(true);
				installLabel.setText(ir.getName());
			}
		}
		
		init();
		validate();
	}

	protected void init() {
		if (name == null || runtime == null)
			return;
		
		if (runtimeWC.getName() != null)
			name.setText(runtimeWC.getName());
		else
			name.setText(""); //$NON-NLS-1$
		
		if (runtimeWC.getLocation() != null)
			installDir.setText(runtimeWC.getLocation().toOSString());
		else
			installDir.setText(""); //$NON-NLS-1$
	}

	protected void validate() {
		if (runtime == null) {
			wizard.setMessage("", IMessageProvider.ERROR); //$NON-NLS-1$
			return;
		}
		
		IStatus status = runtimeWC.validate(null);
		if (status == null || status.isOK())
			wizard.setMessage(null, IMessageProvider.NONE);
		else if (status.getSeverity() == IStatus.WARNING)
			wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
		else
			wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
		wizard.update();
	}

}
