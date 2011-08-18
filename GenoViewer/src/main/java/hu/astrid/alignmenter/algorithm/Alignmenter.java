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

import hu.astrid.alignmenter.core.AlignmentMutation;
import hu.astrid.alignmenter.core.Mutation;
import hu.astrid.alignmenter.core.MutationType;
import hu.astrid.read.FastaRead;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class Alignmenter {

	private static final char GAP = '-';
	private static final char FILL = '-';
	private static final float MIN_COVERAGE = 0.5f;

	private Map<String, List<Mutation>> mutationsTable;
	private List<FastaRead> sequences;
	private Map<Integer, List<AlignmentMutation>> alignmentMutations;
	private Map<String, StringBuilder> alignmentResults = new LinkedHashMap<String, StringBuilder>();

	private int offset = -1;
	private Logger logger = Logger.getLogger(Alignmenter.class);

	public Alignmenter(List<FastaRead> sequences, Map<String, List<Mutation>> mutationsTable) {
		this.mutationsTable = mutationsTable;
		this.sequences = sequences;
	}

	public void getAlignments(FileWriter fileWriter) throws IOException {
		generateAlignments();
		for (Iterator<String> i = alignmentResults.keySet().iterator(); i.hasNext();) {
			String alignmentName = i.next();
			fileWriter.write(alignmentName + "\t" + alignmentResults.get(alignmentName) + "\n");
			fileWriter.flush();
			alignmentResults.remove(i);
		}
	}

	private void generateAlignments() {
		generateOneMutationTable();
		initializeAlignmentResults();
		mutationAdaptation();
	}

	private void generateOneMutationTable() {
		alignmentMutations = new TreeMap<Integer, List<AlignmentMutation>>();
		for (String consensusName : mutationsTable.keySet()) {
			for (Mutation mutation : mutationsTable.get(consensusName)) {
				AlignmentMutation alignmentMutation = new AlignmentMutation(consensusName, mutation);
				if (isEnoughCoverage(alignmentMutation)) {
					Integer startPos = mutation.getStartPos();
					List<AlignmentMutation> alignmentMutationList = new LinkedList<AlignmentMutation>();
					if (alignmentMutations.containsKey(startPos)) {
						alignmentMutationList = alignmentMutations.get(startPos);
					}
					alignmentMutationList.add(alignmentMutation);
					alignmentMutations.put(startPos, alignmentMutationList);
				}
			}
		}
	}

	private boolean isEnoughCoverage(AlignmentMutation alignmentMutation) {
		if (alignmentMutation.getMutation().getCoverage() > MIN_COVERAGE) {
			return true;
		}
		return false;
	}

	private void initializeAlignmentResults() {
		for (FastaRead sequence : sequences) {
			alignmentResults.put(sequence.getId(), new StringBuilder(sequence.toString()));
		}
	}

	private void mutationAdaptation() {
		int i = 0;
		for (Integer startPos : alignmentMutations.keySet()) {
			if (i++ > 10000) {
				logger.info(startPos);
				i = 0;
			}
			snpMnpAdaptation(startPos);
			deletionAdaptation(startPos);
			insertionAdaptation(startPos);
		}
	}

	private void snpMnpAdaptation(Integer startPos) {
		List<AlignmentMutation> alignmentMutationList = alignmentMutations.get(startPos);
		for (AlignmentMutation alignmentMutation : alignmentMutationList) {
			Mutation mutation = alignmentMutation.getMutation();
			if (mutation.getMutationType() == MutationType.SNP || mutation.getMutationType() == MutationType.MNP) {
				alignmentResults.get(alignmentMutation.getConsensusName()).replace(mutation.getStartPos() + offset,
						mutation.getStartPos() + mutation.getLength() + offset, mutation.getMutationSequence().
								substring(1, mutation.getMutationSequence().length() - 1));
			}
		}
	}

	private void deletionAdaptation(Integer startPos) {
		List<AlignmentMutation> alignmentMutationList = alignmentMutations.get(startPos);
		for (AlignmentMutation alignmentMutation : alignmentMutationList) {
			Mutation mutation = alignmentMutation.getMutation();
			if (mutation.getMutationType() == MutationType.DELETION) {
				String delSequence = generateFillSequence(mutation.getLength(), GAP);
				alignmentResults.get(alignmentMutation.getConsensusName()).replace(mutation.getStartPos() + offset,
						mutation.getStartPos() + mutation.getLength() + offset, delSequence);
			}
		}
	}

	private String generateFillSequence(int length, char fillCharacter) {
		StringBuilder delSequence = new StringBuilder();
		for (int i = 0; i < length; i++) {
			delSequence.append(fillCharacter);
		}
		return delSequence.toString();
	}

	private void insertionAdaptation(Integer startPos) {
		List<AlignmentMutation> alignmentMutationList = alignmentMutations.get(startPos);
		Integer maxInsertionLength = 0;
		List<String> notInsertedSequencesName = new LinkedList<String>();
		for (String sequenceName : alignmentResults.keySet()) {
			notInsertedSequencesName.add(sequenceName);
		}
		for (AlignmentMutation alignmentMutation : alignmentMutationList) {
			Mutation mutation = alignmentMutation.getMutation();

			if (mutation.getMutationType() == MutationType.INSERTION) {
				if (maxInsertionLength < mutation.getLength()) {
					maxInsertionLength = mutation.getLength();
				}
				int index = notInsertedSequencesName.indexOf(alignmentMutation.getConsensusName());
				if (index > -1) {
					notInsertedSequencesName.remove(index);
				}
			}
		}
		for (AlignmentMutation alignmentMutation : alignmentMutationList) {
			Mutation mutation = alignmentMutation.getMutation();
			if (mutation.getMutationType() == MutationType.INSERTION) {
				alignmentResults.get(alignmentMutation.getConsensusName()).insert(mutation.getStartPos() + offset,
						fillInsertion(alignmentMutation.getMutation().getMutationSequence().
								substring(1, mutation.getMutationSequence().length() - 1), maxInsertionLength));
			}
		}

		for (String sequenceName : notInsertedSequencesName) {
			alignmentResults.get(sequenceName).insert(startPos + offset, fillInsertion("", maxInsertionLength));
		}

		offset += maxInsertionLength;
	}

	private String fillInsertion(String mutationSequence, Integer maxInsertionLength) {
		StringBuilder insertionSequence = new StringBuilder();
		insertionSequence.append(mutationSequence);
		for (int i = mutationSequence.length(); i < maxInsertionLength; i++) {
			insertionSequence.append(FILL);
		}
		return insertionSequence.toString();
	}

	public static void buildAlignmentFile(String inputFile, String outputFile) {
		List<String> contigIds = new ArrayList<String>();
		List<String> alignments = new ArrayList<String>();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(inputFile));
			String line = null;

			while ((line = reader.readLine()) != null) {
				String data[] = line.split("\t");
				contigIds.add(data[0]);
				alignments.add(data[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}

		int maxContigIdLength = contigIds.get(0).length();

		for (int i = 1; i < contigIds.size(); ++i) {
			if (maxContigIdLength < contigIds.get(i).length()) {
				maxContigIdLength = contigIds.get(i).length();
			}
		}

		for (int i = 0; i < contigIds.size(); ++i) {
			StringBuilder sb = new StringBuilder(contigIds.get(i));

			for (int j = contigIds.get(i).length(); j < maxContigIdLength; ++j) {
				sb.append(" ");
			}
			sb.append("      ");
			contigIds.set(i, sb.toString());
		}

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(outputFile));
			for (int i = 0; i < alignments.get(0).length(); i += 50) {
				int length = i + 50 < alignments.get(0).length() ? 50 : alignments.get(0).length() - i;
				for (int j = 0; j < contigIds.size(); ++j) {
					writer.append(contigIds.get(j) + "\t" + alignments.get(j).substring(i, i + length) + "\n");
				}
				writer.newLine();
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Alignmenter that = (Alignmenter) o;

		if (alignmentMutations != null ? !alignmentMutations.equals(that.alignmentMutations) : that.alignmentMutations != null)
			return false;
		if (alignmentResults != null ? !alignmentResults.equals(that.alignmentResults) : that.alignmentResults != null)
			return false;
		if (mutationsTable != null ? !mutationsTable.equals(that.mutationsTable) : that.mutationsTable != null)
			return false;
		if (sequences != null ? !sequences.equals(that.sequences) : that.sequences != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = mutationsTable != null ? mutationsTable.hashCode() : 0;
		result = 31 * result + (sequences != null ? sequences.hashCode() : 0);
		result = 31 * result + (alignmentMutations != null ? alignmentMutations.hashCode() : 0);
		result = 31 * result + (alignmentResults != null ? alignmentResults.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Alignmenter{" +
				"mutationsTable=" + mutationsTable + "\n" +
				", sequences=" + sequences + "\n" +
				", alignmentMutations=" + alignmentMutations + "\n" +
				", alignmentResults=" + alignmentResults + "\n" +
				'}';
	}
}
