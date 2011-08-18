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

import hu.astrid.core.GenomeLetter;

/**
 * Astrid Research
 * Author: balint
 * Created: Jan 12, 2010 11:26:25 AM
 */
@Deprecated
public class GenomeMapFactory {
    
    public static <T extends GenomeLetter> GenomeMap<T> createGenomeMap(IndexerType indexerType, int seqLength, int arraySize) {
    	AbstractGenomeMap<T> genomeMap = null;
		switch (indexerType) {
			case ARRAY_INDEXER:
				genomeMap = new ArrayGenomeMap<T>(seqLength, arraySize);
				break;
			case LIST_INDEXER:
				genomeMap = new ListGenomeMap<T>(seqLength, arraySize);
				break;
			default:
				throw new IllegalArgumentException("Unknown indexer type!");
		}
		genomeMap.setIndexGenerator(new SimpleIndexGenerator<T>());
        return genomeMap;
    }
    
}
