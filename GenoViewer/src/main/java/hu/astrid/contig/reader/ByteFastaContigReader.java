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

package hu.astrid.contig.reader;

import hu.astrid.contig.ByteContig;
import hu.astrid.contig.Contig;
import hu.astrid.core.Coder;
import hu.astrid.core.Nucleotide;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Astrid Research Author: Attila Created: 2010.01.05.
 */
class ByteFastaContigReader extends AbstractContigReader<Nucleotide> {

	private int capacity = 1000;
	
	private Coder<Nucleotide> coder;

	/**
	 * Constructor
	 * 
	 * Uses default capacity of the internal store.
	 * <p>
	 * Size of bit of nucleotide is 1000;
	 * 
	 * @param buff
	 *            a datasource
	 */
	public ByteFastaContigReader(BufferedReader buff, Coder<Nucleotide> coder) {
		super(buff);
		this.coder = coder;
	}

	/**
	 * Constructor
	 * 
	 * @param buff
	 *            a datasource
	 * @param capacity
	 *            capacity of the internal store, size of bit of nucleotide
	 */
	public ByteFastaContigReader(BufferedReader buff, int capacity, Coder<Nucleotide> coder) {
		this(buff, coder);

		if (capacity > 0) {
			this.capacity = capacity;
		}
	}

	@Override
	protected ByteContig<Nucleotide> createContig() {
		return new ByteContig<Nucleotide>(coder);
	}

	@Override
	public Contig<Nucleotide> loadContig() throws IOException {
		int i = 0;// number of nucleotides in the pack
		byte[] tmpArray = new byte[capacity];
		ByteContig<Nucleotide> contig = null;

		if (record == null) {
			record = fastaSource.readLine();
		}

		while (record != null) {
			if (record.isEmpty()) {
				record = fastaSource.readLine();
				continue;
			}
			
			if (record.charAt(0) != '>') {
				if ((contig == null) || (contig.getId() == null)) {
					throw new IOException("Illegal fasta source!");
				}

				for (char c : record.toCharArray()) {
					if (!Nucleotide.isValid(c)) {
						throw new IllegalArgumentException(
								"Illegal nucleotide character! " + c);
					}

					if (i == capacity) {
						contig.putByteArray(tmpArray, i);
						i = 0;
					}

					tmpArray[i++] = Nucleotide.valueOf(c).byteValue();
				}

			} else {
				if (contig != null) {
					if (i != 0) {
						contig.putByteArray(tmpArray, i);
						contig.concatArray(i);
					}

					break;// if one of contig is ready
				}

				contig = createContig();
				contig.setId(record.substring(1, record.length()));

			}

			record = fastaSource.readLine();
		}

		if ((record == null) && (i != 0)) {
			contig.putByteArray(tmpArray, i);
			contig.concatArray(i);
		}

		return contig;
	}

}
