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

package hu.astrid.mapping.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BamUtilTest {

	public BamUtilTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testConvertToShort() {
		byte[] t1 = new byte[]{56, 50};
		short s1 = BamUtil.toShort(t1);
		assertEquals(12856, s1);

		byte[] t2 = new byte[]{127, 108};
		short s2 = BamUtil.toShort(t2);
		assertEquals(27775, s2);
	}

	@Test
	public void testConvertToInt() {
		byte[] t1 = new byte[]{56, 50, 87, 96};
		int i1 = BamUtil.toInt(t1);
		assertEquals(1616327224, i1);

		byte[] t2 = new byte[]{106, 15, 127, 75};
		int i2 = BamUtil.toInt(t2);
		assertEquals(1266618218, i2);
	}

	@Test
	public void testConvertToByteArray_Int() {
		byte[] t1 = new byte[]{0, 0, 0, 0};
		byte[] i1 = BamUtil.toByteArray(0);
		assertEquals(Arrays.toString(t1), Arrays.toString(i1));

		byte[] t2 = new byte[]{1, 0, 0, 0};
		byte[] i2 = BamUtil.toByteArray(1);
		assertEquals(Arrays.toString(t2), Arrays.toString(i2));

		byte[] t3 = new byte[]{0, 0, 1, 0};
		byte[] i3 = BamUtil.toByteArray(65536);
		assertEquals(Arrays.toString(t3), Arrays.toString(i3));

		byte[] t4 = new byte[]{56, 50, 87, 96};
		byte[] i4 = BamUtil.toByteArray(1616327224);
		assertEquals(Arrays.toString(t4), Arrays.toString(i4));

		byte[] t5 = new byte[]{106, 15, 127, 75};
		byte[] i5 = BamUtil.toByteArray(1266618218);
		assertEquals(Arrays.toString(t5), Arrays.toString(i5));
	}

	@Test
	public void testConvertToByteArray_Short() {

		byte[] t1 = new byte[]{0, 0};
		byte[] i1 = BamUtil.toByteArray((short) 0);
		assertEquals(Arrays.toString(t1), Arrays.toString(i1));

		byte[] t2 = new byte[]{1, 0};
		byte[] i2 = BamUtil.toByteArray((short) 1);
		assertEquals(Arrays.toString(t2), Arrays.toString(i2));

		byte[] t3 = new byte[]{0, 1};
		byte[] i3 = BamUtil.toByteArray((short) 256);
		assertEquals(Arrays.toString(t3), Arrays.toString(i3));

		byte[] t4 = new byte[]{56, 50};
		byte[] i4 = BamUtil.toByteArray((short) 12856);
		assertEquals(Arrays.toString(t4), Arrays.toString(i4));

		byte[] t5 = new byte[]{127, 108};
		byte[] i5 = BamUtil.toByteArray((short) 27775);
		assertEquals(Arrays.toString(t5), Arrays.toString(i5));
	}

	@Test
	public void testConvertToByteArray_String() {

		byte[] t1 = new byte[]{66, 65, 77};
		byte[] s1 = BamUtil.toByteArray("BAM".toCharArray());
		assertEquals(Arrays.toString(t1), Arrays.toString(s1));

	}

	@Test
	public void testRegionToBins() {

		List<Integer> l1 = Arrays.asList(0, 1, 9, 73, 585, 4681);

		List<Integer> t1 = BamUtil.regionToBins(1000, 2000);
		assertEquals(l1, t1);

		List<Integer> l2 = Arrays.asList(0, 1, 9, 73, 585, 4681, 4682);

		List<Integer> t2 = BamUtil.regionToBins(16000, 17000);
		assertEquals(l2, t2);

		List<Integer> l3 = Arrays.asList(0, 1, 9, 73, 585, 586, 4681, 4682, 4683, 4684, 4685, 4686, 4687, 4688, 4689);

		List<Integer> t3 = BamUtil.regionToBins(16000, 138000);
		assertEquals(l3, t3);
	}
}
