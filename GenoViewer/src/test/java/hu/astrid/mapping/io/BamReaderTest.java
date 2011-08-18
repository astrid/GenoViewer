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
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.MappingHeader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;

public class BamReaderTest {
	
	private static final String RESOURCES_DIR = System.getProperty("ngsc.testfiles.dir");

	private static final String BAM_PATH = "test.bam";

	private final String bamHeader = "@HD" + '\t' + "VN:1.0" + '\n'
			+ "@SQ" + '\t' + "SN:ref" + '\t' + "LN:2000";
	private final String ar1 = "read_1\t25344\tref\t1\t20\t50M\t*\t0\t0\tGCAGGCTTCTGGGGTCGCAAGCCCGAGATTCTCGTCCGGTTTAGAGGTCT\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\tCS:Z:T13120320221000123310230032223022231203010032220122";
	private final String ar2 = "read_2\t25344\tref\t51\t20\t50M\t*\t0\t0\tTCTCACGGGTGAGCCCAAGATCGATTTGAGCGCATTGAATCGATCCTGGG\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\tCS:Z:T02221130011223001022323230012233313012032323202100";
	private final String ar3 = "read_3\t25344\tref\t101\t20\t5M2I5M3I35M\t*\t0\t0\tCTTTTAAGAGAAGTCTAGTAGGTTGGCATGGGCCATACCCTTTACGTCAC\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\tCS:Z:T22000302222021223213201010313100301331002003131211";
	private final String ar4 = "read_4\t25344\tref\t146\t20\t5M2D5M3D40M\t*\t0\t0\tCGGCGAGTGGAAGTATGTCAAGAATGTCGCGTCACGATTCTGCTCGACGG\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\tCS:Z:T23033221102021331121022031123331211323022132232130";
	private BamReader bamReader;

	public BamReaderTest() {
	}

	@Before
	public void setUp() throws Exception {
		bamReader = new BamReader(new BufferedInputStream(new FileInputStream(new File(RESOURCES_DIR, BAM_PATH))));
	}

	@Test
	public void testReadHeader() throws Exception {
		MappingHeader mh = bamReader.readHeader();
		assertEquals(bamHeader, mh.toString());
		try {
			bamReader.readHeader();
		} catch (RuntimeException rte) {
			if (!rte.getMessage().equals("Illegal header access!"))
				fail("Unexpected exception: "+rte.getMessage());
		}
	}

	@Test
	public void testNextRecord() throws Exception {
			AlignmentRecord t1 = bamReader.nextRecord();
			assertEquals(ar1, t1.toString());

			AlignmentRecord t2 = bamReader.nextRecord();
			assertEquals(ar2, t2.toString());

			AlignmentRecord t3 = bamReader.nextRecord();
			assertEquals(ar3, t3.toString());

			AlignmentRecord t4 = bamReader.nextRecord();
			assertEquals(ar4, t4.toString());

			AlignmentRecord t5 = bamReader.nextRecord();
			assertEquals(null, t5);

			AlignmentRecord t6 = bamReader.nextRecord();
			assertEquals(null, t6);
	}
}
