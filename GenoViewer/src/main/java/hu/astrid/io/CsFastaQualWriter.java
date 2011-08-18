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

/**
 * Astrid Research Inc.
 * Author: zsdoma
 * Created: 2010.01.05.
 */
public class CsFastaQualWriter extends CsFastaWriter {

	private BufferedWriter qualWriter;
	
	private CsFastaQualWriter(BufferedWriter bufferedWriter, BufferedWriter qualBufferedWriter) {
		super(bufferedWriter);
		this.qualWriter = qualBufferedWriter;
	}

	public static CsFastaQualWriter getInstance(BufferedWriter bufferedWriter, BufferedWriter qualBufferedWriter) {
		return new CsFastaQualWriter(bufferedWriter, qualBufferedWriter);
	}
	
	public void putCSReadWithQual(CsRead csRead, int [] quals) throws IOException {
		appendRead(csRead);
		
		StringBuilder sb = new StringBuilder();
		sb = sb.append(">" + csRead.getId() + "\n");
		for (int qual : quals) {
			sb = sb.append(qual + " ");
		}
		qualWriter.write(sb.toString() + "\n");
		qualWriter.flush();
	}
	
	public void close() throws IOException {
		super.close();
		qualWriter.close();
	}
}
