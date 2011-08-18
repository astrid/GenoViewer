
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

package hu.astrid.viewer.model.alignment;

/**
 * Represents an interval width interval start (iclusive) and end (exclusive) position
 * @author Szuni
 */
public class Interval {

	/**Intervals start position (inclusive), started from 0*/
	public final int start;
	/**Intervals end position (exclusive)*/
	public final int end;

	/**
	 * Creates a new instance of interval
	 * @param start position
	 * @param end position
	 * @throws IllegalArgumentException - if interval length less than 1
	 */
	public Interval(int start, int end) {
		if(start >= end) {
			throw new IllegalArgumentException("Negative interval length "+start+"-"+end);
		}
		this.start = start;
		this.end = end;
	}

	/**
	 * @return length of interval
	 */
	public int length() {
		return end-start;
	}

	@Override
	public String toString() {
		return start+"-"+end;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Interval other = (Interval) obj;
		if (this.start != other.start) {
			return false;
		}
		if (this.end != other.end) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 83 * hash + this.start;
		hash = 83 * hash + this.end;
		return hash;
	}

}
