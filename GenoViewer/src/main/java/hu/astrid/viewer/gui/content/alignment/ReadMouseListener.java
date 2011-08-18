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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.astrid.viewer.gui.content.alignment;

import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.util.Alignment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.apache.log4j.Logger;

/**
 *
 * @author OTTO
 */
public class ReadMouseListener extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent evt) {
		ReadLabel readLabel = (ReadLabel) evt.getSource();
		AlignmentRecord read = Viewer.getController().getReadById(readLabel.getId());
		if(read==null) {
			Logger.getLogger(ReadMouseListener.class).warn("Read isnt loaded");
			return ;
		}
		ReadInfoPanel readInfoPanel = new ReadInfoPanel();
		String sequence = Viewer.getController().getRefrenceAlignmentString(read.getPosition(), Alignment.getRefLength(read));
		readInfoPanel.fillFlagTable(read.getFlag());
		readInfoPanel.fillMateInfoTable(read);
		readInfoPanel.fillQualityTable(read.getQuality());
		readInfoPanel.setReadText(Alignment.getAlignment(read, sequence));
		readInfoPanel.setCigarText(read.getCigar());
		readInfoPanel.setStartEnd(read.getPosition(), read.getPosition() + readLabel.getNumOfDrawnChars());
		readInfoPanel.setMappingQuality(read.getMappingQuality());
		readInfoPanel.setMDTagText((read.getOptionalTag("MD") != null)?read.getOptionalTag("MD").getValue():"N/A");
		Viewer.getMainWindow().setReadInfoDialog(readInfoPanel, read.getQueryName());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		AlignmentRecord read = Viewer.getController().getReadById(((ReadLabel) e.getSource()).getId());
		if(read==null) {
			Logger.getLogger(ReadMouseListener.class).warn("Read isnt loaded");
			return ;
		}
		Viewer.showStatusbarMessage(read.getQueryName());
		return;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Viewer.showStatusbarMessage("");
		return;
	}
}


