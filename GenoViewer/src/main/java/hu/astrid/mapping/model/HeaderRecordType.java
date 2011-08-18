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

import static hu.astrid.mapping.model.HeaderTagType.AS;
import static hu.astrid.mapping.model.HeaderTagType.CL;
import static hu.astrid.mapping.model.HeaderTagType.CN;
import static hu.astrid.mapping.model.HeaderTagType.DS;
import static hu.astrid.mapping.model.HeaderTagType.DT;
import static hu.astrid.mapping.model.HeaderTagType.GO;
import static hu.astrid.mapping.model.HeaderTagType.ID;
import static hu.astrid.mapping.model.HeaderTagType.LB;
import static hu.astrid.mapping.model.HeaderTagType.LN;
import static hu.astrid.mapping.model.HeaderTagType.M5;
import static hu.astrid.mapping.model.HeaderTagType.NULL;
import static hu.astrid.mapping.model.HeaderTagType.PI;
import static hu.astrid.mapping.model.HeaderTagType.PL;
import static hu.astrid.mapping.model.HeaderTagType.PU;
import static hu.astrid.mapping.model.HeaderTagType.SM;
import static hu.astrid.mapping.model.HeaderTagType.SN;
import static hu.astrid.mapping.model.HeaderTagType.SO;
import static hu.astrid.mapping.model.HeaderTagType.SP;
import static hu.astrid.mapping.model.HeaderTagType.UR;
import static hu.astrid.mapping.model.HeaderTagType.VN;
import hu.astrid.mapping.exception.MappingFileFormatException;

import java.util.EnumSet;
import java.util.List;

/**
 * This enumeration represents the possible record types in mapping file
 * headers.
 */
public enum HeaderRecordType {

	/**
	 * Represents the HD (header) record type.
	 */
	HD() {

		@Override
		protected void init() {
			allowedTags = EnumSet.of(VN, SO, GO);
			mandatoryTags = EnumSet.of(VN);
		}

	},

	/**
	 * Represents the SQ (sequence dictionary) record type.
	 */
	SQ() {

		@Override
		protected void init() {
			allowedTags = EnumSet.of(SN, LN, AS, M5, UR, SP);
			mandatoryTags = EnumSet.of(SN, LN);
		}

	},

	/**
	 * Represents the RG (read group) record type.
	 */
	RG() {

		@Override
		protected void init() {
			allowedTags = EnumSet.of(ID, SM, LB, DS, PU, PI, CN, DT, PL);
			mandatoryTags = EnumSet.of(ID, SM);
		}

	},

	/**
	 * Represents the PG (program) record type.
	 */
	PG() {

		@Override
		protected void init() {
			allowedTags = EnumSet.of(ID, VN, CL);
			mandatoryTags = EnumSet.of(ID);
		}

	},

	/**
	 * Represents the CO (comment) records type.
	 */
	CO() {
		@Override
		protected void init() {
			allowedTags = EnumSet.of(NULL);
			mandatoryTags = EnumSet.noneOf(HeaderTagType.class);
		}

	};

	/**
	 * The collection of the tags which are allowed for the record.
	 */
	protected EnumSet<HeaderTagType> allowedTags;

	/**
	 * The collection of the tags which are mandatory for the record.
	 */
	protected EnumSet<HeaderTagType> mandatoryTags;

	/**
	 * Creates a record type instance.
	 */
	private HeaderRecordType() {
		init();
	}

	/**
	 * Returns a record type corresponding to a string value.
	 * 
	 * @param type
	 *            the string value to be converted
	 * @return the corresponding record type
	 * @throws IllegalArgumentException
	 *             if no corresponding record type exists
	 */
	public static HeaderRecordType getInstance(String type)
			throws IllegalArgumentException {
		return valueOf(type);
	}

	/**
	 * Determines whether a given tag type is allowed for the record type or
	 * not.
	 * 
	 * @param headerTagType
	 *            the tag type
	 * @return true if the tag type is allowed for the record type, false
	 *         otherwise
	 */
	public boolean isAllowed(HeaderTagType headerTagType) {
		return this.allowedTags.contains(headerTagType);
	}

	/**
	 * Verifies a tag list for the record type.
	 * 
	 * @param tagList
	 *            the tag list to be verified
	 * @throws DuplicateTagException
	 *             if a forbidden duplication occurs
	 * @throws MissingTagException
	 *             if a mandatory tag is missing
	 */
	public void verify(List<HeaderTag> tagList)
                throws MappingFileFormatException {

		EnumSet<HeaderTagType> nTags = mandatoryTags.clone();
		EnumSet<HeaderTagType> hTags = allowedTags.clone();

		for (HeaderTag headerTag : tagList) {
			HeaderTagType hTagType = headerTag.getType();
			if (nTags.contains(hTagType)) {
				nTags.remove(hTagType);
			}
			if (hTags.contains(hTagType)) {
				hTags.remove(hTagType);
			} else {
				throw new MappingFileFormatException("Duplicate header tag:" + headerTag);
			}
		}

		if (nTags.size() != 0) {
			throw new MappingFileFormatException("Missing header tag:" + nTags);
		}
	}

	/**
	 * @return the allowed tags for the record type
	 */
	public EnumSet<HeaderTagType> getAllowedTags() {
		return this.allowedTags;
	}

	@Override
	public String toString() {
		return this.name();
	}

	/**
	 * Initializes the record type: set the mandatory and allowed tags'
	 * collections.
	 */
	protected abstract void init();

}
