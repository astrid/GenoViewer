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
 * NavigationToolBar.java
 *
 * Created on 2010.06.14., 8:54:01
 */
package hu.astrid.viewer.gui.content.alignment;

import hu.astrid.viewer.Viewer;

import hu.astrid.viewer.util.InDelManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.plaf.ActionMapUIResource;

import org.apache.log4j.Logger;

/**
 *
 * @author zsdoma
 */
public class NavigationToolBar extends javax.swing.JToolBar {

	private static final long serialVersionUID = 1L;
	/* -------------------------------- */
	/** Snps coverageMap keys */
	private List<Integer> keyList = new LinkedList<Integer>();
	/** Stores the Snp positions and coverage */
	private Map<Integer, Integer> coverageMap;
	/** Searched SNP coverage */
	private int cover = 1;
	/** Actual search position */
	private int position = 0;
	/** BamFileReader start position */
	private int start = 0;
	/** BamFileReader end position */
	private int end = 0;
	/** Background search thread */
	private SwingWorker<Object, Object> swingWorker;
	/** Positive direction of search */
	private static final boolean POSITIVE = true;
	/** Negative direction of search */
	private static final boolean NEGATIVE = false;
	/** Direction option (positive or negative) */
	private boolean direction = true;
	/** Is Snp found */
	private boolean found = false;
	/** Default logger */
	/* -------------------------------- */

	/* -------------------------------- */
	private InDelManager inDelManager = null;
	private int inDelCoverageValue;
	/* -------------------------------- */
	private static final Logger logger = Logger.getLogger(NavigationToolBar.class);


