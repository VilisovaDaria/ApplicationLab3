import kotlin.system.exitProcess

var countBlack = 12
var countWhite = 12
var game = true
var ready = mutableListOf<Any?>(Checker(-1, -1, Colour.WHITE), false) //показывает, выделена ли какая-то клетка

fun controller(coordinateX: Double, coordinateY: Double){
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
                    val cellsCanMove = cell.canMove(board)
                    if (cellsCanMove.isNotEmpty()) actionReady(cell, cellsCanMove)
                }
            } else {
                changeColorInCells(readyChecker.canMove(board), null)
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

fun restart(x: Double, y: Double) {
    if (x in 10.0..80.0 && y in 10.0..50.0) {
        countBlack = 12
        countWhite = 12
        game = true
        board = arrayOf()
        board = fillBoard()
        ready = mutableListOf(Checker(-1, -1, Colour.WHITE), false)
    }
}

fun exit(x: Double, y: Double) {
    if (x in 620.0..690.0 && y in 10.0..50.0) exitProcess(0)
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