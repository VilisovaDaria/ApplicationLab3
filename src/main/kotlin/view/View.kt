package view

import utils.*
import controller.Controller
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.scene.image.ImageView
import kotlin.system.exitProcess


fun main() {
    Application.launch(App::class.java)
}

open class App : Application() {

    override fun start(stage: Stage) {
        stage.isResizable = false
        val root = Group()
        stage.scene = Scene(root)
        stage.title = "Русские шашки"
        val canvasNew = Canvas(700.0, 700.0)
        root.children.add(canvasNew)

        val buttonRestart = Button("", ImageView(restart))
        buttonRestart.style = "-fx-background-color: transparent;"
        buttonRestart.layoutX = 6.0
        buttonRestart.layoutY = 10.0

        val buttonExit = Button("", ImageView(exit))
        buttonExit.style = "-fx-background-color: transparent;"
        buttonExit.layoutX = 610.0
        buttonExit.layoutY = 10.0


        root.children.add(buttonRestart)
        root.children.add(buttonExit)

        val gc = canvasNew.graphicsContext2D
        val controller = Controller()
        controller.createBoard()


        fun repaintScene() {
            gc.drawImage(background, 0.0, 0.0)
            for ((image, x, y) in controller.getSourcesToRepaint()) {
                gc.drawImage(image, x, y)
            }
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

        buttonRestart.onMouseClicked = EventHandler {
            controller.restart()
            repaintScene()
        }

        buttonExit.onMouseClicked = EventHandler { exitProcess(0) }

        repaintScene()
        stage.show()
    }
}