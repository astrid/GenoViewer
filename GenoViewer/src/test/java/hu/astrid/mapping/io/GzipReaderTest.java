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

package hu.astrid.mapping.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * @author avarga
 */
public class GzipReaderTest {
	
	private static final String RESOURCES_DIR = System.getProperty("ngsc.testfiles.dir");
	
	private static final String BAM_PATH = "test.bam";

	private GzipReader gzipReader;

	@Before
	public void setUp() throws Exception {
		gzipReader = new GzipReader(new BufferedInputStream(new FileInputStream(new File(RESOURCES_DIR, BAM_PATH))));
	}

	@Test(expected = java.io.IOException.class)
	public void testClosing() throws IOException {
		gzipReader.close();
		gzipReader.available();
	}

	@Test
	public void testRead() throws Exception {
		char c = (char) gzipReader.read();
		assertEquals('B', c);

		byte[] b1 = gzipReader.read(3);
		byte[] a = new byte[]{65, 77, 1};
		assertEquals(Arrays.toString(a), Arrays.toString(b1));

		int actSize = (796 - 4);
		byte[] h = gzipReader.read(actSize);
		assertEquals(actSize, h.length);

		try {
			gzipReader.read(1);
			fail("EOFExcepion was expected, but not thrown.");
		} catch (EOFException ioe) {
			// good, we expected this
		}
	}
}