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
import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.gui.DashBoard;
import hu.astrid.viewer.gui.ResizeableScrollPanel;
import hu.astrid.viewer.model.SelectionModel.SelectionType;
import hu.astrid.viewer.model.ViewerConsensusModel;
import hu.astrid.viewer.properties.ProfileProperties.SequenceDisplayMode;
import hu.astrid.viewer.util.SwingComponentQueryTask;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 *
 * @author Máté
 */
public class ConsensusPanel extends ResizeableScrollPanel implements AbstractView {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ConsensusPanel.class);
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	/** Reference contig length */
	private int consesusContigLength;
	/** Start index of the last read from the actual contig */
	private int lastLoadStartIndex = 0;
	/**
	 * Sequence paddind from left. That means the clipped sequence length from
	 * start to actual sequence
	 */
	private int consesusContigSequencePadding;
	private int rowHeight = DashBoard.fontHeight;
	ConsensusLabel consensusNucleotideLabel = new ConsensusLabel();
	ConsensusLabel consensusColorLabel = new ConsensusLabel(true);

	/** Creates new form ConsensusPanel */
	public ConsensusPanel() {
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.weighty = 0.0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		gridBagConstraints.gridx = 0;

		this.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int viewPortWidth = getViewport().getWidth() / DashBoard.fontWidth + 1;
				int position = e.getValue() / DashBoard.fontWidth;
				if (consensusNucleotideLabel.getText().length() > 0 && (position < lastLoadStartIndex || position > lastLoadStartIndex + consensusNucleotideLabel.getText().length() - viewPortWidth)) {
					reload(position);
				}
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (selectionEnabled) {
			if (selectionPosition != null && Viewer.getController().getSelectedPosition(SelectionType.CONSENSUS).getSelectedPositions().size() > 1) {
//				Color color = new Color(0, 0, 1, 0.2f);
				g.setColor(selectionColor);
				int startPosition = Viewer.getController().getSelectedPosition(SelectionType.CONSENSUS).getStartPosition() * DashBoard.fontWidth - getHorizontalScrollBar().getValue();
				int width = ((Viewer.getController().getSelectedPosition(SelectionType.CONSENSUS).getEndPosition() + 1) - Viewer.getController().getSelectedPosition(SelectionType.CONSENSUS).getStartPosition()) * DashBoard.fontWidth;
				g.fillRect(startPosition, 0, width, 24);
			} else if (Viewer.getController().getSelectedPosition(SelectionType.CONSENSUS).getSelectedPositions().size() == 1) {
//				Color color = new Color(0, 0, 1, 0.2f);
				g.setColor(selectionColor);
				int startPosition = Viewer.getController().getSelectedPosition(SelectionType.CONSENSUS).getStartPosition() * DashBoard.fontWidth - getHorizontalScrollBar().getValue();
				g.fillRect(startPosition, 0, DashBoard.fontWidth, 24);
			}
		}
	}

	/**
	 * Set contig panels height
	 *
	 * @param rows
	 *            number of rows
	 */
	private void setHeight(int rows) {
		this.setMinimumSize(new Dimension(this.getMinimumSize().width, rows * rowHeight + 1));
		this.setMaximumSize(new Dimension(this.getMaximumSize().width, rows * rowHeight + 1));
		this.setPreferredSize(new Dimension(this.getPreferredSize().width, rows * rowHeight + 1));
	}

	/**
	 *
	 * @param position
	 * @param consensusData
	 */
	public void showConsensusSequence(final int position, final ConsensusData consensusData) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (!ConsensusPanel.this.isVisible()) {
					ConsensusPanel.this.setVisible(true);
				}

				final SequenceDisplayMode sequenceShowMode = Viewer.getActiveProfile().getSequenceDisplayMode();
				consesusContigSequencePadding = position;
				consensusNucleotideLabel.setConsensusData(consensusData);
				consensusColorLabel.setConsensusData(consensusData);
				contentPanel.removeAll();
				contentPanel.repaint();
				gridBagConstraints.gridy = 0;

				gridBagConstraints.insets = new Insets(0, consesusContigSequencePadding * DashBoard.fontWidth, 0, 0);
				contentPanel.add(consensusNucleotideLabel, gridBagConstraints);
				gridBagConstraints.gridy++;
				consensusNucleotideLabel.setVisible(sequenceShowMode != SequenceDisplayMode.COLOR);

				gridBagConstraints.insets = new Insets(0, consesusContigSequencePadding * DashBoard.fontWidth + DashBoard.fontWidth / 2, 0, 0);
				contentPanel.add(consensusColorLabel, gridBagConstraints);
				gridBagConstraints.gridy++;
				consensusColorLabel.setVisible(sequenceShowMode != SequenceDisplayMode.NUCLEOTIDE && !(consensusColorLabel.getFont().getSize() != 17 && sequenceShowMode == SequenceDisplayMode.BOTH));

				setHeight(consensusColorLabel.isVisible() && consensusNucleotideLabel.isVisible() ? 2 : 1);
				Viewer.getMainWindow().getDashBoard().validate();
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingConsensusGenom"));
			}
		});
	}

	/**
	 * @return number of characters that reference contig contains
	 */
	public int getConsesusContigLength() {
		return consesusContigLength;
	}

	/**
	 * @return displayed part of the reference contig in nucleotide sequence
	 */
	public String getDisplayedReferenceContig() {
		int sequenceStart = getHorizontalScrollBarValue();

		final int beginIndex = sequenceStart - consesusContigSequencePadding;
		final int endIndex = Math.min(consesusContigLength - consesusContigSequencePadding, beginIndex + getDisplayableCharacters());

		if (SwingUtilities.isEventDispatchThread()) {
			return consensusNucleotideLabel.getText().substring(beginIndex, endIndex);
		} else {
			SwingComponentQueryTask<String> task = new SwingComponentQueryTask<String>() {

				@Override
				protected String query() {
					return ConsensusPanel.this.consensusNucleotideLabel.getText().substring(beginIndex, endIndex);
				}
			};

			SwingUtilities.invokeLater(task);

			try {
				return task.get();
			} catch (InterruptedException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			} catch (ExecutionException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}
		}
	}

	/**
	 * Reloads content to display it from a specific position, that couldnt be
	 * shown completely
	 *
	 * @param screenPosition
	 *            first position of content displayed on the screen
	 */
	protected void reload(int screenPosition) {
		if (this.getHorizontalScrollBar() != null) {
			// The new position will be in the middle of the new load
			int start = screenPosition - Viewer.getApplicationProperties().getBufferSize() / 2 > -1 ? screenPosition - Viewer.getApplicationProperties().getBufferSize() / 2 : 0;
			if (start < getConsesusContigLength()) {
				logger.trace("reload= " + screenPosition);
				readConsensusFromPosition(start);
			}
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void repaintContent() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (Viewer.getController().isReadsLoaded()) {
					consensusColorLabel.setFont(DashBoard.labelFont);
					consensusNucleotideLabel.setFont(DashBoard.labelFont);
					SequenceDisplayMode sequenceShowMode = Viewer.getActiveProfile().getSequenceDisplayMode();

					GridBagConstraints contraints = layout.getConstraints(consensusColorLabel);
					contraints.insets.left = consesusContigSequencePadding * DashBoard.fontWidth + DashBoard.fontWidth / 2;
					layout.setConstraints(consensusColorLabel, contraints);
					consensusNucleotideLabel.setVisible(sequenceShowMode != SequenceDisplayMode.COLOR);
					consensusNucleotideLabel.repaint();

					contraints = layout.getConstraints(consensusNucleotideLabel);
					contraints.insets.left = consesusContigSequencePadding * DashBoard.fontWidth;
					layout.setConstraints(consensusNucleotideLabel, contraints);
					consensusColorLabel.setVisible(sequenceShowMode != SequenceDisplayMode.NUCLEOTIDE && !(consensusColorLabel.getFont().getSize() != 17 && sequenceShowMode == SequenceDisplayMode.BOTH));
					consensusColorLabel.repaint();

					setHeight(consensusColorLabel.isVisible() && consensusNucleotideLabel.isVisible() ? 2 : 1);
				} else {
					ConsensusPanel.this.setVisible(false);
				}
				Viewer.getMainWindow().getDashBoard().validate();
			}
		});
