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

import hu.astrid.viewer.model.ViewerConsensusModel;
import java.io.Serializable;

/**
 *
 * @author onagy
 */
public class Mutation implements Serializable {

	private MutationType mutationType;
	private int startPos;
	private int length;
	private int occurence;
	private int fullCoverage;
	private String referenceSequence;
	private String mutationSequence;

	/**
	 *
	 */
	public Mutation() {
	}

	/**
	 *
	 * @param mutationRegion
	 * @param occurence
	 * @param fullCoverage
	 * @param referenceSequence
	 */
	public Mutation(MutationRegion mutationRegion, int occurence, int fullCoverage, String referenceSequence) {
		this.mutationType = mutationRegion.getMutationType();
		this.startPos = mutationRegion.getStartPos();
		this.length = mutationRegion.getLength();
		this.occurence = occurence;
		this.fullCoverage = fullCoverage;
		this.referenceSequence = referenceSequence;
		this.mutationSequence = mutationRegion.getSequence();
	}

	/**
	 *
	 * @param mutationType
	 * @param startPos
	 * @param length
	 * @param occurence
	 * @param fullCoverage
	 * @param referenceSequence
	 * @param mutationSequence
	 */
	public Mutation(MutationType mutationType, int startPos, int length, int occurence, int fullCoverage, String referenceSequence, String mutationSequence) {
		this.mutationType = mutationType;
		this.startPos = startPos;
		this.length = length;
		this.occurence = occurence;
		this.fullCoverage = fullCoverage;
		this.referenceSequence = referenceSequence;
		this.mutationSequence = mutationSequence;
	}

	/**
	 *
	 * @param mutation
	 */
	public Mutation(Mutation mutation) {
		this(mutation.mutationType, mutation.startPos, mutation.length, mutation.occurence, mutation.fullCoverage, new String(mutation.referenceSequence), new String(mutation.mutationSequence));
	}

	/**
	 *
	 * @return
	 */
	public float getCoverage() {
		return (this.occurence / (float) fullCoverage);
	}

	/**
	 *
	 * @return
	 */
	public String getVerboseCoverage() {
		return (occurence + "/" + fullCoverage);
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
	 * @return mutation sequence with adaptors at the beginning and the end exceptc in case of {@link MutationType#DELETION}<br>
	 * this method used for serialization
	 */
	public String getMutationSequence() {
		return mutationSequence;
	}

	/**
	 *
	 * @return mutation sequence without adaptors
	 */
	public String getDisplayedMutationSequence() {
		if (mutationType == MutationType.DELETION) {
			return mutationSequence;

		} else {
			return mutationSequence.substring(1, mutationSequence.length() - 1);
		}
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
	public String getReferenceSequence() {
		return referenceSequence;
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
	public int getOccurence() {
		return occurence;
	}

	/**
	 *
	 * @return
	 */
	public int getFullCoverage() {
		return fullCoverage;
	}

	/**
	 *
	 * @param fullCoverage
	 */
	public void setFullCoverage(int fullCoverage) {
		this.fullCoverage = fullCoverage;
	}

	/**
	 *
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 *
	 * @param mutationSequence
	 */
	public void setMutationSequence(String mutationSequence) {
		this.mutationSequence = mutationSequence;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + (this.mutationType != null ? this.mutationType.hashCode() : 0);
		hash = 23 * hash + this.startPos;
		hash = 23 * hash + this.length;
		hash = 23 * hash + this.occurence;
		hash = 23 * hash + this.fullCoverage;
		hash = 23 * hash + (this.referenceSequence != null ? this.referenceSequence.hashCode() : 0);
		hash = 23 * hash + (this.mutationSequence != null ? this.mutationSequence.hashCode() : 0);
		return hash;
	}

	/**
	 *
	 * @param mutationType
	 */
	public void setMutationType(MutationType mutationType) {
		this.mutationType = mutationType;
	}

	/**
	 *
	 * @param occurence
	 */
	public void setOccurence(int occurence) {
		this.occurence = occurence;
	}

	/**
	 *
	 * @param referenceSequence
	 */
	public void setReferenceSequence(String referenceSequence) {
		this.referenceSequence = referenceSequence;
	}

	/**
	 *
	 * @param startPos
	 */
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	@Override
	public String toString() {
		return mutationType + " position " + startPos + " length " + length + " occured " + occurence + " times from " + fullCoverage + "\n"
				+ "reference: " + referenceSequence + "; mutated sequence: " + mutationSequence;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Mutation)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		Mutation mut = (Mutation) obj;

		return this.mutationType == mut.mutationType && this.startPos == mut.startPos
				&& this.length == mut.length && this.occurence == mut.occurence && this.fullCoverage == mut.fullCoverage
				&& this.referenceSequence == null ? mut.referenceSequence == null : this.referenceSequence.equals(mut.referenceSequence)
				&& this.getDisplayedMutationSequence() == null ? mut.getDisplayedMutationSequence() == null : this.getDisplayedMutationSequence().equals(mut.getDisplayedMutationSequence());
	}

	/**
	 *
	 * @return
	 */
	public String getColorSequence() {
		return ViewerConsensusModel.convertNucleotideSequenceToColor(mutationSequence);
	}
}
