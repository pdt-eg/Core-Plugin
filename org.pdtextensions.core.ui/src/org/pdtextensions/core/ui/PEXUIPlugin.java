/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.internal.preferences.PreferencesService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.ui.corext.template.php.CodeTemplateContextType;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.themes.ColorUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.osgi.framework.BundleContext;
import org.pdtextensions.core.ui.formatter.CodeFormatterOptions;
import org.pdtextensions.core.ui.preferences.PDTTemplateStore;
import org.pdtextensions.core.ui.preferences.PreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class PEXUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.pdtextensions.core.ui"; //$NON-NLS-1$
	
	public static final String OPTION_ID = "jp.sourceforge.pdt_tools"; //$NON-NLS-1$

	public static final String FORMATTER_PROFILE = PLUGIN_ID
			+ ".formatter_profile"; //$NON-NLS-1$

	public static final String HELP_ID = "org.pdtextensions.core.ui.formatter.help." //$NON-NLS-1$
			+ Locale.getDefault().getLanguage();
	public static final String HELP_ID_FORMATTER = HELP_ID + ".formatter"; //$NON-NLS-1$
	public static final String HELP_ID_SETTINGS = HELP_ID + ".settings"; //$NON-NLS-1$

	public static final String MARKER_ID = PLUGIN_ID + ".problem"; //$NON-NLS-1$
	
	private IPreferenceStore fCombinedPreferenceStore;
	
	private PDTTemplateStore fCodeTemplateStore;
	
	protected ContextTypeRegistry codeTypeRegistry = null;

	private ImageDescriptorRegistry fImageDescriptorRegistry;
	

	// The shared instance
	private static PEXUIPlugin plugin;
	
	// Indent Line color resource
	private Color color;
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public IPreferenceStore getCombinedPreferenceStore() {
		if (fCombinedPreferenceStore == null) {
			IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
			fCombinedPreferenceStore = new ChainedPreferenceStore(
					new IPreferenceStore[] { getPreferenceStore(),
							generalTextStore });
		}
		return fCombinedPreferenceStore;
	}
	

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PEXUIPlugin getDefault() {
		return plugin;
	}
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e));
	}

	public static void log(int severity, String message) {
		log(new Status(severity, PLUGIN_ID, message));
	}

	public static void warning(String string, ASTNode node, int offset, int end) {
		log(new Status(IStatus.WARNING, PLUGIN_ID, "Could not find '" + string
				+ "' @" + node.getClass().getSimpleName() + " [" + offset + "-"
				+ end + "]"));
	}
	
	public Map<String, String> getOptions(IProject project) {
		CodeFormatterOptions options = new CodeFormatterOptions(null);
		PreferencesService service = PreferencesService.getDefault();
		IScopeContext[] contexts = (project != null) ? new IScopeContext[] {
				new ProjectScope(project), new InstanceScope() }
				: new IScopeContext[] { new InstanceScope() };
		HashMap<String, String> settings = new HashMap<String, String>();
		Iterator<String> it = options.getMap().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = service.getString(PLUGIN_ID, key, null, contexts);
			if (value != null) {
				settings.put(key, value);
			}
		}
		if (!settings.isEmpty()) {
			options.set(settings);
		}
		return options.getMap();
	}
	
	
	public void createMarker(IResource resource, int severity, String message,
			boolean persist) {
		try {
			IMarker[] markers = resource.findMarkers(MARKER_ID, false,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				Object sev = marker.getAttribute(IMarker.SEVERITY);
				if (sev != null && !sev.equals(severity)) {
					continue;
				}
				Object msg = marker.getAttribute(IMarker.MESSAGE);
				if (msg != null && !msg.equals(message)) {
					continue;
				}
				return;
			}
		} catch (CoreException e) {
		}
		try {
			IMarker marker = resource.createMarker(MARKER_ID);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(IMarker.SEVERITY, severity);
			map.put(IMarker.MESSAGE, message);
			if (!persist) {
				map.put(IMarker.TRANSIENT, true);
			}
			marker.setAttributes(map);
		} catch (CoreException e) {
		}
	}
	
	public Color getColor() {
		if (color == null) {
			String colorString = getPreferenceStore().getString(
					PreferenceConstants.LINE_COLOR);
			color = new Color(PlatformUI.getWorkbench().getDisplay(),
					ColorUtil.getColorValue(colorString));
		}
		return color;
	}

	public void setColor(Color color) {
		if (this.color != null) {
			this.color.dispose();
		}
		this.color = color;
	}
	
	public IFile getFile(IDocument document) {
		IFile file = null;
		IStructuredModel structuredModel = null;
		try {
			structuredModel = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			if (structuredModel != null) {
				String location = structuredModel.getBaseLocation();
				if (location != null) {
					file = ResourcesPlugin.getWorkspace().getRoot()
							.getFile(new Path(location));
				}
			}
		} finally {
			if (structuredModel != null) {
				structuredModel.releaseFromRead();
			}
		}
		return file;
	}
	
	public TemplateStore getCodeTemplateStore() {
		if (fCodeTemplateStore == null) {

			fCodeTemplateStore = new PDTTemplateStore(
					getCodeTemplateContextRegistry(), getPreferenceStore(),
					PreferenceConstants.CODE_TEMPLATES_KEY);

			try {
				fCodeTemplateStore.load();
			} catch (IOException e) {
//				Logger.logException(e);
			}
		}

		return fCodeTemplateStore;
	}
	
	public ContextTypeRegistry getCodeTemplateContextRegistry() {
		if (codeTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();

			
			CodeTemplateContextType.registerContextTypes(registry);

			codeTypeRegistry = registry;
		}

		return codeTypeRegistry;
	}
	
	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		return PEXUIPlugin.getDefault().internalGetImageDescriptorRegistry();
	}

	private ImageDescriptorRegistry internalGetImageDescriptorRegistry() {
		if (fImageDescriptorRegistry == null) {
			fImageDescriptorRegistry = new ImageDescriptorRegistry();
		}
		return fImageDescriptorRegistry;
	}	
	
}
