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

import hu.astrid.mapping.io.MappingFile;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class represents a header record in the header section of
 * a mapping file.
 */
public class HeaderRecord {

	/**
	 * Prefix of header record lines in mapping files.
	 */
	public static final String HEADER_RECORD_PREFIX = "@";

	/**
	 * The type of the record.
	 */
	private HeaderRecordType type;

	/**
	 * The list of tags of this record.
	 */
	private List<HeaderTag> tags = new ArrayList<HeaderTag>();

	/**
	 * Creates a new record instance with the specified type.
	 * 
	 * @param type
	 *            the type of the record
	 */
	public HeaderRecord(HeaderRecordType type) {
		this.type = type;
	}

	/**
	 * @return the type of the record
	 */
	public HeaderRecordType getType() {
		return type;
	}

	/**
	 * @return the list of tags of the record
	 */
	public List<HeaderTag> getTags() {
		return tags;
	}

        public String getTagValue( HeaderTagType hTagType ) {

            for( HeaderTag hTag : tags )
                if( hTag.getType() == hTagType )
                    return hTag.getValue();

            return null;

        }

	/**
	 * Adds a tag to the record.
	 * 
	 * @param tag
	 *            the tag to be added
	 */
	public void addTag(HeaderTag tag) {
		this.tags.add(tag);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(HEADER_RECORD_PREFIX)
				.append(type.toString());

		for (HeaderTag samHTag : this.tags) {
			result.append(MappingFile.FIELD_SEPARATOR).append(
					samHTag.toString());
		}

		return result.toString();

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof HeaderRecord)) {
			return false;
		}

		HeaderRecord other = (HeaderRecord) obj;
		if (!this.getTags().equals(other.getTags())) {
			return false;
		}

		if (this.getType()!=other.getType()) {
			return false;
		}

		return true;
	}
	
	@Override
	public int hashCode() {
		return type.hashCode() * 31 + tags.hashCode();
	}

}
