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
 * Represents a virtual file offset for BAM file indexing. Each virtual file
 * offset contains two parts: a 48-bit block offset which directs to a BGZF
 * block in the COMPRESSED BAM file and a 16-bit data offset which directs to
 * the beginning of an alignment record in the UNCOMPRESSED content of the block
 * given by the block offset.
 */
public class VirtualFileOffset implements Comparable<VirtualFileOffset> {

	/**
	 * Pattern for the block offset.
	 */
	private static final long BLOCK_OFFSET_PATTERN = 0xFFFFFFFFFFFFL;

	/**
	 * Pattern for the data offset.
	 */
	private static final long DATA_OFFSET_PATTERN = 0xFFFFL;

	/**
	 * The unsigned 48-bit block offset.
	 */
	private final long blockOffset;

	/**
	 * The unsigned 16-bit data offset.
	 */
	private final int dataOffset;

	/**
	 * Creates a virtual file offset from a long value.
	 * 
	 * @param offset
	 *            the two offset packed into a single 64-bit long value (the
	 *            upper 48 bit is the block offset)
	 */
	public VirtualFileOffset(long offset) {
		dataOffset = (int) (offset & DATA_OFFSET_PATTERN);
		blockOffset = ((offset >>> 16) & BLOCK_OFFSET_PATTERN);
	}
	
	/**
	 * @return the packed <code>long</code> representation of the offset
	 */
	public long longRepresentation() {
		return blockOffset << 16 | dataOffset;
	}

	/**
	 * @return the block offset
	 */
	public long getBlockOffset() {
		return blockOffset;
	}

	/**
	 * @return the data offset
	 */
	public int getDataOffset() {
		return dataOffset;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof VirtualFileOffset)) {
			return false;
		}
		
		VirtualFileOffset offset = (VirtualFileOffset) obj;
		if (this.getBlockOffset() == offset.getBlockOffset()
				&& this.getDataOffset() == offset.getDataOffset()) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
	    int hash = 31 + (int) blockOffset;
	    hash = hash * 31 + dataOffset;
	    return hash;
	}

	@Override
	public String toString() {
		return blockOffset + ":" + dataOffset;
	}

	@Override
	public int compareTo(VirtualFileOffset o) {
		if (this.blockOffset < o.getBlockOffset()) {
			return -1;
		}
		if (this.blockOffset > o.getBlockOffset()) {
			return 1;
		}
		if (o.dataOffset < o.getDataOffset()) {
			return -1;
		}
		if (o.dataOffset > o.getDataOffset()) {
			return 1;
		}
		return 0;
	}

}
