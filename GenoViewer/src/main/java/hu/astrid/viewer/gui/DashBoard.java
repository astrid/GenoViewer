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

package hu.astrid.viewer.gui;

import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.content.consensus.ConsensusPanel;
import hu.astrid.viewer.gui.content.alignment.AlignmentPanel;
import hu.astrid.viewer.gui.content.annotation.GffPanel;
import hu.astrid.viewer.gui.content.coverage.CoveragePanel;
import hu.astrid.viewer.gui.content.fasta.FastaPanel;
import hu.astrid.viewer.gui.ruler.RulerPanel;
import hu.astrid.viewer.model.SelectionModel.SelectionType;

import hu.astrid.viewer.util.SwingComponentQueryTask;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

/**
 * DashBoard for holding panels of Viewer
 * @author Szuni, Máté
 */
public class DashBoard extends JPanel {

	/** Default logger */
	private static final Logger logger = Logger.getLogger(DashBoard.class);
	private static final long serialVersionUID = 1L;
	/**Font width*/
	public static int fontWidth = 10;
	/**Font hight*/
	public static int fontHeight = 24;
	/**Container for ruler and fasta content*/
	private Box contigBox = Box.createVerticalBox();
	/**Container for {@code contigBox} and a filler for vertical scrollbars place*/
	private Box horizontalBox1 = Box.createHorizontalBox();
	/**Container for {@code scrollBar} and a filler for vertical scrollbars place*/
	private Box horizontalScrollbarBox = Box.createHorizontalBox();
	protected RulerPanel ruler = new RulerPanel();
	protected FastaPanel fastaPanel = new FastaPanel();
	protected ConsensusPanel consensusPanel = new ConsensusPanel();
	protected GffPanel gffPanel = new GffPanel();
	protected AlignmentPanel alignmentPanel = new AlignmentPanel();
	protected CoveragePanel coveragePanel = new CoveragePanel();
	protected ScrollbarPanel scrollBar = new ScrollbarPanel();
	private Component scrollbarStrut1 = Box.createHorizontalStrut(0);
	private Component scrollbarStrut2 = Box.createHorizontalStrut(0);
	public final static byte[] zoomLevels = new byte[]{2, 7, 12, 17};
	private final static short[] increments = new short[]{100, 50, 25, 10};
	public static byte actualZoomLevel = (byte) (zoomLevels.length - 1);
	public static byte prevZoomLevel = (byte) zoomLevels.length;
	public static int defaultZoom = zoomLevels[zoomLevels.length - 1];
	/**Font of the contigs and reads*/
	public static Font labelFont = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, defaultZoom);
	/**List for store panels added to this containers*/
	private List<ContentPanel> panels = new ArrayList<ContentPanel>();

	/**
	 * Create the DashBoard.
	 */
	public DashBoard() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		ruler.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		ruler.setLabelFontMetrics(this.getFontMetrics(labelFont));
		ruler.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				int curPos = (ruler.getHorizontalScrollBar().getValue() + e.getX()) / fontWidth;
				if (ResizeableScrollPanel.scrollPointerPosition != null && ResizeableScrollPanel.scrollPointerPosition == curPos) {
					ResizeableScrollPanel.scrollPointerPosition = null;
				}
				if (ResizeableScrollPanel.selectionPosition != null && ResizeableScrollPanel.selectionPosition == curPos) {
					ResizeableScrollPanel.selectionPosition = null;
				} else {
					ResizeableScrollPanel.selectionPosition = curPos;
				}
				DashBoard.this.paintChildren(DashBoard.this.getGraphics());
			}
		});
		ruler.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				int curPos = (ruler.getHorizontalScrollBar().getValue() + e.getX()) / fontWidth;
				if (ResizeableScrollPanel.selectionPosition != null && ResizeableScrollPanel.selectionPosition != curPos) {
					ResizeableScrollPanel.selectionPosition = curPos;
					DashBoard.this.paintChildren(DashBoard.this.getGraphics());
				}
			}
		});

		fastaPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if (ResizeableScrollPanel.selectionEnabled) {
					int curPos = (ruler.getHorizontalScrollBar().getValue() + e.getX()) / fontWidth;
					ResizeableScrollPanel.selectionPosition = curPos;
					Viewer.getController().addSelectedPosition(ResizeableScrollPanel.selectionPosition, SelectionType.REFERENCE);
					fastaPanel.repaint();
				}
			}
		});
		fastaPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		fastaPanel.setVisible(false);
		panels.add(fastaPanel);

		consensusPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if (ResizeableScrollPanel.selectionEnabled) {
					int curPos = (ruler.getHorizontalScrollBar().getValue() + e.getX()) / fontWidth;
					ResizeableScrollPanel.selectionPosition = curPos;
					Viewer.getController().addSelectedPosition(ResizeableScrollPanel.selectionPosition, SelectionType.CONSENSUS);
					consensusPanel.repaint();
				}
			}
		});
		consensusPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		consensusPanel.setVisible(false);
		panels.add(consensusPanel);

		gffPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gffPanel.setVisible(false);
		panels.add(gffPanel);

		alignmentPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panels.add(alignmentPanel);
