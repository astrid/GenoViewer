
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

import java.util.List;

/**
 * Class for filtering {@link MutationDialog} table model.
 * @author Szuni
 */
public class MutationTableFilter {

	/**{@link MutationType}s allowed in filtered data*/
	public final List<MutationType> allowedTypes;
	/**Minimum allowed coverage in filtered data [0-1]*/
	public final double minCoverage;
	/**Maximum allowed coverage in filtered data [0-1]*/
	public final double maxCoverage;

	/**
	 * Create a new instance if filter
	 * @param allowedTypes
	 * @param minCoverage [0-1]
	 * @param maxCoverage [0-1]
	 */
	public MutationTableFilter(List<MutationType> allowedTypes, double minCoverage, double maxCoverage) {
		this.allowedTypes = allowedTypes;
		this.minCoverage = minCoverage;
		this.maxCoverage = maxCoverage;
	}

	@Override
	public String toString() {
		return "MutationTableFilter{" + "allovedTypes=" + allowedTypes + " minCoverage=" + minCoverage + " maxCoverage=" + maxCoverage + '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MutationTableFilter)) {
			return false;
		}
		final MutationTableFilter other = (MutationTableFilter) obj;
		return other.allowedTypes.equals(this.allowedTypes) && other.maxCoverage==this.maxCoverage && other.minCoverage == this.minCoverage;
	}

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = allowedTypes != null ? allowedTypes.hashCode() : 0;
        temp = minCoverage != +0.0d ? Double.doubleToLongBits(minCoverage) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = maxCoverage != +0.0d ? Double.doubleToLongBits(maxCoverage) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
