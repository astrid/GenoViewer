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

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

/**
 * @author avarga
 */
public class SamReaderTest {

//    private final String samFileHeader = "@HD" + '\t' + "VN:1.0" + '\t' + "SO:unsorted" + '\t' + "GO:reference" + '\n' +
//                                         "@SQ" + '\t' + "SN:chr20" + '\t' + "LN:62435964" + '\n' +
//                                         "@RG" + '\t' + "ID:L1" + '\t' + "PU:SC_1_10" + '\t' + "LB:SC_1" + '\t' + "SM:NA12891" + '\n' +
//                                         "@RG" + '\t' + "ID:L2" + '\t' + "PU:SC_2_12" + '\t' + "LB:SC_2" + '\t' + "SM:NA12891" + '\n' +
//                                         "@CO" + '\t' + "This is a test message!" + '\n' +
//                                         "@CO" + '\t' + "This is a second test message!";

	private final String samFileHeader = "@HD\tVN:1.0\tSO:unsorted\n" +
			"@SQ\tSN:Generated Genom: 1\tLN:20000000\n" +
			"@PG\tID:ReferenceAssembly";

	private final String samFileBody = "read_28833_29006_6945" + '\t' + "99" + '\t' + "chr20" + '\t' + "28833" + '\t' + "20" + '\t' + "10M1D25M" + '\t' + "=" + '\t' + "28993" + '\t' + "195" + '\t' + "AGCTTAGCTAGCTACCTATATCTTGGTCTTGGCCG" + '\t' + "<<<<<<<<<<<<<<<<<<<<<:<9/,&,22;;<<<" + '\t' + "NM:i:1" + '\t' + "RG:Z:L1" + '\n' +
			"read_28701_28881_323b" + '\t' + "147" + '\t' + "chr20" + '\t' + "28834" + '\t' + "30" + '\t' + "35M" + '\t' + "=" + '\t' + "28701" + '\t' + "-168" + '\t' + "ACCTATATCTTGGCCTTGGCCGATGCGGCCTTGCA" + '\t' + "<<<<<;<<<<7;:<<<6;<<<<<<<<<<<<7<<<<" + '\t' + "MF:i:18" + '\t' + "G:Z:L2";
//"170701\tGenerated Genom FW - secondary genom 16\t0\tGenerated Genom 18\t255\t12M3D37M\t=\t0\t0\tAGGTATAACATACGCTTGGGGGGATGGCCCGTTCTCGCTCACTATGCTA\t*\tCS:Z:A0201333011331332010000023103003102223322112331323\tMD:Z:12^GGT37" +
//"512379\tGenerated Genom RV - secondary genom\t50\t0\tGenerated Genom\t55	255	49M	=	0	0	TCGCTCACTATGCTACGACTGTGCCTCATCGTATAGCCGGCTAAATGCC	*	CS:Z:C2233221123313231321211130221323133323030323003130	MD:Z:49" +
//"648473 Generated Genom FW - secondary genom 71	0	Generated Genom	76	255	49M	=	0	0	TGCCTCATCGTATAGCCGGCTAAATGCCAATCGGTATGCGATTTTTAGC	*	CS:Z:G1130221323133323030323003130103230133133230000323	MD:Z:49";

	private StringReader samReader;
	private StringReader samHeaderReader;

	@Before
	public void setUp() {
		samReader = new StringReader(samFileBody);
		samHeaderReader = new StringReader(samFileHeader + "\n" + samFileBody);
	}

	@Test
	public void testReadHeaderH() throws Exception {
		SamReader samReader = new SamReader(samHeaderReader);
		MappingHeader samHeader = samReader.readHeader();
		assertEquals(samFileHeader, samHeader.toString());
		try {
			samReader.readHeader();
			fail("IllegalStateException expected");
		} catch (IllegalStateException e) {
			assertEquals("Illegal header access!", e.getMessage());
		}
	}

	@Test
	public void testReadHeader() throws Exception {
		SamReader samReader = new SamReader(this.samReader);
		MappingHeader samHeader = samReader.readHeader();
		assertEquals(null, samHeader);
	}

	@Test
	public void testNextRecordH() throws Exception {
		SamReader samReader = new SamReader(samHeaderReader);

		String[] bodyRecords = samFileBody.split("\n");

		String rec1 = samReader.nextRecord().toString();
		assertEquals(bodyRecords[0], rec1);

		String rec2 = samReader.nextRecord().toString();
		assertEquals(bodyRecords[1], rec2);

		AlignmentRecord rec3 = samReader.nextRecord();
		assertEquals(null, rec3);
	}

	@Test
	public void testNextRecord() throws Exception {
		SamReader samReader = new SamReader(this.samReader);

		String[] bodyRecords = samFileBody.split("\n");

		String rec1 = samReader.nextRecord().toString();
		assertEquals(bodyRecords[0], rec1);

		String rec2 = samReader.nextRecord().toString();
		assertEquals(bodyRecords[1], rec2);

		AlignmentRecord rec3 = samReader.nextRecord();
		assertEquals(null, rec3);
	}
}