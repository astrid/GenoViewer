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
import hu.astrid.mapping.model.HeaderRecord;
import hu.astrid.mapping.model.MappingHeader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * Reader implementation which purpose is to sequentially load alignment records
 * from mapping files.
 */
public class SamReader implements MappingFileReader {

    /**
     * Iterator which iterates through the lines of the mapping file.
     */
    private Iterator<String> buffIterator;
    /**
     * Flag which indicates whether the header was already loaded or not.
     */
    private boolean headerLoaded = false;
    /**
     * The buffered next line of the mapping file.
     */
    private String nextLine;

    /**
     * Creates a new reader which is able to load alignment records from the
     * given mapping file.
     *
     * @param file
     *            the mapping file
     * @throws FileNotFoundException
     *             if the file does not exists
     */
    public SamReader(File file) throws FileNotFoundException {
        this(new FileReader(file));
    }

    /**
     * Creates a new reader which is able to load alignment records using the
     * given reader.
     *
     * @param reader
     *            the reader which is used to load alignment records
     */
    public SamReader(Reader reader) {
        BufferedReader breader = null;

        if (reader instanceof BufferedReader) {
            breader = (BufferedReader) reader;
        } else {
            breader = new BufferedReader(reader);
        }

        IterableBufferedReader buffI = new IterableBufferedReader(breader);
        buffIterator = buffI.iterator();
    }

    private boolean isHeaderRecord(String record) {
        String first = record.substring(0, 1);
        return (first.equals(HeaderRecord.HEADER_RECORD_PREFIX));
    }

    @Override
    public MappingHeader readHeader()
            throws MappingFileFormatException, IOException {

        MappingHeader result = null;
        HeaderRecord samHRecord = null;
        String record = null;

        if ((!headerLoaded) && (buffIterator.hasNext())) {
            record = buffIterator.next();
            if (isHeaderRecord(record)) {
                result = new MappingHeader();
            }
            
            do {
                if (!isHeaderRecord(record)) {
                    nextLine = record;
                    break;
                }

                samHRecord = MappingFileParser.parseHeaderRecord(record);
                result.addRecord(samHRecord);
            } while ((record = buffIterator.next()) != null);
            headerLoaded = true;
        } else {
            throw new IllegalStateException("Illegal header access!");
        }

        return result;
    }

    @Override
    public AlignmentRecord nextRecord()
            throws MappingFileFormatException, IOException {

        AlignmentRecord result = null;

        if (!headerLoaded) {
            readHeader();
        }

        if ((nextLine == null) && (buffIterator.hasNext())) {
            nextLine = buffIterator.next();
        }

        if ((nextLine != null) && (nextLine.length() > 0)) {
            result = MappingFileParser.parseAlignmentRecord(nextLine);
            nextLine = null;
        }

        return result;
    }

    @Override
    public void close() throws IOException {
        buffIterator = null;
    }
}
