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

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Astrid Research
 * Author: balint
 * Created: Jan 12, 2010 11:22:59 AM
 */
@Deprecated
public class ArrayGenomeMap<T extends GenomeLetter> extends AbstractGenomeMap<T> {

    /**
     * Tömbös implementáció.
     */
    private int keyLength = 0;

    private int arraySize = 0;

    private int size = 0;

    private int[][] genomeMap;
    
    public ArrayGenomeMap(int seqLength, int arraySize) {
        this.keyLength = seqLength;
        this.arraySize = arraySize;
        this.size = (int) Math
                .pow(Double.valueOf(4), Double.valueOf(seqLength));
        this.genomeMap = new int[size][];
    }

    @Override
    public int keyLength() {
        return keyLength;
    }

    @Override
    public void put(List<T> key, Integer pos) {
        int index = getGenomeMapIndex(key);
        if (genomeMap[index] == null) {
            genomeMap[index] = new int[arraySize];
        }
        int arrayLength = ++genomeMap[index][0];
        if (((arrayLength + 2) % arraySize) == 0) {
            genomeMap[index] = Arrays.copyOf(genomeMap[index],
                    genomeMap[index].length + arraySize);
            genomeMap[index][arrayLength] = pos.intValue();
        } else {
            genomeMap[index][genomeMap[index][0]] = pos.intValue();
        }
    }

    @Override
    public List<Integer> get(List<T> key) {
        int[] posArray = genomeMap[getGenomeMapIndex(key)];
        if (posArray == null) {
            return new ArrayList<Integer>();
        }
        List<Integer> posList = new ArrayList<Integer>(0);

        int size = posArray[0];
        int j = 0;
        for (int i : posArray) {
            if (j == 0 || j > size) {
            } else {
                posList.add(Integer.valueOf(i));
            }
            j++;
        }

        return posList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < genomeMap.length; i++) {
            builder.append(i + " : " + Arrays.toString(genomeMap[i]) + "\n");
        }

        return builder.toString();
    }

}
