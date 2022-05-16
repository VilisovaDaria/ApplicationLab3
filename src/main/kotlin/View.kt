import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
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



        fun repaintScene(board: Array<Array<Chess>>) {
            gc.drawImage(background, 0.0, 0.0)
            for (stroke in board) {
                for (chip in stroke) {
                    var (x, y) = chip.getXY()
                    if (chip.getColour() != null) {
                        val image = chip.getColour()!!.getImage()
                        y = (y + 1) * 70
                        x = 70 * (x + 1)
                        gc.drawImage(image, x.toDouble(), y.toDouble())
                    }
                }
            }
        }

        val board = fillBoard()

        stage.scene.onMousePressed =
            EventHandler<MouseEvent> { event ->
                val (x, y) = cellCoordinatesFromClick(event.sceneX, event.sceneY)
                val cellsCanMove = board[x][y].canMove(board)
                for ((x, y) in cellsCanMove){
                    board[x][y].changeColour(Colour.GREEN)
                }
                repaintScene(board)
            }

        repaintScene(board)
        stage.show()
    }

    fun cellCoordinatesFromClick(x: Double, y: Double): Pair<Int, Int> {
        if (x in 70.0..630.0 && y in 70.0..630.0){
            val cellX = (x.toInt() - 70) / 70
            val cellY = (y.toInt() - 70) / 70
            return Pair(cellX, cellY)
        }
        return Pair(0, 0)
    }
}

