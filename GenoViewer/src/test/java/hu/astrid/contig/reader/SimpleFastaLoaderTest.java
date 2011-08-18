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

import hu.astrid.contig.Contig;
import hu.astrid.contig.ContigImplementationType;
import hu.astrid.contig.reader.ContigReader;
import hu.astrid.contig.reader.ContigReaderFactory;
import hu.astrid.core.Nucleotide;
import static hu.astrid.core.Nucleotide.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Astrid Research
 * Author: Attila
 * Created: 2009.12.21.
 */
public class SimpleFastaLoaderTest {

    private BufferedReader buff;

    public SimpleFastaLoaderTest() {
    }

    @Before
    public void setUp() {
        buff = new BufferedReader(new StringReader(
                ">contig_0\n" +
                "ACGTGTCGAT\n" +
                ">contig_1\n" +
                "TTCGATTAAC\n" +
                "TGGATG\n" +
                ">contig_2\n" +
                "TTCGATTAAC\n" +
                "\n" +
                "TGGATG\n"
        ));
    }

    @Test()
    public void testSimpleFastaLoader() throws IOException {
        ContigReader<Nucleotide> fastaLoader = ContigReaderFactory.createContigReader(ContigImplementationType.SIMPLE, ContigReaderType.FASTA, null, buff);
        List<Contig<Nucleotide>> contigList = fastaLoader.load();
        Iterator<Contig<Nucleotide>> contigIterator = contigList.iterator();

        Contig<Nucleotide> contig1 = contigIterator.next();
        assertEquals("contig_0", contig1.getId());
        assertEquals("ACGTGTCGAT", contig1.toString());
        assertEquals(Nucleotide.C, contig1.get(6));

        List<Nucleotide> nucList = Arrays.asList(G, T, G, T, C);
        assertEquals(nucList, contig1.getSequence(2, 5));

        Contig<Nucleotide> contig2 = contigIterator.next();
        assertEquals("contig_1", contig2.getId());
        assertEquals("TTCGATTAACTGGATG", contig2.toString());
        assertEquals(A, contig2.get(4));

        nucList = Arrays.asList(G, A, T, T);
        assertEquals(nucList, contig2.getSequence(3, 4));
        
        Contig<Nucleotide> contig3 = contigIterator.next();
        assertEquals("contig_2", contig3.getId());
        assertEquals("TTCGATTAACTGGATG", contig3.toString());
        assertEquals(A, contig3.get(4));
    }

    @Test
    public void testSimpleFastaLoader_loadContig() throws IOException {
        ContigReader<Nucleotide> fastaLoader = ContigReaderFactory.createContigReader(ContigImplementationType.SIMPLE, ContigReaderType.FASTA, null, buff);

        Contig<Nucleotide> contig1 = fastaLoader.loadContig();
        assertEquals("contig_0", contig1.getId());
        assertEquals("ACGTGTCGAT", contig1.toString());
        assertEquals(Nucleotide.C, contig1.get(6));

        List<Nucleotide> nucList = Arrays.asList(G, T, G, T, C);
        assertEquals(nucList, contig1.getSequence(2, 5));

        Contig<Nucleotide> contig2 = fastaLoader.loadContig();
        assertEquals("contig_1", contig2.getId());
        assertEquals("TTCGATTAACTGGATG", contig2.toString());
        assertEquals(A, contig2.get(4));

        nucList = Arrays.asList(G, A, T, T);
        assertEquals(nucList, contig2.getSequence(3, 4));
        
        Contig<Nucleotide> contig3 = fastaLoader.loadContig();
        assertEquals("contig_2", contig3.getId());
        assertEquals("TTCGATTAACTGGATG", contig3.toString());
        assertEquals(A, contig3.get(4));
    }

}