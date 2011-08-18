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

package hu.astrid.read;

import hu.astrid.contig.Contig;
import hu.astrid.contig.SimpleContig;
import hu.astrid.core.Nucleotide;

import java.util.List;

/** 
 * Astrid Research
 * Author: mkiss
 * Created: Dec 14
 */
public class FastaRead extends AbstractRead<Nucleotide> {
	
	private Contig<Nucleotide> contig;

	public FastaRead(String id, String fastaSequence) {
		this.contig = new SimpleContig<Nucleotide>(); //new NucleotideContigFactory().createContig(ContigImplementationType.SIMPLE);		
		this.contig.setId(id);

		for (int i = 0; i < fastaSequence.length(); i++) {
			this.contig.put(Nucleotide.valueOf(fastaSequence.charAt(i)));
		}
	}

	public FastaRead(String id, List<Nucleotide> sequence) {
		this.contig = new SimpleContig<Nucleotide>(sequence);		
		this.contig.setId(id);
	}

	
	
	@Override
	public String getId() {
		return this.contig.getId();
	}
	
	@Override
	public List<Nucleotide> getSequence() {
		return this.contig.getSequence();
	}
	
	@Override
	public Nucleotide get(int pos) {
		return this.contig.get(pos);
	}
	
	@Override
	public int size() {
		return this.contig.size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Nucleotide n : this.contig.getSequence()) {
			builder.append(n.charValue());
		}
		return builder.toString();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FastaRead fastaRead = (FastaRead) o;

        if (contig != null ? !contig.equals(fastaRead.contig) : fastaRead.contig != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return contig != null ? contig.hashCode() : 0;
    }
}
