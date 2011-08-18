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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * BGZF decompressor implementation which can uncompress BGZF-compressed block
 * with the necessary checks.
 */
public class BgzfDecompressor {

	/**
	 * The inflater instance which makes the decompression itself.
	 */
	private final Inflater inflater = new Inflater(true);

	/**
	 * The CRC32 instance to perform the CRC check.
	 */
	private final CRC32 crc32 = new CRC32();

	/**
	 * Creates a new decompressor instance.
	 */
	public BgzfDecompressor() {

	}

	/**
	 * Decompresses the given BGZF block.
	 * 
	 * @param block
	 *            the block to be decompressed
	 * @return the bytes of the uncompressed data
	 * @throws DataFormatException
	 *             if the decompression or the CRC check fails
	 */
	public byte[] decompress(BgzfBlock block) throws DataFormatException {
		ByteBuffer byteBuff = ByteBuffer.wrap(block.getCompressedData(), 0,
				block.getDeflatedSize() + 26);
		byteBuff.order(ByteOrder.LITTLE_ENDIAN);
		byteBuff.position(byteBuff.position() + 18 + block.getDeflatedSize());
		int expectedCrc = byteBuff.getInt();

		inflater.reset();
		inflater.setInput(block.getCompressedData(), 18, block
				.getDeflatedSize());
		byte[] resultByteArray = new byte[block.getInputSize()];
                
		inflater.inflate(resultByteArray, 0, block.getInputSize());

		crc32.reset();
		crc32.update(resultByteArray, 0, block.getInputSize());
		long crc = crc32.getValue();

		if ((int) crc != expectedCrc)
			throw new DataFormatException("CRC mismatch");

		return resultByteArray;
	}

}
