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
import hu.astrid.mapping.model.BamHeader;
import hu.astrid.mapping.model.MappingHeader;
import hu.astrid.mapping.util.BamUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * This class implements a GZIPOutputStream for writes BAM records.
 */
public class BamWriter extends GZIPOutputStream implements MappingFileWriter {

    private AlignmentRecordCodec alignmentRecordCodec;

    /**
     * Creates a new BAM file writer.
     *
     * @param out the output stream
     * 
     * @throws IOException
     *              If an I/O error has occurred.
     */
    public BamWriter(OutputStream out) throws IOException {
        super(out);
    }

    /**
     * Creates a new BAM file writer with the specified buffer size.
     *
     * @param out the output stream
     * @param size the output buffer size
     *
     * @throws IOException
     *              If an I/O error has occurred.
     */
    public BamWriter(OutputStream out, int size) throws IOException {
        super(out, size);
    }

    @Override
    public void writeHeader(MappingHeader header)
            throws MappingFileFormatException, IOException {

        if (alignmentRecordCodec != null) {
            throw new IllegalStateException("Illegal header access!");
        }

        if (!(header instanceof BamHeader)) {
            throw new IllegalArgumentException("Illegal BAM header object format exception");
        }

        write(BamUtil.BAM_MAGIC_BYTES);

        byte[] headerRecord = BamUtil.toByteArray(header.toString().toCharArray());

        int l_text = headerRecord.length;

        write(BamUtil.toByteArray(l_text));

        write(headerRecord);

        BamHeader bamHeader = (BamHeader) header;

        List<String> refNames = bamHeader.getReferenceNames();

        int n_ref = refNames.size();

        write(BamUtil.toByteArray(n_ref));

        List<Integer> l_refName = bamHeader.getReferenceLengths();

        int i = 0;

        for (String refName : refNames) {

            int l_name = (refName.length() + 1);

            write(BamUtil.toByteArray(l_name));

            write(BamUtil.toByteArray(refName.toCharArray()));

            write(0);

            int l_ref = l_refName.get(i++);

            write(BamUtil.toByteArray(l_ref));

        }

        flush();
        
        alignmentRecordCodec = new AlignmentRecordCodec(refNames);
    }

    @Override
    public void writeRecord(AlignmentRecord alignmentRecord)
            throws MappingFileFormatException, IOException {

        if (alignmentRecordCodec == null) {
            throw new MappingFileFormatException("Missing header!");
        }

        byte[] record = alignmentRecordCodec.code(alignmentRecord);
        write( BamUtil.toByteArray(record.length));
        write( record );

    }

    /**
     * It flushes data to output stream, then closes it.
     */
    @Override
    public void close() throws IOException {
        flush();
        super.close();
    }
}
