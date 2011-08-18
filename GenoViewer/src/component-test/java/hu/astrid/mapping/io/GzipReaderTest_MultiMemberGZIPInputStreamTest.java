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

import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.BamHeader;
import hu.astrid.mapping.model.MappingHeader;
import hu.astrid.test.FileSupportSystemTestBase;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author user
 */
public class GzipReaderTest_MultiMemberGZIPInputStreamTest extends FileSupportSystemTestBase {
	private static final String BAM_PATH = "alignment1.bam";
	private static final String SAM_PATH = "alignment1.sam";

	@Test
	public void testRead() throws Exception {
		BamReader bamReader = null;
		SamReader samReader = null;

		try {
			FileInputStream bamIn = new FileInputStream(new File(TEST_RESOURCES_DIR, BAM_PATH));

			bamReader = new BamReader(bamIn);

			File samFile = new File(TEST_RESOURCES_DIR, SAM_PATH);

			samReader = new SamReader(samFile);

			BamHeader bamHeader = bamReader.readHeader();
			MappingHeader samHeader = samReader.readHeader();
			assertEquals(samHeader.toString(), bamHeader.toString());

			AlignmentRecord samRec;

			while ((samRec = samReader.nextRecord()) != null) {
				AlignmentRecord bamRec = bamReader.nextRecord();
				assertEquals(bamRec.toString(), samRec.toString());
			}
		} finally {
			if (bamReader != null) {
				bamReader.close();
			}
			if (samReader != null) {
				samReader.close();
			}
		}

	}

}