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

/**
 * 
 */
package hu.astrid.io;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Astrid Research Inc.
 * Author: zsdoma
 * Created: 2010.01.05.
 */
class BaseWriter {

	protected final BufferedWriter bufferedWriter;
	
	public BaseWriter(BufferedWriter bufferedWriter) {
		if (bufferedWriter == null) {
			throw new NullPointerException();
		}
		this.bufferedWriter = bufferedWriter;
	}
	
	public void close() throws IOException {
		this.bufferedWriter.close();
	}
}
