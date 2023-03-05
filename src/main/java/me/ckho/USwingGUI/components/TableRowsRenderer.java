/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ckho.USwingGUI.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author ckhoi
 */

/**
 * Change rows' color by task status
 * */
public class TableRowsRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel ci = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // add tooltip for long cmd scripts
        ci.setToolTipText(ci.getText());
//        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
// Change row color
        String status = (String) table.getModel().getValueAt(row, 0);
        if ("FAILED".equals(status)) {
            super.setForeground(new Color(214, 51, 57));
            return ci;
        } else if ("UNDEFINED".equals(status)) {
            super.setForeground(new Color(214, 122, 51));
            return ci;
        } else if ("SUCCEED".equals(status)) {
            super.setForeground(new Color(31, 186, 77));
            return ci;
        }else{
            super.setForeground(new Color(31, 186, 77));
            return ci;
        }
    }
}

