package model


class Board(var cells: Array<Checker> = arrayOf()) {

    private val notReady = -1

    private fun setColor(y: Int): Colour? {
        return when (y) {
            in 0 until 3 -> Colour.WHITE
            in 5 until 8 -> Colour.BLACK
            else -> null
        }
    }

    fun getCell(x: Int, y: Int): Checker {
        return cells[x + y * 8]
    }

    fun fillBoard(): Array<Checker> {
        for (cell in 0..63) {
            val x = cell % 8
            val y = cell / 8
            cells += if (x % 2 == 0 && y % 2 == 0 || x % 2 == 1 && y % 2 == 1)
                Checker(x, y, setColor(y))
            else Checker(x, y, null)
        }
        return cells
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

    fun continueAttack(selectedCell: Checker): Checker {
        val canAttack = selectedCell.canAttack(cells)

        val ready = if (canAttack.isEmpty()) {
            Checker(notReady, notReady, selectedCell.opposite())
        } else {
            changeColorInCells(canAttack, Colour.GREEN)
            selectedCell
        }

        return ready
    }

    fun changeColorInCells(moves: Array<Pair<Int, Int>>, colour: Colour?): Array<Checker> {
        for ((x, y) in moves) {
            cells[x + y * 8].changeColour(colour)
        }
        return cells
    }

    fun move(toCell: Checker, fromCell: Checker) {
        toCell.changeRang(fromCell.isQueen)
        toCell.changeColour(fromCell.colour) //меняю цвет выбранной зеленой клетки на цвет старой клетки
        fromCell.changeColour(null) //меняю цвет старой клетки на null
        fromCell.changeRang(false)

        toCell.isReadyToBeQueen()
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


    override fun toString(): String {
        var string = ""
        for (cell in 0..63) {
            val x = cell % 8
            val y = cell / 8
            if (x == 0 && y != 0) string += "\n"
            string += when (cells[cell].colour) {
                Colour.WHITE -> "W "
                Colour.BLACK -> "B "
                Colour.GREEN -> "G "
                else -> "# "
            }
        }
        return string
    }

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false

        other as Board
        return cells.contentEquals(other.cells)
    }

    override fun hashCode(): Int {
        return cells.contentHashCode()
    }
}
