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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Iterable buffered reader implementation.
 */
public class IterableBufferedReader implements Iterable<String> {

	/**
	 * The buffered reader which lines are iterated.
	 */
	private BufferedReader bufferedReader;

	/**
	 * The iterator which iterates though the lines of the buffered reader..
	 */
	private Iterator<String> iterator;

	/**
	 * Creates a new instance which will iterate through the lines of the given
	 * buffered reader.
	 * 
	 * @param reader
	 *            the buffered reader to iterate through
	 */
	public IterableBufferedReader(BufferedReader reader) {
		bufferedReader = reader;
		iterator = new BufferedReaderIterator(bufferedReader);
	}

	/**
	 * Creates a new instance which will iterate through the lines of the given
	 * file.
	 * 
	 * @param file
	 *            the file to iterate through
	 * @throws FileNotFoundException
	 *             if the file does not exists
	 */
	public IterableBufferedReader(File file) throws FileNotFoundException {
		this(new BufferedReader(new FileReader(file)));
	}

	@Override
	public Iterator<String> iterator() {
		return iterator;
	}

	/**
	 * The buffered reader iterator implementation.
	 */
	private class BufferedReaderIterator implements Iterator<String> {

		private BufferedReader br;

		private String line;

		public BufferedReaderIterator(BufferedReader reader) {
			br = reader;
			advance();
		}

		@Override
		public boolean hasNext() {
			return line != null;
		}

		@Override
		public String next() {
			String nextLine = line;
			advance();
			return nextLine;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Remove not supported on BufferedReader iteration.");
		}

		private void advance() {
			try {
				line = br.readLine();
			} catch (IOException e) {
				line = null;
			}

			if ((line == null) && (bufferedReader != null)) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					bufferedReader = null;
					// Logger.getLogger(BufferedReaderIterable.class.getName()).log(Level.SEVERE,
					// null, ex);
				}
			}
		}

	}

}
