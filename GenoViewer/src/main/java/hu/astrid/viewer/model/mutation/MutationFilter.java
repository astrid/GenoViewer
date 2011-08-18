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

package hu.astrid.viewer.model.mutation;

/**
 *
 * @author onagy
 */
public final class MutationFilter {

	private final boolean isInterestedInDeletion;
	private final boolean isInterestedInInsertion;
	private final boolean isInterestedInSNP;
	private final boolean isInterestedInMNP;
	private static final MutationFilter allAllowedMutationFilter;
	private static final MutationFilter allDeniedMutationFilter;
	private static final MutationFilter deletionAllowedMutationFilter;
	private static final MutationFilter insertionAllowedMutationFilter;
	private static final MutationFilter snpAllowedMutationFilter;
	private static final MutationFilter mnpAllowedMutationFilter;

	static {

		allAllowedMutationFilter = new MutationFilter(true, true, true, true);
		allDeniedMutationFilter = new MutationFilter(false, false, false, false);
		deletionAllowedMutationFilter = new MutationFilter(true, false, false, false);
		insertionAllowedMutationFilter = new MutationFilter(false, true, false, false);
		snpAllowedMutationFilter = new MutationFilter(false, false, true, false);
		mnpAllowedMutationFilter = new MutationFilter(false, false, false, true);
	}

	private MutationFilter() {

		isInterestedInDeletion = true;
		isInterestedInInsertion = true;
		isInterestedInSNP = true;
		isInterestedInMNP = true;
	}

	private MutationFilter(boolean isInterestedInDeletion, boolean isInterestedInInsertion, boolean isInterestedInSNP, boolean isInterestedInMNP) {
		this.isInterestedInDeletion = isInterestedInDeletion;
		this.isInterestedInInsertion = isInterestedInInsertion;
		this.isInterestedInSNP = isInterestedInSNP;
		this.isInterestedInMNP = isInterestedInMNP;
	}

	/**
	 *
	 * @return
	 */
	public static MutationFilter instanceAllAllowed() {

		return MutationFilter.allAllowedMutationFilter;
	}

	/**
	 *
	 * @return
	 */
	public static MutationFilter instanceAllDenied() {

		return MutationFilter.allDeniedMutationFilter;
	}

	/**
	 *
	 * @param isInterestedInDeletion
	 * @param isInterestedInInsertion
	 * @param isInterestedInSNP
	 * @param isInterestedInMNP
	 * @return
	 */
	public static MutationFilter instanceCustom(boolean isInterestedInDeletion, boolean isInterestedInInsertion, boolean isInterestedInSNP, boolean isInterestedInMNP) {

		return new MutationFilter(isInterestedInDeletion, isInterestedInInsertion, isInterestedInSNP, isInterestedInMNP);
	}

	/**
	 *
	 * @return
	 */
	public static MutationFilter instanceOnlyDeletionAllowed() {

		return deletionAllowedMutationFilter;
	}

	/**
	 *
	 * @return
	 */
	public static MutationFilter instanceOnlyInsertionAllowed() {

		return insertionAllowedMutationFilter;
	}

	/**
	 *
	 * @return
	 */
	public static MutationFilter instanceOnlySNPAllowed() {

		return snpAllowedMutationFilter;
	}

	/**
	 *
	 * @return
	 */
	public static MutationFilter instanceOnlyMNPAllowed() {

		return mnpAllowedMutationFilter;
	}

	/**
	 *
	 * @return
	 */
	public boolean isIsInterestedInDeletion() {

		return isInterestedInDeletion;
	}

	/**
	 *
	 * @return
	 */
	public boolean isIsInterestedInInsertion() {

		return isInterestedInInsertion;
	}

	/**
	 *
	 * @return
	 */
	public boolean isIsInterestedInMNP() {

		return isInterestedInMNP;
	}

	/**
	 *
	 * @return
	 */
	public boolean isIsInterestedInSNP() {
		
		return isInterestedInSNP;
	}
}
