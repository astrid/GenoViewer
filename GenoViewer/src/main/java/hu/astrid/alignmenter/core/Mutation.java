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

public class Mutation {

	private MutationType mutationType;
	private int startPos;
	private int length;
	private int occurence;
	private int fullCoverage;
	private String mutationSequence;
    private String referenceSequence;

    public Mutation() {

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
	public Mutation(MutationType mutationType, int startPos, int length, int occurence, int fullCoverage, String mutationSequence, String referenceSequence) {
		this.mutationType = mutationType;
		this.startPos = startPos;
		this.length = length;
		this.occurence = occurence;
		this.fullCoverage = fullCoverage;
        this.mutationSequence = mutationSequence;
		this.referenceSequence = referenceSequence;

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
	 * @return vulgar fraction form of the occurence value
	 */
	public String getVerboseCoverage() {
		return (occurence + "/" + fullCoverage);
	}

	/**
	 *
	 * @return the length of mutation
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
	 * @return the mutation type
	 */
	public MutationType getMutationType() {
		return mutationType;
	}

	/**
	 *
	 * @return the reference sequence
	 */
	public String getReferenceSequence() {
		return referenceSequence;
	}

	/**
	 *
	 * @return the start position
	 */
	public int getStartPos() {
		return startPos;
	}

	/**
	 *
	 * @return the occurence value
	 */
	public int getOccurence() {
		return occurence;
	}

	/**
	 *
	 * @return the full coverage
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
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Mutation)) return false;

        Mutation mutation = (Mutation) o;

        if (fullCoverage != mutation.fullCoverage) return false;
        if (length != mutation.length) return false;
        if (occurence != mutation.occurence) return false;
        if (startPos != mutation.startPos) return false;
        if (mutationSequence != null ? !mutationSequence.equals(mutation.mutationSequence) : mutation.mutationSequence != null)
            return false;
        if (mutationType != mutation.mutationType) return false;
        if (referenceSequence != null ? !referenceSequence.equals(mutation.referenceSequence) : mutation.referenceSequence != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = mutationType != null ? mutationType.hashCode() : 0;
        result = prime * result + startPos;
        result = prime * result + length;
        result = prime * result + occurence;
        result = prime * result + fullCoverage;
        result = prime * result + (referenceSequence != null ? referenceSequence.hashCode() : 0);
        result = prime * result + (mutationSequence != null ? mutationSequence.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("mutationType: ").append(mutationType).append(" ");
        sb.append("startPos: ").append(startPos).append(" ");
        sb.append("length: ").append(length).append(" ");
        sb.append("occurence: ").append(occurence).append(" ");
        sb.append("fullCoverage: ").append(fullCoverage).append(" ");
        sb.append("referenceSequence: ").append(referenceSequence).append(" ");
        sb.append("mutationSequence: ").append(mutationSequence).append(" ");

        return sb.toString();
    }
}
