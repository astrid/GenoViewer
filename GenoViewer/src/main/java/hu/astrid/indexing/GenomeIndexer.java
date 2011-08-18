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

package hu.astrid.indexing;

import hu.astrid.contig.Contig;
import hu.astrid.core.GenomeLetter;

/**
  * Astrid Research
  * @author Attila
  * Created: 2010.01.25.
 */
@Deprecated
public class GenomeIndexer {
	
	private GenomeIndexer() {
		
	}
	
	public static <T extends GenomeLetter> GenomeMap<T> createMap(IndexerType indexerType, Contig<T> contig, int seqLength, int arraySize) {
		GenomeMap<T> genomeMap = GenomeMapFactory.<T>createGenomeMap(indexerType, seqLength, arraySize);
		init(genomeMap, contig, seqLength, arraySize);

		return genomeMap;
	}
	
	private static <T extends GenomeLetter> void init(GenomeMap<T> genomeMap, Contig<T> contig, int seqLength, int arraySize) {
		final int n = contig.size() - seqLength;

		for (int i = 0; i <= n; i++) {
			if (!contig.containsNonconcreteLetter(i, seqLength)) {
				genomeMap.put(contig.getSequence(i, seqLength), i);
			}
		}
	}

}
