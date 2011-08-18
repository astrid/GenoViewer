
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
 * AlignmentPanel.java
 * Created on 2010.04.26., 13:18:59
 */
package hu.astrid.viewer.gui.content.alignment;

import hu.astrid.viewer.gui.DashBoard;
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.viewer.model.alignment.Interval;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.gui.ContentPanel;
import hu.astrid.viewer.gui.ResizeableScrollPanel;
import hu.astrid.viewer.model.alignment.ReadData;
import hu.astrid.viewer.gui.content.coverage.CoveragePanel;
import hu.astrid.viewer.model.ViewerReadModel;
import hu.astrid.viewer.properties.ProfileProperties;
import hu.astrid.viewer.properties.ProfileProperties.ReadDisplayType;
import hu.astrid.viewer.properties.ProfileProperties.SequenceDisplayMode;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 *
 * @author Szuni
 */
public class AlignmentPanel extends ResizeableScrollPanel implements AbstractView {

	private static final long serialVersionUID = 2L;
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	/**Mouse listener to show Information panel on read clicks. Constructed when both controller and main window are set*/
	private ReadMouseListener readMouseListener = new ReadMouseListener();
	/**Length of reference where reads loaded from*/
	private int referenceLength;
	/***/
	private boolean isFirstLoadOccured = false;
	private static final Logger logger = Logger.getLogger(AlignmentPanel.class);
	private ReadInfoPanel readInfoPanel;
	private boolean colorCodeAvailable;
	private ArrayList<ReadLabel> readLabels = new ArrayList<ReadLabel>();

	/** Creates new form AlignmentPanel */
	public AlignmentPanel() {
		super();

		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.weighty = 0.0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.gridx = 0;

		readInfoPanel = new ReadInfoPanel();

		setPreferredSize(new java.awt.Dimension(1024, 1000));

		// Reads from file, when because of scrollbar use unloaded characters
		// have to be shown
		this.getHorizontalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(final AdjustmentEvent e) {
						int position = e.getValue() / DashBoard.fontWidth;
						//TODO zoomnál a határt megnézni
						// If the new screenposition is before the last
						// loads start index, or there are some unloaded
						// character after the last load in the current
						// viewport
						if (isFirstLoadOccured && !getHorizontalScrollBar().getValueIsAdjusting() && Viewer.getController().isReadsLoaded() && !Viewer.getController().isIntervalLoaded(position, getDisplayableCharacters())) {

							new SwingWorker() {

								Throwable throwable = null;

								@Override
								protected Object doInBackground() throws Exception {
									try {
										reload(e.getValue() / DashBoard.fontWidth);
									} catch (Throwable ex) {
										throwable = ex;
									}
									return null;
								}

								@Override
								protected void done() {
									if (throwable != null) {
										logger.error(throwable.getMessage(), throwable);
									}
								}
							}.execute();
						}
					}
				});
	}

