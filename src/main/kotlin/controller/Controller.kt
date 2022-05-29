package controller

import model.Checker
import model.Colour
import workWithResourses.*
import javafx.scene.image.Image
import model.Board
import kotlin.system.exitProcess


class Controller {
    private var board = Board()
    private var countBlack = 12
    private var countWhite = 12
    private var game = true
    private var ready = mutableListOf<Any?>(Checker(-1, -1, Colour.WHITE), false) //показывает, выделена ли какая-то клетка

    fun createBoard() {
        board.fillBoard()
    }

    fun getBoard(): Array<Checker> {
        return board.cells
    }

    fun exit(x: Double, y: Double) {
        if (x in 620.0..690.0 && y in 10.0..50.0) exitProcess(0)
    }

    fun restart(x: Double, y: Double) {
        if (x in 10.0..80.0 && y in 10.0..50.0) {
            countBlack = 12
            countWhite = 12
            game = true
            board = Board()
            board.fillBoard()
            ready = mutableListOf(Checker(-1, -1, Colour.WHITE), false)
        }
        println("$countBlack - $countWhite - $game - готовность шашки - $ready")
        println((ready[0] as Checker).colour)
    }

    fun clickOnMouse(coordinateX: Double, coordinateY: Double) {
        exit(coordinateX, coordinateY)
        restart(coordinateX, coordinateY)

        val (x, y) = cellCoordinatesFromClick(coordinateX, coordinateY)

        if (game) {
            val cell = board.getCell(x, y)
            val readyChecker = (ready[0] as Checker)
            val attackColour = readyChecker.colour

            val attackCells = canAttackAround(attackColour!!, getBoard())

            if (attackCells.isNotEmpty()) {
                for (checkerInfo in attackCells) {

                    val checkerCanAttack = checkerInfo.first
                    val cellsCanMove = checkerInfo.second

                    if (cell == checkerCanAttack) actionReady(cell, cellsCanMove)
                    else if (cell.colour == Colour.GREEN) {

                        if (readyChecker.colour == Colour.WHITE) countBlack--
                        else countWhite--

                        eat(cellsCanMove, cell, readyChecker)
                        ready = continueAttack(cell)
                    }
                }
            } else
                if (cell.colour != Colour.GREEN) {
                    if (attackColour == cell.colour) {
                        val cellsCanMove = cell.canMove(getBoard())
                        if (cellsCanMove.isNotEmpty()) actionReady(cell, cellsCanMove)
                    }
                } else {
                    changeColorInCells(readyChecker.canMove(getBoard()), null)
                    move(cell, readyChecker)

                    ready = mutableListOf(
                        Checker(-1, -1, cell.opposite()),
                        false
                    )
                }
        }
    }

    fun actionReady(
        selectedCell: Checker,
        moves: Array<Pair<Int, Int>>,
    ) {
        if (!(ready[1] as Boolean)) { //если ни одна клетка не выделена
            ready = mutableListOf(selectedCell, true) //выделяем её
            changeColorInCells(moves, Colour.GREEN)

        } else //повторное нажатие, если же выделена клетка
            if (selectedCell == ready[0] as Checker) { //если нажали на выделенную клетку
                ready =
                    mutableListOf(
                        Checker(-1, -1, selectedCell.colour),
                        false
                    ) //отменяем выделение
                changeColorInCells(moves, null)
            }
    }





    fun eat(
        moves: Array<Pair<Int, Int>>,
        toCell: Checker,
        fromCell: Checker
    ) {
        var cellsToRepaint = moves
        var step = 1
        val coefficientX = (toCell.x - fromCell.x) / (kotlin.math.abs((toCell.x - fromCell.x)))
        val coefficientY = (toCell.y - fromCell.y) / (kotlin.math.abs((toCell.y - fromCell.y)))

        while (fromCell.x + step * coefficientX != toCell.x && fromCell.y + step * coefficientY != toCell.y) {
            cellsToRepaint += Pair(fromCell.x + step * coefficientX, fromCell.y + step * coefficientY)
            step++
        }

        changeColorInCells(cellsToRepaint, null)
        move(toCell, fromCell)
    }

    fun continueAttack(selectedCell: Checker): MutableList<Any?> {
        val canAttack = selectedCell.canAttack(getBoard())
        val ready: MutableList<Any?>
        if (canAttack.isEmpty()) {
            ready = mutableListOf(
                Checker(-1, -1, selectedCell.opposite()),
                false
            ) //отменяю выделение клетки, меняю цвет следующей ходящей
        } else {
            ready = mutableListOf(selectedCell, true)
            changeColorInCells(canAttack, Colour.GREEN)
        }

        return ready
    }

    fun cellCoordinatesFromClick(x: Double, y: Double): Pair<Int, Int> {
        if (x in 70.0..630.0 && y in 70.0..630.0) {
            val cellX = (x.toInt() - 70) / 70
            val cellY = (y.toInt() - 70) / 70
            return Pair(cellX, cellY)
        }
        return Pair(0, 0)
    }

    fun changeColorInCells(moves: Array<Pair<Int, Int>>, colour: Colour?) {
        for ((x, y) in moves) {
            getBoard()[x + y * 8].changeColour(colour)
        }
    }

    fun move(toCell: Checker, fromCell: Checker) {
        toCell.changeRang(fromCell.isQueen)
        toCell.changeColour(fromCell.colour) //меняю цвет выбранной зеленой клетки на цвет старой клетки
        fromCell.changeColour(null) //меняю цвет старой клетки на null
        fromCell.changeRang(false)

        isReadyToBeQueen(toCell)
    }

    fun isReadyToBeQueen(cell: Checker) {
        if (cell.y == 0 && cell.colour == Colour.BLACK) cell.changeRang(true)
        if (cell.y == 7 && cell.colour == Colour.WHITE) cell.changeRang(true)
    }

    fun canAttackAround(attackColour: Colour, board: Array<Checker>): Array<Pair<Checker, Array<Pair<Int, Int>>>> {
        var array = arrayOf<Pair<Checker, Array<Pair<Int, Int>>>>()

        for (cell in board) {
            if (cell.colour == attackColour) {
                val attack = cell.canAttack(board)
                if (attack.isNotEmpty()) array += Pair(cell, cell.canAttack(board))
            }
        }

        return array
    }

    fun whoDidWin(): Image? {
        return if (countWhite == 0) {
            game = false
            whiteWin
        } else if (countBlack == 0) {
            game = false
            blackWin
        } else null
    }
}
