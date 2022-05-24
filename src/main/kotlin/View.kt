import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
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
            gc.drawImage(restart, 10.0, 10.0)
            gc.drawImage(exit, 620.0, 10.0)
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
            if (x in 10.0..80.0 && y in 10.0..50.0) {
                begin()
                repaintScene(board)
            }
        }

        fun exit(x: Double, y: Double) {
            if (x in 620.0..690.0 && y in 10.0..50.0) exitProcess(0)
        }

        fun actionReady(
            selectedCell: Checker,
            moves: Array<Pair<Int, Int>>,
        ){
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



        stage.scene.onMousePressed =
            EventHandler { event ->
                val coordinateX = event.sceneX
                val coordinateY = event.sceneY

                exit(coordinateX, coordinateY)
                restart(coordinateX, coordinateY)

                val (x, y) = cellCoordinatesFromClick(coordinateX, coordinateY)

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
                                actionReady(cell, cellsCanMove)

                                repaintScene(board)

                            } else if (cell.colour == Colour.GREEN) {

                                if (readyChecker.colour == Colour.WHITE) countBlack--
                                else countWhite--

                                board = eat(cellsCanMove, cell, readyChecker)
                                ready = continueAttack(cell)

                                repaintScene(board)
                            }
                        }
                    } else
                        if (cell.colour != Colour.GREEN) {
                            if (attackColour == cell.colour) {

                                val cellsCanMove = cell.canMove(board)
                                if (cellsCanMove.isNotEmpty()) {

                                    actionReady(cell, cellsCanMove)

                                    repaintScene(board)
                                }
                            }
                        } else {
                            changeColorInCells(readyChecker.canMove(board), null)
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

    return board

}

fun continueAttack(selectedCell: Checker): MutableList<Any?> {
    val canAttack = selectedCell.canAttack(board)
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
        board[x][y].changeColour(colour)
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

