package model

class Board(var cells: Array<Checker> = arrayOf()) {

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

    fun fillBoard() {
        for (cell in 0..63){
            val x = cell % 8
            val y = cell / 8
            cells += if (x % 2 == 0 && y % 2 == 0 || x % 2 == 1 && y % 2 == 1)
                Checker(x, y, setColor(y))
            else Checker(x, y, null)
        }
    }

    override fun toString(): String {
        var string = ""
        for (cell in 0..63){
            val x = cell % 8
            val y = cell / 8
            if (x == 0 && y != 0) string += "\n"
            string += when(cells[cell].colour){
                Colour.WHITE -> "W "
                Colour.BLACK -> "B "
                Colour.GREEN -> "G "
                else -> "# "
            }
        }
        return string
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!cells.contentEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.contentHashCode()
    }
}
