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

package hu.astrid.viewer.model.alignment;

import hu.astrid.mapping.io.SamReader;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.AlignmentRecordComparator;
import hu.astrid.viewer.model.alignment.ReadData.PositionMutation;
import hu.astrid.viewer.util.Alignment;
import hu.astrid.viewer.util.Alignment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.DataFormatException;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author OTTO
 */
public class ReadDataTest {

	private List<AlignmentRecord> readList;
	private SamReader samReader;

	@Before
	public void setUp() throws Exception {
		readList = new ArrayList<AlignmentRecord>();
		samReader = new SamReader(new File(System.getProperty("viewer.testfiles.dir"), "c1215jo.sam"));


		AlignmentRecord sam = null;
		sam = samReader.nextRecord();
		while (sam != null) {
			readList.add(sam);
			sam = samReader.nextRecord();
		}
		Collections.sort(readList, new AlignmentRecordComparator());
	}

	@Test
	public void testSnpIndex() throws IOException, DataFormatException {
		String mdTag = "";
		for (Iterator<AlignmentRecord> it = readList.iterator(); it.hasNext();) {
			AlignmentRecord read = it.next();
			ReadData readData = new ReadData(read);
			mdTag = read.getOptionalTag("MD").getValue();
			try {
				Integer.parseInt(mdTag);
				assertEquals(new TreeSet(), readData.getSnpList());
			} catch (Exception ex) {
				//Test the count of snps in a read
				List<String> ops = Alignment.parseMis(mdTag);
				int positions = 0;
				for (Iterator<String> curentOp = ops.iterator(); curentOp.hasNext();) {
					String opString = curentOp.next();
					if (opString.charAt(0) != '^') {
						positions += opString.length();
					}
				}
				assertEquals(positions, readData.getSnpList().size());
			}
		}
	}

	@Test
	public void testCustomMdTag() {
		AlignmentRecord myRead = readList.get(0);

		myRead.getOptionalTag("MD").setValue("1T1T1T1T1T1T1T1T1");
		myRead.setCigar("17M");
		ReadData readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 1.", loadSnpPositions(new int[]{1, 3, 5, 7, 9, 11, 13, 15}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("5TTTTT5TTT5");
		myRead.setCigar("23M");
		readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 2.", loadSnpPositions(new int[]{5, 6, 7, 8, 9, 15, 16, 17}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("5TATATATA0^TATATA5");
		myRead.setCigar("5M6D11M");
		readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 3.", loadSnpPositions(new int[]{5, 6, 7, 8, 9, 10, 11, 12}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("35");
		myRead.setCigar("35M");
		readData = new ReadData(myRead);
		assertEquals(loadSnpPositions(new int[]{}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("20^TG10");
		myRead.setCigar("20M2D10M");
		readData = new ReadData(myRead);
		assertEquals(loadSnpPositions(new int[]{}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("5^TG5TATA20");
		myRead.setCigar("5M2D29M");
		readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 4.", loadSnpPositions(new int[]{12, 13, 14, 15}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("5^TG5TATA5^TTT0AAA2^TTT5");
		myRead.setCigar("5M2D14M3D5M3D5M");
		readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 5.", loadSnpPositions(new int[]{12, 13, 14, 15, 24, 25, 26}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("2^T0A0^T0A0^T0A0^T5");
		myRead.setCigar("2M1D1M1D1M1D1M1D5M");
		readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 6.", loadSnpPositions(new int[]{3, 5, 7}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("5^AA1^TT0GGG0^A5^A0GGGGG5");
		myRead.setCigar("5M2I1M2D3M1I5M1I10M");
		readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 7.", loadSnpPositions(new int[]{8, 9, 10, 16, 17, 18, 19, 20}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("3^AA1^TT0GGG0^A5^A0GGGGG5");
		myRead.setCigar("3M2I1N2D3M1I5N1I10M");
		readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 8.", loadSnpPositions(new int[]{6, 7, 8, 14, 15, 16, 17, 18}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("5^AA3^AA3^AA3^AA3");
		myRead.setCigar("5M2I3M2I3M2I3M2I3M");
		readData = new ReadData(myRead);
		assertEquals(loadSnpPositions(new int[]{}), readData.getSnpList());

		myRead.getOptionalTag("MD").setValue("3^AA1^TT0GGG0^A5^A0GGGGG5");
		myRead.setCigar("2S3M2I1N2D3M1I5N1I10M");
		readData = new ReadData(myRead);
		assertEquals("Hiba MD tag 9.", loadSnpPositions(new int[]{6, 7, 8, 14, 15, 16, 17, 18}), readData.getSnpList());
	}

	private TreeSet<PositionMutation> loadSnpPositions(int[] positions) {
		TreeSet<PositionMutation> snpPositions = new TreeSet<PositionMutation>(ReadData.comparator);
		for (int i = 0; i < positions.length; i++) {
			snpPositions.add(new PositionMutation(positions[i]));
		}

		return snpPositions;
	}
}
