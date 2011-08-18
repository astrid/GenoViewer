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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.astrid.indexing;

import hu.astrid.contig.Contig;
import hu.astrid.contig.ContigImplementationType;
import hu.astrid.contig.reader.ContigReaderFactory;
import hu.astrid.contig.reader.ContigReaderType;
import hu.astrid.core.Coder;
import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;
import hu.astrid.core.Sequence;
import hu.astrid.utility.ContigConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Balázs
 */
public class MapIndexer2Test {

	public MapIndexer2Test() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of get method, of class MapIndexer2.
	 */
	@Test
	public void testGet() {
		System.out.println("get");
		BufferedReader reader = new BufferedReader(new StringReader(">test contig 1\n" +
				"GACTAGCACTGACCTAGACTGACTAGCACTGACTAGCACTG"));
		//		 G2123231121210232212121232311212123231121
		Contig<Color> contig = null;
		try {
			contig = ContigConverter.convert(ContigReaderFactory.createContigReader(ContigImplementationType.BYTE, ContigReaderType.FASTA, new Coder<Nucleotide>(Nucleotide.values()), reader).loadContig());
		} catch (IOException ex) {
			Logger.getLogger(MapIndexer2Test.class.getName()).log(Level.SEVERE, null, ex);
		}
		MapIndexer2<Color> instance = new MapIndexer2<Color>(10);
		instance.add(contig);
		assertArrayEquals(new int[] {0, 20, 30}, instance.get(new Sequence<Color>(Arrays.asList(Color.C2, Color.C1, Color.C2, Color.C3, Color.C2, Color.C3, Color.C1, Color.C1, Color.C2, Color.C1))).get(0).getPositions());
	}

	/**
	 * Test of add method, of class MapIndexer2.
	 */
	@Test
	public void testAdd() {
		System.out.println("add");
		BufferedReader reader = new BufferedReader(new StringReader(">test contig 1\n" +
				"GACTAGCACTGACCTAGACT"));
		//		 G2123231121210232212
		Contig<Color> contig = null;
		try {
			contig = ContigConverter.convert(ContigReaderFactory.createContigReader(ContigImplementationType.BYTE, ContigReaderType.FASTA, new Coder<Nucleotide>(Nucleotide.values()), reader).loadContig());
		} catch (IOException ex) {
			Logger.getLogger(MapIndexer2Test.class.getName()).log(Level.SEVERE, null, ex);
		}
		MapIndexer2<Color> instance = new MapIndexer2<Color>(10);
		instance.add(contig);
		assertArrayEquals(new int[] {0}, instance.get(new Sequence<Color>(Arrays.asList(Color.C2, Color.C1, Color.C2, Color.C3, Color.C2, Color.C3, Color.C1, Color.C1, Color.C2, Color.C1))).get(0).getPositions());
		assertArrayEquals(new int[] {4}, instance.get(new Sequence<Color>(Arrays.asList(Color.C2, Color.C3, Color.C1, Color.C1, Color.C2, Color.C1, Color.C2, Color.C1, Color.C0, Color.C2))).get(0).getPositions());
		assertArrayEquals(new int[] {8}, instance.get(new Sequence<Color>(Arrays.asList(Color.C2, Color.C1, Color.C2, Color.C1, Color.C0, Color.C2, Color.C3, Color.C2, Color.C2, Color.C1))).get(0).getPositions());
	}
}
