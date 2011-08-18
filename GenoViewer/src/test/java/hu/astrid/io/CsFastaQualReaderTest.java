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
import static org.junit.Assert.fail;
import org.junit.Test;

import hu.astrid.read.CsRead;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: bds
 * Date: Dec 11, 2009
 * Time: 7:54:16 PM
 */
public class CsFastaQualReaderTest {
    private BufferedReader seqReader = new BufferedReader(new StringReader(
            "#\n" +
                    "#\n" +
                    ">97_2040_1850_F3\n" +
                    "A000000001\n" +
                    ">97_2040_1850_F4\n" +
                    "A0000.0010\n" +
                    ">97_2040_1850_F5\n" +
                    "A000000100\n" +
                    ">97_2040_1850_F6\n" +
                    "A000001000\n"
    ));
    private BufferedReader qualReader = new BufferedReader(new StringReader(
            "#\n" +
                    "#\n" +
                    ">97_2040_1850_F3\n" +
                    "0 1 2 3 4 5 6 7 8\n" +
                    ">97_2040_1850_F4\n" +
                    "10 11 12 13 14 15 16 17 18\n" +
                    ">97_2040_1850_F5\n" +
                    "20 21 22 23 24 25 26 27 28\n" +
                    ">97_2040_1850_F6\n" +
                    "30 31 32 33 34 35 36 37 38\n"
    ));

    @Test
    public void testReader_dot() throws IOException {
        BufferedReader seqReader = new BufferedReader(new StringReader(
                ">97_2040_1850_F3\n" +
                        "A0.0000001\n"
        ));
        BufferedReader qualReader = new BufferedReader(new StringReader(
                ">97_2040_1850_F3\n" +
                        "38 36 26 33 41 26 24 33 28 31\n"
        ));
        CsFastaQualReader reader = CsFastaQualReader.getInstance(seqReader, qualReader);

        CsRead read = reader.readNext();
        assertNull(read);
    }

    @Test
    public void testReader() throws IOException {

        CsFastaQualReader reader = CsFastaQualReader.getInstance(seqReader, qualReader);

        CsRead read = reader.readNext();
        assertEquals("A000000001", read.toString());
        assertEquals("97_2040_1850_F3", read.getId());
        assertEquals(0, read.getQuality(0));
        assertEquals(1, read.getQuality(1));

        read = reader.readNext();
        assertEquals("A000000100", read.toString());
        assertEquals("97_2040_1850_F5", read.getId());
        assertEquals(20, read.getQuality(0));
        assertEquals(21, read.getQuality(1));

        read = reader.readNext();
        assertEquals("A000001000", read.toString());
        assertEquals("97_2040_1850_F6", read.getId());
        assertEquals(30, read.getQuality(0));
        assertEquals(31, read.getQuality(1));
        assertEquals(38, read.getQuality(8));

        try {
            read.getQuality(10);
            fail("ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        assertNull(reader.readNext());
    }
}
