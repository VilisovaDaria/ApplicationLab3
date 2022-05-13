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

class MyFirstChart: Application() {

    override fun start(stage: Stage) {

        val root = Group()
        stage.scene = Scene(root)
        stage.title = "Русские шашки"

        val canvasNew = Canvas(700.0, 700.0)
        root.children.add(canvasNew)

        val gc = canvasNew.graphicsContext2D
        val image = Image(FileInputStream("src/main/board.jpg"))
        gc.drawImage(image, 0.0, 0.0)

        fun firstArrangement() {
            val imBlack = Image(FileInputStream("src/main/chessBlack.png"))
            val imWhite = Image(FileInputStream("src/main/chessWhite.png"))

            getCoordinates()
            for ((i, b) in getCoordinates()) {
                if (i < 4.0) {
                    gc.drawImage(imWhite, b.first, b.second)
                }
                if (i > 6.0) {
                    gc.drawImage(imBlack, b.first, b.second)
                }
            }
        }

        firstArrangement()
        stage.show()
    }

}

fun getCoordinates(): Map<Double, Pair<Double, Double>> {
    val x = 0
    val result = mutableMapOf<Double, Pair<Double, Double>>()
    val coefficientsFirst = listOf(1 to 1, 2 to 3, 3 to 5, 4 to 7)
    val coefficientsSecond = listOf(2 to 2, 3 to 4, 4 to 6, 5 to 8)

    for ((m,n) in coefficientsFirst) {
        for ((i, k) in coefficientsFirst) {
            result[n + (i.toDouble() / 10.0)] = (70 * k).toDouble() to 70.0 * n
        }
    }
    for ((m,n) in coefficientsSecond) {
        for ((i, k) in coefficientsSecond) {
            result[n + ((i-1).toDouble() / 10.0)] = (70 * k).toDouble() to 70.0 * n
        }
    }
    return result.toSortedMap()
}


//111111111111111111111111