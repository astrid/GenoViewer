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

package hu.astrid.io;

import java.util.ArrayList;
import java.util.List;

public class BedBlock {
	private List<Integer> blockSizes;
	private List<Integer> blockStarts;

	public BedBlock() {
		blockSizes = new ArrayList<Integer>(0);
		blockStarts = new ArrayList<Integer>(0);
	}

	public void addBedBlock(int blockSize, int blockStart) {
		this.blockSizes.add(blockSize);
		this.blockStarts.add(blockStart);
	}

	public Integer getBlockCont() {
		return blockSizes.size();
	}

	public List<Integer> getBlockSizes() {
		return blockSizes;
	}

	public List<Integer> getBlockStarts() {
		return blockStarts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int blockCount = getBlockCont();
		builder.append(blockCount + " ");
		for (int i = 0; i < blockCount; ++i) {
			builder.append(blockSizes.get(i) + ",");
		}
		builder.append(" ");
		for (int i = 0; i < blockCount; ++i) {
			builder.append(blockStarts.get(i));
			if (i <= blockCount - 2) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

}