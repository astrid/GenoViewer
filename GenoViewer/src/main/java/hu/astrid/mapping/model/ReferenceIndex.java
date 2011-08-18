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

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class represents the index structure for a reference
 * sequence.
 */
public class ReferenceIndex {

	/**
	 * The list of bins.
	 */
	private List<Bin> bins = new ArrayList<Bin>();

	/**
	 * The list of linear indices for every 16K tiling window of the reference
	 * sequence.
	 */
	private List<VirtualFileOffset> linearIndices = new ArrayList<VirtualFileOffset>();

	/**
	 * Creates an empty instance.
	 */
	public ReferenceIndex() {

	}

	/**
	 * Returns chunks for given bins.
	 * 
	 * @param binIds
	 *            the IDs of the bins
	 * @return the list of chunks which belong to any of the bins
	 */
	public List<Chunk> getChunks(List<Integer> binIds) {
		List<Chunk> result = new ArrayList<Chunk>();
		for (Bin bin : bins) {
			int binId = bin.getId();
			if (binIds.contains(binId)) {
				result.addAll(bin.getChunks());
			}
		}
		return result;
	}

	/**
	 * Returns chunks for a given bin.
	 * 
	 * @param binId
	 *            the ID of the bin
	 * @return the list of chunks which belong the bin
	 */
	public List<Chunk> getChunks(int binId) {
		for (Bin bin : bins) {
			if (bin.getId() == binId)
				return bin.getChunks();

		}
		return null;
	}
	
	/**
	 * Adds a bin to the index.
	 * 
	 * @param bin
	 *            the bin to be added
	 */
	public void addBin(Bin bin) {
		this.bins.add(bin);
	}
	
	/**
	 * Adds a linear index.
	 * 
	 * @param index
	 *            the linear index to be added
	 */
	public void addLinearIndex(VirtualFileOffset index) {
		this.linearIndices.add(index);
	}

	/**
	 * @return the bins
	 */
	public List<Bin> getBins() {
		return bins;
	}

	/**
	 * @return the linear indices
	 */
	public List<VirtualFileOffset> getLinearIndices() {
		return linearIndices;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ReferenceIndex)) {
			return false;
		}
		
		ReferenceIndex referenceIndex = (ReferenceIndex) obj;
		if (this.getBins().equals(referenceIndex.getBins())
				&& this.getLinearIndices().equals(referenceIndex.getLinearIndices())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return bins.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (Bin bin : bins) {
			result.append(bin + "\n");
		}

		result.append("Linear indices:");
		for (VirtualFileOffset vfo : linearIndices) {
			result.append("\n" + vfo);
		}

		return result.toString();
	}

}
