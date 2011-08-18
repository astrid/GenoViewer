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

package hu.astrid.io;

import hu.astrid.core.GenomeLetter;
import hu.astrid.read.Read;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class AbstractTagReader<T extends GenomeLetter, S extends Read<T>> extends BaseReader implements ReadFileReader<T, S> {

	public AbstractTagReader(BufferedReader seqReader) {
		super(seqReader);
	}
	
	protected abstract S createRead(String id, String sequence);
	
	@Override
    public S readNext() throws IOException {
        Pair pair = getNextPair();
        if (pair == null) {
            return null;
        }

        return this.createRead(pair.getId().substring(1), pair.getValue());
    }

}
