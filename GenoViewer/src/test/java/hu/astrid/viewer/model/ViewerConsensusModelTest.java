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

package hu.astrid.viewer.model;

import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.viewer.model.consensus.ConsensusData;
import hu.astrid.viewer.model.mutation.Mutation;
import hu.astrid.viewer.model.mutation.MutationType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import org.junit.Test;

public class ViewerConsensusModelTest {
    private static ViewerConsensusModel consensusModel;

    @BeforeClass
	public static void setupClass() {
        consensusModel = new ViewerConsensusModel();
	}

	@Test
	public void testConvertNucleotideSequenceToColor() {
		String nucString = "GATTACA";
		String colString = "230311";
		assertEquals(colString, ViewerConsensusModel.convertNucleotideSequenceToColor(nucString));
	}

	@Test
	public void testConsensusSequenceForSave() throws InterruptedException, ExecutionException, IOException, MappingFileFormatException {

		String consSeq = "NATATATAATTTAATAAATACATTCCGACGATACTGGCTCTATGGCTTAGTGGTACAGCATCGCACTTGTAATGCGAAGAT";
		String colorSeq = "00001010101010101010101010";
		List<Mutation> mutations = new ArrayList<Mutation>();

//		delete pos(0-12) 'NATAT A TAATTT' = 'NATAT TAATTTA'
		mutations.add(new Mutation(MutationType.DELETION, 6, 1, 1, 1, "A", "*"));
		consensusModel.setConsensusData(new ConsensusData(mutations, consSeq, colorSeq));
		String sequence = consensusModel.getConsensusSequence(0, 0);
		assertEquals("NATATTAATTTA", sequence.substring(0, 12));

//		delete pos(0-12) 'NATATA TA ATTT' = 'NATATA ATTT'
		mutations.clear();
		mutations.add(new Mutation(MutationType.DELETION, 6, 2, 1, 1, "TA", "**"));
		consensusModel.setConsensusData(new ConsensusData(mutations, consSeq, colorSeq));
		sequence = consensusModel.getConsensusSequence(0, 0);
		assertEquals("NATATAATTTAA", sequence.substring(0, 12));

		//delete pos(0-12) 'NATATA TAA TTT' = 'NATATA TTT'
		mutations.clear();
		mutations.add(new Mutation(MutationType.DELETION, 6, 3, 1, 1, "TAA", "***"));
		consensusModel.setConsensusData(new ConsensusData(mutations, consSeq, colorSeq));
		sequence = consensusModel.getConsensusSequence(0, 0);
		assertEquals("NATATATTTAAT", sequence.substring(0, 12));

		//insertion pos(0-12) 'NATATA G TAATTT' = 'NATATA G TAATT'
		mutations.clear();
		mutations.add(new Mutation(MutationType.INSERTION, 7, 1, 1, 1, "*", "AGT"));
		consensusModel.setConsensusData(new ConsensusData(mutations, consSeq, colorSeq));
		sequence = consensusModel.getConsensusSequence(0, 0);
		assertEquals("NATATAGTAATT", sequence.substring(0, 12));

		//insertion pos(0-12) 'NATATA GG TAATTT' = 'NATATA GG TAAT'
		mutations.clear();
		mutations.add(new Mutation(MutationType.INSERTION, 7, 2, 1, 1, "**", "AGGT"));
		consensusModel.setConsensusData(new ConsensusData(mutations, consSeq, colorSeq));
		sequence = consensusModel.getConsensusSequence(0, 0);
		assertEquals("NATATAGGTAAT", sequence.substring(0, 12));

		//insertion pos(0-12) 'NATATA GGG TAATTT' = 'NATATA GGG TAA'
		mutations.clear();
		mutations.add(new Mutation(MutationType.INSERTION, 7, 3, 1, 1, "***", "AGGGT"));
		consensusModel.setConsensusData(new ConsensusData(mutations, consSeq, colorSeq));
		sequence = consensusModel.getConsensusSequence(0, 0);
		assertEquals("NATATAGGGTAA", sequence.substring(0, 12));

		//all in one
		//NAT del_AT ATA del_A ins_GG TTT ins_G AAT del_AAA TACA ins_GGGGG TTCCGACGATACTGGCT
		//NATATAGGTTTGAATTACAGGGGGTTCCGACGATACTGGCT
		mutations.clear();
		mutations.add(new Mutation(MutationType.DELETION, 4, 2, 1, 1, "AT", "**"));
		mutations.add(new Mutation(MutationType.DELETION, 9, 1, 1, 1, "A", "*"));
		mutations.add(new Mutation(MutationType.INSERTION, 10, 2, 1, 1, "**", "AGGT"));
		mutations.add(new Mutation(MutationType.INSERTION, 13, 1, 1, 1, "*", "TGA"));
		mutations.add(new Mutation(MutationType.DELETION, 16, 3, 1, 1, "AAA", "***"));
		mutations.add(new Mutation(MutationType.INSERTION, 23, 5, 1, 1, "*****", "AGGGGGT"));
		consensusModel.setConsensusData(new ConsensusData(mutations, consSeq, colorSeq));
		sequence = consensusModel.getConsensusSequence(0, 0);
		assertEquals("NATATAGGTTTGAATTACAGGGGGTTCCGACGATACTGGCT", sequence.substring(0, 41));
	}
}
