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

import static org.junit.Assert.assertEquals;
import hu.astrid.contig.Contig;
import hu.astrid.contig.SimpleContig;
import hu.astrid.core.Nucleotide;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class FastaContigWriterTest {

	private FastaContigWriter<Nucleotide> fastaContigWriter;
	
	private StringBuilder legal;
	
	private StringWriter stringWriter;
	
	@Before
	public void setUp() throws FileNotFoundException {
		legal = new StringBuilder();
		stringWriter = new StringWriter();
		fastaContigWriter = new FastaContigWriter<Nucleotide>(new BufferedWriter(stringWriter));
	}
	
	@Test
	public void testFastaWriterWriteMethodOnOneContig() throws IOException {
		List<Nucleotide> sequence = Arrays.asList(Nucleotide.A,Nucleotide.C,Nucleotide.A,Nucleotide.G,
				                                  Nucleotide.T,Nucleotide.G,Nucleotide.T,Nucleotide.A);
		Contig<Nucleotide> contig = new SimpleContig<Nucleotide>(sequence);
		contig.setId("Contig1");
		
		fastaContigWriter.setLengthOfRow(3);
		fastaContigWriter.write(contig);
		legal.append(">Contig1\n");
		legal.append("ACA\n");
		legal.append("GTG\n");
		legal.append("TA\n");
		assertEquals(legal.toString(), stringWriter.getBuffer().toString());
	}
	
	@Test
	public void testFastaWriterWriteMethodOnMoreContigs() throws IOException {
		ArrayList<SimpleContig<Nucleotide>> contigs = new ArrayList<SimpleContig<Nucleotide>>();
		List<Nucleotide> sequence1 = Arrays.asList(Nucleotide.G,Nucleotide.C,Nucleotide.A,Nucleotide.G,
				  				  				   Nucleotide.C,Nucleotide.A,Nucleotide.T,Nucleotide.G,
				  				  				   Nucleotide.T,Nucleotide.A,Nucleotide.C,Nucleotide.T);
		
		List<Nucleotide> sequence2 = Arrays.asList(Nucleotide.A,Nucleotide.A,Nucleotide.C,Nucleotide.G,
				  				  				   Nucleotide.T,Nucleotide.C,Nucleotide.T,Nucleotide.G);

		SimpleContig<Nucleotide> contig1 = new SimpleContig<Nucleotide>(sequence1);
		SimpleContig<Nucleotide> contig2 = new SimpleContig<Nucleotide>(sequence2);

		contig1.setId("Contig1");
		contig2.setId("Contig2");

		contigs.add(contig1);
		contigs.add(contig2);
		
		fastaContigWriter.setLengthOfRow(5);
		fastaContigWriter.write(contigs);
		legal.append(">Contig1\n");
		legal.append("GCAGC\n");
		legal.append("ATGTA\n");
		legal.append("CT\n");
		
		legal.append(">Contig2\n");
		legal.append("AACGT\n");
		legal.append("CTG\n");

		assertEquals(legal.toString(), stringWriter.getBuffer().toString());
	}
	
}
