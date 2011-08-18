
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

import hu.astrid.mapping.exception.MappingFileException;
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.io.MappingFileReader;
import hu.astrid.mapping.io.MappingFileWriter;
import hu.astrid.mapping.io.SerializationUtil;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.AlignmentRecordComparatorExtended;
import hu.astrid.mapping.model.HeaderRecord;
import hu.astrid.mapping.model.HeaderRecordType;
import hu.astrid.mapping.model.HeaderTag;
import hu.astrid.mapping.model.HeaderTagType;
import hu.astrid.mapping.model.MappingHeader;
import hu.astrid.mapping.model.SortOrder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Abstract class for sort records in a mapping file. The sorted records will be writed
 * to another file and headers SO tag set to {@code COORDINATE}. If the memory is full,
 * the previously sorted records serialized to a temp file.
 * @author Szuni
 */
public abstract class MappingFileSorter {

	/**Data structure for store the records*/
	protected MappingFileShorterStructure shorter;
	/**Header of the original file*/
	protected MappingHeader header;
//	protected SamReader[] readers;
	/**File channels for reading from temp files*/
	protected FileChannel[] readers;
	/**First unsaved record of temp files*/
	protected AlignmentRecord[] records;
	/**Read positions if temp files file channels*/
	protected long pos[];
	/**Buffer for reading from temp files*/
	private ByteBuffer buffer = ByteBuffer.allocate(SerializationUtil.BUFFER_SIZE);

	/**Reader for original alignment file*/
	protected MappingFileReader reader;
	/**Writer for sorted alignment file*/
	protected MappingFileWriter writer;

	/**Memory size need to be free*/
	protected static final int MEMORY_TRESHOLD = 10*1024*1024;

	/**Comparator for sorting the records*/
	protected static final AlignmentRecordComparatorExtended comparator = new AlignmentRecordComparatorExtended();

	/**
	 * Create a sorter for the unordered {@code reader} object with output of {@code writer} object.
	 * @param reader reader for unordered alignment file
	 * @param writer writer for odered output file
	 * @throws MappingFileException
	 * @throws IOException
	 */
	public MappingFileSorter(MappingFileReader reader, MappingFileWriter writer) throws MappingFileException, IOException {
		this.shorter = new MappingFileShorterStructure();
		this.reader = reader;
		this.writer = writer;
		header = reader.readHeader();

		for(HeaderRecord headerRecord : header.getRecords()) {
			if(headerRecord.getType() == HeaderRecordType.HD) {
				Iterator<HeaderTag> iterator = headerRecord.getTags().iterator();
				while(iterator.hasNext()) {
					HeaderTag headerTag = iterator.next();
					if(headerTag.getType() == HeaderTagType.SO) {
						iterator.remove();
					}
				}
				HeaderTag sortOrderTag = new HeaderTag(HeaderTagType.SO, SortOrder.COORDINATE.toString().toUpperCase());
				headerRecord.addTag(sortOrderTag);
			}
		}
	}

	/**
	 * Sort the original file and write result to the output file. The records are
	 * loaded into an ordered structure while there are free memory, then serialized
	 * to a temp file. At the end the temp files are merged.
	 * @throws MappingFileException
	 * @throws IOException
	 */
	public void sort() throws MappingFileException, IOException {
		ArrayList<String> tempFiles = new ArrayList<String>();
		AlignmentRecord record = reader.nextRecord();
		while(record!=null){
			if(Runtime.getRuntime().freeMemory()<MEMORY_TRESHOLD) {
				tempFiles.add( shorter.write() );
			}
			shorter.add(record);
			record = reader.nextRecord();
		}

		tempFiles.add( shorter.write() );

//		readers = new SamReader[tempFiles.size()];
//		records = new AlignmentRecord[tempFiles.size()];
//		for(int i=0; i<tempFiles.size(); ++i) {
//			readers[i] = new SamReader(new File(tempFiles.get(i)));
//			records[i] = readers[i].nextRecord();
//		}


		readers = new FileChannel[tempFiles.size()];
		records = new AlignmentRecord[tempFiles.size()];
		pos = new long[tempFiles.size()];
		for(int i=0; i<tempFiles.size(); ++i) {
			readers[i] = new RandomAccessFile(tempFiles.get(i), "r").getChannel();

			buffer.clear();
			readers[i].read(buffer);
			buffer.flip();
			records[i] = SerializationUtil.fromByteBuffer(buffer);
			pos[i] = buffer.position();
		}

		writer.writeHeader(header);

		while(hasRecordsToWrite()) {
			writeNextRecord();
		}

		writer.close();
	}

	/**
	 * Is there any unsaved record
	 * @return true - if any of temp files contains unsaved record
	 *		<br>false - every records saved to the new sorted file
	 */
	protected boolean hasRecordsToWrite() {
		for(AlignmentRecord record : records) {
			if(record != null)
				return true;
		}
		return false;
	}

	/**
	 * Write the first unsaved record. Every temp files first records will be compared,
	 * and the first is saved. After the save, the next record of the temp file
	 * is loaded. If no further record available in a file, the file is closed.
	 * @throws IOException
         * @throws MappingFileFormatException
	 */
	protected void writeNextRecord() throws IOException, MappingFileFormatException {
		int firstIndex=-1;
		int i=0;
		while(records[i]==null) {
			++i;
		}
		firstIndex = i;
		for(; i<records.length; ++i) {
			if( records[i]!=null && comparator.compare(records[firstIndex], records[i]) > 0)
				firstIndex = i;
		}

		writer.writeRecord(records[firstIndex]);

//		records[firstIndex]  = readers[firstIndex].nextRecord();

		buffer.clear();
		if (readers[firstIndex].read(buffer, pos[firstIndex]) <0){
			records[firstIndex] = null;
			readers[firstIndex].close();
		}
		else {
			buffer.flip();
			records[firstIndex] = SerializationUtil.fromByteBuffer(buffer);
			pos[firstIndex] += buffer.position();
		}


	}

}
