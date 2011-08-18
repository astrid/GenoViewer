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

package hu.astrid.viewer.gui.content.fasta;

import hu.astrid.viewer.gui.DashBoard;
import hu.astrid.mvc.swing.AbstractView;

import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.gui.ResizeableScrollPanel;
import hu.astrid.viewer.gui.SequenceLabel;
import hu.astrid.viewer.model.SelectionModel.SelectionType;
import hu.astrid.viewer.model.ViewerFastaModel;
import hu.astrid.viewer.properties.ProfileProperties.SequenceDisplayMode;
import hu.astrid.viewer.reader.FastaRandomReader.ContigIndexOutOfBoundsException;
import hu.astrid.viewer.reader.FastaRandomReader.ContigPositionOutOfBoundsException;
import hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException;
import hu.astrid.viewer.reader.FastaRandomReader.MissingContigStartIndicesException;
import hu.astrid.viewer.util.SwingComponentQueryTask;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 *
 * @author Szuni
 */
public class FastaPanel extends ResizeableScrollPanel implements AbstractView {

	private static final long serialVersionUID = 1L;
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	/**Reference contig length*/
	private int referenceContigLength;
	/**Start index of the last read from the actual contig*/
	private int lastLoadStartIndex = 0;
	/**Sequence paddind from left. That means the clipped sequence length from start to actual sequence*/
	private int referenceContigSequencePadding;
	private int rowHeight = DashBoard.fontHeight;
	/** Default logger */
	private static final Logger logger = Logger.getLogger(FastaPanel.class);
	protected SequenceLabel referenceContigColorLabel = new SequenceLabel(true);
	protected SequenceLabel referenceContigNucleotideLabel = new SequenceLabel();

	/** Creates new form FastaPanel */
	public FastaPanel() {
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.gridx = 0;

		// Reads from file, when because of scrollbar use unloaded characters
		// have to be shown
		this.getHorizontalScrollBar().addAdjustmentListener(
			new AdjustmentListener() {

				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {


					int viewPortWidth = getViewport().getWidth() / DashBoard.fontWidth + 1;
					int position = e.getValue() / DashBoard.fontWidth;
					if (referenceContigNucleotideLabel.getText().length() > 0 && (position < lastLoadStartIndex || position > lastLoadStartIndex + referenceContigNucleotideLabel.getText().length() - viewPortWidth - Viewer.getController().getMaxReadLength())) {
						reload(position);
					}
				}
			});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (selectionEnabled) {
			if (Viewer.getController().getSelectedPosition(SelectionType.REFERENCE).getSelectedPositions().size() > 1) {
				g.setColor(selectionColor);
				int startPosition = Viewer.getController().getSelectedPosition(SelectionType.REFERENCE).getStartPosition() * DashBoard.fontWidth - getHorizontalScrollBar().getValue();
				int width = ((Viewer.getController().getSelectedPosition(SelectionType.REFERENCE).getEndPosition() + 1) - Viewer.getController().getSelectedPosition(SelectionType.REFERENCE).getStartPosition()) * DashBoard.fontWidth;
				g.fillRect(startPosition, 0, width, 24);
			} else if (Viewer.getController().getSelectedPosition(SelectionType.REFERENCE).getSelectedPositions().size() == 1) {
//				Color color = new Color(0, 0, 1, 0.2f);
				g.setColor(selectionColor);
				int startPosition = Viewer.getController().getSelectedPosition(SelectionType.REFERENCE).getStartPosition() * DashBoard.fontWidth - getHorizontalScrollBar().getValue();
				g.fillRect(startPosition, 0, DashBoard.fontWidth, 24);
			}
		}
	}

	/**
	 * Set contig panels height
	 * @param rows number of rows
	 */
	private void setHeight(int rows) {
		this.setMinimumSize(new Dimension(this.getMinimumSize().width, rows * rowHeight + 1));
		this.setMaximumSize(new Dimension(this.getMaximumSize().width, rows * rowHeight + 1));
		this.setPreferredSize(new Dimension(this.getPreferredSize().width, rows * rowHeight + 1));
	}

