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
import hu.astrid.mapping.model.BamIndex;
import hu.astrid.mapping.model.Bin;
import hu.astrid.mapping.model.ReferenceIndex;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class BaiReaderTest {
	
	private static final String RESOURCES_DIR = System.getProperty("ngsc.testfiles.dir");

	BamIndex bamIndex;
	private static final String BAI_FILE_NAME = "test.bam.bai";

	@Before
	public void setup() throws Exception {
		bamIndex = new BaiReader().load(new File(RESOURCES_DIR, BAI_FILE_NAME));
	}

	@Test
	public void testBinId() {
		ReferenceIndex refI = bamIndex.getReferenceIndices().get(0);
		Bin bin = refI.getBins().get(0);
		int id = bin.getId();
		assertEquals(4681, id);
	}

}
