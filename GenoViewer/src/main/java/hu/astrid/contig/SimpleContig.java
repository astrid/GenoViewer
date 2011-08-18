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

import hu.astrid.core.GenomeLetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Astrid Research
 * Author: Attila
 * Created: 2009.12.18.
 */
public class SimpleContig<T extends GenomeLetter> extends AbstractContig<T> {

    private List<T> sequence = new ArrayList<T>();
    
    public SimpleContig() { }

    public SimpleContig(List<T> sequence) {
    	super();
    	this.sequence = sequence;
    }    
    
//    public SimpleContig(String id, String sequence) {
//    	super(id);
//    	for (int i = 1; i < sequence.length(); i++) {
//        	this.put(Color.valueOf(sequence.charAt(i)));
//        }
//    }

    /**
     * @param pos a position on the contig
     *
     * @return the genome letter which in pos position is
     */
    @Override
    public T get(int pos) {
        return sequence.get(pos);
    }

	@Override
    public void put(T letter) {
        sequence.add(letter);
    }

    @Override
    public List<T> getSequence() {
        return sequence;
    }

    @Override
    public List<T> getSequence(int pos, int seqLength) {
        return new ArrayList<T>(sequence.subList(pos, pos + seqLength));
    }

    @Override
    public int size() {
        return this.sequence.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleContig that = (SimpleContig) o;

        if (sequence != null ? !sequence.equals(that.sequence) : that.sequence != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sequence != null ? sequence.hashCode() : 0;
    }
}
