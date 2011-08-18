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
package hu.astrid.viewer.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hu.astrid.io.FastaReader;
import hu.astrid.read.FastaRead;
import hu.astrid.viewer.reader.FastaRandomReader.InvalidFastaFileException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author Szuni
 */
@RunWith(Parameterized.class)
public class FastaRandomReaderTest {
	
	private static final String RESOURCES_DIR = System.getProperty("viewer.testfiles.dir");

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private String filePath;
	private FastaRandomReader fastaReader;

	public FastaRandomReaderTest(String path) {
		filePath = path;
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws IOException, InvalidFastaFileException {
		fastaReader = new FastaRandomReader(filePath);
	}

	@After
	public void tearDown() throws IOException {
		fastaReader.closeFile();
	}

	//temp könyvtár összes .fasta fájljára tesztel
	@Parameters
	public static List<Object[]> filesToTest() {
		File resourcesDir = new File(RESOURCES_DIR);
		String[] fastaFiles = resourcesDir.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return name.endsWith(".fasta");
			}
		});
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		for (String fileName : fastaFiles) {
			list.add(new String[]{ RESOURCES_DIR + "/" + fileName });
		}
		return list;
	}

	/**
	 * Összehasonlítja a fájl kontigjainak pozícionált beolvasását az eredeti fájl tartalmával
	 * @throws Exception
	 */
	@Test
	public void testRead1() throws Exception {
		System.out.println("Reader által olvasott " + filePath + " fájl kontigjainak összehasonlítása az eredetivel");
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line = reader.readLine();
		long pos = -LINE_SEPARATOR.length();
		int positionToLoad = 0;
		int contigNumber = 0;

		CharSequence readedContent = "";
		int readedContentIndex = 0, lineIndex = 0, loaded = 0;

		assertTrue("Első kontig beolvasása esetén eltérés van", fastaReader.getNumberOfContigs() > 0 ? line.contains(">") : line == null);

		while (true) {
			//Beolvasás, ha valami elfogyott
			if (lineIndex == line.length()) {
				line = reader.readLine();
				lineIndex = 0;
				pos += LINE_SEPARATOR.length();
			}
			if (readedContentIndex == readedContent.length()) {
				positionToLoad += readedContent.length();
				readedContent = fastaReader.load(positionToLoad, contigNumber).toString();

				readedContentIndex = 0;
			}

			if (line != null || loaded != -1) {
				break;
			}

			//Contig id-t a fasta reader nem ad vissza, ennek léptetése
			if (line.charAt(0) == '>') {
				positionToLoad = 0;
				++contigNumber;
				pos = +line.length() + LINE_SEPARATOR.length();
				line = reader.readLine();
				readedContent = fastaReader.load(positionToLoad, contigNumber).toString();
			} else {
				/*Összehasonlítás karakterenként*/
				while (lineIndex < line.length() && readedContentIndex < readedContent.length()) {
					char expected = line.charAt(lineIndex++);
					char actual = readedContent.charAt(readedContentIndex++);
					assertEquals("Eltérés az eredeti fájl " + pos + " helyén " + contigNumber + " kontigban. Várt karakter :'" + expected + "', kapott karakter:'" + actual + "'.", expected, actual);
					pos++;
				}
			}
		}
		assertTrue("Beolvasásott kontigok hossza nem egyezik meg", lineIndex == 0 && readedContentIndex == 0);
		System.out.println("OK");
	}

	/**
	 * Összehasonlítja a fájl pozícionált beolvasását az eredeti fájl tartalmával
	 * @throws Exception
	 */
	@Test
	public void testRead2() throws Exception {
		System.out.println("Reader által olvasott " + filePath + " fájl minden egyes pozíciójának összehasonlítása az eredetivel");
		FastaReader reader = new FastaReader(new BufferedReader(new FileReader(filePath)));
		FastaRead read = reader.readNext();


		int contig = 0;
		while (read != null) {
			for (int i = 0; i < read.size(); ++i) {
				fastaReader.load(i, contig);
				String expected = read.toString().substring(i).toUpperCase();
				String actual = fastaReader.load(i, contig).toString();
				assertEquals("Eltérés a " + contig + " contig " + i + ". pozíció olvasásánál", expected.substring(0, Math.min(5, expected.length())), actual.substring(0, Math.min(5, actual.length())));
			}
			read = reader.readNext();
			contig++;
		}

		System.out.println("OK");
	}

//    /**
//     * Tesztelés üres pufferre, olvasás feltöltés nélkül.
//     * @throws Exception
//     */
//    @Test(expected = FastaRandomReader.EmptyBufferException.class)
//    public void testEmptyBuffer1() throws Exception{
//        System.out.println("Olvasás "+filePath+" fájlból a puffer feltöltése nélkül - kivétel várható");
//
//        fastaReader.scanFile();
//
//        fastaReader.getLoadedContig();
//
//        System.out.println("Hiba: nem volt kivétel");
//    }
//    /**
//     * Úgy próbál olvasni a fájlból, hogy nem ismeri a contigok  kezdeteinek helyét
//     * @throws Exception
//     */
//    @Test(expected = FastaRandomReader.MissingContigStartIndicesException.class)
//    public void testNotIndexedFileRead() throws Exception{
//        System.out.println("Olvasás "+filePath+" fájlból contigok kezdőindexeinek ismerete nélkül - kivétel várható");
//
//        fastaReader.load(0, 0);
//
//        System.out.println("Hiba: nem volt kivétel");
//    }
	/**
	 * Olyan contigból próbál olvasni, amit már nem tartalmaz a fájl
	 * @throws Exception
	 */
	@Test(expected = FastaRandomReader.ContigIndexOutOfBoundsException.class)
	public void testHighContigIndex() throws Exception {
		System.out.println("Olvasás " + filePath + " fájlból a tartalmazott contigok számánál nagyobb contigból - kivétel várható");

		try {
			fastaReader.load(0, fastaReader.getNumberOfContigs());
		} catch (FastaRandomReader.ContigIndexOutOfBoundsException ex) {
			System.out.println("OK");
			throw ex;
		}
		System.out.println("Hiba: nem volt kivétel");
	}

	/**
	 * Olyan pozícióról próbál olvasni, amit már nem tartalmaz a contig
	 * @throws Exception
	 */
	@Test(expected = FastaRandomReader.ContigPositionOutOfBoundsException.class)
	public void testHighPosition() throws Exception {
		System.out.println("Olvasás " + filePath + " fájlból a contig hosszánál nagyobb pozícióról - kivétel várható");
		try {
			fastaReader.load(fastaReader.getContigLength(0), 0);
		} catch (FastaRandomReader.ContigPositionOutOfBoundsException ex) {
			System.out.println("OK");
			throw ex;
		}
		System.out.println("Hiba: nem volt kivétel");
	}

	/**
	 * Lezárt fájlból próbál olvasni
	 * @throws Exception
	 */
	@Test(expected = IOException.class)
	public void testChannelClose() throws Exception {
		System.out.println("Olvasás " + filePath + " fájlból lezárás után - kivétel várható");

		fastaReader.closeFile();
		try {
			fastaReader.load(0, 0);
		} catch (IOException ex) {
			System.out.println("OK");
			throw ex;
		}

		System.out.println("Hiba: nem volt kivétel");
	}
}
