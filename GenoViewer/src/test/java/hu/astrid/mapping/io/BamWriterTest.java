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
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.BamHeader;
import hu.astrid.mapping.model.MappingHeader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BamWriterTest {
	
	private static final String RESOURCES_DIR = System.getProperty("ngsc.testfiles.dir");

	private static final String BAM_PATH = "test.bam";

	private BamReader bamReader;
	private BamWriter bamWriter;
	private ByteArrayOutputStream byteOutStream;

	@Before
	public void setUp() throws Exception {
		bamReader = new BamReader(new BufferedInputStream(new FileInputStream(new File(RESOURCES_DIR, BAM_PATH))));
		byteOutStream = new ByteArrayOutputStream();
		bamWriter = new BamWriter(byteOutStream);
	}

	@After
	public void tearDown() throws Exception {
		if (bamReader != null) {
			bamReader.close();
		}
	}

	@Test
	public void testWriteHeader() throws Exception {
		MappingHeader bamHeader = bamReader.readHeader();
		bamWriter.writeHeader(bamHeader);
		bamWriter.close();

		ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteOutStream.toByteArray());
		BamReader bamR = new BamReader(byteInStream);
		BamHeader bamHeader2 = bamR.readHeader();
		assertEquals(bamHeader, bamHeader2);
	}

	@Test
	public void testWriteRecord_First() throws Exception {
		AlignmentRecord alignmentRec = bamReader.nextRecord();
		try {
			bamWriter.writeRecord(alignmentRec);
			fail("MappingFileFormatException expected, but not thrown.");
		} catch (MappingFileFormatException mfe) {
			assertEquals("Missing header!", mfe.getMessage());
		}
		if (bamWriter != null) {
			bamWriter.close();
		}
	}

	@Test
	public void testWriteRecord_WrongAlignment() throws Exception {
		BamHeader bamHeader = bamReader.readHeader();
		AlignmentRecord alignmentRec = new AlignmentRecord();
		alignmentRec.setReferenceName("wrong");
		bamWriter.writeHeader(bamHeader);
		try {
			bamWriter.writeRecord(alignmentRec);
			fail("MappingFileFormatException expected, but not thrown.");
		} catch (MappingFileFormatException mfe) {
			assertEquals("Invalid reference name![wrong]", mfe.getMessage());
		}
		if (bamWriter != null) {
			bamWriter.close();
		}
	}

	@Test
	public void testWriteRecord() throws Exception {
		BamHeader bamHeader = bamReader.readHeader();
		bamWriter.writeHeader(bamHeader);

		List<AlignmentRecord> alignmentRecS = new ArrayList<AlignmentRecord>();
		AlignmentRecord alignmentRec = null;
		while ((alignmentRec = bamReader.nextRecord()) != null)
			alignmentRecS.add(alignmentRec);

		for (AlignmentRecord alignmentRecord : alignmentRecS)
			bamWriter.writeRecord(alignmentRecord);

		bamWriter.close();

		ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteOutStream.toByteArray());
		BamReader bamR = new BamReader(byteInStream);
		for (AlignmentRecord alignmentRecord : alignmentRecS) {
			AlignmentRecord aligmentRec = bamR.nextRecord();
			assertEquals(alignmentRecord.toString(), aligmentRec.toString());
		}
		AlignmentRecord alignmentR = bamR.nextRecord();
		assertEquals(null, alignmentR);
		bamR.close();

	}
}
