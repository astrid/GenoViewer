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
import hu.astrid.core.Coder;
import hu.astrid.core.Nucleotide;
import static hu.astrid.core.Nucleotide.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Astrid Research Author: Attila Created: 2009.12.30.
 */
public class PackedFastaLoaderTest {

	private BufferedReader buff;
	
	private Coder<Nucleotide> coder = new Coder<Nucleotide>(Nucleotide.values());

	public PackedFastaLoaderTest() {
	}

	@Before
	public void setUp() {
		buff = new BufferedReader(new StringReader(">gi|88943055\n"
				+ "CGGGTTTTGGGGTTTT\n" + "CAAACCCCAAAACCCT\n"
				+ ">gi|88943065\n" + "CGGGTTGTGGA\n" + ">gi|88943075\n"
				+ "AGGGTTTTGGGGTTTT\n" + "AAAACCCCAAAACCCC\n"
				+ "GGGGTTTTGGGGTTTT\n" + "CTTAG\n" + ">gi|88943085\n"
				+ "CGGGTTTTGGGGTTTT\n" + "CAAACCCCAAAACCCT\n"
				+ "GGGGTTTTGGGGTTTT\n" + "CAAACGCCAAAACCCT\n"));
	}

	@Test
	public void testPackedFastaLoader() throws IOException {
		ContigReader<Nucleotide> fastaLoader = ContigReaderFactory
				.createContigReader(ContigImplementationType.PACKED, ContigReaderType.FASTA, coder, buff);
		List<Contig<Nucleotide>> contigList = fastaLoader.load();
		Iterator<Contig<Nucleotide>> contigIterator = contigList.iterator();

		Contig<Nucleotide> contig1 = contigIterator.next();
		assertEquals("gi|88943055", contig1.getId());
		assertEquals("CGGGTTTTGGGGTTTTCAAACCCCAAAACCCT", contig1.toString());

		assertEquals(32, contig1.size());

		assertEquals(C, contig1.get(0));
		assertEquals(T, contig1.get(31));
		assertEquals(C, contig1.get(16));

		List<Nucleotide> nucList = Arrays.asList(C, G, G, G, T);
		assertEquals(nucList, contig1.getSequence(0, 5));

		Contig<Nucleotide> contig2 = contigIterator.next();
		assertEquals("gi|88943065", contig2.getId());
		assertEquals("CGGGTTGTGGA", contig2.toString());

		assertEquals(11, contig2.size());

		assertEquals(C, contig2.get(0));
		assertEquals(G, contig2.get(1));
		assertEquals(A, contig2.get(10));
		assertEquals(G, contig2.get(6));

		nucList = Arrays.asList(T, G, G, A);
		assertEquals(nucList, contig2.getSequence(7, 5));

		Contig<Nucleotide> contig3 = contigIterator.next();
		assertEquals("gi|88943075", contig3.getId());
		assertEquals("AGGGTTTTGGGGTTTTAAAACCCCAAAACCCCGGGGTTTTGGGGTTTTCTTAG",
				contig3.toString());

		assertEquals(53, contig3.size());

		assertEquals(A, contig3.get(0));
		assertEquals(G, contig3.get(52));
		assertEquals(C, contig3.get(48));

		nucList = Arrays.asList(A, G, G, G, T);
		assertEquals(nucList, contig3.getSequence(0, 5));

		nucList = Arrays.asList(C, C, G, G, G);
		assertEquals(nucList, contig3.getSequence(30, 5));

		nucList = new ArrayList<Nucleotide>();
		nucList.add(Nucleotide.G);
		assertEquals(nucList, contig3.getSequence(52, 5));

		nucList = new ArrayList<Nucleotide>();
		assertEquals(nucList, contig3.getSequence(53, 5));

		Contig<Nucleotide> contig4 = contigIterator.next();
		assertEquals("gi|88943085", contig4.getId());
		assertEquals(
				"CGGGTTTTGGGGTTTTCAAACCCCAAAACCCTGGGGTTTTGGGGTTTTCAAACGCCAAAACCCT",
				contig4.toString());

		assertEquals(64, contig4.size());

		assertEquals(C, contig4.get(0));
		assertEquals(T, contig4.get(63));
		assertEquals(T, contig4.get(31));
		assertEquals(G, contig4.get(53));

		nucList = Arrays.asList(C, G, G);
		assertEquals(nucList, contig4.getSequence(0, 3));

		nucList = new ArrayList<Nucleotide>();
		nucList.add(Nucleotide.T);
		assertEquals(nucList, contig4.getSequence(63, 5));

		nucList = Arrays.asList(C, C, C, T, G);
		assertEquals(nucList, contig4.getSequence(28, 5));
	}

