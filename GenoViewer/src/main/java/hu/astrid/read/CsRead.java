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

import hu.astrid.contig.AdaptorContig;
import hu.astrid.contig.AdaptorContigImpl;
import hu.astrid.contig.Contig;
import hu.astrid.contig.SimpleContig;
import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;

import java.util.List;

/**
 * Astrid Research
 * Author: balint
 * Created: Dec 11, 2009 3:53:12 PM
 */
public class CsRead extends AbstractRead<Color> implements AdaptorRead {

    private AdaptorContig adaptorContig;
    
    public CsRead(String id, String abiSequence) {
    	this.adaptorContig = new AdaptorContigImpl(); //new AdaptorContigFactory().createContig(ContigImplementationType.SIMPLE);
    	
    	// TODO rewrite
    	Contig<Color> contig = new SimpleContig<Color>();
        contig.setId(id);
        this.adaptorContig.setContig(contig);
        this.adaptorContig.setAdaptor(Nucleotide.valueOf(abiSequence.charAt(0)));

        for (int i = 1; i < abiSequence.length(); i++) {
        	contig.put(Color.valueOf(abiSequence.charAt(i)));
        }
    }

    public CsRead(String id, String abiSequence, int[] qualArray) {
        this(id, abiSequence);
        if (this.adaptorContig.size() != qualArray.length) {
        	throw new IllegalArgumentException("The number of color codes does not match the number of quality values.");
        }
        this.qualityValues = qualArray;
    }

    @Override
    public String getId() {
        return this.adaptorContig.getId();
    }
    
    @Override
    public List<Color> getSequence() {
    	return this.adaptorContig.getSequence();
    }
    
    @Override
    public Color get(int index) {
    	return this.adaptorContig.get(index);
    }
    
    @Override
    public int size() {
    	return this.adaptorContig.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(adaptorContig.getAdaptor());
        appendColorCodes(builder);
        return builder.toString();
    }

    private StringBuilder appendColorCodes(StringBuilder builder) {
        for (Color c : this.adaptorContig.getSequence()) {
            builder.append(c.charValue());
        }
        return builder;
    }
    
    public Nucleotide getFirstNucleotide() {
        return adaptorContig.getAdaptor().translate(this.adaptorContig.get(0));
    }

	@Override
	public Nucleotide getAdaptor() {
		return this.adaptorContig.getAdaptor();
	}
}
