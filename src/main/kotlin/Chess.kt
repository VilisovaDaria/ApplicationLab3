import java.io.FileInputStream
import javafx.scene.image.Image
import javax.swing.BoxLayout
import kotlin.math.abs

enum class Colour(private val image: Image) {
    BLACK(Image(FileInputStream("src/main/chessBlack.png"))),
    WHITE(Image(FileInputStream("src/main/chessWhite.png"))),
    GREEN(Image(FileInputStream("src/main/green.png")));

    @JvmName("getImage1")
    fun getImage(): Image {
        return image
    }
}

class Chess(var x: Int, var y: Int, var colour: Colour?) {

    private val baseColours = arrayOf(Colour.BLACK, Colour.WHITE)

    fun getXY(): Pair<Int, Int> {
        return Pair(x, y)
    }

    @JvmName("getColour1")
    fun getColour(): Colour? {
        return colour
    }

    fun changeColour(newColour: Colour?) {
        colour = newColour
    }

    fun opposite(): Colour {
        return if (colour == Colour.WHITE) Colour.BLACK
        else Colour.WHITE
    }

    fun canMove(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var y = this.y
        var array = arrayOf<Pair<Int, Int>>()

        if (colour != null) {
            if (colour == Colour.WHITE) {
                y++
            } else y--

            if (x in 1..7 && board[x - 1][y].colour !in baseColours) array += Pair(x - 1, y)
            if (x in 0..6 && board[x + 1][y].colour !in baseColours) array += Pair(x + 1, y)

        } else println("Это белая клетка")

        return array
    }

    fun canAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        val coefficient = listOf(Pair(this.y - 1, this.y - 2), Pair(this.y + 1, this.y + 2))

        try {
            for (i in coefficient.indices) {
                val second = coefficient[i].second
                val first = coefficient[i].first

                if (x in 2..7) {
                    val firstCell = board[x - 1][first]
                    if (firstCell.colour != colour && firstCell.colour in baseColours) {
                        if (board[x - 2][second].getColour() == null ||
                            board[x - 2][second].getColour() == Colour.GREEN
                        ) array += Pair(x - 2, second)
                    }
                }
                if (x in 0..6) {
                    val secondCell = board[x + 1][first]
                    if (secondCell.colour != colour && secondCell.colour in baseColours) {
                        if (board[x + 2][second].getColour() == null ||
                            board[x + 2][second].getColour() == Colour.GREEN
                        ) array += Pair(x + 2, second)
                    }
                }
            }
        } catch (e: Exception) { println(IndexOutOfBoundsException(""))}

        return array
    }

}

fun canAttackAround(attackColour: Colour, board: Array<Array<Chess>>): Array<Pair<Chess, Array<Pair<Int, Int>>>> {
    var array = arrayOf<Pair<Chess,Array<Pair<Int, Int>>>>()

    for (stroke in board){
        for (chip in stroke){
            if (chip.getColour() == attackColour) {
                val attack = chip.canAttack(board)
                if (attack.isNotEmpty()) array += Pair(chip, chip.canAttack(board))
            }
        }
    }
    return array
}
