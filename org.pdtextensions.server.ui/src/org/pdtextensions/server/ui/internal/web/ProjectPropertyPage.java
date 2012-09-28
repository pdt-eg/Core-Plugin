/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.pdtextensions.server.ui.internal.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.pdtextensions.server.PEXServerPlugin;
import org.pdtextensions.server.ui.internal.lhttpd.SWTUtil;
import org.pdtextensions.server.web.IPhpWebFolder;
import org.pdtextensions.server.web.IPhpWebProject;

/**
 * @author mepeisen
 *
 */
public class ProjectPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {
	protected IPhpWebProject project;
	private Text htdocs;
	private Button browse;
	private WebRootsList webRoots;
	private List<IPhpWebFolder> removedFolders = new ArrayList<IPhpWebFolder>();

	/**
	 * 
	 */
	public ProjectPropertyPage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		IAdaptable element = getElement();
		final IProject p = (IProject) element.getAdapter(IProject.class);
		try {
			this.project = PEXServerPlugin.create(p);
		} catch (CoreException e) {
			PEXServerPlugin.logError(e);
		}
		
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		layout.verticalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Composite composite2 = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		composite2.setLayout(layout);
		composite2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label label = new Label(composite2, SWT.WRAP);
		label.setText(Messages.ProjectPropertyPage_LabelHtdocsFolder);
		GridData data = new GridData(SWT.NONE);
		data.widthHint = 200;
		label.setLayoutData(data);
		
