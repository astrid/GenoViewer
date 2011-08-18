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

package hu.astrid.viewer.gui.content.alignment;

import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.OptionalTag;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.SequenceLabel;
import hu.astrid.viewer.model.ViewerReadModel;
import hu.astrid.viewer.model.alignment.ReadData.InDel;
import hu.astrid.viewer.model.alignment.ReadData.PositionMutation;
import hu.astrid.viewer.model.alignment.ReadData.ReadError;
import hu.astrid.viewer.properties.ProfileProperties;
import hu.astrid.viewer.model.alignment.ReadData;
import java.awt.Color;
import java.awt.Graphics;

import org.apache.log4j.Logger;

/**
 * Draws a read. Insertions just signed but not showed, deletions showed by gaps.
 * Can be displayed SNPs, read errors and strand direction.
 * @author Szuni, OTTO
 */
public class ReadLabel extends SequenceLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private ReadData readData;
	private static final Logger logger = Logger.getLogger(ReadLabel.class);

	/**
	 * Sample constructor to use in {@see ProfleSettinsDialog}
	 */
	public ReadLabel() {
		this(false);
	}

	/**
	 * Sample constructor to use in {@see ProfleSettinsDialog}
	 * @param mode label shoud be displayed in color mode or nucleotide
	 */
	public ReadLabel(boolean mode) {
		super(mode);
		AlignmentRecord sampleRecord = new AlignmentRecord();
		sampleRecord.setCigar("5M2I5M3D8M");
		sampleRecord.setSequence("GCAGGCTTCTGGGGTCGCAA");
		sampleRecord.setFlag((short) 0);
		OptionalTag XRTag = new OptionalTag();
		XRTag.setTagName("XR");
		XRTag.setValueType('Z');
		XRTag.setValue("2-2;17-0");
		OptionalTag CSTag = new OptionalTag();
		CSTag.setTagName("CS");
		CSTag.setValueType('Z');
		CSTag.setValue("T13120320221000123310");
		OptionalTag MDTag = new OptionalTag();
		MDTag.setTagName("MD");
		MDTag.setValueType('Z');
		MDTag.setValue("6A1A11");
		sampleRecord.addOptionalTag(CSTag);
		sampleRecord.addOptionalTag(XRTag);
		sampleRecord.addOptionalTag(MDTag);
		this.readData = new ReadData(sampleRecord);
		this.setColorMode(mode);
	}

	/**
	 * Create label with specified data in specified mode
	 * @param readData
	 * @param id id for get {@see AlignmentRecord} for {@see ReadInfoPanel} from {@see ViewerReadModel}
	 * @param mode label shoud be displayed in color mode or nucleotide
	 */
	public ReadLabel(ReadData readData, int id, boolean mode) {
		super(mode);
		this.readData = readData;
		this.id = id;
		this.setColorMode(mode);
	}

	@Override
	public void setColorMode(boolean colorMode) {
		super.setColorMode(colorMode);
		if (colorMode) {
			if (readData.getColorSequence() == null) {
				throw new IllegalStateException("Color code isn't available");
			}
			this.setText(readData.getColorSequence());
		} else {
			this.setText(readData.getSequence());
		}
	}

	/**
	 * @return Id of read. Used to get it from the {@see ViewerReadModel} for {@see ReadInfoPanel}
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return start position of the read
	 */
	public int getPosition() {
		return readData.getPosition();
	}

	@Override
	public void paint(Graphics g) {
		int width = g.getFontMetrics().charWidth('A');
		int height = g.getFontMetrics().getHeight();
		final ProfileProperties activeProfile = super.tempProperties == null ? Viewer.getActiveProfile() : super.tempProperties;
		final Color originalZoomedTextColor = activeProfile.getZoomedTextColor();
		final Color originalTextColor = activeProfile.getTextColor();
		if (activeProfile.isShowDirection()) {
			activeProfile.setTextColor(readData.isReverse() ? activeProfile.getNegativeStrandColor() : activeProfile.getPositiveStrandColor());
			if (getFont().getSize() < 17) {
				activeProfile.setZoomedTextColor(readData.isReverse() ? activeProfile.getNegativeStrandColor() : activeProfile.getPositiveStrandColor());
			}
		}
		if (activeProfile.isNonSpecificHighlight() && !readData.isSpecific()) {
			activeProfile.setTextColor(activeProfile.getNonSpecificColor());
			activeProfile.setZoomedTextColor(activeProfile.getNonSpecificColor());
		}
		super.paint(g);
		int diff = 0;
		for (InDel insertion : readData.getInsertions()) {

			g.setColor(activeProfile.getInsertionColor());
			if (!colorMode) {
				g.fillRect((insertion.position - diff) * width - 1, 0, 2, height);
			} else {
				g.clearRect(insertion.position * width, 0, width, height);
				g.drawOval(insertion.position * width, 0, width - 1, height - 1);
//				paintChars(insertion.position * width, g.getFontMetrics().getAscent(), g, getText().substring(insertion.position);
			}
		}
		if (activeProfile.isShowReadErrors() && colorMode) {
			for (ReadError error : readData.getReadErrors()) {
				g.setColor(activeProfile.getReadErrorColor());
				g.fillRect(error.position * width, 0, width, height);
				super.paintChars(error.position * width, g.getFontMetrics().getAscent(), g, getText().substring(error.position, error.position + 1));
				g.setColor(Color.black);
				g.drawRect(error.position * width, 0, width - 1, height - 1);
			}
		}
		if (activeProfile.isShowSNPs()) {
			int signWidth = colorMode ? width * 2 : width;
			for (PositionMutation snp : readData.getSnpList()) {
				g.setColor(activeProfile.getSNPColor());
				g.fillPolygon(new int[]{(snp.position * width) + 0, (snp.position * width) + (signWidth / 2), (snp.position * width) + signWidth, (snp.position * width) + (signWidth / 2)}, new int[]{height / 2, height, height / 2, 0}, 4);
				super.paintChars(snp.position * width, g.getFontMetrics().getAscent(), g, getText().substring(snp.position, snp.position + (colorMode ? 2 : 1)));
			}
		}
		if (activeProfile.isShowDirection()) {
			int signSize = (int) (width * 0.6);
			g.setColor(activeProfile.getDirectionBackgroundColor());
			g.fillRect(0, 0, signSize + 1, signSize + 1);
			g.setColor(activeProfile.getDirectionIndicatorColor());
			g.drawLine(0, signSize / 2, signSize, signSize / 2);
			if (!readData.isReverse()) {
				g.drawLine(signSize / 2, 0, signSize / 2, signSize);
			}
		}
		activeProfile.setZoomedTextColor(originalZoomedTextColor);
		activeProfile.setTextColor(originalTextColor);
		super.tempProperties = null;
	}

	/**
	 * Repaint the component with a temporary profile,
	 * after the repaint the component uses the actual profile settings
	 * @param profile teporary profile
	 */
	public void refreshViewSettings(ProfileProperties profile) {
		super.tempProperties = profile;
		repaint();
	}

	@Override
	public int getNumOfDrawnChars() {
		return this.readData.getSequence().length();
	}
}
