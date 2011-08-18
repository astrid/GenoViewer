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

import hu.astrid.mapping.model.AlignmentRecord;

/**
 *
 * @author onagy
 */
public class MutationRegion implements DeviantSequence<AlignmentRecord>, Comparable<MutationRegion> {

	private final int startPos;
	private final int length;
	private final MutationType mutationType;
	private final String mutationSequence;

	/**
	 *
	 * @param startPos
	 * @param length
	 * @param mutationType
	 * @param mutationSequence
	 */
	public MutationRegion(int startPos, int length, MutationType mutationType, String mutationSequence) {
		this.startPos = startPos;
		this.length = length;
		this.mutationType = mutationType;
		this.mutationSequence = mutationSequence;
	}

	/**
	 *
	 * @return
	 */
	public int getLength() {
		return length;
	}

	/**
	 *
	 * @return
	 */
	public MutationType getMutationType() {
		return mutationType;
	}

	/**
	 *
	 * @return
	 */
	public int getStartPos() {
		return startPos;
	}

	/**
	 *
	 * @return
	 */
	public AlignmentRecord getOwnerRecord() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String toString() {
		return (mutationType + "@" + startPos + ", length:" + length+" "+mutationSequence);
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof MutationRegion)) {
			return false;
		} else {
			MutationRegion mutationRegion = (MutationRegion) obj;

			if ((mutationRegion.mutationType != this.mutationType) || (mutationRegion.startPos != this.startPos)
					|| (mutationRegion.length != this.length) || !(mutationRegion.mutationSequence.equals(this.mutationSequence))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 19 * hash + this.startPos;
		hash = 19 * hash + this.length;
		hash = 19 * hash + (this.mutationType != null ? this.mutationType.hashCode() : 0);
		hash = 19 * hash + (this.mutationSequence != null ? this.mutationSequence.hashCode() : 0);
		return hash;
	}

	/**
	 *
	 * @return
	 */
	public String getSequence() {
		return mutationSequence;
	}

	public int compareTo(MutationRegion mutationRegion) {

		if (this.startPos > mutationRegion.startPos) {
			return 1;
		} else if (this.startPos < mutationRegion.startPos) {
			return -1;
		} else {
			if (this.length > mutationRegion.length) {
				return 1;
			} else if (this.length < mutationRegion.length) {
				return -1;
			}
			return 0;
		}
	}
}
