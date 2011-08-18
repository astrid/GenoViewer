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

/**
 * An instance of this class represents a tag in the header section of a mapping
 * file.
 */
public class HeaderTag {

	/**
	 * The type of the tag.
	 */
	private HeaderTagType type;

	/**
	 * The value of the tag.
	 */
	private String value;

	/**
	 * Creates a NULL tag with the specified comment text for CO records.
	 * 
	 * @param value
	 *            the comment text
	 */
	public HeaderTag(String value) {
		this(HeaderTagType.NULL, value);
	}

	/**
	 * Creates a new tag instance with the given type and value.
	 * 
	 * @param type
	 *            the tag type
	 * @param value
	 *            the value of the tag
	 */
	public HeaderTag(HeaderTagType type, String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * @return the tag type
	 */
	public HeaderTagType getType() {
		return type;
	}

	/**
	 * @return the value of the tag
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String samHTT = type.toString();

		if (!samHTT.equals("NULL")) {
			result.append(samHTT).append(":");
		}

		result.append(value);
		return result.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof HeaderTag)) {
			return false;
		}

		HeaderTag other = (HeaderTag) obj;
		if (!this.getValue().equals(other.getValue())) {
			return false;
		}

		if (this.getType()!=other.getType()) {
			return false;
		}

		return true;
	}
	
	@Override
	public int hashCode() {
		return type.hashCode() * 31 + value.hashCode();
	}

}
