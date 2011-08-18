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
 * An instance of this class represents an optional tag of an alignment record
 * in a mapping file.
 */
public class OptionalTag {

	/**
	 * The name of the tag.
	 */
	private String tagName;

	/**
	 * The value type of the tag.
	 */
	private char valueType;

	/**
	 * The value of the tag.
	 */
	private String value;

	/**
	 * Creates a new optional tag instance.
	 */
	public OptionalTag() {
	}

	/**
	 * @return the name of the tag
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName
	 *            the name of the tag
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the value type of the tag
	 */
	public char getValueType() {
		return valueType;
	}

	/**
	 * @param valueType
	 *            the value type of the tag
	 */
	public void setValueType(char valueType) {
		this.valueType = valueType;
	}

	/**
	 * @return the value of the tag
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value of the tag
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new StringBuilder(tagName).append(":").append(valueType).append(
				":").append(value).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof OptionalTag)) {
			return false;
		}
		
		OptionalTag other = (OptionalTag) obj;
		if (!this.tagName.equals(other.getTagName())) {
			return false;
		}
		if (this.valueType != other.getValueType()) {
			return false;
		}
		if (!this.value.equals(other.getValue())) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return tagName.hashCode() * 31 + value.hashCode();
	}

}
