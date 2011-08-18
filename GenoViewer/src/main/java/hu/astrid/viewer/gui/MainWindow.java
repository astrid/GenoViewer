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
 * MainWindow.java
 *
 * Created on 2010.03.03., 16:42:501
 */
package hu.astrid.viewer.gui;

import hu.astrid.mapping.exception.GffFileFormatException;
import hu.astrid.mapping.exception.MappingFileException;
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.gui.content.alignment.AlignmentPanel;
import hu.astrid.viewer.gui.content.annotation.AnnotationTypesDialog;
import hu.astrid.viewer.gui.help.About;
import hu.astrid.viewer.gui.workspace.WorkspaceWizard;
import hu.astrid.viewer.model.Project;
import hu.astrid.viewer.model.SelectionModel;
import hu.astrid.viewer.model.ViewerConsensusModel;
import hu.astrid.viewer.model.ViewerFastaModel;
import hu.astrid.viewer.model.ViewerReadModel;
import hu.astrid.viewer.model.WorkspaceModel;
import hu.astrid.viewer.properties.ProfileProperties;
import hu.astrid.viewer.properties.PropertyHandler;
import hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException;
import hu.astrid.viewer.reader.FastaRandomReader.InvalidFastaFileException;
import hu.astrid.viewer.util.FileTypes;
import hu.astrid.viewer.util.JFileFilters;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * Fasta megjelenítő alkalmazás nézet modulja.
 *
 * @author Szuni, Máté
 */
public class MainWindow extends JFrame implements AbstractView {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/** Szabványos swing elemeken megjelenő feliratok elérési útja */
	public static final String SWING_RESOURCES = "SwingStandardComponentsResources";
	/** Felületen megjelenő feliratok */
	ResourceBundle swingStandardComponentsResources;
	/** Collection for mapping quicklink menus with filetype as key */
	EnumMap<FileTypes, JMenu> quickLinkMenus = new EnumMap<FileTypes, JMenu>(FileTypes.class);
	/** Frame for holding options panel */
//	private WorkspacePanel workspacePanel = new WorkspacePanel();
	/** Default logger */
	private static final Logger logger = Logger.getLogger(MainWindow.class);

