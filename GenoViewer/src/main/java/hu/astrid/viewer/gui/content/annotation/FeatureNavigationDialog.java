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
 * FeatureNavigationDialog.java
 *
 * Created on 2010.05.07., 9:53:25
 */
package hu.astrid.viewer.gui.content.annotation;

import hu.astrid.mapping.exception.GffFileFormatException;
import hu.astrid.mapping.model.GffRecord;
import hu.astrid.viewer.Viewer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JTable;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.apache.log4j.Logger;

/**
 *
 * @author OTTO
 */
public class FeatureNavigationDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3909173005650582024L;
	private static final Logger logger = Logger.getLogger(FeatureNavigationDialog.class);

	private static class FeatureData implements Comparable<FeatureData> {

		private int startPosition;
		private String name;
		private String type;

		public FeatureData(int startPosition, String name, String type) {
			this.startPosition = startPosition;
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return "Start: " + startPosition + " Name: " + name;
		}

		@Override
		public int compareTo(FeatureData o) {
			if (this.startPosition > o.startPosition) {
				return 1;
			}
			if (this.startPosition < o.startPosition) {
				return -1;
			}
			return 0;
		}
	}

	public void loadFeatureTable() {
		Object[] featureTableColumnNames = {Viewer.getLabelResources().getString("featureNavigationName"),
			Viewer.getLabelResources().getString("featureNavigationPosition"),
			Viewer.getLabelResources().getString("featureNavigationType")};
		List<FeatureData> features = new ArrayList<FeatureData>();
		for (Set<GffRecord> set : Viewer.getGffModel().getAnnotations().values()) {
			for (GffRecord gffKeyRecord : set) {
				String featureID = null;

				try {
					featureID = gffKeyRecord.getAttributeValue("Name", "ID", "locus_tag");
				} catch (GffFileFormatException exc) {
//					logger.error("Cannot specify any identification field in GFF file, mark feature name as 'unknown'", exc);
					featureID = "unknown";
				}
				features.add(new FeatureData(gffKeyRecord.getStart(), featureID, gffKeyRecord.getType()));
			}
		}
		Collections.sort(features);

		Object[][] featureTableRowData = new Object[features.size()][3];
		for (int i = 0; i < features.size(); i++) {
			featureTableRowData[i][0] = features.get(i).name;
			featureTableRowData[i][1] = features.get(i).startPosition;
			featureTableRowData[i][2] = features.get(i).type;
		}
		featureTable.setModel(new DefaultTableModel(featureTableRowData, featureTableColumnNames));
		featureTable.getColumn(featureTableColumnNames[0]).setCellRenderer(new CellRenderer());
		featureTable.getColumn(featureTableColumnNames[1]).setCellRenderer(new CellRenderer());
		featureTable.getColumn(featureTableColumnNames[2]).setCellRenderer(new CellRenderer());
		featureTable.getColumn(featureTableColumnNames[0]).setPreferredWidth(150);
		this.setVisible(Viewer.getActiveProfile().isShowFeatureTable());
	}

	/** Creates new form FeatureNavigationDialog */
	public FeatureNavigationDialog() {
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

        jScrollPane1 = new javax.swing.JScrollPane();
        featureTable = new javax.swing.JTable();

        setTitle(Viewer.getLabelResources().getString("features")); // NOI18N
        setFocusable(false);
        setFocusableWindowState(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        featureTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Position", "Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        featureTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        featureTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                featureTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(featureTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void featureTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_featureTableMouseClicked
		Viewer.getMainWindow().scrollToPosition((Integer) featureTable.getModel().getValueAt(featureTable.getSelectedRow(), 1));
	}//GEN-LAST:event_featureTableMouseClicked

	private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		Viewer.getActiveProfile().setShowFeatureTable(false);
	}//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable featureTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

	/**
	 * Simple cell renderer that suppelents default renderer of cells to
	 * show content in tooltip if its not fully displayed
	 */
	private static class CellRenderer implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel content = (JLabel) table.getDefaultRenderer(table.getColumnClass(column)).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (table.getColumnModel().getColumn(column).getWidth() < content.getPreferredSize().width) {
				content.setToolTipText(content.getText());
			} else {
				content.setToolTipText(null);
			}


			Map<String, Boolean> map = Viewer.getController().getAnnotationsVisibility();
			String type = (String) table.getValueAt(row, 2);

			Font font = content.getFont();
			Map attributes = font.getAttributes();

			if (map.containsKey(type) && !map.get(type)) {
				content.setBackground(Color.lightGray);
				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			} else {
				content.setBackground(null);
			}

			content.setForeground(Color.black);
			content.setFont(new Font(attributes));

			if (isSelected) {
				content.setFont(content.getFont().deriveFont(Font.BOLD));
			}
			
			return content;
		}
	}
}
