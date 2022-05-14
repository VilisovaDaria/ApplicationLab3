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
        val image = Image(FileInputStream("src/main/board.jpg"))
        gc.drawImage(image, 0.0, 0.0)



        fun drawChess() {
            val blackChess = Color.BLACK.getImage()
            val whiteChess = Color.WHITE.getImage()


            for ((i, b) in drawChips()) {
                if (i < 4.0) {

                    gc.drawImage(whiteChess, b.first, b.second)
                }
                if (i > 6.0) {
                    gc.drawImage(blackChess, b.first, b.second)
                }
            }
        }


        //Определяю цвет для шашки, если в ячейке нет шашки, то null
        fun setColor(y: Int): Color? {
            return when (y) {
                in 0 until 3 -> Color.WHITE
                in 5 until 8 -> Color.BLACK
                else -> null
            }
        }

        //заполняю board шашками
        fun fillBoard(): Array<Array<Chess>> {
            var board = arrayOf<Array<Chess>>()
            for (x in 0 until 4) {
                var array = arrayOf<Chess>()
                for (y in 0 until 8) {
                    val a = setColor(y)
                    array += Chess(x, y, a)
                }
                board += array
            }
            return board
        }

        var board = fillBoard()
        drawChess()
        stage.show()
    }

}

fun drawChips(): Map<Double, Pair<Double, Double>> {
    val result = mutableMapOf<Double, Pair<Double, Double>>()
    val coefficient = listOf(1 to 1, 2 to 3, 3 to 5, 4 to 7)

    for ((_, n) in coefficient) {
        for ((i, k) in coefficient) {
            result[n + (i.toDouble() / 10.0)] = (70 * k).toDouble() to 70.0 * n
            result[(n + 1) + ((i + 1).toDouble() / 10.0)] = (70 * (k + 1)).toDouble() to 70.0 * (n + 1)
        }
    }

    return result.toSortedMap()
}