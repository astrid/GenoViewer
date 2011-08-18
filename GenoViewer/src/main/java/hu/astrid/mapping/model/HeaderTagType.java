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
 * This enumeration represents the possible tag types in mapping file headers.
 */
public enum HeaderTagType {

	/**
	 * Represents the VN (file format version) tag type.
	 */
	VN {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the SO (sort order) tag type.
	 */
	SO {
		@Override
		public boolean isAllowed(String value) {
			try {
				valueOf(SortOrder.class, value.toUpperCase());
			} catch (IllegalArgumentException iae) {
				return false;
			}
			return true;
		}
	},

	/**
	 * Represents the GO (group order) tag type.
	 */
	GO {
		@Override
		public boolean isAllowed(String value) {
			try {
				valueOf(GroupOrder.class, value.toUpperCase());
			} catch (IllegalArgumentException iae) {
				return false;
			}
			return true;
		}
	},

	/**
	 * Represents the SN (sequence name) tag type.
	 */
	SN {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the LN (sequence length) tag type.
	 */
	LN {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the AS (genome assembly identifier) tag type.
	 */
	AS {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the M5 (MD5 checksum) tag type.
	 */
	M5 {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the UR (sequence URI) tag type.
	 */
	UR {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the SP (species) tag type.
	 */
	SP {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the ID (read group identifier) tag type.
	 */
	ID {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the SM (sample) tag type.
	 */
	SM {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the LB (library) tag type.
	 */
	LB {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the DS (description) tag type.
	 */
	DS {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the PU (platform unit) tag type.
	 */
	PU {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the PI (predicted median insert size) tag type.
	 */
	PI {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the CN (sequencing center) tag type.
	 */
	CN {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the DT (sequencing date) tag type.
	 */
	DT {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the PL (platform or technology) tag type.
	 */
	PL {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the CL (command line) tag type.
	 */
	CL {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	},

	/**
	 * Represents the empty or null tag type for CO the record type.
	 */
	NULL {
		@Override
		public boolean isAllowed(String value) {
			return true;
		}
	};

	/**
	 * Returns a tag type corresponding to a string value.
	 * @param type the string value to be converted
	 * @return the corresponding tag type
	 * @throws IllegalArgumentException if no corresponding tag type exists
	 */
	public static HeaderTagType getInstance(String type)
			throws IllegalArgumentException {
		return valueOf(type);
	}

	@Override
	public String toString() {
		return this.name();
	}

	/**
	 * Determines whether a given string value is allowed as the value of the tag or not.
	 * @param value the given string value
	 * @return true if the value is allowed, false otherwise
	 */
	abstract public boolean isAllowed(String value);

}
