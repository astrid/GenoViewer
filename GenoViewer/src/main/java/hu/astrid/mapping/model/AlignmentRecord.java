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

package hu.astrid.mapping.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class represents an alignment record which can be stored
 * in SAM and BAM files. An alignment record describes how a query sequence was
 * mapped to a reference sequence.
 */
public class AlignmentRecord extends AlignmentPosition {

	/**
	 * The bitwise flag of the alignment. See
	 * {@link hu.astrid.mapping.model.AlignmentFlag}.
	 */
	private short flag;

	/**
	 * The quality of the alignment.
	 */
	private byte mappingQuality;

	/**
	 * The extended CIGAR string of the alignment.
	 */
	private String cigar;

	/**
	 * The reference sequence name of the mate.
	 */
	private String mateReferenceName;

	/**
	 * 1-based leftmost position or coordinate of the clipped mate sequence.
	 */
	private int matePosition;

	/**
	 * The inferred insert size.
	 */
	private int insertSize;

	/**
	 * The query sequence.
	 */
	private String sequence;

	/**
	 * The ACSII-33-based quality values of the query sequence.
	 */
	private String quality;

	/**
	 * The list of optional tags of the alignment record.
	 */
	private List<OptionalTag> optionalTags = new ArrayList<OptionalTag>();

	/**
	 * Creates an empty alignment record.
	 */
	public AlignmentRecord() {
	}

	/**
	 * @return the bitwise flag
	 */
	public short getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the bitwise flag to be set
	 */
	public void setFlag(short flag) {
		this.flag = flag;
	}

	/**
	 * @return the quality of the alignment
	 */
	public byte getMappingQuality() {
		return mappingQuality;
	}

	/**
	 * @param mappingQuality
	 *            the quality of the alignment to be set
	 */
	public void setMappingQuality(byte mappingQuality) {
		this.mappingQuality = mappingQuality;
	}

	/**
	 * @return the cigar string
	 */
	public String getCigar() {
		return cigar;
	}

	/**
	 * @param cigar
	 *            the cigar string to be set
	 */
	public void setCigar(String cigar) {
		this.cigar = cigar;
	}

	/**
	 * @return the reference sequence name of the mate
	 */
	public String getMateReferenceName() {
		return mateReferenceName;
	}

	/**
	 * @param mateReferenceName
	 *            the reference sequence name of the mate to be set
	 */
	public void setMateReferenceName(String mateReferenceName) {
		this.mateReferenceName = mateReferenceName;
	}

	/**
	 * @return the position of the mate
	 */
	public int getMatePosition() {
		return matePosition;
	}

	/**
	 * @param matePosition
	 *            the position of the mate to be set
	 */
	public void setMatePosition(int matePosition) {
		this.matePosition = matePosition;
	}

	/**
	 * @return the insert size
	 */
	public int getInsertSize() {
		return insertSize;
	}

	/**
	 * @param insertSize
	 *            the insert size to be set
	 */
	public void setInsertSize(int insertSize) {
		this.insertSize = insertSize;
	}

	/**
	 * @return the sequence of the query
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence of the query to be set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
		this.readLength = sequence.length();
	}

	/**
	 * @return the quality string of the query
	 */
	public String getQuality() {
		return quality;
	}

	/**
	 * @param quality
	 *            the quality string of the query to be set
	 */
	public void setQuality(String qualality) {
		this.quality = qualality;
	}

	/**
	 * Adds an optional tag to the alignment record.
	 * 
	 * @param optionalTag
	 *            the optional tag to be added to the record
	 */
	public void addOptionalTag(OptionalTag optionalTag) {
		this.optionalTags.add(optionalTag);
	}

	/**
	 * @param tagName
	 *            String.
	 * @return an optional member by the tagName parameter is defined.
	 */
	public OptionalTag getOptionalTag(String tagName) {
		OptionalTag optionalTag = null;

		for (OptionalTag tag : this.optionalTags) {
			if (tag.getTagName().equals(tagName)) {
				optionalTag = tag;
				break;
			}
		}

		return optionalTag;
	}

