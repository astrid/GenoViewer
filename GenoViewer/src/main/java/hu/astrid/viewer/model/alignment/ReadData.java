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
package hu.astrid.viewer.model.alignment;

import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.OptionalTag;
import hu.astrid.viewer.util.Alignment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 * //TODO komment mindenhez
 * @author OTTO
 */
public class ReadData {

	private TreeSet<InDel> insertions;
	private TreeSet<InDel> delitions;
	private TreeSet<ReadError> readErrors;
	private TreeSet<PositionMutation> snpList;
	private String sequence;
	private String colorSequence;
	private boolean reverse;
	private int position;
	private boolean specific = true;
	private Logger logger = Logger.getLogger(ReadData.class);
	private final int coveredLengthWithInsertionExcluded;

	public static class PositionMutation {

		public int position;

		public PositionMutation(int position) {
			this.position = position;
		}

		@Override
		public String toString() {
			return position + "";
		}

		public int getPosition() {
			return position;
		}
	}
	protected static Comparator<PositionMutation> comparator = new Comparator<PositionMutation>() {

		@Override
		public int compare(PositionMutation pm1, PositionMutation pm2) {
			if (pm1.position == pm2.position) {
				return 0;

			}
			return pm1.position < pm2.position ? -1 : 1;
		}
	};

	public static class ReadError extends PositionMutation {

		int corruptedColor;

		public ReadError(String XRTagValue) {
			super(new Integer(XRTagValue.substring(0, XRTagValue.indexOf('-'))));
			int length = XRTagValue.length();
			corruptedColor = new Integer(XRTagValue.substring(XRTagValue.indexOf('-') + 1, length));
		}

		@Override
		public String toString() {
			return position + " " + corruptedColor;
		}
	}

	public static class InDel extends PositionMutation {

		public final int length;

		public InDel(int position, int length) {
			super(position);
			this.length = length;
		}

		@Override
		public String toString() {
			return position + " " + length;
		}
	}

	public ReadData(AlignmentRecord read) {
		sequence = read.getSequence();
		try {
			colorSequence = read.getOptionalTag("CS").getValue().substring(1);
		} catch (NullPointerException ex) {
			colorSequence = null;
		}
		findReadErrors(read);
		sequence = parseSequence(read, sequence, false);
		if (colorSequence != null) {
			colorSequence = parseSequence(read, colorSequence, true);
		}
		findSnps(read);
		position = read.getPosition();
		reverse = (read.getFlag() & 0x10) > 0;

		OptionalTag XPTag = read.getOptionalTag("XP");
		if (XPTag != null) {
			switch (XPTag.getValue().charAt(0)) {
				case 'T':
					specific = false;
					break;
				default:
					throw new AssertionError();
			}
		}

		this.coveredLengthWithInsertionExcluded = read.getCoveredLength(true);
	}

	@Override
	public String toString() {
		return sequence + " " + colorSequence;
	}

