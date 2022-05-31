import model.Board
import model.Checker
import model.Colour
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals


class Tests {

    private val startBoard = Board(Board().fillBoard())

    @Test
    fun getCell() {
        assertTrue(Checker(0, 0, Colour.WHITE, false) == startBoard.getCell(0, 0))
        assertTrue(Checker(2, 5, null, false) == startBoard.getCell(2, 5))
    }

    @Test
    fun changeColorInCells() {
        val board = Board(arrayOf(Checker(0, 0, Colour.BLACK), Checker(1, 0, Colour.WHITE)))

        assertContentEquals(
            arrayOf(Checker(0, 0, Colour.WHITE), Checker(1, 0, Colour.WHITE)),
            board.changeColorInCells(arrayOf(0 to 0, 1 to 0), Colour.WHITE)
        )
    }


    @Test
    fun isReadyToBeQueen() {
        startBoard.cells[61] = Checker(5, 7, Colour.WHITE, false)
        assertTrue(startBoard.isReadyToBeQueen(startBoard.cells[61]) == startBoard.cells[61].changeRang(true))
    }


    @Test
    fun canMove() {
        startBoard.cells[16].changeRang(true)

        assertContentEquals(arrayOf(1 to 3, 2 to 4), startBoard.cells[16].canMove(startBoard.cells))
        assertContentEquals(arrayOf(5 to 3, 7 to 3), startBoard.cells[22].canMove(startBoard.cells))
    }


    @Test
    fun canAttack() {
        startBoard.cells[16].changeRang(true)
        startBoard.cells[25] = Checker(1, 3, Colour.BLACK, false)

        assertContentEquals(arrayOf(2 to 4), startBoard.cells[16].canAttack(startBoard.cells))
    }

    @Test
    fun continueAttack() {
        startBoard.cells[25] = Checker(1, 3, Colour.BLACK, false)

        assertEquals(
            Checker(0, 2, Colour.WHITE),
            startBoard.continueAttack(Checker(0, 2, Colour.WHITE))
        )
    }


    @Test
    fun move() {
        assertContentEquals(
            arrayOf(),
            startBoard.canAttackAround(Colour.WHITE, Board().fillBoard())
        )
    }


    @Test
    fun eat() {
        val newBoard = Board(
            arrayOf(
                Checker(0, 0, Colour.WHITE), Checker(1, 0, null), Checker(2, 0, Colour.BLACK),
                Checker(3, 0, null), Checker(4, 0, null), Checker(5, 0, null),
                Checker(6, 0, Colour.WHITE), Checker(7, 0, null), Checker(0, 1, null),
                Checker(1, 1, Colour.BLACK)
            )
        )

        newBoard.eat(arrayOf(1 to 1), Checker(1, 1, Colour.BLACK), Checker(0, 0, Colour.WHITE))

        assertContentEquals(
            newBoard.changeColorInCells(arrayOf(1 to 1), null),
            arrayOf(
                Checker(0, 0, Colour.WHITE), Checker(1, 0, null), Checker(2, 0, Colour.BLACK),
                Checker(3, 0, null), Checker(4, 0, null), Checker(5, 0, null),
                Checker(6, 0, Colour.WHITE), Checker(7, 0, null), Checker(0, 1, null),
                Checker(1, 1, null)
            )
        )
    }


    @Test
    fun hashcodeTestForBoard() {
        assertTrue(startBoard.hashCode() == startBoard.hashCode())
        assertFalse(startBoard.hashCode() == Board().hashCode())
    }

    @Test
    fun equalsTestForBoard() {
        assertTrue(startBoard == startBoard)
        assertFalse(startBoard == Board())
    }

    @Test
    fun testToStringForBoard() {

        startBoard.cells[0].changeColour(Colour.GREEN)
        assertEquals(
            """G # W # W # W # 
# W # W # W # W 
W # W # W # W # 
# # # # # # # # 
# # # # # # # # 
# B # B # B # B 
B # B # B # B # 
# B # B # B # B """, startBoard.toString()
        )
    }
}