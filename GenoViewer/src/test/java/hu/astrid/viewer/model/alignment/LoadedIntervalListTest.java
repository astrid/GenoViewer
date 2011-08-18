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

package hu.astrid.viewer.model.alignment;

import hu.astrid.viewer.model.alignment.Interval;
import hu.astrid.viewer.model.alignment.LoadedIntervalList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Szuni
 */
public class LoadedIntervalListTest {


	private LoadedIntervalList list;

    public LoadedIntervalListTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
		list = new LoadedIntervalList(500);
    }

    @After
    public void tearDown() {
		list = null;
    }

    @Test
    public void testAdd() {
		list.init(10, 15);
		list.add(1, 10);
		assertTrue(list.contains(1, 15));
		assertEquals(new Interval(1, 15), list.first.interval);
		list.add(100, 115);
		assertEquals(list.first.next.interval, new Interval(100, 115));
		list.add(115, 180);
		assertEquals(2, list.size());
		assertEquals(new Interval(100, 180), list.first.next.interval);
		assertTrue(list.contains(100,120));
		assertFalse(list.contains(180,220));
		assertFalse(list.contains(99,120));
		list.add(15, 100);
		assertEquals(1, list.size());
		assertEquals(new Interval(1, 180), list.first.interval);
	}

	@Test
    public void testContains() {
		list.init(10, 15);
		list.add(1, 10);
		assertTrue(list.contains(1, 15));
		list.add(100, 115);
		list.add(115, 180);
		assertTrue(list.contains(100,120));
		assertFalse(list.contains(80,220));
		assertFalse(list.contains(99,120));
		list.add(15, 100);
		assertTrue(list.contains(1,180));
		assertTrue(list.contains(80,120));
		assertFalse(list.contains(0,10));
		assertFalse(list.contains(180,181));

	}

	@Test
    public void testContainingInterval() {
		list.init(10, 15);
		assertEquals(new Interval(10, 15), list.getContainigInterval(10));
		assertEquals(new Interval(10, 15), list.getContainigInterval(14));
		assertEquals(null, list.getContainigInterval(15));
		list.add(100, 115);
		list.add(115, 180);
		assertEquals(new Interval(10, 15), list.getContainigInterval(11));
		assertEquals(new Interval(100, 180), list.getContainigInterval(105));
		assertEquals(new Interval(100, 180), list.getContainigInterval(160));
		assertEquals(null, list.getContainigInterval(70));
	}

	@Test(expected=IllegalArgumentException.class)
	public void overlap1() {
		list.init(60, 200);
		list.add(30, 100);
	}

	@Test(expected=IllegalArgumentException.class)
	public void overlap2() {
		list.init(60, 200);
		list.add(199, 203);
	}

	@Test(expected = IllegalArgumentException.class)
	public void overlap3() {
		list.init(60, 200);
		list.add(300, 500);
		list.add(320, 400);
	}

	@Test(expected = IllegalArgumentException.class)
	public void incorrectInterval() {
		list.init(200, 200);
	}

}