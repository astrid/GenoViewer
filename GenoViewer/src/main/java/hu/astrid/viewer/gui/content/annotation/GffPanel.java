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

package hu.astrid.viewer.gui.content.annotation;

import hu.astrid.mapping.model.GffRecord;
import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.DashBoard;
import hu.astrid.viewer.gui.ResizeableScrollPanel;
import hu.astrid.viewer.model.ViewerGffModel;
import hu.astrid.viewer.util.FileTypes;
import java.awt.Component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 *
 * @author Szuni
 */
public class GffPanel extends ResizeableScrollPanel implements AbstractView {

	private static final long serialVersionUID = 3L;
	/**Height of a row in pixels*/
	public static final int ROW_HEIGHT = 24;
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	private int maxLengthOfAnnotations;
	/** Default logger */
	private static final Logger logger = Logger.getLogger(GffPanel.class);
	public final FeatureNavigationDialog featureNavigationDialog = new FeatureNavigationDialog();

//	private Set<String> groupTypes = new HashSet<String>(Arrays.asList(new String[]{"gene", "ORF", "tRNA", "snRNA", "ncRNA", "snoRNA", "rRNA", "pseudogene"}));
//	private Set<String> featureTypes = new HashSet<String>(Arrays.asList(new String[]{"CDS", "start_codon", "stop_codon", "gap", "noncoding_exon", "intron"}));
	/** Creates new form GffPanel */
	public GffPanel() {
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;

		setMaximumSize(new Dimension(32767, ROW_HEIGHT + 1));
		setMinimumSize(new Dimension(23, ROW_HEIGHT + 1));
		setPreferredSize(new java.awt.Dimension(1024, 30));

		featureNavigationDialog.setAlwaysOnTop(true);
		featureNavigationDialog.setLocationRelativeTo(Viewer.getMainWindow());
	}

	/**
	 * @return last annotated position in the features
	 */
	public int getMaxLengthOfAnnotations() {
		return maxLengthOfAnnotations;
	}

	/**
	 * Show annotations (groups and features) on the panel. Uses event dispatch thread
	 * @param annotations annotation hierarchy
	 * @param annotationsVisibility
	 * @param groups
	 */
	public void setAnnotations(final Map<String, Set<GffRecord>> annotations, final Map<String, Boolean> annotationsVisibility, final Set<String> groups) {
		Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingAnnotations"));
		Runnable clearTask = new Runnable() {

			@Override
			public void run() {
				if (!GffPanel.this.isVisible()) {
					GffPanel.this.setVisible(true);
				}

				contentPanel.removeAll();
				contentPanel.repaint();
			}
		};
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(clearTask);
			} catch (InterruptedException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (InvocationTargetException ex) {
				logger.error(ex.getMessage(), ex);
			}
		} else {
			clearTask.run();
		}


		//TODO hosszú nem jelenik meg

		for (String type : annotations.keySet()) {
			if (annotationsVisibility.containsKey(type) && annotationsVisibility.get(type)) {
				for (GffRecord record : annotations.get(type)) {
					if (groups.contains(type)) {
						final GroupLabel group = new GroupLabel(record);
						gridBagConstraints.insets = new Insets(0, group.getStartPosition() * DashBoard.fontWidth, 0, 0);
						if (!SwingUtilities.isEventDispatchThread()) {
							try {
								SwingUtilities.invokeAndWait(new Runnable() {

									@Override
									public void run() {
										contentPanel.add(group, gridBagConstraints);
									}
								});
							} catch (InterruptedException ex) {
								logger.error(ex.getMessage(), ex);
							} catch (InvocationTargetException ex) {
								logger.error(ex.getMessage(), ex);
							}
						} else {
							contentPanel.add(group, gridBagConstraints);
						}
					} else {
						final FeatureLabel feature = new FeatureLabel(record);
						gridBagConstraints.insets = new Insets(0, feature.getStartPosition() * DashBoard.fontWidth, 0, 0);
						if (!SwingUtilities.isEventDispatchThread()) {
							try {
								SwingUtilities.invokeAndWait(new Runnable() {

									@Override
									public void run() {
										contentPanel.add(feature, gridBagConstraints);
									}
								});
							} catch (InterruptedException ex) {
								logger.error(ex.getMessage(), ex);
							} catch (InvocationTargetException ex) {
								logger.error(ex.getMessage(), ex);
							}
						} else {
							contentPanel.add(feature, gridBagConstraints);
						}
					}
				}
			}
		}


		Runnable finalizeTask = new Runnable() {

			@Override
			public void run() {
				GffPanel.this.validate();
				featureNavigationDialog.loadFeatureTable();
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingAnnotations"));
			}
		};
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(finalizeTask);
			} catch (InterruptedException ex) {
				logger.error(ex.getMessage());
			} catch (InvocationTargetException ex) {
				logger.error(ex.getMessage(), ex);
			}
		} else {
			finalizeTask.run();
		}
	}

	/**
	 * Repaint the annotations
	 */
	@Override
	public void repaintContent() {
		if (contentPanel.getComponentCount() > 0) {
			Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingAnnotations"));

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					for (Component c : contentPanel.getComponents()) {
						AbstractAnnotationLabel annotation = (AbstractAnnotationLabel) c;
						GridBagConstraints contraints = layout.getConstraints(annotation);
						contraints.insets.left = annotation.getStartPosition() * DashBoard.fontWidth;
						layout.setConstraints(annotation, contraints);
						annotation.invalidate();
					}
					contentPanel.paintImmediately(contentPanel.getVisibleRect());
					Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageDrawingAnnotations"));
				}
			});
		}
	}

	@Override
	public void clear() {
		super.clear();
		maxLengthOfAnnotations = 0;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewerGffModel.ANNOTATIONS_LOAD)) {
			boolean isFileOpened = evt.getNewValue() != null;
			if (isFileOpened) {
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
				maxLengthOfAnnotations = Viewer.getController().getMaxLengthOfAnnotation();
				this.invokePanelResizeListeners();

				Viewer.getController().loadAnnotationData();
				if (evt.getNewValue().toString().equals(Viewer.getController().getFileNameInActProject(FileTypes.GFF)) && Viewer.getController().isAnnotationsReady()) {
					setAnnotations(Viewer.getController().getAnnotations(), Viewer.getController().getAnnotationsVisibility(), Viewer.getController().getAnnotationGroups());
				} else {
					AnnotationTypesDialog dialog = new AnnotationTypesDialog(Viewer.getMainWindow(), true);
					dialog.customize(Viewer.getController().getAnnotationTypes(), Viewer.getController().getAnnotationsVisibility(), Viewer.getController().getAnnotationGroups());
					dialog.setLocationRelativeTo(Viewer.getMainWindow());
					dialog.setVisible(true);
				}
			} else {
				this.clear();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						GffPanel.this.setVisible(false);
					}
				});

				this.invokePanelResizeListeners();
			}
		}
	}
}
