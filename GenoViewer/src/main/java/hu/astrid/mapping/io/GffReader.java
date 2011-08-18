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

import hu.astrid.core.Strand;
import hu.astrid.mapping.exception.GffFileFormatException;
import hu.astrid.mapping.model.GffRecord;
import hu.astrid.mapping.model.Phase;
import hu.astrid.mapping.util.BamUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Sequential GFF file reader implementation.Supports GFFv1, GFFv2, GFFv3 formats
 * GFFv3 validator can be found @ http://modencode.oicr.on.ca/cgi-bin/validate_gff3_online
 */
public class GffReader {
	protected static final char FIELD_DELIM = '\t';
	protected static final char TAG_DELIM = ';';
	protected static final char VALUE_DELIM = ',';
	protected static final char TAG_OPERATOR = '=';
	protected static final char VALUE_MARK = '%';
	protected static final String COMMENT = "#";
	protected static final String META_INFORMATION = "##";
	protected static final String FORWARD_REFERENCES_RESOLVED = "###";
	protected static final String VERSION_LABEL = "gff-version";
	private BufferedReader reader;
	private String nextLine;
	private Integer versionNumber;
	private static final Logger logger = Logger.getLogger(GffReader.class);
	private Map<String, String> gffMetaInformation;

	/**
	 * Creates a new reader which is able to load GFF file records using the
	 * given reader.
	 *
	 * @param reader the reader
	 */
	public GffReader(Reader reader) {
		if (!(reader instanceof BufferedReader))
			reader = new BufferedReader(reader);
		this.reader = (BufferedReader) reader;
		this.versionNumber = null;
		this.gffMetaInformation = new HashMap<String, String>();
	}

