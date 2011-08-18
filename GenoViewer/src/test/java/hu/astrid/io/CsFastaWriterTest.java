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
public class CsFastaWriterTest {

	@Test
	public void testPutCSRead() throws IOException {
		StringWriter stringWriter = new StringWriter();
		
		BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
		CsFastaWriter csFastaWriter = new CsFastaWriter(bufferedWriter);
		
		csFastaWriter.appendComment("this is a comment");
		csFastaWriter.appendRead(new CsRead("read1", "T0233223120012103102130"));
		csFastaWriter.appendRead(new CsRead("read2", "T3210323210321302132103301"));
		
		Assert.assertEquals(
				"#this is a comment\n" +
				">read1\n" +
				"T0233223120012103102130\n" +
				">read2\n" +
				"T3210323210321302132103301\n",
				stringWriter.toString());
	}

}
