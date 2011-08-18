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
import hu.astrid.read.CsRead;
import hu.astrid.read.FastaRead;

import java.util.ArrayList;
import java.util.List;

/**
 * Astrid Research Inc.
 * Author: zsdoma
 * Created: 2009.12.29.
 */
public class Splitter {

	public List<List<Nucleotide>> split(FastaRead fastaRead,
			int length) {
		List<Nucleotide> nucleotides = fastaRead.getSequence();
		List<List<Nucleotide>> result = new ArrayList<List<Nucleotide>>();
		
		int count = 0;
		List<Nucleotide> line = new ArrayList<Nucleotide>();

		for (Nucleotide nucleotide : nucleotides) {
			if (count < length) {
				line.add(nucleotide);
				++count;
			} else {
				result.add(line);
				count = 1;
				line = new ArrayList<Nucleotide>();
				line.add(nucleotide);
			}
		}
		result.add(line);

		return result;
	}

	public List<List<Color>> split(CsRead cSRead,
			int length) {
		List<Color> colors = cSRead.getSequence().subList(1, cSRead.getSequence().size());
		List<List<Color>> result = new ArrayList<List<Color>>();
	
		int count = 0;
		List<Color> line = new ArrayList<Color>();

		for (Color color : colors) {
			if (count < length) {
				line.add(color);
				++count;
			} else {
				result.add(line);
				count = 1;
				line = new ArrayList<Color>();
				line.add(color);
			}
		}
		result.add(line);

		return result;
	}
}
