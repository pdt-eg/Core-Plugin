package org.pdtextensions.core.tests.codegeneration;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.php.core.tests.AbstractPDTTTest;
import org.eclipse.php.core.tests.PdttFile;
import org.pdtextensions.core.codegenerator.IElementGenerator;
import org.pdtextensions.core.codegenerator.InterfaceGenerator;
import org.pdtextensions.core.codegenerator.TraitGenerator;
import org.eclipse.php.internal.core.PHPVersion;

@SuppressWarnings("restriction")
public class ElementGeneratorTests extends AbstractPDTTTest {

	protected static final Map<PHPVersion, String[]> TESTS = new LinkedHashMap<PHPVersion, String[]>();

	static {
		TESTS.put(PHPVersion.PHP5_3, new String[] { "/workspace/generator/php53" });
		TESTS.put(PHPVersion.PHP5_4, new String[] { "/workspace/generator/php54" });
	};

	public ElementGeneratorTests(String description) {
		super(description);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Create code for elements");

		for (final PHPVersion phpVersion : TESTS.keySet()) {
			TestSuite phpVerSuite = new TestSuite(phpVersion.getAlias());

			for (String testsDirectory : TESTS.get(phpVersion)) {

				for (final String fileName : getPDTTFiles(testsDirectory)) {
					try {
						final PdttFile pdttFile = new PdttFile(fileName);
						phpVerSuite.addTest(new ElementGeneratorTests(phpVersion.getAlias() + " - /" + fileName) {

							protected void runTest() throws Throwable {

								IElementGenerator generator = createCodeGenerator(pdttFile);
								String actual = generator.generateCode();
								assertContents(pdttFile.getExpected(), actual);
							}
						});
					} catch (final Exception e) {
						// dummy test indicating PDTT file parsing failure
						phpVerSuite.addTest(new TestCase(fileName) {
							protected void runTest() throws Throwable {
								throw e;
							}
						});
					}
				}
			}

			suite.addTest(phpVerSuite);
		}
		// Create a setup wrapper
		TestSetup setup = new TestSetup(suite) {
			protected void setUp() throws Exception {
			}

			protected void tearDown() throws Exception {
			}
		};
		return setup;
	}

	/**
	 * @throws Exception
	 * @test
	 */
	public void tests() throws Exception {
		String[] files = getPDTTFiles("/workspace/generator/interface");
		for (String file : files) {
			PdttFile testFile = new PdttFile(file);
		}
	}

	/**
	 * 
	 * @param testFile
	 * @return InterfaceGenerator
	 * @throws Exception
	 */
	private static IElementGenerator createCodeGenerator(PdttFile testFile) throws Exception {
		IElementGenerator generator;

		if (testFile.getConfig().get("generator") == null) {
			throw new Exception("No 'generator' param in config section in pdtt file");
		}

		if (testFile.getConfig().get("generator").equals("interface")) {
			generator = new InterfaceGenerator();
		} else if (testFile.getConfig().get("generator").equals("trait")) {
			generator = new TraitGenerator();
		} else {
			throw new Exception("Cannot create class according config section in pdtt file ("
					+ testFile.getConfig().get("generator") + ")");
		}

		generator.setName(testFile.getConfig().get("name"));
        
		String namespace = testFile.getConfig().get("namespace");
		if (namespace != null) {
			generator.setNamespace(namespace);
		}
		String superclass = testFile.getConfig().get("superclass");
		if(superclass != null) {
			generator.setSuperclass(superclass);
		}

		String interfacesConfig = testFile.getConfig().get("interfaces");
		if (interfacesConfig != null) {
			String[] interfaces = interfacesConfig.split(",");
			for (String item : interfaces) {
				generator.addInterface(item.trim());
			}
		}

		return generator;
	}
}
