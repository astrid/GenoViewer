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

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static hu.astrid.io.BedItem.Strand.*;
import hu.astrid.io.BedBlock;
import hu.astrid.io.BedItem;

import org.junit.Test;

public class BedItemTest {

	@Test
	public void testConstructor() {
		BedItem bedItem = new BedItem.Builder("chr1", 0, 100).build();
		assertEquals("chr1 0 100", bedItem.toString());
		assertEquals("chr1", bedItem.getChrom());
	}

	@Test
	public void testBuilder() {
		// chr22 1000 5000 cloneA 960 + 1000 5000 0 2 567,488, 0,3512

		BedBlock bedBlock = new BedBlock();
		bedBlock.addBedBlock(567, 0);
		bedBlock.addBedBlock(488, 3512);

		BedItem bedItem = new BedItem.Builder("chr22", 1000, 5000).name(
				"cloneA").score(960).strand(PLUS).thickStart(1000)
				.thickEnd(5000).itemRgb(0).bedBlock(bedBlock).build();
		assertEquals(
				"chr22 1000 5000 cloneA 960 + 1000 5000 0 2 567,488, 0,3512",
				bedItem.toString());
	}

	@Test
	public void testException() {
		try {
			@SuppressWarnings("unused")
			BedItem bedItem = new BedItem.Builder("chr1", 0, 100).name("chr22").itemRgb(2)
					.build();
			fail("IllegalArgumentException expected");
		} catch (RuntimeException e) {
		}
	}
}
