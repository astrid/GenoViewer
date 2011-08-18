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

import hu.astrid.core.Nucleotide;
import hu.astrid.read.FastaRead;

import java.io.BufferedReader;

/**
 * Astrid Research
 * Author: mkiss
 * Created: Dec 14, 2009
 */
public class FastaReader extends AbstractTagReader<Nucleotide, FastaRead> {
	
   public FastaReader(BufferedReader bufferedReader) {
        super(bufferedReader);
    }

	@Override
	protected FastaRead createRead(String id, String sequence) {
		return new FastaRead(id, sequence);
	}
    
}
