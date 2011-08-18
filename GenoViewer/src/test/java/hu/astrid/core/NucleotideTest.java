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

package hu.astrid.core;

import java.lang.IllegalArgumentException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author onagy
 */
public class NucleotideTest {

	public NucleotideTest() {
	}

	@Test
	public void testGetConcreteNucleotides() {
		Set<Nucleotide> nucleotideValues = Nucleotide.getConcreteNucleotides();

		assertTrue(nucleotideValues.contains(Nucleotide.A));
		assertTrue(nucleotideValues.contains(Nucleotide.C));
		assertTrue(nucleotideValues.contains(Nucleotide.G));
		assertTrue(nucleotideValues.contains(Nucleotide.T));
		assertFalse(nucleotideValues.contains(Nucleotide.N));
	}

	@Test
	public void testGetComplement() {
		assertEquals(Nucleotide.A, Nucleotide.T.getComplement());
		assertEquals(Nucleotide.T, Nucleotide.A.getComplement());

		assertEquals(Nucleotide.C, Nucleotide.G.getComplement());
		assertEquals(Nucleotide.G, Nucleotide.C.getComplement());

		assertNull(Nucleotide.N.getComplement());
	}

	@Test
	public void testValidIupacCodes() {

		Map<Character, Character> expectedCharToNucleotidePairs = new HashMap<Character, Character>() {

			{
				put('A', 'A');
				put('C', 'C');
				put('G', 'G');
				put('T', 'T');

				put('N', 'N');

				put('R', 'N');
				put('Y', 'N');
				put('M', 'N');
				put('K', 'N');
				put('W', 'N');
				put('S', 'N');
				put('B', 'N');
				put('D', 'N');
				put('H', 'N');
				put('V', 'N');
			}
		};

		for (Character key : expectedCharToNucleotidePairs.keySet()) {

			assertTrue(Nucleotide.isValid(key));
			assertEquals(expectedCharToNucleotidePairs.get(key).charValue(), Nucleotide.valueOf(key).charValue());
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidIupacCode() {

		Nucleotide.valueOf('E');
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidValueOfByte() {

		Nucleotide.valueOf((byte)5);
	}
}
