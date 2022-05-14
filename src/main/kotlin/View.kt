import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.stage.Stage
import java.io.FileInputStream

fun main() {
    Application.launch(MyFirstChart::class.java)
}

class MyFirstChart : Application() {

    override fun start(stage: Stage) {

        val root = Group()
        stage.scene = Scene(root)
        stage.title = "Русские шашки"

        val canvasNew = Canvas(700.0, 700.0)
        root.children.add(canvasNew)

        val gc = canvasNew.graphicsContext2D
        val background = Image(FileInputStream("src/main/board.jpg"))



        fun draw(board: Array<Array<Chess>>) {
            gc.drawImage(background, 0.0, 0.0)
            for (stroke in board) {
                for (chip in stroke) {
                    if (chip.getColor() != null) {
                        var (x, y) = chip.getCell()
                        var x1 = 1
                        if (y % 2 == 1) x1 = 2
                        val image = chip.getColor()!!.getImage()
                        y = (y + 1) * 70
                        x = 70 * (x * 2 + x1)
                        gc.drawImage(image, x.toDouble(), y.toDouble())
                    }
                }
            }
        }


//        fun drawChess() {
//            val blackChess = Colour.BLACK.getImage()
//            val whiteChess = Colour.WHITE.getImage()
//
//
//            for ((i, b) in coordinatesOfChips()) {
//                if (i < 4.0) {
//
//                    gc.drawImage(whiteChess, b.first, b.second)
//                }
//                if (i > 6.0) {
//                    gc.drawImage(blackChess, b.first, b.second)
//                }
//            }
//        }



        var board = fillBoard()
        draw(board)
        stage.show()
    }
}

//заполняю board шашками
fun fillBoard(): Array<Array<Chess>> {
    var board = arrayOf<Array<Chess>>()
    for (x in 0 until 4) {
        var array = arrayOf<Chess>()
        for (y in 0 until 8) {
            array += Chess(x, y, setColor(y))
        }
        board += array
    }
    return board
}

//Определяю цвет для шашки, если в ячейке нет шашки, то null
fun setColor(y: Int): Colour? {
    return when (y) {
        in 0 until 3 -> Colour.WHITE
        in 5 until 8 -> Colour.BLACK
        else -> null
    }
}

fun canMove(board: Array<Array<Chess>>, chess: Chess): Boolean {
    var (x, y) = chess.getCell()
    if (chess.getColor() == Colour.WHITE) y -= 1
    else if (chess.getColor() == Colour.BLACK) y += 1
    else return false //если не будет шашки в клетке

    if (y % 2 == 0) x -= 1

    return board[x + 1][y].getColor() == null || board[x][y].getColor() == null
}


//fun coordinatesOfChips(): Map<Double, Pair<Double, Double>> {
//    val result = mutableMapOf<Double, Pair<Double, Double>>()
//    val coefficient = listOf(1 to 1, 2 to 3, 3 to 5, 4 to 7)
//
//    for ((_, n) in coefficient) {
//        for ((i, k) in coefficient) {
//            result[n + (i.toDouble() / 10.0)] = (70 * k).toDouble() to 70.0 * n
//            result[(n + 1) + ((i + 1).toDouble() / 10.0)] = (70 * (k + 1)).toDouble() to 70.0 * (n + 1)
//        }
//    }
//
//    return result.toSortedMap()
//}
