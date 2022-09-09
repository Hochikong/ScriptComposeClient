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
 *
 * @author ckhoi
 */
public class TableRowsRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
// Only change cell color
//        TaskStatusEnum status = (TaskStatusEnum) value;
//        if (value == TaskStatusEnum.FAILED) {
//            super.setForeground(Color.red);
//            
//        }else{
//            super.setForeground(Color.blue);
//        }

// Change row color
        String hash = (String) table.getModel().getValueAt(row, 0);
        if ("FAILED".equals(hash)) {
            super.setForeground(new Color(214, 51, 57));
        }else{
            super.setForeground(new Color(31, 186, 77));
        }
        if ("SUCCEED".equals(hash)) {
            super.setForeground(new Color(31, 186, 77));
        }
        return c;
    }
}
