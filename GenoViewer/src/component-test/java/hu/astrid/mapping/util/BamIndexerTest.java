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

package hu.astrid.mapping.util;

import static org.junit.Assert.assertEquals;
import hu.astrid.mapping.io.BaiWriter;
import hu.astrid.mapping.io.IndexedBamReader;
import hu.astrid.mapping.io.SamReader;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.AlignmentRecordComparator;
import hu.astrid.mapping.model.BamIndex;
import hu.astrid.test.FileSupportSystemTestBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BamIndexerTest extends FileSupportSystemTestBase {
	
	private static final String BAM_FILE = "alignment1.bam";
	private static final String SAM_FILE = "alignment1.sam";
	private static final String REFERENCE_NAME = "Generated Genom";
	
	private File baiFile;
	private File bamFile;
	
	@Before
	public void setUp() throws Exception {
		baiFile = File.createTempFile("bai-test-", "", new File(TEMP_DIR));
		baiFile.deleteOnExit();
		
		BamIndexer indexer = new BamIndexer(new File(TEST_RESOURCES_DIR, BAM_FILE).getPath());
		BamIndex bamIndex = indexer.index();
		BaiWriter baiWriter = new BaiWriter(new FileOutputStream(baiFile));
		baiWriter.write(bamIndex);
		
		bamFile = new File(TEST_RESOURCES_DIR, BAM_FILE);
	}
	
	@Test
	public void testIndex() throws Exception {
//		IndexedBamReader reader = new IndexedBamReader(bamFile.getPath(), baiFile.getPath());
		IndexedBamReader reader = new IndexedBamReader(bamFile.getPath());
		File testSamFile = new File(TEST_RESOURCES_DIR, SAM_FILE);
		
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
			List<AlignmentRecord> actualRecords = reader.loadRecords(REFERENCE_NAME, interval[0], interval[1]);
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
