/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ckho.USwingGUI.codegen;

import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;
import com.formdev.flatlaf.ui.FlatToolBarBorder;
import me.ckho.USwingGUI.components.QueryTable;
import me.ckho.USwingGUI.components.TableRowsRenderer;
import me.ckho.USwingGUI.components.VerticalTabComp;
import me.ckho.USwingGUI.entity.SimpleConnectionCfg;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author ckhoi
 */
public class impMainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public impMainFrame() {
        this.connReg.put("Test", new SimpleConnectionCfg("Test", "URL", "USERNAME", "PASSWD"));
        initComponents();
        HideHead();
    }

    public impMainFrame(List<SimpleConnectionCfg> connReg) {
        for (SimpleConnectionCfg cfg : connReg) {
            this.connReg.put(cfg.getName(), cfg);
        }
        initComponents();
        HideHead();
    }

    protected String[] timeFilters = {"Not Selected", "Last 1 Hour", "Last 6 Hours", "Last 12 Hours", "Last 1 Day", "Last 3 Days", "Last Week"};

    //<Auto-Generate>
    private void RefreshRegsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshRegsButtonActionPerformed
        impRefreshRegsButtonActionPerformed(evt);
    }//GEN-LAST:event_RefreshRegsButtonActionPerformed

    private void AboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutMenuItemActionPerformed
        impAboutMenuItemActionPerformed(evt);
    }//GEN-LAST:event_AboutMenuItemActionPerformed

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        impExitMenuItemActionPerformed(evt);
    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void AnalyzeFilterLogsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnalyzeFilterLogsButtonActionPerformed
        impAnalyzeFilterLogsButtonActionPerformed(evt);
    }//GEN-LAST:event_AnalyzeFilterLogsButtonActionPerformed

    private void ViewLogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewLogButtonActionPerformed
        impViewLogButtonActionPerformed(evt);
    }//GEN-LAST:event_ViewLogButtonActionPerformed

    private void ScriptsTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ScriptsTabbedPaneStateChanged
        impScriptsTabbedPaneStateChanged(evt);
    }//GEN-LAST:event_ScriptsTabbedPaneStateChanged

    private void ClearFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearFilterButtonActionPerformed
        impClearFilterButtonActionPerformed(evt);
    }//GEN-LAST:event_ClearFilterButtonActionPerformed

    private void LogSelectComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LogSelectComboBoxItemStateChanged
        impLogSelectComboBoxItemStateChanged(evt);
    }//GEN-LAST:event_LogSelectComboBoxItemStateChanged

    private void QuickRangeSelectComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_QuickRangeSelectComboBoxItemStateChanged
        impQuickRangeSelectComboBoxItemStateChanged(evt);
    }//GEN-LAST:event_QuickRangeSelectComboBoxItemStateChanged

    //</Auto-Generate>
    protected String currentSelectedTreeNode = "";
    protected HashMap<String, SimpleConnectionCfg> connReg = new HashMap<>();

    protected JLabel registerVerticalTab(String tabTitle, boolean clockwise) {
        JLabel result = new JLabel(tabTitle);
        result.setPreferredSize(new Dimension(12, 80));
        result.setFont(OverrideUIFont);
        result.setVerticalAlignment(SwingConstants.CENTER);
        result.setHorizontalAlignment(SwingConstants.CENTER);
        result.setUI(new VerticalTabComp(clockwise));
        return result;
    }

    protected void HideHead() {
        // Like IDEA style
        this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    }

    // -------------------------------------------------------------------------
    // Logic code
    protected TableModel buildSearchResultModel() {
        return new QueryTable();
    }

    protected TableModel buildSearchResultModel(Object[][] data) {
        return new QueryTable(data);
    }

    protected void newTab(String groupName) {
        JScrollPane ScriptsScrollPane = new javax.swing.JScrollPane();
        ScriptsScrollPane.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        ScriptsTabbedPane.addTab(groupName, ScriptsScrollPane);

        JTable tableResult = new javax.swing.JTable();
        tableResult.setRowHeight(30);
        tableResult.setShowVerticalLines(true);
        tableResult.setModel(buildSearchResultModel());
        tableResult.removeColumn(tableResult.getColumnModel().getColumn(0));
        // hide last column
        tableResult.removeColumn(tableResult.getColumnModel().getColumn(5));

        ListSelectionModel selectionModel = tableResult.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                // make it not emit event twice
                if (!e.getValueIsAdjusting()) {
                    // due to fifth column hidded, should get data from model not the table!!
                    currentSelectTaskHash = tableResult.getModel().getValueAt(tableResult.getSelectedRow(), 5).toString();
                }
            }
        });

        tableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableResult.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        ScriptsScrollPane.setViewportView(tableResult);
    }

    protected void updateRowColor() {
        int count = ScriptsTabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            JScrollPane scrollPane = (JScrollPane) ScriptsTabbedPane.getComponentAt(i);
            JViewport viewport = scrollPane.getViewport();
            JTable tb = (JTable) viewport.getView();
            int tableRows = tb.getRowCount();
            for (int j = 0; j < tableRows; j++) {
                tb.setDefaultRenderer(Object.class, new TableRowsRenderer());
            }
        }
    }

    protected void treeMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2 && !evt.isConsumed() && currentSelectedTreeNode != "Compose Service") {
            this.ConnStatusLabel.setText(this.currentSelectedTreeNode + " Connected");
            this.ConnStatusLabel.setForeground(new Color(0, 204, 153));
        }
    }

    protected JTree initConnectionsTree() {
        DefaultMutableTreeNode topTree = new DefaultMutableTreeNode("Compose Service");
        for (String key : connReg.keySet()) {
            DefaultMutableTreeNode services = new DefaultMutableTreeNode(key);
            topTree.add(services);
        }
        JTree tree = new JTree(topTree);
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMouseClicked(evt);
            }
        });
        tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                currentSelectedTreeNode = selectedNode.getUserObject().toString();
            }
        });
        return tree;
    }

    protected void refreshScriptsLogsDropdown() {
        LogSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(availableScriptsLogs.stream().toArray(String[]::new)));
    }

    protected List<String> availableScriptsList = new ArrayList<>();
    protected List<String> availableScriptsLogs = new ArrayList<>();
    protected int currentSelectedTab = 0;
    protected String currentSelectTaskHash = "";
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JMenuItem AboutMenuItem;
    protected javax.swing.JButton AnalyzeFilterLogsButton;
    protected javax.swing.JPanel BottomPanel;
    protected javax.swing.JButton ClearFilterButton;
    protected javax.swing.JPanel ComposePanel;
    protected javax.swing.JScrollPane ComposeScrollPane;
    protected javax.swing.JLabel ConnStatusLabel;
    protected javax.swing.JTree DefaultTree;
    protected javax.swing.JLabel EdTimeLabel;
    protected javax.swing.JTextField EdTimeTextField;
    protected javax.swing.JMenuItem ExitMenuItem;
    protected javax.swing.JButton FetchLogBriefButton;
    protected javax.swing.JMenu FileMenu;
    protected javax.swing.JMenu HelpMenu;
    protected javax.swing.JTabbedPane LeftTabbedPane;
    protected javax.swing.JComboBox<String> LogSelectComboBox;
    protected javax.swing.JLabel LogViewLabel;
    protected javax.swing.JMenuBar MainMenuBar;
    protected javax.swing.JSplitPane MainSplitPane;
    protected javax.swing.JToolBar MainToolBar;
    protected javax.swing.JToolBar.Separator MainToolBarSeparator;
    protected javax.swing.JProgressBar ProgressBar;
    protected javax.swing.JPanel QueryPanel;
    protected javax.swing.JComboBox<String> QuickRangeSelectComboBox;
    protected javax.swing.JButton RefreshRegsButton;
    protected javax.swing.JSplitPane RightSplitPane;
    protected javax.swing.JTabbedPane ScriptsTabbedPane;
    protected javax.swing.JLabel StTimeLabel;
    protected javax.swing.JTextField StTimeTextField;
    protected javax.swing.JLabel StatusLabel;
    protected javax.swing.JPanel ToolPanel;
    protected javax.swing.JButton ViewLogButton;
    // controll vertical tab menu font size
    private Font OverrideUIFont = UIManager.getFont("defaultFont").deriveFont(14.0F);

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLightFlatIJTheme.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                impMainFrame f = new impMainFrame();
                f.setVisible(true);
                f.setLocationRelativeTo(null);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ToolPanel = new javax.swing.JPanel();
        MainToolBar = new javax.swing.JToolBar();
        RefreshRegsButton = new javax.swing.JButton();
        AnalyzeFilterLogsButton = new javax.swing.JButton();
        MainToolBarSeparator = new javax.swing.JToolBar.Separator();
        LogViewLabel = new javax.swing.JLabel();
        LogSelectComboBox = new javax.swing.JComboBox<>();
        FetchLogBriefButton = new javax.swing.JButton();
        ViewLogButton = new javax.swing.JButton();
        MainSplitPane = new javax.swing.JSplitPane();
        LeftTabbedPane = new javax.swing.JTabbedPane();
        ComposePanel = new javax.swing.JPanel();
        ComposeScrollPane = new javax.swing.JScrollPane();
        DefaultTree = initConnectionsTree();
        RightSplitPane = new javax.swing.JSplitPane();
        ScriptsTabbedPane = new javax.swing.JTabbedPane();
        QueryPanel = new javax.swing.JPanel();
        StTimeLabel = new javax.swing.JLabel();
        StTimeTextField = new javax.swing.JTextField();
        EdTimeLabel = new javax.swing.JLabel();
        EdTimeTextField = new javax.swing.JTextField();
        QuickRangeSelectComboBox = new javax.swing.JComboBox<>();
        ClearFilterButton = new javax.swing.JButton();
        BottomPanel = new javax.swing.JPanel();
        StatusLabel = new javax.swing.JLabel();
        ProgressBar = new javax.swing.JProgressBar();
        ConnStatusLabel = new javax.swing.JLabel();
        MainMenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        ExitMenuItem = new javax.swing.JMenuItem();
        HelpMenu = new javax.swing.JMenu();
        AboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ScriptComposeClient");
        setSize(new java.awt.Dimension(1920, 1080));

        ToolPanel.setLayout(new java.awt.BorderLayout());

        MainToolBar.setBorder(new FlatToolBarBorder());
        MainToolBar.setRollover(true);

        RefreshRegsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ico/20pix/refresh.png"))); // NOI18N
        RefreshRegsButton.setToolTipText("Refresh all registered scripts");
        RefreshRegsButton.setFocusable(false);
        RefreshRegsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        RefreshRegsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        RefreshRegsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshRegsButtonActionPerformed(evt);
            }
        });
        MainToolBar.add(RefreshRegsButton);

        AnalyzeFilterLogsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ico/20pix/filter.png"))); // NOI18N
        AnalyzeFilterLogsButton.setToolTipText("Analyze logs by keywords");
        AnalyzeFilterLogsButton.setFocusable(false);
        AnalyzeFilterLogsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AnalyzeFilterLogsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AnalyzeFilterLogsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnalyzeFilterLogsButtonActionPerformed(evt);
            }
        });
        MainToolBar.add(AnalyzeFilterLogsButton);
        MainToolBar.add(MainToolBarSeparator);

        LogViewLabel.setText("Log View: ");
        MainToolBar.add(LogViewLabel);

        LogSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        LogSelectComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LogSelectComboBoxItemStateChanged(evt);
            }
        });
        MainToolBar.add(LogSelectComboBox);

        FetchLogBriefButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ico/20pix/getlog.png"))); // NOI18N
        FetchLogBriefButton.setToolTipText("Fetch logs");
        FetchLogBriefButton.setFocusable(false);
        FetchLogBriefButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        FetchLogBriefButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolBar.add(FetchLogBriefButton);

        ViewLogButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ico/20pix/view.png"))); // NOI18N
        ViewLogButton.setToolTipText("View log details");
        ViewLogButton.setFocusable(false);
        ViewLogButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ViewLogButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ViewLogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewLogButtonActionPerformed(evt);
            }
        });
        MainToolBar.add(ViewLogButton);

        ToolPanel.add(MainToolBar, java.awt.BorderLayout.CENTER);

        MainSplitPane.setDividerSize(2);

        LeftTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        ComposePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        ComposePanel.setPreferredSize(new java.awt.Dimension(200, 597));

        ComposeScrollPane.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        ComposeScrollPane.setViewportView(DefaultTree);

        javax.swing.GroupLayout ComposePanelLayout = new javax.swing.GroupLayout(ComposePanel);
        ComposePanel.setLayout(ComposePanelLayout);
        ComposePanelLayout.setHorizontalGroup(
                ComposePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(ComposePanelLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(ComposeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                                .addGap(0, 0, 0))
        );
        ComposePanelLayout.setVerticalGroup(
                ComposePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(ComposePanelLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(ComposeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                                .addGap(0, 0, 0))
        );

        LeftTabbedPane.addTab("tab1", ComposePanel);
        LeftTabbedPane.setTabComponentAt(0, registerVerticalTab("Servers", false));

        MainSplitPane.setLeftComponent(LeftTabbedPane);

        RightSplitPane.setDividerLocation(540);
        RightSplitPane.setDividerSize(2);
        RightSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        ScriptsTabbedPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        ScriptsTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ScriptsTabbedPaneStateChanged(evt);
            }
        });
        RightSplitPane.setLeftComponent(ScriptsTabbedPane);

        QueryPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        QueryPanel.setMinimumSize(new java.awt.Dimension(0, 50));

        StTimeLabel.setText("Start Time: ");

        EdTimeLabel.setText("End Time: ");

        QuickRangeSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(timeFilters));
        QuickRangeSelectComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                QuickRangeSelectComboBoxItemStateChanged(evt);
            }
        });

        ClearFilterButton.setText("Clear Filter");
        ClearFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearFilterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout QueryPanelLayout = new javax.swing.GroupLayout(QueryPanel);
        QueryPanel.setLayout(QueryPanelLayout);
        QueryPanelLayout.setHorizontalGroup(
                QueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(QueryPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(StTimeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(StTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(EdTimeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(EdTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(QuickRangeSelectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ClearFilterButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        QueryPanelLayout.setVerticalGroup(
                QueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(QueryPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(QueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(QueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(EdTimeLabel)
                                                .addComponent(EdTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(QuickRangeSelectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(ClearFilterButton))
                                        .addGroup(QueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(StTimeLabel)
                                                .addComponent(StTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(23, Short.MAX_VALUE))
        );

        RightSplitPane.setRightComponent(QueryPanel);

        MainSplitPane.setRightComponent(RightSplitPane);

        BottomPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        StatusLabel.setText("Connection Status: ");

        javax.swing.GroupLayout BottomPanelLayout = new javax.swing.GroupLayout(BottomPanel);
        BottomPanel.setLayout(BottomPanelLayout);
        BottomPanelLayout.setHorizontalGroup(
                BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(BottomPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(StatusLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ConnStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        BottomPanelLayout.setVerticalGroup(
                BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(BottomPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(StatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(ConnStatusLabel))
                                        .addComponent(ProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        FileMenu.setText("File");

        ExitMenuItem.setText("Exit");
        ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(ExitMenuItem);

        MainMenuBar.add(FileMenu);

        HelpMenu.setText("Help");

        AboutMenuItem.setText("About");
        AboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutMenuItemActionPerformed(evt);
            }
        });
        HelpMenu.add(AboutMenuItem);

        MainMenuBar.add(HelpMenu);

        setJMenuBar(MainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ToolPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(MainSplitPane)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(ToolPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(MainSplitPane)
                                .addGap(0, 0, 0)
                                .addComponent(BottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // End of variables declaration//GEN-END:variables

    //<Auto-Generate-Result>
    protected void impRefreshRegsButtonActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void impAboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void impExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void impAnalyzeFilterLogsButtonActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void impViewLogButtonActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void impScriptsTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {
    }

    protected void impClearFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void impLogSelectComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
    }

    protected void impQuickRangeSelectComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
    }
    //</Auto-Generate-Result>
}
