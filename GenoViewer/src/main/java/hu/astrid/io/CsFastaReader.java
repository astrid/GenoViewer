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

import hu.astrid.core.Color;
import hu.astrid.read.CsRead;

import java.io.BufferedReader;

/**
 * Astrid Research
 * Author: balint
 * Created: Dec 11, 2009 3:52:04 PM
 */
public class CsFastaReader extends AbstractTagReader<Color, CsRead> {

    public CsFastaReader(BufferedReader bufferedReader) {
        super(bufferedReader);
    }

	@Override
	protected CsRead createRead(String id, String sequence) {
		return new CsRead(id, sequence);
	}
}
