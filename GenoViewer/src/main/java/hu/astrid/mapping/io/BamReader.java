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
import hu.astrid.mapping.util.BamUtil;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Sequential BAM reader implementation.
 */
public class BamReader extends GzipReader implements MappingFileReader {

    /**
     * Flag which indicates whether the header was already loaded or not.
     */
    private boolean headerLoaded = false;

    private AlignmentRecordCodec alignmentRecordCodec;

    /**
     * Creates a new reader which is able to load alignment records using the
     * given input stream.
     *
     * @param in
     *            the input stream
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public BamReader(InputStream in) throws IOException {
        super(in);
        this.headerLoaded = false;
        this.alignmentRecordCodec = null;
    }

    @Override
    public BamHeader readHeader() throws MappingFileFormatException, IOException {
        BamHeader result = null;

        try {
            if (!headerLoaded) {
                byte[] magic = read(4);
                byte[] lText = read(4);
                int l = BamUtil.toInt(lText);
                byte[] text = read(l);
                byte[] nRef = read(4);
                int n = BamUtil.toInt(nRef);

                List<byte[]> bList = new ArrayList<byte[]>((3 * n) + 4);
                bList.add(magic);
                bList.add(lText);
                bList.add(text);
                bList.add(nRef);

                int sumL = (l + 12);
                for (int i = 0; i < n; i++) {
                    byte[] lName = read(4);
                    int ln = BamUtil.toInt(lName);
                    byte[] name = read(ln);
                    byte[] lRef = read(4);
                    sumL += (ln + 8);
                    bList.add(lName);
                    bList.add(name);
                    bList.add(lRef);
                }

                byte[] sumB = new byte[sumL];
                int offset = 0;

                for (byte[] byteArray : bList) {
                    System.arraycopy(byteArray, 0, sumB, offset,
                            byteArray.length);
                    offset += byteArray.length;
                }

                result = MappingFileParser.parseBamHeader(sumB);
                alignmentRecordCodec = new AlignmentRecordCodec(result.getReferenceNames());
                headerLoaded = true;
            } else {
                throw new IllegalStateException("Illegal header access!");
            }

        } catch (EOFException eofe) {
            result = null;
        }

        return result;
    }

    @Override
    public AlignmentRecord nextRecord()
            throws MappingFileFormatException, IOException {

        AlignmentRecord result = null;

        if (!headerLoaded && (readHeader() == null)) {
            return null;
        }

        try {
            int blockSize = BamUtil.toInt(read(4));
            byte[] block = read(blockSize);
            result = alignmentRecordCodec.decode(block);
        } catch (EOFException eofe) {
            result = null;
        }

        return result;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
}
