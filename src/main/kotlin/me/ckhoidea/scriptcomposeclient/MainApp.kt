package me.ckhoidea.scriptcomposeclient

import com.fasterxml.jackson.databind.JsonMappingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ckho.USwingGUI.codegen.impMainFrame
import me.ckho.USwingGUI.components.HeaderRenderer
import me.ckho.USwingGUI.components.TableRowsRendererNoColors
import me.ckho.USwingGUI.dialog.SimpleEnlargePreview
import me.ckho.USwingGUI.entity.SimpleConnectionCfg
import me.ckhoidea.scriptcomposeclient.entity.*
import me.ckhoidea.scriptcomposeclient.utils.*
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.awt.Color
import java.awt.Image
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.MouseEvent
import java.io.File
import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean
import java.net.CookieManager
import java.net.CookiePolicy
import java.time.format.DateTimeParseException
import javax.swing.*
import javax.swing.event.ChangeEvent
import kotlin.system.exitProcess


class MainApp(connReg: List<SimpleConnectionCfg>, icon: Image) : impMainFrame(connReg, icon) {
    init {
        super.AnalyzeFilterLogsButton.isEnabled = false
        super.TaskDetailOverviewButton.isEnabled = false
        super.FetchLogBriefButton.isEnabled = false
        super.ViewLogButton.isEnabled = false
        super.RefreshRegsButton.isEnabled = false
        val os = System.getProperty("os.name")
//        println(os)
        if (os.lowercase() == "linux") {
            getRootPane().windowDecorationStyle = JRootPane.NONE
        }
    }

