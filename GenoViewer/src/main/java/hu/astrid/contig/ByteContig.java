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

package hu.astrid.contig;

import hu.astrid.core.Coder;
import hu.astrid.core.GenomeLetter;

import java.util.ArrayList;
import java.util.List;

public class ByteContig<T extends GenomeLetter> extends AbstractCoderContig<T> {

	protected int size;

	protected byte[] sequence = new byte[0];

	protected List<byte[]> seqBitList;
	
	public ByteContig(Coder<T> coder) {
		super(coder);
		this.seqBitList = new ArrayList<byte[]>();
	}

	public void concatArray(int lastSize) {
		int offset = 0;
		int maxIndex = (this.seqBitList.size() - 1);

		this.sequence = new byte[this.size];

		for (int i = 0; i < maxIndex; i++) {
			byte[] bArray = this.seqBitList.get(i);
			System.arraycopy(bArray, 0, this.sequence, offset, bArray.length);
			offset += bArray.length;
		}

		byte[] bArray = this.seqBitList.get(maxIndex);
		System.arraycopy(bArray, 0, this.sequence, offset, lastSize);
		this.seqBitList = null;
	}

	public void putByteArray(byte[] array, int size) {
		this.size += size;
        byte[] cloneByteArray = new byte[size];

		System.arraycopy(array, 0, cloneByteArray, 0, size);
		this.seqBitList.add(cloneByteArray);
	}

	@Override
	public T get(int pos) {
		return this.decode(this.sequence[pos]);
	}

	@Override
	public void put(T letter) {
		byte[] tempArray = new byte[size + 1];

		System.arraycopy(this.sequence, 0, tempArray, 0, size);
		tempArray[size] = this.encode(letter);

		this.sequence = tempArray;
		++size;
	}

	@Override
	public List<T> getSequence() {
		List<T> result = new ArrayList<T>(this.size);
		
		for (byte b : this.sequence) {
			result.add(this.decode(b));
		}

		return result;
	}

	@Override
	public List<T> getSequence(int pos, int seqLength) {
		int maxIndex = (pos + seqLength);
		List<T> result = new ArrayList<T>(seqLength);
		
		for (int i = pos; i < maxIndex; i++) {
			result.add(this.decode(this.sequence[i]));
		}

		return result;
	}

	@Override
	public int size() {
		return this.sequence.length;
	}

}