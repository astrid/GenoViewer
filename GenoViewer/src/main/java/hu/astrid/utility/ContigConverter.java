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

package hu.astrid.utility;

import java.util.List;

import hu.astrid.contig.Contig;
import hu.astrid.contig.ContigFactory;
import hu.astrid.contig.ContigImplementationType;
import hu.astrid.core.Coder;
import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;

/**
 * <b>Astrid Research Inc.</b><br>
 * Project: <b>NGSequencingCommon</b><br>
 * Type: <b>ContigConverter</b><br>
 * Created: 2010.03.23.<br>
 * @author zsdoma
 */

@Deprecated
public class ContigConverter {
	
    public static Contig<Color> convert(Contig<Nucleotide> contig) {
    	Contig<Color> colorContig =
    		ContigFactory.createContig(ContigImplementationType.SIMPLE,	new Coder<Color>(new Color[] { Color.C0, Color.C1, Color.C2, Color.C3}));

    	Nucleotide prevNucleotide = contig.getSequence().get(0);
    	
    	List<Nucleotide> subList = contig.getSequence().subList(1, contig.size());
    	for (Nucleotide actual : subList) {
    		colorContig.put(prevNucleotide.getColor(actual));
    		prevNucleotide = actual;
    	}
    	colorContig.setId(contig.getId());
        return colorContig;
    }

}
