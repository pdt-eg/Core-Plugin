/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;

/**
 * Access to the PDT Extension images.
 * 
 *
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com> 
 *
 */
public class PDTPluginImages {

	public static final IPath ICONS_PATH = new Path("/ico/full"); //$NON-NLS-1$
	
	private static final String NAME_PREFIX = "org.pdtextensions.core.ui."; //$NON-NLS-1$
	private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();
	
	// The plug-in registry
	private static ImageRegistry fgImageRegistry = null;
	
	private static HashMap<String, ImageDescriptor> fgAvoidSWTErrorMap = null;
	private static final String T_OBJ = "obj16"; //$NON-NLS-1$
	
	public static final String IMG_OBJS_ROUTE = NAME_PREFIX + "route-icon.gif";
	public static final String IMG_CORRECTION_CHANGE= NAME_PREFIX + "correction_change.gif"; //$NON-NLS-1$
	public static final ImageDescriptor DESC_OBJS_ROUTE = createManagedFromKey(T_OBJ, IMG_OBJS_ROUTE);	
	public static final ImageDescriptor DESC_CORRECTION_CHANGE= createManagedFromKey(T_OBJ, IMG_CORRECTION_CHANGE);

	private static final String T_WIZBAN = "wizban"; //$NON-NLS-1$
	
	public static final ImageDescriptor DESC_WIZBAN_NEW_PHPCLASS = create(T_WIZBAN, "newclass_wiz.png");//$NON-NLS-1$	
	
	private static ImageDescriptor createManagedFromKey(String prefix, String key) {
		return createManaged(prefix, key.substring(NAME_PREFIX_LENGTH), key);
	}

	private static ImageDescriptor createManaged(String prefix, String name,
			String key) {
		try {
			ImageDescriptor result = create(prefix, name, true);
			if (fgAvoidSWTErrorMap == null) {
				fgAvoidSWTErrorMap = new HashMap<String, ImageDescriptor>();
			}
			fgAvoidSWTErrorMap.put(key, result);
			if (fgImageRegistry != null) {
				// Plugin.logErrorMessage("Image registry already defined");
				// //$NON-NLS-1$

			}
			return result;
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}	
	
	/*
	 * Creates an image descriptor for the given prefix and name in the Symfony UI
	 * bundle. The path can contain variables like $NL$. If no image could be
	 * found, <code>useMissingImageDescriptor</code> decides if either the
	 * 'missing image descriptor' is returned or <code>null</code>. or <code>null</code>.
	 */
	private static ImageDescriptor create(String prefix, String name, boolean useMissingImageDescriptor) {
		IPath path = ICONS_PATH.append(prefix).append(name);
				
		return createImageDescriptor(PEXUIPlugin.getDefault().getBundle(), path, useMissingImageDescriptor);
	}

	/*
	 * Creates an image descriptor for the given prefix and name in the Symfony UI
	 * bundle. The path can contain variables like $NL$. If no image could be
	 * found, the 'missing image descriptor' is returned.
	 */
	private static ImageDescriptor create(String prefix, String name) {
		return create(prefix, name, true);
	}	
	
	/*
	 * Creates an image descriptor for the given path in a bundle. The path can
	 * contain variables like $NL$. If no image could be found, <code>useMissingImageDescriptor</code>
	 * decides if either the 'missing image descriptor' is returned or <code>null</code>.
	 * Added for 3.1.1.
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor) {
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}	

}
