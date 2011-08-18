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
import hu.astrid.mapping.model.MappingBody;
import hu.astrid.mapping.model.MappingHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * An instance of this class represents the content of a mapping file.
 */
public class MappingFile {

	/**
	 * The field separator of mapping files.
	 */
	public static final String FIELD_SEPARATOR = "\t";

	/**
	 * The tag separator of mapping files.
	 */
	public static final String TAG_SEPARATOR = ":";

	/**
	 * The header section of the mapping file.
	 */
	private MappingHeader header;

	/**
	 * The alignment section of the file.
	 */
	private MappingBody body;

	/**
	 * Creates an empty mapping file.
	 */
	public MappingFile() {
		this.header = null;
		this.body = null;
	}

	/**
	 * @return the header section of the mapping file
	 */
	public MappingHeader getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header section of the mapping file
	 */
	public void setHeader(MappingHeader header) {
		this.header = header;
	}

	/**
	 * @return the alignment section of the mapping file
	 */
	public MappingBody getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the alignment section of the mapping file
	 */
	public void setBody(MappingBody body) {
		this.body = body;
	}

	/**
	 * Loads the content of a mapping file.
	 * 
	 * @param file
	 *            the file to be loaded
	 * @throws IllegalHeaderValueException
	 *             if a header tag's value is invalid
	 * @throws IllegalHeaderTagException
	 *             if a header tag is illegal
	 * @throws DuplicateTagException
	 *             if a forbidden tag duplication occurs
	 * @throws MissingTagException
	 *             if a mandatory tag is missing
	 * @throws IllegalHeaderAccessException
	 *             if an illegal access to the header occurs
	 * @throws AlignmentRecordFormatException
	 *             if the format of an alignment record is incorrect
	 * @throws OptionalTagFormatException
	 *             if the format of an optional tag is incorrect
	 * @throws FileNotFoundException
	 *             if the mapping file does not exists
	 */
	public void load(File file) throws MappingFileFormatException, IOException {
		FileReader fileReader = new FileReader(file);
		try {
			load(fileReader);
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				
			}
		}
	}

	/**
	 * Loads mapping content from a given reader.
	 * 
	 * @param reader
	 *            the reader to load from
	 * @throws IllegalHeaderValueException
	 *             if a header tag's value is invalid
	 * @throws IllegalHeaderTagException
	 *             if a header tag is illegal
	 * @throws DuplicateTagException
	 *             if a forbidden tag duplication occurs
	 * @throws MissingTagException
	 *             if a mandatory tag is missing
	 * @throws IllegalHeaderAccessException
	 *             if an illegal access to the header occurs
	 * @throws AlignmentRecordFormatException
	 *             if the format of an alignment record is incorrect
	 * @throws OptionalTagFormatException
	 *             if the format of an optional tag is incorrect
	 */
	public void load(Reader reader) throws MappingFileFormatException, IOException {

		SamReader samReader = new SamReader(reader);
		setHeader(samReader.readHeader());
		setBody(new MappingBody());

		AlignmentRecord bodyRecord = null;
		while ((bodyRecord = samReader.nextRecord()) != null) {
			body.addRecord(bodyRecord);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder(header.toString()).append("\n").append(
				body.toString()).toString();
	}

}
