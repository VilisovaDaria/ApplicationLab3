import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Stage
import tornadofx.pause
import tornadofx.toProperty
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

        fun paintScene(board: Array<Array<Chess>>) {
            gc.drawImage(background, 0.0, 0.0)
            for (stroke in board) {
                for (chip in stroke) {
                    var (x, y) = chip.getCell()
                    if (chip.getColour(x, y) != null) {
                        var x1 = 1
                        if (y % 2 == 1) x1 = 2
                        val image = chip.getColour(x, y)!!.getImage()
                        y = (y + 1) * 70
                        x = 70 * (x * 2 + x1)
                        gc.drawImage(image, x.toDouble(), y.toDouble())
                    }
                }
            }
        }

        val board = fillBoard()
        board[0][0] = Chess(3, 5, Colour.BLACK) // board - откуда берем шашку, а chess куда ставим

        paintScene(board)
        stage.show()
    }

}