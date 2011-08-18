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
 * Interface of readers which are able to read mapping information sequentially from file.
 */
public interface MappingFileReader {

    /**
     * Reads the header of a mapping file. An exception is thrown in case of
     * calling this method multiple times or after reading the first alignment
     * record.
     *
     * @return the header section of the mapping file or null if it is not
     *         present
     * @throws IllegalHeaderValueException
     *             if a header tag's value is invalid
     * @throws IllegalHeaderTagException
     *             if a header tag is illegal
     * @throws DuplicateTagException
     *             if a forbidden tag duplication occurs
     * @throws MissingTagException
     *             if a mandatory tag is missing
     * @throws IllegalHeaderAccessException
     *             in case of calling this method multiple times or after
     *             reading the first alignment record
     * @throws IOException
     *             if an I/O error occurs
     */
    MappingHeader readHeader() throws MappingFileFormatException, IOException;

    /**
     * Return the next alignment record from the mapping file. In case of
     * reading the first record without calling readHeader(), the header is
     * skipped.
     *
     * @return the next alignment record
     * @throws AlignmentRecordFormatException
     *             if the format of an alignment record is incorrect
     * @throws OptionalTagFormatException
     *             if the format of an optional tag is incorrect
     * @throws IOException
     *             if an I/O error occurs
     */
    AlignmentRecord nextRecord() throws MappingFileFormatException, IOException;

    /**
     */
    void close() throws IOException;
}
