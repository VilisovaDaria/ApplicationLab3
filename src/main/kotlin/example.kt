import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import java.io.FileInputStream

class CanvasDemoController {

    var canvas = Canvas(700.0, 700.0)
    var d = Chess()

    private val gc = canvas.graphicsContext2D
    private val background = Image(FileInputStream("src/main/board.jpg"))

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

    //рассмотрим только move
    fun repaint(chess: Chess) {
        board = fillBoard()
        val (x, y) = chess.checkerMove(chess)[0]
        val (x1, y1) = chess.checkerMove(chess)[1]

        // board - откуда берем шашку, а chess куда ставим
        board[chess.x][chess.y] = Chess(x, y, chess.colour)
        board[chess.x][chess.y] = Chess(x1, y1, chess.colour)
    }


    fun initialize() {
        canvas!!.onMouseClicked = EventHandler { event: MouseEvent ->
            d.teleport(event.x, event.y)
            refresh()
        }
    }
}
