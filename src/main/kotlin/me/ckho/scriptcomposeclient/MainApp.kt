package me.ckho.scriptcomposeclient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ckho.USwingGUI.codegen.impMainFrame
import me.ckho.USwingGUI.entity.SimpleConnectionCfg
import me.ckho.scriptcomposeclient.entity.TaskListEntity
import me.ckho.scriptcomposeclient.utils.JSONMapper
import me.ckho.scriptcomposeclient.utils.composeLogin
import me.ckho.scriptcomposeclient.utils.listCronTasks
import me.ckho.scriptcomposeclient.utils.splitClustersAndCreateNewTabs
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.awt.Color
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.MouseEvent
import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean
import java.net.CookieManager
import java.net.CookiePolicy
import javax.swing.*
import kotlin.system.exitProcess


class MainApp(connReg: List<SimpleConnectionCfg>) : impMainFrame(connReg) {
    // must set cookiejar, or can't log in to script-compose
    private val cookieManager = CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ALL) }
    val httpClient: OkHttpClient = OkHttpClient().newBuilder().cookieJar(JavaNetCookieJar(cookieManager)).build()

    private var currentRangeSelect = "Last 1 Hour"
    private var st_time = ""
    private var ed_time = ""

    // override methods
    override fun impExitMenuItemActionPerformed(evt: ActionEvent?) {
        exitProcess(0)
    }

    override fun impAboutMenuItemActionPerformed(evt: ActionEvent?) {
        val system: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean()
        val runtime = ManagementFactory.getRuntimeMXBean()
        val info = "OS: ${system.name} \nArch: ${system.arch} " +
                "\nVM: ${runtime.vmName} \nVendor: ${runtime.vmVendor}" +
                "\nSpec: ${runtime.specVersion} \nVersion: ${runtime.vmVersion}"
        JOptionPane.showMessageDialog(
            this,
            "Script Compose Client \nckhoidea@hotmail.com \n\n$info",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        )
    }

    override fun impRefreshRegsButtonActionPerformed(evt: ActionEvent?) {
        super.ProgressBar.isIndeterminate = true
        val cfg = super.connReg[super.currentSelectedTreeNode]
        if (cfg != null) {
            while (ScriptsTabbedPane.tabCount > 0)
                ScriptsTabbedPane.remove(0);

            val done = composeLogin(cfg)
            if (done) {
                super.ProgressBar.isIndeterminate = false
                ConnStatusLabel.text = "$currentSelectedTreeNode Connected"
                ConnStatusLabel.foreground = Color(0, 204, 153)

                val jsonStr = listCronTasks(cfg)
                val scripts = JSONMapper.readValue(jsonStr, TaskListEntity::class.java)
                splitClustersAndCreateNewTabs(scripts.tasks)

                JOptionPane.showMessageDialog(
                    this@MainApp,
                    "Refresh done"
                )
            } else {
                super.ProgressBar.isIndeterminate = false
                ConnStatusLabel.text = "$currentSelectedTreeNode Connect Failed"
                ConnStatusLabel.foreground = Color(196, 22, 7)
            }
        }
    }

    override fun impQuickRangeSelectComboBoxItemStateChanged(evt: ItemEvent?) {
        if(evt!!.stateChange == ItemEvent.SELECTED) {
            this.currentRangeSelect = QuickRangeSelectComboBox.selectedItem as String
        }
    }

    override fun treeMouseClicked(evt: MouseEvent?) {
        if (evt!!.clickCount == 2 && !evt.isConsumed && currentSelectedTreeNode != "Compose Service") {
            CoroutineScope(Dispatchers.IO).launch {
                super.ProgressBar.isIndeterminate = true
                val cfg = super.connReg[super.currentSelectedTreeNode]!!

                try {
                    val done = composeLogin(cfg)
                    if (done) {
                        super.ProgressBar.isIndeterminate = false
                        ConnStatusLabel.text = "$currentSelectedTreeNode Connected"
                        ConnStatusLabel.foreground = Color(0, 204, 153)

                        val jsonStr = listCronTasks(cfg)
                        val scripts = JSONMapper.readValue(jsonStr, TaskListEntity::class.java)
                        splitClustersAndCreateNewTabs(scripts.tasks)
                    } else {
                        super.ProgressBar.isIndeterminate = false
                        ConnStatusLabel.text = "$currentSelectedTreeNode Connect Failed"
                        ConnStatusLabel.foreground = Color(196, 22, 7)
                    }
                } catch (e: java.net.SocketTimeoutException) {
                    super.ProgressBar.isIndeterminate = false
                    ConnStatusLabel.text = "$currentSelectedTreeNode Connect Failed"
                    ConnStatusLabel.foreground = Color(196, 22, 7)
                }
            }
        }
    }

    fun newTab(title: String, data: Array<Array<String>>) {
        CoroutineScope(Dispatchers.IO).launch {
            val scriptsScrollPane = JScrollPane()
            scriptsScrollPane.border = BorderFactory.createCompoundBorder()
            ScriptsTabbedPane.addTab(title, scriptsScrollPane)

            val tableResult = JTable()
            tableResult.rowHeight = 30
            tableResult.showVerticalLines = true
            tableResult.model = buildSearchResultModel(data)
            tableResult.removeColumn(tableResult.columnModel.getColumn(0))
            // hide last column
            // hide last column
            tableResult.removeColumn(tableResult.columnModel.getColumn(5))

            val selectionModel = tableResult.selectionModel
            selectionModel.addListSelectionListener { e ->
                // make it not emit event twice
                if (!e.valueIsAdjusting) {
                    // due to fifth column hidded, should get data from model not the table!!
                    currentSelectTaskHash = tableResult.model.getValueAt(tableResult.selectedRow, 5).toString()
                }
            }

            tableResult.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            tableResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

            scriptsScrollPane.setViewportView(tableResult)
        }
    }
}

