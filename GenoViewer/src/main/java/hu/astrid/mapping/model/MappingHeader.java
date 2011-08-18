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

package hu.astrid.mapping.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class represents the header section of a mapping file.
 */
public class MappingHeader {

	/**
	 * The list of records in the header.
	 */
	private List<HeaderRecord> records = new ArrayList<HeaderRecord>();

	/**
	 * Creates an empty header.
	 */
	public MappingHeader() {
		
	}

	/**
	 * @return the list of records of the header
	 */
	public List<HeaderRecord> getRecords() {
		return records;
	}

	/**
	 * Adds a record to the header.
	 * @param headerRecord the record to be added
	 */
	public void addRecord(HeaderRecord headerRecord) {
		this.records.add(headerRecord);
	}

        /**
         * @param hType a header record type
         */
        public List<HeaderRecord> isMatch( HeaderRecordType hType ) {

            List<HeaderRecord> result = null;

            for( HeaderRecord hRecord : records )
                if( hRecord.getType() == hType ) {
                    if( result == null )
                        result = new ArrayList<HeaderRecord>();
                    result.add(hRecord);
                }

            return result;

        }

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (HeaderRecord samHrecord : this.records) {
			if (result.length() > 0) {
				result.append('\n');
			}
			result.append(samHrecord.toString());
		}

		return result.toString();
	}

}