	/** Creates new form MainWindow */
	public MainWindow() {
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		swingStandardComponentsResources = swingStandardComponentsResources = ResourceBundle.getBundle(SWING_RESOURCES, Viewer.getViewerLocale());
		initComponents();
		workspaceInternalFrame.setVisible(false);
		profileSettingsDialog.setEnabled(false);
		try {
			setIconImage(javax.imageio.ImageIO.read(this.getClass().getResourceAsStream("/icon.png")));
		} catch (Exception ex) {
			logger.warn("Icon image not found: " + ex.getMessage());
		}
		quickLinkMenus.put(FileTypes.FASTA, quickLinksToFASTAFiles);
		quickLinkMenus.put(FileTypes.GFF, quickLinksToGFFFiles);
		quickLinkMenus.put(FileTypes.SAM, quickLinksToSAMFiles);
		quickLinkMenus.put(FileTypes.BAM, quickLinksToBAMFiles);
		quickLinkMenus.put(FileTypes.WORKSPACE, quickLinksToWorkspaces);
		makeProfilesMenu();

		for (final Locale locale : Viewer.getApplicationProperties().getSupportedLocales()) {
			JRadioButtonMenuItem languageMenuItem = new JRadioButtonMenuItem(
					ResourceBundle.getBundle(Viewer.LABEL_RESOURCES, locale).getString("localeName"),
					locale.equals(Viewer.getViewerLocale()));
			languageMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Viewer.getApplicationProperties().notifiyNewLocale(locale);
					refreshI18N();
				}
			});
			languageButtonGroup.add(languageMenuItem);
			menuLanguage.add(languageMenuItem);
		}
		refreshI18N();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {
                java.awt.GridBagConstraints gridBagConstraints;

        fastaFileChooser = new javax.swing.JFileChooser();
        readFileChooser = new javax.swing.JFileChooser();
        gffFileChooser = new javax.swing.JFileChooser();
        languageButtonGroup = new javax.swing.ButtonGroup();
        profileButtonGroup = new javax.swing.ButtonGroup();
        readInfoDialog = new javax.swing.JDialog(this);
        profileSettingsDialog = new hu.astrid.viewer.properties.ProfileSettingsDialog();
        mutationsDialog = new hu.astrid.viewer.gui.mutation.MutationsDialog();
        toolbarPanel = new javax.swing.JPanel();
        zoomToolBar = new hu.astrid.viewer.gui.ZoomToolBar();
        selectToolBar = new hu.astrid.viewer.gui.selection.SelectToolBar();
        genominerBanner = new javax.swing.JLabel();
        dashBoard = new hu.astrid.viewer.gui.DashBoard();
        statusBar = new hu.astrid.viewer.gui.StatusBar();
        navigationToolbar = new hu.astrid.viewer.gui.content.alignment.NavigationToolBar();
        workspaceInternalFrame = new javax.swing.JInternalFrame();
        workspacePanel = new hu.astrid.viewer.gui.workspace.WorkspacePanel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemOpenWorkspace = new javax.swing.JMenuItem();
        menuItemCreateWorkspace = new javax.swing.JMenuItem();
        menuItemOpenFasta = new javax.swing.JMenuItem();
        menuItemOpenGff = new javax.swing.JMenuItem();
        menuItemOpenSAM = new javax.swing.JMenuItem();
        menuItemOpenBAM = new javax.swing.JMenuItem();
        menuItemReadFromPosition = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        quickLinksToFASTAFiles = new javax.swing.JMenu();
        quickLinksToGFFFiles = new javax.swing.JMenu();
        quickLinksToSAMFiles = new javax.swing.JMenu();
        quickLinksToBAMFiles = new javax.swing.JMenu();
        quickLinksToWorkspaces = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuItemExit = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        defaultProfile = new javax.swing.JRadioButtonMenuItem();
        editProfile = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        menuItemAnnotationSettings = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuZoom = new javax.swing.JMenu();
        menuZoomIn = new javax.swing.JMenuItem();
        menuZoomOut = new javax.swing.JMenuItem();
        menuZoomDefault = new javax.swing.JMenuItem();
        menuLanguage = new javax.swing.JMenu();
        menuContigs = new javax.swing.JMenu();
        consensusMenu = new javax.swing.JMenu();
        consensusGenerateMenuItem = new javax.swing.JMenuItem();
        menuItemMutationTable = new javax.swing.JMenuItem();
        saveConsensusMenuItem = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();

                fastaFileChooser.setCurrentDirectory(Viewer.getPropertyHandler().getApplicationProperties().getLastOpenedDirectory(FileTypes.FASTA));
                fastaFileChooser.setFileFilter(JFileFilters.FASTA_FILTER);

                readFileChooser.setCurrentDirectory(Viewer.getPropertyHandler().getApplicationProperties().getLastOpenedDirectory(FileTypes.SAM));
                readFileChooser.setFileFilter(JFileFilters.SAM_FILTER);

                gffFileChooser.setCurrentDirectory(Viewer.getPropertyHandler().getApplicationProperties().getLastOpenedDirectory(FileTypes.GFF));
                gffFileChooser.setFileFilter(JFileFilters.GFF_FILTER);

                readInfoDialog.setTitle("");
                readInfoDialog.setModal(true);
                readInfoDialog.setName("readInfoDialog"); // NOI18N
                readInfoDialog.setResizable(false);
                readInfoDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent evt) {
                                readInfoDialogWindowClosing(evt);
                        }
                });

                javax.swing.GroupLayout readInfoDialogLayout = new javax.swing.GroupLayout(readInfoDialog.getContentPane());
                readInfoDialog.getContentPane().setLayout(readInfoDialogLayout);
                readInfoDialogLayout.setHorizontalGroup(
                        readInfoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
                );
                readInfoDialogLayout.setVerticalGroup(
                        readInfoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
                );

                mutationsDialog.setIconImage(null);
                mutationsDialog.setModal(true);

                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                setTitle(Viewer.getLabelResources().getString("applicationTitle")); // NOI18N
                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
                setMinimumSize(new java.awt.Dimension(530, 440));
                addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent evt) {
                                formWindowClosing(evt);
                        }
                });

                toolbarPanel.setLayout(new java.awt.GridBagLayout());

                zoomToolBar.setFloatable(false);
                zoomToolBar.setRollover(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                toolbarPanel.add(zoomToolBar, gridBagConstraints);

                selectToolBar.setFloatable(false);
                selectToolBar.setRollover(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                toolbarPanel.add(selectToolBar, gridBagConstraints);

                genominerBanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/genominerBanner.png"))); // NOI18N
                genominerBanner.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                genominerBanner.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                genominerBannerMouseClicked(evt);
                        }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
                gridBagConstraints.weightx = 1.0;
                toolbarPanel.add(genominerBanner, gridBagConstraints);

                dashBoard.setBorder(javax.swing.BorderFactory.createEtchedBorder());

                navigationToolbar.setBorder(null);

                workspaceInternalFrame.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                workspaceInternalFrame.setClosable(true);
                workspaceInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
                workspaceInternalFrame.setVisible(true);


                javax.swing.GroupLayout workspaceInternalFrameLayout = new javax.swing.GroupLayout(workspaceInternalFrame.getContentPane());
                workspaceInternalFrame.getContentPane().setLayout(workspaceInternalFrameLayout);
                workspaceInternalFrameLayout.setHorizontalGroup(
                        workspaceInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 208, Short.MAX_VALUE)
                        .addGroup(workspaceInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(workspacePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                );
                workspaceInternalFrameLayout.setVerticalGroup(
                        workspaceInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 712, Short.MAX_VALUE)
                        .addGroup(workspaceInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(workspacePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE))
                );

                menuBar.setFocusCycleRoot(true);

                menuFile.setMnemonic(Viewer.getLabelResources().getString("menuFileMnemonic").charAt(0));
                menuFile.setText(Viewer.getLabelResources().getString("menuFile")); // NOI18N
                menuFile.addMenuListener(new javax.swing.event.MenuListener() {
                        public void menuCanceled(javax.swing.event.MenuEvent evt) {
                        }
                        public void menuDeselected(javax.swing.event.MenuEvent evt) {
                        }
                        public void menuSelected(javax.swing.event.MenuEvent evt) {
                                menuFileMenuSelected(evt);
                        }
                });

                menuItemOpenWorkspace.setMnemonic(Viewer.getLabelResources().getString("workspaceOpenMenuItemMnemonic").charAt(0));
                menuItemOpenWorkspace.setText(Viewer.getLabelResources().getString("workspaceOpenMenuItem")); // NOI18N
                menuItemOpenWorkspace.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemOpenWorkspaceActionPerformed(evt);
                        }
                });
                menuFile.add(menuItemOpenWorkspace);

                menuItemCreateWorkspace.setMnemonic(Viewer.getLabelResources().getString("menuItemCreateWorkspaceMnemonic").charAt(0));
                menuItemCreateWorkspace.setText(Viewer.getLabelResources().getString("menuItemCreateWorkspace")); // NOI18N
                menuItemCreateWorkspace.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemCreateWorkspaceActionPerformed(evt);
                        }
                });
                menuFile.add(menuItemCreateWorkspace);

                menuItemOpenFasta.setMnemonic(Viewer.getLabelResources().getString("menuItemOpenFastaMnemonic").charAt(0));
                menuItemOpenFasta.setText(Viewer.getLabelResources().getString("menuItemOpenFasta")); // NOI18N
                menuItemOpenFasta.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemOpenFastaActionPerformed(evt);
                        }
                });
                menuFile.add(menuItemOpenFasta);

                menuItemOpenGff.setMnemonic(Viewer.getLabelResources().getString("menuItemOpenGFFMnemonic").charAt(0));
                menuItemOpenGff.setText(Viewer.getLabelResources().getString("menuItemOpenGFF")); // NOI18N
                menuItemOpenGff.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemOpenGffActionPerformed(evt);
                        }
                });
                menuFile.add(menuItemOpenGff);

                menuItemOpenSAM.setMnemonic(Viewer.getLabelResources().getString("menuItemOpenSAMMnemonic").charAt(0));
                menuItemOpenSAM.setText(Viewer.getLabelResources().getString("menuItemOpenSAM")); // NOI18N
                menuItemOpenSAM.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemOpenSAMActionPerformed(evt);
                        }
                });
                menuFile.add(menuItemOpenSAM);

                menuItemOpenBAM.setMnemonic(Viewer.getLabelResources().getString("menuItemOpenBAMMnemonic").charAt(0));
                menuItemOpenBAM.setText(Viewer.getLabelResources().getString("menuItemOpenBAM")); // NOI18N
                menuItemOpenBAM.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemOpenBAMActionPerformed(evt);
                        }
                });
                menuFile.add(menuItemOpenBAM);

                menuItemReadFromPosition.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
                menuItemReadFromPosition.setMnemonic(Viewer.getLabelResources().getString("menuItemReadFromPositionMnemonic").charAt(0));
                menuItemReadFromPosition.setText(Viewer.getLabelResources().getString("menuItemReadFromPosition")); // NOI18N
                menuItemReadFromPosition.setEnabled(false);
                menuItemReadFromPosition.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemReadFromPositionActionPerformed(evt);
                        }
                });
                menuFile.add(menuItemReadFromPosition);
                menuFile.add(jSeparator1);

                quickLinksToFASTAFiles.setText(Viewer.getLabelResources().getString("menuItemRecentFasta")); // NOI18N
                menuFile.add(quickLinksToFASTAFiles);

                quickLinksToGFFFiles.setText(Viewer.getLabelResources().getString("menuItemRecentGFF")); // NOI18N
                menuFile.add(quickLinksToGFFFiles);

                quickLinksToSAMFiles.setText(Viewer.getLabelResources().getString("menuItemRecentSAM")); // NOI18N
                menuFile.add(quickLinksToSAMFiles);

                quickLinksToBAMFiles.setText(Viewer.getLabelResources().getString("menuItemRecentBAM")); // NOI18N
                menuFile.add(quickLinksToBAMFiles);

                quickLinksToWorkspaces.setText(Viewer.getLabelResources().getString("menuItemRecentWorkspace")); // NOI18N
                menuFile.add(quickLinksToWorkspaces);
                menuFile.add(jSeparator5);

                menuItemExit.setMnemonic(Viewer.getLabelResources().getString("menuItemExitMnemonic").charAt(0));
                menuItemExit.setText(Viewer.getLabelResources().getString("menuItemExit")); // NOI18N
                menuItemExit.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemExitActionPerformed(evt);
                        }
                });
                menuFile.add(menuItemExit);

                menuBar.add(menuFile);

                menuView.setMnemonic(Viewer.getLabelResources().getString("menuViewMnemonic").charAt(0));
                menuView.setText(Viewer.getLabelResources().getString("menuView")); // NOI18N

                profileButtonGroup.add(defaultProfile);
                defaultProfile.setMnemonic(Viewer.getLabelResources().getString("menuItemDefaultProfileMnemonic").charAt(0));
                defaultProfile.setSelected(true);
                defaultProfile.setText(Viewer.getLabelResources().getString("menuItemDefaultProfile")); // NOI18N
                defaultProfile.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                defaultProfileActionPerformed(evt);
                        }
                });
                menuView.add(defaultProfile);

                editProfile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
                editProfile.setMnemonic(Viewer.getLabelResources().getString("menuItemEditProfileMnemonic").charAt(0));
                editProfile.setText(Viewer.getLabelResources().getString("menuItemEditProfile")); // NOI18N
                editProfile.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                editProfileActionPerformed(evt);
                        }
                });
                menuView.add(editProfile);
                menuView.add(jSeparator2);


        menuItemAnnotationSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        menuItemAnnotationSettings.setMnemonic(Viewer.getLabelResources().getString("menuItemAnnotationSettingsMnemonic").charAt(0));
        menuItemAnnotationSettings.setText(Viewer.getLabelResources().getString("menuItemAnnotationSettings")); // NOI18N
        menuItemAnnotationSettings.setEnabled(false);
        menuItemAnnotationSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAnnotationSettingsActionPerformed(evt);
            }
        });
        menuView.add(menuItemAnnotationSettings);
        menuView.add(jSeparator3);

        menuZoom.setMnemonic(Viewer.getLabelResources().getString("menuZoomMnemonic").charAt(0));
        menuZoom.setText(Viewer.getLabelResources().getString("menuZoom")); // NOI18N


                menuZoomIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, 0));
                menuZoomIn.setText(Viewer.getLabelResources().getString("menuItemZoomIn")); // NOI18N
                menuZoomIn.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                zoomInActionPerformed(evt);
                        }
                });
                menuZoom.add(menuZoomIn);

                menuZoomOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, 0));
                menuZoomOut.setText(Viewer.getLabelResources().getString("menuItemZoomOut")); // NOI18N
                menuZoomOut.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                zoomOutActionPerformed(evt);
                        }
                });
                menuZoom.add(menuZoomOut);

                menuZoomDefault.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MULTIPLY, 0));
                menuZoomDefault.setText(Viewer.getLabelResources().getString("menuItemZoomDefault")); // NOI18N
                menuZoomDefault.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                zoomDefaultActionPerformed(evt);
                        }
                });
                menuZoom.add(menuZoomDefault);

                menuView.add(menuZoom);

                menuLanguage.setMnemonic(Viewer.getLabelResources().getString("menuLanguageMnemonic").charAt(0));
                menuLanguage.setText(Viewer.getLabelResources().getString("menuLanguage")); // NOI18N
                menuLanguage.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                hungarianRadioButtonActionPerformed(evt);
                        }
                });
                menuView.add(menuLanguage);

                menuBar.add(menuView);

                menuContigs.setMnemonic(Viewer.getLabelResources().getString("menuContigsMnemonic").charAt(0));
                menuContigs.setText(Viewer.getLabelResources().getString("menuContigs")); // NOI18N
                menuContigs.setEnabled(false);
                menuBar.add(menuContigs);

                consensusMenu.setMnemonic(Viewer.getLabelResources().getString("consensusMenuMnemonic").charAt(0));
                consensusMenu.setText(Viewer.getLabelResources().getString("consensusMenu")); // NOI18N
                consensusMenu.setEnabled(false);

                consensusGenerateMenuItem.setMnemonic(Viewer.getLabelResources().getString("consensusGenerateMenuItemMnemonic").charAt(0));
                consensusGenerateMenuItem.setText(Viewer.getLabelResources().getString("consensusGenerateMenuItem")); // NOI18N
                consensusGenerateMenuItem.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                consensusGenerateMenuItemActionPerformed(evt);
                        }
                });
                consensusMenu.add(consensusGenerateMenuItem);

                menuItemMutationTable.setMnemonic(Viewer.getLabelResources().getString("menuItemMutationTableMnemonic").charAt(0));
                menuItemMutationTable.setText(Viewer.getLabelResources().getString("menuItemMutationTable")); // NOI18N
                menuItemMutationTable.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemMutationTableActionPerformed(evt);
                        }
                });
                consensusMenu.add(menuItemMutationTable);

                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("LabelResources_en_US"); // NOI18N
                saveConsensusMenuItem.setText(bundle.getString("menuItemSaveConsensus")); // NOI18N
                saveConsensusMenuItem.setEnabled(false);
                saveConsensusMenuItem.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                saveConsensusMenuItemActionPerformed(evt);
                        }
                });
                consensusMenu.add(saveConsensusMenuItem);

                menuBar.add(consensusMenu);

                menuHelp.setMnemonic(Viewer.getLabelResources().getString("menuHelpMnemonic").charAt(0));
                menuHelp.setText(Viewer.getLabelResources().getString("menuHelp")); // NOI18N

                menuItemAbout.setMnemonic(Viewer.getLabelResources().getString("menuItemAboutMnemonic").charAt(0));
                menuItemAbout.setText(Viewer.getLabelResources().getString("menuItemAbout")); // NOI18N
                menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemAboutActionPerformed(evt);
                        }
                });
                menuHelp.add(menuItemAbout);

                menuBar.add(menuHelp);

                setJMenuBar(menuBar);


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1257, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(navigationToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(toolbarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1257, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(workspaceInternalFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dashBoard, javax.swing.GroupLayout.DEFAULT_SIZE, 1039, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dashBoard, javax.swing.GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE)
                    .addComponent(workspaceInternalFrame))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(navigationToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );


                navigationToolbar.setVisible(false);

                pack();
        }// </editor-fold>//GEN-END:initComponents

    private void consensusGenerateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consensusGenerateMenuItemActionPerformed
		consensusGenerateMenuItem.setEnabled(false);
		new SwingWorker<Void, Void>() {

			Throwable throwable = null;

			@Override
			protected Void doInBackground() throws Exception {
				//Ez hosszú és így amíg fut, nem foglalja a SwingWorker threadet
				Executors.newSingleThreadExecutor().execute(new Runnable() {

					@Override
					public void run() {
						Viewer.getController().generateConsensus();
					}
				});
				return null;
			}

			@Override
			protected void done() {
				if (throwable != null) {
					logger.error(throwable.getMessage(), throwable);
				}
			}
		}.execute();
    }//GEN-LAST:event_consensusGenerateMenuItemActionPerformed

	private void defaultProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultProfileActionPerformed
		Viewer.getPropertyHandler().getDefaultProfile().compareForReload(Viewer.getActiveProfile());
		Viewer.getPropertyHandler().setActiveProfile(PropertyHandler.DEF_PROFILE_NAME);
	}//GEN-LAST:event_defaultProfileActionPerformed

	private void menuItemOpenSAMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenSAMActionPerformed
		readFileChooser.setFileFilter(JFileFilters.SAM_FILTER);
		readFileChooser.setCurrentDirectory(Viewer.getApplicationProperties().getLastOpenedDirectory(FileTypes.SAM));
		int returnVal = readFileChooser.showOpenDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			fileOpenAction(readFileChooser.getSelectedFile(), FileTypes.SAM);
		}
	}//GEN-LAST:event_menuItemOpenSAMActionPerformed

	/**
	 * Kilépés
	 *
	 * @param evt
	 */
	private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		exit();
	}//GEN-LAST:event_formWindowClosing

	/**
	 * Fájl adott contigjának adott pozíciótól való kiíratása. A pozíciókat
	 * 1-től számolja
	 *
	 * @param evt
	 */
	private void menuItemReadFromPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemReadFromPositionActionPerformed
		Integer position = null;
		String initMessage = "";
		do {
			Object input = JOptionPane.showInputDialog(this, Viewer.getLabelResources().getString("textboxLabelStartPosition"), Viewer.getLabelResources().getString("dialogTitleInputPosition"), JOptionPane.PLAIN_MESSAGE, null, null, initMessage);
			if (input == null) {
				return;
			}
			try {
				position = Integer.parseInt(input.toString());
				if (position < 0) {
					position = null;
					throw new NumberFormatException();
				}
			} catch (NumberFormatException ex) {
				logger.warn(ex.getMessage());
				String corruptValue = input.toString();
				if (!corruptValue.contains(Viewer.getLabelResources().getString("inputCorrupted"))) {
					initMessage = corruptValue + Viewer.getLabelResources().getString("inputCorrupted");
				} else {
					initMessage = corruptValue;
				}
			}
		} while (position == null);
		scrollToPosition(position);
	}//GEN-LAST:event_menuItemReadFromPositionActionPerformed

	/**
	 * Megnyitás esetén feldob egy fájlválasztó ablakot, ha kiválasztottuk a
	 * fájlt, megpróbálja megnyitni és indexelni, majd megjeleníti az elejét.
	 *
	 * @param evt
	 */
	private void menuItemOpenFastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenFastaActionPerformed
		int returnVal = fastaFileChooser.showOpenDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			fileOpenAction(fastaFileChooser.getSelectedFile(), FileTypes.FASTA);
		}
	}//GEN-LAST:event_menuItemOpenFastaActionPerformed

	/**
	 * Kilépés
	 *
	 * @param evt
	 */
	private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
		exit();
	}//GEN-LAST:event_menuItemExitActionPerformed

	private void menuItemOpenBAMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenBAMActionPerformed
		readFileChooser.setFileFilter(JFileFilters.BAM_FILTER);
		readFileChooser.setCurrentDirectory(Viewer.getApplicationProperties().getLastOpenedDirectory(FileTypes.BAM));
		int returnVal = readFileChooser.showOpenDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			fileOpenAction(readFileChooser.getSelectedFile(), FileTypes.BAM);
		}
	}//GEN-LAST:event_menuItemOpenBAMActionPerformed

	private void hungarianRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hungarianRadioButtonActionPerformed
		Viewer.getApplicationProperties().notifiyNewLocale(new Locale("hu", "HU"));
		swingStandardComponentsResources = ResourceBundle.getBundle(SWING_RESOURCES, Viewer.getViewerLocale());
		refreshI18N();
	}//GEN-LAST:event_hungarianRadioButtonActionPerformed

	public void zoomInActionPerformed(java.awt.event.ActionEvent evt) {
		dashBoard.zoomIn();
	}

	public void zoomOutActionPerformed(java.awt.event.ActionEvent evt) {
		dashBoard.zoomOut();
	}

	public void zoomDefaultActionPerformed(java.awt.event.ActionEvent evt) {
		dashBoard.zoomDefault();
	}

	private void menuFileMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_menuFileMenuSelected
		refreshQuickLinks();
	}//GEN-LAST:event_menuFileMenuSelected

	private void menuItemOpenGffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenGffActionPerformed
		int returnVal = gffFileChooser.showOpenDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			fileOpenAction(gffFileChooser.getSelectedFile(), FileTypes.GFF);
		}
	}//GEN-LAST:event_menuItemOpenGffActionPerformed

	private void menuItemOpenWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenWorkspaceActionPerformed

		JFileChooser directoryChooser = new JFileChooser();
		directoryChooser.setCurrentDirectory(Viewer.getApplicationProperties().getLastOpenedDirectory(FileTypes.WORKSPACE));
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int option = directoryChooser.showOpenDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			File workspace = directoryChooser.getSelectedFile();
			fileOpenAction(workspace, FileTypes.WORKSPACE);
		}
	}//GEN-LAST:event_menuItemOpenWorkspaceActionPerformed

	private void editProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editProfileActionPerformed
		profileSettingsDialog.setEnabled(true);
		profileSettingsDialog.loadProfileList();
		profileSettingsDialog.refreshLongestRead();
		profileSettingsDialog.setLocationRelativeTo(this);
		profileSettingsDialog.setVisible(true);
	}//GEN-LAST:event_editProfileActionPerformed

	private void menuItemCreateWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCreateWorkspaceActionPerformed
		// TODO add your handling code here:
		WorkspaceWizard workspaceWizard = new WorkspaceWizard();
		workspaceWizard.setLocationRelativeTo(this);
		workspaceWizard.setVisible(true);
	}//GEN-LAST:event_menuItemCreateWorkspaceActionPerformed

	private void menuItemMutationTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMutationTableActionPerformed
		new SwingWorker<Void, Void>() {

			Throwable th = null;

			@Override
			protected Void doInBackground() throws Exception {
				Viewer.getController().setModelProperty(ViewerReadModel.MUTATIONS_LOAD, true);
				return null;
			}

			@Override
			protected void done() {
				if (th != null) {
					logger.error(th.getMessage(), th);
				}
			}
		}.execute();

	}//GEN-LAST:event_menuItemMutationTableActionPerformed

	private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
		About about = new About(this, true);
		about.setEnabled(true);
		about.setLocationRelativeTo(this);
		about.setVisible(true);
	}//GEN-LAST:event_menuItemAboutActionPerformed

	private void genominerBannerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genominerBannerMouseClicked
		try {
			open(new URI("http://ngsanalysis.com/"));
		} catch (URISyntaxException ex) {
			Logger.getLogger(About.class).error(ex.getMessage(), ex);
		}
	}//GEN-LAST:event_genominerBannerMouseClicked

        private void saveConsensusMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConsensusMenuItemActionPerformed

			String consensusContigName = (Viewer.getController().getReadFileName().split("\\."))[0];
			String fileName = "";
			JFileChooser directoryChooser = new JFileChooser();
			directoryChooser.setFileFilter(JFileFilters.FASTA_FILTER);
			directoryChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			directoryChooser.setSelectedFile(new File(consensusContigName + "_consensus.fasta"));
			if (directoryChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				fileName = checkFileNameExtension(directoryChooser.getSelectedFile().getName());
				logger.debug("fileName: " + fileName);
				if (!fileName.equals("")) {
					File fileForSave = new File(directoryChooser.getCurrentDirectory() + System.getProperty("file.separator") + fileName);
					if (fileForSave.exists()) {
						if (JOptionPane.showConfirmDialog(this, fileForSave.getName() + Viewer.getLabelResources().getString("overwriteFile"), "Overwrite file", JOptionPane.OK_OPTION) == JOptionPane.OK_OPTION) {
							saveConsensus(consensusContigName, fileForSave);
						}
					} else {
						saveConsensus(consensusContigName, fileForSave);
					}
				}
			} else {
				return;
			}
        }//GEN-LAST:event_saveConsensusMenuItemActionPerformed


	private void readInfoDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_readInfoDialogWindowClosing
		dashBoard.setEnabled(true);
	}//GEN-LAST:event_readInfoDialogWindowClosing


		private void menuItemAnnotationSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAnnotationSettingsActionPerformed
			AnnotationTypesDialog dialog = new AnnotationTypesDialog(Viewer.getMainWindow(), true);
			dialog.customize(Viewer.getController().getAnnotationTypes(), Viewer.getController().getAnnotationsVisibility(), Viewer.getController().getAnnotationGroups());
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
		}//GEN-LAST:event_menuItemAnnotationSettingsActionPerformed
	/**
	 * Check the extension of the file
	 * @param fileName
	 * @return
	 */
	private String checkFileNameExtension(String fileName) {
		logger.debug("fileNameCheck: " + fileName);
		if (!fileName.endsWith(".fasta")) {
			return fileName + ".fasta";
		} else {
			return fileName;
		}
	}

	/**
	 *
	 * @param consensusContigName contig name quick tip
	 * @param file path to save
	 */
	private void saveConsensus(String consensusContigName, File file) {
		String contigName = "";
		while (contigName.equals("")) {
			contigName = (String) JOptionPane.showInputDialog(this, Viewer.getLabelResources().getString("consensusSaveDialogLabel"), consensusContigName + " consensus");
			if (contigName != null) {
				if (!contigName.equals("")) {
					Viewer.getController().saveConsensus(contigName, file);
				}
			} else {
				return;
			}
		}
	}

	private void refreshI18N() {
		changeWindowTitle(Viewer.getController().getFastaFileName(), Viewer.getController().getGffFileName(), Viewer.getController().getReadFileName());
		// File menu
		menuFile.setText(Viewer.getLabelResources().getString("menuFile"));
		menuFile.setMnemonic(Viewer.getLabelResources().getString("menuFileMnemonic").charAt(0));
		menuItemOpenWorkspace.setText(Viewer.getLabelResources().getString("workspaceOpenMenuItem"));
		menuItemOpenWorkspace.setMnemonic(Viewer.getLabelResources().getString("workspaceOpenMenuItemMnemonic").charAt(0));
		menuItemCreateWorkspace.setText(Viewer.getLabelResources().getString("menuItemCreateWorkspace"));
		menuItemCreateWorkspace.setMnemonic(Viewer.getLabelResources().getString("menuItemCreateWorkspaceMnemonic").charAt(0));
		menuItemOpenFasta.setText(Viewer.getLabelResources().getString("menuItemOpenFasta"));
		menuItemOpenFasta.setMnemonic(Viewer.getLabelResources().getString("menuItemOpenFastaMnemonic").charAt(0));
		menuItemOpenGff.setText(Viewer.getLabelResources().getString("menuItemOpenGFF"));
		menuItemOpenGff.setMnemonic(Viewer.getLabelResources().getString("menuItemOpenGFFMnemonic").charAt(0));
		menuItemOpenSAM.setText(Viewer.getLabelResources().getString("menuItemOpenSAM"));
		menuItemOpenSAM.setMnemonic(Viewer.getLabelResources().getString("menuItemOpenSAMMnemonic").charAt(0));
		menuItemOpenBAM.setText(Viewer.getLabelResources().getString("menuItemOpenBAM"));
		menuItemOpenBAM.setMnemonic(Viewer.getLabelResources().getString("menuItemOpenBAMMnemonic").charAt(0));
		menuItemReadFromPosition.setText(Viewer.getLabelResources().getString("menuItemReadFromPosition"));
		quickLinksToFASTAFiles.setText(Viewer.getLabelResources().getString("menuItemRecentFasta")); // NOI18N
		quickLinksToGFFFiles.setText(Viewer.getLabelResources().getString("menuItemRecentGFF")); // NOI18N
		quickLinksToSAMFiles.setText(Viewer.getLabelResources().getString("menuItemRecentSAM")); // NOI18N
		quickLinksToBAMFiles.setText(Viewer.getLabelResources().getString("menuItemRecentBAM")); // NOI18N
		quickLinksToWorkspaces.setText(Viewer.getLabelResources().getString("menuItemRecentWorkspace")); // NOI18N
		menuItemExit.setText(Viewer.getLabelResources().getString("menuItemExit"));
		menuItemExit.setMnemonic(Viewer.getLabelResources().getString("menuItemExitMnemonic").charAt(0));
		menuContigs.setText(Viewer.getLabelResources().getString("menuContigs"));
		menuContigs.setMnemonic(Viewer.getLabelResources().getString("menuContigsMnemonic").charAt(0));

		// View menu
		menuView.setText(Viewer.getLabelResources().getString("menuView"));
		menuView.setMnemonic(Viewer.getLabelResources().getString("menuViewMnemonic").charAt(0));
		menuItemAnnotationSettings.setText(Viewer.getLabelResources().getString("menuItemAnnotationSettings"));
		menuItemAnnotationSettings.setMnemonic(Viewer.getLabelResources().getString("menuItemAnnotationSettingsMnemonic").charAt(0));
		// Language menu
		menuLanguage.setText(Viewer.getLabelResources().getString("menuLanguage"));
		menuLanguage.setMnemonic(Viewer.getLabelResources().getString("menuLanguageMnemonic").charAt(0));
		// Zoom menu
		menuZoom.setText(Viewer.getLabelResources().getString("menuZoom"));
		menuZoom.setMnemonic(Viewer.getLabelResources().getString("menuZoomMnemonic").charAt(0));
		menuZoomIn.setText(Viewer.getLabelResources().getString("menuItemZoomIn"));
		menuZoomOut.setText(Viewer.getLabelResources().getString("menuItemZoomOut"));
		menuZoomDefault.setText(Viewer.getLabelResources().getString("menuItemZoomDefault"));
		// Profiles menu
		defaultProfile.setText(Viewer.getLabelResources().getString("menuItemDefaultProfile"));
		defaultProfile.setMnemonic(Viewer.getLabelResources().getString("menuItemDefaultProfileMnemonic").charAt(0));
		editProfile.setText(Viewer.getLabelResources().getString("menuItemEditProfile"));
		editProfile.setMnemonic(Viewer.getLabelResources().getString("menuItemEditProfileMnemonic").charAt(0));

		// Consesnus menu
		consensusMenu.setText(Viewer.getLabelResources().getString("consensusMenu"));
		consensusMenu.setMnemonic(Viewer.getLabelResources().getString("consensusMenuMnemonic").charAt(0));
		consensusGenerateMenuItem.setText(Viewer.getLabelResources().getString("consensusGenerateMenuItem"));
		consensusGenerateMenuItem.setMnemonic(Viewer.getLabelResources().getString("consensusGenerateMenuItemMnemonic").charAt(0));
		menuItemMutationTable.setText(Viewer.getLabelResources().getString("menuItemMutationTable"));
		menuItemMutationTable.setMnemonic(Viewer.getLabelResources().getString("menuItemMutationTableMnemonic").charAt(0));

		// Help menu
		menuHelp.setText(Viewer.getLabelResources().getString("menuHelp"));
		menuHelp.setMnemonic(Viewer.getLabelResources().getString("menuHelpMnemonic").charAt(0));
		menuItemAbout.setText(Viewer.getLabelResources().getString("menuItemAbout"));
		menuItemAbout.setMnemonic(Viewer.getLabelResources().getString("menuItemAboutMnemonic").charAt(0));

		Enumeration<String> swingStandardComponentsResourcesKeys = swingStandardComponentsResources.getKeys();
		while (swingStandardComponentsResourcesKeys.hasMoreElements()) {
			String key = swingStandardComponentsResourcesKeys.nextElement();
			UIManager.put(key, swingStandardComponentsResources.getString(key));
		}
		fastaFileChooser.updateUI();
		fastaFileChooser.setFileFilter(JFileFilters.FASTA_FILTER);
		readFileChooser.updateUI();
		gffFileChooser.updateUI();
		gffFileChooser.setFileFilter(JFileFilters.GFF_FILTER);
		workspaceInternalFrame.updateUI();

		workspacePanel.refreshI18N();
		navigationToolbar.refreshI18N();
		profileSettingsDialog.refreshI18N();
		mutationsDialog.refreshI18N();

		zoomToolBar.refreshI18N();
		selectToolBar.refreshI18N();

		mutationsDialog.refreshI18N();

		for (ContentPanel panel : dashBoard.getPanels()) {
			if (panel instanceof AlignmentPanel) {
				((AlignmentPanel) panel).getReadInfoPanel().refreshLabels();
			}
		}
	}

	public void setReadInfoDialog(JPanel jPanel, String title) {
		readInfoDialog.setContentPane(jPanel);
		readInfoDialog.setTitle(title);
		readInfoDialog.pack();
		if (!readInfoDialog.isVisible()) {
			readInfoDialog.setLocationRelativeTo(this);
			readInfoDialog.setVisible(true);
		}
	}

	/**
	 * Modell állapotának megváltozásakor hívódik meg.
	 *
	 * @param evt
	 *            tulajdonság változásról értesítő esemény
	 */
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewerController.VIEWER_PROFILE_PROPERTY)) {
			ProfileProperties activeProfile = Viewer.getActiveProfile();
			for (int i = 0; i < menuView.getItemCount(); ++i) {
				if (menuView.getItem(i) instanceof JRadioButtonMenuItem && menuView.getItem(i).getText().equals(activeProfile.getProfileName())) {
					menuView.getItem(i).setSelected(true);
					break;
				}
			}
		} else if (evt.getPropertyName().equals(WorkspaceModel.WORKSPACE_PROJECT_CHANGE)) {
			dashBoard.clearPaintings();
			@SuppressWarnings("unchecked")
			Map<FileTypes, File> files = (Map<FileTypes, File>) evt.getNewValue();
			for (FileTypes key : files.keySet()) {
				if (files.get(key) != null) {
					fileOpenAction(files.get(key), key);
				}
			}
		} else if (evt.getPropertyName().equals(WorkspaceModel.PROJECT_NEW_FILE)) {
			Integer projectIndex = new Integer(evt.getOldValue().toString());
			if (projectIndex > -1) {
				@SuppressWarnings("unchecked")
				EnumMap<FileTypes, File> files = ((List<Project>) evt.getNewValue()).get(projectIndex).getFiles();
				for (FileTypes type : files.keySet()) {
					switch (type) {
						case FASTA: {
							if (!Viewer.getController().isFastaFileOpened()) {
								fileOpenAction(files.get(type), type);
							}
							break;
						}
						case SAM:
						case BAM: {
							if (!Viewer.getController().isReadsLoaded()) {
								fileOpenAction(files.get(type), type);
							}
							break;
						}
						case GFF: {
							if (!Viewer.getController().isAnnotationsLoaded()) {
								fileOpenAction(files.get(type), type);
							}
							break;
						}
						default:
							throw new AssertionError();
					}
				}
			}
		} else if (evt.getPropertyName().equals(ViewerReadModel.MUTATIONS_LOADING_STATE)) {
			menuItemMutationTable.setEnabled(!(Boolean) evt.getNewValue());
		} else {
			if (evt.getPropertyName().equals(WorkspaceModel.WORKSPACE_LOAD)) {
				Viewer.getController().closeAllFiles();
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
				workspaceInternalFrame.setVisible(true);
				workspaceInternalFrame.setTitle(Viewer.getController().getWorkspaceName());
			}
			// Fájl megnyitása és lezárása esetén a menüelemek elérhetőségét
			// beállítja és a contigok id-jait felölti
			if (evt.getPropertyName().equals(ViewerFastaModel.REFERENCE_LOAD)) {
				makeContigMenu(Viewer.getController().getContigNames(), Viewer.getController().getRefNames());
			} // Read file opened or closed
			else if (evt.getPropertyName().equals(ViewerReadModel.ALIGNMENT_LOAD)) {
				makeContigMenu(Viewer.getController().getContigNames(), Viewer.getController().getRefNames());
			}// Open selected project's files

			// TODO minden egyes propertynél lefut
			enableMenus(Viewer.getController().isFastaFileOpened(), Viewer.getController().isReadsLoaded(), Viewer.getController().isAnnotationsLoaded());
			changeWindowTitle(Viewer.getController().getFastaFileName(), Viewer.getController().getGffFileName(), Viewer.getController().getReadFileName());
		}
		if (evt.getPropertyName().equals(ViewerConsensusModel.CONSENSUS_LOAD)) {
			saveConsensusMenuItem.setEnabled((Boolean) evt.getNewValue());
		}
		if (evt.getPropertyName().equals(ViewerController.CONSENSUS_SAVE)) {
			this.setEnabled((Boolean) evt.getNewValue());
		}
		if (evt.getPropertyName().equals(SelectionModel.CLIPBOARD_BOUNDARY)) {
			JOptionPane.showMessageDialog(this, Viewer.getLabelResources().getString("clipboardBoundaryLimit"));
		}
		this.refreshVisiblePanels();
	}

	/**
	 * Sets the read from position, contigs and show mode menus enabled or
	 * disabled
	 *
	 * @param isFastaFileOpened
	 * @param isReadsLoaded
	 * @param isAnnotationsLoaded
	 */
	private void enableMenus(final boolean isFastaFileOpened, final boolean isReadsLoaded, final boolean isAnnotationsLoaded) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				menuItemReadFromPosition.setEnabled(isFastaFileOpened || isReadsLoaded || isAnnotationsLoaded);
				menuContigs.setEnabled(isFastaFileOpened || isReadsLoaded);
				consensusMenu.setEnabled(isReadsLoaded);
				consensusGenerateMenuItem.setEnabled(isReadsLoaded);
				menuItemMutationTable.setEnabled(isReadsLoaded);
				menuItemAnnotationSettings.setEnabled(isAnnotationsLoaded);
			}
		});
	}

	/**
	 * Fills the contig menu with fasta and alignment contig name with a
	 * separator between the two types
	 *
	 * @param fastaContigs
	 *            names of contigs in fasta file
	 * @param readRefNames
	 *            names of references in alignment file
	 */
	private void makeContigMenu(final List<String> fastaContigs, final List<String> readRefNames) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				menuContigs.removeAll();
				// Fasta contigs
				int i = 0;
				for (String contigId : fastaContigs) {
					final int j = i;
					JMenuItem contigMenuItem = new JMenuItem(contigId);
					// Kiválasztás esetén az első pozíciótól megjeleníti a
					// contigot
					contigMenuItem.addActionListener(new java.awt.event.ActionListener() {

						@Override
						public void actionPerformed(java.awt.event.ActionEvent evt) {
							Viewer.getController().setModelProperty(ViewerFastaModel.ACT_CONTIG_INDEX, j);
						}
					});
					i++;
					menuContigs.add(contigMenuItem);
					if (j >= 20) {
						break;
					}
				}
				menuContigs.add(new JSeparator());
				// Read references
				i = 0;
				for (String contigId : readRefNames) {
					final int j = i;
					JMenuItem contigMenuItem = new JMenuItem(contigId);
					// Kiválasztás esetén az első pozíciótól megjeleníti a readeket
					contigMenuItem.addActionListener(new java.awt.event.ActionListener() {

						@Override
						public void actionPerformed(java.awt.event.ActionEvent evt) {
							Viewer.getController().setModelProperty(ViewerReadModel.ACT_ALIGNMENT_REF_INDEX, j);
						}
					});
					i++;
					menuContigs.add(contigMenuItem);
					if (j >= 20) {
						break;
					}
				}
				if (menuContigs.getMenuComponentCount() == 1) {
					menuContigs.setEnabled(false);
				}
			}
		});
	}

	/**
	 * Fill profiles menu with the useable profiles. Remove every item except
	 * default, edit and the separator, and add the items for actual profiles
	 */
	public void makeProfilesMenu() {
		ProfileProperties activeProfile = Viewer.getActiveProfile();

		int i = 1;
		while (i < menuView.getMenuComponentCount() - 6) {
			menuView.remove(i);
		}

		i = 1;
		for (final ProfileProperties profile : Viewer.getPropertyHandler().getAllProfile()) {
			JRadioButtonMenuItem profileMenuItem = new JRadioButtonMenuItem(profile.toString());
			profileButtonGroup.add(profileMenuItem);
			menuView.add(profileMenuItem, i++);
			if (activeProfile.getProfileName().equals(profile.getProfileName())) {
				profileMenuItem.setSelected(true);
			}
			profileMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					profile.compareForReload(Viewer.getActiveProfile());
					Viewer.getPropertyHandler().setActiveProfile(profile.toString());
				}
			});
		}
	}

	/**
	 * Scroll to be the given position in the middle of the screen.
	 *
	 * @param position
	 *            , started from 0
	 */
	public void scrollToPosition(Integer position) {
		ResizeableScrollPanel.scrollPointerPosition = position - 1;
		dashBoard.scrollToPosition(position.intValue());
	}

	/**
	 * Shows the opened file names after the application title
	 *
	 * @param fastaFileName
	 *            filename or {@code null}
	 * @param gffFileName
	 *            filename or {@code null}
	 * @param readFileName
	 *            filename or {@code null}
	 */
	private void changeWindowTitle(final String fastaFileName, final String gffFileName, final String readFileName) {
		final String title = Viewer.getLabelResources().getString("applicationTitle");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				MainWindow.this.setTitle(title + (fastaFileName != null ? " - " + (fastaFileName) : "") + (gffFileName != null ? " - " + (gffFileName) : "") + (readFileName != null ? " - " + (readFileName) : ""));
			}
		});
	}

	/**
	 * Kilépés a programból. Lezárja a megnyitott fájlt is.
	 */
	protected void exit() {
		this.setVisible(false);
		readInfoDialog.dispose();
		profileSettingsDialog.dispose();
		dashBoard.gffPanel.featureNavigationDialog.dispose();
		mutationsDialog.dispose();
		this.dispose();
		System.exit(0);
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem consensusGenerateMenuItem;
    private javax.swing.JMenu consensusMenu;
    private hu.astrid.viewer.gui.DashBoard dashBoard;
    private javax.swing.JRadioButtonMenuItem defaultProfile;
    private javax.swing.JMenuItem editProfile;
    private javax.swing.JFileChooser fastaFileChooser;
    private javax.swing.JLabel genominerBanner;
    private javax.swing.JFileChooser gffFileChooser;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.ButtonGroup languageButtonGroup;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuContigs;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemAnnotationSettings;
    private javax.swing.JMenuItem menuItemCreateWorkspace;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemMutationTable;
    private javax.swing.JMenuItem menuItemOpenBAM;
    private javax.swing.JMenuItem menuItemOpenFasta;
    private javax.swing.JMenuItem menuItemOpenGff;
    private javax.swing.JMenuItem menuItemOpenSAM;
    private javax.swing.JMenuItem menuItemOpenWorkspace;
    private javax.swing.JMenuItem menuItemReadFromPosition;
    private javax.swing.JMenu menuLanguage;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenu menuZoom;
    private javax.swing.JMenuItem menuZoomDefault;
    private javax.swing.JMenuItem menuZoomIn;
    private javax.swing.JMenuItem menuZoomOut;
    private hu.astrid.viewer.gui.mutation.MutationsDialog mutationsDialog;
    private hu.astrid.viewer.gui.content.alignment.NavigationToolBar navigationToolbar;
    private javax.swing.ButtonGroup profileButtonGroup;
    private hu.astrid.viewer.properties.ProfileSettingsDialog profileSettingsDialog;
    private javax.swing.JMenu quickLinksToBAMFiles;
    private javax.swing.JMenu quickLinksToFASTAFiles;
    private javax.swing.JMenu quickLinksToGFFFiles;
    private javax.swing.JMenu quickLinksToSAMFiles;
    private javax.swing.JMenu quickLinksToWorkspaces;
    private javax.swing.JFileChooser readFileChooser;
    private javax.swing.JDialog readInfoDialog;
    private javax.swing.JMenuItem saveConsensusMenuItem;
    private hu.astrid.viewer.gui.selection.SelectToolBar selectToolBar;
    private hu.astrid.viewer.gui.StatusBar statusBar;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JInternalFrame workspaceInternalFrame;
    private hu.astrid.viewer.gui.workspace.WorkspacePanel workspacePanel;
    private hu.astrid.viewer.gui.ZoomToolBar zoomToolBar;
    // End of variables declaration//GEN-END:variables

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public DashBoard getDashBoard() {
		return dashBoard;
	}

	private void refreshQuickLinks() {
		for (final FileTypes fileType : FileTypes.values()) {
//			if (fileType != FileTypes.WORKSPACE && fileType != FileTypes.PROFILE) {
			if (fileType != FileTypes.PROFILE) {
				final JMenu menu = quickLinkMenus.get(fileType);
				final List<File> files = Viewer.getApplicationProperties().getLastOpenedFiles(fileType);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (files.isEmpty()) {
							menu.setVisible(false);
						} else {
							menu.setVisible(true);
							menu.removeAll();
							for (final File file : files) {
								JMenuItem tempJMenu = new JMenuItem(file.getName());
								tempJMenu.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										fileOpenAction(file, fileType);
									}
								});
								tempJMenu.setToolTipText(file.getAbsolutePath());
								menu.add(tempJMenu);
							}
						}
					}
				});
			}
		}
	}

	public void fileOpenAction(final File file, final FileTypes fileType) {
		Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
		new SwingWorker<Object, Void>() {

			Throwable throwable = null;

			@Override
			protected Object doInBackground() throws Exception {
				try {
					switch (fileType) {
						case FASTA: {
							try {
								Viewer.getController().setFileOpenInProgress(true);
								Viewer.getController().openFastaFile(file);
							} catch (FastaRandomReaderException ex) {
								Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
								if (ex instanceof InvalidFastaFileException) {
									SwingUtilities.invokeLater(new Runnable() {

										@Override
										public void run() {
											JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("warningMessageInvalidFastaFile"), Viewer.getLabelResources().getString("dialogTitleWarning"), JOptionPane.WARNING_MESSAGE);
										}
									});
								} else {
									SwingUtilities.invokeLater(new Runnable() {

										@Override
										public void run() {
											JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("errorMessageUnknownError"), Viewer.getLabelResources().getString("dialogTitleError"), JOptionPane.WARNING_MESSAGE);
										}
									});
								}
							}
							break;
						}
						case GFF: {
							try {
								Viewer.getController().setFileOpenInProgress(true);
								Viewer.getController().loadAnnotations(file);
							} catch (GffFileFormatException ex) {
								Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("warningMessageWrongGffFileFormat"), Viewer.getLabelResources().getString("dialogTitleWarning"), JOptionPane.WARNING_MESSAGE);
									}
								});
							}
							break;
						}
						case SAM: {
							try {
								Viewer.getController().setFileOpenInProgress(true);
								Viewer.getController().loadSamFile(file);
							} catch (MappingFileFormatException ex) {
								Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("warningMessageWrongMappingFileFormat"), Viewer.getLabelResources().getString("dialogTitleWarning"), JOptionPane.WARNING_MESSAGE);
									}
								});
							} catch (final ViewerReadModel.SamSizeExceededException ex) {
								Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										JOptionPane.showMessageDialog(Viewer.getMainWindow(), MessageFormat.format(Viewer.getLabelResources().getString("warningMessageSamSizeExceeded"), ex.getFilePath(), ex.getSizeLimit()), Viewer.getLabelResources().getString("dialogTitleWarning"), JOptionPane.WARNING_MESSAGE);
									}
								});
							}
							break;
						}
						case BAM: {
							try {
								Viewer.getController().setFileOpenInProgress(true);
								Viewer.getController().loadBamFile(file);
							} catch (MappingFileException ex) {
								Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("warningMessageWrongMappingFileFormat"), Viewer.getLabelResources().getString("dialogTitleWarning"), JOptionPane.WARNING_MESSAGE);
									}
								});
							}
							break;
						}
						case WORKSPACE: {
							final boolean exists = new File(file.getPath() + System.getProperty("file.separator") + file.getName() + ".info").exists();
							if (!exists) {
								Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
								JOptionPane.showMessageDialog(MainWindow.this, "Nem valós Viewer workspace!");
								break;
							}
							Viewer.getController().setModelProperty(WorkspaceModel.WORKSPACE_LOAD, file);
							Viewer.getController().setFileOpenInProgress(true);
							break;
						}
						default: {
							throw new AssertionError("Unsupported file type " + fileType);
						}
					}
				} catch (final FileNotFoundException ex) {
					logger.warn(ex.getMessage());
					Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("errorMessageFileNotFound") + "\n" + ex.getMessage(), Viewer.getLabelResources().getString("dialogTitleError"), JOptionPane.ERROR_MESSAGE);
						}
					});
				} catch (final IOException ex) {
					logger.warn(ex.getMessage());
					Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(Viewer.getMainWindow(), Viewer.getLabelResources().getString("errorMessageIO"), Viewer.getLabelResources().getString("dialogTitleError"), JOptionPane.ERROR_MESSAGE);
						}
					});
				} catch (Throwable tw) {
					throwable = tw;
				}
				return null;
			}

			@Override
			protected void done() {
				Viewer.getController().setFileOpenInProgress(false);
				if (throwable != null) {
					Viewer.stopStatusbarJob(throwable.getMessage());
					logger.error(throwable.getMessage(), throwable);
				}
			}
		}.execute();
	}

	/**
	 * @return scrollbar position of the center of the screen
	 * @see DashBoard#getScrollBarMiddle()
	 */
	public int scrollBarMiddle() {
		return dashBoard.getScrollBarMiddle();
	}

	/**
	 * @return width of display in number of characters
	 * @see DashBoard#getDisplayWidth()
	 */
	public int getDisplayWidth() {
		return dashBoard.getDisplayWidth();
	}

	public List<AbstractView> getAdditionalViews() {
		ArrayList<AbstractView> views = new ArrayList<AbstractView>(5);
		for (ContentPanel panel : dashBoard.getPanels()) {
			views.add((AbstractView) panel);
		}
		views.add(workspacePanel);
		views.add(mutationsDialog);
		views.add(selectToolBar);
		return views;
	}

	private void refreshVisiblePanels() {
		if (Viewer.getReadModel().isReadsLoaded()) {
			if (Viewer.getActiveProfile().isShowNavigationPanel()) {
				navigationToolbar.useNewInDelManager(); //TODO from indel
			}
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				navigationToolbar.setVisible(Viewer.getActiveProfile().isShowNavigationPanel() && Viewer.getController().isReadsLoaded());
			}
		});

		//TODO bezáráskor eltünteti de megnyitáskor egyből feldobja
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				dashBoard.gffPanel.featureNavigationDialog.setVisible(Viewer.getActiveProfile().isShowFeatureTable() && Viewer.getController().isAnnotationsLoaded());
			}
		});
	}

	@Override
	public void setCursor(java.awt.Cursor cursor) {
		super.setCursor(cursor);
		workspaceInternalFrame.setCursor(cursor);
	}

	private static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
			} catch (IOException e) {
				// TODO: error handling
			}
		} else {
			// TODO: error handling
		}
	}
}