	/**
	 * @return the list of optional tags of the alignment record.
	 */
	public List<OptionalTag> listOptionalTags() {
		return this.optionalTags;
	}
	
	/**
	 * @return true, if has only matches (perfect alignment).
	 */
	public boolean isPerfectRead() {
		return getCigar().matches("^\\d*M");
	}
	
	@Override
	public int getReadLength() {
		return this.sequence.length();
	}

	/**
	 * Get covered sequence length. Length of read extended by length of delitions,
	 * shortened by length of soft clips and in need length of insertions.
	 * @param instertionExcluded inserion length need to be excluded from covered length
	 * @return covered sequence length
	 */
	public int getCoveredLength(boolean instertionExcluded) {
		int length = getReadLength();
		for(int i=0; i<cigar.length(); ++i) {
			switch(cigar.charAt(i)) {
				case 'I':
					if(instertionExcluded) {
						length-=getCigarNumberBeforePosition(i);
					}
					break;
				case 'S':
					length-=getCigarNumberBeforePosition(i);
					break;
				case 'D':
					length+=getCigarNumberBeforePosition(i);
					break;
			}
		}
		return length;
	}

	/**
	 * Get number of nucleotides covered by cigar operator
	 * @param position position of operator in cigar string
	 * @return number befor specified operator
	 */
	private int getCigarNumberBeforePosition(int position) {
		for(int i=position-1; i>=-1; --i) {
			if(i==-1 || !Character.isDigit(cigar.charAt(i))) {
				return Integer.parseInt(cigar.substring(i+1, position));
			}
		}
		throw new AssertionError("Incorrect cigar string: "+cigar);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(queryName).append('\t').append(flag).append('\t').append(
				referenceName).append('\t').append(position).append('\t')
				.append(mappingQuality & 0xFF).append('\t').append(cigar)
				.append('\t').append(mateReferenceName).append('\t').append(
						matePosition).append('\t').append(insertSize).append(
						'\t').append(sequence).append('\t').append(quality);

		for (OptionalTag optionalTag : this.optionalTags) {
			result.append('\t').append(optionalTag.toString());
		}

		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cigar == null) ? 0 : cigar.hashCode());
		result = prime * result + flag;
		result = prime * result + insertSize;
		result = prime * result + mappingQuality;
		result = prime * result + matePosition;
		result = prime
				* result
				+ ((mateReferenceName == null) ? 0 : mateReferenceName
						.hashCode());
		result = prime * result
				+ ((optionalTags == null) ? 0 : optionalTags.hashCode());
		result = prime * result + ((quality == null) ? 0 : quality.hashCode());
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof AlignmentRecord)) {
			return false;
		}

		AlignmentRecord other = (AlignmentRecord) obj;
		if (!this.getQueryName().equals(other.getQueryName())) {
			return false;
		}
		if (this.getFlag() != other.getFlag()) {
			return false;
		}
		if (!this.getReferenceName().equals(other.getReferenceName())) {
			return false;
		}
		if (this.getPosition() != other.getPosition()) {
			return false;
		}
		if (this.getMappingQuality() != other.getMappingQuality()) {
			return false;
		}
		if (!this.getCigar().equals(other.getCigar())) {
			return false;
		}
		if (!this.getMateReferenceName().equals(other.getMateReferenceName())) {
			return false;
		}
		if (this.getMatePosition() != other.getMatePosition()) {
			return false;
		}
		if (this.getInsertSize() != other.getInsertSize()) {
			return false;
		}
		if (!this.getSequence().equals(other.getSequence())) {
			return false;
		}
		if (!this.getQuality().equals(other.getQuality())) {
			return false;
		}
		if (!this.listOptionalTags().equals(other.listOptionalTags())) {
			return false;
		}

		return true;
	}

}
