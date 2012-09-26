package org.pdtextensions.server.internal.lhttpd;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.pdtextensions.server.LHttpdConstants;
import org.pdtextensions.server.lhttpd.ILHttpdLocationConfig;
import org.pdtextensions.server.lhttpd.ILHttpdModule;
import org.pdtextensions.server.lhttpd.ILHttpdPortConfig;
import org.pdtextensions.server.lhttpd.ILHttpdServer;

public class LHttpdServerDelegate extends ServerDelegate implements ILHttpdServer {

	@Override
	public IStatus canModifyModules(IModule[] arg0, IModule[] arg1) {
		return Status.OK_STATUS;
	}

	@Override
	public IModule[] getChildModules(IModule[] module) {
		if (module == null)
			return null;
		
		IModuleType moduleType = module[0].getModuleType();
		
		if (module.length == 1 && moduleType != null && LHttpdConstants.MODULE_TYPE_ID.equals(moduleType.getId())) {
			// currently no child modules
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
		if (LHttpdConstants.MODULE_TYPE_ID.equals(module.getModuleType().getId())) {
			IStatus status = canModifyModules(new IModule[] { module }, null);
			if (status == null || !status.isOK())
				throw new CoreException(status);
			return new IModule[] { module };
		}
		return null;
	}

	@Override
	public void modifyModules(IModule[] add, IModule[] remove,
			IProgressMonitor monitor) throws CoreException {
		IStatus status = canModifyModules(add, remove);
		if (status == null || !status.isOK())
			throw new CoreException(status);
		
		// TODO Auto-generated method stub

	}

	@Override
	public ILHttpdPortConfig[] getPortConfigurations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsingRuntimePorts() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDefaultHtdocs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsingRuntimeHtdocs() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ILHttpdModule getHtdocsModule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILHttpdLocationConfig[] getLocationConfigs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsingRuntimeLocations() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAllowOverride() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsingRuntimeAllowOverride() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHttpdConf() {
		// TODO Auto-generated method stub
		return null;
	}

}
