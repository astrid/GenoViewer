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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapIndexer<T extends GenomeLetter> extends AbstractContigIndexer<T> {

	private Map<Sequence<T>, Map<Short, Position>> index;
	private Map<Short, String> idCoder;
	private short contigNumber = 0;

	public MapIndexer(int keyLength) {
		super(keyLength);
		idCoder = new HashMap<Short, String>();
		index = new HashMap<Sequence<T>, Map<Short, Position>>();
	}

	@Override
	public List<Position> get(Sequence<T> key) {
		List<Position> positions = new LinkedList<Position>();
		if (index.containsKey(key)) {
			for (Iterator<Short> it = index.get(key).keySet().iterator(); it.hasNext();) {
				positions.add(index.get(key).get(it.next()));
			}
		}
		return positions;
	}

	@Override
	public void add(Contig<T> contig) {
		idCoder.put(contigNumber, contig.getId());
		init(contig);
		fill(contig);
		contigNumber++;
	}

	private void fill(Contig<T> contig) {
		for (int i = 0; i <= contig.size() - keyLength; ++i) {
			Sequence<T> sequence = new Sequence<T>(contig.getSequence(i, keyLength));
			if (sequence.containsNonconcreteLetter()) {
				continue;
			}
			index.get(sequence).get(contigNumber).putPosition(i);
		}
	}

	private void init(Contig<T> contig) {
		for (int i = 0; i <= contig.size() - keyLength; ++i) {
			Sequence<T> sequence = new Sequence<T>(contig.getSequence(i, keyLength));
			if (sequence.containsNonconcreteLetter()) {
				continue;
			}
			if (!index.containsKey(sequence)) {
				index.put(sequence, new HashMap<Short, Position>());
			}
			if (!index.get(sequence).containsKey(contigNumber)) {
				index.get(sequence).put(contigNumber, new Position(contigNumber));
			}
			index.get(sequence).get(contigNumber).incSize();
		}
	}

	public String getContigIdByCoderId(short contigId) {
		return idCoder.get(contigId);
	}
}
