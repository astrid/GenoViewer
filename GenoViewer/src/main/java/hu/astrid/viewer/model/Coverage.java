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

/**
 *
 * @author onagy
 */
/**
 * Class for store a position's coverage covered by reads
 */
public class Coverage {

	/**Position on alignment reference*/
	private final int absPosition;
	/**Number of reads cover this position*/
	private int coverage;

	/**
	 * Create instance for specified position with specified coverage
	 * @param absPosition
	 * @param coverage
	 */
	public Coverage(int absPosition, int coverage) {

		this.absPosition = absPosition;
		this.coverage = coverage;
	}

	/**
	 *  Create instance for specified position with 0 coverage
	 * @param absPosition
	 */
	public Coverage(int absPosition) {
		this(absPosition, 0);
	}

	/**
	 * Increase coverage value
	 * @return self object
	 */
	public Coverage increaseCoverageByOne() {

		this.coverage++;

		return this;
	}

	/**
	 * @return this coverage objects position on alignment reference
	 */
	public int getAbsPosition() {
		return absPosition;
	}

	/**
	 * @return this coverage objects value on alignment reference at its position
	 */
	public int getCoverage() {
		return coverage;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null || !(obj instanceof Coverage)) {

			return false;
		}

		if (this.absPosition == ((Coverage) obj).absPosition) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 13;
		hash = 41 * hash + this.absPosition;
		hash = 59 * hash + this.coverage;
		return hash;
	}
}


