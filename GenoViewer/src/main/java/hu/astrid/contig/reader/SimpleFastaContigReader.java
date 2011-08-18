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

import hu.astrid.contig.AbstractContig;
import hu.astrid.contig.Contig;
import hu.astrid.contig.SimpleContig;
import hu.astrid.core.Nucleotide;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Astrid Research Author: Attila Created: 2009.12.18.
 */
class SimpleFastaContigReader extends AbstractContigReader<Nucleotide> {

	public SimpleFastaContigReader(BufferedReader fastaSource) {
		super(fastaSource);
	}

	@Override
	protected AbstractContig<Nucleotide> createContig() {
		return new SimpleContig<Nucleotide>();
	}

	@Override
	public Contig<Nucleotide> loadContig() throws IOException {
		AbstractContig<Nucleotide> contig = null;
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

					contig.put(Nucleotide.valueOf(c));
				}
			} else {
				if (contig != null) {
					break;// if one of contig is ready
				}

				contig = createContig();
				contig.setId(record.substring(1, record.length()));
			}

			record = fastaSource.readLine();
		}

		return contig;

	}

}
