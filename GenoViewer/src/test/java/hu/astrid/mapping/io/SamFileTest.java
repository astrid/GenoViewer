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
import hu.astrid.mapping.model.MappingBody;
import hu.astrid.mapping.model.MappingHeader;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

/**
 * @author avarga
 */
public class SamFileTest {

	private final String samFileHeader = "@HD" + '\t' + "VN:1.0" + '\t' + "SO:unsorted" + '\t' + "GO:reference" + '\n' +
			"@SQ" + '\t' + "SN:chr20" + '\t' + "LN:62435964" + '\n' +
			"@RG" + '\t' + "ID:L1" + '\t' + "PU:SC_1_10" + '\t' + "LB:SC_1" + '\t' + "SM:NA12891" + '\n' +
			"@RG" + '\t' + "ID:L2" + '\t' + "PU:SC_2_12" + '\t' + "LB:SC_2" + '\t' + "SM:NA12891" + '\n' +
			"@CO" + '\t' + "This is a test message!" + '\n' +
			"@CO" + '\t' + "This is a second test message!";

	private final String samFileBody = "read_28833_29006_6945" + '\t' + "99" + '\t' + "chr20" + '\t' + "28833" + '\t' + "20" + '\t' + "10M1D25M" + '\t' + "=" + '\t' + "28993" + '\t' + "195" + '\t' + "AGCTTAGCTAGCTACCTATATCTTGGTCTTGGCCG" + '\t' + "<<<<<<<<<<<<<<<<<<<<<:<9/,&,22;;<<<" + '\t' + "NM:i:1" + '\t' + "RG:Z:L1" + '\n' +
			"read_28833_29006_6945" + '\t' + "99" + '\t' + "chr20" + '\t' + "28833" + '\t' + "20" + '\t' + "10M1D25M" + '\t' + "=" + '\t' + "28993" + '\t' + "195" + '\t' + "AGCTTAGCTAGCTACCTATATCTTGGTCTTGGCCG" + '\t' + "<<<<<<<<<<<<<<<<<<<<<:<9/,&,22;;<<<" + '\t' + "NM:i:1" + '\t' + "RG:Z:L1";
	//"read_28701_28881_323b" + '\t' + "147" + '\t' + "chr20" + '\t' + "28834" + '\t' + "30" + '\t' + "35M" + '\t' + "=" + '\t' + "28701" + '\t' + "-168" + '\t' + "ACCTATATCTTGGCCTTGGCCGATGCGGCCTTGCA" + '\t' + "<<<<<;<<<<7;:<<<6;<<<<<<<<<<<<7<<<<" + '\t' + "MF:i:18" + '\t' + "G:Z:L2";

	private StringReader sreader;
	private MappingFile samFile;

	@Before
	public void setUp() {
		sreader = new StringReader(samFileHeader + "\n" + samFileBody);
		samFile = new MappingFile();
	}

	@Test
	public void testLoad() throws Exception {
		samFile.load(sreader);
		assertEquals(samFile.toString(), samFileHeader + '\n' + samFileBody);
	}

	@Test
	public void testGetHeader() throws Exception {
		samFile.load(sreader);
		MappingHeader samHeader = samFile.getHeader();
		assertEquals(samHeader.toString(), samFileHeader.toString());
	}

	@Test
	public void testGetBody() throws Exception {
		samFile.load(sreader);
		MappingBody samBody = samFile.getBody();
		assertEquals(samBody.toString(), samFileBody.toString());
	}

}