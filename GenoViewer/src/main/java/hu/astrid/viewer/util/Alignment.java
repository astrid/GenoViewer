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

package hu.astrid.viewer.util;

import hu.astrid.mapping.model.AlignmentRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * 
 * @author Mat
 */
public class Alignment {

	private static Logger logger = Logger.getLogger(Alignment.class);

	public static enum OperatorType {

		INSERTION('I'), DELETION('D'), SOFTCLIP('S'), HARDCLIP('H'), SNP('J');//joker
		private Character op;

		OperatorType(Character op) {
			this.op = op;
		}

		public Character toChar() {
			return op;
		}
	}

	/**
	 * Parse sizes from cigar string.
	 * 
	 * @param cigar
	 * @return
	 */
	public static List<Integer> parseSizes(String cigar) {
		List<Integer> sizes = new ArrayList<Integer>();
		int start = 0;
		for (int i = 0; i < cigar.length(); ++i) {
			if (isCigarLetter(cigar.charAt(i))) {
				sizes.add(new Integer(cigar.substring(start, i)));
				start = i + 1;
			}
		}
		return sizes;
	}

	/**
	 * Parse operators from cigar string.
	 * 
	 * @param cigar
	 * @return
	 */
	public static List<Character> parseOperators(String cigar) {
		List<Character> operators = new ArrayList<Character>();
		for (int i = 0; i < cigar.length(); ++i) {
			if (isCigarLetter(cigar.charAt(i))) {
				operators.add(cigar.charAt(i));
			}
		}
		return operators;
	}

	/**
	 * @param c
	 *            character of ciqar string
	 * @return true if {@code c} is a letter in a cigar string<br>
	 *         false if {@code c} is a number
	 */
	private static boolean isCigarLetter(char c) {
		if (c == 'M' || c == 'I' || c == 'D' || c == 'N' || c == 'S' || c == 'H' || c == 'P') {
			return true;
		}
		return false;
	}

	/**
	 * Calculate the nucleotid length, from operators and sizes.
	 * 
	 * @param sizes
	 * @param operators
	 * @return
	 */
	private static int calculateLength(List<Integer> sizes, List<Character> operators) {
		int length = 0;
		for (int i = 0; i < sizes.size(); i++) {
			if (operators.get(i) != 'I' && operators.get(i) != 'H') {
				length += sizes.get(i);
			}
		}
		return length;
	}

	private static int calculateAlignmentLength(List<Integer> sizes, List<Character> operators) {
		int length = 0;
		for (int i = 0; i < sizes.size(); i++) {
			if (operators.get(i) != 'S' && operators.get(i) != 'H') {
				length += sizes.get(i);
			}
		}
		return length;
	}

	/**
	 * Parse lengths from mdTag.
	 * 
	 * @param mdTag
	 * @return
	 */
	public static List<Integer> parseLengths(String mdTag) {
		List<Integer> lengths = new ArrayList<Integer>();
		Pattern pat = Pattern.compile("\\d+");
		Matcher match = pat.matcher(mdTag);
		while (match.find()) {
			lengths.add(Integer.valueOf(match.group()));
		}
		return lengths;
	}

	/**
	 * Parse mis from mdTag.
	 * 
	 * @param mdTag
	 * @return
	 */
	public static List<String> parseMis(String mdTag) {
		List<String> mis = new ArrayList<String>();
		Pattern pat = Pattern.compile("[\\^ACGT]+");
		Matcher match = pat.matcher(mdTag);
		match.reset();
		while (match.find()) {
			mis.add(match.group());
		}
		return mis;
	}

	/**
	 * Return the length of the needed referecne size. Calculate from cigar
	 * string.
	 * 
	 * @param read
	 * @return
	 */
	public static int getRefLength(AlignmentRecord read) {
		List<Integer> sizes = new ArrayList<Integer>();
		List<Character> operators = new ArrayList<Character>();

		String cigar = read.getCigar();

		sizes = parseSizes(cigar);
		operators = parseOperators(cigar);

		return calculateLength(sizes, operators);
	}

