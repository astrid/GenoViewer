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
import hu.astrid.mapping.model.HeaderRecord;
import hu.astrid.mapping.model.HeaderRecordType;
import hu.astrid.mapping.model.HeaderTag;
import hu.astrid.mapping.model.HeaderTagType;
import hu.astrid.mapping.model.OptionalTag;
import hu.astrid.mapping.util.BamUtil;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Utility class which provides parser methods for header and alignment records
 * of mapping files.
 */
public class MappingFileParser {

	public static final Charset ASCII_CHARSET = Charset.forName("US-ASCII");

	private MappingFileParser() {
	}

	/**
	 * Parses a header record of the mapping file.
	 *
	 * @param record
	 *            the string representation of the record to be parsed
	 * @return the parsed header record instance
	 * @throws IllegalHeaderValueException
	 *             if a header tag's value is invalid
	 * @throws IllegalHeaderTagException
	 *             if a header tag is illegal
	 * @throws DuplicateTagException
	 *             if a forbidden tag duplication occurs
	 * @throws MissingTagException
	 *             if a mandatory tag is missing
	 */
	public static HeaderRecord parseHeaderRecord(String record)
		throws MappingFileFormatException {

		String[] headerFields = BamUtil.getTokens(record, MappingFile.FIELD_SEPARATOR);

		String hType = headerFields[0].substring(1, 3);

		HeaderRecordType samHRT = HeaderRecordType.getInstance(hType);

		HeaderRecord result = new HeaderRecord(samHRT);

		if (!hType.equals("CO")) {

			for (int i = 1; i < headerFields.length; i++) {
				int separatorIndex = headerFields[i].indexOf(MappingFile.TAG_SEPARATOR);
				String[] tagFields = new String[2];
				tagFields[0] = headerFields[i].substring(0, separatorIndex);
				tagFields[1] = headerFields[i].substring(separatorIndex + 1);

				HeaderTagType samHTT = HeaderTagType.getInstance(tagFields[0]);

				if (!samHRT.isAllowed(samHTT)) {
					throw new MappingFileFormatException(
						"Illegal header tag: [" + samHTT.toString() + "]");
				}

				if (!samHTT.isAllowed(tagFields[1])) {
					throw new MappingFileFormatException("Illegal header value: ["
						+ samHTT.toString() + ":" + tagFields[1] + "]");
				}

				HeaderTag samHTag = new HeaderTag(samHTT, tagFields[1]);

				result.addTag(samHTag);

			}

		} else {

			HeaderTag samHTag = new HeaderTag(headerFields[1]);

			result.addTag(samHTag);

		}

		samHRT.verify(result.getTags());

		return result;
	}

	/**
	 * Parses an alignment record of the mapping file.
	 *
	 * @param record
	 *            the string representation of the record to be parsed
	 * @return the parsed alignment record instance
	 * @throws AlignmentRecordFormatException
	 *             if the format of the alignment record is incorrect
	 * @throws OptionalTagFormatException
	 *             if the format of an optional record is incorrect
	 */
	public static AlignmentRecord parseAlignmentRecord(String record)
		throws MappingFileFormatException {

		AlignmentRecord result = new AlignmentRecord();

		String[] bodyFields = BamUtil.getTokens(record, MappingFile.FIELD_SEPARATOR);

		if (bodyFields.length < 10) {
			throw new MappingFileFormatException("Missing alignment record field! record:[" + record + "]");
		}

		try {

			result.setQueryName(bodyFields[0]);
			result.setFlag(new Short(bodyFields[1]));
			result.setReferenceName(bodyFields[2]);
			result.setPosition(new Integer(bodyFields[3]));
			result.setMappingQuality(new Short(bodyFields[4]).byteValue());
			result.setCigar(bodyFields[5]);
			result.setMateReferenceName(bodyFields[6]);
			result.setMatePosition(new Integer(bodyFields[7]));
			result.setInsertSize(new Integer(bodyFields[8]));
			result.setSequence(bodyFields[9]);
			result.setQuality(bodyFields[10]);

		} catch (NumberFormatException nfe) {
			throw new MappingFileFormatException("Alignment record number format exception!");

		}

		if (bodyFields.length > 11) {

			for (int i = 11; i < bodyFields.length; i++) {

				String[] opBodyTag = BamUtil.getTokens(bodyFields[i], MappingFile.TAG_SEPARATOR);

				if (opBodyTag.length == 3) {

					OptionalTag opTag = new OptionalTag();
					opTag.setTagName(opBodyTag[0]);
					opTag.setValueType(opBodyTag[1].charAt(0));
					opTag.setValue(opBodyTag[2]);

					result.addOptionalTag(opTag);

				} else {
					throw new MappingFileFormatException("Illegal optional tag fomat:" + bodyFields[i]);
				}

			}

		}

		return result;

	}

	public static boolean isValidBamHeader(byte[] header) {
		int totalLength = 12;
		if (header.length < totalLength) {
			return false;
		}
		if (header[0] != (byte) 'B' && header[1] != (byte) 'A' && header[2] != (byte) 'M' && header[3] != 1) {
			return false;
		}

		totalLength += BamUtil.toInt(Arrays.copyOfRange(header, 4, 8));
		if (header.length < totalLength) {
			return false;
		}

		int pointer = totalLength;
		int referenceCount = BamUtil.toInt(Arrays.copyOfRange(header, pointer - 4, pointer));
		for (int i = 0; i < referenceCount; ++i) {
			totalLength += 8;
			if (header.length < totalLength) {
				return false;
			}
			int length = BamUtil.toInt(Arrays.copyOfRange(header, pointer, pointer + 4));
			totalLength += length;
			if (header.length < totalLength) {
				return false;
			}
			pointer = totalLength;
		}

		return true;
	}

	public static BamHeader parseBamHeader(byte[] header)
		throws MappingFileFormatException {
		BamHeader bamHeader = new BamHeader();
		int headerLength = BamUtil.toInt(Arrays.copyOfRange(header, 4, 8));
		String headerText = new String(Arrays.copyOfRange(header, 8, 8 + headerLength), ASCII_CHARSET);

		String[] records = BamUtil.getTokens(headerText, "\n");
		for (String record : records) {
			if (record.length() > 0) {
				if (record.charAt(record.length() - 1) == ((char) 13)) {
					record = record.substring(0, record.length() - 1);
				}
				if (record.length() > 0) {
					bamHeader.addRecord(parseHeaderRecord(record));
				}
			}
		}

		int offset = 8 + headerLength;
		int referenceCount = BamUtil.toInt(Arrays.copyOfRange(header, offset, offset + 4));
		offset += 4;

		for (int i = 0; i < referenceCount; ++i) {
			int referenceNameLength = BamUtil.toInt(Arrays.copyOfRange(header, offset, offset + 4));
			offset += 4;
			String referenceName = new String(Arrays.copyOfRange(header, offset, offset + referenceNameLength - 1));
			offset += referenceNameLength;
			int referenceLength = BamUtil.toInt(Arrays.copyOfRange(header, offset, offset + 4));
			offset += 4;
			bamHeader.addReference(referenceName, referenceLength);
		}

		return bamHeader;
	}
}