		this.htdocs = new Text(composite2, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		this.htdocs.setLayoutData(data);
		this.htdocs.setEditable(false);
		this.htdocs.setText(this.project.getDefaultWebFolder().getProjectRelativePath().toString());
		
		this.browse = SWTUtil.createButton(composite2, Messages.ProjectPropertyPage_Browse);
		this.browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
                ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new ProjectContentProvider());
                dialog.setTitle(Messages.ProjectPropertyPage_SelectHtdocsFolderTitle); 
                dialog.setMessage(Messages.ProjectPropertyPage_SelectHtdocsFolderDescription); 
                dialog.setInput(project.getEclipseProject().getWorkspace()); 
                dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
                dialog.setInitialSelection(project.getDefaultWebFolder());
                dialog.setAllowMultiple(false);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    IResource resource = (IResource) dialog.getFirstResult();
                    if (resource != null) {
                    	htdocs.setText(resource.getProjectRelativePath().toString());
                    	validate();
                    }
                }
			}
		});
		
		this.webRoots = new WebRootsList(composite, SWT.NONE);
		data = new GridData(GridData.FILL_BOTH);
		this.webRoots.setLayoutData(data);
		
		validate();
		
		return composite;
	}

	private void validate() {
		this.setErrorMessage(null);
		
		// validate htdocs
		if (this.htdocs.getText().length() > 0) {
			final IFolder htdocs = project.getEclipseProject().getFolder(this.htdocs.getText());
			if (!htdocs.isAccessible() || !htdocs.exists()) {
				this.setErrorMessage(NLS.bind(Messages.ProjectPropertyPage_ErrorHtdocsDoesNotExist, this.htdocs.getText()));
				return;
			}
		}
		
		final Set<String> paths = new HashSet<String>();
		for (final TableItem tableItem : webRoots.table.getItems()) {
			final WebRootItem item = (WebRootItem) tableItem.getData();
			if (!item.getPathName().startsWith("/")) { //$NON-NLS-1$
				this.setErrorMessage(NLS.bind(Messages.ProjectPropertyPage_ErrorPathMustStartWithSlash, item.getPathName()));
				return;
			}
			if (paths.contains(item.getPathName())) {
				this.setErrorMessage(NLS.bind(Messages.ProjectPropertyPage_ErrorDuplicatePath, item.getPathName()));
				return;
			}
			paths.add(item.getPathName());
			if (!item.getFolder().exists()) {
				this.setErrorMessage(NLS.bind(Messages.ProjectPropertyPage_ErrorHtdocsDoesNotExist, item.getFolder().getProjectRelativePath().toString()));
				return;
			}
		}
	}
	
	/**
	 * @author mepeisen
	 *
	 */
	private final class ProjectContentProvider extends WorkbenchContentProvider {
		@Override
		public Object[] getChildren(Object element) {
			if (element instanceof IWorkspace) {
				return new Object[]{project.getEclipseProject()};
			}
			final List<Object> result = new ArrayList<Object>();
			for (final Object elm : super.getChildren(element)) {
				if (elm instanceof IFolder) {
					result.add(elm);
				}
			}
			return result.toArray();
		}
	}

	private final class WebRootsList extends Composite {

		private Table table;
		private Button browseButton;
		private Button addButton;
		private Button removeButton;
		private TableEditor tableEditor;

		/**
		 * @param parent
		 * @param style
		 */
		public WebRootsList(Composite parent, int style) {
			super(parent, style);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			this.setLayout(layout);
			
			this.table = new Table(this, SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.VIRTUAL | SWT.SINGLE);
			this.table.setHeaderVisible(true);
			for (final IPhpWebFolder folder : project.getWebFolders()) {
				final TableItem item = new TableItem(table, SWT.NONE);
            	final WebRootItem rootItem = new WebRootItem(folder);
            	item.setText(0, rootItem.getPathName());
            	item.setText(1, rootItem.getFolder().getProjectRelativePath().toString());
            	item.setData(rootItem);
			}
			
			TableLayout tableLayout = new TableLayout();
			
			GridData data = new GridData(GridData.FILL_BOTH);
			this.table.setLayoutData(data);
			
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText(Messages.ProjectPropertyPage_TablePathName);
			ColumnWeightData colData = new ColumnWeightData(10, 100, true);
			tableLayout.addColumnData(colData);

			col = new TableColumn(table, SWT.NONE);
			col.setText(Messages.ProjectPropertyPage_TableHtdocsFolder);
			colData = new ColumnWeightData(10, 100, true);
			tableLayout.addColumnData(colData);
			table.setLayout(tableLayout);
			
			this.tableEditor = new TableEditor(table);
			this.tableEditor.horizontalAlignment = SWT.LEFT;
			this.tableEditor.grabHorizontal = true;
			this.tableEditor.minimumWidth = 50;
			
			this.table.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					final boolean enabled = table.getSelectionCount() == 1;
					browseButton.setEnabled(enabled);
					removeButton.setEnabled(enabled);
					
					final Control oldEditor = tableEditor.getEditor();
					if (oldEditor != null) oldEditor.dispose();
					
					if (enabled) {
						final TableItem item = WebRootsList.this.table.getSelection()[0];
						Text newEditor = new Text(table, SWT.NONE);
						newEditor.setText(item.getText(0));
						newEditor.addModifyListener(new ModifyListener() {
							public void modifyText(ModifyEvent e) {
								Text text = (Text)tableEditor.getEditor();
								String value = text.getText();
								if (!value.startsWith("/")) value = "/" + value;  //$NON-NLS-1$//$NON-NLS-2$
								tableEditor.getItem().setText(0, value);
								((WebRootItem)tableEditor.getItem().getData()).setPathName(value);
								validate();
							}
						});
						newEditor.selectAll();
						newEditor.setFocus();
						tableEditor.setEditor(newEditor, item, 0);
					}
				}
				
			});
			
			final Composite composite = new Composite(this, SWT.NONE);
			data = new GridData(GridData.FILL_VERTICAL);
			composite.setLayoutData(data);
			composite.setLayout(new GridLayout(1, false));
			
			this.browseButton = SWTUtil.createButton(composite, Messages.ProjectPropertyPage_Browse);
			this.browseButton.setEnabled(false);
			this.browseButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (WebRootsList.this.table.getSelection() == null || WebRootsList.this.table.getSelection().length == 0) return;
					ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new ProjectContentProvider());
					final TableItem item = WebRootsList.this.table.getSelection()[0];
	                dialog.setTitle(Messages.ProjectPropertyPage_SelectHtdocsFolderTitle); 
	                dialog.setMessage(Messages.ProjectPropertyPage_SelectHtdocsFolderDescription); 
	                dialog.setInput(project.getEclipseProject().getWorkspace()); 
	                dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
	                dialog.setInitialSelection(((WebRootItem)item.getData()).getFolder());
	                dialog.setAllowMultiple(false);
	                if (dialog.open() == IDialogConstants.OK_ID) {
	                    IResource resource = (IResource) dialog.getFirstResult();
	                    if (resource != null) {
	                    	((WebRootItem)item.getData()).setFolder((IContainer) resource);
	                    	item.setText(1, resource.getProjectRelativePath().toString());
	                    	validate();
	                    }
	                }
				}
				
			});
			
			this.addButton = SWTUtil.createButton(composite, Messages.ProjectPropertyPage_Add);
			this.addButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new ProjectContentProvider());
					dialog.setTitle(Messages.ProjectPropertyPage_SelectHtdocsFolderTitle); 
	                dialog.setMessage(Messages.ProjectPropertyPage_SelectHtdocsFolderDescription); 
	                dialog.setInput(project.getEclipseProject().getWorkspace()); 
	                dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
	                dialog.setAllowMultiple(false);
	                if (dialog.open() == IDialogConstants.OK_ID) {
	                    IResource resource = (IResource) dialog.getFirstResult();
	                    if (resource != null) {
	                    	final TableItem item = new TableItem(table, SWT.NONE);
	                    	final WebRootItem rootItem = new WebRootItem("/" + resource.getProjectRelativePath().toString(), (IContainer) resource); //$NON-NLS-1$
	                    	item.setText(0, rootItem.getPathName());
	                    	item.setText(1, rootItem.getFolder().getProjectRelativePath().toString());
	                    	item.setData(rootItem);
	                    	validate();
	                    }
	                }
				}
				
			});
			
			this.removeButton = SWTUtil.createButton(composite, Messages.ProjectPropertyPage_Remove);
			this.removeButton.setEnabled(false);
			this.removeButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (WebRootsList.this.table.getSelection() == null || WebRootsList.this.table.getSelection().length == 0) return;
					
					final TableItem item = WebRootsList.this.table.getSelection()[0];
					if (!((WebRootItem)item.getData()).isNew()) {
						removedFolders.add(((WebRootItem)item.getData()).getWebFolder());
					}
					for (int i = 0; i < table.getItemCount(); i++) {
						if (table.getItem(i) == item) {
							table.remove(i);
							break;
						}
					}
					validate();
				}
				
			});
		}		
	}
	
	private final class WebRootItem {

		private IPhpWebFolder webFolder;
		private boolean changed;
		
		private String pathName;
		private IContainer folder;
		
		public WebRootItem(String pathName, IContainer folder) {
			this.pathName = pathName;
			this.folder = folder;
		}

		public WebRootItem(IPhpWebFolder folder) {
			this.webFolder = folder;
			this.pathName = folder.getPathName();
			this.folder = folder.getFolder();
		}
		
		public void setPathName(String pathName) {
			this.pathName = pathName;
			this.changed = true;
		}
		
		public void setFolder(IContainer folder) {
			this.folder = folder;
			this.changed = true;
		}
		
		public boolean isChanged() {
			return this.changed;
		}
		
		public boolean isNew() {
			return this.webFolder == null;
		}
		
		public String getPathName() {
			return this.pathName;
		}
		
		public IContainer getFolder() {
			return this.folder;
		}
		
		public IPhpWebFolder getWebFolder() {
			return this.webFolder;
		}
		
	}

	@Override
	protected void performDefaults() {
		this.htdocs.setText(""); //$NON-NLS-1$
		this.removedFolders.clear();
		for (final IPhpWebFolder folder : this.project.getWebFolders()) {
			this.removedFolders.add(folder);
		}
		this.webRoots.table.removeAll();
		this.validate();
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		try {
			this.project.setDefaultWebFolder(this.htdocs.getText().length() > 0 ? this.project.getEclipseProject().getFolder(this.htdocs.getText()) : this.project.getEclipseProject());
			for (final IPhpWebFolder folder : this.removedFolders) {
				this.project.removeWebFolder(folder);
			}
			for (final TableItem tableItem : this.webRoots.table.getItems()) {
				final WebRootItem item = (WebRootItem) tableItem.getData();
				if (item.isNew()) {
					this.project.createWebFolder(item.getFolder(), item.getPathName());
				}
				else if (item.isChanged()) {
					item.getWebFolder().set(item.getFolder(), item.getPathName());
				}
			}
		}
		catch (CoreException ex) {
			PEXServerPlugin.logError(ex);
		}
		return super.performOk();
	}

}
