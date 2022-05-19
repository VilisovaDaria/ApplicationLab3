import java.io.FileInputStream
import javafx.scene.image.Image

enum class Colour(private val image: Image) {
    BLACK(Image(FileInputStream("src/main/chessBlack.png"))),
    WHITE(Image(FileInputStream("src/main/chessWhite.png"))),
    GREEN(Image(FileInputStream("src/main/green.png")));

    @JvmName("getImage1")
    fun getImage(): Image {
        return image
    }
}

class Chess(var x: Int, var y: Int, var colour: Colour?) {

    private val baseColours = arrayOf(Colour.BLACK, Colour.WHITE)

    fun getXY(): Pair<Int, Int> {
        return Pair(x, y)
    }

    @JvmName("getColour1")
    fun getColour(): Colour? {
        return colour
    }

    fun changeColour(newColour: Colour?) {
        colour = newColour
    }

    fun opposite(): Colour {
        return if (colour == Colour.WHITE) Colour.BLACK
        else Colour.WHITE
    }

    fun canMove(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var y = this.y
        var array = arrayOf<Pair<Int, Int>>()

        if (colour != null) {
            if (colour == Colour.WHITE) {
                y++
            } else y--

            if (x in 1..7 && board[x - 1][y].colour !in baseColours) array += Pair(x - 1, y)
            if (x in 0..6 && board[x + 1][y].colour !in baseColours) array += Pair(x + 1, y)

        } else println("Это белая клетка")

        return array
    }

    fun canAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()

        val coefficient = listOf(1, -1)
        for (i in coefficient) {
            if (y + 2 * i in 0..7) {
                if (x in 2..7) {
                    val cell = board[x - 1][y + i]
                    if (cell.colour != colour && cell.colour in baseColours) {
                        if (board[x - 2][y + 2 * i].getColour() == null ||
                            board[x - 2][y + 2 * i].getColour() == Colour.GREEN
                        ) array += Pair(x - 2, y + 2 * i)
                    }
                }
                if (x in 0..5) {
                    val cell = board[x + 1][y + i]
                    if (cell.colour != colour && cell.colour in baseColours) {
                        if (board[x + 2][y + 2 * i].getColour() == null ||
                            board[x + 2][y + 2 * i].getColour() == Colour.GREEN
                        ) array += Pair(x + 2, y + 2 * i)
                    }
                }
            }
        }
        return array
    }
}

fun canAttackAround(attackColour: Colour, board: Array<Array<Chess>>): Array<Pair<Chess, Array<Pair<Int, Int>>>> {
    var array = arrayOf<Pair<Chess, Array<Pair<Int, Int>>>>()

    for (stroke in board) {
        for (chip in stroke) {
            if (chip.getColour() == attackColour) {
                val attack = chip.canAttack(board)
                if (attack.isNotEmpty()) array += Pair(chip, chip.canAttack(board))
            }
        }
    }
    return array
}

fun isInside(x: Int, y: Int): Boolean = (x in 0..7 && y in 0..7)