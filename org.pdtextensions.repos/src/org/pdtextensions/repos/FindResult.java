package org.pdtextensions.repos;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.pdtextensions.repos.api.IFindResult;
import org.pdtextensions.repos.api.IModuleVersion;

/**
 * Standard implementation of a search result.
 * 
 * @author mepeisen
 */
public class FindResult implements IFindResult {
	
	private IStatus status;
	private Collection<IModuleVersion> result;

	public FindResult(IStatus status, Collection<IModuleVersion> result) {
		this.status = status;
		this.result = result;
	}

	@Override
	public boolean isOk() {
		return this.status.isOK();
	}

	@Override
	public IStatus getStatus() {
		return this.status;
	}

	@Override
	public Iterable<IModuleVersion> moduleVersions() {
		return Collections.unmodifiableCollection(this.result);
	}

}
