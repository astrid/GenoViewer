/*
 * This file is part of GenoViewer.
 *
 * GenoViewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GenoViewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenoViewer.  If not, see <http://www.gnu.org/licenses/>.
 */

package hu.astrid.viewer.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author onagy
 */
public class ParameterParserTest {

	private static File tempDir = null;
	private static File workspaceRootDir = null;
	private static File infoFile = null;
	private static File projectDir = null;
	private static File projectProperties = null;
	private static File sampleTestDir = null;
	private static File sampleAppProperties = null;

	@BeforeClass
	public static void setUpClass() throws Exception {
		tempDir = new File((String) System.getProperty("java.io.tmpdir"));
		workspaceRootDir = new File(tempDir + File.separator + "WSTestDir");
		infoFile = new File(workspaceRootDir + File.separator + workspaceRootDir.getName() + ".info");
		projectDir = new File(workspaceRootDir + File.separator + "ProjectTestDir");
		projectProperties = new File(projectDir + File.separator + projectDir.getName() + ".properties");
		sampleTestDir = new File(tempDir + File.separator + "SampleTestDir");
		sampleAppProperties = new File(sampleTestDir + File.separator + "application-setting.properties");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		try {

			if (!projectProperties.delete()) {
				throw new Exception("Cannot delete file [" + projectProperties.getAbsolutePath() + "]");
			}

			if (!projectDir.delete()) {
				throw new Exception("Cannot delete directory [" + projectDir.getAbsolutePath() + "]");
			}

			if (!infoFile.delete()) {
				throw new Exception("Cannot delete directory [" + infoFile.getAbsolutePath() + "]");
			}

			if (!workspaceRootDir.delete()) {
				throw new Exception("Cannot delete directory [" + workspaceRootDir.getAbsolutePath() + "]");
			}

			if (!sampleAppProperties.delete()) {
				throw new Exception("Cannot delete file [" + sampleAppProperties.getAbsolutePath() + "]");
			}

			if (!sampleTestDir.delete()) {
				throw new Exception("Cannot delete directory [" + sampleTestDir.getAbsolutePath() + "]");
			}
		} catch (SecurityException exc) {
			exc.printStackTrace();
		} catch (Exception exc) {
			throw exc;
		}
	}

	@Test
	public void testGetMapIfCmdLineIsValid() throws Exception {
		String[] wrongCommandLine1 = {"-w", "-s", "-a"};
		String[] wrongCommandLine2 = {"-sample"};
		String[] correctCommandLine = {"-s", sampleTestDir.getAbsolutePath(), "-a", sampleAppProperties.getAbsolutePath()};

		Map<Parameter, String> parameterMap = null;

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(wrongCommandLine1);
			fail("Expected GenoViewerParameterException");
		} catch (GenoViewerParameterException exc) {
			//good, we expected that
		}

