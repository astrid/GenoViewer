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

import java.io.BufferedWriter;
import java.io.IOException;

public class FastaWriter extends BaseWriter implements ReadFileWriter<Nucleotide, FastaRead> {

	public FastaWriter(BufferedWriter bufferedWriter) {
		super(bufferedWriter);
	}

	@Override
	public void appendComment(String comment) throws IOException {
		bufferedWriter.write("#" + comment + "\n");
		bufferedWriter.flush();
	}

	@Override
	public void appendRead(FastaRead read) throws IOException {
		bufferedWriter.write(">" + read.getId() + "\n");
		bufferedWriter.write(read + "\n");
		bufferedWriter.flush();
	}

}
