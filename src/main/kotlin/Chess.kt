import java.io.FileInputStream
import javafx.scene.image.Image
import kotlin.math.abs

enum class Colour(private val image: Image) {
    BLACK(Image(FileInputStream("src/main/chessBlack.png"))),
    WHITE(Image(FileInputStream("src/main/chessWhite.png")));

    @JvmName("getImage1")
    fun getImage(): Image {
        return image
    }
}

class Chess(var x: Int, var y: Int, var colour: Colour?) {

    fun getCell(): Pair<Int, Int> {
        return Pair(x, y)
    }

    fun getColour(x: Int, y: Int): Colour? {
        return colour
    }

    fun isCellEmpty(x: Int, y: Int): Boolean {
        return getColour(x, y) == null
    }


    fun checkerMove(chess: Chess): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()

        if (chess.x in 1..3) {
            if (chess.y % 2 == 0) result.add(chess.x - 1 to chess.y + 1)
            else result.add(chess.x + 1 to chess.y + 1)
        }

        result.add(chess.x to chess.y + 1)

        result.forEach {
            if (isCellEmpty(it.first, it.second)) return listOf(it)
        }

        return listOf()
    }


    fun checkerMoveAttack(x: Int, y: Int, chess: Chess): Pair<Int, Int>? {
        val (xInactive, yInactive) = chess.getCell()
        val xAttack = x
        val yAttack = y
        val colorAttack = getColour(xAttack, yAttack)
        val colorInactive = getColour(xInactive, yInactive)

        if (isCellEmpty(xAttack, yAttack)) throw IllegalArgumentException("Chess does not exist")

        if (colorAttack != colorInactive) {

            if (checkerMove(chess).isNotEmpty()) { //проверяем от 2 шашки, которую предположительно будем бить, куда она может пойти
                // так как 1 из ходов является нужными координатами

                checkerMove(chess)[0]  // нужная пара под 0 индексом в общем случае, но нужна проверка в отличии координат х

                if (abs(checkerMove(chess)[0].first - xAttack) == 2) return checkerMove(chess)[0]
            }
        }
        return null
    }

    fun teleport(x: Int, y: Int) {
        if (checkerMove(Chess(x, y, colour)).isNotEmpty())  {
            this.x = x
            this.y = y
        }
    }
}
