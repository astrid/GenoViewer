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

import java.util.ArrayList;
import java.util.List;

import hu.astrid.contig.Contig;
import hu.astrid.contig.SimpleContig;
import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;
import hu.astrid.read.CsRead;
import hu.astrid.read.FastaRead;

public class SequenceConverter {
	
	/**
	 * @param sequence a nucleotide sequence
	 * @return the converted sequence
	 */
	public static List<Color> convert(List<Nucleotide> sequence) {
		Nucleotide prevNucleotide = sequence.get(0);
    	List<Nucleotide> subList = sequence.subList(1, sequence.size());
    	List<Color> transcriptedSequence = new ArrayList<Color>();
    	
    	for (Nucleotide actual : subList) {
    		transcriptedSequence.add(prevNucleotide.getColor(actual));
    		prevNucleotide = actual;
    	}
    	
    	return transcriptedSequence;
	}
	
	/**
	 * @param contig a nucleotide contig
	 * @return a color contig
	 */
	public static Contig<Color> convert(Contig<Nucleotide> contig) {
		List<Color> sequence = convert(contig.getSequence());
    	Contig<Color> colorContig = new SimpleContig<Color>(sequence);
    	
    	colorContig.setId(contig.getId());
        return colorContig;
    }
	
	/**
	 * @param fastaRead a fasta read
	 * @return a {@link CsRead} instance
	 */
	public static CsRead convert(FastaRead fastaRead) {
		StringBuilder stringBuilder = new StringBuilder();
		List<Color> sequence = convert(fastaRead.getSequence());
    	
    	CsRead csRead = null;
    	
    	stringBuilder.append(fastaRead.getSequence().get(0));
    	
    	for(Color color : sequence) {
    		stringBuilder.append(color);
    	}
    	
    	csRead = new CsRead(fastaRead.getId(), stringBuilder.toString());
    	
        return csRead;
    }

    public static FastaRead convert(CsRead read) {
        StringBuilder buf = new StringBuilder();
        Nucleotide currentNucleotide = read.getAdaptor();
        for (Color c : read.getSequence()) {
            buf.append(currentNucleotide.charValue());
            currentNucleotide = currentNucleotide.translate(c);
        }
        buf.append(currentNucleotide.charValue());
        return new FastaRead(read.getId(), buf.toString());
    }
}
