import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme
import me.ckhoidea.scriptcomposeclient.MainApp
import me.ckhoidea.scriptcomposeclient.entity.ComposeServicesEntity
import me.ckhoidea.scriptcomposeclient.utils.YAMLLoader
import java.awt.EventQueue
import java.io.File

fun main() {
    FlatLightFlatIJTheme.setup()
    val yamlCfg = YAMLLoader.readValue(File("./cfg/services.yaml"), ComposeServicesEntity::class.java)
    val app = MainApp(yamlCfg.services)
    app.setLocationRelativeTo(null)
    EventQueue.invokeLater { app.isVisible = true }
}