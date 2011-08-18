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
import java.util.ArrayList;

/**
 * Astrid Research
 * Author: balint
 * Created: Jan 12, 2010 11:21:04 AM
 */
@Deprecated
public class ListGenomeMap<T extends GenomeLetter> extends AbstractGenomeMap<T> {
    /**
     * Listás implementáció
     */
    private int keyLength = 0;
    private int arraySize = 0;
    private int size = 0;
    List<int[]>[] genomeMap;

    @SuppressWarnings("unchecked")
    public ListGenomeMap(int seqLength, int arraySize) {
        this.keyLength = seqLength;
        this.arraySize = arraySize;
        this.size = (int) Math
                .pow(Double.valueOf(4), Double.valueOf(seqLength));
        this.genomeMap = new List[size];
    }

    @Override
    public int keyLength() {
        return keyLength;
    }

    @Override
    public void put(List<T> key, Integer pos) {
        int index = getGenomeMapIndex(key);

        if (genomeMap[index] == null) {
            genomeMap[index] = new ArrayList<int[]>();
            genomeMap[index].add(new int[arraySize]);
        }

        int arrayLength = ++genomeMap[index].get(0)[0];
        arrayLength++;

        if (((arrayLength + 1) % arraySize) == 0) {
            genomeMap[index].add(new int[arraySize]);
        }
        int arrayIndex = (arrayLength - 1) / arraySize;

        int tempIndex = arrayLength - (arrayIndex * arraySize) - 1;
        genomeMap[index].get(arrayIndex)[tempIndex] = pos;
    }

	@Override
	public List<Integer> get(List<T> key) {
		List<int[]> arrayList = genomeMap[getGenomeMapIndex(key)];
		if (arrayList == null) {
			return new ArrayList<Integer>();
		}
		List<Integer> posList = new ArrayList<Integer>(0);
		int size = arrayList.get(0)[0];
		int j = 0;

		for (int[] array : arrayList) {
			for (int pos : array) {
				if (j == 0 || j > size) {
				} else {
					posList.add(pos);
				}
				j++;
			}
		}

		return posList;
	}

    public String toString() {
        // TODO formazas
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (List<int[]> arrayList : genomeMap) {
            if (arrayList != null) {
                builder.append(i + " :");
                builder.append("[");
                for (int[] array : arrayList) {
                    for (int pos : array) {
                        builder.append(pos + ", ");
                    }
                }
                builder.append("]\n");
            } else {
                builder.append(i + " : null");
                builder.append("\n");
            }
            i++;
        }

        return builder.toString();
    }

}
