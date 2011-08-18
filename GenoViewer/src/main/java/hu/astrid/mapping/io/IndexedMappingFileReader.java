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

import hu.astrid.mapping.exception.MappingFileException;
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.model.AlignmentPosition;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.BamHeader;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 * Interface of readers which are able to read mapping information from file
 * using indices.
 */
public interface IndexedMappingFileReader {

	/**
	 * Reads and returns the header of the mapping file.
	 * 
	 * @return the header
	 */
	BamHeader getHeader();

	/**
	 * Loads alignment records in a given interval for a given reference
	 * sequence.
	 * 
	 * @param referenceName
	 *            the name of the reference sequence on which the alignment
	 *            records are
	 * @param start
	 *            the beginning of the interval in which the start of the
	 *            alignment records are, inclusive
	 * @param end
	 *            the end of the interval in which the end of the alignment
	 *            records are, exclusive
	 * @return the list of alignment records whose start position is in the
	 *         given interval on the given reference sequence
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws DataFormatException
	 *             if the binary data format is incorrect
	 * @throws MappingFileException
	 *             if the file format is incorrect
	 */
	List<AlignmentRecord> loadRecords(String referenceName, int start, int end)
			throws IOException, DataFormatException, MappingFileException;

	/**
	 * Loads alignment record positions in a given interval for a given
	 * reference sequence.
	 * 
	 * @param referenceName
	 *            the name of the reference sequence on which the alignment
	 *            records are
	 * @param start
	 *            the beginning of the interval in which the start of the
	 *            alignment records are, inclusive
	 * @param end
	 *            the end of the interval in which the end of the alignment
	 *            records are, exclusive
	 * @return
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws DataFormatException
	 *             if the binary data format is incorrect
	 * @throws MappingFileException
	 *             if the file format is incorrect
	 */
	List<AlignmentPosition> loadPositions(String referenceName, int start,
			int end) throws IOException, DataFormatException,
			MappingFileException;

	/**
	 * Loads an alignment record with a given name in a given position.
	 * 
	 * @param referenceName
	 *            the name of the reference on which the read was mapped
	 * @param queryName
	 *            the name of the read
	 * @param position
	 *            the mapping position
	 * @param length
	 *            the length of the alignment
	 * @return the appropriate alignment record of null if it was not found
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws MappingFileFormatException
	 *             if the file format is incorrect
	 */
	AlignmentRecord loadRecord(String referenceName, String queryName,
			int position, int length) throws IOException,
			MappingFileFormatException;

	/**
	 * Close alignnemt file underneath
	 *
	 * @throws IOException
	 */
	void close() throws IOException;

}