	/**
	 * Return the referecne, read sequence and alignment.
	 * 
	 * @param read
	 * @param ref
	 * @param full 
	 * @return
	 */
	@Deprecated
	public static String getAlignmentSequence(AlignmentRecord read, String ref, boolean full) {
		List<Integer> sizes = new ArrayList<Integer>();
		List<Character> operators = new ArrayList<Character>();
		List<Integer> lengths = new ArrayList<Integer>();
		List<String> mis = new ArrayList<String>();
		StringBuilder alignmentString = new StringBuilder();
		StringBuilder refSequence = new StringBuilder();
		StringBuilder readSequence = new StringBuilder();

		String cigar = read.getCigar();

		sizes = parseSizes(cigar);
		operators = parseOperators(cigar);

//		calculateLength(sizes, operators);

		if (read.getOptionalTag("MD") != null) {
			String mdTag = read.getOptionalTag("MD").getValue();

			lengths = parseLengths(mdTag);
			mis = parseMis(mdTag);

			int seqLength = 0;
			for (String s : mis) {
				if (s.startsWith("^")) {
					seqLength += s.length() - 1;
				} else {
					seqLength += s.length();
				}
			}
			for (int i : lengths) {
				seqLength += i;
			}

			int misIndex = 0;
			int lengthsIndex = 0;
			int operatorsIndex = 0;
			int sizesIndex = 0;
			int refIndex = 0;
			int readIndex = 0;

			if (ref.length() == 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < seqLength; i++) {
					sb.append(" ");
				}
				ref = sb.toString();
			}

			for (int i = 0; i < seqLength;) {
				if (operatorsIndex < operators.size()) {
					if (operators.get(operatorsIndex).equals('S')) {
						for (int j = 0; j < sizes.get(sizesIndex); j++) {
							readIndex++;
						}
						sizesIndex++;
						operatorsIndex++;
					}
				}
				if (lengthsIndex < lengths.size()) {
					for (int j = 0; j < lengths.get(lengthsIndex); j++) {
						refSequence.append(ref.charAt(refIndex));
						readSequence.append(read.getSequence().charAt(readIndex));
						alignmentString.append("|");
						i++;
						refIndex++;
						readIndex++;
					}
					lengthsIndex++;
					if (misIndex < mis.size() && mis.get(misIndex).indexOf("^") != -1) {
						operatorsIndex++;
					}
				}
				if (misIndex < mis.size() && mis.get(misIndex).indexOf("^") == -1) {
					for (int j = 0; j < mis.get(misIndex).length(); j++) {
						alignmentString.append(".");
						refSequence.append(ref.charAt(i));
						if (full) {
							readSequence.append(read.getSequence().charAt(readIndex));
						} else {
							readSequence.append('-');
						}
						i++;
						readIndex++;
						refIndex++;
					}
					misIndex++;
				} else if (operatorsIndex < operators.size()) {
					if (operators.get(operatorsIndex).equals('I')) {
						if (full) {
							readSequence.append(mis.get(misIndex).substring(1));
						}
						for (int j = 0; j < mis.get(misIndex).length() - 1; j++) {
							alignmentString.append(" ");
							refSequence.append("-");
//							i++;
							readIndex++;
						}
						misIndex++;
						operatorsIndex++;
					} else if (operators.get(operatorsIndex).equals('D')) {
						for (int j = 0; j < mis.get(misIndex).length() - 1; j++) {
							refSequence.append(ref.charAt(refIndex));
							alignmentString.append(" ");
							readSequence.append("-");
							i++;
							refIndex++;
						}
						misIndex++;
						operatorsIndex++;
					} else {
						i++;
					}
				}
			}

			if (full) {
				return refSequence.toString() + "\n" + alignmentString.toString() + "\n" + readSequence.toString();
			}
			return readSequence.toString();
		}
		return "";
	}

	public static String getAlignment( AlignmentRecord read, String ref) {
		int length = calculateAlignmentLength(parseSizes(read.getCigar()), parseOperators(read.getCigar()));
		String mdTag = "";
		String reference = "";
		if (read.getOptionalTag("MD") != null) {
			mdTag = read.getOptionalTag("MD").getValue();
		}
		if (ref.length() > length) {
			ref = ref.substring(0, length - 1);
		} else if (ref.equals("") || ref == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; i++) {
				sb.append(" ");
			}
			reference = sb.toString();
		} else {
			reference = calculateSequenceFromCigar(read.getCigar(), ref, OperatorType.INSERTION);
		}
		String alignment = alignmentSequence(length, read.getCigar(), mdTag);
		String readSequence = calculateSequenceFromCigar(read.getCigar(), read.getSequence(), OperatorType.SOFTCLIP);
		readSequence = calculateSequenceFromCigar(read.getCigar(), readSequence, OperatorType.DELETION);
		return reference + "\n" + alignment + "\n" + readSequence;
	}

	private static String calculateSequenceFromCigar(String cigar, String sequence, OperatorType operatorType) {
		List<Integer> sizes = parseSizes(cigar);

		Map<Integer, Integer> opPositions = operatorPositions(cigar, operatorType);
		StringBuilder sb = new StringBuilder(sequence);

		for (Integer key : opPositions.keySet()) {
			if (operatorType == OperatorType.SOFTCLIP) {
				if (key > 0) {
					sb.delete(sb.length() - sizes.get(key), sb.length());
				} else {
					sb.delete(key, key + sizes.get(key));
				}
			} else {
				StringBuilder sign = new StringBuilder();
				for (int i = 0; i < sizes.get(key); i++) {
					sign.append("-");
				}
				sb.insert(opPositions.get(key), sign);
			}
		}
		return sb.toString();
	}

	private static String calculateAlignmentSequence(String cigar, String mdTag, String sequence, OperatorType operatorType) {
		Map<Integer, Integer> operatorPositions = null;
		List<Integer> sizes = parseSizes(cigar);
		if (operatorType == OperatorType.SNP) {
			operatorPositions = snpPositions(mdTag);
		} else {
			operatorPositions = operatorPositions(cigar, operatorType);
		}
		StringBuilder sb = new StringBuilder(sequence);

		for (Integer key : operatorPositions.keySet()) {
			if (operatorType == OperatorType.SNP) {
				for (int i = key; i < key + operatorPositions.get(key);) {
					sb.replace(i, i + 1, ".");
					i++;
				}
			} else {
				for (int i = operatorPositions.get(key); i < operatorPositions.get(key) + sizes.get(key);) {
					sb.replace(i, i + 1, " ");
					i++;
				}
			}

		}
		return sb.toString();
	}

	private static String alignmentSequence(int length, String cigar, String mdTag) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append("|");
		}
		String alignment = sb.toString();
		alignment = calculateAlignmentSequence(cigar, mdTag, alignment, OperatorType.DELETION);
		alignment = calculateAlignmentSequence(cigar, mdTag, alignment, OperatorType.INSERTION);
		alignment = calculateAlignmentSequence(cigar, mdTag, alignment, OperatorType.SNP);
		return alignment;
	}

	private static Map<Integer, Integer> operatorPositions(String cigar, OperatorType operatorType) {
		List<Integer> sizes = parseSizes(cigar);
		List<Character> operators = parseOperators(cigar);
		Map<Integer, Integer> positions = new HashMap<Integer, Integer>();
		int indexOf = -1;
		int position = 0;

		while (operators.subList(indexOf + 1, operators.size()).indexOf(operatorType.toChar()) != -1) {
			indexOf += operators.subList(indexOf + 1, operators.size()).indexOf(operatorType.toChar()) + 1;
			for (int j = 0; j < indexOf; j++) {
				position += sizes.get(j);
			}
			if (operators.get(0) == OperatorType.HARDCLIP.toChar()) {
				position -= sizes.get(0);
			}
			positions.put(indexOf, position);
			position = 0;
		}
		return positions;
	}

	private static Map<Integer, Integer> snpPositions(String mdTag) {
		Map<Integer, Integer> positions = new HashMap<Integer, Integer>();
		List<String> mdTagOperators = parseMis(mdTag);
		List<Integer> lengths = parseLengths(mdTag);
		int position = 0;
		for (int i = 0; i < mdTagOperators.size(); i++) {
			position += lengths.get(i);
			if (mdTagOperators.get(i).matches("[A-Z]*")) {
				positions.put(position, mdTagOperators.get(i).length());
				position += mdTagOperators.get(i).length();
			} else {
				position += mdTagOperators.get(i).length() - 1;
			}
		}
		return positions;
	}
	
	/**
	 * //TODO
	 * @param read
	 * @return
	 */
	public static String getSequenceForConsensus(AlignmentRecord read) {
		List<Integer> cigarLength = new ArrayList<Integer>();
		List<Character> cigarOperators = new ArrayList<Character>();
		List<Integer> mdMatches = new ArrayList<Integer>();
		List<String> mdMismatches = new ArrayList<String>();
		StringBuilder readSequence = new StringBuilder(read.getSequence());

		String cigar = read.getCigar();

		cigarLength = parseSizes(cigar);
		cigarOperators = parseOperators(cigar);
		if (cigarOperators.get(0) == 'S') {
			readSequence.delete(0, cigarLength.get(0));
		}
		if (cigarOperators.get(cigarOperators.size() - 1) == 'S') {
			readSequence.delete(cigarLength.get(cigarLength.size() - 1), readSequence.length());
		}

		if (read.getOptionalTag("MD") != null) {
			String mdTag = read.getOptionalTag("MD").getValue();

			mdMatches = parseLengths(mdTag);
			mdMismatches = parseMis(mdTag);

			int mdIndex = 0;
			Iterator<Integer> matchIt = mdMatches.iterator();
			Iterator<String> misIt = mdMismatches.iterator();
			Iterator<Character> cigarOperatorIt = cigarOperators.iterator();

			while (matchIt.hasNext()) {
				int matchLength = matchIt.next();
				if (misIt.hasNext()) {
					mdIndex += matchLength;
					String misMatch = misIt.next();
					if (misMatch.charAt(0) != '^') {
						if (readSequence.substring(mdIndex, mdIndex + misMatch.length()).equals(misMatch)) {
							char[] padding = new char[misMatch.length()];
							Arrays.fill(padding, 'N');
							readSequence.replace(mdIndex, mdIndex + misMatch.length(), new String(padding));
						} else {
							readSequence.replace(mdIndex, mdIndex + misMatch.length(), misMatch);
						}
						mdIndex += misMatch.length();
					} else {
						char operator = ' ';
						while (!(operator == 'I' || operator == 'D')) {
							operator = cigarOperatorIt.next();
						}
						switch (operator) {
							case 'I':
								readSequence.delete(mdIndex, mdIndex + misMatch.length() - 1);
								break;
							case 'D':
								readSequence.insert(mdIndex, misMatch.substring(1));
								mdIndex += misMatch.length() - 1;
								break;
							default:
								throw new AssertionError(operator);
						}
					}
				}
			}
			return readSequence.toString();
		}

		int alignmentIndex = 0;
		for (int i = 0; i < cigarLength.size(); ++i) {
			switch (cigarOperators.get(i)) {
				case 'S': {
					break;
				}
				case 'M':
				case 'N': {
					alignmentIndex += cigarLength.get(i);
					break;
				}
				case 'I': {
					readSequence.delete(alignmentIndex, alignmentIndex + cigarLength.get(i));
					break;
				}
				case 'D': {
					char[] padding = new char[cigarLength.get(i)];
					Arrays.fill(padding, 'N');
					readSequence.insert(alignmentIndex, padding);
					alignmentIndex += cigarLength.get(i);
					break;
				}
				default: {
					throw new AssertionError(cigarOperators.get(i));
				}
			}
		}
		return readSequence.toString();
	}
}
