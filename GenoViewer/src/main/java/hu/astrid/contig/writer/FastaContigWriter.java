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

package hu.astrid.contig.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import hu.astrid.contig.Contig;
import hu.astrid.core.GenomeLetter;

public class FastaContigWriter<T extends GenomeLetter> extends AbstractContigWriter<T> {

	public FastaContigWriter(BufferedWriter bufferedWriter) {
		this.bufferedWriter = bufferedWriter;
	}
	
	public FastaContigWriter(FileWriter fileWriter) throws IOException {
		super();
		bufferedWriter = new BufferedWriter(fileWriter);
	}
	
	/**
	 * Writes a {@link hu.astrid.contig.Contig<T>}instance on the (@link java.io.BufferedReader) instance.
	 * @throws IOException If an I/O error occurs
	 */
	@Override
	public void write(Contig<T> contig) throws IOException {
		bufferedWriter.write(">" + contig.getId() + "\n");
		List<T> sequence = contig.getSequence();
		int currentRowLength = 0;
		for (T genomeLetter : sequence) {
			bufferedWriter.write(genomeLetter.toString());

			if (++currentRowLength  >= lengthOfRow) {
				bufferedWriter.write("\n");
				currentRowLength = 0;
			}
		}
		bufferedWriter.write("\n");
		bufferedWriter.flush();

	}

}