//		scrollBar.setViewportView(alignmentPanel);

		coveragePanel.setVisible(false);
		panels.add(coveragePanel);

		scrollBar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		ScrollbarPanel.panelResizeListeners.add(panelResizeListener);

		// Reads from file, when because of scrollbar use unloaded characters
		// have to be shown
		scrollBar.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(final AdjustmentEvent e) {
				// Distribute the new value to components
				ruler.getViewport().setViewPosition(new Point(e.getValue(), 0));
				fastaPanel.getHorizontalScrollBar().setValue(e.getValue());
				fastaPanel.repaint();
				consensusPanel.getHorizontalScrollBar().setValue(e.getValue());
				consensusPanel.repaint();
				gffPanel.getHorizontalScrollBar().setValue(e.getValue());
				alignmentPanel.getHorizontalScrollBar().setValueIsAdjusting(e.getValueIsAdjusting());
				alignmentPanel.getHorizontalScrollBar().setValue(e.getValue());
				coveragePanel.reload(e.getValue());
				// else if (e.getValueIsAdjusting() && Viewer.getPropertyHandler().getActiveProfile().isShowCoveragePanel()) {
				//	;
				//}
			}
		});
		scrollBar.getHorizontalScrollBar().setUnitIncrement(fontWidth);
		// Fits header when vertical scrollbar appears or disappears
		alignmentPanel.getVerticalScrollBar().addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				fixPanelsWidth(true);
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				fixPanelsWidth(false);
			}
		});
		// Fits header when window is resized
		alignmentPanel.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				fixPanelsWidth(alignmentPanel.getVerticalScrollBar().isVisible());
			}

			@Override
			public void componentShown(ComponentEvent e) {
				fixPanelsWidth(true);
				return;
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				fixPanelsWidth(false);
				return;
			}
		});
		contigBox.add(ruler);
		contigBox.add(fastaPanel);
		contigBox.add(consensusPanel);
		contigBox.add(gffPanel);
		contigBox.add(coveragePanel);

		horizontalBox1.add(contigBox);
		horizontalBox1.add(scrollbarStrut1);
		horizontalScrollbarBox.add(scrollBar);
		horizontalScrollbarBox.add(scrollbarStrut2);
		this.add(horizontalBox1);
		this.add(alignmentPanel);
		this.add(horizontalScrollbarBox);
		ruler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		fastaPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		consensusPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		gffPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		coveragePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		alignmentPanel.setBorder(BorderFactory.createEmptyBorder());
		scrollBar.setBorder(BorderFactory.createEmptyBorder());

		for (ContentPanel contentPanel : panels) {
			contentPanel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					scrollBar.contentPanel.requestFocus();
				}
			});
		}
	}

	/**
	 * @return width of display in number of characters
	 */
	public Integer getDisplayWidth() {
		if (SwingUtilities.isEventDispatchThread()) {
			return scrollBar.getViewport().getWidth() / fontWidth;
		} else {

			SwingComponentQueryTask<Integer> task = new SwingComponentQueryTask<Integer>() {

				@Override
				protected Integer query() {
					return DashBoard.this.scrollBar.getViewport().getWidth() / fontWidth;
				}
			};
			SwingUtilities.invokeLater(task);
			try {
				return task.get();
			} catch (InterruptedException ex) {
				logger.trace("querying display width interrupted");
			} catch (ExecutionException ex) {
				logger.error(ex.getMessage(), ex);
			}
			return 0;
		}
	}

	/**
	 * Set the panels width fit to the alignmment panels with or without the scrollbar
	 * @param isScrollbarShown
	 */
	private void fixPanelsWidth(boolean isScrollbarShown) {
		int scrollbarWidth = isScrollbarShown ? alignmentPanel.getVerticalScrollBar().getSize().width : 0;
		horizontalBox1.remove(scrollbarStrut1);
		scrollbarStrut1 = Box.createHorizontalStrut(scrollbarWidth);
		horizontalBox1.add(scrollbarStrut1);
		horizontalBox1.validate();
		horizontalScrollbarBox.remove(scrollbarStrut2);
		scrollbarStrut2 = Box.createHorizontalStrut(scrollbarWidth);
		horizontalScrollbarBox.add(scrollbarStrut2);
		horizontalScrollbarBox.validate();
	}

	/**
	 * Returns the the displayed part of the reference contig in nucleotide sequence.
	 * @return
	 */
	public String getReferenceContig() {
		return fastaPanel.getDisplayedReferenceContig();
	}

	/**
	 * Returns the the displayed part of the reference contig in nucleotide sequence.
	 * @return
	 */
	public String getConsensusContig() {
		return consensusPanel.getDisplayedReferenceContig();
	}

	/**
	 * Scrolls the panel to show the given position on the middle of the panel.
	 * @param position postion to show, started from 0
	 */
	public void scrollToPosition(final Integer position) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				scrollBar.getHorizontalScrollBar().setValue((position.intValue() - getDisplayWidth() / 2) * fontWidth);
				DashBoard.this.paintChildren(DashBoard.this.getGraphics());
				logger.trace("scrolled to " + position);
			}
		});

	}

	/**
	 * Returns the panels width in number of characters
	 * @return
	 */
	protected int getPanelsWidth() {
		return Math.max(Math.max(Math.max(fastaPanel.getReferenceContigLength(), alignmentPanel.getReferenceLength()), gffPanel.getMaxLengthOfAnnotations()), consensusPanel.getConsesusContigLength());
	}

	/**
	 * Sets the panels width to fit the longes item. Uses event dispatch thread
	 */
	private void setPanelsPreferedWidth() {
		scrollBar.setPreferredWidth(getPanelsWidth());
		ruler.setPreferredWidth(getPanelsWidth());
		for (ContentPanel panel : this.getPanels()) {
			if (panel instanceof ResizeableScrollPanel) {
				((ResizeableScrollPanel) panel).setPreferredWidth(getPanelsWidth());
			}
		}
	}

	/**
	 * Reload the currend displayed content and show it in given way.
	 */
	private void reload() {
		new SwingWorker<Object, Object>() {

			Throwable throwable = null;

			@Override
			protected Object doInBackground() throws Exception {
				try {
					for (ContentPanel panel : DashBoard.this.getPanels()) {
						panel.repaintContent();
					}
				} catch (Throwable e) {
					throwable = e;
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

	/**
	 * Zoom to default.
	 */
	public void zoomDefault() {
		actualZoomLevel = (byte) (zoomLevels.length - 1);
		doZoom(zoomLevels[actualZoomLevel]);
	}

	/**
	 * Zoom out.
	 */
	public void zoomOut() {
		actualZoomLevel = (byte) ((actualZoomLevel > 0) ? actualZoomLevel - 1 : 0);
		doZoom(zoomLevels[actualZoomLevel]);
	}

	/**
	 * Zoom in.
	 */
	public void zoomIn() {
		actualZoomLevel = (byte) ((actualZoomLevel < zoomLevels.length - 1) ? actualZoomLevel + 1 : zoomLevels.length - 1);
		doZoom(zoomLevels[actualZoomLevel]);
	}

	/**
	 * Set the zoom and repaint/reload the panels.
	 * @param zoom
	 */
	private void doZoom(byte zoomValue) {
		// System.out.println("zoomValue is:" + zoomValue);
		if (prevZoomLevel == zoomValue) {
			return;
		}
		int middleScreenPositionX = (scrollBar.getHorizontalScrollBar().getValue()) / fontWidth + getDisplayWidth() / 2;
		labelFont = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, zoomValue);
		fontWidth = this.getFontMetrics(labelFont).charWidth('A');
		fontHeight = this.getFontMetrics(labelFont).getHeight();
//		ruler.setLabelFontMetrics(this.getFontMetrics(labelFont));
		ruler.setIncrement(increments[actualZoomLevel]);
		ruler.repaint();
		reload();
		scrollToPosition(middleScreenPositionX);
		scrollBar.invokePanelResizeListeners();
		scrollBar.getHorizontalScrollBar().setUnitIncrement(fontWidth);
		prevZoomLevel = zoomValue;
	}

	public static byte getZoomValue() {
		return zoomLevels[actualZoomLevel];
	}

	/**
	 * Simple empty panel that contains a scrollbar
	 */
	protected static class ScrollbarPanel extends ResizeableScrollPanel {

		private static final long serialVersionUID = 1L;

		ScrollbarPanel() {
//			this.setPreferredSize(new Dimension(1024, 0));
		}

		@Override
		public void repaintContent() {
			throw new UnsupportedOperationException("Not supported.");
		}
	}

	/**
	 * @return position of scrollbar, started from 0, not running in event dispatch thread!!!
	 */
	public int scrollBarPosition() {
		return scrollBar.getHorizontalScrollBar().getValue() / fontWidth;
	}

	/**
	 * @return scrollbar position of the center of the screen
	 */
	public Integer getScrollBarMiddle() {
		if (SwingUtilities.isEventDispatchThread()) {
			return scrollBar.getHorizontalScrollBar().getValue() / fontWidth + getDisplayWidth() / 2;
		} else {
			SwingComponentQueryTask<Integer> task = new SwingComponentQueryTask<Integer>() {

				@Override
				protected Integer query() {
					return scrollBar.getHorizontalScrollBar().getValue() / fontWidth + getDisplayWidth() / 2;
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
	 * Set the horizontal scrollbar enabled or disabled
	 * @param isEnabled
	 */
	public void setScrollbarEnabled(boolean isEnabled) {
		this.scrollBar.getHorizontalScrollBar().setEnabled(isEnabled);
	}

	/**
	 * @return list of panels in this container
	 */
	public List<ContentPanel> getPanels() {
		return panels;
	}

	/**
	 * This listener class sets all panels width in this container
	 */
	public class PanelResizeListener {

		/**
		 * Set panels width to the maximum value of them.
		 * @see DashBoard#setPanelsPreferedWidth()
		 */
		public void resize() {
			DashBoard.this.setPanelsPreferedWidth();
		}
	};
	/**Listener for resizeing panels*/
	private PanelResizeListener panelResizeListener = new PanelResizeListener();

	/**
	 *
	 */
	public void clearPaintings() {
		ResizeableScrollPanel.scrollPointerPosition = null;
		ResizeableScrollPanel.selectionPosition = null;
		Viewer.getController().clearSelectedPositions();
		DashBoard.this.paintChildren(DashBoard.this.getGraphics());
	}
}
