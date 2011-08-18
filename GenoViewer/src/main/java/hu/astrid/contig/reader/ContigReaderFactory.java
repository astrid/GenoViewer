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

import hu.astrid.contig.ContigImplementationType;
import hu.astrid.core.Coder;
import hu.astrid.core.Nucleotide;

import java.io.BufferedReader;

/**
 * Astrid Research Author: Attila Created: 2009.12.18.
 */
public class ContigReaderFactory {

	private ContigReaderFactory() {
	}
	
	// TODO should be generic
	public static ContigReader<Nucleotide> createContigReader(ContigImplementationType implementationType, ContigReaderType readerType, Coder<Nucleotide> coder, BufferedReader buff) {
		return createContigReader(implementationType, readerType, coder, buff, -1);
	}
	
	// TODO should be generic
	public static ContigReader<Nucleotide> createContigReader(ContigImplementationType implementationType, ContigReaderType readerType, Coder<Nucleotide> coder, BufferedReader buff, int capacity) {
		ContigReader<Nucleotide> reader = null;
		switch (readerType) {
		case FASTA:
			switch (implementationType) {
			case SIMPLE:
				reader = new SimpleFastaContigReader(buff);
				break;
			case BYTE:
				reader = new ByteFastaContigReader(buff, capacity, coder);
				break;
			case PACKED:
				reader = new PackedFastaContigReader(buff, capacity, coder);
				break;
			default:
				throw new IllegalArgumentException("Unknown contig implementation type!");
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown contig reader type!");
		}
		return reader;
	}

//	public static ContigReader<Nucleotide> getSimpleFastaContigReader(BufferedReader buff) {
//		return new SimpleFastaContigReader(buff);
//	}
//
//	public static ContigReader<Nucleotide> getPackedFastaContigReader(BufferedReader buff) {
//		return new PackedFastaContigReader(buff);
//	}
//
//	public static ContigReader<Nucleotide> getPackedFastaContigReader(BufferedReader buff, int capacity) {
//		return new PackedFastaContigReader(buff, capacity);
//	}
//
//	public static ContigReader<Nucleotide> getByteFastaContigReader(BufferedReader buff) {
//		return new ByteFastaContigReader(buff);
//	}
//
//	public static ContigReader<Nucleotide> getByteFastaContigReader(BufferedReader buff, int capacity) {
//		return new ByteFastaContigReader(buff, capacity);
//	}

}
