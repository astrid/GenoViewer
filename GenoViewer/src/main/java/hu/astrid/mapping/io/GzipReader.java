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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * BGZF-compressed file reader which can return bytes from the uncompressed
 * stream.
 */
public class GzipReader extends MultiMemberGZIPInputStream {

	/**
	 * Creates a reader instance for the given input stream.
	 * @param in the input stream to read from
	 * @throws IOException if an I/O error occurs
	 */
	public GzipReader(InputStream in) throws IOException {
		super(in);
	}

	/**
	 * Reads up to len bytes of data from this input stream.
	 * 
	 * @param len
	 *            count of bytes which will read
	 * 
	 * @throws EOFException
	 *             if no more data because the end of the stream has been
	 *             reached
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected byte[] read(int len) throws EOFException, IOException {

		byte[] b = new byte[len];

		int r = read(b);

                while( r < len ) {

                    int n = (len - r);

                    byte[] tmp = new byte[n];

                    n = read(tmp);

                    if (n == -1)
                            throw new EOFException();

                    System.arraycopy(tmp, 0, b, r, n);

                    r += n;

                }

		return b;

	}

}
