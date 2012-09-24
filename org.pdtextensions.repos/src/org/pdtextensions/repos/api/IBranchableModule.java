package org.pdtextensions.repos.api;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Extension for modules to allow branches. To test if branching is support you need to
 * check if the module object implements this interface. Branches are a repository specific way to
 * group versions of the same module.
 * 
 * Following {@link http://www.semver.org} every repository should be aware of implementing the
 * version name branches. That means: Branch "2.4" groups all versions with "2.4" prefix, for example
 * "2.4.4", "2.4.5-dev", "2.4.5-beta".
 * 
 * @author mepeisen
 */
public interface IBranchableModule extends IModule {
	
	/**
	 * Lists the release versions of the given branch.
	 * @param branch the branch
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return list of release versions within the given branch.
	 * @throws CoreException
	 */
	Iterable<IModuleVersion> listReleaseVersions(String branch, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Lists the development versions of the given branch.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @param branch the branch
	 * @return list of development versions within the given branch.
	 * @throws CoreException
	 */
	Iterable<IModuleVersion> listDevVersions(String branch, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Lists the newest release version of the given branch.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @param branch the branch
	 * @return release version or {@code null} if the version does not exist.
	 * @throws CoreException
	 */
	IModuleVersion getNewestReleaseVersion(String branch, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Lists the newest development version of the given branch.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @param branch the branch
	 * @return development version or {@code null} if the version does not exist.
	 * @throws CoreException
	 */
	IModuleVersion getNewestDevVersion(String branch, IProgressMonitor monitor) throws CoreException;

}
