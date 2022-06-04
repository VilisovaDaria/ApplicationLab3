package controller

import model.Checker
import model.Colour
import model.Board
import utils.*
import javafx.scene.image.Image
import kotlin.system.exitProcess

const val notReady = -1

class Controller {
    private var board = Board()
    private var countBlack = 12
    private var countWhite = 12
    private var game = true
    private var readyChecker = Checker(notReady, notReady, Colour.WHITE)

    fun createBoard() {
        board.fillBoard()
    }

    private fun getBoard(): Array<Checker> {
        return board.cells
    }

    fun getSourcesToRepaint(): Array<Triple<Image, Double, Double>> {
        var sources = arrayOf<Triple<Image, Double, Double>>()
        for (cell in getBoard()){
            if (cell.colour != null) {
                val image = if (cell.isQueen) {
                    when (cell.colour!!.image) {
                        white -> whiteQueen
                        black -> blackQueen
                        else -> cell.colour!!.image
                    }
                } else cell.colour!!.image
                val y = (cell.y + 1) * 70
                val x = 70 * (cell.x + 1)
                sources += Triple(image, x.toDouble(), y.toDouble())
            }
        }
        return sources
    }

    fun restart() {
        countBlack = 12
        countWhite = 12
        game = true
        board = Board()
        board.fillBoard()
        readyChecker = Checker(notReady, notReady, Colour.WHITE)
    }

    fun clickOnMouse(coordinateX: Double, coordinateY: Double) {
        val (x, y) = getCellFromClick(coordinateX, coordinateY)

        if (game) {
            val cell = board.getCell(x, y)
            val attackColour = readyChecker.colour

            val attackCells = board.canAttackAround(attackColour!!, getBoard())

            if (attackCells.isNotEmpty()) {
                for (checkerInfo in attackCells) {

                    val checkerCanAttack = checkerInfo.first
                    val cellsCanMove = checkerInfo.second

                    if (cell == checkerCanAttack) isActionReady(cell, cellsCanMove)
                    else if (cell.colour == Colour.GREEN) {

                        if (readyChecker.colour == Colour.WHITE) countBlack--
                        else countWhite--

                        board.eat(cellsCanMove, cell, readyChecker)
                        readyChecker = board.continueAttack(cell)
                    }
                }
            } else
                if (cell.colour != Colour.GREEN) {
                    if (attackColour == cell.colour) {
                        val cellsCanMove = cell.canMove(getBoard())
                        if (cellsCanMove.isNotEmpty()) isActionReady(cell, cellsCanMove)
                    }
                } else {
                    board.changeColorInCells(readyChecker.canMove(getBoard()), null)
                    board.move(cell, readyChecker)

                    readyChecker = Checker(notReady, notReady, cell.opposite())

                }
        }
    }

    private fun isActionReady(
        selectedCell: Checker,
        moves: Array<Pair<Int, Int>>,
    ) {
        if (readyChecker.x == notReady) { //если ни одна клетка не выделена
            readyChecker = selectedCell //выделяем её
            board.changeColorInCells(moves, Colour.GREEN)

        } else //повторное нажатие, если же выделена клетка
            if (selectedCell == readyChecker) { //если нажали на выделенную клетку
                readyChecker = Checker(notReady, notReady, selectedCell.colour) //отменяем выделение
                board.changeColorInCells(moves, null)
            }
    }

    private fun getCellFromClick(x: Double, y: Double): Pair<Int, Int> {
        if (x in 70.0..630.0 && y in 70.0..630.0) {
            val cellX = (x.toInt() - 70) / 70
            val cellY = (y.toInt() - 70) / 70
            return Pair(cellX, cellY)
        }
        return Pair(0, 0)
    }

    fun determineWinner(): Image? {
        return if (countWhite == 0) {
            game = false
            blackWin
        } else if (countBlack == 0) {
            game = false
            whiteWin
        } else null
    }
}
