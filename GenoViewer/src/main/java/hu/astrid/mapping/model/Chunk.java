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

/**
 * Represents a chunk of alignment records in a BAM file.
 */
public class Chunk implements Comparable<Chunk> {

	/**
	 * Virtual file offset to the beginning of the chunk.
	 */
	private VirtualFileOffset startOffset;

	/**
	 * Virtual file offset to the end of the chunk.
	 */
	private VirtualFileOffset endOffset;

	/**
	 * Creates a chunk instance with the given offsets.
	 * 
	 * @param startOffset
	 *            the offset to the beginning of the chunk
	 * @param endOffset
	 *            the offset to the end of the chunk
	 */
	public Chunk(VirtualFileOffset startOffset, VirtualFileOffset endOffset) {
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	/**
	 * @return the offset to the beginning of the chunk
	 */
	public VirtualFileOffset getStartOffset() {
		return startOffset;
	}
	
	/**
	 * @param startOffset the start offset to be set
	 */
	public void setStartOffset(VirtualFileOffset startOffset) {
		this.startOffset = startOffset;
	}

	/**
	 * @return the offset to the end of the chunk
	 */
	public VirtualFileOffset getEndOffset() {
		return endOffset;
	}
	
	/**
	 * @param endOffset the end offset to be set
	 */
	public void setEndOffset(VirtualFileOffset endOffset) {
		this.endOffset = endOffset;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Chunk)) {
			return false;
		}
		Chunk chunk = (Chunk) obj;
		if (this.getStartOffset().equals(chunk.getStartOffset())
				&& this.getEndOffset().equals(chunk.getEndOffset())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 31 + startOffset.hashCode();
		hash = hash * 31 + endOffset.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return startOffset + " - " + endOffset;
	}

	@Override
	public int compareTo(Chunk chunk) {
		return this.startOffset.compareTo(chunk.getStartOffset());
	}

}
