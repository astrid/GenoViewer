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
package hu.astrid.viewer.util;

import hu.astrid.viewer.Viewer;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * TODO create component tests for testing stop event
 * @author onagy
 */
public class InDelManagerTest {

	private static final String RESOURCES_DIR = System.getProperty("viewer.testfiles.dir");

	public InDelManagerTest() {
		try {
			Viewer.getController().loadBamFile(new File(RESOURCES_DIR + "/" + "test.bam"));
			Viewer.getReadModel().setActAlignmentRefNameIndex(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		Viewer.init();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		//Empty
	}

	@Before
	public void setUp() {
		//Empty
	}

	@After
	public void tearDown() {
		//Empty
	}

	@Test
	public void testScenario1() {
		InDelManager instance = new InDelManager(1, Viewer.getController().lastReadEndPos());
		assertTrue(instance.hasNextDeletion(0));
		assertEquals(151, instance.getNextDeletion(0));
		assertFalse(instance.hasPrevDeletion(0));
		assertFalse(instance.hasPrevInsertion(0));
		assertTrue(instance.hasNextInsertion(0));
		assertEquals(106, instance.getNextInsertion(0));
		assertTrue(instance.hasPrevDeletion(151));
		assertEquals(151, instance.getPrevDeletion(151));
		assertTrue(instance.hasNextDeletion(151));
		assertEquals(151, instance.getNextDeletion(151));
		assertFalse(instance.hasPrevDeletion(150));
		assertTrue(instance.hasNextDeletion(152));
		assertEquals(158, instance.getNextDeletion(152));
		assertTrue(instance.hasPrevInsertion(151));
		assertEquals(111, instance.getPrevInsertion(151));
		assertTrue(instance.hasPrevInsertion(111));
		assertEquals(111, instance.getPrevInsertion(111));
		assertTrue(instance.hasNextInsertion(111));
		assertEquals(111, instance.getNextInsertion(111));
		assertTrue(instance.hasPrevInsertion(110));
		assertEquals(106, instance.getPrevInsertion(110));
		assertFalse(instance.hasPrevDeletion(110));
		assertTrue(instance.hasNextDeletion(152));
		assertEquals(158, instance.getNextDeletion(152));
		assertFalse(instance.hasNextInsertion(152));
		assertTrue(instance.hasNextDeletion(158));
		assertEquals(158, instance.getNextDeletion(152));
		assertFalse(instance.hasNextDeletion(159));
		assertFalse(instance.hasNextDeletion(165));
		assertTrue(instance.hasNextDeletion(155));
		assertFalse(instance.hasNextDeletion(160));
		assertFalse(instance.hasNextDeletion(180));
	}

	/**
	 * Test of setMinCoverage method, of class InDelManager.
	 */
	@Test(expected = IllegalStateException.class)
	public void testSetMinCoverage() {
		// throw new
		// IllegalStateException("Created exception only as placeholder");
		InDelManager instance = new InDelManager(1, Viewer.getController().lastReadEndPos());
		InDelManager expected = null;
		// expecting IllegalStateException
		instance = new InDelManager(1, Viewer.getController().lastReadEndPos());
		expected = instance.setMinCoverage(0);
		assertEquals(expected, null);
		// expecting IllegalStateException
		instance = new InDelManager(1, Viewer.getController().lastReadEndPos());
		expected = instance.setMinCoverage(-1);
		assertNull(expected);
		// expecting IllegalStateException
		instance = new InDelManager(-1000, Viewer.getController().lastReadEndPos());
		assertEquals(0, instance.getMinCoverage());
		expected = instance.setMinCoverage(-10);
		assertNull(expected);
		expected = instance.setMinCoverage(10);
		assertEquals(10, instance.getMinCoverage());
		instance = new InDelManager(0, Viewer.getController().lastReadEndPos());
		expected = instance.setMinCoverage(5);
		assertEquals(expected, instance);
		assertEquals(5, instance.getMinCoverage());
		instance = new InDelManager(1000, Viewer.getController().lastReadEndPos());
		assertEquals(1000, instance.getMinCoverage());
		expected = instance.setMinCoverage(15);
		assertEquals(expected, instance);
		assertEquals(15, instance.getMinCoverage());
	}

	@Test
	public void testBounds() {
		InDelManager instance = new InDelManager(1, Viewer.getController().lastReadEndPos());
		assertTrue(instance.hasPrevDeletion(Viewer.getController().lastReadEndPos()));
		assertTrue(instance.hasPrevInsertion(Viewer.getController().lastReadEndPos()));
		assertFalse(instance.hasNextDeletion(10000));
		assertFalse(instance.hasNextDeletion(100000));
		assertFalse(instance.hasNextDeletion(1000000));
		assertFalse(instance.hasNextInsertion(10000));
		assertFalse(instance.hasNextInsertion(100000));
		assertFalse(instance.hasNextInsertion(1000000));
		assertFalse(instance.hasPrevDeletion(0));
		assertFalse(instance.hasPrevDeletion(-1));
		assertFalse(instance.hasPrevDeletion(-100));
		assertFalse(instance.hasPrevDeletion(-100000));
		assertFalse(instance.hasPrevInsertion(0));
		assertFalse(instance.hasPrevInsertion(-1));
		assertFalse(instance.hasPrevInsertion(-100));
		assertFalse(instance.hasPrevInsertion(-100000));
	}

	@Test
	public void testCoverage() {
		InDelManager instance = new InDelManager(2, Viewer.getController().lastReadEndPos());
		assertFalse(instance.hasNextDeletion(0));
		assertFalse(instance.hasNextInsertion(0));
		assertFalse(instance.hasPrevDeletion(Viewer.getController().lastReadEndPos()));
		assertFalse(instance.hasPrevInsertion(Viewer.getController().lastReadEndPos()));
		instance = new InDelManager(1, Viewer.getController().lastReadEndPos());
		assertTrue(instance.hasNextDeletion(0));
		assertTrue(instance.hasNextInsertion(0));
		assertTrue(instance.hasPrevDeletion(Viewer.getController().lastReadEndPos()));
		assertTrue(instance.hasPrevInsertion(Viewer.getController().lastReadEndPos()));
		instance = new InDelManager(50, Viewer.getController().lastReadEndPos());
		assertFalse(instance.hasNextDeletion(0));
		assertFalse(instance.hasNextInsertion(0));
		assertFalse(instance.hasPrevDeletion(Viewer.getController().lastReadEndPos()));
		assertFalse(instance.hasPrevInsertion(Viewer.getController().lastReadEndPos()));
		instance.setMinCoverage(1);
		assertTrue(instance.hasNextDeletion(0));
		assertTrue(instance.hasNextInsertion(0));
		assertTrue(instance.hasPrevDeletion(Viewer.getController().lastReadEndPos()));
		assertTrue(instance.hasPrevInsertion(Viewer.getController().lastReadEndPos()));
	}
}
