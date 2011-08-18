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

import hu.astrid.mapping.model.*;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class SamWriterTest {

	/*
	@HD	VN:1.0
	@SQ	SN:chr20	LN:62435964
	@RG	ID:L1	PU:SC_1_10	LB:SC_1	SM:NA12891
	@RG	ID:L2	PU:SC_2_12	LB:SC_2	SM:NA12891
	read_28833_29006_6945	99	chr20	28833	20	10M1D25M	=	28993	195	AGCTTAGCTAGCTACCTATATCTTGGTCTTGGCCG	<<<<<<<<<<<<<<<<<<<<<:<9/,&,22;;<<<	NM:i:1	RG:Z:L1
	read_28701_28881_323b	147	chr20	28834	30	35M	=	28701	-168	ACCTATATCTTGGCCTTGGCCGATGCGGCCTTGCA	<<<<<;<<<<7;:<<<6;<<<<<<<<<<<<7<<<<	MF:i:18	G:Z:L2
	*/

	private SamWriter samWriter;
	private StringWriter stringWriter;

	@Before
	public void setUp() throws Exception {
		stringWriter = new StringWriter();
		samWriter = new SamWriter(new BufferedWriter(stringWriter));
	}

	@Test
	public void testWriteHeader() throws Exception {
		HeaderRecord headerRecordHD = new HeaderRecord(HeaderRecordType.HD);
		headerRecordHD.addTag(new HeaderTag(HeaderTagType.VN, "1.0"));

		HeaderRecord headerRecordSQ = new HeaderRecord(HeaderRecordType.SQ);
		headerRecordSQ.addTag(new HeaderTag(HeaderTagType.SN, "chr20"));
		headerRecordSQ.addTag(new HeaderTag(HeaderTagType.LN, "62435964"));

		HeaderRecord headerRecordRG = new HeaderRecord(HeaderRecordType.RG);
		headerRecordRG.addTag(new HeaderTag(HeaderTagType.ID, "L1"));
		headerRecordRG.addTag(new HeaderTag(HeaderTagType.PU, "SC_1_10"));
		headerRecordRG.addTag(new HeaderTag(HeaderTagType.LB, "SC_1"));
		headerRecordRG.addTag(new HeaderTag(HeaderTagType.SM, "NA12891"));

		HeaderRecord headerRecordRG2 = new HeaderRecord(HeaderRecordType.RG);
		headerRecordRG2.addTag(new HeaderTag(HeaderTagType.ID, "L2"));
		headerRecordRG2.addTag(new HeaderTag(HeaderTagType.PU, "SC_2_12"));
		headerRecordRG2.addTag(new HeaderTag(HeaderTagType.LB, "SC_2"));
		headerRecordRG2.addTag(new HeaderTag(HeaderTagType.SM, "NA12891"));

		MappingHeader samFileHeader = new MappingHeader();
		samFileHeader.addRecord(headerRecordHD);
		samFileHeader.addRecord(headerRecordSQ);
		samFileHeader.addRecord(headerRecordRG);
		samFileHeader.addRecord(headerRecordRG2);

		samWriter.writeHeader(samFileHeader);

		assertEquals(
				"@HD\tVN:1.0\n" +
						"@SQ\tSN:chr20\tLN:62435964\n" +
						"@RG\tID:L1\tPU:SC_1_10\tLB:SC_1\tSM:NA12891\n" +
						"@RG\tID:L2\tPU:SC_2_12\tLB:SC_2\tSM:NA12891\n",
				stringWriter.toString());

		AlignmentRecord samBodyRecord = new AlignmentRecord();
		samBodyRecord.setQueryName("read_28833_29006_6945");
		samBodyRecord.setFlag((short) 99);
		samBodyRecord.setReferenceName("chr20");
		samBodyRecord.setPosition(28833);
		samBodyRecord.setMappingQuality((byte) 20);
		samBodyRecord.setCigar("10M1D25M");
		samBodyRecord.setMateReferenceName("=");
		samBodyRecord.setMatePosition(28993);
		samBodyRecord.setInsertSize(195);
		samBodyRecord.setSequence("AGCTTAGCTAGCTACCTATATCTTGGTCTTGGCCG");
		samBodyRecord.setQuality("<<<<<<<<<<<<<<<<<<<<<:<9/,&,22;;<<<");

		OptionalTag optionalTag = new OptionalTag();
		optionalTag.setTagName("NM");
		optionalTag.setValueType('i');
		optionalTag.setValue("1");
		samBodyRecord.addOptionalTag(optionalTag);

		optionalTag = new OptionalTag();
		optionalTag.setTagName("RG");
		optionalTag.setValueType('Z');
		optionalTag.setValue("L1");
		samBodyRecord.addOptionalTag(optionalTag);

		samWriter.writeRecord(samBodyRecord);

		samBodyRecord = new AlignmentRecord();
		samBodyRecord.setQueryName("read_28701_28881_323b");
		samBodyRecord.setFlag((short) 147);
		samBodyRecord.setReferenceName("chr20");
		samBodyRecord.setPosition(28834);
		samBodyRecord.setMappingQuality((byte) 30);
		samBodyRecord.setCigar("35M");
		samBodyRecord.setMateReferenceName("=");
		samBodyRecord.setMatePosition(28701);
		samBodyRecord.setInsertSize(-168);
		samBodyRecord.setSequence("ACCTATATCTTGGCCTTGGCCGATGCGGCCTTGCA");
		samBodyRecord.setQuality("<<<<<;<<<<7;:<<<6;<<<<<<<<<<<<7<<<<");

		optionalTag = new OptionalTag();
		optionalTag.setTagName("MF");
		optionalTag.setValueType('i');
		optionalTag.setValue("18");
		samBodyRecord.addOptionalTag(optionalTag);

		optionalTag = new OptionalTag();
		optionalTag.setTagName("G");
		optionalTag.setValueType('Z');
		optionalTag.setValue("L2");
		samBodyRecord.addOptionalTag(optionalTag);

		samWriter.writeRecord(samBodyRecord);

		assertEquals(
				"@HD\tVN:1.0\n" +
						"@SQ\tSN:chr20\tLN:62435964\n" +
						"@RG\tID:L1\tPU:SC_1_10\tLB:SC_1\tSM:NA12891\n" +
						"@RG\tID:L2\tPU:SC_2_12\tLB:SC_2\tSM:NA12891\n" +
						"read_28833_29006_6945\t99\tchr20\t28833\t20\t10M1D25M\t=\t28993\t195\tAGCTTAGCTAGCTACCTATATCTTGGTCTTGGCCG\t<<<<<<<<<<<<<<<<<<<<<:<9/,&,22;;<<<\tNM:i:1\tRG:Z:L1\n" +
						"read_28701_28881_323b\t147\tchr20\t28834\t30\t35M\t=\t28701\t-168\tACCTATATCTTGGCCTTGGCCGATGCGGCCTTGCA\t<<<<<;<<<<7;:<<<6;<<<<<<<<<<<<7<<<<\tMF:i:18\tG:Z:L2\n",
				stringWriter.toString());
	}

	@Test
	public void testMethod() throws Exception {
		HeaderRecord headerRecordHD = new HeaderRecord(HeaderRecordType.HD);
		headerRecordHD.addTag(new HeaderTag(HeaderTagType.VN, "1.0"));

		HeaderRecord headerRecordSQ = new HeaderRecord(HeaderRecordType.SQ);
		headerRecordSQ.addTag(new HeaderTag(HeaderTagType.SN, "Generated genom"));
		headerRecordSQ.addTag(new HeaderTag(HeaderTagType.LN, "62435964"));

		HeaderRecord headerRecordRG = new HeaderRecord(HeaderRecordType.RG);
		headerRecordRG.addTag(new HeaderTag(HeaderTagType.ID, "RefAssemblyAlignmenter"));

		MappingHeader samFileHeader = new MappingHeader();
		samFileHeader.addRecord(headerRecordHD);
		samFileHeader.addRecord(headerRecordSQ);
		samFileHeader.addRecord(headerRecordRG);

		samWriter.writeHeader(samFileHeader);

		AlignmentRecord samBodyRecord = new AlignmentRecord();
		samBodyRecord.setQueryName("86 Generated genom FW - secondary genom 13");
		samBodyRecord.setFlag((short) (
				AlignmentFlag.PAIRED.getValue() |
						AlignmentFlag.MAPPED_PAIR_MEMBER.getValue() |
						AlignmentFlag.FIRST_READ_OF_PAIR.getValue()
		));//81
		samBodyRecord.setReferenceName("Generated genom");
		samBodyRecord.setPosition(13);
		samBodyRecord.setMappingQuality((byte) 255);
		samBodyRecord.setCigar("16M4D32M");
		samBodyRecord.setMateReferenceName("*");
		samBodyRecord.setMatePosition(0);
		samBodyRecord.setInsertSize(0);
		samBodyRecord.setSequence("1232023110232203010221202101311011013011213002313");
		samBodyRecord.setQuality("*");

//		SAMBodyRecordOptionalTag optionalTag = new SAMBodyRecordOptionalTag();
//		optionalTag.setTag("NM");
//		optionalTag.setVtype("i");
//		optionalTag.setValue("1");
//		samBodyRecord.addOptionalTags(optionalTag);
//		
//		optionalTag = new SAMBodyRecordOptionalTag();
//		optionalTag.setTag("RG");
//		optionalTag.setVtype("Z");
//		optionalTag.setValue("L1");
//		samBodyRecord.addOptionalTags(optionalTag);

		samWriter.writeRecord(samBodyRecord);

		assertEquals(
				"@HD\tVN:1.0\n" +
						"@SQ\tSN:Generated genom\tLN:62435964\n" +
						"@RG\tID:RefAssemblyAlignmenter\n" +
						"86 Generated genom FW - secondary genom 13\t67\tGenerated genom\t13\t255\t16M4D32M\t*\t0\t0\t1232023110232203010221202101311011013011213002313\t*\n",
				stringWriter.toString());
	}
}
