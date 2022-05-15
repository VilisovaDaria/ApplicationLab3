var board = arrayOf<Array<Chess>>()

fun fillBoard(): Array<Array<Chess>> {
    for (x in 0 until 4) {
        var array = arrayOf<Chess>()
        for (y in 0 until 8) {
            array += Chess(x, y, setColor(y))
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
