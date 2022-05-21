import java.io.FileInputStream
import javafx.scene.image.Image

enum class Colour(var image: Image) {
    BLACK(Image(FileInputStream("src/main/chessBlack.png"))),
    WHITE(Image(FileInputStream("src/main/chessWhite.png"))),
    GREEN(Image(FileInputStream("src/main/green.png"))),
    BLACKQUEEN(Image(FileInputStream("src/main/queenBlack.png"))),
    WHITEQUEEN(Image(FileInputStream("src/main/queenWhite.png")));
}

class Chess(var x: Int, var y: Int, var colour: Colour?, var isQueen: Boolean = false) {

    private val baseColours = arrayOf(Colour.BLACK, Colour.WHITE)

    fun getXY(): Pair<Int, Int> {
        return Pair(x, y)
    }

    fun changeColour(newColour: Colour?): Colour? {
        colour = newColour
        return colour
    }

    fun changeColourForQueen(colour: Colour): Colour? {
        if (colour == Colour.BLACK) return changeColour(Colour.BLACKQUEEN)
        if (colour == Colour.WHITE) return changeColour(Colour.WHITEQUEEN)
        else return null
    }

    fun opposite(): Colour {
        return if (colour == Colour.WHITE) Colour.BLACK
        else Colour.WHITE
    }

    fun changeRang(newRang: Boolean) {
        isQueen = newRang
    }

    private fun defaultMove(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
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

    fun canMove(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        return if (isQueen) queenMove(board)
        else defaultMove(board)
    }

    private fun defaultAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (i in coefficient) {
            if (y + 2 * i in 0..7) {
                for (j in coefficient) {
                    if (x + j * 2 in 0..7) {
                        val cell = board[x + j][y + i]
                        if (cell.colour != colour && cell.colour in baseColours) {
                            if (board[x + j * 2][y + 2 * i].colour == null ||
                                board[x + j * 2][y + 2 * i].colour == Colour.GREEN
                            ) array += Pair(x + j * 2, y + 2 * i)
                        }
                    }
                }
            }
        }
        return array
    }

    fun canAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        return if (isQueen) queenAttack(board)
        else defaultAttack(board)
    }

    private fun queenMove(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        if (board[x][y].colour in baseColours) board[x][y].changeColourForQueen(colour!!)

        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (j in coefficient) {
            for (k in coefficient) {
                for (i in 1..7) {
                    if (x + i * j in 0..7 && y + i * k in 0..7) {
                        val cell = board[x + i * j][y + i * k]
                        if (cell.colour !in baseColours) {
                            array += Pair(x + i * j, y + i * k)
                        } else break
                    }
                }
            }
        }

        return array
    }

    private fun queenAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        if (board[x][y].colour in baseColours) board[x][y].changeColourForQueen(colour!!)

        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)


        for (j in coefficient) {
            for (k in coefficient) {
                for (i in 1..7) {
                    if ((x + (i + 1) * j) in 0..7 && (y + (i + 1) * k in 0..7)) {
                        val cell = board[x + i * j][y + i * k]
                        if (cell.opposite() == colour) {
                            if (board[x + (i + 1) * j][y + (i + 1) * k].colour == null ||
                                board[x + (i + 1) * j][y + (i + 1) * k].colour == Colour.GREEN
                            ) {
                                array += Pair(x + (i + 1) * j, y + (i + 1) * k)
                                break
                            } else break
                        } else break
                    } else break
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
            if (chip.changeColourForQueen(chip.colour!!) == Colour.BLACKQUEEN && attackColour == Colour.BLACK ||
            chip.changeColourForQueen(chip.colour!!) == Colour.WHITEQUEEN && attackColour == Colour.WHITE
                    ) {
                val attack = chip.canAttack(board)
                if (attack.isNotEmpty()) array += Pair(chip, chip.canAttack(board))
            }
        }
    }
    return array
}
