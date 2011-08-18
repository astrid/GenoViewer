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
 * SelectToolBar.java
 *
 * Created on 2010.08.17., 15:18:00
 */
package hu.astrid.viewer.gui.selection;

import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.ResizeableScrollPanel;
import hu.astrid.viewer.model.SelectionModel.SelectionType;
import hu.astrid.viewer.model.WorkspaceModel;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 *
 * @author OTTO
 */
public class SelectToolBar extends JToolBar implements AbstractView {

	/** Creates new form SelectToolBar */
	public SelectToolBar() {
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectToggleButton = new javax.swing.JToggleButton();
        copyReferenceButton = new javax.swing.JButton();
        copyConsensusButton = new javax.swing.JButton();
        copyAllButton = new javax.swing.JButton();

        selectToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/selecticons/select_cursor.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("LabelResources_en_US"); // NOI18N
        selectToggleButton.setToolTipText(bundle.getString("selectToolToolTipText")); // NOI18N
        selectToggleButton.setFocusable(false);
        selectToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectToggleButtonActionPerformed(evt);
            }
        });

        copyReferenceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/selecticons/copyReferenceIcon.png"))); // NOI18N
        copyReferenceButton.setToolTipText(bundle.getString("copyReferenceToolTipText")); // NOI18N
        copyReferenceButton.setEnabled(false);
        copyReferenceButton.setFocusable(false);
        copyReferenceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        copyReferenceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        copyReferenceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyReferenceButtonActionPerformed(evt);
            }
        });

        copyConsensusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/selecticons/copyConsensus.png"))); // NOI18N
        copyConsensusButton.setToolTipText(bundle.getString("copyConsensusToolTipText")); // NOI18N
        copyConsensusButton.setEnabled(false);
        copyConsensusButton.setFocusable(false);
        copyConsensusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        copyConsensusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        copyConsensusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyConsensusButtonActionPerformed(evt);
            }
        });

        copyAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/selecticons/copyAllIcon.png"))); // NOI18N
        copyAllButton.setToolTipText(bundle.getString("copyBothSequencToolTipText")); // NOI18N
        copyAllButton.setEnabled(false);
        copyAllButton.setFocusable(false);
        copyAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        copyAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        copyAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyAllButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(selectToggleButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyReferenceButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyConsensusButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyAllButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(copyAllButton)
            .addComponent(copyConsensusButton)
            .addComponent(copyReferenceButton)
            .addComponent(selectToggleButton)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void copyConsensusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyConsensusButtonActionPerformed
	    Viewer.getController().copyToClipboard(SelectionType.CONSENSUS);
    }//GEN-LAST:event_copyConsensusButtonActionPerformed

    private void copyReferenceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyReferenceButtonActionPerformed
	    Viewer.getController().copyToClipboard(SelectionType.REFERENCE);
    }//GEN-LAST:event_copyReferenceButtonActionPerformed

    private void copyAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyAllButtonActionPerformed
	    Viewer.getController().copyToClipboard(SelectionType.BOTH);
    }//GEN-LAST:event_copyAllButtonActionPerformed

    private void selectToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectToggleButtonActionPerformed
	    ResizeableScrollPanel.selectionEnabled = selectToggleButton.isSelected();
	    if (!ResizeableScrollPanel.selectionEnabled) {
		    Viewer.getMainWindow().getDashBoard().clearPaintings();
		    copyAllButton.setEnabled(false);
		    copyConsensusButton.setEnabled(false);
		    copyReferenceButton.setEnabled(false);
	    } else {
		    copyAllButton.setEnabled(true);
		    copyConsensusButton.setEnabled(true);
		    copyReferenceButton.setEnabled(true);
	    }
    }//GEN-LAST:event_selectToggleButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyAllButton;
    private javax.swing.JButton copyConsensusButton;
    private javax.swing.JButton copyReferenceButton;
    private javax.swing.JToggleButton selectToggleButton;
    // End of variables declaration//GEN-END:variables

	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(WorkspaceModel.WORKSPACE_PROJECT_CHANGE)) {
			ResizeableScrollPanel.selectionEnabled = false;
			selectToggleButton.setSelected(false);
			copyAllButton.setEnabled(false);
			copyConsensusButton.setEnabled(false);
			copyReferenceButton.setEnabled(false);
		}
	}

	public void refreshI18N() {
		this.selectToggleButton.setToolTipText(Viewer.getLabelResources().getString("selectToolToolTipText"));
		this.copyReferenceButton.setToolTipText(Viewer.getLabelResources().getString("copyReferenceToolTipText"));
		this.copyConsensusButton.setToolTipText(Viewer.getLabelResources().getString("copyConsensusToolTipText"));
		this.copyAllButton.setToolTipText(Viewer.getLabelResources().getString("copyBothSequencToolTipText"));
	}
}