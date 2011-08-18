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

package hu.astrid.alignmenter.algorithm;

import hu.astrid.alignmenter.core.Mutation;
import hu.astrid.alignmenter.core.MutationType;
import hu.astrid.alignmenter.util.AlignmenterReader;
import hu.astrid.read.FastaRead;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlignmenterTest {

	private static final Logger logger = Logger.getLogger(AlignmenterReader.class);

	private AlignmenterReader alignmenterReader;

	@Before
	public void before() {
		alignmenterReader = new AlignmenterReader();
	}

	private Alignmenter alignmenter;

	@Test
	public void test() {
		String expected =
				"--AAG----TT\n" +
						"--GGGGGCCTG\n" +
						"--A-GC---TT\n" +
						"TTAAG----TT\n";

		List<FastaRead> sequences = Arrays.asList(
				new FastaRead("refSeq", "AAGTT"),
				new FastaRead("con1", "AAGTT"),
				new FastaRead("con2", "ANGTT"),
				new FastaRead("con3", "AAGTT")
		);

		Map<String, List<Mutation>> mutationsMap = new HashMap<String, List<Mutation>>() {
			{
				put("con1", Arrays.asList(
						new Mutation(MutationType.INSERTION, 4, 4, 20, 30, "GGCC", "****"),
						new Mutation(MutationType.SNP, 5, 1, 20, 30, "G", "T"),
						new Mutation(MutationType.MNP, 1, 2, 20, 30, "GG", "AA")));

				put("con2", Arrays.asList(
						new Mutation(MutationType.INSERTION, 4, 1, 20, 30, "C", "*"),
						new Mutation(MutationType.DELETION, 2, 1, 20, 30, "*", "A")));

				put("con3", Arrays.asList(new Mutation(MutationType.INSERTION, 1, 2, 20, 30, "TT", "**")));
			}
		};

	}

	@Test
	public void testReadAlignmenterTest1() throws IOException {
		final String RES_DIR = "pacnes";
		List<String> fileNameList = Arrays.asList(
				"Propionibacterium_acnes_ref_seq.fasta",
				"s0205_20090619_PA_18_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090619_PA_18_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090619_PA_20_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090619_PA_20_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090619_PA_226_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090619_PA_226_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090619_PA_23_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090619_PA_23_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090619_PA_24_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090619_PA_24_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090619_PA_434_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090619_PA_434_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090619_PA_54_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090619_PA_54_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090619_PA_9880_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090619_PA_9880_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090703_PA_33810_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090703_PA_33810_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090703_PA_35934_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090703_PA_35934_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090703_PA_440671_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090703_PA_440671_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090703_PA_51_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090703_PA_51_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090902_12S_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090902_12S_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090902_51318_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090902_51318_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20090902_Asn12_F3.csfasta_alignment.sam.bam.sort_consensus.fasta",
				"s0205_20090902_Asn12_F3.csfasta_alignment.sam.bam.sort.csv",
				"s0205_20100628_Pacnes_10281614_F3.csfasta.sam.bam.sort_consensus.fasta",
				"s0205_20100628_Pacnes_10281614_F3.csfasta.sam.bam.sort.csv",
				"s0205_20100628_Pacnes_10334913_F3.csfasta.sam.bam.sort_consensus.fasta",
				"s0205_20100628_Pacnes_10334913_F3.csfasta.sam.bam.sort.csv",
				"s0205_20100628_P_acnes_157256_F3.csfasta.sam.bam.sort_consensus.fasta",
				"s0205_20100628_P_acnes_157256_F3.csfasta.sam.bam.sort.csv",
				"s0205_20100628_P_acnes_299038_F3.csfasta.sam.bam.sort_consensus.fasta",
				"s0205_20100628_P_acnes_299038_F3.csfasta.sam.bam.sort.csv",
				"s0205_20100628_P_acnes_317669_F3.csfasta.sam.bam.sort_consensus.fasta",
				"s0205_20100628_P_acnes_317669_F3.csfasta.sam.bam.sort.csv",
				"s0205_20100628_Pacnes_7751723_F3.csfasta.sam.bam.sort_consensus.fasta",
				"s0205_20100628_Pacnes_7751723_F3.csfasta.sam.bam.sort.csv",
				"s0205_20100628_Pacnes_8729417_F3.csfasta.sam.bam.sort_consensus.fasta",
				"s0205_20100628_Pacnes_8729417_F3.csfasta.sam.bam.sort.csv",
				"s0205_20100628_P_acnes_887674_F3.csfasta.sam.bam.sort_consensus.fasta",
				"s0205_20100628_P_acnes_887674_F3.csfasta.sam.bam.sort.csv"
		);


		Alignmenter actual;
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter("out.info");
			actual = alignmenterReader.readAlignmenter(RES_DIR, fileNameList);
//            fileWriter.write(actual.getAlignments());
			fileWriter.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (alignmenterReader != null) {
					alignmenterReader.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		//logger.info("actual:\n" + actual.getAlignments());

	}

	@Test
	public void testReadAlignmenterTest21() throws IOException {
		final String RES_DIR = "test";
		List<String> fileNameList = Arrays.asList("0.fasta",
				"1.fasta",
				"1.csv"
//				"2.fasta",
//				"2.csv"
		);

		Alignmenter actual = null;
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter("multiple_alignment_consensuses_result.txt");
			actual = alignmenterReader.readAlignmenter(RES_DIR, fileNameList);
			actual.getAlignments(fileWriter);
			fileWriter.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				alignmenterReader.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

	}


}