	/**
	 * Displays the given part of reference contig from position. Stores the nucleotide
	 * and color sequences, shows only the necessary sequence. Uses event dispatch thread
	 * @param position start position of the part in the reference contig
	 * @param nucleotideSequence
	 * @param colorSequence
	 */
	public void showReferenceContig(final int position, final String nucleotideSequence, final String colorSequence) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (!FastaPanel.this.isVisible()) {
					FastaPanel.this.setVisible(true);
				}
				final SequenceDisplayMode sequenceShowMode = Viewer.getActiveProfile().getSequenceDisplayMode();
				referenceContigSequencePadding = position;
				referenceContigNucleotideLabel.setText(nucleotideSequence.toUpperCase());
				referenceContigColorLabel.setText(colorSequence);
				contentPanel.removeAll();
				contentPanel.repaint();
				gridBagConstraints.gridy = 0;

				gridBagConstraints.insets = new Insets(0, referenceContigSequencePadding * DashBoard.fontWidth, 0, 0);
				contentPanel.add(referenceContigNucleotideLabel, gridBagConstraints);
				gridBagConstraints.gridy++;
				referenceContigNucleotideLabel.setVisible(sequenceShowMode != SequenceDisplayMode.COLOR);

				gridBagConstraints.insets = new Insets(0, referenceContigSequencePadding * DashBoard.fontWidth + DashBoard.fontWidth / 2, 0, 0);
				contentPanel.add(referenceContigColorLabel, gridBagConstraints);
				gridBagConstraints.gridy++;
				referenceContigColorLabel.setVisible(sequenceShowMode != SequenceDisplayMode.NUCLEOTIDE && !(referenceContigColorLabel.getFont().getSize() != 17 && sequenceShowMode == SequenceDisplayMode.BOTH));

