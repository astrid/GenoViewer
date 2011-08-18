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

import java.io.IOException;
import java.util.Collection;
import hu.astrid.contig.Contig;
import hu.astrid.core.GenomeLetter;

public interface ContigWriter<T extends GenomeLetter> {

	/**
	 * Write a simple contig.
	 * @param contig a character to be written
	 * @throws IOException If an I/O error occurs
	 */
    void write(Contig<T> contig) throws IOException;

    /** 
     * Writes all of the elements.
     * @throws IOException If an I/O error occurs
     */
	void write(Collection<? extends Contig<T>> contigs) throws IOException;
	
	/**
	 * Close the stream.
	 * @throws IOException If an I/O error occurs
	 */
	void close() throws IOException;

	
}
