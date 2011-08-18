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

import hu.astrid.core.Color;
import hu.astrid.read.CsRead;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Astrid Research Inc.
 * Author: zsdoma
 * Created: 2010.01.05.
 */
public class CsFastaWriter extends BaseWriter implements ReadFileWriter<Color, CsRead> {

	public CsFastaWriter(BufferedWriter bufferedWriter) {
		super(bufferedWriter);
	}
	
	@Override
	public void appendRead(CsRead csRead) throws IOException {
		bufferedWriter.write(">" + csRead.getId() + "\n" + csRead + "\n");
		bufferedWriter.flush();
	}
	
	@Override
	public void appendComment(String comment) throws IOException {
		bufferedWriter.write("#" + comment + "\n");
		bufferedWriter.flush();
	}
}
