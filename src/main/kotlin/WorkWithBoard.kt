var board = arrayOf<Array<Checker>>()

fun fillBoard(): Array<Array<Checker>> {
    for (x in 0 until 8) {
        var array = arrayOf<Checker>()
        for (y in 0 until 8) {
            array += if (x % 2 == 0 && y % 2 == 0 || x % 2 == 1 && y % 2 == 1)
                Checker(x, y, setColor(y))
            else Checker(x, y, null)
        }
        board += array
    }
    return board
}

fun setColor(y: Int): Colour? {
    return when (y) {
        in 0 until 3 -> Colour.WHITE
        in 5 until 8 -> Colour.BLACK
        else -> null
    }
}
