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
import hu.astrid.mapping.io.BamWriter;
import hu.astrid.mapping.io.SamReader;
import hu.astrid.mapping.model.BamHeader;
import hu.astrid.mapping.model.HeaderRecord;
import hu.astrid.mapping.model.MappingHeader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;

import static hu.astrid.mapping.model.HeaderTagType.*;
import static hu.astrid.mapping.model.HeaderRecordType.*;

/**
 * Type of SAM file converts to type of BAM file.
 */
public class SamToBamConverter extends MappingFileConverter {

    /**
     * Creates an new SAM to BAM file converter.
     *
     * @param inSam the SAM file is reader
     * @param outBam the BAM file is output stream
     *
     * @throws FileNotFoundException
     *              if the inSam is file not exists.
     * @throws IOException
     *              If an I/O error has occurred.
     */
    public SamToBamConverter(Reader inSam, OutputStream outBam)
    throws FileNotFoundException, IOException {

        mappingFileReader = new SamReader(inSam);

        mappingFileWriter = new BamWriter(outBam);

    }

    @Override
    protected void convertHeader() throws MappingFileFormatException, IOException {

        MappingHeader samHeader = mappingFileReader.readHeader();

        BamHeader bamHeader = new BamHeader();

        for( HeaderRecord headerRecord : samHeader.getRecords() )
            bamHeader.addRecord(headerRecord);

        if (bamHeader != null) {
            List<HeaderRecord> hRecords = null;
            if ((hRecords = bamHeader.isMatch(SQ)) == null ) {
                throw new MappingFileFormatException("Missing header tag! "+SQ);
            }

            for( HeaderRecord hRecord : hRecords ) {

                String seqName = hRecord.getTagValue(SN);

                try{

                    int seqL = new Integer( hRecord.getTagValue(LN) );
                    bamHeader.addReference( seqName, seqL );
                    
                } catch( NumberFormatException nfe ) {
                    throw new RuntimeException("Invalid " + LN + " value:" + hRecord.getTagValue(LN));
                }

            }

            mappingFileWriter.writeHeader(bamHeader);
            
        } else {
            throw new MappingFileFormatException("Missing header!");
        }
    }
}
