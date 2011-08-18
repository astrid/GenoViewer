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

import hu.astrid.mapping.model.AlignmentRecord;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

public class MappingFileParserTest {

	@Test
	public void testParseAlignmentRecordByteArrayListOfString() throws Exception {
		byte [] byteArray = new byte [] {
				0, 0, 0, 0, -119, 3, 0, 0, 46, -1, 73, 18, 1, 0, 0, 0, 49, 0,
				 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0, 49, 57, 51, 56,
				 32, 71, 101, 110, 101, 114, 97, 116, 101, 100, 32, 103, 101,
				 110, 111, 109, 32, 70, 87, 32, 45, 32, 115, 101, 99, 111, 110,
				 100, 97, 114, 121, 32, 103, 101, 110, 111, 109, 32, 57, 48, 49,
				 0, 16, 3, 0, 0, 17, 72, -124, 66, 17, -126, 36, -126, -126, -124,
				 17, 34, 24, -126, -124, -124, 18, 68, -120, 36, -124, 36, -120,
				 68, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				 -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				 -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				 -1, -1, -1, 67, 83, 90, 84, 51, 48, 50, 49, 48, 49, 48, 51, 49,
				 48, 51, 50, 48, 51, 49, 50, 50, 50, 50, 49, 50, 48, 49, 48, 49,
				 51, 48, 50, 50, 49, 49, 49, 50, 49, 51, 48, 49, 48, 50, 51, 49,
				 49, 51, 51, 49, 48, 49, 48, 51, 0, 77, 68, 90, 52, 57, 0
		};
		
//		AlignmentRecord actual = MappingFileParser.parseAlignmentRecord(byteArray, Arrays.asList("Generated genom"));
		AlignmentRecord actual = new AlignmentRecordCodec( Arrays.asList("Generated genom")).decode(byteArray);
		
		Assert.assertEquals(
				"1938 Generated genom FW - secondary genom 901\t" +
				"0\t" +
				"Generated genom\t" +
				"906\t" +
				"255\t" +
				"49M\t" +
				"=\t" +
				"0\t" +
				"0\t" +
				"AAGTTGGCAATCCGTCTCTGAACCATTCTGTGACGGTTCGTGCGTTGGC\t" +
				"*\t" +
				"CS:Z:T3021010310320312222120101302211121301023113310103\t" +
				"MD:Z:49",
				actual.toString());
	}
	
}
