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

package hu.astrid.mapping.io;

/**
 * Represents a BGZF compressed block which can be uncompressed.
 */
public class BgzfBlock {

	/**
	 * Bytes of the compressed data.
	 */
	private byte[] compressedData;

	/**
	 * The deflated size of the data.
	 */
	private int deflatedSize;

	/**
	 * The size of the data before compression e. g. the expected uncompressed
	 * size.
	 */
	private int inputSize;

	/**
	 * Creates a new BGZF compressed block instance.
	 * 
	 * @param compressedData
	 *            byte array of the compressed data
	 * @param deflatedSize
	 *            the deflated size of the data
	 * @param inputSize
	 *            the expected uncompressed size
	 */
	public BgzfBlock(byte[] compressedData, int deflatedSize, int inputSize) {
		super();
		this.compressedData = compressedData;
		this.deflatedSize = deflatedSize;
		this.inputSize = inputSize;
	}

	/**
	 * @return the compressed data
	 */
	public byte[] getCompressedData() {
		return compressedData;
	}

	/**
	 * 
	 * @param compressedData
	 *            the compressed data to be set
	 */
	public void setCompressedData(byte[] compressedData) {
		this.compressedData = compressedData;
	}

	/**
	 * @return the deflated size of the data
	 */
	public int getDeflatedSize() {
		return deflatedSize;
	}

	/**
	 * @param deflatedSize
	 *            the deflated size of the data
	 */
	public void setDeflatedSize(int deflatedSize) {
		this.deflatedSize = deflatedSize;
	}
	
	/**
	 * @return the expected uncompressed size
	 */
	public int getInputSize() {
		return inputSize;
	}

	/**
	 * @param inputSize the expected uncompressed size
	 */
	public void setInputSize(int inputSize) {
		this.inputSize = inputSize;
	}

}
