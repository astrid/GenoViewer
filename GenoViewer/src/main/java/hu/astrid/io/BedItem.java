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

/**
 * Astrid Research
 * Author: mkiss
 * Created: Dec 14, 2009
 */

public class BedItem {

	public enum Strand {
		PLUS{
		    @Override
		    public String toString() {
		    	return "+";
		    }
		    
		}, MINUS{
		    @Override
		    public String toString() {
		    	return "-";
		    }
		}
		
	}

	private final String chrom;
	private final Long chromStart;
	private final Long chromEnd;
	private final String name;
	private final Integer score;
	private final Strand strand;
	private final Integer thickStart;
	private final Integer thickEnd;
	private final Integer itemRgb;
	private final BedBlock bedBlock;

	public static class Builder {
		// Required parameters
		private final String chrom;
		private final Long chromStart;
		private final Long chromEnd;

		// Optional parameters - initialized to default values
		private String name = null;
		private Integer score = null;
		private Strand strand = null;
		private Integer thickStart = null;
		private Integer thickEnd = null;
		private Integer itemRgb = null;
		private BedBlock bedBlock = null;

		public Builder(String chrom, long chromStart, long chromEnd) {
			this.chrom = chrom;
			this.chromStart = chromStart;
			this.chromEnd = chromEnd;
		}

		public Builder name(String val) {
			name = val;
			return this;
		}

		public Builder score(int val) {
			score = val;
			return this;
		}

		public Builder strand(Strand val) {
			strand = val;
			return this;
		}

		public Builder thickStart(int val) {
			thickStart = val;
			return this;
		}

		public Builder thickEnd(int val) {
			thickEnd = val;
			return this;
		}

		public Builder itemRgb(int val) {
			itemRgb = val;
			return this;
		}

		public Builder bedBlock(BedBlock val) {
			bedBlock = val;
			return this;
		}

		public BedItem build() {
			boolean validOrder = true;
			if (name == null && score != null) {
				validOrder = false;
			}
			else if (strand != null && (score == null || name == null)) {
				validOrder = false;
			}
			else if (thickStart != null
					&& (strand == null || score == null || name == null)) {
				validOrder = false;
			}
			else if (thickEnd != null
					&& (thickStart == null || strand == null || score == null || name == null)) {
				validOrder = false;
			}
			else if (itemRgb != null
					&& (thickEnd == null || thickStart == null
							|| strand == null || score == null || name == null)) {
				validOrder = false;
			}
			else if (bedBlock != null
					&& (itemRgb == null || thickEnd == null
							|| thickStart == null || strand == null
							|| score == null || name == null)) {
				validOrder = false;
			}

			if (validOrder == false) {
				throw new RuntimeException("Missing parameters!");
			} else {
				return new BedItem(this);
			}
		}
	}

	private BedItem(Builder builder) {
		chrom = builder.chrom;
		chromStart = builder.chromStart;
		chromEnd = builder.chromEnd;
		name = builder.name;
		score = builder.score;
		strand = builder.strand;
		thickStart = builder.thickStart;
		thickEnd = builder.thickEnd;
		itemRgb = builder.itemRgb;
		bedBlock = builder.bedBlock;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(chrom + " " + chromStart + " " + chromEnd);
		if (name != null) {
			builder.append(" " + name);
		}
		if (score != null) {
			builder.append(" " + score);
		}
		if (strand != null) {
			if (strand == Strand.PLUS) {
				builder.append(" " + Strand.PLUS.toString());
			} else {
				builder.append(" " + Strand.MINUS.toString());
			}
		}
		if (thickStart != null) {
			builder.append(" " + thickStart);
		}
		if (thickEnd != null) {
			builder.append(" " + thickEnd);
		}
		if (itemRgb != null) {
			builder.append(" " + itemRgb);
		}
		if (bedBlock != null) {
			builder.append(" " + bedBlock.toString());
		}
		return builder.toString();
	}

	public String getChrom() {
		return chrom;
	}

	public Long getChromStart() {
		return chromStart;
	}

	public Long getChromEnd() {
		return chromEnd;
	}

	public String getName() {
		return name;
	}

	public Integer getScore() {
		return score;
	}

	public Character getStrand() {
		return strand == null ? null : strand.toString().charAt(0);
	}

	public Integer getThickStart() {
		return thickStart;
	}

	public Integer getThickEnd() {
		return thickEnd;
	}

	public Integer getItemRgb() {
		return itemRgb;
	}

	public BedBlock getBedBlock() {
		return bedBlock;
	}

}
