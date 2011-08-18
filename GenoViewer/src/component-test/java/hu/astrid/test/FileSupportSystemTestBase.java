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

package hu.astrid.test;

import org.junit.BeforeClass;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: bds
 */
public class FileSupportSystemTestBase {
	
	protected static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	protected static final String TEST_RESOURCES_DIR = System.getProperty("ngsc.testfiles.dir");

	@BeforeClass
	public static void init() throws IOException {
		if (TEST_RESOURCES_DIR == null) {
			throw new IllegalArgumentException("The ngsc.testfiles.dir system property must be set.");
		}
	}
}
