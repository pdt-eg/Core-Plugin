package org.pdtextensions.core.tests.codeassist;

import junit.framework.TestCase;

import org.junit.Test;


public class CodeAssistTest extends TestCase /*extends AbstractPDTTTest*/ { 
	
		@Test
	public void testDummy()
	{
		assertTrue(true);
	}
		
		/*
	protected static final List<String> TESTS = new ArrayList<>();
	private static IProject project;
	protected static IFile testFile;
	protected static final char OFFSET_CHAR = '|';
	
	static { TESTS.add("/workspace/codeassist/php54"); };	
	
	public CodeAssistTest(String description) {
		super(description);
	}
	
	
	public static Test suite() {
		
		TestSuite suite = new TestSuite("Method override Codeassist Tests");
		
		for (String testsDirectory : TESTS) {
			
			TestSuite phpVerSuite = new TestSuite("test");
			for (final String fileName : dogetPDTTFiles(testsDirectory)) {
				
				try {
					final CodeAssistPdttFile pdttFile = new CodeAssistPdttFile(fileName);
					
					phpVerSuite.addTest(new CodeAssistTest(fileName) {
						
						protected void setUp() throws Exception {
							PHPCoreTests.setProjectPhpVersion(project, PHPVersion.PHP5_4);
							pdttFile.applyPreferences();
						}

						protected void tearDown() throws Exception {
							if (testFile != null) {
								testFile.delete(true, null);
								testFile = null;
							}
						}

						protected void runTest() throws Throwable {
							CompletionProposal[] proposals = getProposals(pdttFile
									.getFile());
							compareProposals(proposals, pdttFile);
						}
						
					});
				} catch (final Exception e) {
					suite.addTest(new TestCase(fileName) { // dummy
						// test
						// indicating
						// PDTT
						// file
						// parsing
						// failure
						protected void runTest() throws Throwable {
							throw e;
						}
					});
				}
			}
			suite.addTest(phpVerSuite);
		}
		
		// Create a setup wrapper
		TestSetup setup = new TestSetup(suite) {
			protected void setUp() throws Exception {
				setUpSuite();
			}

			protected void tearDown() throws Exception {
				tearDownSuite();
			}
		};
		return setup;		
	}
	
	public static void setUpSuite() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("CodeAssistTest");
		if (project.exists()) {
			return;
		}

		project.create(null);
		project.open(null);

		// configure nature
		IProjectDescription desc = project.getDescription();
		desc.setNatureIds(new String[] { PHPNature.ID });
		project.setDescription(desc, null);
	}

	public static void tearDownSuite() throws Exception {
		project.close(null);
		project.delete(true, true, null);
		project = null;
	}
	
	protected static ISourceModule getSourceModule() {
		return DLTKCore.createSourceModuleFrom(testFile);
	}

	public static CompletionProposal[] getProposals(String data)
			throws Exception {
		int offset = createFile(data);
		return getProposals(offset);
	}

	public static CompletionProposal[] getProposals(int offset)
			throws ModelException {
		return getProposals(getSourceModule(), offset);
	}

	public static CompletionProposal[] getProposals(ISourceModule sourceModule,
			int offset) throws ModelException {
		final List<CompletionProposal> proposals = new LinkedList<CompletionProposal>();
		
		final CompletionRequestor requestor = new CompletionRequestor() {
			public void accept(CompletionProposal proposal) {
				proposals.add(proposal);
			}
		};
		sourceModule.codeComplete(offset, requestor, 1000);
		return (CompletionProposal[]) proposals
				.toArray(new CompletionProposal[proposals.size()]);
	}

	public static void compareProposals(CompletionProposal[] proposals,
			CodeAssistPdttFile pdttFile) throws Exception {
		ExpectedProposal[] expectedProposals = pdttFile.getExpectedProposals();

		boolean proposalsEqual = true;
		if (proposals.length == expectedProposals.length) {
			for (ExpectedProposal expectedProposal : pdttFile
					.getExpectedProposals()) {
				boolean found = false;
				for (CompletionProposal proposal : proposals) {
					IModelElement modelElement = proposal.getModelElement();
					if (modelElement == null) {
						if (new String(proposal.getName())
								.equalsIgnoreCase(expectedProposal.name)) { // keyword
							found = true;
							break;
						}
					} else if (modelElement.getElementType() == expectedProposal.type) {
						if (modelElement instanceof AliasType) {
							if (((AliasType) modelElement).getAlias().equals(
									expectedProposal.name)) {

								found = true;
								break;
							}

						} else if ((modelElement instanceof FakeConstructor)
								&& (modelElement.getParent() instanceof AliasType)) {
							if (((AliasType) modelElement.getParent())
									.getAlias().equals(expectedProposal.name)) {

								found = true;
								break;
							}

						} else {
							if (modelElement.getElementName().equalsIgnoreCase(
									expectedProposal.name)) {
								found = true;
								break;
							}
						}
					} else if (modelElement.getElementType() == expectedProposal.type
							&& new String(proposal.getName())
									.equalsIgnoreCase(expectedProposal.name)) {
						// for phar include
						found = true;
						break;
					}
				}
				if (!found) {
					proposalsEqual = false;
					break;
				}
			}
		} else {
			proposalsEqual = false;
		}

		if (!proposalsEqual) {
			StringBuilder errorBuf = new StringBuilder();
			errorBuf.append("\nEXPECTED COMPLETIONS LIST:\n-----------------------------\n");
			errorBuf.append(pdttFile.getExpected());
			errorBuf.append("\nACTUAL COMPLETIONS LIST:\n-----------------------------\n");
			for (CompletionProposal p : proposals) {
				IModelElement modelElement = p.getModelElement();
				if (modelElement == null
						|| modelElement.getElementName() == null) {
					errorBuf.append("keyword(").append(p.getName())
							.append(")\n");
				} else {
					switch (modelElement.getElementType()) {
					case IModelElement.FIELD:
						errorBuf.append("field");
						break;
					case IModelElement.METHOD:
						errorBuf.append("method");
						break;
					case IModelElement.TYPE:
						errorBuf.append("type");
						break;
					}
					if (modelElement instanceof AliasType) {
						errorBuf.append('(')
								.append(((AliasType) modelElement).getAlias())
								.append(")\n");
					} else {
						errorBuf.append('(')
								.append(modelElement.getElementName())
								.append(")\n");
					}
				}
			}
			fail(errorBuf.toString());
		}
	}
	
	protected static int createFile(String data) throws Exception {
		int offset = data.lastIndexOf(OFFSET_CHAR);
		if (offset == -1) {
			throw new IllegalArgumentException("Offset character is not set");
		}

		// replace the offset character
		data = data.substring(0, offset) + data.substring(offset + 1);

		testFile = project.getFile("test.php");
		testFile.create(new ByteArrayInputStream(data.getBytes()), true, null);
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);

		PHPCoreTests.waitForIndexer();
		// PHPCoreTests.waitForAutoBuild();

		return offset;
	}
	
	protected static String[] dogetPDTTFiles(String testsDirectory) {
		return AbstractPDTTTest.getPDTTFiles(testsDirectory, PEXCoreTestPlugin.getDefault()
				.getBundle());
	}
*/	
}