				setHeight(referenceContigColorLabel.isVisible() && referenceContigNucleotideLabel.isVisible() ? 2 : 1);
				Viewer.getMainWindow().getDashBoard().validate();
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReferenceGenom"));
			}
		});
	}

	/**
	 * @return number of characters that reference contig contains
	 */
	public int getReferenceContigLength() {
		return referenceContigLength;
	}

	/**
	 * @return displayed part of the reference contig in nucleotide sequence
	 */
	public String getDisplayedReferenceContig() {
		int sequenceStart = getHorizontalScrollBarValue();

		final int beginIndex = sequenceStart - referenceContigSequencePadding;
		final int endIndex = Math.min(referenceContigLength - referenceContigSequencePadding, beginIndex + getDisplayableCharacters());

		if (SwingUtilities.isEventDispatchThread()) {
			return referenceContigNucleotideLabel.getText().substring(beginIndex, endIndex);
		} else {
			SwingComponentQueryTask<String> task = new SwingComponentQueryTask<String>() {

				@Override
				protected String query() {
					return FastaPanel.this.referenceContigNucleotideLabel.getText().substring(beginIndex, endIndex);
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
	 * Reloads content to display it from a specific position, that couldnt be shown
	 * completely
	 * @param screenPosition first position of content displayed on the screen
	 */
	protected void reload(int screenPosition) {
		if (this.getHorizontalScrollBar() != null) {
			// The new position will be in the middle of the new load
			int start = screenPosition - Viewer.getApplicationProperties().getBufferSize() / 2 > -1 ? screenPosition - Viewer.getApplicationProperties().getBufferSize() / 2 : 0;
			if (Viewer.getController().isFastaFileOpened() && start < this.getReferenceContigLength()) {
				readFromPosition(start);
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
				referenceContigColorLabel.setFont(DashBoard.labelFont);
				referenceContigNucleotideLabel.setFont(DashBoard.labelFont);
				SequenceDisplayMode sequenceShowMode = Viewer.getActiveProfile().getSequenceDisplayMode();

				GridBagConstraints contraints = layout.getConstraints(referenceContigColorLabel);
				contraints.insets.left = referenceContigSequencePadding * DashBoard.fontWidth + DashBoard.fontWidth / 2;
				layout.setConstraints(referenceContigColorLabel, contraints);
				referenceContigNucleotideLabel.setVisible(sequenceShowMode != SequenceDisplayMode.COLOR);
				referenceContigNucleotideLabel.repaint();

				contraints = layout.getConstraints(referenceContigNucleotideLabel);
				contraints.insets.left = referenceContigSequencePadding * DashBoard.fontWidth;
				layout.setConstraints(referenceContigNucleotideLabel, contraints);
				referenceContigColorLabel.setVisible(sequenceShowMode != SequenceDisplayMode.NUCLEOTIDE && !(referenceContigColorLabel.getFont().getSize() != 17 && sequenceShowMode == SequenceDisplayMode.BOTH));
				referenceContigColorLabel.repaint();

				setHeight(referenceContigColorLabel.isVisible() && referenceContigNucleotideLabel.isVisible() ? 2 : 1);

				Viewer.getMainWindow().getDashBoard().validate();
			}
		});

//		readFromPosition(lastLoadStartIndex);
	}

	/**
	 * Reads part of the actual contig from the given position.
	 * When it is done, the controller warns the view and it will be displayed.
	 * If read fails beacouse of fatal error that prevents further reads, the reader
	 * will be closed and panel cleared
	 * @param position start position of the part of the contig
	 * @throws IOException
	 * @throws hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException
	 */
	private void readFromPosition(int position) {
		if (!Viewer.getController().isFastaFileOpened()) {
			return;
		}
		Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReferenceGenom"));
		try {
			position = Math.min(position, referenceContigLength - 1);
			Viewer.getController().readFromPosition(position);
			lastLoadStartIndex = position;
		} catch (IOException ex) {
			logger.warn(ex.getMessage());
			Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReferenceGenom"));
			this.clear();
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("errorMessageIO"), Viewer.getLabelResources().getString("dialogTitleError"), JOptionPane.ERROR_MESSAGE);
				}
			});
		} catch (final FastaRandomReaderException ex) {
			logger.warn(ex.getMessage());
			Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReferenceGenom"));
			if (ex instanceof ContigIndexOutOfBoundsException) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						JOptionPane.showMessageDialog(Viewer.getMainWindow(), MessageFormat.format(
							Viewer.getLabelResources().getString("errorMessageContigIndexOutOfBounds"),
							((ContigIndexOutOfBoundsException) ex).getContigIndex()),
							Viewer.getLabelResources().getString("dialogTitleError"),
							JOptionPane.ERROR_MESSAGE);
					}
				});
			} else if (ex instanceof MissingContigStartIndicesException) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("errorMessageMissingContigStartIndices"),
							Viewer.getLabelResources().getString("dialogTitleError"),
							JOptionPane.ERROR_MESSAGE);
					}
				});
			} else if (ex instanceof ContigPositionOutOfBoundsException) {
				//Do nothing
				return;
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("errorMessageUnknownError"),
							Viewer.getLabelResources().getString("dialogTitleError"),
							JOptionPane.WARNING_MESSAGE);
					}
				});
			}
		} catch (final Exception ex) {
			logger.warn(ex.getMessage());
			Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingReferenceGenom"));
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(Viewer.getMainWindow(), ex.getMessage(),
						Viewer.getLabelResources().getString("dialogTitleError"),
						JOptionPane.WARNING_MESSAGE);
				}
			});
		}
	}

	@Override
	public void clear() {
		super.clear();
		lastLoadStartIndex = 0;
		referenceContigLength = 0;

	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// FĂˇjl megynitĂˇsa Ă©s bezĂˇrĂˇs esetĂ©n olvas a fĂˇjlbĂłl vagy tĂ¶rli a kĂ©pernyĹ‘t
		if (evt.getPropertyName().equals(ViewerController.VIEWER_PROFILE_PROPERTY)) {
			if (Viewer.getController().isFastaFileOpened()) {
//				if (((ProfileProperties) evt.getNewValue()).isNeedReload()) {
//					readFromPosition(this.getHorizontalScrollBarValue());
//				} else {
				this.repaintContent();
//				}
			}
		} else if (evt.getPropertyName().equals(ViewerFastaModel.REFERENCE_LOAD)) {
			boolean isFileOpened = evt.getNewValue() != null;
			if (isFileOpened) {
				// OlvasĂˇs
				Viewer.getController().setModelProperty(ViewerFastaModel.ACT_CONTIG_INDEX, 0);
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
			} else {
				this.clear();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						FastaPanel.this.setVisible(false);
					}
				});
				this.invokePanelResizeListeners();
			}
		} // Contig váltásnál beolvassa az elejét
		else if (evt.getPropertyName().equals(
			ViewerFastaModel.ACT_CONTIG_INDEX)) {
			this.clear();
			this.referenceContigLength = Viewer.getController().getContigLength();
			this.invokePanelResizeListeners();
			this.readFromPosition(this.getHorizontalScrollBar().getValue() / DashBoard.fontWidth);
		}

	}
}
