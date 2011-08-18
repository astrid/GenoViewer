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

import hu.astrid.mapping.util.BamUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Instances of this class represent indices built for a BAM file.
 */
public class BamIndex {

	/**
	 * The list of indices built for specific reference sequences.
	 */
	private List<ReferenceIndex> referenceIndices = new ArrayList<ReferenceIndex>();

	/**
	 * Creates an empty instance.
	 */
	public BamIndex() {

	}
	
	public List<Chunk> getChunks(int referenceIndex, int start, int end) {
    	List<Chunk> chunks = new ArrayList<Chunk>();
        List<Integer> binIds = BamUtil.regionToBins(start, end);
        
        for (Integer binId : binIds) {
        	List<Chunk> chunkList = this.getChunks(referenceIndex, binId);
            if (chunkList != null) {
                chunks.addAll(chunkList);
            }
        }
        
        Collections.sort(chunks);
        return chunks;
    }

	/**
	 * Returns the chunks of a given reference sequence, for given bins.
	 * 
	 * @param index
	 *            the index of the reference sequence
	 * @param binIds
	 *            the IDs of the bins
	 * @return the list of chunks which belong to any of the bins
	 */
	public List<Chunk> getChunks(int index, List<Integer> binIds) {
		return referenceIndices.get(index).getChunks(binIds);
	}

	/**
	 * Returns the chunks of a given reference sequence, for a given bin.
	 * 
	 * @param index
	 *            the index of the reference sequence
	 * @param binId
	 *            the ID of the bin
	 * @return the list of chunks which belong the bin
	 */
	public List<Chunk> getChunks(int index, int binId) {
		return referenceIndices.get(index).getChunks(binId);
	}

	/**
	 * Returns the linear index for a given position of a given reference
	 * sequence.
	 * 
	 * @param index
	 *            the index of the reference sequence
	 * @param position
	 *            the position
	 * @return the linear offset
	 */
	public VirtualFileOffset linearIndexOf(int index, int position) {
		if (referenceIndices.get(index).getLinearIndices() == null
				|| referenceIndices.get(index).getLinearIndices().isEmpty()) {
			return null;
		}
		try {
			return referenceIndices.get(index).getLinearIndices().get(
				position >>> 14);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @return reference indexes
	 */
	public List<ReferenceIndex> getReferenceIndices() {
		return this.referenceIndices;
	}

	/**
	 * @param referenceIndex
	 *            a reference index
	 */
	public void addReferenceIndex(ReferenceIndex referenceIndex) {
		this.referenceIndices.add(referenceIndex);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof BamIndex)) {
			return false;
		}
		
		BamIndex bamIndex = (BamIndex) obj;
		if (this.getReferenceIndices().equals(bamIndex.getReferenceIndices())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return referenceIndices.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(BamUtil.BAI_MAGIC_STRING);
		for (ReferenceIndex refI : referenceIndices) {
			result.append("\n");
			result.append(refI);
		}
		return result.toString();
	}

}
