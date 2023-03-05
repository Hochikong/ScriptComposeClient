package me.ckho.USwingGUI.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Use this to avoid color rendered on JTable rows before user click AnalyzeFilterLogsButton btn
 */
public class TableRowsRendererNoColors extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel ci = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // add tooltip for long cmd scripts
        ci.setToolTipText(ci.getText());
        return ci;
    }
}
