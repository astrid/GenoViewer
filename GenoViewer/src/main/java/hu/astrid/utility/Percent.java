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

package hu.astrid.utility;

import java.security.InvalidParameterException;

/**
 * 
 * @author zsdoma, mkiss
 *
 */
public class Percent {
	private long base;
	private long leg;
	private long oldLeg;
	private long value;
	private int step = 1;
	
	public Percent(long startValue, long endValue) {
		super();
		if (startValue > endValue) {
			long temp = startValue;
			startValue = endValue;
			endValue = temp;
		}
		this.base = endValue - startValue;
		this.value = 0;
		this.leg = (this.value * 100/step)/this.base;
		this.oldLeg = leg;
	}

	public long getActualValue() {
		return value;
	}

	public void setActualValue(long value) {
		this.value = value;
		this.compute();
	}
	
	public boolean hasChanged() {
		return leg != oldLeg ? true : false;
	}
	
	public void next() {
		this.value++;
		this.compute();
	}
	
	private void compute() {
		this.oldLeg = this.leg;
		this.leg = (this.value * 100/step)/this.base;
	}


	public long getLeg() {
		return leg;
	}

	public int getStep() {
	    return step;
	}

	public void setStep(int i) {
	    if (step < 1 || step > 100) {
		throw new InvalidParameterException();
	    }
	    this.step = i;
	}

	@Override
	public String toString() {
		return  leg * step + "%";
	}
	
}
