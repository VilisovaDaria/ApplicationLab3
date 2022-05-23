import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import kotlin.system.exitProcess


fun main() {
    Application.launch(MyFirstChart::class.java)
}

class MyFirstChart : Application() {

    override fun start(stage: Stage) {

        stage.isResizable = false
        val root = Group()
        stage.scene = Scene(root)
        stage.title = "Русские шашки"
        val canvasNew = Canvas(700.0, 700.0)
        root.children.add(canvasNew)

        val gc = canvasNew.graphicsContext2D

        var countBlack = 12
        var countWhite = 12
        var game = true
        board = fillBoard()
        var ready = mutableListOf<Any?>(Checker(-1, -1, Colour.WHITE), false) //показывает, выделена ли какая-то клетка

        fun begin() {
            countBlack = 12
            countWhite = 12
            game = true
            board = arrayOf()
            board = fillBoard()
            ready = mutableListOf(Checker(-1, -1, Colour.WHITE), false)
        }


        fun repaintScene(board: Array<Array<Checker>>) {
            gc.drawImage(background, 0.0, 0.0)
            gc.drawImage(restart, 0.0, 0.0)
            gc.drawImage(exit, 630.0, 0.0)
            for (stroke in board) {
                for (checker in stroke) {
                    var (x, y) = checker.getXY()
                    if (checker.colour != null) {
                        val image = if (checker.isQueen) {
                            when (checker.colour) {
                                Colour.WHITE -> Colour.WHITEQUEEN.image
                                Colour.BLACK -> Colour.BLACKQUEEN.image
                                else -> checker.colour!!.image
                            }
                        } else checker.colour!!.image
                        y = (y + 1) * 70
                        x = 70 * (x + 1)
                        gc.drawImage(image, x.toDouble(), y.toDouble())
                    }
                }
            }
        }

        fun restart(x: Double, y: Double) {
            if (x in 0.0..70.0 && y in 0.0..40.0) {
                begin()
                repaintScene(board)
            }
        }

        fun exit(x: Double, y: Double) {
            if (x in 630.0..700.0 && y in 0.0..40.0) exitProcess(0)
        }

        fun actionReady(
            selectedCell: Checker,
            moves: Array<Pair<Int, Int>>,
            readyInfo: MutableList<Any?>,
        ): Pair<Array<Array<Checker>>, MutableList<Any?>> {

            var ready = readyInfo
            val readyChip = ready[0] as Checker
            val isReady = ready[1] as Boolean
            if (!isReady) { //если ни одна клетка не выделена
                ready = mutableListOf(selectedCell, true) //выделяем её
                board = changeColorInCells(moves, Colour.GREEN, board)

            } else //повторное нажатие, если же выделена клетка
                if (selectedCell == readyChip) { //если нажали на выделенную клетку
                    ready =
                        mutableListOf(
                            Checker(-1, -1, selectedCell.colour),
                            false
                        ) //отменяем выделение
                    board = changeColorInCells(moves, null, board)
                }
            return Pair(board, ready)
        }



        stage.scene.onMousePressed =
            EventHandler<MouseEvent> { event ->
                val coordinateX = event.sceneX
                val coordinateY = event.sceneY
                val (x, y) = cellCoordinatesFromClick(coordinateX, coordinateY)
                exit(coordinateX, coordinateY)
                restart(coordinateX, coordinateY)
                if (game) {
                    val cell = board[x][y]
                    val readyChecker = (ready[0] as Checker)
                    val attackColour = readyChecker.colour

                    val attackCells = canAttackAround(attackColour!!, board)

                    if (attackCells.isNotEmpty()) {
                        for (checkerInfo in attackCells) {

                            val checkerCanAttack = checkerInfo.first
                            val cellsCanMove = checkerInfo.second

                            if (cell == checkerCanAttack) {
                                val a = actionReady(cell, cellsCanMove, ready)
                                board = a.first
                                ready = a.second

                                repaintScene(board)

                            } else if (cell.colour == Colour.GREEN) {

                                if (readyChecker.colour == Colour.WHITE) countBlack--
                                else countWhite--

                                board = eat(cellsCanMove, cell, readyChecker)
                                val continueInfo = continueAttack(cell)
                                board = continueInfo.first
                                ready = continueInfo.second

                                repaintScene(board)
                            }
                        }


                    } else
                        if (cell.colour != Colour.GREEN) {
                            if (attackColour == cell.colour) {

                                val cellsCanMove = cell.canMove(board)
                                if (cellsCanMove.isNotEmpty()) {

                                    val readyInfo = actionReady(cell, cellsCanMove, ready)
                                    board = readyInfo.first
                                    ready = readyInfo.second

                                    repaintScene(board)
                                }
                            }
                        } else {
                            val oldMoves = readyChecker.canMove(board) //нахожу её возможные ходы
                            board = changeColorInCells(oldMoves, null, board)

                            move(cell, readyChecker)

                            ready = mutableListOf(
                                Checker(-1, -1, cell.opposite()),
                                false
                            )

                            repaintScene(board)
                        }
                    if (countWhite == 0) {
                        println("Black win")
                        gc.drawImage(blackWin, 0.0, 0.0)
                        gc.drawImage(restart, 0.0, 0.0)
                        gc.drawImage(exit, 630.0, 0.0)
                        game = false
                    } else if (countBlack == 0) {
                        println("White win")
                        game = false
                        gc.drawImage(whiteWin, 0.0, 0.0)
                        gc.drawImage(restart, 0.0, 0.0)
                        gc.drawImage(exit, 630.0, 0.0)
                    }
                }
            }
        repaintScene(board)
        stage.show()
    }
}


fun eat(
    moves: Array<Pair<Int, Int>>,
    toCell: Checker,
    fromCell: Checker
): Array<Array<Checker>> {

    var moves = moves
    var step = 1
    val coefficientX = (toCell.x - fromCell.x) / (kotlin.math.abs((toCell.x - fromCell.x)))
    val coefficientY = (toCell.y - fromCell.y) / (kotlin.math.abs((toCell.y - fromCell.y)))

    while (fromCell.x + step * coefficientX != toCell.x && fromCell.y + step * coefficientY != toCell.y) {
        moves += Pair(fromCell.x + step * coefficientX, fromCell.y + step * coefficientY)
        step++
    }

    board = changeColorInCells(moves, null, board)
    move(toCell, fromCell)

    return board

}

fun continueAttack(selectedCell: Checker): Pair<Array<Array<Checker>>, MutableList<Any?>> {
    val canAttack = selectedCell.canAttack(board)
    val ready: MutableList<Any?>
    if (canAttack.isEmpty()) {
        ready = mutableListOf(
            Checker(-1, -1, selectedCell.opposite()),
            false
        ) //отменяю выделение клетки, меняю цвет следующей ходящей
    } else {
        ready = mutableListOf(selectedCell, true)
        board = changeColorInCells(canAttack, Colour.GREEN, board)
    }

    return Pair(board, ready)
}

fun cellCoordinatesFromClick(x: Double, y: Double): Pair<Int, Int> {
    if (x in 70.0..630.0 && y in 70.0..630.0) {
        val cellX = (x.toInt() - 70) / 70
        val cellY = (y.toInt() - 70) / 70
        return Pair(cellX, cellY)
    }
    return Pair(0, 0)
}

fun changeColorInCells(moves: Array<Pair<Int, Int>>, colour: Colour?, board: Array<Array<Checker>>): Array<Array<Checker>> {
    for ((x, y) in moves) {
        board[x][y].changeColour(colour)
    }
    return board
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

