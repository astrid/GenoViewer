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

package hu.astrid.viewer.model.mutation;

/**
 *
 * @author onagy {@link Enum Enum} for discriminate mutations
 */
public enum MutationType{

	/**
	 * 
	 */
	INSERTION('I'),
	/**
	 *
	 */
	DELETION('D'),
	/**
	 * 
	 */
	MNP(null),
	/**
	 *
	 */
	SNP(null);

	private final Character cigarChar;

	private MutationType(Character cigarChar) {
		this.cigarChar = cigarChar;
	}

	/**
	 *
	 * @return the corresponding character in CIGAR notation if it has any, on the other case returns null
	 */
	public Character getCIGARChar() {
		return this.cigarChar;
	}
}
