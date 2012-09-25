package org.pdtextensions.server.internal.lhttpd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.php.internal.core.facet.PHPFacetsConstants;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

public class LHttpdModuleFactoryDelegate extends ProjectModuleFactoryDelegate {
	
	private static final String ID = "org.pdtextensions.server.internal.lhttpdmodule"; //$NON-NLS-1$
	protected List<ModuleDelegate> moduleDelegates = new ArrayList<ModuleDelegate>();

	/**
	 * @see DeployableProjectFactoryDelegate#getFactoryID()
	 */
	public static String getFactoryId() {
		return ID;
	}
	
//	/**
//	 * Use {@link #createModules(IProject)} instead.
//	 * @deprecated
//	 * @param nature
//	 * @return
//	 */
//	protected IModule[] createModules(ModuleCoreNature nature) {
//		if(nature != null){
//			return createModules(nature.getProject());
//		}
//		return null;
//	}
	/**
	 * Returns true if the project represents a deployable project of this type.
	 * 
	 * @param project
	 *            org.eclipse.core.resources.IProject
	 * @return boolean
	 */
	protected boolean isValidModule(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject == null)
				return false;
			IProjectFacet webFacet = ProjectFacetsManager.getProjectFacet(PHPFacetsConstants.PHP_CORE_COMPONENT);
			return facetedProject.hasProjectFacet(webFacet);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @see org.eclipse.wst.server.core.model.ModuleFactoryDelegate#getModuleDelegate(org.eclipse.wst.server.core.IModule)
	 */
	@Override
	public ModuleDelegate getModuleDelegate(IModule module) {
		for (Iterator<ModuleDelegate> iter = moduleDelegates.iterator(); iter.hasNext();) {
			ModuleDelegate element = (ModuleDelegate) iter.next();
			if (module == element.getModule())
				return element;
		}
		return null;

	}

	@Override
	protected IModule[] createModules(IProject project) {
		LHttpdModule moduleDelegate = null;
		IModule module = null;
		try {
			if(isValidModule(project)) {
				moduleDelegate = new LHttpdModule(project);
				module = createModule(project.getName(), project.getName(), "org.pdtextensions.server.lhttpd.moduleType", "1.0", moduleDelegate.getProject());
				moduleDelegate.initialize(module);
			}
		} catch (Exception e) {
			// TODO WSTWebPlugin.logError(e);
			e.printStackTrace();
		} finally {
			if (module != null) {
				if (getModuleDelegate(module) == null)
					moduleDelegates.add(moduleDelegate);
			}
		}
		if (module == null)
			return null;
		return new IModule[] {module};
	}
	
	/**
	 * Returns the list of resources that the module should listen to
	 * for state changes. The paths should be project relative paths.
	 * Subclasses can override this method to provide the paths.
	 *
	 * @return a possibly empty array of paths
	 */
	@Override
	protected IPath[] getListenerPaths() {
		return new IPath[] {
			new Path(".project"), // nature //$NON-NLS-1$
			//new Path(StructureEdit.MODULE_META_FILE_NAME), // component
			new Path(".settings/org.eclipse.wst.common.project.facet.core.xml") // facets //$NON-NLS-1$
		};
	}

}
