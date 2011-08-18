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

package hu.astrid.mapping.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import hu.astrid.mapping.exception.GffFileFormatException;
import hu.astrid.mapping.model.GffRecord;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 *
 * @author mkiss
 */
public class GffV1ReaderTest {

	private String gffFileHeader1 = "##gff-version\t2\n" + "# date\tMon Oct 12 21:00:00 2009\n";
	private String gffFileHeader2 = "# date\tMon Oct 12 21:00:00 2009\n";
	private String gffFile = "##gff-version 1\n"
		+ "# date\tMon Oct 12 21:00:00 2009\n"
		+ "V_15447726\tS1\tFE\t90913\t91251\t0\t+\t0\tB0391.4\n";

	public GffV1ReaderTest() {
	// Empty
	}

	@Test
	public void testNextRecord() {
		Map<String, List<String>> tmp = new HashMap<String, List<String>>();
		List<String> v1 = new ArrayList<String>();
		v1.add("B0391.4");
		tmp.put("Group", v1);
		StringReader reader = new StringReader(gffFile);
		try {
			GffReader gffReader = new GffReader(reader);
			GffRecord rec = gffReader.nextRecord();
			assertEquals(rec.getSeqId(), "V_15447726");
			assertEquals(rec.getSource(), "S1");
			assertEquals(rec.getType(), "FE");
			assertEquals(rec.getStart(), 90913);
			assertEquals(rec.getEnd(), 91251);
			assertEquals(rec.getScore(), "0");
			assertEquals(rec.getStarnd().getValue(), '+');
			assertEquals(rec.getPhase().getValue(), '0');
			for (String tmpName : tmp.keySet()) {
				List<String> tmpValues = tmp.get(tmpName);
				List<String> values = rec.getAttributeValues(tmpName);
				assertEquals(tmpValues.size(), values.size());
				for (String value : values) {
					boolean contain = tmpValues.contains(value);
					assertEquals(true, contain);
					tmpValues.remove(value);
				}
			}
			rec = gffReader.nextRecord();
			assertEquals(null, rec);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} catch (GffFileFormatException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testGetVersion() {
		try {
			StringReader reader = new StringReader(gffFileHeader1);
			GffReader gffReader = new GffReader(reader);
			gffReader.nextRecord();
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		} catch (GffFileFormatException gffe) {
			String message = gffe.getMessage();
			if (!message.equals("Invalid version: 0"))
				fail(gffe.getMessage());
		}
		try {
			StringReader reader = new StringReader(gffFileHeader2);
			GffReader gffReader = new GffReader(reader);
			gffReader.nextRecord();
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		} catch (GffFileFormatException gffe) {
			String message = gffe.getMessage();
			if (!message.equals("Invalid version: 0")) {
				fail(gffe.getMessage());
			}
		}
	}
}
