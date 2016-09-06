package org.pdtextensions.core.ui.extension;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.php.composer.core.ComposerPlugin;
import org.eclipse.php.composer.core.resources.IComposerProject;
import org.pdtextensions.core.ui.extension.INamespaceResolver;

public class ComposerNamespaceResolver implements INamespaceResolver {

	@Override
	public String resolve(IScriptFolder container) {
		if (Platform.getBundle("org.eclipse.php.composer.core") != null) {

			IComposerProject project = ComposerPlugin.getDefault().getComposerProject(container.getScriptProject());

			IPath path = container.getPath().makeRelativeTo(project.getFullPath());

			return project.getNamespace(path);
		}
		
		return null;
	}
}