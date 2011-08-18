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

import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.MappingHeader;

import java.io.IOException;

/**
 * Interface of writer which are able to write mapping information sequentially to file.
 */
public interface MappingFileWriter {

    /**
     * Writes the header section of the mapping file. In case of calling after
     * writing one or more alignment records, an exception is thrown.
     *
     * @param header
     *             the header to be written
     * @throws IOException
     *             if an I/O error occurs
     * @throws IllegalHeaderAccessException
     *             in case of calling this method multiple times or after
     *             writing the first alignment record
     * @throws HeaderObjectFormatException
     *             if header's format doesn't compatible
     */
    void writeHeader(MappingHeader header) throws MappingFileFormatException, IOException;

    /**
     * Writes the next alignment record to the mapping file. In case of writing
     * the first record without writing the header, the file will not contain
     * any header section.
     *
     * @param alignmentRecord
     *              the alignment record to be written
     * @throws IOException
     *              if an I/O error occurs
     * @throws MissingHeaderException
     *              if you haven't written header section
     * @throws AlignmentRecordFormatException
     *              if alignment record's format is invalid
     */
    void writeRecord(AlignmentRecord alignmentRecord)
            throws MappingFileFormatException, IOException;

    /**
     * Closes the mapping file.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    void close() throws IOException;
}
