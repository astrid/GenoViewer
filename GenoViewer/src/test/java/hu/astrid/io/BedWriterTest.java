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

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import static hu.astrid.io.BedItem.Strand.*;
import hu.astrid.io.BedBlock;
import hu.astrid.io.BedItem;
import hu.astrid.io.BedWriter;

import org.junit.Test;

public class BedWriterTest {
	@Test
	public void testWriter() throws IOException {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);

		BedWriter writer = BedWriter.getInstance(bufferedWriter);
		
		BedBlock bedBlock = new BedBlock();
		bedBlock.addBedBlock(567, 0);
		bedBlock.addBedBlock(488, 3512);
		BedItem bedItem = new BedItem.Builder("chr22", 1000, 5000).name(
				"cloneA").score(960).strand(PLUS).thickStart(1000)
				.thickEnd(5000).itemRgb(0).bedBlock(bedBlock).build();
		writer.addItem(bedItem);
		
		bedBlock = new BedBlock();
		bedBlock.addBedBlock(433, 0);
		bedBlock.addBedBlock(399, 3601);
		bedItem = new BedItem.Builder("chr22", 2000, 6000).name(
				"cloneB").score(900).strand(MINUS).thickStart(2000)
				.thickEnd(6000).itemRgb(0).bedBlock(bedBlock).build();
		writer.addItem(bedItem);
		
		writer.close();

		assertEquals("chr22 1000 5000 cloneA 960 + 1000 5000 0 2 567,488, 0,3512\n" + 
				"chr22 2000 6000 cloneB 900 - 2000 6000 0 2 433,399, 0,3601\n", stringWriter.toString());
	}
}
