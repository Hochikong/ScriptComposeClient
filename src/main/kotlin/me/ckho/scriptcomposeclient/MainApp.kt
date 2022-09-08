package me.ckho.scriptcomposeclient

import me.ckho.USwingGUI.codegen.impMainFrame
import me.ckho.USwingGUI.entity.SimpleConnectionCfg
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.Color
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean
import java.net.CookieManager
import java.net.CookiePolicy
import javax.swing.JOptionPane
import kotlin.system.exitProcess


class MainApp(connReg: List<SimpleConnectionCfg>) : impMainFrame(connReg) {
    // must set cookiejar, or can't log in to script-compose
    private val cookieManager = CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ALL) }
    private val client: OkHttpClient = OkHttpClient().newBuilder().cookieJar(JavaNetCookieJar(cookieManager)).build()

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
        super.newTab("SB")
        super.updateRowColor()
    }

    override fun treeMouseClicked(evt: MouseEvent?) {
        if (evt!!.clickCount == 2 && !evt.isConsumed && currentSelectedTreeNode != "Compose Service") {
            val cfg = super.connReg[super.currentSelectedTreeNode]!!
            if (composeLogin(cfg)) {
                ConnStatusLabel.text = currentSelectedTreeNode + " Connected"
                ConnStatusLabel.foreground = Color(0, 204, 153)

                this.listCronTasks(cfg)
            } else {
                ConnStatusLabel.text = currentSelectedTreeNode + " Connect Failed"
                ConnStatusLabel.foreground = Color(196, 22, 7)
            }

        }
    }


    // logic
    private fun composeLogin(cfg: SimpleConnectionCfg): Boolean {

        val reqPayload = FormBody.Builder()
            .add("username", cfg.username)
            .add("password", cfg.password)
            .build()
        val req = Request.Builder()
            .url(cfg.url + "/login")
            .post(reqPayload)
            .build()
        val response = client.newCall(req).execute()
        val homePage = response.body!!.string()
        return "Welcome to use Scripts Composer" in homePage
    }

    private fun listCronTasks(cfg: SimpleConnectionCfg) {
        val urlParams = (cfg.url + "/tasks/allTasks/byType").toHttpUrl().newBuilder()
            .addQueryParameter("type", "cron")
            .build()
        print(urlParams)

        val req = Request.Builder()
            .url(urlParams)
            .get()
            .build()
        val response = client.newCall(req).execute()
        val data = response.body!!.string()
        print(data)
    }
}

