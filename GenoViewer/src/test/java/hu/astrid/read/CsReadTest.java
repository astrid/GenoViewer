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

import hu.astrid.core.Nucleotide;
import hu.astrid.read.CsRead;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Astrid Research
 * Author: balint
 * Created: Dec 11, 2009 4:11:53 PM
 */
public class CsReadTest {
    @Test
    public void testConstructor1() {
        CsRead read = new CsRead("rid", "T02121");
        assertEquals("T02121", read.toString());
        assertEquals(Nucleotide.T, read.getAdaptor());
        assertEquals(Nucleotide.T, read.getFirstNucleotide());
        assertEquals("rid", read.getId());
    }

    @Test
    public void testConstructor2() {
        CsRead read = new CsRead("rid", "G3100232");
        assertEquals("G3100232", read.toString());
        assertEquals(Nucleotide.G, read.getAdaptor());
        assertEquals(Nucleotide.C, read.getFirstNucleotide());
        assertEquals("rid", read.getId());
    }

    @Test
    public void testConstructor_unknown() {
        try {
            @SuppressWarnings({"UnusedDeclaration"})
            CsRead read = new CsRead("rid", ".3100235");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // this is what we expect...
        }
    }

    @Test
    public void testToString() {
        CsRead read = new CsRead("rid", "G3100232");
        assertEquals("G3100232", read.toString());
    }

}
