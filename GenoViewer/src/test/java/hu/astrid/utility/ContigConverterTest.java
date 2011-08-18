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

import static hu.astrid.core.Color.C2;
import static hu.astrid.core.Color.C3;
import hu.astrid.contig.Contig;
import hu.astrid.contig.ContigImplementationType;
import hu.astrid.contig.reader.ContigReader;
import hu.astrid.contig.reader.ContigReaderFactory;
import hu.astrid.contig.reader.ContigReaderType;
import hu.astrid.core.Coder;
import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

/**
 * <b>Astrid Research Inc.</b><br>
 * Project: <b>NGSequencingCommon</b><br>
 * Type: <b>ContigConverterTest</b><br>
 * Created: 2010.03.23.<br>
 * @author zsdoma
 */
public class ContigConverterTest {

	@Test
	public void testConvert() throws IOException {
		StringReader stringReader = new StringReader(
				">test_contig\n" +
				"CTAGCT\n"
				);
		ContigReader<Nucleotide> contigReader = ContigReaderFactory.createContigReader(
				ContigImplementationType.BYTE,
				ContigReaderType.FASTA, 
				new Coder<Nucleotide>(new Nucleotide[] {Nucleotide.A, Nucleotide.C, Nucleotide.G, Nucleotide.T}),
				new BufferedReader(stringReader));
		
		Contig<Nucleotide> nucleotideContig = contigReader.loadContig();
		
		Contig<Color> colorContig = ContigConverter.convert(nucleotideContig);
		
		Assert.assertEquals("test_contig", colorContig.getId());
		Assert.assertEquals(Arrays.asList(C2, C3, C2, C3, C2), colorContig.getSequence());
	}

}
