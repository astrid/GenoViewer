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

/*
 * CoveragePanel.java
 *
 * Created on 2010.04.22., 14:54:49
 */
package hu.astrid.viewer.gui.content.coverage;

import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.gui.ContentPanel;
import hu.astrid.viewer.gui.DashBoard;
import hu.astrid.viewer.gui.ResizeableScrollPanel;
import hu.astrid.viewer.model.Coverage;
import hu.astrid.viewer.model.CoverageModel;
import hu.astrid.viewer.util.CoverageLoadingTask;
import hu.astrid.viewer.properties.ProfileProperties;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * Coverage panel indicates each positions coverage by reads of the underlying model
 * @author onagy
 */
public class CoveragePanel extends javax.swing.JPanel implements AbstractView, ContentPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -1693816553721707018L;
	private List<Coverage> coverageList = null;
	private double percentageOfVisibleScreen = 10.0;
	/**Background task for generate and draw coverage values*/
	private CoverageLoadingTask coverageLoading = new CoverageLoadingTask();
	/**Maximum value of coverage in actual list*/
	private int maxCoverage = -1;

	/** Creates new form CoveragePanel */
	public CoveragePanel() {
		this.setDoubleBuffered(true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void modelPropertyChange(final PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(CoverageModel.COVERAGE_DATA_GENERATED)) {

			coverageList = (List<Coverage>) evt.getNewValue();

			maxCoverage = -1;
			for (Coverage coverageItem : coverageList) {

				if (coverageItem.getCoverage() > maxCoverage) {

					maxCoverage = coverageItem.getCoverage();
				}
			}

			this.repaintContent();
		} else if (evt.getPropertyName().equals(ViewerController.VIEWER_PROFILE_PROPERTY)) {
			this.repaintContent();
		}
	}

	/**
	 * Perform a reload of coverage data. Data generated beginnig with the position
	 * with the displayed area's width as length. After it its displayed.
	 * @param position screens start position
	 */
	public void reload(int position) {
		if (this.isVisible() && Viewer.getController().isReadsLoaded()) {
			coverageLoading.cancel(false);
//			logger.debug("reloadCoverage: "+coverageLoading.isCancelled());
			CoveragePanel.this.setHeight(percentageOfVisibleScreen);
			coverageLoading = new CoverageLoadingTask();
			coverageLoading.setPosition(position);
			coverageLoading.execute();
		}
	}

	@Override
	public void repaintContent() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (Viewer.getActiveProfile().isShowCoveragePanel() && Viewer.getReadModel().isReadsLoaded()) {

					if (CoveragePanel.this.isVisible()) {
						CoveragePanel.this.paint(CoveragePanel.this.getGraphics());
					} else {
						CoveragePanel.this.setVisible(true);
						CoveragePanel.this.reload(Viewer.getMainWindow().getDashBoard().scrollBarPosition());
					}
				} else {
					CoveragePanel.this.setVisible(false);
				}
			}
		});

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		ProfileProperties profile = Viewer.getActiveProfile();

		if (maxCoverage == -1) {
			return;
		}

		g.setColor(profile.getTextColor());
		//upper and lower marker...
		String lowerDottedLineMarker = "0";
		String upperDottedLineMarker = Integer.toString(maxCoverage);
		int lowerDottedLineStart = lowerDottedLineMarker.length() * this.getFontMetrics(this.getFont()).charWidth('0') + 8;
		int upperDottedLineStart = upperDottedLineMarker.length() * this.getFontMetrics(this.getFont()).charWidth('0') + 8;

		g.drawBytes(upperDottedLineMarker.getBytes(), 0, upperDottedLineMarker.length(), 5, (this.getFontMetrics(this.getFont()).getAscent()) - 2);
		g.drawBytes(lowerDottedLineMarker.getBytes(), 0, lowerDottedLineMarker.length(), 5, this.getHeight() - 1);

		int limit = this.getWidth();
		for (int i = 0; i < limit; i += 6) {
			g.drawLine(upperDottedLineStart + i, 5, upperDottedLineStart + i + 4, 5);
			g.drawLine(lowerDottedLineStart + i, this.getHeight() - 6, lowerDottedLineStart + i + 4, this.getHeight() - 6);
		}

		Point prevPoint = null;
		int diameter = DashBoard.fontWidth, radius = diameter / 2;
		int panelHeight = this.getHeight() - 11;
		int yCord = panelHeight;

		for (int i = 0; i < coverageList.size(); i++) {
			if (ResizeableScrollPanel.scrollPointerPosition != null && coverageList.get(i).getAbsPosition() == ResizeableScrollPanel.scrollPointerPosition) {
				g.setColor(Viewer.getActiveProfile().getAutoSelectionColor());
				g.drawLine((i * diameter) + radius, 0, (i * diameter) + radius, this.getHeight());
			}
			if (ResizeableScrollPanel.selectionPosition != null && coverageList.get(i).getAbsPosition() == ResizeableScrollPanel.selectionPosition) {
				g.setColor(Viewer.getActiveProfile().getManualSelectionColor());
				g.drawLine((i * diameter) + radius, 0, (i * diameter) + radius, this.getHeight());
			}

			g.setColor(profile.getCoverageColor());

			if (maxCoverage != 0) {
				yCord = (int) (panelHeight * (1 - ((double) coverageList.get(i).getCoverage() / maxCoverage)));
			}
//			g.fillOval(((i - 1) * diameter), yCord, diameter, diameter);
			if (prevPoint != null) {
				g.drawLine((i * diameter) + 6, yCord + 6, prevPoint.x, prevPoint.y);
			}

			prevPoint = new Point((i * diameter) + 6, yCord + 6);
		}
	}

	/**
	 * @param percentageOfVisibleScreen
	 */
	private void setHeight(double percentageOfVisibleScreen) {
		this.setPreferredSize(new Dimension(this.getPreferredSize().width, (int) ((percentageOfVisibleScreen / 100) * Viewer.getMainWindow().getHeight())));

		this.setMinimumSize(new Dimension(this.getMinimumSize().width, (int) ((percentageOfVisibleScreen / 100) * Viewer.getMainWindow().getHeight())));
		this.setMaximumSize(new Dimension(this.getMaximumSize().width, (int) ((percentageOfVisibleScreen / 100) * Viewer.getMainWindow().getHeight())));
		this.validate();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
