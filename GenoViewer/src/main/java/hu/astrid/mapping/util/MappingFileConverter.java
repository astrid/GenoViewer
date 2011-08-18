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
import hu.astrid.mapping.io.MappingFileReader;
import hu.astrid.mapping.io.MappingFileWriter;
import hu.astrid.mapping.model.AlignmentRecord;
import java.io.IOException;

/**
 * This class base class of converter methods, SAM to BAM or BAM to SAM.
 */
public abstract class MappingFileConverter {

    protected MappingFileReader mappingFileReader;
    protected MappingFileWriter mappingFileWriter;

    /**
     * Creates a new converter.
     */
    public MappingFileConverter() {}

    /**
     * This method one record converts to another record format.
     *
     * @throws AlignmentRecordFormatException
     *              if an record format is invalid
     * @throws OptionalTagFormatException
     *              if an optional tag format is invalid
     * @throws MissingHeaderException
     *              if the header not exist
     * @throws IOException
     *              If an I/O error has occurred.
     */
    protected void convertRecord()
            throws MappingFileFormatException, IOException {

        if (mappingFileReader == null || mappingFileWriter == null) {
            throw new IOException("Something I/O error occured!");
        }

        AlignmentRecord alignmentRecord = null;

        while ((alignmentRecord = mappingFileReader.nextRecord()) != null)
            mappingFileWriter.writeRecord(alignmentRecord);


    }

    /**
     * This method one file converts to file format.
     *
     * @throws MappingFileException
     *              If an mapping file write or read error has occurred.
     * @throws IOException
     *              If an I/O error has occurred.
     */
    public void convert()
            throws MappingFileFormatException,
            IOException {

        try{
            convertHeader();

            convertRecord();

        } finally {
            mappingFileWriter.close();
            mappingFileReader.close();
        }
    }

    /**
     * This method one header converts to another header format.
     *
     * @throws MappingFileException
     *              If an mapping file write or read error has occurred.
     * @throws IOException
     *              If an I/O error has occurred.
     */
    abstract protected void convertHeader() throws MappingFileFormatException, IOException;
}
