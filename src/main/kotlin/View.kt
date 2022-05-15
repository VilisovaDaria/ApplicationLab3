import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import java.io.FileInputStream


fun main() {
    Application.launch(MyFirstChart::class.java)
}

class MyFirstChart : Application() {

    override fun start(stage: Stage) {
        stage.isResizable = false
        val root = Group()
        stage.scene = Scene(root)
        stage.title = "Русские шашки"


        val canvasNew = Canvas(700.0, 700.0)
        root.children.add(canvasNew)

        val gc = canvasNew.graphicsContext2D
        val background = Image(FileInputStream("src/main/board.jpg"))

        fun repaintScene(vararg chess: Chess) {
            fillBoard()
            gc.drawImage(background, 0.0, 0.0)

            for (cell in board) {
                chess.forEach {
                    if (it.colour != null) {
                        var (x, y) = it.getCell()
                        var x1 = 1
                        if (y % 2 == 1) x1 = 2
                        val image = it.colour!!.getImage()
                        y = (y + 1) * 70
                        x = 70 * (x * 2 + x1)
                        gc.drawImage(image, x.toDouble(), y.toDouble())
                    }
                }
            }
        }

        fun paintScene() {
            var array = arrayOf<Chess>()
            fillBoard().forEach { array += it }
            repaintScene(*array)
        }

        paintScene()
        stage.show()
    }
}