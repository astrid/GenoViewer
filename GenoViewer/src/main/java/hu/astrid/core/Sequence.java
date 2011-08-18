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

package hu.astrid.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Astrid Research Inc.
 * Author: Gór Balázs
 * Created: 2010.04.14., 8:47:41
 * Class: hu.astrid.core.Sequence
 */
public class Sequence<T extends GenomeLetter> {

	public static final int MAX_LENGTH_OF_SEQUENCE = (Integer.SIZE / 2);
	
	private List<T> sequence;

	public Sequence() {
		sequence = new ArrayList<T>();
	}

	public Sequence(List<T> sequence) {
		this.sequence = sequence;
	}

	public boolean containsNonconcreteLetter() {
		boolean containsNonConcreteLetter = false;
		for (T item : sequence) {
			if (!item.isConcrete()) {
				containsNonConcreteLetter = true;
				break;
			}
		}
		return containsNonConcreteLetter;
	}

	public List<T> getSequence() {
		return sequence;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (sequence.size() > MAX_LENGTH_OF_SEQUENCE) {
			hash = 5;
			hash = 29 * hash + (this.sequence.hashCode());
		} else {
			for (T letter : sequence) {
				int i = letter.byteValue();
				hash <<= 2;
				hash |= i;
			}
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Sequence<T> other = (Sequence<T>) obj;
		if (this.sequence != other.sequence && (this.sequence == null || !this.sequence.equals(other.sequence))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(sequence.size());
		for (GenomeLetter genomeLetter : sequence) {
			builder.append(genomeLetter.charValue());
		}
		return builder.toString();
	}
}
