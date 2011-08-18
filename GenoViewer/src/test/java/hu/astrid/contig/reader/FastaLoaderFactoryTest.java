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

import static org.junit.Assert.*;
import hu.astrid.contig.ContigImplementationType;
import hu.astrid.contig.reader.ByteFastaContigReader;
import hu.astrid.contig.reader.ContigReader;
import hu.astrid.contig.reader.ContigReaderFactory;
import hu.astrid.contig.reader.PackedFastaContigReader;
import hu.astrid.core.Nucleotide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Before;
import org.junit.Test;

/**
 * Astrid Research
 * Author: Attila
 * Created: 2009.12.21.
 */
public class FastaLoaderFactoryTest {

    private BufferedReader buff;

    public FastaLoaderFactoryTest() {
    }

    @Before
    public void setUp() {
        buff = new BufferedReader( new StringReader(
                ">contig_0\n" +
                "ACGTGTCGAT\n" +
                ">contig_1\n" +
                "TTCGATTAAC\n"
                ) );
    }

    @Test
    public void testSimpleFastaLoaderFactory() throws IOException {
        ContigReader<Nucleotide> simpleFastaLoader = ContigReaderFactory.createContigReader(ContigImplementationType.SIMPLE, ContigReaderType.FASTA, null, buff);
		assertTrue(simpleFastaLoader instanceof ContigReader<?>);
    }

    @Test
    public void testPackedFastaLoaderFactory() throws IOException {
		ContigReader<Nucleotide> packedFastaLoader1 = ContigReaderFactory.createContigReader(ContigImplementationType.PACKED, ContigReaderType.FASTA, null, buff);
		assertTrue(packedFastaLoader1 instanceof PackedFastaContigReader);

		ContigReader<Nucleotide> packedFastaLoader2 = ContigReaderFactory.createContigReader(ContigImplementationType.PACKED, ContigReaderType.FASTA, null, buff, 1000);
		assertTrue(packedFastaLoader2 instanceof PackedFastaContigReader);
    }

    @Test
    public void testByteFastaLoaderFactory() throws IOException {
        ContigReader<Nucleotide> byteFastaLoader1 = ContigReaderFactory.createContigReader(ContigImplementationType.BYTE, ContigReaderType.FASTA, null, buff);
		assertTrue(byteFastaLoader1 instanceof ByteFastaContigReader);

		ContigReader<Nucleotide> byteFastaLoader2 = ContigReaderFactory.createContigReader(ContigImplementationType.BYTE, ContigReaderType.FASTA, null, buff, 10);
		assertTrue(byteFastaLoader2 instanceof ByteFastaContigReader);
    }

}