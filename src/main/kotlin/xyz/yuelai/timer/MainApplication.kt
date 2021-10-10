package xyz.yuelai.timer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL

class MainApplication : Application() {

    private var resource: URL? = null

    private lateinit var controller: MainController
    private lateinit var fxmlLoader: FXMLLoader

    override fun init() {
        controller = MainController()
        resource = MainApplication::class.java.getResource("mainView.fxml")
    }


    override fun start(stage: Stage) {
        fxmlLoader = FXMLLoader(resource)
        fxmlLoader.setController(controller)
        val scene = Scene(fxmlLoader.load())
        scene.stylesheets.add(MainApplication::class.java.getResource("timer.css")?.toExternalForm())
        scene.fill = null
        stage.title = "Timer"
        stage.scene = scene
        stage.sizeToScene()
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.show()
    }

    override fun stop() {
        controller.timer.cancel()
        controller.uiTimer.stop()
    }
}

fun main() {
    Application.launch(MainApplication::class.java)
}