package org.pdtextensions.repos.internal.debug;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.pdtextensions.repos.api.IFindResult;
import org.pdtextensions.repos.api.IModule;
import org.pdtextensions.repos.api.IRepositoryProvider;

/**
 * A sample provider for debug usage
 * 
 * @author mepeisen
 */
public class DebugProvider implements IRepositoryProvider {

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFindResult findModule(String vendor, String name, String version)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IModule> listModules() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
