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
import hu.astrid.core.Sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class MapIndexerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMapIndexer() {
		Contig<Nucleotide> contigMock1 = EasyMock.createMock(Contig.class);
		
		expect(contigMock1.size()).andReturn(16).anyTimes();
		expect(contigMock1.getSequence(0, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A)).anyTimes();
		expect(contigMock1.getSequence(1, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A)).anyTimes();
		expect(contigMock1.getSequence(2, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A)).anyTimes();
		expect(contigMock1.getSequence(3, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, T)).anyTimes();
		expect(contigMock1.getSequence(4, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, T, T)).anyTimes();
		expect(contigMock1.getSequence(5, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, T, T, T)).anyTimes();
		expect(contigMock1.containsNonconcreteLetter(0, 11)).andReturn(false);
		expect(contigMock1.containsNonconcreteLetter(1, 11)).andReturn(false);
		expect(contigMock1.containsNonconcreteLetter(2, 11)).andReturn(false);
		expect(contigMock1.containsNonconcreteLetter(3, 11)).andReturn(false);
		expect(contigMock1.containsNonconcreteLetter(4, 11)).andReturn(false);
		expect(contigMock1.containsNonconcreteLetter(5, 11)).andReturn(false);
		expect(contigMock1.getId()).andReturn("generate");
		replay(contigMock1);

		Contig<Nucleotide> contigMock2 = EasyMock.createMock(Contig.class);
		
		expect(contigMock2.size()).andReturn(17).anyTimes();
		expect(contigMock2.getSequence(0, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A)).anyTimes();
		expect(contigMock2.getSequence(1, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A)).anyTimes();
		expect(contigMock2.getSequence(2, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, A)).anyTimes();
		expect(contigMock2.getSequence(3, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, A, T)).anyTimes();
		expect(contigMock2.getSequence(4, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, A, T, T)).anyTimes();
		expect(contigMock2.getSequence(5, 11)).andReturn(
			Arrays.asList(A, A, A, A, A, A, A, A, T, T, T)).anyTimes();
		expect(contigMock2.getSequence(6, 11)).andReturn(
				Arrays.asList(A, A, A, A, A, A, A, T, T, T, T)).anyTimes();
		expect(contigMock2.containsNonconcreteLetter(0, 11)).andReturn(false);
		expect(contigMock2.containsNonconcreteLetter(1, 11)).andReturn(false);
		expect(contigMock2.containsNonconcreteLetter(2, 11)).andReturn(false);
		expect(contigMock2.containsNonconcreteLetter(3, 11)).andReturn(false);
		expect(contigMock2.containsNonconcreteLetter(4, 11)).andReturn(false);
		expect(contigMock2.containsNonconcreteLetter(5, 11)).andReturn(false);
		expect(contigMock2.containsNonconcreteLetter(6, 11)).andReturn(false);
		expect(contigMock2.getId()).andReturn("generate");
		replay(contigMock2);

		
		MapIndexer<Nucleotide> mapIndexer = new MapIndexer(11);
		mapIndexer.add(contigMock1);
		mapIndexer.add(contigMock2);
		
		//Test first
		List<Nucleotide> nucList = new ArrayList<Nucleotide>(
				Arrays.asList(A, A,	A, A, A, A, A, A, A, A, A));

		Assert.assertEquals(
				"[[id=0;positions=[0, 1, 2];size=3;current=3], " +
				"[id=1;positions=[0, 1, 2];size=3;current=3]]",
				mapIndexer.get(new Sequence<Nucleotide>(nucList)).toString());

		//Test second
		nucList = new ArrayList<Nucleotide>(
				Arrays.asList(A, A, A, A, A, A, A, A, A, A, T));
		Assert.assertEquals(
				"[[id=0;positions=[3];size=1;current=1], " +
				"[id=1;positions=[3];size=1;current=1]]",
				mapIndexer.get(new Sequence<Nucleotide>(nucList)).toString());

		//Test third
		nucList = new ArrayList<Nucleotide>(
				Arrays.asList(A, G, A, A, A, G, A, A, A, A, T));
		Assert.assertEquals(
				"[]",
				mapIndexer.get(new Sequence<Nucleotide>(nucList)).toString());
		
		//Test fourth
		nucList = new ArrayList<Nucleotide>(
				Arrays.asList(A, A, A, A, A, A, A, T, T, T, T));
		Assert.assertEquals(
				"[[id=1;positions=[6];size=1;current=1]]",
				mapIndexer.get(new Sequence<Nucleotide>(nucList)).toString());
	}

}
