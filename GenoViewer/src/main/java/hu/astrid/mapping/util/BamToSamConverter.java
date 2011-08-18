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
import hu.astrid.mapping.io.BamReader;
import hu.astrid.mapping.io.SamWriter;
import hu.astrid.mapping.model.MappingHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * Type of BAM file converts to type of SAM file.
 */
public class BamToSamConverter extends MappingFileConverter {

    /**
     * Flag which indicates it is going to write into SAM file.
     */
    private boolean writeHeader;

    /**
     * Creates an new BAM to SAM file converter.
     *
     * @param inBam the BAM file is input stream
     * @param outSam the SAM file is writer
     * @param writeHeader if ture, then converter writes header to SAM file.
     *
     * @throws IOException
     *              If an I/O error has occurred.
     */
    public BamToSamConverter(InputStream inBam, Writer outSam, boolean writeHeader )
            throws IOException {

        mappingFileReader = new BamReader(inBam);

        mappingFileWriter = new SamWriter(outSam);

        this.writeHeader = writeHeader;

    }

    @Override
    protected void convertHeader() throws IOException, MappingFileFormatException {

        MappingHeader samHeader = mappingFileReader.readHeader();

        if ((samHeader != null) && writeHeader) {
            mappingFileWriter.writeHeader(samHeader);
        }
    }
    
}
