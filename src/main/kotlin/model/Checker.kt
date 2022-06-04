package model

import javafx.scene.image.Image
import utils.*

enum class Colour(var image: Image) {
    BLACK(black),
    WHITE(white),
    GREEN(green);
}

class Checker(var x: Int, var y: Int, var colour: Colour?, var isQueen: Boolean = false) {

    private val baseColours = arrayOf(Colour.BLACK, Colour.WHITE)

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

    fun isReadyToBeQueen() {
        if (y == 0 && colour == Colour.BLACK) changeRang(true)
        if (y == 7 && colour == Colour.WHITE) changeRang(true)
    }

    private fun defaultMove(board: Array<Checker>): Array<Pair<Int, Int>> {
        var y = this.y
        var array = arrayOf<Pair<Int, Int>>()

        if (colour != null) {
            if (colour == Colour.WHITE) {
                y++
            } else y--

            if (x in 1..7 && board[x - 1 + y * 8].colour !in baseColours) array += Pair(x - 1, y)
            if (x in 0..6 && board[x + 1 + y * 8].colour !in baseColours) array += Pair(x + 1, y)

        }

        return array
    }

    private fun queenMove(board: Array<Checker>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (coefficientX in coefficient) {
            for (coefficientY in coefficient) {
                for (i in 1..7) {
                    if (x + i * coefficientX in 0..7 && y + i * coefficientY in 0..7) {
                        val cell = board[x + i * coefficientX + (y + i * coefficientY) * 8]
                        if (cell.colour !in baseColours) {
                            array += Pair(x + i * coefficientX, y + i * coefficientY)
                        } else break
                    }
                }
            }
        }
        return array
    }

    fun canMove(board: Array<Checker>): Array<Pair<Int, Int>> {
        return if (isQueen) queenMove(board)
        else defaultMove(board)
    }

    private fun defaultAttack(board: Array<Checker>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (coefficientY in coefficient) {
            if (y + 2 * coefficientY in 0..7) {
                for (coefficientX in coefficient) {
                    if (x + coefficientX * 2 in 0..7) {
                        val cell = board[x + coefficientX+ (y + coefficientY) * 8]
                        if (cell.colour != colour && cell.colour in baseColours) {
                            if (board[x + coefficientX * 2 + (y + 2 * coefficientY) * 8].colour == null ||
                                board[x + coefficientX * 2 + (y + 2 * coefficientY) * 8].colour == Colour.GREEN
                            ) array += Pair(x + coefficientX * 2, y + 2 * coefficientY)
                        }
                    }
                }
            }
        }
        return array
    }

    private fun queenAttack(board: Array<Checker>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(1, -1)

        for (coefficientX in coefficient) {
            for (coefficientY in coefficient) {
                var countOfEnemyChips = 0
                var i = 1
                while ((x + i * coefficientX) in 0..7 && (y + i * coefficientY in 0..7)) {

                    val cell = board[x + i * coefficientX + (y + i * coefficientY) * 8]
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

    fun canAttack(board: Array<Checker>): Array<Pair<Int, Int>> {
        return if (isQueen) queenAttack(board)
        else defaultAttack(board)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Checker) return false
        return x == other.x && y == other.y && colour == other.colour && isQueen == other.isQueen
    }
}
