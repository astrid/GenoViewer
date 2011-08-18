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

package hu.astrid.indexing;

import static hu.astrid.core.Nucleotide.A;
import static hu.astrid.core.Nucleotide.G;
import static hu.astrid.core.Nucleotide.T;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import hu.astrid.contig.Contig;
import hu.astrid.core.Nucleotide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Test;

/**
 * Astrid Research Author: balint, mkiss Created: Dec 15, 2009 2:54:28 PM
 */
@Deprecated
public class GenomeIndexerTest {

	@SuppressWarnings("unchecked")
	private void doTest(IndexerType indexerType) {
		Contig<Nucleotide> contigMock = EasyMock.createMock(Contig.class);
		
		expect(contigMock.size()).andReturn(16);
		expect(contigMock.getSequence(0, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A));
		expect(contigMock.getSequence(1, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A));
		expect(contigMock.getSequence(2, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A));
		expect(contigMock.getSequence(3, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, T));
		expect(contigMock.getSequence(4, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, T, T));
		expect(contigMock.getSequence(5, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, T, T, T));
		expect(contigMock.containsNonconcreteLetter(0, 11)).andReturn(false);
		expect(contigMock.containsNonconcreteLetter(1, 11)).andReturn(false);
		expect(contigMock.containsNonconcreteLetter(2, 11)).andReturn(false);
		expect(contigMock.containsNonconcreteLetter(3, 11)).andReturn(false);
		expect(contigMock.containsNonconcreteLetter(4, 11)).andReturn(false);
		expect(contigMock.containsNonconcreteLetter(5, 11)).andReturn(false);
		replay(contigMock);
		
		GenomeMap<Nucleotide> genomeMap = GenomeIndexer.<Nucleotide>createMap(indexerType, contigMock, 11, 32);

		List<Nucleotide> nucList = new ArrayList<Nucleotide>(Arrays.asList(A, A,
			A, A, A, A, A, A, A, A, A));

		Assert.assertEquals("[0, 1, 2]", genomeMap.get(nucList).toString());

		nucList = new ArrayList<Nucleotide>(Arrays.asList(A, A, A, A, A, A, A,
			A, A, A, T));
		Assert.assertEquals("[3]", genomeMap.get(nucList).toString());

		nucList = new ArrayList<Nucleotide>(Arrays.asList(A, G, A, A, A, G, A,
			A, A, A, T));
		Assert.assertEquals("[]", genomeMap.get(nucList).toString());
	    }

	    @Test
	    public void testArrayIndexer() throws IOException {
			doTest(IndexerType.ARRAY_INDEXER);
	    }

	    @Test
	    public void testListIndexer() throws IOException {
			doTest(IndexerType.LIST_INDEXER);
	    }

}
