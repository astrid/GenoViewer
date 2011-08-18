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

package hu.astrid.mapping.util;

import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.io.SerializationUtil;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.AlignmentRecordComparatorExtended;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This data structure stores alignment records sorted and can store it in a temp file.
 * @author Szuni
 */
public class MappingFileShorterStructure extends LinkedList<AlignmentRecord>{

	private static final long serialVersionUID = 1353215419588153859L;

	/**Temp file index*/
	private int tempIndex = 0;

	/**Prefix name of temporary file*/
	private static final String fileName = "sorttemp";

	/**
	 * Creates a new empty instance, initialized with the corresponding comparator
	 */
	public MappingFileShorterStructure() {
		super();
	}

	/**
	 * Write the content to a temp file and clear the data structure
	 * @return name of temp file
	 * @throws IOException
	 * @throws IllegalHeaderAccessException
	 * @throws HeaderObjectFormatException
	 */
	public String write() throws IOException, MappingFileFormatException {

		Collections.sort(this, new AlignmentRecordComparatorExtended());

		FileChannel writer = null;

		while(writer == null) {
			try {
				File tempFile = new File("./"+fileName+tempIndex);
				writer = new FileOutputStream(tempFile).getChannel();
				tempFile.deleteOnExit();
			} catch (Exception ex) {
				tempIndex++;
			}
		}

//		SamWriter writer = new SamWriter(tempFile);
//		writer.writeHeader(header);
//		writer.writeRecords(this);
//		writer.close();

		for (AlignmentRecord r : this) {
			ByteBuffer buffer = SerializationUtil.toByteBuffer(r);
			buffer.flip();
			writer.write(buffer);
		}
		writer.close();

		this.clear();
		System.gc();

		return fileName+tempIndex;
	}
}
