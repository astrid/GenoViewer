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

import hu.astrid.contig.Contig;
import hu.astrid.core.GenomeLetter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import org.apache.log4j.Logger;

abstract class AbstractContigWriter<T extends GenomeLetter> implements ContigWriter<T> {

	protected static volatile Logger logger = Logger .getLogger(FastaContigWriter.class);

	protected static final int DEFAULT_LENGTH_OF_ROW = 60;
	
	protected int lengthOfRow = DEFAULT_LENGTH_OF_ROW;
	
    protected BufferedWriter bufferedWriter;
	
	public int getLengthOfRow() {
		return lengthOfRow;
	}
	
	public void setLengthOfRow(int lengthOfRow) {
		this.lengthOfRow = lengthOfRow;	
	}

	
    /**
     * 
     * @see hu.astrid.contig.writer.ContigWriter#write(java.util.Collection)
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void write(Collection<? extends Contig<T>> contigs) throws IOException {

        for(Contig<T> contig : contigs) {
        	write(contig);
        }
    }

    /**
     * Closes the {@link BufferedReader} instance.
     * @see java.io.BufferedReader#close()
     * @throws IOException If I/O error occurs.
     */
	public void close() throws IOException {
		try {
			bufferedWriter.close();
		} catch(IOException e) {
			logger.error(e);
		}
	}

}
