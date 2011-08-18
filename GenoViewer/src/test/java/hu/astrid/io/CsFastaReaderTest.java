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

package hu.astrid.io;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import hu.astrid.read.CsRead;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Astrid Research
 * Author: balint
 * Created: Dec 11, 2009 3:36:08 PM
 */

public class CsFastaReaderTest {
    @Test
    public void testReader_dot() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(
                ">ID1\n" +
                        ".000000001\n"
        ));
        CsFastaReader reader = new CsFastaReader(bufferedReader);

        CsRead read = reader.readNext();
        assertNull(read);
    }

    @Test
    public void testReader() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(
                "#abc\n" +
                        "#cde\n" +
                        ">ID1\n" +
                        "A000000001\n" +
                        ">ID2\n" +
                        "A0000.0010\n" +
                        ">ID3\n" +
                        "A000000100\n" +
                        ">ID4\n" +
                        "A000001000\n"
        ));

        CsFastaReader reader = new CsFastaReader(bufferedReader);

        CsRead read = reader.readNext();
        assertEquals("A000000001", read.toString());
        assertEquals("ID1", read.getId());

        read = reader.readNext();
        assertEquals("A000000100", read.toString());
        assertEquals("ID3", read.getId());

        read = reader.readNext();
        assertEquals("A000001000", read.toString());
        assertEquals("ID4", read.getId());

        assertNull(reader.readNext());
        
    }

}
