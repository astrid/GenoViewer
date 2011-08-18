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

public class Transformation implements Comparable<Transformation> {

	private MutationType type;
	private int position;
	private float coverage;
	private String sequence;
	private String colorSequence;
	
	private int length;
	
	public Transformation(Transformation tr) {
		super();
		this.type = tr.getType();
		this.position = tr.getPosition();
		this.coverage = tr.getCoverage();
		this.sequence = tr.getSequence();
		this.colorSequence = tr.getColorSequence();
		this.length = tr.getLength();
	}

	public Transformation() {
		//Empty
	}

	@Override
	public String toString() {
//		return " " + position + " ";
		return "Transformation [coverage=" + coverage + ", position=" + position + ", sequence=" + sequence + ", type=" + type + ", length=" + length + "]";
	}
	
	public MutationType getType() {
		return type;
	}
	
	public void setType(MutationType type) {
		this.type = type;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public float getCoverage() {
		return coverage;
	}
	
	public void setCoverage(float coverage) {
		this.coverage = coverage;
	}
	
	public String getSequence() {
		return sequence;
	}
	
	public void setSequence(String sequence) {
		this.sequence = sequence;
		setLength(sequence.length());
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	@Override
	public int compareTo(Transformation o) {
		if (this.getPosition() < o.getPosition()) {
			return -1;
		} else if (this.getPosition() > o.getPosition()) {
			return 1;
		}
		return 0;
	}
	
	public String getColorSequence() {
		return colorSequence;
	}

	public void setColorSequence(String colorSequence) {
		this.colorSequence = colorSequence;
	}
}