	private synchronized String parseSequence(AlignmentRecord record, String sequence, boolean colorMode) {
		insertions = new TreeSet<InDel>(comparator);
		delitions = new TreeSet<InDel>(comparator);
		List<Character> operators = Alignment.parseOperators(record.getCigar());
		List<Integer> sizes = Alignment.parseSizes(record.getCigar());
		int index = 0;
		int length = sequence.length();
		StringBuilder sb = new StringBuilder(length);
		boolean delitionOccoured = false;
		int clipSize = 0;
		for (int i = 0; i < operators.size(); ++i) {
			int size = sizes.get(i);
			if (delitionOccoured) {
				size--;
				delitionOccoured = false;
			}
			switch (operators.get(i)) {
				case 'I': {
					int diff = 0;
					for (InDel indel : delitions) {
						diff += indel.length;
					}
					for (InDel indel : insertions) {
						diff -= indel.length;
					}
					insertions.add(new InDel(index + diff - clipSize, sizes.get(i)));
					if (colorMode) {
						for (ReadError error : readErrors.tailSet(new ReadError(index - clipSize + "-0"))) {
							if (error.position < index - clipSize + size) {
								readErrors.remove(error);
							}
							error.position -= size;
						}
					}
					index += size;
					break;
				}
				case 'D': {
					int diff = 0;
					for (InDel indel : delitions) {
						diff += indel.length;
					}
					for (InDel indel : insertions) {
						diff -= indel.length;
					}
					delitions.add(new InDel(index + diff - clipSize, sizes.get(i)));
					for (int j = 0; j < size; ++j) {
						sb.append('-');
					}
					if (colorMode) {
						sb.append("-");
						index++;
						delitionOccoured = true;
						for (ReadError error : readErrors.tailSet(new ReadError(index - clipSize + "-0"))) {
							error.position += size;
						}
					}
					break;
				}
				case 'M':
				case 'N': {
					sb.append(sequence.substring(index, index + size));
					index += size;
					break;
				}
				case 'S': {
					if (colorMode) {
						for (ReadError error : readErrors.tailSet(new ReadError(index + "-0"))) {
							error.position -= size;
						}
					}
					clipSize += size;
					index += size;
					break;
				}
				case 'H': {
					break;
				}
				default: {
					sb.append(sequence.substring(index - clipSize, index - clipSize + size));
					index += size;
				}
			}
		}

//	System.out.println("sb: "+sb.toString());
		return sb.toString();
	}

	private void findSnps(AlignmentRecord read) {
		snpList = new TreeSet<PositionMutation>(comparator);
		String MDTag = null;
		try {
			MDTag = read.getOptionalTag("MD").getValue();
		} catch (NullPointerException ex) {
			return;
		}

		ArrayList<Integer> positions = (ArrayList<Integer>) Alignment.parseLengths(MDTag);
		ArrayList<String> ops = (ArrayList<String>) Alignment.parseMis(MDTag);

		int actualPosition = 0, diff = 0;
		for (int i = 0; i < Math.max(positions.size(), ops.size()); i++) {
			if (i < positions.size()) {
				actualPosition += positions.get(i);
			}

			if (i < ops.size()) {

				if (ops.get(i) != null && ops.get(i).charAt(0) != '^') {
					for (int j = 0; j < ops.get(i).length(); ++j) {
						snpList.add(new PositionMutation(actualPosition + diff));
						actualPosition++;
					}
				}
				if (ops.get(i) != null && ops.get(i).charAt(0) == '^') {
					SortedSet<InDel> subSet;
					InDel current = new InDel(actualPosition + diff, 0);
					subSet = insertions.tailSet(current);
					if (!subSet.isEmpty() && subSet.first().position == current.position) {
						diff -= ops.get(i).length() - 1;
					}
					subSet = delitions.tailSet(current);
					if (!subSet.isEmpty() && subSet.first().position == current.position) {
						//Empty
					}
					actualPosition += ops.get(i).length() - 1;
				}
			}
		}
	}

	private void findReadErrors(AlignmentRecord record) {
		readErrors = new TreeSet<ReadError>(comparator);
		OptionalTag XRTag = record.getOptionalTag("XR");
		if (XRTag != null) {
			String[] errorTags = XRTag.getValue().split(";");
			for (String s : errorTags) {
				ReadError readError = new ReadError(s);
				if (readError.position > this.sequence.length()) {
					Logger.getLogger(ReadData.class).warn("position invalid in " + readError);
				} else {
					readErrors.add(readError);
				}
			}
		}
	}

	public TreeSet<InDel> getDelitions() {
		return delitions;
	}

	public TreeSet<InDel> getInsertions() {
		return insertions;
	}

	public TreeSet<ReadError> getReadErrors() {
		return readErrors;
	}

	public String getSequence() {
		return sequence;
	}

	public TreeSet<PositionMutation> getSnpList() {
		return snpList;
	}

	public String getColorSequence() {
		return colorSequence;
	}

	public int getPosition() {
		return position;
	}

	public boolean isReverse() {
		return reverse;
	}

	public boolean isSpecific() {
		return specific;
	}

	public int getCoveredLength() {
		return coveredLengthWithInsertionExcluded;
	}
}