	@Test
	public void testPackedFastaLoader_loadContig() throws IOException {
		ContigReader<Nucleotide> fastaLoader = ContigReaderFactory
				.createContigReader(ContigImplementationType.PACKED, ContigReaderType.FASTA, coder, buff);

		Contig<Nucleotide> contig1 = fastaLoader.loadContig();
		assertEquals("gi|88943055", contig1.getId());
		assertEquals("CGGGTTTTGGGGTTTTCAAACCCCAAAACCCT", contig1.toString());

		assertEquals(32, contig1.size());

		assertEquals(C, contig1.get(0));
		assertEquals(T, contig1.get(31));
		assertEquals(C, contig1.get(16));

		List<Nucleotide> nucList = Arrays.asList(C, G, G, G, T);
		assertEquals(nucList, contig1.getSequence(0, 5));

		Contig<Nucleotide> contig2 = fastaLoader.loadContig();
		assertEquals("gi|88943065", contig2.getId());
		assertEquals("CGGGTTGTGGA", contig2.toString());

		assertEquals(11, contig2.size());

		assertEquals(C, contig2.get(0));
		assertEquals(G, contig2.get(1));
		assertEquals(A, contig2.get(10));
		assertEquals(G, contig2.get(6));

		nucList = Arrays.asList(T, G, G, A);
		assertEquals(nucList, contig2.getSequence(7, 5));

		Contig<Nucleotide> contig3 = fastaLoader.loadContig();
		assertEquals("gi|88943075", contig3.getId());
		assertEquals("AGGGTTTTGGGGTTTTAAAACCCCAAAACCCCGGGGTTTTGGGGTTTTCTTAG",
				contig3.toString());

		assertEquals(53, contig3.size());

		assertEquals(A, contig3.get(0));
		assertEquals(G, contig3.get(52));
		assertEquals(C, contig3.get(48));

		nucList = Arrays.asList(A, G, G, G, T);
		assertEquals(nucList, contig3.getSequence(0, 5));

		nucList = Arrays.asList(C, C, G, G, G);
		assertEquals(nucList, contig3.getSequence(30, 5));

		nucList = new ArrayList<Nucleotide>();
		nucList.add(Nucleotide.G);
		assertEquals(nucList, contig3.getSequence(52, 5));

		nucList = new ArrayList<Nucleotide>();
		assertEquals(nucList, contig3.getSequence(53, 5));

		Contig<Nucleotide> contig4 = fastaLoader.loadContig();
		assertEquals("gi|88943085", contig4.getId());
		assertEquals(
				"CGGGTTTTGGGGTTTTCAAACCCCAAAACCCTGGGGTTTTGGGGTTTTCAAACGCCAAAACCCT",
				contig4.toString());

		assertEquals(64, contig4.size());

		assertEquals(C, contig4.get(0));
		assertEquals(T, contig4.get(63));
		assertEquals(T, contig4.get(31));
		assertEquals(G, contig4.get(53));

		nucList = Arrays.asList(C, G, G);
		assertEquals(nucList, contig4.getSequence(0, 3));

		nucList = new ArrayList<Nucleotide>();
		nucList.add(Nucleotide.T);
		assertEquals(nucList, contig4.getSequence(63, 5));

		nucList = Arrays.asList(C, C, C, T, G);
		assertEquals(nucList, contig4.getSequence(28, 5));
	}

}