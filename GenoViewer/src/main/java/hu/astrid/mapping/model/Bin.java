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
 * Represents a bin for BAM indices.
 */
public class Bin implements Comparable<Bin> {

	/**
	 * The ID of the bin which refers to the genomic interval covered by the
	 * bin.
	 */
	private int id;

	/**
	 * The list of alignment chunks which belong to the bin.
	 */
	private List<Chunk> chunks = new ArrayList<Chunk>();

	/**
	 * Creates an empty bin with the given id.
	 * 
	 * @param id
	 *            the ID of the bin to be created
	 */
	public Bin(int id) {
		this.id = id;
	}

	/**
	 * @return the ID of the bin
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the list of chunks belonging to the bin
	 */
	public List<Chunk> getChunks() {
		return this.chunks;
	}

	/**
	 * Adds a chunk to the bin.
	 * 
	 * @param chunk
	 *            the chunk to be added
	 */
	public void addChunk(Chunk chunk) {
		this.chunks.add(chunk);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Bin)) {
			return false;
		}
		Bin bin = (Bin) obj;
		if (this.getId() == bin.getId()
				&& this.getChunks().equals(bin.getChunks())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return chunks.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Bin: " + id);
		for (Chunk chunk : chunks) {
			result.append("\n" + chunk);
		}
		return result.toString();
	}
	
	@Override
	public int compareTo(Bin bin) {
		if (this.id == bin.id) {
			return 0;
		} else if (this.id < bin.id) {
			return -1;
		}
		return 1;
	}

}
