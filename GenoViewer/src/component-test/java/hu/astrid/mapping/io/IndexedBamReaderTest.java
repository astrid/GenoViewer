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
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.AlignmentRecordComparator;
import hu.astrid.mapping.model.MappingHeader;
import hu.astrid.test.FileSupportSystemTestBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class IndexedBamReaderTest extends FileSupportSystemTestBase {
	
	private IndexedBamReader bamReader;

	private static final String REFERENCE_NAME = "Generated Genom";
	private static final String TEST_BAM = "alignment1.bam";
	private static final String TEST_SAM = "alignment1.sam";
	private File testSamFile = new File(TEST_RESOURCES_DIR, TEST_SAM);

	@Before
	public void setUp() throws Exception {
		this.bamReader = new IndexedBamReader(new File(TEST_RESOURCES_DIR, TEST_BAM).getPath());
	}

	@Test
	public void testGetHeader() throws Exception {
		MappingHeader actual = bamReader.getHeader();
		MappingHeader expected;

		SamReader reader = new SamReader(new BufferedReader(new FileReader(testSamFile)));
		expected = reader.readHeader();

		assertEquals(expected.toString(), actual.toString());
	}

	@Test
	public void testBamReaderByIntervals() throws Exception {
		int[][] intervalsToTest = new int[][]{
				{ 0, 1000 },
				{ 1000, 2000 },
				{ 16000, 17000 },
				{ 130500, 131500 },
				{ 393000, 394000 },
				{ 1048000, 1049000 },
				{ 20001000, 20002000 }
		};

		for (int[] interval : intervalsToTest) {
			List<AlignmentRecord> expectedRecords = loadSamForInterval(testSamFile, interval[0], interval[1]);

			Collections.sort(expectedRecords, new AlignmentRecordComparator());
			List<AlignmentRecord> actualRecords = bamReader.loadRecords(REFERENCE_NAME, interval[0], interval[1]);
			assertEquals(expectedRecords, actualRecords);
		}
	}

	private List<AlignmentRecord> loadSamForInterval(File samFile, int start, int end) throws Exception {
		List<AlignmentRecord> expectedRecords = new ArrayList<AlignmentRecord>();
		SamReader samReader = new SamReader(new BufferedReader(new FileReader(samFile)));
		
		AlignmentRecord record;
		while ((record = samReader.nextRecord()) != null) {
			if (record.getPosition() >= start && record.getPosition() < end) {
				expectedRecords.add(record);
			}
		}
		return expectedRecords;
	}

}
