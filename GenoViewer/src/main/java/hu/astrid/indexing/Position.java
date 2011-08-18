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

package hu.astrid.indexing;

import java.util.Arrays;

public class Position {

	private short id;
	private int [] positions;
	private int size = 0;
	private int current = 0;

	public Position(short id) {
		super();
		this.id = id;
	}
	
	public void incSize() {
		this.size++;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public void putPosition(int position) {
		if (current == 0) {
			positions = new int [size];
		}
		this.positions[this.current++] = position;
	}

	public void setPositions(int[] positions) {
		this.positions = positions;
		current += positions.length;
	}
	
	public int [] getPositions() {
		return this.positions;
	}

	public short getId() {
		return id;
	}

	@Override
	public String toString() {
		return "[id=" + id + ";positions=" + Arrays.toString(positions) + ";size=" + size + ";current=" + current + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + Arrays.hashCode(positions);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (current != other.current)
			return false;
		if (id != other.id)
			return false;
		if (!Arrays.equals(positions, other.positions))
			return false;
		if (size != other.size)
			return false;
		return true;
	}
	
}
