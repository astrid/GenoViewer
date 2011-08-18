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

/**
 *
 */
package hu.astrid.utility;

import static hu.astrid.core.Color.C0;
import static hu.astrid.core.Color.C1;
import static hu.astrid.core.Color.C2;
import static hu.astrid.core.Color.C3;
import static hu.astrid.core.Nucleotide.A;
import static hu.astrid.core.Nucleotide.C;
import static hu.astrid.core.Nucleotide.G;
import static hu.astrid.core.Nucleotide.T;
import hu.astrid.core.Color;
import hu.astrid.core.Nucleotide;
import hu.astrid.read.CsRead;
import hu.astrid.read.FastaRead;
import hu.astrid.utility.Splitter;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Astrid Research Inc. Author: zsdoma Created: 2009.12.22.
 */
public class SplitterTest {

    @Before
    public void setUp() throws Exception {
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testSplit() {
        String sequence = "GTTCTTAGCTATTACTGTAC";

        List<List<Nucleotide>> expected = Arrays.asList(
                Arrays.asList(G, T, T, C, T, T, A, G, C, T),
                Arrays.asList(A, T, T, A, C, T, G, T, A, C));

        List<List<Nucleotide>> actual = new Splitter().split(new FastaRead("seq1", sequence), 10);

        Assert.assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testSplit2() {
        String sequence = "TTGTCAGGTGACTGGATGCCCA";

        List<List<Nucleotide>> expected = Arrays.asList(Arrays.asList(T, T, G, T, C),
                Arrays.asList(A, G, G, T, G),
                Arrays.asList(A, C, T, G, G),
                Arrays.asList(A, T, G, C, C),
                Arrays.asList(C, A));

        List<List<Nucleotide>> actual = new Splitter().split(new FastaRead("seq1",
        		sequence), 5);

        Assert.assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testSplitCs() {
        String sequence = "T0232111010";

        List<List<Color>> expected = Arrays.asList( Arrays.asList(C2, C3, C2, C1, C1),
                Arrays.asList(C1, C0, C1, C0));

        List<List<Color>> actual = new Splitter().split(new CsRead("seq1",
        		sequence), 5);

        Assert.assertEquals(expected, actual);
    }
}