//	/**
//	 * Set reads panels height
//	 * @param height height in pixels
//	 */
//	private void setHeight(int height) {
//		contentPanel.setPreferredSize(new Dimension(contentPanel.getPreferredSize().width, height));
//		this.validate();
//	}
	@Override
	public void setPreferredWidth(int width) {
		super.setPreferredWidth(width);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				AlignmentPanel.this.getVerticalScrollBar().setUnitIncrement(DashBoard.fontHeight);
			}
		});
	}

	/**
	 * @return length of reference where reads loaded from
	 */
	public int getReferenceLength() {
		return referenceLength;
	}

	/**
	 * Construct a label for a read from {@see ReadData}
	 * @param record record with read properties
	 * @param readIndex unique id for further searches for {@see ReadInfoPanel}
	 * @param colorMode display mode
	 * @return readlabel
	 */
	private ReadLabel makeRead(ReadData record, int readIndex, boolean colorMode) {
		ReadLabel read = new ReadLabel(record, readIndex, colorMode);
		read.addMouseListener(readMouseListener);
		read.setCursor(new Cursor(Cursor.HAND_CURSOR));

		return read;
	}

	/**
	 * Show the reads. Uses event dispatch thread
	 * @param readDataList
	 * @param interval
	 */
	public void showReads(List<ReadData> readDataList, final Interval interval) {
		final SequenceDisplayMode sequenceShowMode = Viewer.getActiveProfile().getSequenceDisplayMode();
		final ReadDisplayType readsShowType = Viewer.getActiveProfile().getReadDisplayType();

		if (!readDataList.isEmpty() && readDataList.get(0).getColorSequence() == null) {
			colorCodeAvailable = false;
//			if (sequenceShowMode != SequenceDisplayMode.NUCLEOTIDE) {
//				SwingUtilities.invokeLater(new Runnable() {
//
//					@Override
//					public void run() {
//						JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("warningMessageMissingColorCodes"), Viewer.getLabelResources().getString("dialogTitleWarning"),
//								JOptionPane.WARNING_MESSAGE);
//					}
//				});
//			}
		} else {
			colorCodeAvailable = true;
		}

		int readIndex = 0;
		readLabels = new ArrayList<ReadLabel>(readDataList.size() * 2);
		final boolean nucleotideVisibility = sequenceShowMode != SequenceDisplayMode.COLOR;
		for (ReadData readData : readDataList) {
			final ReadLabel nucleotideRead = makeRead(readData, readIndex, false);
			if (!nucleotideVisibility) {
				nucleotideRead.setVisible(false);
			}
			readLabels.add(nucleotideRead);
			if (colorCodeAvailable) {
				final ReadLabel colorRead = makeRead(readData, readIndex, true);

				readLabels.add(colorRead);
			}
			readIndex++;
		}

		repaintContent();

		if (readsShowType == ReadDisplayType.SHORT) {
			AlignmentPanel.this.getVerticalScrollBar().setValue(0);
		}

		logger.trace("interval " + interval + " painted ");

		if (interval != null) {
			isFirstLoadOccured = true;
			new SwingWorker<Object, Object>() {

				@Override
				protected Object doInBackground() throws Exception {
					Viewer.getController().notifyPreloader(interval);
					return null;
				}
			}.execute();
		}

		Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReads"));

	}

	/**
	 * Show the preloaded reads from model. The previously added reads arent removed.
	 * New reads added to the top or bottom of the panel.
	 * @param idStart
	 * @param readDataList
	 */
	private void showPreloadedReads(final int idStart, List<ReadData> readDataList) {
		logger.trace("prepare preloaded " + readDataList.get(0).getPosition() + "-" + readDataList.get(readDataList.size() - 1).getPosition() + " from id " + idStart);
		final SequenceDisplayMode sequenceShowMode = Viewer.getActiveProfile().getSequenceDisplayMode();
		final ReadDisplayType readsShowType = Viewer.getActiveProfile().getReadDisplayType();
		final int readDistance = Viewer.getApplicationProperties().getReadDistance();

		int readIndex = idStart;
		final ArrayList<ReadLabel> preloadedReadLabels = new ArrayList<ReadLabel>(readDataList.size() * (sequenceShowMode == SequenceDisplayMode.BOTH ? 2 : 1));
		final boolean nucleotideVisibility = sequenceShowMode != SequenceDisplayMode.COLOR;
		for (ReadData readData : readDataList) {
			final ReadLabel nucleotideRead = makeRead(readData, readIndex, false);
			if (!nucleotideVisibility) {
				nucleotideRead.setVisible(false);
			}
			preloadedReadLabels.add(nucleotideRead);
			if (colorCodeAvailable) {
				final ReadLabel colorRead = makeRead(readData, readIndex, true);
				preloadedReadLabels.add(colorRead);
			}
			readIndex++;
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (contentPanel.getComponentCount() == 0) {
					logger.error("panel cleared before preload paint");
					return;
				}
				logger.trace("start paint preloaded " + preloadedReadLabels.get(0).getPosition() + "-" + preloadedReadLabels.get(preloadedReadLabels.size() - 1).getPosition() + " from id " + idStart);
				contentPanel.remove(contentPanel.getComponent(contentPanel.getComponentCount() - 1));

				int maxRow = layout.getLayoutDimensions()[1].length;

				final boolean needPaintToTop = readsShowType == ReadDisplayType.LONG && preloadedReadLabels.get(0).getPosition() < ((ReadLabel) contentPanel.getComponent(0)).getPosition();
				int i = readsShowType == ReadDisplayType.LONG && !needPaintToTop ? contentPanel.getComponentCount() : 0;

				gridBagConstraints.weighty = 0.0;

				if (needPaintToTop) {
					for (Component c : contentPanel.getComponents()) {
						GridBagConstraints constraints = layout.getConstraints(c);
						constraints.gridy += preloadedReadLabels.size();
						layout.setConstraints(c, constraints);
					}
				}

				int columnStartPos = preloadedReadLabels.isEmpty() ? 0 : preloadedReadLabels.get(0).getPosition() - preloadedReadLabels.get(0).getPosition() % readDistance;
				for (ReadLabel readLabel : preloadedReadLabels) {
					if ((readLabel.isColorMode() && sequenceShowMode == SequenceDisplayMode.NUCLEOTIDE) || (!readLabel.isColorMode() && sequenceShowMode == SequenceDisplayMode.COLOR)) {
						continue;
					}
					if (readsShowType == ReadDisplayType.SHORT && (columnStartPos + readDistance < readLabel.getPosition())) {
						columnStartPos = readLabel.getPosition() - readLabel.getPosition() % readDistance;
						if (maxRow < i) {
							maxRow = i;
						}
						i = 0;
					}

					gridBagConstraints.insets = new Insets(0, (readLabel.getPosition() - 1) * DashBoard.fontWidth - (readLabel.isColorMode() ? DashBoard.fontWidth / 2 : 0), 0, 0);
					gridBagConstraints.gridy = i;
					contentPanel.add(readLabel, gridBagConstraints, i);
					++i;
				}

				if (readsShowType == ReadDisplayType.LONG) {
					maxRow += preloadedReadLabels.size();
				}
				maxRow = Math.max(i, maxRow);
				gridBagConstraints.gridy = maxRow + 1;
				gridBagConstraints.insets = new Insets(0, 0, 0, 0);
				gridBagConstraints.weighty = 1.0;
				contentPanel.add(Box.createGlue(), gridBagConstraints);

//				AlignmentPanel.this.setHeight(maxRow * DashBoard.fontHeight);
				AlignmentPanel.this.validate();

				if (needPaintToTop) {
					//Scroll down. Zoom value, colorcode availability and show type checked
					AlignmentPanel.this.getVerticalScrollBar().setValue(AlignmentPanel.this.getVerticalScrollBar().getValue() + (DashBoard.fontHeight * i));
				}

				if (needPaintToTop) {
					readLabels.addAll(0, preloadedReadLabels);
				} else {
					readLabels.addAll(preloadedReadLabels);
				}

				logger.trace("preload done");

				new SwingWorker<Object, Object>() {

					Exception ex;

					@Override
					protected Object doInBackground() throws Exception {
						try {
							Viewer.getController().notifyPreloader(null);
						} catch (Exception exc) {
							ex = exc;
						}
						return null;
					}

					@Override
					protected void done() {
						if (ex != null) {
							logger.error(ex.getMessage(), ex);
						}
					}
				}.execute();
			}
		});

	}

	/**
	 * Reloads content to display it from a specific position, that couldnt be shown
	 * completely
	 * @param screenPosition first position of content displayed on the screen
	 */
	private void reload(int screenPosition) {

		int start = screenPosition - Viewer.getController().getMaxReadLength();
		if (start < 0) {
			start = 0;
		}
		if (!Viewer.getController().isWholeFileLoaded() && start < referenceLength) {
			loadReads(start, screenPosition + this.getDisplayableCharacters() + 3);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void repaintContent() {
		if (readLabels.isEmpty()) {
			return;
		}
		Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReads"));
		final SequenceDisplayMode sequenceShowMode = Viewer.getActiveProfile().getSequenceDisplayMode();
		final ReadDisplayType readsShowType = Viewer.getActiveProfile().getReadDisplayType();
		if (!colorCodeAvailable && sequenceShowMode != SequenceDisplayMode.NUCLEOTIDE) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("warningMessageMissingColorCodes"), Viewer.getLabelResources().getString("dialogTitleWarning"),
							JOptionPane.WARNING_MESSAGE);
				}
			});
		}
		final int readDistance = Viewer.getApplicationProperties().getReadDistance();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				AlignmentPanel.this.contentPanel.removeAll();
				AlignmentPanel.this.contentPanel.repaint();
				logger.trace("clear before paint");


				int i = 0;
				int maxRow = 0;
				int columnStartPos = readLabels.isEmpty() ? 0 : readLabels.get(0).getPosition() - readLabels.get(0).getPosition() % readDistance;
				gridBagConstraints.weighty = 0.0;

				for (ReadLabel readLabel : readLabels) {
					readLabel.setFont(DashBoard.labelFont);
					if ((readLabel.isColorMode() && (sequenceShowMode == SequenceDisplayMode.NUCLEOTIDE || (readLabel.getFont().getSize() < 17 && sequenceShowMode == SequenceDisplayMode.BOTH))) || (!readLabel.isColorMode() && sequenceShowMode == SequenceDisplayMode.COLOR)) {
						continue;
					}

					if (readsShowType == ReadDisplayType.SHORT && (columnStartPos + readDistance < readLabel.getPosition())) {
						columnStartPos = readLabel.getPosition() - readLabel.getPosition() % readDistance;
						if (maxRow < i) {
							maxRow = i;
						}
						i = 0;
					}

					gridBagConstraints.insets = new Insets(0, (readLabel.getPosition() - 1) * DashBoard.fontWidth - (readLabel.isColorMode() ? DashBoard.fontWidth / 2 : 0), 0, 0);
					gridBagConstraints.gridy = i;
					contentPanel.add(readLabel, gridBagConstraints);
					++i;
				}

				if (readsShowType == ReadDisplayType.LONG) {
					maxRow = i;
				}
				maxRow = Math.max(i, maxRow);
				gridBagConstraints.gridy = maxRow + 1;
				gridBagConstraints.insets = new Insets(0, 0, 0, 0);
				gridBagConstraints.weighty = 1.0;
				contentPanel.add(Box.createGlue(), gridBagConstraints);

				//Vertical scroll to first read in viewport
				if (readsShowType == ReadDisplayType.LONG) {
					int verticalReadStart = 0, sceenPosition = AlignmentPanel.this.getHorizontalScrollBarValue();
					for (Component c : contentPanel.getComponents()) {
						if (!(c instanceof ReadLabel)) {
							continue;
						}
						ReadLabel r = (ReadLabel) c;
						if (r.getPosition() >= sceenPosition) {
							verticalReadStart = layout.getConstraints(r).gridy;
							break;
						}
					}
					AlignmentPanel.this.getVerticalScrollBar().setValue(DashBoard.fontHeight * verticalReadStart);
				}

				AlignmentPanel.this.validate();
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReads"));
			}
		});
	}

	/**
	 * Load reads from model, where reads start index is in an interval
	 * @param start
	 * @param end
	 */
	private void loadReads(int start, int end) {
		//Modify interval to match column starts
		final int readDistance = Viewer.getApplicationProperties().getReadDistance();
		start -= start % readDistance;
		end += readDistance - end % readDistance;

		Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReads"));
		try {
			int limit = referenceLength - getDisplayableCharacters();
			if (start > limit) {
				int diff = start - limit;
				start -= diff;
				end -= diff;
			}
			Viewer.getController().loadReads(start, end);
		} catch (IOException ex) {
			Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReads"));
			clear();
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, Viewer.getLabelResources().getString("errorMessageIO"), Viewer.getLabelResources().getString("dialogTitleError"), JOptionPane.ERROR_MESSAGE);
				}
			});
		} catch (MappingFileFormatException ex) {
			Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReads"));
			clear();
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, Viewer.getLabelResources().getString("warningMessageWrongMappingFileFormat"), Viewer.getLabelResources().getString("dialogTitleWarning"), JOptionPane.WARNING_MESSAGE);
				}
			});
		}
	}

	@Override
	public void clear() {
		super.clear();
		isFirstLoadOccured = false;
		referenceLength = 0;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewerController.VIEWER_PROFILE_PROPERTY)) {

			if (Viewer.getReadModel().isReadsLoaded()) {
				if (((ProfileProperties) evt.getNewValue()).isNeedReload()) {
					Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReads"));
					showReads(Viewer.getController().getReadsDataList(), null);
				} else {
					this.repaintContent();
				}
			}
		}

		// Fájl megynitása és bezárás esetén olvas a fájlból vagy törli a képernyőt
		if (evt.getPropertyName().equals(ViewerReadModel.ALIGNMENT_LOAD)) {
			boolean isFileOpened = evt.getNewValue() != null;
			if (isFileOpened) {
				Viewer.getController().setModelProperty(ViewerReadModel.ACT_ALIGNMENT_REF_INDEX, 0);
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
			} else {
				this.clear();
				this.invokePanelResizeListeners();
			}
			for (ContentPanel panel : Viewer.getMainWindow().getDashBoard().getPanels()) {
				if (panel instanceof CoveragePanel) {
					panel.repaintContent();
				}
			}
		} else if (evt.getPropertyName().equals(ViewerReadModel.ACT_ALIGNMENT_REF_INDEX)) {
			this.clear();
			this.referenceLength = Viewer.getController().getAlignmentReferenceLength();
			this.invokePanelResizeListeners();
			int screenPosition = this.getHorizontalScrollBarValue();
			this.loadReads(Math.max(screenPosition - 100, 0), screenPosition + this.getDisplayableCharacters());
		} else if (evt.getPropertyName().equals(ViewerReadModel.ALIGNMENT_PRELOAD)) {
			List<ReadData> preloadedReads = Viewer.getController().getPreloadedReads((Integer) evt.getOldValue(), (Integer) evt.getNewValue());
			AlignmentPanel.this.showPreloadedReads((Integer) evt.getOldValue(), preloadedReads);
		}
	}

	/**
	 * @return info panel for the reads
	 */
	public ReadInfoPanel getReadInfoPanel() {
		return readInfoPanel;
	}

	/**
	 * Just for test
	 * @return panel containing reads
	 */
	protected JPanel getContentPanel() {
		return contentPanel;
	}
}
