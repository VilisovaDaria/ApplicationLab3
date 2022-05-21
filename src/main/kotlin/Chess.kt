import java.io.FileInputStream
import javafx.scene.image.Image

enum class Colour(var image: Image) {
    BLACK(Image(FileInputStream("src/main/chessBlack.png"), 70.0, 70.0, false, true)),
    WHITE(Image(FileInputStream("src/main/chessWhite.png"), 70.0, 70.0, false, true)),
    GREEN(Image(FileInputStream("src/main/green.png"), 70.0, 70.0, false, true)),
    BLACKQUEEN(Image(FileInputStream("src/main/queenBlack.png"), 70.0, 70.0, false, true)),
    WHITEQUEEN(Image(FileInputStream("src/main/queenWhite.png"), 70.0, 70.0, false, true));
}

class Chess(var x: Int, var y: Int, var colour: Colour?, var isQueen: Boolean = false) {

    private val baseColours = arrayOf(Colour.BLACK, Colour.WHITE)

    fun getXY(): Pair<Int, Int> {
        return Pair(x, y)
    }

    fun changeColour(newColour: Colour?) {
        colour = newColour
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

        for (coefficientY in coefficient) {
            if (y + 2 * coefficientY in 0..7) {
                for (coefficientX in coefficient) {
                    if (x + coefficientX * 2 in 0..7) {
                        val cell = board[x + coefficientX][y + coefficientY]
                        if (cell.colour != colour && cell.colour in baseColours) {
                            if (board[x + coefficientX * 2][y + 2 * coefficientY].colour == null ||
                                board[x + coefficientX * 2][y + 2 * coefficientY].colour == Colour.GREEN
                            ) array += Pair(x + coefficientX * 2, y + 2 * coefficientY)
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
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (coefficientX in coefficient) {
            for (coefficientY in coefficient) {
                for (i in 1..7) {
                    if (x + i * coefficientX in 0..7 && y + i * coefficientY in 0..7) {
                        val cell = board[x + i * coefficientX][y + i * coefficientY]
                        if (cell.colour !in baseColours) {
                            array += Pair(x + i * coefficientX, y + i * coefficientY)
                        } else break
                    }
                }
            }
        }
        return array
    }

    private fun queenAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (coefficientX in coefficient) {
            for (coefficientY in coefficient) {
                var countOfEnemyChips = 0
                var i = 1
                while ((x + i * coefficientX) in 0..7 && (y + i * coefficientY in 0..7)) {

                    val cell = board[x + i * coefficientX][y + i * coefficientY]
                    if (cell.colour in baseColours) {
                        countOfEnemyChips++
                        if (cell.opposite() != colour || countOfEnemyChips >= 2) break

                    } else {
                        if (countOfEnemyChips == 1) {
                            if (cell.colour == null || cell.colour == Colour.GREEN) array += Pair(
                                x + i * coefficientX,
                                y + i * coefficientY
                            )
                        }
                    }
                    i++
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
            if (chip.colour == attackColour) {
                val attack = chip.canAttack(board)
                if (attack.isNotEmpty()) array += Pair(chip, chip.canAttack(board))
            }
        }
    }
    return array
}
