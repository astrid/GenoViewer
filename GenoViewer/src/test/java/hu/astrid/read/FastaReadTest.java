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

package hu.astrid.read;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import hu.astrid.read.FastaRead;

import org.junit.Test;

/**
 * Astrid Research
 * Author: mkiss
 * Created: Dec 14
 */
public class FastaReadTest {
	@Test
	public void testConstructor1() {
		FastaRead read = new FastaRead("gi", "ATTCGGCTA");
		assertEquals("gi", read.getId());
		assertEquals("ATTCGGCTA", read.toString());
	}

	@Test
	public void testConstructor2() {
		FastaRead read = new FastaRead("gi", "GGTTTAAACC");
		assertEquals("gi", read.getId());
		assertEquals("GGTTTAAACC", read.toString());
	}

	@Test
	public void testConstructor_unknown() {
		try {
			@SuppressWarnings({"UnusedDeclaration"})
            FastaRead read = new FastaRead("gi", ".GGTT");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// this is what we expect...
		}
	}

	@Test
	public void testIllegalString() {
		try {
			@SuppressWarnings({"UnusedDeclaration"})
            FastaRead read = new FastaRead("gi", "G3100232");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// this is what we expect...
		}
	}

}