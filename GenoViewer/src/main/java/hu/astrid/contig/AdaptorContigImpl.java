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

import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;

import java.util.List;

public class AdaptorContigImpl implements AdaptorContig {
	
	private Contig<Color> contig;
	
	private Nucleotide adaptor;

	@Override
	public Nucleotide getAdaptor() {
		return adaptor;
	}

	@Override
	public Color get(int pos) {
		return contig.get(pos);
	}

	@Override
	public String getId() {
		return contig.getId();
	}
	
	@Override
	public void setId(String id) {
		contig.setId(id);	
	}

	@Override
	public List<Color> getSequence() {
		return contig.getSequence();
	}

	@Override
	public List<Color> getSequence(int pos, int seqLength) {
		return contig.getSequence(pos, seqLength);
	}

	@Override
	public int size() {
		return contig.size();
	}
	
	@Override
	public boolean containsNonconcreteLetter(int start, int length) {
		return contig.containsNonconcreteLetter(start, length);
	}
	
	@Override
	public boolean containsNonconcreteLetter() {
		return contig.containsNonconcreteLetter();
	}
	
	@Override
	public void put(Color letter) {
		this.contig.put(letter);
	}

	@Override
	public Contig<Color> getContig() {
		return contig;
	}

	@Override
	public void setContig(Contig<Color> contig) {
		this.contig = contig;
	}

	@Override
	public void setAdaptor(Nucleotide adaptor) {
		this.adaptor = adaptor;
	}

}
