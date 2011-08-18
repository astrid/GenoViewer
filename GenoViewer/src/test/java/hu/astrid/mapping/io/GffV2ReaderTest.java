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
public class GffV2ReaderTest {

	private String gffFileHeader1 = "##gff-version\t2\n" + "# date\tMon Oct 12 21:00:00 2009\n";
	private String gffFileHeader2 = "# date\tMon Oct 12 21:00:00 2009\n";
	private String gffFile = "##gff-version 2\n"
		+ "# date\tMon Oct 12 21:00:00 2009\n"
		+ "Ca19-mtDNA\tCGD\tcontig\t1\t40420\t.\t.\t.\tID=Ca19-mtDNA\n"
		+ "Contig19-10011\tCGD\tcontig\t1\t9594\t.\t.\t.\tID=Contig19-10011\n"
		+ "Contig19-20231\tGD\tORF\t54764\t56206\t.\t+\t.\tID=orf19.13117;Name=orf19.13117;Gene=MEP2;Alias=tmp1,tmp2;Note=Ammonium%20permease%20and%20regulator%20of%20nitrogen%20starvation-induced%20filamentation%3B%2011%20predicted%20transmembrane%20regions%3B%20in%20low%20nitrogen%20cytoplasmic%20C-terminus%20activates%20Ras%2FcAMP%20and%20MAPK%20signal%20transduction%20pathways%20to%20induce%20filamentation\n"
		+ "1\t.\tmiRNA\t19096152\t19096229\t.\t-\t.\tACC=\"MI0006352\"; ID=\"hsa-mir-1290\";";

	public GffV2ReaderTest() {
	// Empty
	}

	@Test
	public void testNextRecord() {
		Map<String, List<String>> tmp = new HashMap<String, List<String>>();
		List<String> v1 = new ArrayList<String>();
		v1.add("orf19.13117");
		tmp.put("ID", v1);
		List<String> v2 = new ArrayList<String>();
		v2.add("orf19.13117");
		tmp.put("Name", v2);
		List<String> v3 = new ArrayList<String>();
		v3.add("MEP2");
		tmp.put("Gene", v3);
		List<String> v4 = new ArrayList<String>();
		v4.add("Ammonium permease and regulator of nitrogen starvation-induced filamentation; 11 predicted transmembrane regions; in low nitrogen cytoplasmic C-terminus activates Ras/cAMP and MAPK signal transduction pathways to induce filamentation");
		tmp.put("Note", v4);
		List<String> v5 = new ArrayList<String>();
		v5.add("tmp1");
		v5.add("tmp2");
		tmp.put("Alias", v5);
		
		Map<String, List<String>> tmp2 = new HashMap<String, List<String>>();
		List<String> vv1 = new ArrayList<String>();
		vv1.add("\"MI0006352\"");
		tmp2.put("ACC", vv1);
		List<String> vv2 = new ArrayList<String>();
		vv2.add("\"hsa-mir-1290\"");
		tmp2.put("ID", vv2);
		
		StringReader reader = new StringReader(gffFile);
		try {
			GffReader gffReader = new GffReader(reader);
			GffRecord rec = gffReader.nextRecord();
			assertEquals("Ca19-mtDNA\tCGD\tcontig\t1\t40420\t.\t.\t.\tID=Ca19-mtDNA", rec.toString());
			rec = gffReader.nextRecord();
			assertEquals("Contig19-10011\tCGD\tcontig\t1\t9594\t.\t.\t.\tID=Contig19-10011", rec.toString());
			rec = gffReader.nextRecord();
			assertEquals(rec.getSeqId(), "Contig19-20231");
			assertEquals(rec.getSource(), "GD");
			assertEquals(rec.getType(), "ORF");
			assertEquals(rec.getStart(), 54764);
			assertEquals(rec.getEnd(), 56206);
			assertEquals(rec.getScore(), ".");
			assertEquals(rec.getStarnd().getValue(), '+');
			assertEquals(rec.getPhase().getValue(), '.');
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
			assertEquals(rec.getSeqId(), "1");
			assertEquals(rec.getSource(), ".");
			assertEquals(rec.getType(), "miRNA");
			assertEquals(rec.getStart(), 19096152);
			assertEquals(rec.getEnd(), 19096229);
			assertEquals(rec.getScore(), ".");
			assertEquals(rec.getStarnd().getValue(), '-');
			assertEquals(rec.getPhase().getValue(), '.');
			for (String tmpName : tmp2.keySet()) {
				List<String> tmpValues = tmp2.get(tmpName);
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
