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

package hu.astrid.mapping.exception;

/**
 * Common superclass of exceptions occured during processing a mapping file.
 */
public class MappingFileException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception instance with the given message.
	 * 
	 * @param message
	 *            the message
	 */
	public MappingFileException(String message) {
		super(message);
	}

}
