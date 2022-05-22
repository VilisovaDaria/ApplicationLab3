import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals


class Test {

    private val board = fillBoard()

    @Test
    fun getXY() {
        assertEquals(4 to 6, board[4][6].getXY())
        assertEquals(1 to 0, board[1][0].getXY())
    }

    @Test
    fun opposite() {
        assertEquals(Colour.BLACK, board[0][0].opposite())
        assertEquals(Colour.WHITE, board[7][7].opposite())
    }

    @Test
    fun chessMove() {
        board[7][5] = Chess(7, 5, Colour.BLACKQUEEN, true)
        board[5][3] = Chess(5, 3, null, false)
        assertContentEquals(arrayOf(6 to 4, 5 to 3), board[7][5].canMove(board))

        assertContentEquals(arrayOf(1 to 3), board[0][2].canMove(board))
        assertContentEquals(arrayOf(2 to 4, 4 to 4), board[3][5].canMove(board))
        assertContentEquals(arrayOf(), board[5][1].canMove(board))
    }


    @Test
    fun chessAttack() {
        board[1][3] = Chess(1, 3, Colour.BLACK, false)
        assertContentEquals(arrayOf(2 to 4), board[0][2].queenAttack(board))
        board[3][3] = Chess(3, 3, Colour.BLACK, false)
        board[5][3] = Chess(5, 3, Colour.BLACK, false)
        assertContentEquals(arrayOf(6 to 4, 2 to 4), board[4][2].canAttack(board))
    }

    @Test
    fun canAttackAround() {
        assertContentEquals(arrayOf(), canAttackAround(board[0][2].colour!!, board))
    }
}