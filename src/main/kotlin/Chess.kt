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

            if (x - 1 >= 0 && x + 1 <= 7 && y in 0..7) {
                val firstCell = board[x - 1][y]
                val secondCell = board[x + 1][y]
                if (x - 1 >= 0 && (firstCell.getColour() == null || firstCell.getColour() == Colour.GREEN)) {
                    array += Pair(x - 1, y)
                }
                if (x + 1 <= 7 && (secondCell.getColour() == null || secondCell.getColour() == Colour.GREEN)) {
                    array += Pair(x + 1, y)
                }
            }
        } else println("Это белая клетка")
        return array
    }


    // у меня проблема, боковые шашки не бьют
    fun canAttack(board: Array<Array<Chess>>): Array<Pair<Int, Int>> {
        var array = arrayOf<Pair<Int, Int>>()
        var y = this.y
        var x = this.x
        if (colour == Colour.WHITE) {
            y++
        } else y--

        if (x - 1 >= 0 && x + 1 <= 7 && y in 0..7) {
            val firstCell = board[x - 1][y]
            val secondCell = board[x + 1][y]
            val baseColours = arrayOf(Colour.BLACK, Colour.WHITE)
            if (x - 1 >= 0 && (firstCell.getColour() != colour && firstCell.getColour() in baseColours)) {
                y = if (colour == Colour.WHITE) {
                    this.y + 2
                } else this.y - 2
                if (x - 2 >= 0 && y in 0..7 && (board[x - 2][y].getColour() == null || board[x - 2][y].getColour() == Colour.GREEN)) {
                    array += Pair(x - 2, y)
                }
            }
            if (x + 1 <= 7 && (secondCell.getColour() != colour && secondCell.getColour() in baseColours)) {
                y = if (colour == Colour.WHITE) {
                    this.y + 2
                } else this.y - 2
                if (x + 2 >= 0 && y in 0..7 && (board[x + 2][y].getColour() == null || board[x + 2][y].getColour() == Colour.GREEN)) {
                    array += Pair(x + 2, y)
                }
            }
        }
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