		assertNull(parameterMap);

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(wrongCommandLine2);
			fail("Expected GenoViewerParameterException");
		} catch (GenoViewerParameterException exc) {
			//good, we expected that
		}

		assertNull(parameterMap);

		if (!sampleTestDir.mkdir()) {
			fail("Cannot create directory [" + sampleTestDir.getAbsoluteFile() + "] for workspace opening test!");
		}

		try {
			if (!sampleAppProperties.createNewFile()) {
				fail("Cannot create file [" + sampleAppProperties.getAbsoluteFile() + "] for workspace opening test!");
			}
		} catch (IOException exc) {
			exc.printStackTrace();
			fail("An error occured when tried to create test file for workspace opening test");
		}

		assertNull(parameterMap);

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(correctCommandLine);
		} catch (Exception e) {
			fail(e.getMessage() + " occured");
		}

		assertNotNull(parameterMap);
		assertEquals(2, parameterMap.size());
		assertTrue(ParameterParser.checkConflicts(parameterMap).isEmpty());

		assertTrue(parameterMap.containsKey(Parameter.SAMPLE));
		assertEquals(sampleTestDir.getAbsolutePath(), parameterMap.get(Parameter.SAMPLE));

		assertTrue(parameterMap.containsKey(Parameter.APPSETTINGS));
		assertEquals(sampleAppProperties.getAbsolutePath(), parameterMap.get(Parameter.APPSETTINGS));
	}

	@Test(expected = GenoViewerParameterException.class)
	public void testWrongWorkspaceParam() throws GenoViewerParameterException {
		String[] commandLineArguments = {"--workspace", "./fakeWorkspaceDirectoryDoNotCreate!!!!"};

		ParameterParser.getMapIfCmdLineIsValid(commandLineArguments);
	}

	@Test
	public void testCorrectWorkspaceParam() {

		System.out.println("Using temporary directory-->[" + tempDir.getAbsoluteFile() + "]");

		if (!workspaceRootDir.mkdir()) {
			fail("Cannot create directory [" + workspaceRootDir.getAbsoluteFile() + "] for workspace opening test!");
		}

		if (!projectDir.mkdir()) {
			fail("Cannot create directory [" + projectDir.getAbsoluteFile() + "] for workspace opening  test!");
		}

		try {

			if (!infoFile.createNewFile()) {
				fail("Cannot create file [" + infoFile.getAbsoluteFile() + "] for workspace opening test!");
			}
			if (!projectProperties.createNewFile()) {
				fail("Cannot create file [" + projectProperties.getAbsoluteFile() + "] for workspace opening test!");
			}
		} catch (IOException exc) {
			exc.printStackTrace();
			fail("An error occured when tried to create test files for workspace opening test");
		}

		String[] commandLineArguments = {"--workspace", workspaceRootDir.getAbsolutePath()};

		Map<Parameter, String> parameterMap = null;

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(commandLineArguments);
		} catch (Exception e) {
			fail(e.getMessage() + " occured");
		}

		assertNotNull(parameterMap);
		assertTrue(parameterMap.containsKey(Parameter.WORKSPACE));
		assertEquals(workspaceRootDir.getAbsolutePath(), parameterMap.get(Parameter.WORKSPACE));
	}

	@Test
	public void testHelpConflict() {
		String[] longCmdLineCorrect = {"--help"};
		String[] shortCmdLineCorrect = {"-h"};
		String[] longCmdLineWrong = {"--help", "--workspace", workspaceRootDir.getAbsolutePath()};
		Map<Parameter, String> parameterMap = null;

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(longCmdLineCorrect);
		} catch (Exception e) {
			fail(e.getMessage() + " occured");
		}

		assertNotNull(parameterMap);
		assertTrue(parameterMap.containsKey(Parameter.HELP));
		assertNull(parameterMap.get(Parameter.HELP));
		assertTrue(ParameterParser.checkConflicts(parameterMap).isEmpty());

		parameterMap = null;

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(shortCmdLineCorrect);
		} catch (Exception e) {
			fail(e.getMessage() + " occured");
		}

		assertNotNull(parameterMap);
		assertTrue(parameterMap.containsKey(Parameter.HELP));
		assertNull(parameterMap.get(Parameter.HELP));
		assertTrue(ParameterParser.checkConflicts(parameterMap).isEmpty());

		parameterMap = null;

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(longCmdLineWrong);
		} catch (GenoViewerParameterException e) {
			fail(e.getMessage() + " occured");
		}

		assertNotNull(parameterMap);
		assertTrue(parameterMap.containsKey(Parameter.HELP));
		assertTrue(parameterMap.containsKey(Parameter.WORKSPACE));
		assertNull(parameterMap.get(Parameter.HELP));
		assertNotNull(parameterMap.get(Parameter.WORKSPACE));

		assertFalse(ParameterParser.checkConflicts(parameterMap).isEmpty());
	}

	@Test
	public void testSampleAndWorkspaceConflict() {
		String[] longCmdLineCorrect = {"-s", ".", "-w", workspaceRootDir.getAbsolutePath()};

		Map<Parameter, String> parameterMap = null;

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(longCmdLineCorrect);
		} catch (Exception e) {
			fail(e.getMessage() + " occured");
		}

		assertFalse(ParameterParser.checkConflicts(parameterMap).isEmpty());
	}
}
