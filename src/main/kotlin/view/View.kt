package view

import workWithResourses.*
import controller.Controller
import controller.notReady
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import tornadofx.add
import kotlin.system.exitProcess

fun main() {
    Application.launch(App::class.java)
}

open class App : Application() {

    override fun start(stage: Stage) {
        stage.isResizable = false


        val viewFirst = ImageView(exit)
        viewFirst.fitHeight = 35.0
        viewFirst.isPreserveRatio = true
        val buttonForExit = Button()
        buttonForExit.translateX = 620.0
        buttonForExit.translateY = 10.0
        buttonForExit.graphic = viewFirst

        val viewSecond = ImageView(restart)
        viewSecond.fitHeight = 35.0
        viewSecond.isPreserveRatio = true
        val buttonForRestart = Button()
        buttonForRestart.translateX = 10.0
        buttonForRestart.translateY = 10.0
        buttonForRestart.graphic = viewSecond

        val root = Group(buttonForRestart, buttonForExit)
        stage.scene = Scene(root)
        stage.title = "Русские шашки"
        val canvasNew = Canvas(700.0, 700.0)
        val d = AnchorPane(canvasNew)
        val dfirst = d.add(buttonForExit)
        val dsecond = d.add(buttonForRestart)
        root.children.add(d)

        val gc = canvasNew.graphicsContext2D
        val controller = Controller()
        controller.createBoard()

        fun repaintScene() {
            gc.drawImage(background, 0.0, 0.0)
            for ((image, x, y) in controller.getSourcesToRepaint()) {
                gc.drawImage(image, x, y)
            }
        }

        buttonForExit.onMouseClicked = EventHandler { exitProcess(0) }

        buttonForRestart.onMouseClicked = EventHandler {event ->
            controller.restart()
            repaintScene()
        }

        stage.scene.onMousePressed =
            EventHandler { event ->
                controller.clickOnMouse(event.sceneX, event.sceneY)

                val winImage = controller.whoDidWin()
                if (winImage != null) {
                    gc.drawImage(winImage, 0.0, 0.0)
                } else
                    repaintScene()

            }
        repaintScene()
        stage.show()
    }
}