	/**
	 * Creates a new reader which is able to load GFF file records from the
	 * given file.
	 *
	 * @param file the file
	 *
	 * @throws FileNotFoundException
	 *              if the file is not exist
	 */
	public GffReader(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	/**
	 * This method reads version of the GFF file.
	 *
	 * @throws GffFileFormatException
	 *              if version of the GFF file is incorrect
	 */
	private void checkVersion()throws GffFileFormatException {

		if (gffMetaInformation == null || !gffMetaInformation.containsKey(VERSION_LABEL) || versionNumber == null) {

			throw new GffFileFormatException("Cannot detect GFF version number in header");
		}

		if (!(versionNumber == 1 || versionNumber == 2 || versionNumber == 3)) {

			throw new GffFileFormatException("Invalid version" + versionNumber);
		}
	}
	
	/**
	 * This method parses an GFF file record string.
	 *
	 * @param record represents an GFF file record
	 *
	 * @return instance of GffRecord which represent an record of GFF
	 *
	 * @throws GffFileFormatException
	 *              the GFF file format is incorrect.
	 */
	private GffRecord parseRecord(String record) throws GffFileFormatException {

		if (record == null || record.isEmpty()) {

			return null;
		}
		//# means comment, ## means meta-information, ### means that all forward references have been resolved
		else if (record.startsWith("#")) {

			//forward references resolved, doesn't store this info
			if (record.startsWith(FORWARD_REFERENCES_RESOLVED)) {
				;
			}
			//meta-information found
			else if (record.startsWith(META_INFORMATION)) {
				processMetaInformation(record.substring(META_INFORMATION.length()));
			}
			//comment found, doesn't store this info
			else {
				;
			}

			return null;
		}
		else {
			checkVersion();
			GffRecord result = null;
			result = new GffRecord();
			String[] fields = BamUtil.getTokens(record, String.valueOf(FIELD_DELIM));
			if (fields.length < 9) {
				throw new GffFileFormatException("Field length:" + fields.length);
			}
			result.setSeqId(fields[0]);
			result.setSource(fields[1]);
			result.setType(fields[2]);
			result.setStart(Integer.valueOf(fields[3]).intValue());
			result.setEnd(Integer.valueOf(fields[4]).intValue());
			result.setScore(fields[5]);
			Strand strand = Strand.getInstance(fields[6].charAt(0));
			result.setStarnd(strand);
			Phase phase = Phase.getInstance(fields[7].charAt(0));
			result.setPhase(phase);
			Map<String, List<String>> attribTags;
			if (versionNumber == 1) {
				List<String> list = new ArrayList<String>();
				list.add(fields[8]);
				attribTags = new HashMap<String, List<String>>();
				attribTags.put("Group", list);
			} else {
				attribTags = parseAttributes(fields[8]);
			}
			result.addAttributes(attribTags);
			return result;
		}
	}

	/**
	 * Return the next GFF record from the GFF file, or null if the end of the
	 * reader has been reached.
	 *
	 * @return the next GFF record
	 *
	 * @throws IOException
	 *              if an I/O error occurs
	 *
	 * @throws GffFileFormatException
	 *              the GFF file format is incorrect.
	 */
	public GffRecord nextRecord() throws IOException, GffFileFormatException {
		
		if ((nextLine = reader.readLine()) == null) {
			reader.close();
			return null;
		}

		GffRecord result = null;

		while (true) {

			if (nextLine == null) {
				reader.close();
				return null;
			}

			result = parseRecord(nextLine);

			if (result == null) {
				nextLine = reader.readLine();
			}
			else {
				return result;
			}
		}
		/*
		GffRecord result = null;
		if (!ver)
			readVersion();
		if (nextLine != null) {
			result = parseRecord(nextLine);
			System.out.println("Result is:" + result);
			nextLine = reader.readLine();
		} else {
			close();
		}
		return result;
		 *
		 */
	}

	/**
	 * This method closes the reader.
	 *
	 * @throws IOException
	 *              if an I/O error occurs
	 */
	public void close() throws IOException {
		if (reader != null) {
			reader.close();
			reader = null;
		}
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private String decodeAttributesTagValue(String value) {
		List<Integer> indexes = new ArrayList<Integer>();
		int pos = 0;
		int fromIndex = 0;
		do {
			pos = value.indexOf(VALUE_MARK, fromIndex);
			if (pos > -1) {
				indexes.add(pos);
				fromIndex = (pos + 3);
			}
		} while (pos > -1);
		if (indexes.size() > 0) {
			pos = 0;
			StringBuilder s = new StringBuilder();
			for (Integer i : indexes) {
				String hex = value.substring(i + 1, i + 3);
				char code = (char) Integer.parseInt(hex, 16);
				s.append(value.substring(pos, i));
				pos = i + 3;
				s.append(code);
			}
			if (pos < value.length())
				s.append(value.substring(pos));
			value = s.toString();
		}
		return value;
	}
	
	/**
	 * This method parses an attributes field string.
	 * <p>{@link http://www.sequenceontology.org/gff3.shtml}
	 *
	 * @param attributes attribute of the GFF file field is values.
	 *
	 * @return map of attribute tags, where the key a name of tag
	 *
	 * @throws GffFileFormatException
	 *               if an Gff file format error occurs
	 *				 no GFFFileFormatException if empty values or duplicated tags found, instead of correction done
	 */
	private Map<String, List<String>> parseAttributes(String attributes) throws GffFileFormatException {
		//TODO A v2-ben valahol spacek vannak a határolók előtt, vajon jó így?
		attributes = attributes.replaceAll(" ", "");
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		String[] attributeTags = BamUtil.getTokens(attributes, String.valueOf(TAG_DELIM));
		for (String tag : attributeTags) {
			String[] unit = BamUtil.getTokens(tag, String.valueOf(TAG_OPERATOR));
			String[] values = null;
			if (unit.length == 1) {

				logger.warn("Tag-value pair expected, but tag [" + unit[0]+ "] doesn't have value component. Setting value to empty string.");
				values = new String[] {""};
			}
			else {

				values = BamUtil.getTokens(unit[1], String.valueOf(VALUE_DELIM));
			}

			List<String> valueList = new ArrayList<String>(values.length);
			for (String val : values) {
				valueList.add(decodeAttributesTagValue(val));
			}

			if (result.containsKey(unit[0])) {

				logger.warn("Malformed record, duplicated tag found: [" +  unit[0] + "] with value(s): " + Arrays.toString(valueList.toArray()) + ".\n" +
						"\tValue(s) stored as if it were multiple values with the same type. Example: valid formatting:\"Parent=AF2312,AB2812,abc-3\" invalid format:" +
						"\"Parent=AF2312;Parent=AB2812;Parent=abc-3\"");

				result.get(unit[0]).addAll(valueList);
			}
			else {

				result.put(unit[0], valueList);
			}
		}
		return result;
	}

	public Map<String, String> getMetaInformation() {

		return new HashMap<String, String>(gffMetaInformation);
	}

	private void processMetaInformation(String metaInformation) {

		if (metaInformation == null) {

			return;
		}

		String[] tokens = metaInformation.split("[ \\t]");

		if (tokens.length < 2) {

			logger.error("Invalid format of meta information in GFF file. The problematic record was:" + metaInformation);
			return;
		}

		if (tokens[0].equals(VERSION_LABEL)) {
			
			try {
				
				Integer verNumber = Integer.parseInt(tokens[1]);

				if (verNumber == 1 || verNumber == 2 || verNumber == 3) {

					versionNumber = new Integer(verNumber);
					gffMetaInformation.put(VERSION_LABEL, verNumber.toString());
				}
			} catch (NumberFormatException exc) {
				
				logger.error("Cannot parse GFF version number from record:" + metaInformation, exc);
			}
		}
		else {

			logger.info("Unknown meta information key:" + tokens[0]);
		}
	}
}
