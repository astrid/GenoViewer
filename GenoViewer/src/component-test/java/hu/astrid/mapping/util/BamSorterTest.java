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

import hu.astrid.mapping.io.BamReader;
import hu.astrid.mapping.model.*;
import hu.astrid.mapping.util.BamSorter;
import hu.astrid.test.FileSupportSystemTestBase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Szuni
 */
public class BamSorterTest extends FileSupportSystemTestBase {
	private AlignmentRecordComparatorExtended comparator;

	private static final String TEST_BAM = "c1215jo_sort.bam";
	private static File sortedFile = new File(TEMP_DIR, TEST_BAM + "_sorted");

	@AfterClass
	public static void cleanup() throws IOException {
		if (sortedFile.exists()) {
			sortedFile.delete();
		}
	}

	@Before
	public void setUp() {
		comparator = new AlignmentRecordComparatorExtended();
	}

	@Test
	public void testRecordOrder() throws Exception {
		BamSorter sorter = new BamSorter(new File(TEST_RESOURCES_DIR, TEST_BAM), sortedFile);
		sorter.sort();

		BamReader testReader = new BamReader(new FileInputStream(sortedFile));
		BamReader bamReader = new BamReader(new FileInputStream(new File(TEST_RESOURCES_DIR, TEST_BAM)));

		//Comparing headers
		MappingHeader testHeader = testReader.readHeader();
		MappingHeader samHeader = bamReader.readHeader();
		for (HeaderRecord record : samHeader.getRecords()) {
			if (record.getType() == HeaderRecordType.HD) {
				HeaderTag sortOrderTag = null;
				for (HeaderRecord testHeaderrecord : testHeader.getRecords()) {
					if (testHeaderrecord.getType() == HeaderRecordType.HD) {
						for (HeaderTag tag : testHeaderrecord.getTags()) {
							if (tag.getType() == HeaderTagType.SO) {
								sortOrderTag = tag;
								break;
							}
						}
						break;
					}
				}
				assertNotNull(sortOrderTag);
				for (HeaderTag tag : record.getTags()) {
					if (tag.getType() == HeaderTagType.SO) {
						assertEquals(sortOrderTag.getValue().toUpperCase(), SortOrder.COORDINATE.toString().toUpperCase());
					} else {
						assertTrue(record.getTags().contains(tag));
					}
				}
			} else {
				assertTrue(testHeader.getRecords().contains(record));
			}
		}

		//Comparing records in sorted file
		AlignmentRecord record1 = testReader.nextRecord();
		AlignmentRecord record2 = testReader.nextRecord();

		while (record2 != null) {
			assertTrue(comparator.compare(record1, record2) <= 0);
			record1 = record2;
			record2 = testReader.nextRecord();
		}
	}
}
