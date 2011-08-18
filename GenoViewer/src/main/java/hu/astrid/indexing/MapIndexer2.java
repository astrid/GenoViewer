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
import hu.astrid.core.Sequence;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Astrid Research Inc.
 * Author: Gór Balázs
 * Created: 2010.05.06., 7:56:34
 * Class: hu.astrid.indexing.MapIndexer2
 */
public class MapIndexer2<T extends GenomeLetter> extends AbstractContigIndexer<T> {

	private Map<Short, String> idCoder;
	private int[][] index;
	private int size[];
	private int current[];
	private byte[][] contigId;
	private short contigNumber;

	public MapIndexer2(int keyLength) {
		super(keyLength);
		idCoder = new HashMap<Short, String>();
		contigNumber = 0;
		index = new int[(int) Math.pow(4, keyLength)][];
		contigId = new byte[(int) Math.pow(4, keyLength)][];
		size = new int[(int) Math.pow(4, keyLength)];
		current = new int[(int) Math.pow(4, keyLength)];
		for (int i = 0; i < size.length; ++i) {
			size[i] = 0;
			current[i] = 0;
		}
	}

	public List<Position> get(Sequence<T> key) {
		List<Position> positions = new ArrayList<Position>(contigNumber);
		if (key.containsNonconcreteLetter()) {
			return positions;
		}
		final int hash = key.hashCode();
		if (index[hash] != null) {
			int id = -1;
			int offset = 0;
			int i = 0;
			for (; i < index[hash].length; ++i) {
				if (id != contigId[hash][i]) {
					if (id != -1) {
						Position position = new Position((short) contigId[hash][i]);
						position.setSize(i - offset);
						position.setPositions(IntBuffer.wrap(index[hash], offset, i - offset).array());
						positions.add(position);
					}
					id = contigId[hash][i];
					offset = i;
				}
			}
			--i;
			if (id != -1) {
				Position position = new Position((short) contigId[hash][i]);
				position.setSize(i - offset);
				position.setPositions(IntBuffer.wrap(index[hash], offset, i - offset).array());
				positions.add(position);
			}
		}
		return positions;
	}

	public void add(Contig<T> contig) {
		idCoder.put(contigNumber, contig.getId());
		init(contig);
		copy();
		fill(contig);
		contigNumber++;
	}

	private void init(Contig<T> contig) {
		for (int i = 0; i <= contig.size() - keyLength; ++i) {
			Sequence<T> sequence = new Sequence<T>(contig.getSequence(i, keyLength));
			if (!sequence.containsNonconcreteLetter()) {
				++size[sequence.hashCode()];
			}
		}
	}

	private void copy() {
		for (int i = 0; i < index.length; ++i) {
			if (index[i] != null) {
				// no new position?
				if (index[i].length == size[i]) {
					continue;
				}
				int[] newIndex = new int[size[i]];
				byte[] newContigId = new byte[size[i]];
				System.arraycopy(index[i], 0, newIndex, 0, index[i].length);
				System.arraycopy(contigId[i], 0, newContigId, 0, contigId[i].length);
				index[i] = newIndex;
				contigId[i] = newContigId;
			} else {
				index[i] = new int[size[i]];
				contigId[i] = new byte[size[i]];
			}
		}
	}

	private void fill(Contig<T> contig) {
		for (int i = 0; i <= contig.size() - keyLength; ++i) {
			Sequence<T> sequence = new Sequence<T>(contig.getSequence(i, keyLength));
			final int hash = sequence.hashCode();
			if (!sequence.containsNonconcreteLetter()) {
				index[hash][current[hash]] = i;
				contigId[hash][current[hash]] = (byte) contigNumber;
				++current[hash];
			}
		}
	}

	public String getContigIdByCoderId(short contigId) {
		return idCoder.get(contigId);
	}
}
