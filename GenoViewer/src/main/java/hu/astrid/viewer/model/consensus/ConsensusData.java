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

package hu.astrid.viewer.model.consensus;

import hu.astrid.viewer.model.mutation.Mutation;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 *
 * @author zsdoma, mkiss 
 */
public class ConsensusData {

	private List<Mutation> mutations;
	private String sequence;
	private String colorSequence;
	private static final Logger logger = Logger.getLogger(ConsensusData.class);

	public ConsensusData(List<Mutation> mutations, String sequence, String colorSequence) {
		super();
		this.mutations = mutations;
		this.sequence = sequence;
		this.colorSequence = colorSequence;
	}

	@Override
	public String toString() {
		return sequence + " " + colorSequence + ":\n" + mutations;
	}

	public List<Mutation> getMutations() {
		return mutations;
	}

	public String getSequence() {
		return sequence;
	}

	public String getColorSequence() {
		return colorSequence;
	}

	public List<Mutation> getTransformationsByPosition(int start, int end) {
		List<Mutation> subList = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation.getStartPos() >= start && mutation.getStartPos() <= end) {
				subList.add(new Mutation(mutation));
			}
		}
		return subList;
	}

}
