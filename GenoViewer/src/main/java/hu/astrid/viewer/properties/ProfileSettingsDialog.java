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

package hu.astrid.viewer.properties;

import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.properties.ProfileProperties.ReadDisplayType;
import hu.astrid.viewer.properties.ProfileProperties.SequenceDisplayMode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Logger;

/**
 * Profile settings frame
 * @author Mat
 */
public class ProfileSettingsDialog extends javax.swing.JDialog {

	/**Serial version id*/
	private static final long serialVersionUID = -1555195499237580417L;
	private static Logger logger = Logger.getLogger(ProfileSettingsDialog.class);
	//TODO egy így ok, de lehetne jobb
	private final DocumentListener documentListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			tempProperties.compareForReload(null);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			;
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			;
		}
	};
	/**Selected index*/
	private int selectedIndex = 0;
	/**Previous property*/
	private ProfileProperties tempProperties;
	/**Previous propery name*/
	private String previousProfile;
	/**Profile list*/
	private DefaultListModel listModel;

	public void refreshProfile() {
		if (tempProperties.getReadDisplayType() == ReadDisplayType.SHORT) {
			readDisplayType.setSelectedIndex(0);
		} else {
			readDisplayType.setSelectedIndex(1);
		}
		if (tempProperties.getSequenceDisplayMode() == ProfileProperties.SequenceDisplayMode.NUCLEOTIDE) {
			sequnceShowMode.setSelectedIndex(0);
		} else if (tempProperties.getSequenceDisplayMode() == ProfileProperties.SequenceDisplayMode.COLOR) {
			sequnceShowMode.setSelectedIndex(1);
		} else {
			sequnceShowMode.setSelectedIndex(2);
		}
		showSnp.setSelected(tempProperties.isShowSNPs());
		navPanel.setSelected(tempProperties.isShowNavigationPanel());
		snp.setForeground(tempProperties.getSNPColor());
		showReadError.setSelected(tempProperties.isShowReadErrors());
		re.setForeground(tempProperties.getReadErrorColor());
		showDirection.setSelected(tempProperties.isShowDirection());
		dbg.setForeground(tempProperties.getDirectionBackgroundColor());
		di.setForeground(tempProperties.getDirectionIndicatorColor());
		c0.setForeground(tempProperties.get0Color());
		c1.setForeground(tempProperties.get1Color());
		c2.setForeground(tempProperties.get2Color());
		c3.setForeground(tempProperties.get3Color());
		gffFeatureTable.setSelected(tempProperties.isShowFeatureTable());
		coveragePanel.setSelected(tempProperties.isShowCoveragePanel());
		nucleotideLabel.refreshViewSettings(tempProperties);
		colorLabel.refreshViewSettings(tempProperties);
		positiveDirectionLabel.setBackground(tempProperties.getPositiveStrandColor());
		negativeDirectionLabel.setBackground(tempProperties.getNegativeStrandColor());
		columnDistanceTextField.getDocument().removeDocumentListener(documentListener);
		columnDistanceTextField.setText(Viewer.getApplicationProperties().getReadDistance() + "");
		columnDistanceTextField.getDocument().addDocumentListener(documentListener);
		//TODO color setting
		snpColorButton.setColor(tempProperties.getSNPColor());
		readErrorColorButton.setColor(tempProperties.getReadErrorColor());
		dbgColorButton.setColor(tempProperties.getDirectionBackgroundColor());
		diColorButton.setColor(tempProperties.getDirectionIndicatorColor());
		positiveDirectionButton.setColor(tempProperties.getPositiveStrandColor());
		negativeDirectionButton.setColor(tempProperties.getNegativeStrandColor());

		manualSelectionColorChooser.setColor(tempProperties.getManualSelectionColor());
		autoSelectionColorChooser.setColor(tempProperties.getAutoSelectionColor());
		insertionColorChooser.setColor(tempProperties.getInsertionColor());
		delitionColorChooser.setColor(tempProperties.getDelitionColor());
		coverageColorChooser.setColor(tempProperties.getCoverageColor());
		nonspecificHighlightColorChooser.setColor(tempProperties.getNonSpecificColor());
		nonspecificHighlightCheckBox.setSelected(tempProperties.isNonSpecificHighlight());
		c0Button.setColor(tempProperties.get0Color());
		c1Button.setColor(tempProperties.get1Color());
		c2Button.setColor(tempProperties.get2Color());
		c3Button.setColor(tempProperties.get3Color());
	}

	/** Creates new form NewJFrame */
	public ProfileSettingsDialog() {
		initComponents();
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (!tempProperties.equals(Viewer.getPropertyHandler().getProfile(previousProfile))
					|| Integer.valueOf(columnDistanceTextField.getText()) != Viewer.getApplicationProperties().getReadDistance()) {
					saveConfirm();
				}
			}
		});
	}

	/**
	 * Query maximum read length from controller and show it
	 */
	public void refreshLongestRead() {
		longestReadTextField.setText(Viewer.getController().getMaxReadLength() + "");
	}

	public void loadProfileList() {
		listModel = new DefaultListModel();
		selectedIndex = 0;
		int i = 1;
		listModel.addElement(PropertyHandler.DEF_PROFILE_NAME);
		for (ProfileProperties o : Viewer.getPropertyHandler().getAllProfile()) {
			listModel.addElement(o.getProfileName());
			if (o.getProfileName().equals(Viewer.getActiveProfile().getProfileName())) {
				selectedIndex = i;
			}
			i++;
		}
		profileList.setModel(listModel);
		profileList.setSelectedIndex(selectedIndex);
		previousProfile = profileList.getSelectedValue().toString();
		tempProperties = Viewer.getPropertyHandler().getProfile(previousProfile).makeCopy(previousProfile);
		refreshProfile();
	}

	private void saveConfirm() {
		if (tempProperties.getProfileName().equals(PropertyHandler.DEF_PROFILE_NAME)) {
			Object[] options = {Viewer.getLabelResources().getString("yesButton"), Viewer.getLabelResources().getString("noButton")};
			JOptionPane.showOptionDialog(this, Viewer.getLabelResources().getString("saveAsConfirmButton"), "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		} else {
			Object[] options = {Viewer.getLabelResources().getString("yesButton"), Viewer.getLabelResources().getString("noButton")};
			int answer = JOptionPane.showOptionDialog(this, Viewer.getLabelResources().getString("saveConfirmButton"), "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (answer == JOptionPane.YES_OPTION) {
				saveAndActivateProfile();
			}
		}
		tempProperties = Viewer.getPropertyHandler().getProfile(profileList.getSelectedValue().toString()).makeCopy(profileList.getSelectedValue().toString());
		previousProfile = tempProperties.getProfileName();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jPanel1 = new javax.swing.JPanel();
                snp = new javax.swing.JLabel();
                deleteProfileButton = new javax.swing.JButton();
                resetButton = new javax.swing.JButton();
                saveButton = new javax.swing.JButton();
                readShowTypeLabel = new javax.swing.JLabel();
                readDisplayType = new javax.swing.JComboBox();
                sequenceShowModeLabel = new javax.swing.JLabel();
                sequnceShowMode = new javax.swing.JComboBox();
                showSnp = new javax.swing.JCheckBox();
                showReadError = new javax.swing.JCheckBox();
                showDirection = new javax.swing.JCheckBox();
                re = new javax.swing.JLabel();
                dbg = new javax.swing.JLabel();
                di = new javax.swing.JLabel();
                saveAsButton = new javax.swing.JButton();
                newButton = new javax.swing.JButton();
                c0 = new javax.swing.JLabel();
                c1 = new javax.swing.JLabel();
                c2 = new javax.swing.JLabel();
                c3 = new javax.swing.JLabel();
                gffFeatureTable = new javax.swing.JCheckBox();
                nucleotideLabel = new hu.astrid.viewer.gui.content.alignment.ReadLabel();
                colorLabel = new hu.astrid.viewer.gui.content.alignment.ReadLabel();
                coveragePanel = new javax.swing.JCheckBox();
                navPanel = new javax.swing.JCheckBox();
                positiveDirectionLabel = new javax.swing.JLabel();
                negativeDirectionLabel = new javax.swing.JLabel();
                longestReadLabel = new javax.swing.JLabel();
                longestReadTextField = new javax.swing.JTextField();
                columnDistanceLabel = new javax.swing.JLabel();
                columnDistanceTextField = new javax.swing.JTextField();
                insertionColorLabel = new javax.swing.JLabel();
                deletionColorLabel = new javax.swing.JLabel();
                manualSelectionColorLabel = new javax.swing.JLabel();
                autoSelectionColorLabel = new javax.swing.JLabel();
                nonspecificHighlightCheckBox = new javax.swing.JCheckBox();
                snpColorButton = new hu.astrid.viewer.gui.ColorChooserButton();
                readErrorColorButton = new hu.astrid.viewer.gui.ColorChooserButton();
                dbgColorButton = new hu.astrid.viewer.gui.ColorChooserButton();
                diColorButton = new hu.astrid.viewer.gui.ColorChooserButton();
                positiveDirectionButton = new hu.astrid.viewer.gui.ColorChooserButton();
                negativeDirectionButton = new hu.astrid.viewer.gui.ColorChooserButton();
                manualSelectionColorChooser = new hu.astrid.viewer.gui.ColorChooserButton();
                autoSelectionColorChooser = new hu.astrid.viewer.gui.ColorChooserButton();
                insertionColorChooser = new hu.astrid.viewer.gui.ColorChooserButton();
                delitionColorChooser = new hu.astrid.viewer.gui.ColorChooserButton();
                nonspecificHighlightColorChooser = new hu.astrid.viewer.gui.ColorChooserButton();
                coverageColorChooser = new hu.astrid.viewer.gui.ColorChooserButton();
                c0Button = new hu.astrid.viewer.gui.ColorChooserButton();
                c1Button = new hu.astrid.viewer.gui.ColorChooserButton();
                c2Button = new hu.astrid.viewer.gui.ColorChooserButton();
                c3Button = new hu.astrid.viewer.gui.ColorChooserButton();
                profilesLabel = new javax.swing.JLabel();
                profileSettingsLabel = new javax.swing.JLabel();
                jScrollPane1 = new javax.swing.JScrollPane();
                profileList = new javax.swing.JList();

                setModal(true);

                snp.setBackground(new java.awt.Color(0, 204, 102));
                snp.setForeground(new java.awt.Color(0, 204, 153));
                snp.setText(Viewer.getLabelResources().getString("SNP")); // NOI18N

                deleteProfileButton.setText(Viewer.getLabelResources().getString("deleteButton")); // NOI18N
                deleteProfileButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                deleteProfileButtonActionPerformed(evt);
                        }
                });

                resetButton.setText(Viewer.getLabelResources().getString("resetButton")); // NOI18N
                resetButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                resetButtonActionPerformed(evt);
                        }
                });

                saveButton.setText(Viewer.getLabelResources().getString("saveButton")); // NOI18N
                saveButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                saveButtonActionPerformed(evt);
                        }
                });

                readShowTypeLabel.setLabelFor(readDisplayType);
                readShowTypeLabel.setText(Viewer.getLabelResources().getString("readShowType")); // NOI18N

                readDisplayType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Short", "Long" }));
                readDisplayType.setSelectedIndex(Viewer.getActiveProfile().getReadDisplayType().ordinal());
                readDisplayType.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                readDisplayTypeActionPerformed(evt);
                        }
                });

                sequenceShowModeLabel.setLabelFor(sequnceShowMode);
                sequenceShowModeLabel.setText(Viewer.getLabelResources().getString("sequenceShowMode")); // NOI18N

                sequnceShowMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nucleotide", "Color", "Both" }));
                sequnceShowMode.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                sequnceShowModeActionPerformed(evt);
                        }
                });

                showSnp.setText(Viewer.getLabelResources().getString("showSnp")); // NOI18N
                showSnp.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                showSnpActionPerformed(evt);
                        }
                });

                showReadError.setText(Viewer.getLabelResources().getString("showReadError")); // NOI18N
                showReadError.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                showReadErrorActionPerformed(evt);
                        }
                });

                showDirection.setText(Viewer.getLabelResources().getString("showDirection")); // NOI18N
                showDirection.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                showDirectionActionPerformed(evt);
                        }
                });

                re.setBackground(new java.awt.Color(0, 204, 102));
                re.setForeground(new java.awt.Color(0, 204, 153));
                re.setText("RE");

                dbg.setBackground(new java.awt.Color(0, 204, 102));
                dbg.setForeground(new java.awt.Color(0, 204, 153));
                dbg.setText("DBG");

                di.setBackground(new java.awt.Color(0, 204, 102));
                di.setForeground(new java.awt.Color(0, 204, 153));
                di.setText("DI");

                saveAsButton.setText(Viewer.getLabelResources().getString("saveAsButton")); // NOI18N
                saveAsButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                saveAsButtonActionPerformed(evt);
                        }
                });

                newButton.setText(Viewer.getLabelResources().getString("newButton")); // NOI18N
                newButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                newButtonActionPerformed(evt);
                        }
                });

                c0.setLabelFor(c0Button);
                c0.setText("C0");

                c1.setLabelFor(c1Button);
                c1.setText("C1");

                c2.setLabelFor(c2Button);
                c2.setText("C2");

                c3.setLabelFor(c3Button);
                c3.setText("C3");

                gffFeatureTable.setText(Viewer.getLabelResources().getString("gffFeatureTable")); // NOI18N
                gffFeatureTable.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                gffFeatureTableActionPerformed(evt);
                        }
                });

                nucleotideLabel.setPreferredSize(new java.awt.Dimension(210, 26));

                colorLabel.setColorMode(true);
                colorLabel.setPreferredSize(new java.awt.Dimension(215, 26));

                coveragePanel.setText(Viewer.getLabelResources().getString("showCoveragePanel")); // NOI18N
                coveragePanel.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                coveragePanelActionPerformed(evt);
                        }
                });

                navPanel.setText(Viewer.getLabelResources().getString("navigationPanel")); // NOI18N
                navPanel.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                navPanelActionPerformed(evt);
                        }
                });

                positiveDirectionLabel.setBackground(java.awt.Color.green);
                positiveDirectionLabel.setLabelFor(positiveDirectionButton);
                positiveDirectionLabel.setText(Viewer.getLabelResources().getString("positiveDirection")); // NOI18N
                positiveDirectionLabel.setOpaque(true);

                negativeDirectionLabel.setBackground(java.awt.Color.red);
                negativeDirectionLabel.setLabelFor(negativeDirectionButton);
                negativeDirectionLabel.setText(Viewer.getLabelResources().getString("negativeDirection")); // NOI18N
                negativeDirectionLabel.setOpaque(true);

                longestReadLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                longestReadLabel.setLabelFor(longestReadTextField);
                longestReadLabel.setText(Viewer.getLabelResources().getString("profileDialogLongestRead")); // NOI18N

                longestReadTextField.setEditable(false);
                longestReadTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
                longestReadTextField.setText(Viewer.getController().getMaxReadLength()+"");
                longestReadTextField.setEnabled(false);

                columnDistanceLabel.setLabelFor(columnDistanceTextField);
                columnDistanceLabel.setText(Viewer.getLabelResources().getString("profileDialogColumnDistance")); // NOI18N
                columnDistanceLabel.setToolTipText(Viewer.getLabelResources().getString("profileDialogApplicationSetting")); // NOI18N

                columnDistanceTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
                columnDistanceTextField.setText(Viewer.getApplicationProperties().getReadDistance()+"");
                columnDistanceTextField.setToolTipText(Viewer.getLabelResources().getString("profileDialogApplicationSetting")); // NOI18N

                insertionColorLabel.setLabelFor(insertionColorChooser);
                insertionColorLabel.setText(Viewer.getLabelResources().getString("insertionColor")); // NOI18N

                deletionColorLabel.setLabelFor(delitionColorChooser);
                deletionColorLabel.setText(Viewer.getLabelResources().getString("deletionColor")); // NOI18N

                manualSelectionColorLabel.setLabelFor(manualSelectionColorChooser);
                manualSelectionColorLabel.setText(Viewer.getLabelResources().getString("manualSelectionColor")); // NOI18N

                autoSelectionColorLabel.setLabelFor(autoSelectionColorChooser);
                autoSelectionColorLabel.setText(Viewer.getLabelResources().getString("autoSelectionColor")); // NOI18N

                nonspecificHighlightCheckBox.setText(Viewer.getLabelResources().getString("nonspecificHighlight")); // NOI18N
                nonspecificHighlightCheckBox.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                nonspecificHighlightCheckBoxActionPerformed(evt);
                        }
                });

                snpColorButton.setText("");
                snpColorButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                snpColorButtonActionPerformed(evt);
                        }
                });

                readErrorColorButton.setText("");
                readErrorColorButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                readErrorColorButtonActionPerformed(evt);
                        }
                });

                dbgColorButton.setText("");
                dbgColorButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                dbgColorButtonActionPerformed(evt);
                        }
                });

                diColorButton.setText("");
                diColorButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                diColorButtonActionPerformed(evt);
                        }
                });

                positiveDirectionButton.setText("");
                positiveDirectionButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                positiveDirectionButtonActionPerformed(evt);
                        }
                });

                negativeDirectionButton.setText("");
                negativeDirectionButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                negativeDirectionButtonActionPerformed(evt);
                        }
                });

                manualSelectionColorChooser.setText("");
                manualSelectionColorChooser.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                manualSelectionColorChooserActionPerformed(evt);
                        }
                });

                autoSelectionColorChooser.setText("");
                autoSelectionColorChooser.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                autoSelectionColorChooserActionPerformed(evt);
                        }
                });

                insertionColorChooser.setText("");
                insertionColorChooser.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                insertionColorChooserActionPerformed(evt);
                        }
                });

                delitionColorChooser.setText("");
                delitionColorChooser.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                delitionColorChooserActionPerformed(evt);
                        }
                });

                nonspecificHighlightColorChooser.setText("");
                nonspecificHighlightColorChooser.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                nonspecificHighlightColorChooserActionPerformed(evt);
                        }
                });

                coverageColorChooser.setText("");
                coverageColorChooser.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                coverageColorChooserActionPerformed(evt);
                        }
                });

                c0Button.setText("");
                c0Button.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                c0ButtonActionPerformed(evt);
                        }
                });

                c1Button.setText("");
                c1Button.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                c1ButtonActionPerformed(evt);
                        }
                });

                c2Button.setText("");
                c2Button.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                c2ButtonActionPerformed(evt);
                        }
                });

                c3Button.setText("");
                c3Button.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                c3ButtonActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(gffFeatureTable)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(coveragePanel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(coverageColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(navPanel)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(nonspecificHighlightCheckBox)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(nonspecificHighlightColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(showDirection)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(dbg)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(dbgColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(di)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(diColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(sequenceShowModeLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sequnceShowMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(readShowTypeLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(readDisplayType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(longestReadLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(longestReadTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(columnDistanceLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(columnDistanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(showSnp)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(snp)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(snpColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(showReadError)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(re)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(readErrorColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addComponent(positiveDirectionLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(positiveDirectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(negativeDirectionLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(negativeDirectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(manualSelectionColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(manualSelectionColorLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(autoSelectionColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(autoSelectionColorLabel))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(c0)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(c0Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(c1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(c1Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(c2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(c2Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(c3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(c3Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(saveButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(resetButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(deleteProfileButton)
                                                .addGap(18, 18, 18)
                                                .addComponent(saveAsButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(newButton))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(colorLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(nucleotideLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(insertionColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(insertionColorLabel))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(delitionColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(deletionColorLabel)))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                jPanel1Layout.setVerticalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(41, 41, 41)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(readShowTypeLabel)
                                                        .addComponent(readDisplayType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(longestReadLabel)
                                                        .addComponent(longestReadTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(columnDistanceLabel)
                                                        .addComponent(columnDistanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(sequenceShowModeLabel)
                                                        .addComponent(sequnceShowMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(showSnp)
                                                        .addComponent(snp)
                                                        .addComponent(snpColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(showReadError)
                                                        .addComponent(re)
                                                        .addComponent(readErrorColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(showDirection)
                                                        .addComponent(dbg)
                                                        .addComponent(di)
                                                        .addComponent(dbgColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(diColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(positiveDirectionLabel)
                                                        .addComponent(negativeDirectionLabel)
                                                        .addComponent(positiveDirectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(negativeDirectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(3, 3, 3)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(manualSelectionColorLabel)
                                                                        .addComponent(manualSelectionColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(autoSelectionColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(autoSelectionColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(c0)
                                                                        .addComponent(c0Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(10, 10, 10))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(c3)
                                                                        .addComponent(c3Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(10, 10, 10))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(c1)
                                                                        .addComponent(c1Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(10, 10, 10)))
                                                .addGap(19, 19, 19))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(236, 236, 236)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(c2)
                                                        .addComponent(c2Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(nucleotideLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(insertionColorLabel)
                                                .addComponent(insertionColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(colorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(deletionColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(delitionColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(nonspecificHighlightCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nonspecificHighlightColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gffFeatureTable)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(coveragePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(coverageColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(navPanel)
                                .addGap(52, 52, 52)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(saveButton)
                                        .addComponent(resetButton)
                                        .addComponent(deleteProfileButton)
                                        .addComponent(saveAsButton)
                                        .addComponent(newButton))
                                .addContainerGap())
                );

                profilesLabel.setText(Viewer.getLabelResources().getString("profiles")); // NOI18N

                profileSettingsLabel.setText(Viewer.getLabelResources().getString("profileSettings")); // NOI18N

                profileList.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
                        public int getSize() { return strings.length; }
                        public Object getElementAt(int i) { return strings[i]; }
                });
                profileList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                                profileListValueChanged(evt);
                        }
                });
                jScrollPane1.setViewportView(profileList);

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(profilesLabel)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(profileSettingsLabel)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(140, Short.MAX_VALUE))
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(profilesLabel)
                                        .addComponent(profileSettingsLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                );

                pack();
        }// </editor-fold>//GEN-END:initComponents

        private void navPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_navPanelActionPerformed
		// TODO add your handling code here:
		tempProperties.setShowNavigationPanel(!tempProperties.isShowNavigationPanel());
        }//GEN-LAST:event_navPanelActionPerformed

	private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
		String s = JOptionPane.showInputDialog(this, Viewer.getLabelResources().getString("profileName"), Viewer.getLabelResources().getString("profileSaveAs"), JOptionPane.PLAIN_MESSAGE);
		if (s != null) {
			if (!s.equals("")) {
				if (Viewer.getPropertyHandler().getProfile(s) != null) {
					JOptionPane.showMessageDialog(this, Viewer.getLabelResources().getString("profileNameExists"));
					return;
				}
				// TODO gyökérbe bassza bele, kedves reviewzó így ez jó? :)
				// TODO a teljes profilokra vonatkozó kerseési helyet pontosítani és egységesíteni kellene
				ProfileProperties saveAsProfile = tempProperties.makeCopy(s);
				Viewer.getPropertyHandler().addNewProfile(saveAsProfile, new File(saveAsProfile.getProfileName() + ".profile"));
				Viewer.getPropertyHandler().setActiveProfile(saveAsProfile.getProfileName());
				Viewer.getMainWindow().makeProfilesMenu();
				tempProperties = Viewer.getPropertyHandler().getProfile(saveAsProfile.getProfileName()).makeCopy(saveAsProfile.getProfileName());
				previousProfile = tempProperties.getProfileName();
				listModel.addElement(saveAsProfile.getProfileName());
				profileList.setSelectedIndex(listModel.size() - 1);
				selectedIndex = listModel.size() - 1;
				refreshProfile();
				return;
			} else {
				JOptionPane.showMessageDialog(this, Viewer.getLabelResources().getString("profileNameEmpty"));
			}
		}
	}//GEN-LAST:event_saveAsButtonActionPerformed

	private void coveragePanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coveragePanelActionPerformed
		tempProperties.setShowCoveragePanel(!tempProperties.isShowCoveragePanel());
	}//GEN-LAST:event_coveragePanelActionPerformed

	private void showReadErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showReadErrorActionPerformed
		tempProperties.setShowReadErrors(!tempProperties.isShowReadErrors());
		colorLabel.refreshViewSettings(tempProperties);
		nucleotideLabel.refreshViewSettings(tempProperties);
	}//GEN-LAST:event_showReadErrorActionPerformed

	private void profileListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_profileListValueChanged
		if (profileList.getValueIsAdjusting()) {
			if (tempProperties != null && profileList.getSelectedValue() != null && Viewer.getPropertyHandler().getProfile(previousProfile) != null) {
				if (!tempProperties.equals(Viewer.getPropertyHandler().getProfile(previousProfile))) {
					saveConfirm();
				}
			}
			if (profileList.getSelectedValue() != null) {
				tempProperties = Viewer.getPropertyHandler().getProfile(profileList.getSelectedValue().toString()).makeCopy(profileList.getSelectedValue().toString());
				previousProfile = tempProperties.getProfileName();
			}
			refreshProfile();
		}
	}//GEN-LAST:event_profileListValueChanged

	private void readDisplayTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readDisplayTypeActionPerformed
		if (readDisplayType.getSelectedIndex() == 0) {
			tempProperties.setReadDisplayType(ReadDisplayType.SHORT);
		} else {
			tempProperties.setReadDisplayType(ReadDisplayType.LONG);
		}
	}//GEN-LAST:event_readDisplayTypeActionPerformed

	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
		if (profileList.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(this, Viewer.getLabelResources().getString("defaultSaveLabel"));
			return;
		}
		saveAndActivateProfile();
	}//GEN-LAST:event_saveButtonActionPerformed

	private void sequnceShowModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sequnceShowModeActionPerformed
		if (sequnceShowMode.getSelectedIndex() == 0) {
			tempProperties.setSequenceDisplayMode(SequenceDisplayMode.NUCLEOTIDE);
		} else if (sequnceShowMode.getSelectedIndex() == 1) {
			tempProperties.setSequenceDisplayMode(SequenceDisplayMode.COLOR);
		} else {
			tempProperties.setSequenceDisplayMode(SequenceDisplayMode.BOTH);
		}
	}//GEN-LAST:event_sequnceShowModeActionPerformed

	private void showSnpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSnpActionPerformed
		tempProperties.setShowSNPs(!tempProperties.isShowSNPs());
		colorLabel.refreshViewSettings(tempProperties);
		nucleotideLabel.refreshViewSettings(tempProperties);
	}//GEN-LAST:event_showSnpActionPerformed

	private void showDirectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDirectionActionPerformed
		tempProperties.setShowDirection(!tempProperties.isShowDirection());
		colorLabel.refreshViewSettings(tempProperties);
		nucleotideLabel.refreshViewSettings(tempProperties);
	}//GEN-LAST:event_showDirectionActionPerformed

	private void deleteProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProfileButtonActionPerformed
		Object[] options = {Viewer.getLabelResources().getString("yesButton"), Viewer.getLabelResources().getString("noButton")};
		int answer = JOptionPane.showOptionDialog(this, Viewer.getLabelResources().getString("areYouSure"), "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (answer == JOptionPane.YES_OPTION) {
			if (profileList.getSelectedIndex() == 0) {
				JOptionPane.showMessageDialog(this, Viewer.getLabelResources().getString("errorDeleteDefault"));
				return;
			}
			Viewer.getPropertyHandler().removeProfile(profileList.getSelectedValue().toString());
			listModel.remove(profileList.getSelectedIndex());
			profileList.setSelectedIndex(0);
			selectedIndex = 0;
			tempProperties = Viewer.getPropertyHandler().getProfile(profileList.getSelectedValue().toString()).makeCopy(profileList.getSelectedValue().toString());
			previousProfile = tempProperties.getProfileName();
			Viewer.getPropertyHandler().setActiveProfile(PropertyHandler.DEF_PROFILE_NAME);
			refreshProfile();
			Viewer.getMainWindow().makeProfilesMenu();
		}
	}//GEN-LAST:event_deleteProfileButtonActionPerformed

	private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
		tempProperties = Viewer.getPropertyHandler().getProfile(tempProperties.getProfileName()).makeCopy(tempProperties.getProfileName());
		refreshProfile();
	}//GEN-LAST:event_resetButtonActionPerformed

	private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
		String s = JOptionPane.showInputDialog(this, Viewer.getLabelResources().getString("profileName"), Viewer.getLabelResources().getString("profileSaveAs"), JOptionPane.PLAIN_MESSAGE);
		if (s != null) {
			if (!s.equals("")) {
				if (Viewer.getPropertyHandler().getProfile(s) != null) {
					JOptionPane.showMessageDialog(this, Viewer.getLabelResources().getString("profileNameExists"));
					return;
				}
				// TODO Ez az egy sor az eltérés lehetne refactorozni.
				ProfileProperties saveAsProfile = Viewer.getPropertyHandler().getDefaultProfile().makeCopy(s);
				Viewer.getPropertyHandler().addNewProfile(saveAsProfile, new File(saveAsProfile.getProfileName() + ".profile"));
				Viewer.getPropertyHandler().setActiveProfile(saveAsProfile.getProfileName());
				Viewer.getMainWindow().makeProfilesMenu();
				tempProperties = Viewer.getPropertyHandler().getProfile(saveAsProfile.getProfileName()).makeCopy(saveAsProfile.getProfileName());
				previousProfile = tempProperties.getProfileName();
				listModel.addElement(saveAsProfile.getProfileName());
				profileList.setSelectedIndex(listModel.size() - 1);
				selectedIndex = listModel.size() - 1;
				refreshProfile();
				return;
			} else {
				JOptionPane.showMessageDialog(this, Viewer.getLabelResources().getString("profileNameEmpty"));
			}
		}
	}//GEN-LAST:event_newButtonActionPerformed

	private void gffFeatureTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gffFeatureTableActionPerformed
		tempProperties.setShowFeatureTable(!tempProperties.isShowFeatureTable());
	}//GEN-LAST:event_gffFeatureTableActionPerformed

	private void nonspecificHighlightCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nonspecificHighlightCheckBoxActionPerformed
		tempProperties.setNonSpecificHighlight(nonspecificHighlightCheckBox.isSelected());
		colorLabel.refreshViewSettings(tempProperties);
		nucleotideLabel.refreshViewSettings(tempProperties);
	}//GEN-LAST:event_nonspecificHighlightCheckBoxActionPerformed

	private void snpColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snpColorButtonActionPerformed
		Color initialColor = snp.getForeground(); // Show the dialog; this
		// method does not return
		// until the dialog is
		// closed
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			snp.setForeground(newColor);
			snpColorButton.setColor(newColor);
			tempProperties.setSNPColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_snpColorButtonActionPerformed

	private void readErrorColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readErrorColorButtonActionPerformed
		Color initialColor = re.getForeground(); // Show the dialog; this method
		// does not return until the
		// dialog is closed
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			re.setForeground(newColor);
			readErrorColorButton.setColor(newColor);
			tempProperties.setReadErrorColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_readErrorColorButtonActionPerformed

	private void dbgColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbgColorButtonActionPerformed
		Color initialColor = dbg.getForeground(); // Show the dialog; this
		// method
		// does not return until the
		// dialog is closed
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			dbg.setForeground(newColor);
			dbgColorButton.setColor(newColor);
			tempProperties.setDirectionBackgroundColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_dbgColorButtonActionPerformed

	private void diColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diColorButtonActionPerformed
		Color initialColor = di.getForeground(); // Show the dialog; this method
		// does not return until the
		// dialog is closed
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			di.setForeground(newColor);
			diColorButton.setColor(newColor);
			tempProperties.setDirectionIndicatorColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_diColorButtonActionPerformed

	private void positiveDirectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positiveDirectionButtonActionPerformed
		Color initialColor = positiveDirectionLabel.getBackground();
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			positiveDirectionLabel.setBackground(newColor);
			positiveDirectionButton.setColor(newColor);
			tempProperties.setPositiveStrandColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_positiveDirectionButtonActionPerformed

	private void negativeDirectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_negativeDirectionButtonActionPerformed
		Color initialColor = negativeDirectionLabel.getBackground();
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			negativeDirectionLabel.setBackground(newColor);
			negativeDirectionButton.setColor(newColor);
			tempProperties.setNegativeStrandColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_negativeDirectionButtonActionPerformed

	private void manualSelectionColorChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualSelectionColorChooserActionPerformed
		Color newColor = JColorChooser.showDialog(this, "", manualSelectionColorChooser.getColor());
		if (newColor != null) {
			manualSelectionColorChooser.setColor(newColor);
			tempProperties.setManualSelectionColor(newColor);
		}
	}//GEN-LAST:event_manualSelectionColorChooserActionPerformed

	private void autoSelectionColorChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSelectionColorChooserActionPerformed
		Color newColor = JColorChooser.showDialog(this, "", autoSelectionColorChooser.getColor());
		if (newColor != null) {
			autoSelectionColorChooser.setColor(newColor);
			tempProperties.setAutoSelectionColor(newColor);
		}
	}//GEN-LAST:event_autoSelectionColorChooserActionPerformed

	private void insertionColorChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertionColorChooserActionPerformed
		Color newColor = JColorChooser.showDialog(this, "", insertionColorChooser.getColor());
		if (newColor != null) {
			insertionColorChooser.setColor(newColor);
			tempProperties.setInsertionColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_insertionColorChooserActionPerformed

	private void delitionColorChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delitionColorChooserActionPerformed
		Color newColor = JColorChooser.showDialog(this, "", delitionColorChooser.getColor());
		if (newColor != null) {
			delitionColorChooser.setColor(newColor);
//			Logger.getLogger(ProfileSettingsDialog.class.getName()).log(Level.INFO, "message");
			tempProperties.setDelitionColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_delitionColorChooserActionPerformed

	private void nonspecificHighlightColorChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nonspecificHighlightColorChooserActionPerformed
		Color newColor = JColorChooser.showDialog(this, "", nonspecificHighlightColorChooser.getColor());
		if (newColor != null) {
			nonspecificHighlightColorChooser.setColor(newColor);
			tempProperties.setNonSpecificColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_nonspecificHighlightColorChooserActionPerformed

	private void coverageColorChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coverageColorChooserActionPerformed
		Color newColor = JColorChooser.showDialog(this, "", coverageColorChooser.getColor());
		if (newColor != null) {
			coverageColorChooser.setColor(newColor);
			tempProperties.setCoverageColor(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_coverageColorChooserActionPerformed

	private void c0ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c0ButtonActionPerformed
		Color initialColor = c0.getForeground(); // Show the dialog; this method
		// does not return until the
		// dialog is closed
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			c0.setForeground(newColor);
			c0Button.setColor(newColor);
			tempProperties.set0Color(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_c0ButtonActionPerformed

	private void c1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c1ButtonActionPerformed
		Color initialColor = c1.getForeground(); // Show the dialog; this method
		// does not return until the
		// dialog is closed
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			c1.setForeground(newColor);
			c1Button.setColor(newColor);
			tempProperties.set1Color(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}	}//GEN-LAST:event_c1ButtonActionPerformed

	private void c2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c2ButtonActionPerformed
		Color initialColor = c2.getForeground(); // Show the dialog; this method
		// does not return until the
		// dialog is closed
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			c2.setForeground(newColor);
			c2Button.setColor(newColor);
			tempProperties.set2Color(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_c2ButtonActionPerformed

	private void c3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c3ButtonActionPerformed
		Color initialColor = c3.getForeground(); // Show the dialog; this method
		// does not return until the
		// dialog is closed
		Color newColor = JColorChooser.showDialog(this, "", initialColor);
		if (newColor != null) {
			c3.setForeground(newColor);
			c3Button.setColor(newColor);
			tempProperties.set3Color(newColor);
			colorLabel.refreshViewSettings(tempProperties);
			nucleotideLabel.refreshViewSettings(tempProperties);
		}
	}//GEN-LAST:event_c3ButtonActionPerformed

	private void saveAndActivateProfile() {
		tempProperties.compareForReload(Viewer.getActiveProfile());
		Viewer.getApplicationProperties().setReadDistance(Integer.valueOf(columnDistanceTextField.getText()));
		Viewer.getPropertyHandler().addNewProfile(tempProperties);
		Viewer.getPropertyHandler().saveProfile(profileList.getSelectedValue().toString());
		Viewer.getPropertyHandler().setActiveProfile(profileList.getSelectedValue().toString());
		tempProperties = Viewer.getPropertyHandler().getProfile(profileList.getSelectedValue().toString()).makeCopy(profileList.getSelectedValue().toString());
		previousProfile = tempProperties.getProfileName();
	}

	public void refreshI18N() {
//		this.c0Button.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.c1Button.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.c2Button.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.c3Button.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.snpColorButton.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.readErrorColorButton.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.diColorButton.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.dbgColorButton.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.positiveDirectionButton.setText(Viewer.getLabelResources().getString("changeButton"));
//		this.negativeDirectionButton.setText(Viewer.getLabelResources().getString("changeButton"));
		this.saveButton.setText(Viewer.getLabelResources().getString("saveButton"));
		this.saveAsButton.setText(Viewer.getLabelResources().getString("saveAsButton"));
		this.deleteProfileButton.setText(Viewer.getLabelResources().getString("deleteButton"));
		this.resetButton.setText(Viewer.getLabelResources().getString("resetButton"));
		this.newButton.setText(Viewer.getLabelResources().getString("newButton"));
		this.showSnp.setText(Viewer.getLabelResources().getString("showSnp"));
		this.showReadError.setText(Viewer.getLabelResources().getString("showReadError"));
		this.showDirection.setText(Viewer.getLabelResources().getString("showDirection"));
//		this.snpPanel.setText(Viewer.getLabelResources().getString("snpPanel"));
		this.navPanel.setText(Viewer.getLabelResources().getString("navigationPanel"));
//		this.indelPanel.setText(Viewer.getLabelResources().getString("indelPanel"));
		this.gffFeatureTable.setText(Viewer.getLabelResources().getString("gffFeatureTable"));
		this.profilesLabel.setText(Viewer.getLabelResources().getString("profiles"));
		this.profileSettingsLabel.setText(Viewer.getLabelResources().getString("profileSettings"));
		this.readShowTypeLabel.setText(Viewer.getLabelResources().getString("readShowType"));
		this.sequenceShowModeLabel.setText(Viewer.getLabelResources().getString("sequenceShowMode"));
		this.coveragePanel.setText(Viewer.getLabelResources().getString("showCoveragePanel"));
		this.positiveDirectionLabel.setText(Viewer.getLabelResources().getString("positiveDirection"));
		this.negativeDirectionLabel.setText(Viewer.getLabelResources().getString("negativeDirection"));
		this.longestReadLabel.setText(Viewer.getLabelResources().getString("profileDialogLongestRead"));
		this.columnDistanceLabel.setText(Viewer.getLabelResources().getString("profileDialogColumnDistance"));
		this.columnDistanceLabel.setToolTipText(Viewer.getLabelResources().getString("profileDialogApplicationSetting"));
		this.columnDistanceTextField.setToolTipText(Viewer.getLabelResources().getString("profileDialogApplicationSetting"));
		this.insertionColorLabel.setText(Viewer.getLabelResources().getString("insertionColor"));
		this.deletionColorLabel.setText(Viewer.getLabelResources().getString("deletionColor"));
		this.autoSelectionColorLabel.setText(Viewer.getLabelResources().getString("autoSelectionColor"));
		this.manualSelectionColorLabel.setText(Viewer.getLabelResources().getString("manualSelectionColor"));
		this.nonspecificHighlightCheckBox.setText(Viewer.getLabelResources().getString("nonspecificHighlight"));

		readDisplayType.setModel(new javax.swing.DefaultComboBoxModel(new String[]{Viewer.getLabelResources().getString("readShort"), Viewer.getLabelResources().getString("readLong")}));
		sequnceShowMode.setModel(new javax.swing.DefaultComboBoxModel(new String[]{Viewer.getLabelResources().getString("nucleotideMode"), Viewer.getLabelResources().getString("colourMode"), Viewer.getLabelResources().getString("bothMode")}));

		this.pack();
	}
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private hu.astrid.viewer.gui.ColorChooserButton autoSelectionColorChooser;
        private javax.swing.JLabel autoSelectionColorLabel;
        private javax.swing.JLabel c0;
        private hu.astrid.viewer.gui.ColorChooserButton c0Button;
        private javax.swing.JLabel c1;
        private hu.astrid.viewer.gui.ColorChooserButton c1Button;
        private javax.swing.JLabel c2;
        private hu.astrid.viewer.gui.ColorChooserButton c2Button;
        private javax.swing.JLabel c3;
        private hu.astrid.viewer.gui.ColorChooserButton c3Button;
        private hu.astrid.viewer.gui.content.alignment.ReadLabel colorLabel;
        private javax.swing.JLabel columnDistanceLabel;
        private javax.swing.JTextField columnDistanceTextField;
        private hu.astrid.viewer.gui.ColorChooserButton coverageColorChooser;
        private javax.swing.JCheckBox coveragePanel;
        private javax.swing.JLabel dbg;
        private hu.astrid.viewer.gui.ColorChooserButton dbgColorButton;
        private javax.swing.JButton deleteProfileButton;
        private javax.swing.JLabel deletionColorLabel;
        private hu.astrid.viewer.gui.ColorChooserButton delitionColorChooser;
        private javax.swing.JLabel di;
        private hu.astrid.viewer.gui.ColorChooserButton diColorButton;
        private javax.swing.JCheckBox gffFeatureTable;
        private hu.astrid.viewer.gui.ColorChooserButton insertionColorChooser;
        private javax.swing.JLabel insertionColorLabel;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JLabel longestReadLabel;
        private javax.swing.JTextField longestReadTextField;
        private hu.astrid.viewer.gui.ColorChooserButton manualSelectionColorChooser;
        private javax.swing.JLabel manualSelectionColorLabel;
        private javax.swing.JCheckBox navPanel;
        private hu.astrid.viewer.gui.ColorChooserButton negativeDirectionButton;
        private javax.swing.JLabel negativeDirectionLabel;
        private javax.swing.JButton newButton;
        private javax.swing.JCheckBox nonspecificHighlightCheckBox;
        private hu.astrid.viewer.gui.ColorChooserButton nonspecificHighlightColorChooser;
        private hu.astrid.viewer.gui.content.alignment.ReadLabel nucleotideLabel;
        private hu.astrid.viewer.gui.ColorChooserButton positiveDirectionButton;
        private javax.swing.JLabel positiveDirectionLabel;
        private javax.swing.JList profileList;
        private javax.swing.JLabel profileSettingsLabel;
        private javax.swing.JLabel profilesLabel;
        private javax.swing.JLabel re;
        private javax.swing.JComboBox readDisplayType;
        private hu.astrid.viewer.gui.ColorChooserButton readErrorColorButton;
        private javax.swing.JLabel readShowTypeLabel;
        private javax.swing.JButton resetButton;
        private javax.swing.JButton saveAsButton;
        private javax.swing.JButton saveButton;
        private javax.swing.JLabel sequenceShowModeLabel;
        private javax.swing.JComboBox sequnceShowMode;
        private javax.swing.JCheckBox showDirection;
        private javax.swing.JCheckBox showReadError;
        private javax.swing.JCheckBox showSnp;
        private javax.swing.JLabel snp;
        private hu.astrid.viewer.gui.ColorChooserButton snpColorButton;
        // End of variables declaration//GEN-END:variables
}
