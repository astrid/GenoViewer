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

package hu.astrid.alignmenter.core;

public class AlignmentMutation implements Comparable<AlignmentMutation> {
	String consensusName;
	Mutation mutation;

	public AlignmentMutation(String consensusName, Mutation mutation) {
		this.consensusName = consensusName;
		this.mutation = mutation;
	}

	public String getConsensusName() {
		return consensusName;
	}

	public Mutation getMutation() {
		return mutation;
	}

	@Override
	public String toString() {
		return consensusName + " " + this.getMutation().toString();
	}

	@Override
	public int compareTo(AlignmentMutation alignmentMutation) {
		if (this.getMutation().getMutationType().compareTo(alignmentMutation.getMutation().getMutationType()) < 0) {
			return -1;
		} else if (this.getMutation().getMutationType().compareTo(alignmentMutation.getMutation().getMutationType()) > 0) {
			return 1;
		} else {
			if (this.getMutation().getStartPos() < alignmentMutation.getMutation().getStartPos()) {
				return -1;
			} else if (this.getMutation().getStartPos() > alignmentMutation.getMutation().getStartPos()) {
				return 1;
			}
		}
		return 0;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlignmentMutation that = (AlignmentMutation) o;

        if (consensusName != null ? !consensusName.equals(that.consensusName) : that.consensusName != null)
            return false;
        if (mutation != null ? !mutation.equals(that.mutation) : that.mutation != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = consensusName != null ? consensusName.hashCode() : 0;
        result = 31 * result + (mutation != null ? mutation.hashCode() : 0);
        return result;
    }
}