	/** Creates new form NavigationToolBar */
	public NavigationToolBar() {
		initComponents();
		snpCheck.setSelected(true);
		/* from indel */
		insertionCheck.setSelected(false);
		deletionCheck.setSelected(false);
		inDelCoverageValue = 1;
		coverText.setText(new Integer(inDelCoverageValue).toString());

		assignAccelerator(prevButton, KeyEvent.VK_F3, KeyEvent.SHIFT_DOWN_MASK, "prevSearch", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				prevButtonActionPerformed(e);
			}
		});
		assignAccelerator(nextButton, KeyEvent.VK_F3, 0, "nextSearch", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				nextButtonActionPerformed(e);
			}
		});

	}

	private void assignAccelerator(JComponent component, int keyEvent, int modifiers, String actionName, AbstractAction action) {
		InputMap keyMap = new ComponentInputMap(component);
		keyMap.put(KeyStroke.getKeyStroke(keyEvent, modifiers), actionName);
		ActionMap actionMap = new ActionMapUIResource();
		actionMap.put(actionName, action);
		SwingUtilities.replaceUIActionMap(component, actionMap);
		SwingUtilities.replaceUIInputMap(component, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
		String tooltip = component.getToolTipText();
		if(component.getToolTipText()==null)
			tooltip="";
		String keyText = KeyEvent.getKeyText(keyEvent);
		String modifierText = modifiers!=0 ? KeyEvent.getModifiersExText(modifiers)+"+":"";
		component.setToolTipText(tooltip+" ("+modifierText+keyText+")");
		/* from indel */
	}

	public void useNewInDelManager() {
		if (inDelManager == null) {
			inDelManager = new InDelManager(this.inDelCoverageValue, Viewer.getController().lastReadEndPos());
		} else {

			inDelManager.setMinCoverage(this.inDelCoverageValue);
			inDelManager.setMaxReadLength(Viewer.getController().lastReadEndPos());
		}
	}

	public void refreshI18N() {
		ResourceBundle resBundle = Viewer.getLabelResources();
		deletionCheck.setText(resBundle.getString("deletionCheck"));
		insertionCheck.setText(resBundle.getString("insertCheck"));
		snpCheck.setText(resBundle.getString("snpCheck"));
		//TODO Navigation label ??
		//indelLabel.setText(resBundle.getString("indelNavigationPanel"));
		coverLabel.setText(resBundle.getString("navigationCoverage"));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        prevButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        coverLabel = new javax.swing.JLabel();
        coverText = new javax.swing.JTextField();
        snpCheck = new javax.swing.JRadioButton();
        insertionCheck = new javax.swing.JRadioButton();
        deletionCheck = new javax.swing.JRadioButton();

        setFloatable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        prevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/snpnavigation/prevIcon.png"))); // NOI18N
        prevButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        prevButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/snpnavigation/stopIcon.png"))); // NOI18N
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/snpnavigation/nextIcon.png"))); // NOI18N
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        coverLabel.setDisplayedMnemonic(Viewer.getLabelResources().getString("navigationCoverageMemonic").charAt(0));
        coverLabel.setLabelFor(coverText);
        coverLabel.setText(Viewer.getLabelResources().getString("navigationCoverage")); // NOI18N

        coverText.setMaximumSize(new java.awt.Dimension(2147483647, 30));

        buttonGroup1.add(snpCheck);
        snpCheck.setMnemonic(Viewer.getLabelResources().getString("snpCheckMemonic").charAt(0));
        snpCheck.setText(Viewer.getLabelResources().getString("snpCheck")); // NOI18N

        buttonGroup1.add(insertionCheck);
        insertionCheck.setMnemonic(Viewer.getLabelResources().getString("insertCheckMemonic").charAt(0));
        insertionCheck.setText(Viewer.getLabelResources().getString("insertCheck")); // NOI18N

        buttonGroup1.add(deletionCheck);
        deletionCheck.setMnemonic(Viewer.getLabelResources().getString("deletionCheckMemonic").charAt(0));
        deletionCheck.setText(Viewer.getLabelResources().getString("deletionCheck")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(prevButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coverLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coverText, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(snpCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(insertionCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deletionCheck)
                .addContainerGap(190, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(coverLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(coverText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(snpCheck)
                        .addComponent(insertionCheck)
                        .addComponent(deletionCheck))
                    .addComponent(nextButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(prevButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
		// TODO add your handling code here:
		if (snpCheck.isSelected()) {
			if (swingWorker == null || swingWorker.isDone()) {
				// TODO i18n
				Viewer.startStatusbarJob(Viewer.getLabelResources().getString("prevSnp"));
				Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(false);
				stopButton.setEnabled(true);
				nextButton.setEnabled(false);
				swingWorker = new SwingWorker<Object, Object>() {

					@Override
					protected Object doInBackground() throws Exception {
						if (direction != NEGATIVE) {
							direction = NEGATIVE;
							keyList.clear();
							start = 0;
						}
						start = Viewer.getMainWindow().scrollBarMiddle();
						if (!keyList.isEmpty() && (start + 1 > keyList.get(keyList.size() - 1))) {
							keyList.clear();
						}
						start -= 499;
						int cover = 1;
						try {
							cover = Integer.parseInt(coverText.getText());
						} catch (NumberFormatException ex) {
							logger.warn(ex.getMessage());
							coverText.setText("1");
						}
						found = false;
						if (position < 0) {
							position = 0;
						}
						while (!isCancelled() && !found) {

//    						logger.trace("keylist " + keyList.size() + " position " + position);

							if (!keyList.isEmpty() && position >= keyList.size()) {
								keyList.clear();
								start -= 500;
							}
							if (keyList.isEmpty()) {
								position = 0;
								while (!isCancelled() && keyList.isEmpty()) {
									end = start + 500;
									if (end < 0) {
										break;
									}
//    								Viewer.showStatusbarMessage(Viewer.getLabelResources().getString("prevSnp") + " [" + end + ", " + start + "]");
									coverageMap = Viewer.getController().loadSnpCoverage(start, end);
//    								logger.debug(" [" + start + ", " + end + "] " + coverageMap.size());
									for (Integer key : coverageMap.keySet()) {
										keyList.add(key);
									}
									if (keyList.isEmpty()) {
										start -= 500;
									}
									Collections.sort(keyList);
									Collections.reverse(keyList);
								}

							}
							if (!keyList.isEmpty() && coverageMap.get(keyList.get(position)) >= cover && Viewer.getMainWindow().scrollBarMiddle() > keyList.get(position) && Viewer.getMainWindow().scrollBarMiddle() > Viewer.getMainWindow().getDisplayWidth() / 2) {
								found = true;
								break;
							}
							if (end < 0) {
								break;
							}
							position++;
						}
						return null;
					}

					@Override
					protected void done() {
						// TODO i18n
						Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(true);
						Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("prevSnp"));
						nextButton.setEnabled(true);
						// stopButton.setEnabled(false);
						if (found) {
							Viewer.getMainWindow().scrollToPosition(keyList.get(position));
//    						logger.trace("találta " + position);
							position++;
						} else if (end < 0) {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), "Nincs találat!");
						}
					}
				};
				swingWorker.execute();
			}
		} else if (insertionCheck.isSelected() || deletionCheck.isSelected()) {
			if (!(hasValidCoverageValue())) {
				getNewCoverageValue();
				return;
			} else if (swingWorker != null && !swingWorker.isDone()) {

				return;
			}
			inDelCoverageValue = Integer.parseInt(coverText.getText());
			inDelManager.setMinCoverage(inDelCoverageValue);
			Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(false);
			if (insertionCheck.isSelected()) {
				Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageSearchingForInsertion"));
			} else if (deletionCheck.isSelected()) {
				Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageSearchingForDeletion"));
			}
			(swingWorker = new SwingWorker<Object, Object>() {

				Throwable throwable = null;
				Integer position;
				boolean cancelled = false;

				@Override
				protected Object doInBackground() {
					int searchFrom = Viewer.getMainWindow().scrollBarMiddle() - 1;
					if (searchFrom < Viewer.getMainWindow().getDisplayWidth() / 2) {
						logger.trace(Viewer.getMainWindow().getDisplayWidth() / 2);
						return null;
					}
					try {
						if (deletionCheck.isSelected()) {
							if (inDelManager.hasPrevDeletion(searchFrom)) {
								position = inDelManager.getPrevDeletion(searchFrom);
							} else if (inDelManager.isSearchCancelled()) {
								cancelled = true;
								position = inDelManager.getSearchInterval()[0];
							}
						} else if (insertionCheck.isSelected()) {
							if (inDelManager.hasPrevInsertion(searchFrom)) {
								position = inDelManager.getPrevInsertion(searchFrom);
							} else if (inDelManager.isSearchCancelled()) {
								cancelled = true;
								position = inDelManager.getSearchInterval()[0];
							}
						}
					} catch (Exception exc) {
						throwable = exc;
					}
					return null;
				}

				@Override
				protected void done() {
					super.done();
					Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(true);
					if (insertionCheck.isSelected()) {
						Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageSearchingForInsertion"));
					} else if (deletionCheck.isSelected()) {
						Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageSearchingForDeletion"));
					}
					if (position != null) {
						Viewer.getMainWindow().scrollToPosition(position);
						if (cancelled) {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("searchForIndelsCancelled"));
						}
					} else if (!cancelled) {
						if (insertionCheck.isSelected()) {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("noMoreInsertion"));
						}
						if (deletionCheck.isSelected()) {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("noMoreDeletion"));
						}
					}
					if (throwable != null) {
						logger.error(throwable.getMessage(), throwable);
					}
				}
			}).execute();
		}
    }//GEN-LAST:event_prevButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
		// TODO add your handling code here:
		if (snpCheck.isSelected()) {
			Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(true);
			swingWorker.cancel(true);
		} else if (insertionCheck.isSelected() || deletionCheck.isSelected()) {
			Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(true);
			inDelManager.cancelRunnningSearch();
		}
    }//GEN-LAST:event_stopButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
		if (snpCheck.isSelected() && (swingWorker == null || swingWorker.isDone())) {
			Viewer.startStatusbarJob(Viewer.getLabelResources().getString("nextSnp"));
			Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(false);
			prevButton.setEnabled(false);
			swingWorker = new SwingWorker<Object, Object>() {

				@Override
				protected Object doInBackground() throws Exception {
					start = Viewer.getMainWindow().scrollBarMiddle();
					if (!keyList.isEmpty() && (start + 1 < keyList.get(0))) {
						keyList.clear();
					}
					start -= 100;
					if (direction != POSITIVE) {
						keyList.clear();
					}
					direction = POSITIVE;
					try {
						cover = Integer.parseInt(coverText.getText());
					} catch (NumberFormatException ex) {
						logger.warn(ex.getMessage());
						coverText.setText("1");
					}
					found = false;
					if (position < 0) {
						position = 0;
					}
					// amíg nincs találat keres és nem szakítják meg a keresést
					while (!isCancelled() && !found) {

						logger.trace("keylist " + keyList.size() + " position " + position);

						// Ha végigjártuk a listát és nincs találat akkor
						// olvassuk
						// be a következő szakaszt!
						if (!keyList.isEmpty() && position >= keyList.size()) {
							keyList.clear();
							start += 500;
						}
						// következő szakasz beolvasása
						if (keyList.isEmpty()) {
							position = 0;
							while (!isCancelled() && keyList.isEmpty()) {
								end = start + 500;
//    								Viewer.showStatusbarMessage(Viewer.getLabelResources().getString("nextSnp") + " [" + start + ", " + end + "]");
								coverageMap = Viewer.getController().loadSnpCoverage(start, end);
								logger.debug(" [" + start + ", " + end + "]" + coverageMap.size());
								for (Integer key : coverageMap.keySet()) {
									keyList.add(key);
								}
								if (keyList.isEmpty()) {
									start += 500;
								}
								if (start > Viewer.getController().getAlignmentReferenceLength()) {
									break;
								}
								Collections.sort(keyList);
							}
						}
						// Ha a Map-ben található adott lefedettségű találat
						// akkor
						// ugrás oda
						if (!keyList.isEmpty() && coverageMap.get(keyList.get(position)) >= cover && Viewer.getMainWindow().scrollBarMiddle() < Viewer.getController().getAlignmentReferenceLength() - (Viewer.getMainWindow().getDisplayWidth() - Viewer.getMainWindow().getDisplayWidth() / 2) - 1 && Viewer.getMainWindow().scrollBarMiddle() < keyList.get(position)) {
							logger.info(Viewer.getMainWindow().scrollBarMiddle() + " " + (Viewer.getController().getAlignmentReferenceLength() - (Viewer.getMainWindow().getDisplayWidth() - Viewer.getMainWindow().getDisplayWidth() / 2) - 1));
							found = true;
							break;
						}
						// ha elérjük a végét akkor jelzi :D
						if (start > Viewer.getController().getAlignmentReferenceLength()) {
							break;
						}
						position++;
					}
					return null;
				}

				@Override
				protected void done() {
					Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(true);
					// TODO i18n
					Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("nextSnp"));

					if (found && !isCancelled()) {
						Viewer.getMainWindow().scrollToPosition(keyList.get(position));
//    						logger.trace("találta " + position);
						position++;
					} else if (start > Viewer.getController().getAlignmentReferenceLength()) {
//    						logger.trace("stop next snp: not found");
						JOptionPane.showMessageDialog(Viewer.getMainWindow(), "Nincs találat!");
//    						logger.trace("nincs találat " + position);
					}
					// stopButton.setEnabled(false);
					prevButton.setEnabled(true);
				}
			};
			swingWorker.execute();
		} else if (insertionCheck.isSelected() || deletionCheck.isSelected()) {
			if (!(hasValidCoverageValue())) {
				getNewCoverageValue();
				return;
			} else if (swingWorker != null && !swingWorker.isDone()) {
				return;
			}
			inDelCoverageValue = Integer.parseInt(coverText.getText());
			inDelManager.setMinCoverage(inDelCoverageValue);
			Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(false);
			if (insertionCheck.isSelected()) {
				Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageSearchingForInsertion"));
			} else if (deletionCheck.isSelected()) {
				Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageSearchingForDeletion"));
			}
			(swingWorker = new SwingWorker<Object, Object>() {

				Throwable throwable = null;
				Integer position;
				boolean cancelled = false;

				@Override
				protected Object doInBackground() {
					int searchFrom = Viewer.getMainWindow().scrollBarMiddle() + 1;
					if (searchFrom > Viewer.getController().getAlignmentReferenceLength() - Viewer.getMainWindow().getDisplayWidth() / 2) {
						return null;
					}
					try {
						if (deletionCheck.isSelected()) {
							if (inDelManager.hasNextDeletion(searchFrom)) {
								position = inDelManager.getNextDeletion(searchFrom);
							} else if (inDelManager.isSearchCancelled()) {
								cancelled = true;
								position = inDelManager.getSearchInterval()[1];
							}
						} else if (insertionCheck.isSelected()) {
							if (inDelManager.hasNextInsertion(searchFrom)) {
								position = inDelManager.getNextInsertion(searchFrom);
							} else if (inDelManager.isSearchCancelled()) {
								cancelled = true;
								position = inDelManager.getSearchInterval()[1];
							}
						}
					} catch (Exception exc) {
						throwable = exc;
					}
					return null;
				}

				@Override
				protected void done() {
					super.done();
					Viewer.getMainWindow().getDashBoard().setScrollbarEnabled(true);
					if (insertionCheck.isSelected()) {
						Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageSearchingForInsertion"));
					} else if (deletionCheck.isSelected()) {
						Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageSearchingForDeletion"));
					}
					if (position != null) {
						Viewer.getMainWindow().scrollToPosition(position);
						if (cancelled) {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("searchForIndelsCancelled"));
						}
					} else if (!cancelled) {
						if (insertionCheck.isSelected()) {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("noMoreInsertion"));
						}
						if (deletionCheck.isSelected()) {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("noMoreDeletion"));
						}
					}
					if (throwable != null) {
						logger.error(throwable.getMessage(), throwable);
					}
				}
			}).execute();
		}
    }//GEN-LAST:event_nextButtonActionPerformed

	private void getNewCoverageValue() {
		JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("coverageValueMustBeValid"));
	}

	private boolean hasValidCoverageValue() {
		int coverageValueCandidate = 1;
		try {
			coverageValueCandidate = Integer.parseInt(coverText.getText());
			if (coverageValueCandidate < 1) {
				return false;
			}
		} catch (NumberFormatException exc) {
			// doesn't matter, set to 1
			logger.warn("doesn't matter, set to 1");
			return false;
		}
		return true;
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel coverLabel;
    private javax.swing.JTextField coverText;
    private javax.swing.JRadioButton deletionCheck;
    private javax.swing.JRadioButton insertionCheck;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JRadioButton snpCheck;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
