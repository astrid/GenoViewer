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

package hu.astrid.viewer;

import org.junit.Test;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Roland Harangozo
 * Date: Sep 15, 2010
 * Time: 11:19:00 AM
 */
public class ViewerParameterTest {

	private static final String RESOURCES_DIR = System.getProperty("viewer.testfiles.dir");

	@Test
	public void runWithSampleTest() throws Exception {
		Viewer.main(new String[]{
				"--sample", RESOURCES_DIR + File.separator + "WebstartFiles"});
	}

	@Test
	public void runWithoutArgs() throws Exception {
		Viewer.main(new String[]{});
	}

	@Test
	public void runWithAppSettings() throws Exception {
		Viewer.main(new String[]{
				"-a", getClass().getResource("/test.xml").getPath()});
	}


}
