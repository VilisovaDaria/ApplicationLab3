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
        board = fillBoard()

        fun repaintScene(board: Array<Array<Checker>>) {
            gc.drawImage(background, 0.0, 0.0)
            gc.drawImage(restart, 10.0, 10.0)
            gc.drawImage(exit, 620.0, 10.0)
            for (stroke in board) {
                for (checker in stroke) {
                    var (x, y) = checker.getXY()
                    if (checker.colour != null) {
                        val image = if (checker.isQueen) {
                            when (checker.colour) {
                                Colour.WHITE -> whiteQueen
                                Colour.BLACK -> blackQueen
                                else -> checker.colour!!.image
                            }
                        } else checker.colour!!.image
                        y = (y + 1) * 70
                        x = 70 * (x + 1)
                        gc.drawImage(image, x.toDouble(), y.toDouble())
                    }
                }
            }
        }

        stage.scene.onMousePressed =
            EventHandler { event ->
                mouseEvent(event.sceneX, event.sceneY)

                if (countWhite == 0) {
                    gc.drawImage(blackWin, 0.0, 0.0)
                    gc.drawImage(restart, 0.0, 0.0)
                    gc.drawImage(exit, 630.0, 0.0)
                    game = false
                } else if (countBlack == 0) {
                    game = false
                    gc.drawImage(whiteWin, 0.0, 0.0)
                    gc.drawImage(restart, 0.0, 0.0)
                    gc.drawImage(exit, 630.0, 0.0)
                } else
                    repaintScene(board)

            }
        repaintScene(board)
        stage.show()
    }
}