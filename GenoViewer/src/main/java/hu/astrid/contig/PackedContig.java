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

/**
 * Astrid Research
 * Author: Attila
 * Created: 2009.12.21.
 */
@Deprecated
public class PackedContig<T extends GenomeLetter> extends AbstractCoderContig<T> {

    private final int maxNucBit = (Long.SIZE >> 1);

    private final int typeRatio = 5;

    private byte lastPackSize;//number of nucleotide in the last long pack

    private int storeCapacity = 1000;//default size of store 1000

    private int currentStoreIndex;//current index of long pack in the store

    private long[] store;//long container

    public PackedContig(Coder<T> coder) {
    	super(coder);
    	this.store = new long[this.storeCapacity];
    }

	public PackedContig(Coder<T> coder, int storeCapacity) {
		super(coder);
		this.storeCapacity = storeCapacity;
        this.store = new long[this.storeCapacity];
    }

    /**Index start to zero.
     */
	private int getPackIndex(int pos) {
        if( ++pos < 1 )
            throw new IllegalArgumentException(
                    "Wrong position of nucleotid! " + pos
                    );
        
        int posOffset = pos >>> typeRatio;//for limit of seqPack
        return (pos == (this.maxNucBit * posOffset)) ? --posOffset : posOffset;
    }

    /**Index start to zero.
     */
	private int getSeqBitIndex(int pos, int packIndex) {
        if( pos < 0 )
            throw new IllegalArgumentException(
                    "Wrong position of nucleotid! " + pos
                    );
        
        return (pos - (packIndex << typeRatio));//position in slice of sequence
    }

    /**
     * @param packSize size of pack from store
     * @param seqBitPos position of in the pack
     *
     * @return offset, if packSize bigger than seqBitPos then
     * return minus value
     */
	private int getOffset(int packSize, int seqBitPos) {
        int result = ( packSize - seqBitPos );
        return ( result < 0 ) ? result : ( result << 1 );
    }

	private ArrayList<T> unpack(long value, int size, int offset) {
		ArrayList<T> result = new ArrayList<T>();
		long mask = 3l;
		value >>>= offset;

		for (int i = 0; i < size; i++) {
			int index = (int) (value & mask);
			result.add(0, this.decode((byte) index));

			value >>>= 2;
		}

		return result;
	}

    /**Return a slice of pack seqLength long Nucleotide list.
     *
     * Index start to zero.
     *
     * @param pos start nuc index
     * @param seqLength length of slice of sequence
     *
     * @return a slice of pack seqLength long Nucleotide list
     */
	private List<T> getSeqFromPack(int pos, int seqLength) {
        List<T> result = new ArrayList<T>();
        int packIndex = getPackIndex( pos );
        pos = getSeqBitIndex( pos, packIndex );
        int packSize = (packIndex == this.currentStoreIndex) ? this.lastPackSize : this.maxNucBit;
        int offset = getOffset( packSize, pos + seqLength );

        if( offset < 0 ) {
            seqLength -= ( ( offset < 0 ) ? -offset : offset );
            offset = 0;
        }

        if( seqLength > 0 ) {
            result =  unpack( this.store[packIndex], seqLength, offset );
        }

        return result;
    }

    /**Index start to zero.
     */
    @Override
    public T get(int pos) {
        int indexOffset;
        int seqPackIndex = getPackIndex( pos );
        int seqBitIndex = getSeqBitIndex( pos , seqPackIndex );

        if( ( seqPackIndex == this.currentStoreIndex ) && ( seqBitIndex <= this.lastPackSize ) ) {
            indexOffset = getOffset( this.lastPackSize, seqBitIndex+1 );
        } else {
            indexOffset = getOffset( this.maxNucBit, seqBitIndex+1 );
        }

        int code = (int) ( ( this.store[seqPackIndex] >>> indexOffset ) & 3 );

        return this.decode((byte) code);
    }

    @Override
	public void put(T letter) {
        if( this.lastPackSize == this.maxNucBit ) {
            this.lastPackSize = 0;

            if( ++this.currentStoreIndex == this.storeCapacity ) {
                this.storeCapacity += (this.storeCapacity >> 1);//the capacity of store increment
                long[] tmpStore = new long[storeCapacity];
                System.arraycopy( this.store, 0, tmpStore, 0, this.store.length );
                this.store = tmpStore;
            }
        }

        int code = this.encode(letter);
        
        this.store[this.currentStoreIndex] <<= 2;
        this.store[this.currentStoreIndex] |= code;
        this.lastPackSize++;
    }

    @Override
    public List<T> getSequence() {
        List<T> result = new ArrayList<T>();

		if (this.currentStoreIndex == 0)
            result.addAll( unpack( this.store[0], this.lastPackSize, 0 ) );

		for (int i = 0; i < this.currentStoreIndex; i++)
			result.addAll(unpack(this.store[i], this.maxNucBit, 0));

		if (this.currentStoreIndex > 0)
            result.addAll( unpack( this.store[this.currentStoreIndex], this.lastPackSize, 0 ) );

        return result;
    }

    /**Index start to zero.
     */
    @Override
	public List<T> getSequence(int pos, int seqLength) {
        List<T> result = new ArrayList<T>();
        int packIndex = getPackIndex( pos );

        for( ; packIndex < this.currentStoreIndex; packIndex++ ) {
            List<T> nucList =  getSeqFromPack( pos, seqLength );
            result.addAll( nucList );
            int listSize = nucList.size();
            seqLength -= listSize;
            pos += listSize;
        }

        //last package
        if( (packIndex == this.currentStoreIndex) && (seqLength > 0) ) {
            seqLength = ( (seqLength > this.lastPackSize) ) ?  this.lastPackSize : seqLength;
            result.addAll( getSeqFromPack( pos, seqLength ) );
        }

        return result;
    }

    @Override
    public int size() {
        return ( this.currentStoreIndex * this.maxNucBit ) + this.lastPackSize;
    }

}
