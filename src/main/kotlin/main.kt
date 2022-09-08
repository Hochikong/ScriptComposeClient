import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme
import me.ckho.scriptcomposeclient.MainApp
import me.ckho.scriptcomposeclient.entity.ComposeServicesEntity
import me.ckho.scriptcomposeclient.utils.YAMLLoader
import java.awt.EventQueue
import java.io.File

fun main() {
    FlatLightFlatIJTheme.setup()
    val yamlCfg = YAMLLoader.readValue(File("./cfg/services.yaml"), ComposeServicesEntity::class.java)
    val app = MainApp(yamlCfg.services)
    app.setLocationRelativeTo(null)
    EventQueue.invokeLater { app.isVisible = true }
}