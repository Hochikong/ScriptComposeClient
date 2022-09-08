/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ckho.USwingGUI.components;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ckhoi
 */
public class QueryTable extends AbstractTableModel {
    private String[] columnNames = {
            "Status",
            "Group",
            "Command",
            "Job Type",
            "Interval",
            "Trigger",
            "Hash"};

    private Object[][] data = {
            {"FAILED", "Group 1", "sh xxx.py", "cron", "20", "0 * * ? * *", "Hash 1"},
            {"SUCCEED", "Group 1", "sh yyy.py", "cron", "20", "0 * * ? * *", "Hash 2"},};

    public QueryTable(Object[][] customData) {
        this.data = customData;
    }

    public QueryTable() {
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    // my implementation
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = aValue;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public Object[][] readData() {
        return this.data;
    }
}