//		if (Viewer.getController().isReadsLoaded()) {
//			readConsensusFromPosition(lastLoadStartIndex);
//		}
	}

	/**
	 *
	 * @param position
	 */
	private void readConsensusFromPosition(int position) {
		if (Viewer.getController().isConsensusAvailable()) {
			Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingConsensusGenom"));
			position = Math.min(position, consesusContigLength - 1);
			Viewer.getController().readConsensusFromPosition(position);
			lastLoadStartIndex = position;
		}
	}

	@Override
	public void clear() {
		super.clear();
		lastLoadStartIndex = 0;
		consesusContigLength = 0;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewerController.VIEWER_PROFILE_PROPERTY)) {
			if (Viewer.getController().isReadsLoaded()) {
				this.repaintContent();
			}
		} else if (evt.getPropertyName().equals(ViewerConsensusModel.CONSENSUS_LOAD)) {
			this.clear();
			boolean isConsensusAvailable = (Boolean) evt.getNewValue();
			if (isConsensusAvailable) {
				logger.trace("consPanel: modelPropertyChange");
				this.consesusContigLength = Viewer.getController().getConsensusLength();
				invokePanelResizeListeners();
				this.readConsensusFromPosition(this.getHorizontalScrollBarValue());
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						ConsensusPanel.this.setVisible(false);
					}
				});
			}
		}
	}
}
