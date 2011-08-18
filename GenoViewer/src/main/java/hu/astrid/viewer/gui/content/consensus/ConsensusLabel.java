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

package hu.astrid.viewer.gui.content.consensus;

import hu.astrid.viewer.model.consensus.ConsensusData;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.SequenceLabel;
import hu.astrid.viewer.model.mutation.Mutation;
import hu.astrid.viewer.model.mutation.MutationType;
import java.awt.Color;

import java.awt.Graphics;

public class ConsensusLabel extends SequenceLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConsensusData consensusData;

	public ConsensusLabel(ConsensusData consensusData, boolean mode) {
		super(mode);
		this.consensusData = consensusData;
		this.setConsensusData(consensusData);
	}

	public ConsensusLabel(boolean colorMode) {
		super(colorMode);
	}

	public ConsensusLabel() {
		this(false);
	}

	public void setConsensusData(ConsensusData data) {
		this.consensusData = data;
		if (colorMode) {
			this.setText(consensusData.getColorSequence());
		} else {
			this.setText(consensusData.getSequence());
		}
	}

	@Override
	public void paint(Graphics g) {
		int width = g.getFontMetrics().charWidth('A');
		int height = g.getFontMetrics().getHeight();
		super.paint(g);
		int diff = 0;
		for (Mutation t : consensusData.getMutations()) {
			if (t.getMutationType() == MutationType.INSERTION) {
				int pos = t.getStartPos() - 1;
				g.setColor(Viewer.getActiveProfile().getInsertionColor());
				if (!colorMode) {
					g.fillRect((pos - diff) * width - 1, 0, 2, height);
				} else {
					g.clearRect((pos - 1) * width, 0, width, height);
					g.drawOval((pos - 1) * width, 0, width - 1, height - 1);
//					paintChars((pos - 1) * width, g.getFontMetrics().getAscent(), g, getText());
				}
			}

			if (t.getMutationType() == MutationType.SNP || t.getMutationType() == MutationType.MNP) {

				int signWidth = colorMode ? width * 2 : width;
				int pos = t.getStartPos() - 1;
				g.clearRect(pos*width, 0, width, height);
				g.setColor(getParent().getBackground());
				g.fillRect(pos*width, 0, width, height);
				if (colorMode) {
					for (int i = 0; i < t.getColorSequence().length(); ++i) {
						switch (t.getColorSequence().charAt(i)) {
							case '0': {
								g.setColor(Viewer.getActiveProfile().get0Color());
								break;
							}
							case '1': {
								g.setColor(Viewer.getActiveProfile().get1Color());
								break;
							}
							case '2': {
								g.setColor(Viewer.getActiveProfile().get2Color());
								break;
							}
							case '3': {
								g.setColor(Viewer.getActiveProfile().get3Color());
								break;
							}
							default: {
								g.setColor(Viewer.getActiveProfile().getTextColor());
								continue;
							}
						}
						g.fillOval((pos - 1 + i) * width, 0, width, g.getFontMetrics().getHeight());
					}
				}
				g.setColor(Viewer.getActiveProfile().getSNPColor());
				if (colorMode) {
					if (Viewer.getActiveProfile().isShowSNPs()) {
						for (int i = 0; i < t.getLength(); ++i) {
							g.fillPolygon(new int[]{((pos - 1 + i) * width) + 0, ((pos - 1 + i) * width) + (signWidth / 2), ((pos - 1 + i) * width) + signWidth, ((pos - 1 + i) * width) + (signWidth / 2)}, new int[]{height / 2, height, height / 2, 0}, 4);
						}
					}
					paintChars((pos - 1) * width, g.getFontMetrics().getAscent(), g, t.getColorSequence());
				} else {
					if (Viewer.getActiveProfile().isShowSNPs()) {
						for (int i = 0; i < t.getLength(); ++i) {
							g.fillPolygon(new int[]{((pos+i) * width) + 0, ((pos+i) * width) + (signWidth / 2), ((pos+i) * width) + signWidth, ((pos+i) * width) + (signWidth / 2)}, new int[]{height / 2, height, height / 2, 0}, 4);
						}
					}
					paintChars(pos * width, g.getFontMetrics().getAscent(), g, t.getDisplayedMutationSequence());
				}
			}
			if (t.getMutationType() == MutationType.DELETION) {
				int pos = t.getStartPos() - 1;
				//TODO egybe az egészet
				for (int i = 0; i < t.getLength(); i++) {
					g.setColor(Viewer.getActiveProfile().getDelitionColor());
					g.fillRect((i + pos) * width, 0, width, height);
					g.setColor(Viewer.getActiveProfile().getTextColor());
					paintChars((i + pos) * width, g.getFontMetrics().getAscent(), g, "-");
				}
				if (colorMode) {
					g.setColor(Viewer.getActiveProfile().getDelitionColor());
					g.fillRect((pos - 1) * width, 0, width, height);
					g.setColor(Viewer.getActiveProfile().getTextColor());
					paintChars((pos - 1) * width, g.getFontMetrics().getAscent(), g, "-");
				}
			}
		}
	}
}
