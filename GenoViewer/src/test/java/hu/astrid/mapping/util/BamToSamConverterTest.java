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
import hu.astrid.mapping.io.BamReader;
import hu.astrid.mapping.io.SamReader;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.BamHeader;
import hu.astrid.mapping.model.MappingHeader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * @author user
 */
public class BamToSamConverterTest {
	
	private static final String RESOURCES_DIR = System.getProperty("ngsc.testfiles.dir");

	private static final String BAM_PATH = "test.bam";

	private static final File BAM_FILE = new File(RESOURCES_DIR, BAM_PATH);
	BufferedInputStream bamIn;

	@Test
	public void testConvertWithHeader() throws Exception {
		bamIn = new BufferedInputStream(new FileInputStream(BAM_FILE));
		StringWriter samOut = new StringWriter();
		BamToSamConverter bamToSamConverter = new BamToSamConverter(bamIn, samOut, true);
		bamToSamConverter.convert();

		bamIn = new BufferedInputStream(new FileInputStream(BAM_FILE));
		BamReader bamReder = new BamReader(bamIn);
		BamHeader bamHeader = bamReder.readHeader();

		StringReader samIn = new StringReader(samOut.toString());
		SamReader samReader = new SamReader(samIn);
		MappingHeader samHeader = samReader.readHeader();

		assertEquals(bamHeader.toString(), samHeader.toString());

		List<AlignmentRecord> bamRecords = new ArrayList<AlignmentRecord>();
		AlignmentRecord record = null;
		while ((record = bamReder.nextRecord()) != null)
			bamRecords.add(record);

		List<AlignmentRecord> samRecords = new ArrayList<AlignmentRecord>();
		record = null;
		while ((record = samReader.nextRecord()) != null)
			samRecords.add(record);

		assertEquals(bamRecords.size(), samRecords.size());

		Iterator<AlignmentRecord> samIterator = samRecords.iterator();

		for (AlignmentRecord bamAlignmentR : bamRecords)
			assertEquals(bamAlignmentR.toString(), samIterator.next().toString());
		bamIn.close();
	}

	@Test
	public void testConvertWithOutHeader() throws Exception {
		bamIn = new BufferedInputStream(new FileInputStream(BAM_FILE));
		StringWriter samOut = new StringWriter();
		BamToSamConverter bamToSamConverter = new BamToSamConverter(bamIn, samOut, false);
		bamToSamConverter.convert();

		bamIn = new BufferedInputStream(new FileInputStream(BAM_FILE));
		BamReader bamReder = new BamReader(bamIn);

		StringReader samIn = new StringReader(samOut.toString());
		SamReader samReader = new SamReader(samIn);
		MappingHeader samHeader = samReader.readHeader();

		assertEquals(null, samHeader);//missing sam header

		List<AlignmentRecord> bamRecords = new ArrayList<AlignmentRecord>();
		AlignmentRecord record = null;
		while ((record = bamReder.nextRecord()) != null)
			bamRecords.add(record);

		List<AlignmentRecord> samRecords = new ArrayList<AlignmentRecord>();
		record = null;
		while ((record = samReader.nextRecord()) != null)
			samRecords.add(record);

		assertEquals(bamRecords.size(), samRecords.size());

		Iterator<AlignmentRecord> samIterator = samRecords.iterator();

		for (AlignmentRecord bamAlignmentR : bamRecords)
			assertEquals(bamAlignmentR.toString(), samIterator.next().toString());
		bamIn.close();
	}
}
