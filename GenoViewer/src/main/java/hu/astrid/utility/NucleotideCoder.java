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

import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;

import java.util.ArrayList;
import java.util.List;

/**
 * Astrid Research Author: balint Created: Jan 6, 2010 2:27:06 PM
 */
public class NucleotideCoder {
	
    private static final List<Nucleotide> nucCodeList;
    private static final List<Color> colCodeList;

    static {
		nucCodeList = new ArrayList<Nucleotide>();
		nucCodeList.add(Nucleotide.A);
		nucCodeList.add(Nucleotide.C);
		nucCodeList.add(Nucleotide.G);
		nucCodeList.add(Nucleotide.T);
    }

    static {
		colCodeList = new ArrayList<Color>();
		colCodeList.add(Color.C0);
		colCodeList.add(Color.C1);
		colCodeList.add(Color.C2);
		colCodeList.add(Color.C3);
    }

    private NucleotideCoder() { }

    public static int nucleotidesToIndex(List<Nucleotide> key) {
		int index = 0;
		for (Nucleotide n : key) {
		    int i = nucCodeList.indexOf(n);
		    index <<= 2;
		    index |= i;
		}
		return index;
    }

    public static int colorsToIndex(List<Color> key) {
		int index = 0;
		for (Color c : key) {
		    int i = colCodeList.indexOf(c);
		    index <<= 2;
		    index |= i;
		}
		return index;
    }

    public static List<Nucleotide> indexToNucleotides(int index, int seqLength) {
		List<Nucleotide> unPack = new ArrayList<Nucleotide>();
		int mask = 3;
	
		for (int i = 0; i < seqLength; i++) {
		    int code = (index & mask);
		    unPack.add(0, nucCodeList.get(code));
		    index >>>= 2;
		}
		return unPack;
    }

    public static List<Color> indexToColors(int index, int seqLength) {
		List<Color> unPack = new ArrayList<Color>();
		int mask = 3;
	
		for (int i = 0; i < seqLength; i++) {
		    int code = (index & mask);
		    unPack.add(0, colCodeList.get(code));
		    index >>>= 2;
		}
		return unPack;
    }
    
}
