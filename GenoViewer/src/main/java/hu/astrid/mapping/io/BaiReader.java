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

import hu.astrid.mapping.exception.IndexFileFormatException;
import hu.astrid.mapping.model.BamIndex;
import hu.astrid.mapping.model.Bin;
import hu.astrid.mapping.model.Chunk;
import hu.astrid.mapping.model.ReferenceIndex;
import hu.astrid.mapping.model.VirtualFileOffset;
import hu.astrid.mapping.util.BamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

/**
 * Reader implementation for BAM index files.
 */
public class BaiReader {

    /**
     * The mapped byte buffer to read from.
     */
    private MappedByteBuffer reader;

    /**
     * Creates a new reader instance.
     */
    public BaiReader() {
    	this.reader = null;
    }

    /**
     *
     * Validates the magic string.
     *
     * @return true, if the magic string is valid, otherwise false
     *
     * @throws IOException
     *              if the magic string is invalid
     */
    private boolean validateMagicString() throws IOException {
        byte[] magic = new byte[4];
        reader.get(magic);
        return Arrays.equals(magic, BamUtil.BAI_MAGIC_BYTES);
    }
    
    /**
    *
    * Loads a BAI file's content.
    *
    * @param baiFile the BAI file to be loaded
    *
    * @throws IOException
    *              if an I/O error occurs
    * @throws IndexFileFormatException
    *              if the magic string is incorrect
    */
	public BamIndex load(File baiFile) throws IOException,
			IndexFileFormatException {

		BamIndex result = new BamIndex();
        FileInputStream stream = null;
        FileChannel channel = null;
	MappedByteBuffer reader=null;
        try {
            stream = new FileInputStream(baiFile);
            channel = stream.getChannel();
            reader = channel.map(MapMode.READ_ONLY, 0L, channel.size());
	    this.reader=reader;
            reader.order(ByteOrder.LITTLE_ENDIAN);

            if (!validateMagicString()) {
                throw new IndexFileFormatException("Invalid Magic String!");
            }

            int nRef = reader.getInt();
            for (int i = 0; i < nRef; i++) {
                ReferenceIndex refIndex = new ReferenceIndex();
                int nBin = reader.getInt();

                for (int j = 0; j < nBin; j++) {
                    Bin bin = new Bin(reader.getInt());
                    int nChunk = reader.getInt();

                    for (int k = 0; k < nChunk; k++) {
                        VirtualFileOffset start = new VirtualFileOffset(reader.getLong());
                        VirtualFileOffset end = new VirtualFileOffset(reader.getLong());
                        Chunk chunk = new Chunk(start, end);
                        bin.addChunk(chunk);
                    }
                    
                    refIndex.addBin(bin);
                }

                int nIntv = reader.getInt();
                for (int m = 0; m < nIntv; m++) {
                    VirtualFileOffset ioffset = new VirtualFileOffset(reader.getLong());
                    refIndex.addLinearIndex(ioffset);
                }
                result.addReferenceIndex(refIndex);
            }
        } finally {
            reader = null;
            if (channel != null) {
                channel.close();
            }
            if (stream != null) {
                stream.close();
            }
        }

        return result;
	}

    /**
     *
     * It loads bai file's content.
     *
     * @param baiFile BAI file name of path
     *
     * @throws IOException
     *              if an I/O error occurs
     * @throws IndexFileFormatException
     *              if the magic string is incorrect
     */
	public BamIndex load(String baiFile) throws IOException,
			IndexFileFormatException {
        return this.load(new File(baiFile));
    }
}
