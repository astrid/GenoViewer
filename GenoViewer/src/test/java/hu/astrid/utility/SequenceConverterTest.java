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

package hu.astrid.utility;

import static hu.astrid.core.Color.*;
import static org.junit.Assert.*;

import hu.astrid.contig.Contig;
import hu.astrid.contig.ContigImplementationType;
import hu.astrid.contig.reader.ContigReader;
import hu.astrid.contig.reader.ContigReaderFactory;
import hu.astrid.contig.reader.ContigReaderType;
import hu.astrid.core.Coder;
import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;
import hu.astrid.io.FastaReader;
import hu.astrid.read.CsRead;
import hu.astrid.read.FastaRead;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;


import org.junit.Test;

public class SequenceConverterTest {

    StringReader stringReader;

    Coder<Nucleotide> nucleotideCoder = new Coder<Nucleotide>(new Nucleotide[]{Nucleotide.A, Nucleotide.C, Nucleotide.G, Nucleotide.T});

    @Test
    public void testConverterOnContig() throws IOException {
        stringReader = new StringReader(
                ">test_contig\n" +
                        "CTAGCT\n"
        );
        ContigReader<Nucleotide> contigReader = ContigReaderFactory.createContigReader(
                ContigImplementationType.BYTE,
                ContigReaderType.FASTA,
                nucleotideCoder,
                new BufferedReader(stringReader));

        Contig<Nucleotide> nucleotideContig = contigReader.loadContig();

        Contig<Color> colorContig = SequenceConverter.convert(nucleotideContig);

        assertEquals("test_contig", colorContig.getId());
        assertEquals(Arrays.asList(C2, C3, C2, C3, C2), colorContig.getSequence());
    }

    @Test
    public void testConverterOnFastaRead() throws IOException {
        stringReader = new StringReader(
                ">test_read\n" +
                        "AGCTC\n"
        );
        FastaReader fastaReader = new FastaReader(new BufferedReader(stringReader));

        FastaRead fastaRead = fastaReader.readNext();

        CsRead csRead = SequenceConverter.convert(fastaRead);

        assertEquals("test_read", csRead.getId());
        assertEquals(Arrays.asList(C2, C3, C2, C2), csRead.getSequence());

    }

    @Test
    public void testConvertCsRead2FastaRead() throws IOException {
        CsRead read = new CsRead("read1", "T0232111010");
        FastaRead fread = SequenceConverter.convert(read);
        assertEquals("read1", fread.getId());
        assertEquals("TTCGACACCAA", fread.toString());
    }
}
