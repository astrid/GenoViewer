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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.astrid.viewer.util;

import org.junit.Test;

import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.OptionalTag;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Mat
 */
public class AlignmentTest {

	@Test
	public void testAlignment() {
		OptionalTag opt = new OptionalTag();
		opt.setTagName("MD");
		opt.setValueType('Z');
		AlignmentRecord read = new AlignmentRecord();

		opt.setValue("3");
		read.addOptionalTag(opt);
		read.setCigar("2S3M2S");
		read.setSequence("TTAAATT");
		assertEquals(7, Alignment.getRefLength(read));
		System.out.println("1");
		assertEquals("Hiba alignment 1 (SoftClip)", "   \n|||\nAAA", Alignment.getAlignment(read, ""));

		opt.setValue("3^TA2");
		read.addOptionalTag(opt);
		read.setCigar("3M2I2M");
		read.setSequence("TTTTACC");
		assertEquals(5, Alignment.getRefLength(read));
		assertEquals("Hiba alignment 2", "TTT--CC\n|||  ||\nTTTTACC", Alignment.getAlignment(read, "TTTCC"));
//		assertEquals("TTTCC", Alignment.getAlignmentSequence(read, "", false));

		opt.setValue("4");
		read.addOptionalTag(opt);
		read.setCigar("4M");
		read.setSequence("GGGG");
		assertEquals(4, Alignment.getRefLength(read));
		assertEquals("Hiba alignment 3", "GGGG\n||||\nGGGG", Alignment.getAlignment(read, "GGGG"));
//		assertEquals("GGGG", Alignment.getAlignmentSequence(read, "", false));

		opt.setValue("2^AGC2");
		read.addOptionalTag(opt);
		read.setCigar("2M3D2M");
		read.setSequence("TTTT");
		assertEquals(7, Alignment.getRefLength(read));
		assertEquals("Hiba alignment 4", "TTAGCTT\n||   ||\nTT---TT", Alignment.getAlignment(read, "TTAGCTT"));
//		assertEquals("TT---TT", Alignment.getAlignmentSequence(read, "", false));

		opt.setValue("2AA3");
		read.addOptionalTag(opt);
		read.setCigar("7M");
		read.setSequence("TTAATGT");
		assertEquals(7, Alignment.getRefLength(read));
		assertEquals("Hiba alignment 5", "TTCCTGT\n||..|||\nTTAATGT", Alignment.getAlignment(read, "TTCCTGT"));
//		assertEquals("TT--TGT", Alignment.getAlignmentSequence(read, "", false));

		opt.setValue("2AA3");
		read.addOptionalTag(opt);
		read.setCigar("7M");
		read.setSequence("TTAATGT");
		assertEquals(7, Alignment.getRefLength(read));
		assertEquals("Hiba alignment 6", "       \n||..|||\nTTAATGT", Alignment.getAlignment(read, ""));

		opt.setValue("2AA3^A0");
		read.addOptionalTag(opt);
		read.setCigar("7M1I0M");
		read.setSequence("TTAATGTA");
		assertEquals(7, Alignment.getRefLength(read));
		assertEquals("Hiba alignment 7", "TTCCTGT-\n||..||| \nTTAATGTA", Alignment.getAlignment(read, "TTCCTGT"));
//		assertEquals("TT--TGT", Alignment.getAlignmentSequence(read, "", false));

		opt.setValue("");
		read.addOptionalTag(opt);
		read.setCigar("14H20M1I5M14H");
		read.setSequence("TAAACGTAAAACCTAAAACCATAAAA");
		assertEquals(25, Alignment.getRefLength(read));
		//|||||||||||||||||||| |||||
		//TAAACGTAAAACCTAAAACCATAAAA
		assertEquals("Hiba alignment 8 (HardClip without Mdtag)", "                          \n|||||||||||||||||||| |||||\nTAAACGTAAAACCTAAAACCATAAAA", Alignment.getAlignment(read, ""));
	}

//	@Test
//	public void testMergeAlignments() {
//		String al1 = "";
//		String al2 = "TT";
//		assertEquals("TT", Alignment.mergeAlignments(al1, al2));
//		
//		al1 = "TT";
//		al2 = "";
//		assertEquals("TT", Alignment.mergeAlignments(al1, al2));
//		
//		al1 = "T-T";
//		al2 = "TAT";
//		assertEquals("TAT", Alignment.mergeAlignments(al1, al2));
//		
//		al1 = "T-T";
//		al2 = "T-T";
//		assertEquals("T-T", Alignment.mergeAlignments(al1, al2));
//		
//		al1 = "T-T";
//		al2 = "TA-";
//		assertEquals("TAT", Alignment.mergeAlignments(al1, al2));
//	}
	@Test
	public void testConsensusGen() {
		OptionalTag opt = new OptionalTag();
		AlignmentRecord read = new AlignmentRecord();

		opt.setTagName("MD");
		opt.setValueType('Z');

		//Without MD tag

		read.setCigar("2S3M2D3S");
		read.setSequence("TTAAAACG");
		assertEquals("AAANN", Alignment.getSequenceForConsensus(read));

		read.setCigar("2S3M2I2M");
		read.setSequence("TTAAAGGTC");
		assertEquals("AAATC", Alignment.getSequenceForConsensus(read));

//		//With MD

		opt.setValue("3");
		read.addOptionalTag(opt);
		read.setCigar("2S3M");
		read.setSequence("TTAAA");
		assertEquals("AAA", Alignment.getSequenceForConsensus(read));

		opt.setValue("3GG");
		read.addOptionalTag(opt);
		read.setCigar("5M");
		assertEquals("TTAGG", Alignment.getSequenceForConsensus(read));

		opt.setValue("2^GG2");
		read.addOptionalTag(opt);
		read.setCigar("1S2M2D2M");
		assertEquals("TAGGAA", Alignment.getSequenceForConsensus(read));

		opt.setValue("3TC1^TC2A0^AA2");
		read.addOptionalTag(opt);
		read.setCigar("2S5M2D3M2I2M");
		read.setSequence("TTAAAGTCGATAACC");
		assertEquals("AAATCCTCGAACC", Alignment.getSequenceForConsensus(read));
	}
}
