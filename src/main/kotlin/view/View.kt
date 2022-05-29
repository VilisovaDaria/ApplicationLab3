package view

import workWithResourses.*
import controller.Controller
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.stage.Stage

fun main() {
    Application.launch(App::class.java)
}

class App : Application() {

    override fun start(stage: Stage) {
        stage.isResizable = false
        val root = Group()
        stage.scene = Scene(root)
        stage.title = "Русские шашки"
        val canvasNew = Canvas(700.0, 700.0)
        root.children.add(canvasNew)

        val gc = canvasNew.graphicsContext2D
        val controller = Controller()
        controller.createBoard()

        fun repaintScene() {
            gc.drawImage(background, 0.0, 0.0)
            gc.drawImage(restart, 10.0, 10.0)
            gc.drawImage(exit, 620.0, 10.0)
            for (cell in controller.getBoard()){
                if (cell.colour != null) {
                        val image = if (cell.isQueen) {
                            when (cell.colour!!.image) {
                                white -> whiteQueen
                                black -> blackQueen
                                else -> cell.colour!!.image
                            }
                        } else cell.colour!!.image
                        val y = (cell.y + 1) * 70
                        val x = 70 * (cell.x + 1)
                        gc.drawImage(image, x.toDouble(), y.toDouble())
                    }
            }
        }

        stage.scene.onMousePressed =
            EventHandler { event ->
                controller.clickOnMouse(event.sceneX, event.sceneY)

                val winImage = controller.whoDidWin()
                if (winImage != null){
                    gc.drawImage(winImage, 0.0, 0.0)
                    gc.drawImage(restart, 0.0, 0.0)
                    gc.drawImage(exit, 630.0, 0.0)
                } else
                    repaintScene()

            }
        repaintScene()
        stage.show()
    }
}