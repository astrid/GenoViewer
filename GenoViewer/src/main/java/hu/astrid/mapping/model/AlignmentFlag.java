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

/**
 * This enumeration contains the flags of the alignment record's bitwise flag.
 */
public enum AlignmentFlag {

	/**
	 * This flag means that the read was paired in sequencing.
	 */
	PAIRED((short) 0x0001),

	/**
	 * This flag means that the read was mapped in a proper pair.
	 */
	MAPPED_PAIR_MEMBER((short) 0x0002),

	/**
	 * This flag means that the query sequence itself is unmapped.
	 */
	UNMAPPED((short) 0x0004),

	/**
	 * This flag means that the mate is unmapped.
	 */
	MATE_UNMAPPED((short) 0x0008),

	/**
	 * This flag means that the query was mapped to the reverse strand.
	 */
	REVERSE_STRAND((short) 0x0010),

	/**
	 * This flag means that the mate was mapped to the reverse strand.
	 */
	MATE_REVERSE_STRAND((short) 0x0020),

	/**
	 * This flag means that the read is the first read in the pair.
	 */
	FIRST_READ_OF_PAIR((short) 0x0040),

	/**
	 * This flag means that the read is the second read in the pair.
	 */
	SECOND_READ_OF_PAIR((short) 0x0080),

	/**
	 * This flag means that the alignment is not primary.
	 */
	NOT_PRIMARY_ALINMENT((short) 0x0100),

	/**
	 * This flag means that the read fails platform or vendor quality checks.
	 */
	FAILS_QUALITY_CHECK((short) 0x0200),

	/**
	 * This flag means that the read is either a PCR duplicate or an optical
	 * duplicate.
	 */
	PCR_OR_OPTICAL_DUPLICATE((short) 0x0400);

	/**
	 * The value of the flag.
	 */
	private short value;

	/**
	 * Creates a flag for alignment records.
	 * 
	 * @param value
	 *            the value of the flag
	 */
	AlignmentFlag(short value) {
		this.value = value;
	}

	/**
	 * @return the value of the flag
	 */
	public short getValue() {
		return value;
	}

}
