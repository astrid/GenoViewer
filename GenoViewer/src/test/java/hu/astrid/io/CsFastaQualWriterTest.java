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

/**
 * 
 */
package hu.astrid.io;

import hu.astrid.read.CsRead;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Astrid Research Inc.
 * Author: zsdoma
 * Created: 2010.01.05.
 */
public class CsFastaQualWriterTest {

	@Test
	public void testPutReadWithQual() throws IOException {
		StringWriter stringWriter = new StringWriter();
		StringWriter qualStringWriter = new StringWriter(); 
		
		BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
		BufferedWriter qualBufferedWriter = new BufferedWriter(qualStringWriter);
		
		CsFastaQualWriter csFastaQualWriter = CsFastaQualWriter.getInstance(bufferedWriter, qualBufferedWriter);
		
		csFastaQualWriter.appendComment("this is a comment");
		
		csFastaQualWriter.putCSReadWithQual(
				new CsRead("read1", "T0233"),
				new int [] {12, 58, 62, 12});
		
		csFastaQualWriter.putCSReadWithQual(
				new CsRead("read2", "T321032"),
				new int [] {58, 54, 65, 47, 21, 23});
		
		Assert.assertEquals(
				"#this is a comment\n" +
				">read1\n" +
				"T0233\n" +
				">read2\n" +
				"T321032\n",
				stringWriter.toString());

		Assert.assertEquals(
				">read1\n" +
				"12 58 62 12 \n" +
				">read2\n" +
				"58 54 65 47 21 23 \n",
				qualStringWriter.toString());

	}

}
