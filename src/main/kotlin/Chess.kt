import java.io.FileInputStream
import javafx.scene.image.Image

enum class Colour(private val image: Image) {
    BLACK(Image(FileInputStream("src/main/chessBlack.png"))),
    WHITE(Image(FileInputStream("src/main/chessWhite.png"))),
    GREEN(Image(FileInputStream("src/main/green.png"))),
    BLACKQUEEN(Image(FileInputStream("src/main/queenBlack.png"))),
    WHITEQUEEN(Image(FileInputStream("src/main/queenWhite.png")));


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

        }

        return array
    }

    fun canAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (i in coefficient) {
            if (y + 2 * i in 0..7) {
                for (j in coefficient) {
                    if (x + j * 2 in 0..7) {
                        val cell = board[x + j][y + i]
                        if (cell.colour != colour && cell.colour in baseColours) {
                            if (board[x + j * 2][y + 2 * i].getColour() == null ||
                                board[x + j * 2][y + 2 * i].getColour() == Colour.GREEN
                            ) array += Pair(x + j * 2, y + 2 * i)
                        }
                    }
                }
            }
        }
        return array
    }


    fun queenAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (i in 0..7) {
            for (k in coefficient) {
                for (j in coefficient) {
                    if (x + i * j in 0..7 && y + i * k in 0..7 && x + i in 0..7 && y + i in 0..7) {
                        val cell = board[x + i * j][y + i * k]
                        if (cell.colour == null || cell.colour == Colour.GREEN)
                            array += Pair(x + i * j, y + i * k)
                        if (cell.colour == colour) break
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
