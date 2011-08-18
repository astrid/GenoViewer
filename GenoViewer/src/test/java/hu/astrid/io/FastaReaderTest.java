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
import org.junit.Before;

import hu.astrid.read.FastaRead;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Astrid Research
 * Author: mkiss
 * Created: Dec 14
 */

public class FastaReaderTest {
    private BufferedReader bufferedReader;

    @Before
    public void before() {
        bufferedReader = new BufferedReader(new StringReader(
                "#abc\n" +
                        "#cde\n" +
                        ">ID1\n" +
                        "ACCGGTT\n" +
                        "#333\n" +
                        ">ID2\n" +
                        "A.GGTTCC\n" +
                        ">ID3\n" +
                        "GGG..ATTCC\n" +
                        ">ID4\n" +
                        "AGGGGTTCTCCTGG\n" +
                        "#megjegyzés\n" +
                        ">ID5\n" +
                        "CCTTCATG\n" +
                        "AAATTAAT\n" +
                        ">ID6\n" +
                        "AAATTAGGAT\n" +
                        ">ID7\n" +
                        "CCTTTTCATG\n" +
                        "AAATTACCAT\n" +
                        "AAA\n"
        ));
    }

    @Test
    public void testReader1() throws IOException {

        FastaReader reader = new FastaReader(bufferedReader);

        FastaRead read = reader.readNext();
        assertEquals("ID1", read.getId());
        assertEquals("ACCGGTT", read.toString());

        read = reader.readNext();
        assertEquals("ID4", read.getId());
        assertEquals("AGGGGTTCTCCTGG", read.toString());

        read = reader.readNext();
        assertEquals("ID5", read.getId());
        assertEquals("CCTTCATGAAATTAAT", read.toString());

        read = reader.readNext();
        assertEquals("ID6", read.getId());
        assertEquals("AAATTAGGAT", read.toString());

        read = reader.readNext();
        assertEquals("ID7", read.getId());
        assertEquals("CCTTTTCATGAAATTACCATAAA", read.toString());

        assertNull(reader.readNext());
    }


    @Test
    public void testPushBack() throws Exception {
        BufferedReader sequenceReader = new BufferedReader(new StringReader(
                "ATCG\n" +
                        "GCTA\n" +
                        ">\n"));
        FastaReader reader = new FastaReader(sequenceReader);
        assertEquals("ATCG", reader.nextLine());
        reader.pushBack("ATCG");
        assertEquals("ATCG", reader.nextLine());
        assertEquals("GCTA", reader.nextLine());

    }

    @Test
    public void testGetSequence() throws Exception {

        BufferedReader sequenceReader = new BufferedReader(new StringReader(
                "ATCG\n" +
                        "GCTA\n" +
                        ">\n"));
        FastaReader reader = new FastaReader(sequenceReader);
        assertEquals("ATCGGCTA", reader.getSequence());
    }

    @Test
    public void testGetSequence_noseq() throws Exception {

        BufferedReader sequenceReader = new BufferedReader(new StringReader(
                        ">\n"));
        FastaReader reader = new FastaReader(sequenceReader);
        assertEquals("", reader.getSequence());
    }
}
