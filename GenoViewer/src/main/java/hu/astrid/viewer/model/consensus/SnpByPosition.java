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

import hu.astrid.viewer.model.mutation.MutationType;
import hu.astrid.core.Nucleotide;
import hu.astrid.viewer.model.ViewerConsensusModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SnpByPosition {
	private int position;
	private Map<Nucleotide, Integer> snpCoverageMap;
	private char prevNuc = 'N';
	private char nextNuc = 'N';
	
	public SnpByPosition(int position, Nucleotide nucleotide) {
		this(position);
		addNucleotideCoverage(nucleotide);
	}

	public SnpByPosition(int position) {
		snpCoverageMap = new HashMap<Nucleotide, Integer>();
		this.setPosition(position);
	}

	public void addNucleotideCoverage(Nucleotide nucleotide) {
		if (this.snpCoverageMap.get(nucleotide) != null) {
			int oldCoverage = this.snpCoverageMap.get(nucleotide);
			this.snpCoverageMap.put(nucleotide, oldCoverage + 1);
		} else {
			this.snpCoverageMap.put(nucleotide, 1);
		}
	}
	
	public Transformation getSnpTransformation() {
		Iterator<Nucleotide> it = this.snpCoverageMap.keySet().iterator();
		Nucleotide bestN = null;
		int bestI = 0;
		while (it.hasNext()) {
			Nucleotide nuc = it.next();
			int count = snpCoverageMap.get(nuc);
			if (count > bestI) {
				bestN = nuc;
				bestI = count;
			}
		}
		
		Transformation transformation = new Transformation();
		transformation.setPosition(position);
		transformation.setSequence(bestN.toString());
		transformation.setColorSequence(ViewerConsensusModel.convertNucleotideSequenceToColor(prevNuc + bestN.toString() + nextNuc));
		transformation.setType(MutationType.SNP);
		transformation.setLength(1);
		transformation.setCoverage(bestI);
		
		return transformation;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public void setPrevNuc(char c) {
		this.prevNuc = c;		
	}

	public void setNextNux(char c) {
		this.nextNuc = c;
	}
}
