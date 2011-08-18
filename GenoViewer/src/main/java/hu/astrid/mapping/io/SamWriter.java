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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * Writer implementation which purpose is to sequentially write alignment
 * records to mapping files.
 */
public class SamWriter implements MappingFileWriter {

    /**
     * The buffered writer which is used to write to the mapping file.
     */
    private BufferedWriter bufferedWriter;
    /**
     * Flag which indicates whether the header was already written or not.
     */
    private boolean headerWritten = false;

    /**
     * Creates a new writer which is able to write alignment records using the
     * given writer.
     *
     * @param writer
     *            the writer which is used to write alignment records
     */
    public SamWriter(Writer writer) {
        if (writer instanceof BufferedWriter) {
            this.bufferedWriter = (BufferedWriter) writer;
        } else {
            this.bufferedWriter = new BufferedWriter(writer);
        }
    }

    /**
     * Creates a new writer which is able to write alignment records to the
     * given mapping file.
     *
     * @param file
     *            the mapping file
     * @throws IOException
     *             if an I/O error occurs
     */
    public SamWriter(File file) throws IOException {
        this(new FileWriter(file));
    }

    @Override
    public void writeHeader(MappingHeader samFileHeader)
            throws MappingFileFormatException, IOException{

        if (headerWritten) {
            throw new IllegalStateException("Illegal header access!");
        }
        this.bufferedWriter.write(samFileHeader.toString() + "\n");
        this.bufferedWriter.flush();
        headerWritten = true;
    }

    @Override
    public void writeRecord(AlignmentRecord samBodyRecord) throws IOException {
        this.bufferedWriter.write(samBodyRecord.toString() + "\n");
        this.bufferedWriter.flush();
    }

    /**
     * Writes multiple alignment records to the mapping file in the given order.
     *
     * @param records
     *            the list of alignment records to be written
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeRecords(Collection<AlignmentRecord> records) throws IOException {
        if (records != null && !records.isEmpty()) {
            for (AlignmentRecord record : records) {
                writeRecord(record);
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.bufferedWriter.close();
    }
}
