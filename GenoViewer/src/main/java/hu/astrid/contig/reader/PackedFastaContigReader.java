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
import hu.astrid.core.Coder;
import hu.astrid.core.Nucleotide;
import hu.astrid.contig.PackedContig;

import java.io.BufferedReader;

/**
 * Astrid Research Author: Attila Created: 2009.12.21.
 */
class PackedFastaContigReader extends SimpleFastaContigReader {

	private int storeSize = -1;
	
	private Coder<Nucleotide> coder;

	public PackedFastaContigReader(BufferedReader fastaSource, Coder<Nucleotide> coder) {
		super(fastaSource);
		this.coder = coder;
	}

	public PackedFastaContigReader(BufferedReader fastaSource, int storeSize, Coder<Nucleotide> coder) {
		this(fastaSource, coder);
		this.storeSize = storeSize;
	}

	@Override
	protected AbstractContig<Nucleotide> createContig() {
		if (storeSize == -1) {
			return new PackedContig<Nucleotide>(coder);
		}

		return new PackedContig<Nucleotide>(coder, this.storeSize);

	}

}
