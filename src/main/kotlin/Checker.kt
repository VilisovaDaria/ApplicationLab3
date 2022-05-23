import java.io.FileInputStream
import javafx.scene.image.Image
import java.nio.file.Paths

class ResourceReader {
    fun pathResource(fileName: String): String {
        val uri = this.javaClass.getResource("/$fileName")!!.toURI()
        return Paths.get(uri).toString()
    }
}

fun getImage(fileName: String,height: Double, width: Double): Image {
    val reader = ResourceReader()
    return Image(FileInputStream(reader.pathResource(fileName)), height, width, false, true)
}

val black = getImage("chessBlack.png", 70.0, 70.0)
val white = getImage("chessWhite.png", 70.0, 70.0)
val green = getImage("green.png", 70.0, 70.0)
val blackQueen = getImage("queenBlack.png", 70.0, 70.0)
val whiteQueen = getImage("queenWhite.png", 70.0, 70.0)
val background = getImage("board.jpg", 700.0, 700.0)
val restart = getImage("restart.png", 70.0, 40.0)
val exit = getImage("exit.png", 70.0, 40.0)
val whiteWin = getImage("whiteWin.png", 700.0, 700.0)
val blackWin = getImage("blackWin.png", 700.0, 700.0)


enum class Colour(var image: Image) {
    BLACK(black),
    WHITE(white),
    GREEN(green),
    BLACKQUEEN(blackQueen),
    WHITEQUEEN(whiteQueen);
}

class Checker(var x: Int, var y: Int, var colour: Colour?, var isQueen: Boolean = false) {

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

    private fun defaultMove(board: Array<Array<Checker>>): Array<Pair<Int, Int>> {
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

    fun canMove(board: Array<Array<Checker>>): Array<Pair<Int, Int>> {
        return if (isQueen) queenMove(board)
        else defaultMove(board)
    }

    private fun defaultAttack(board: Array<Array<Checker>>): Array<Pair<Int, Int>> {
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

    fun canAttack(board: Array<Array<Checker>>): Array<Pair<Int, Int>> {
        return if (isQueen) queenAttack(board)
        else defaultAttack(board)
    }


    private fun queenMove(board: Array<Array<Checker>>): Array<Pair<Int, Int>> {
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

    fun queenAttack(board: Array<Array<Checker>>): Array<Pair<Int, Int>> {
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

fun canAttackAround(attackColour: Colour, board: Array<Array<Checker>>): Array<Pair<Checker, Array<Pair<Int, Int>>>> {
    var array = arrayOf<Pair<Checker, Array<Pair<Int, Int>>>>()

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
