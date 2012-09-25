package org.pdtextensions.server.internal.lhttpd;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.ServerDelegate;

public class LHttpdServerDelegate extends ServerDelegate {

	@Override
	public IStatus canModifyModules(IModule[] arg0, IModule[] arg1) {
		return Status.OK_STATUS;
	}

	@Override
	public IModule[] getChildModules(IModule[] module) {
		if (module == null)
			return null;
		
		IModuleType moduleType = module[0].getModuleType();
		
		if (module.length == 1 && moduleType != null && "org.pdtextensions.server.lhttpd.moduleType".equals(moduleType.getId())) {
			// TODO
//			IWebModule webModule = (IWebModule) module[0].loadAdapter(IWebModule.class, null);
//			if (webModule != null) {
//				IModule[] modules = webModule.getModules();
//				//if (modules != null)
//				//	System.out.println(modules.length);
//				return modules;
//			}
		}
		return new IModule[0];
	}

	@Override
	public IModule[] getRootModules(IModule module) throws CoreException {
		if ("org.pdtextensions.server.lhttpd.moduleType".equals(module.getModuleType().getId())) {
			IStatus status = canModifyModules(new IModule[] { module }, null);
			if (status == null || !status.isOK())
				throw new CoreException(status);
			return new IModule[] { module };
		}
		return null;
	}

	@Override
	public void modifyModules(IModule[] arg0, IModule[] arg1,
			IProgressMonitor arg2) throws CoreException {
		// TODO Auto-generated method stub

	}

}