    // must set cookiejar, or can't log in to script-compose
    private val cookieManager = CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ALL) }
    val httpClient: OkHttpClient = OkHttpClient().newBuilder().cookieJar(JavaNetCookieJar(cookieManager)).build()

    private var allAvailableTaskDetails = listOf<TaskDetailEntity>()

    // query filter
    private var currentRangeSelect = "Last 1 Hour"
    private var stDateField = ""
    private var edDateField = ""

    private var availableScriptsLogsStorage = mutableListOf<TaskBriefEntity>()

    private lateinit var currentSelectedLog: TaskBriefEntity

    // log analysis
    private val keyWords = JSONMapper.readValue(File("./cfg/keyword.json"), KeywordMatchingEntity::class.java)

    // override methods
    override fun impExitMenuItemActionPerformed(evt: ActionEvent?) {
        exitProcess(0)
    }

    override fun impAboutMenuItemActionPerformed(evt: ActionEvent?) {
        val buildVersion = "20221212 V1.0.2"
        val system: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean()
        val runtime = ManagementFactory.getRuntimeMXBean()
        val info = "OS: ${system.name} \nArch: ${system.arch} " +
                "\nVM: ${runtime.vmName} \nVendor: ${runtime.vmVendor}" +
                "\nSpec: ${runtime.specVersion} \nVersion: ${runtime.vmVersion}"
        JOptionPane.showMessageDialog(
            this,
            "Script Compose Client \nBuild $buildVersion\nckhoidea@hotmail.com \n\n$info",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        )
    }

    override fun impClearFilterButtonActionPerformed(evt: ActionEvent?) {
        java.awt.EventQueue.invokeLater(kotlinx.coroutines.Runnable {
            StTimeTextField.text = ""
            EdTimeTextField.text = ""
            QuickRangeSelectComboBox.selectedIndex = 0
            currentRangeSelect = "Last 1 Hour"
        })
    }

    override fun impScriptsTabbedPaneStateChanged(evt: ChangeEvent?) {
        java.awt.EventQueue.invokeLater {
            val count = ScriptsTabbedPane.tabCount
            for (i in 0 until count) {
                val scrollPane = ScriptsTabbedPane.getComponentAt(i) as JScrollPane
                val viewport = scrollPane.viewport
                val tb = viewport.view as JTable
                tb.selectionModel.clearSelection()
            }
        }
    }

    override fun impLogSelectComboBoxItemStateChanged(evt: ItemEvent?) {
        currentSelectedLog = availableScriptsLogsStorage[LogSelectComboBox.selectedIndex]
    }

    override fun impViewLogButtonActionPerformed(evt: ActionEvent?) {
        if (currentSelectTaskHash.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val cfg = super.connReg[super.currentSelectedTreeNode]!!
                java.awt.EventQueue.invokeLater(kotlinx.coroutines.Runnable {
                    super.ProgressBar.isIndeterminate = true
                })
                var jsonStr = ""
                try {
                    jsonStr = fetchLog(cfg, currentSelectedLog.logHash)
                } catch (e: UninitializedPropertyAccessException) {
                }
                java.awt.EventQueue.invokeLater {
                    super.ProgressBar.isIndeterminate = false
                }
                try {
                    val logContent = JSONMapper.readValue(jsonStr, TaskLogEntity::class.java)
                    val dialog = SimpleEnlargePreview(this@MainApp, false)
                    val taskSelected =
                        allAvailableTaskDetails.filter { it.taskHash == currentSelectTaskHash }.toList()[0]
                    dialog.updateTitle("Task: ${taskSelected.command}")
                    dialog.setPreview(
                        "<html><h3></h3>Log Hash: ${currentSelectedLog.logHash}<br><div>${
                            logContent.log.replace(
                                "\n",
                                "<br>"
                            )
                        }</div></html>"
                    )
                    dialog.setLocationRelativeTo(null)

                    dialog.isVisible = true
                } catch (e: JsonMappingException) {
                    JOptionPane.showMessageDialog(
                        this@MainApp,
                        "No such log details"
                    )
                }
            }
        }

    }


    /**
     * Refresh all registered scripts
     * */
    override fun impRefreshRegsButtonActionPerformed(evt: ActionEvent?) {
        super.ProgressBar.isIndeterminate = true
        val cfg = super.connReg[super.currentSelectedTreeNode.replace("_++cron", "").replace("_++one", "")]
        if (cfg != null) {
            while (ScriptsTabbedPane.tabCount > 0)
                ScriptsTabbedPane.remove(0)

            val done = composeLogin(cfg)
            if (done) {
                super.ProgressBar.isIndeterminate = false
                ConnStatusLabel.text = "${
                    currentSelectedTreeNode
                        .replace("_++cron", "")
                        .replace("_++one", "")
                } Connected"
                ConnStatusLabel.foreground = Color(0, 204, 153)

                if (super.currentSelectedTreeNode.endsWith("_++cron")) {
                    val jsonStr = listCronTasks(cfg)
                    val scripts = JSONMapper.readValue(jsonStr, TasksEntity::class.java)
                    allAvailableTaskDetails = scripts.tasks
                    splitClustersAndCreateNewTabs(scripts.tasks)
                } else if (super.currentSelectedTreeNode.endsWith("_++one")) {
                    // TODO
                } else {
                    val jsonStr = listCronTasks(cfg)
                    val scripts = JSONMapper.readValue(jsonStr, TasksEntity::class.java)
                    allAvailableTaskDetails = scripts.tasks
                    splitClustersAndCreateNewTabs(scripts.tasks)
                }

                JOptionPane.showMessageDialog(
                    this@MainApp,
                    "Refresh done"
                )
            } else {
                super.ProgressBar.isIndeterminate = false
                ConnStatusLabel.text = "${
                    currentSelectedTreeNode
                        .replace("_++cron", "")
                        .replace("_++one", "")
                } Connect Failed"
                ConnStatusLabel.foreground = Color(196, 22, 7)
            }
        }
    }

    override fun impQuickRangeSelectComboBoxItemStateChanged(evt: ItemEvent?) {
        if (evt!!.stateChange == ItemEvent.SELECTED) {
            this.currentRangeSelect = QuickRangeSelectComboBox.selectedItem as String
        }
    }

    override fun impAnalyzeFilterLogsButtonActionPerformed(evt: ActionEvent?) {
        if (currentSelectedTreeNode != "Compose Service" && currentSelectedTreeNode.isNotEmpty()) {
            java.awt.EventQueue.invokeLater {
                while (ScriptsTabbedPane.tabCount > 0)
                    ScriptsTabbedPane.remove(0)
            }

            CoroutineScope(Dispatchers.IO).launch {
                java.awt.EventQueue.invokeLater(kotlinx.coroutines.Runnable {
                    val cfg = super.connReg[super.currentSelectedTreeNode]!!
                    java.awt.EventQueue.invokeLater {
                        super.ProgressBar.isIndeterminate = true
                    }
                    val jsonStr = listCronTasks(cfg)
                    val scripts = JSONMapper.readValue(jsonStr, TasksEntity::class.java)
                    allAvailableTaskDetails = scripts.tasks

                    val whichTaskHashHasBadLog = mutableListOf<String>()
                    val whichTaskHashHasUndefinedLog = mutableListOf<String>()
                    val whichTaskHashHasSucceedLog = mutableListOf<String>()

                    for (s in scripts.tasks) {
                        val brs = fetchLogBriefsWrapper(cfg, s.taskHash).brief
                        if (brs.isEmpty()) {
                            // if not run, set as undefined
                            whichTaskHashHasUndefinedLog.add(s.taskHash)
                            continue
                        } else {
                            // check the latest one' status
                            val br = brs.last()
                            val jsonStrLog = fetchLog(cfg, br.logHash)
                            val logContent = JSONMapper.readValue(jsonStrLog, TaskLogEntity::class.java).log
                            for (badWord in keyWords.failed) {
                                if (badWord in logContent) {
                                    whichTaskHashHasBadLog.add(s.taskHash)
                                }
                            }
                            for (undWord in keyWords.undefined) {
                                if (undWord in logContent) {
                                    whichTaskHashHasUndefinedLog.add(s.taskHash)
                                }
                            }
                            for (sucWord in keyWords.succeed) {
                                if (sucWord in logContent) {
                                    whichTaskHashHasSucceedLog.add(s.taskHash)
                                }
                            }
                        }
                    }

                    splitClustersAndCreateNewTabs(
                        scripts.tasks,
                        true,
                        whichTaskHashHasBadLog.toSet().toList(),
                        whichTaskHashHasUndefinedLog.toSet().toList(),
                        whichTaskHashHasSucceedLog.toSet().toList()
                    )
                    Thread.sleep(1000)
                    java.awt.EventQueue.invokeLater {
                        updateRowColor()
                        super.ProgressBar.isIndeterminate = false
                        JOptionPane.showMessageDialog(
                            this@MainApp,
                            "Refresh done",
                            "Done",
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    }
                })
            }
        }
    }

    override fun impTaskDetailOverviewButtonActionPerformed(evt: ActionEvent?) {
        val cfg = super.connReg[super.currentSelectedTreeNode]!!
        java.awt.EventQueue.invokeLater {
            val taskDetail = allAvailableTaskDetails.filter { it.taskHash == currentSelectTaskHash }
            val dialog = SimpleEnlargePreview(this@MainApp, false)
            dialog.updateTitle(taskDetail[0].command)
            dialog.setPreview(
                "<html><h3>URL: ${cfg.url}/taskDetails?taskHash=${taskDetail[0].taskHash}</h3>" +
                        "<br><div>${taskDetail[0].toString().replace("\n", "<br>")}</div></html>"
            )
            dialog.setLocationRelativeTo(null)

            dialog.isVisible = true
        }
    }

    override fun impFetchLogBriefButtonActionPerformed(evt: ActionEvent?) {
        if (currentSelectTaskHash.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val cfg = super.connReg[super.currentSelectedTreeNode]!!
                java.awt.EventQueue.invokeLater(kotlinx.coroutines.Runnable {
                    super.ProgressBar.isIndeterminate = true
                })
                val briefs = fetchLogBriefsWrapper(cfg, currentSelectTaskHash)


                this@MainApp.availableScriptsLogsStorage = mutableListOf()
                if (briefs.brief.isEmpty()) {
                    availableScriptsLogs = mutableListOf("Empty")
                } else {
                    currentSelectedLog = briefs.brief[0]
                    availableScriptsLogs = mutableListOf()
                    for (b in briefs.brief) {
                        availableScriptsLogsStorage.add(b)
                        availableScriptsLogs.add(b.duration)
                    }
                }
                availableScriptsLogsStorage.reverse()
                availableScriptsLogs.reverse()

                java.awt.EventQueue.invokeLater {
                    refreshScriptsLogsDropdown()
                    super.ProgressBar.isIndeterminate = false
                }
            }
        }
    }

    private fun fetchLogBriefsWrapper(cfg: SimpleConnectionCfg, taskHash: String): TaskBriefsEntity {
        // if parse st_time and ed_time field
        var willUseStOrEdTime = 0
        try {
            stDateField = StTimeTextField.text
            date2instant(stDateField)
            willUseStOrEdTime += 1
        } catch (e: DateTimeParseException) {
            willUseStOrEdTime -= 1
            stDateField
        }

        try {
            edDateField = EdTimeTextField.text
            date2instant(edDateField)
            willUseStOrEdTime += 1
        } catch (e: DateTimeParseException) {
            willUseStOrEdTime -= 1
            edDateField = ""
        }

        var headTime = 1L
        var tailTime = 1L
        if (willUseStOrEdTime > 0) {
            headTime = date2timestamp(stDateField)
            tailTime = date2timestamp(edDateField)
        } else {
            headTime = beforeNow2timestamp(currentRangeSelect)
            tailTime = now2timestamp()
        }

        val jsonStr =
            fetchLogBriefs(cfg, taskHash, headTime, tailTime)
        return JSONMapper.readValue(jsonStr, TaskBriefsEntity::class.java)
    }

    override fun treeMouseClicked(evt: MouseEvent?) {
        if (evt!!.clickCount == 2 && !evt.isConsumed && currentSelectedTreeNode != "Compose Service" && currentSelectedTreeNode.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                java.awt.EventQueue.invokeLater {
                    super.ProgressBar.isIndeterminate = true
                }
                val cfg = super.connReg[super.currentSelectedTreeNode.replace("_++cron", "").replace("_++one", "")]!!
                try {
                    val done = composeLogin(cfg)
                    if (done) {

                        java.awt.EventQueue.invokeLater {
                            super.ProgressBar.isIndeterminate = false
                            ConnStatusLabel.text = "$currentSelectedTreeNode Connected"
                            ConnStatusLabel.foreground = Color(0, 204, 153)
                        }

                        // by default show all cron tasks
                        when {
                            currentSelectedTreeNode.endsWith("_++cron") -> this@MainApp.showCronTasks(cfg)
                            currentSelectedTreeNode.endsWith("_++one") -> this@MainApp.showOneTimeTasks(cfg)
                            else -> this@MainApp.showCronTasks(cfg)
                        }

                    } else {
                        java.awt.EventQueue.invokeLater {
                            super.ProgressBar.isIndeterminate = false
                            ConnStatusLabel.text = "$currentSelectedTreeNode Connect Failed"
                            ConnStatusLabel.foreground = Color(196, 22, 7)
                        }

                    }
                } catch (e: java.net.SocketTimeoutException) {
                    java.awt.EventQueue.invokeLater {
                        super.ProgressBar.isIndeterminate = false
                        ConnStatusLabel.text = "$currentSelectedTreeNode Connect Failed"
                        ConnStatusLabel.foreground = Color(196, 22, 7)
                    }

                }
            }
        }
    }

    private fun showCronTasks(cfg: SimpleConnectionCfg) {
        val jsonStr = listCronTasks(cfg)
        val scripts = JSONMapper.readValue(jsonStr, TasksEntity::class.java)
        allAvailableTaskDetails = scripts.tasks
        splitClustersAndCreateNewTabs(scripts.tasks)

        java.awt.EventQueue.invokeLater {
            super.AnalyzeFilterLogsButton.isEnabled = true
            super.TaskDetailOverviewButton.isEnabled = true
            super.FetchLogBriefButton.isEnabled = true
            super.ViewLogButton.isEnabled = true
            super.RefreshRegsButton.isEnabled = true
        }
    }

    private fun showOneTimeTasks(cfg: SimpleConnectionCfg) {
        // TODO
    }

    fun newTab(title: String, data: Array<Array<String>>) {
        CoroutineScope(Dispatchers.IO).launch {
            java.awt.EventQueue.invokeLater(kotlinx.coroutines.Runnable {
                val scriptsScrollPane = JScrollPane()
                scriptsScrollPane.border = BorderFactory.createCompoundBorder()
                ScriptsTabbedPane.addTab(title, scriptsScrollPane)
                val tableResult = JTable()
                tableResult.tableHeader.defaultRenderer = HeaderRenderer(tableResult)
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
                        currentSelectTaskHash = try {
                            tableResult.model.getValueAt(tableResult.selectedRow, 6).toString()
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            ""
                        }
                        availableScriptsLogs = mutableListOf("Empty")
                        currentSelectedLog = TaskBriefEntity()
                        java.awt.EventQueue.invokeLater {
                            refreshScriptsLogsDropdown()
                        }
                    }
                }

                tableResult.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
                tableResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

                scriptsScrollPane.setViewportView(tableResult)

                // add tooltip text without rendering colors
                val viewport = scriptsScrollPane.viewport
                val tb = viewport.view as JTable
                val tableRows = tb.rowCount
                for (j in 0 until tableRows) {
                    tb.setDefaultRenderer(String::class.java, TableRowsRendererNoColors())
                }
            })


        }
    }
}

