import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import java.io.FileInputStream


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
        val background = Image(FileInputStream("src/main/board.jpg"))

        var countBlack = 12
        var countWhite = 12
        var game = true

        fun repaintScene(board: Array<Array<Chess>>) {
            gc.drawImage(background, 0.0, 0.0)
            for (stroke in board) {
                for (chip in stroke) {
                    var (x, y) = chip.getXY()
                    if (chip.getColour() != null) {
                        val image = chip.getColour()!!.getImage()
                        y = (y + 1) * 70
                        x = 70 * (x + 1)
                        gc.drawImage(image, x.toDouble(), y.toDouble())
                    }
                }
            }
        }

        fun actionReady(
            selectedCell: Chess,
            moves: Array<Pair<Int, Int>>,
            readyInfo: MutableList<Any?>,
            board: Array<Array<Chess>>
        ): Pair<Array<Array<Chess>>, MutableList<Any?>> {
            var board = board
            var ready = readyInfo
            val readyChip = ready[0] as Chess
            val isReady = ready[1] as Boolean
            if (!isReady) { //если ни одна клетка не выделена
                ready = mutableListOf(selectedCell, true) //выделяем её
                board = changeColorInCells(moves, Colour.GREEN, board)

            } else //повторное нажатие, если же выделена клетка
                if (selectedCell == readyChip) { //если нажали на выделенную клетку
                    ready =
                        mutableListOf(
                            Chess(-1, -1, selectedCell.getColour()),
                            false
                        ) //отменяем выделение
                    board = changeColorInCells(moves, null, board)
                }
            return Pair(board, ready)
        }

        var board = fillBoard()
        var ready = mutableListOf<Any?>(Chess(-1, -1, Colour.WHITE), false) //показывает, выделена ли какая-то клетка


        stage.scene.onMousePressed =
            EventHandler<MouseEvent> { event ->
                if (game) {
                    val (x, y) = cellCoordinatesFromClick(event.sceneX, event.sceneY)
                    val cell = board[x][y]
                    val readyChip = (ready[0] as Chess)
                    val attackColour = readyChip.getColour()

                    val attackCells = canAttackAround(attackColour!!, board)

                    if (attackCells.isNotEmpty()) {
                        println("1111")
                        for (chipInfo in attackCells) {

                            val chipCanAttack = chipInfo.first
                            val cellsCanMove = chipInfo.second

                            if (cell == chipCanAttack) {
                                val a = actionReady(cell, cellsCanMove, ready, board)
                                board = a.first
                                ready = a.second

                                repaintScene(board)

                            } else if (cell.getColour() == Colour.GREEN) {

                                if (readyChip.getColour() == Colour.WHITE) countBlack--
                                else countWhite--

                                board = eat(cellsCanMove, cell, readyChip, board)
                                val continueInfo = continueAttack(cell, board)
                                board = continueInfo.first
                                ready = continueInfo.second

                                repaintScene(board)
                            }
                        }


                    } else
                        if (cell.getColour() != Colour.GREEN) {
                            if (attackColour == cell.getColour()) {

                                val cellsCanMove = cell.canMove(board)
                                if (cellsCanMove.isNotEmpty()) {

                                    val readyInfo = actionReady(cell, cellsCanMove, ready, board)
                                    board = readyInfo.first
                                    ready = readyInfo.second

                                    repaintScene(board)
                                }
                            }
                        } else {
                            val oldMoves = readyChip.canMove(board) //нахожу её возможные ходы
                            board = changeColorInCells(oldMoves, null, board)
                            cell.changeColour(readyChip.getColour()) //меняю цвет выбранной зеленой клетки на цвет старой клетки
                            readyChip.changeColour(null) //меняю цвет старой клетки на null

                            ready = mutableListOf(
                                Chess(-1, -1, cell.opposite()),
                                false
                            )

                            repaintScene(board)
                        }
                    if (countWhite == 0) {
                        println("Black win")
                        gc.drawImage(Image(FileInputStream("src/main/blackwin.png")), 0.0, 0.0)
                        game = false
                    } else if (countBlack == 0) {
                        println("White win")
                        game = false
                        gc.drawImage(Image(FileInputStream("src/main/whitewin.png")), 0.0, 0.0)
                    }
                }
            }
        repaintScene(board)
        stage.show()
    }
}


fun eat(
    moves: Array<Pair<Int, Int>>,
    toCell: Chess,
    fromCell: Chess,
    oldBoard: Array<Array<Chess>>
): Array<Array<Chess>> {
    var board = oldBoard
    val (x1, y1) = toCell.getXY()
    val (x2, y2) = fromCell.getXY()
    val x3: Int = if (x1 - x2 < 0){
        x1 + 1
    } else x1 - 1
    val y3: Int = if (y1 - y2 < 0){
        y1 + 1
    } else y1 - 1

    board[x3][y3].changeColour(null)

    board = changeColorInCells(moves, null, board)

    toCell.changeColour(fromCell.getColour()) //меняю цвет выбранной зеленой клетки на цвет старой клетки
    fromCell.changeColour(null) //меняю цвет старой клетки на null
    return board
}

fun continueAttack(selectedCell: Chess, oldBoard: Array<Array<Chess>>): Pair<Array<Array<Chess>>, MutableList<Any?>> {
    val canAttack = selectedCell.canAttack(oldBoard)
    var board = oldBoard
    val ready: MutableList<Any?>
    if (canAttack.isEmpty()) {
        ready = mutableListOf(
            Chess(-1, -1, selectedCell.opposite()),
            false
        ) //отменяю выделение клетки, меняю цвет следующей ходящей
    } else {
        ready = mutableListOf(selectedCell, true)
        board = changeColorInCells(canAttack, Colour.GREEN, board)
    }

    return Pair(board, ready)
}

fun cellCoordinatesFromClick(x: Double, y: Double): Pair<Int, Int> {
    if (x in 70.0..630.0 && y in 70.0..630.0) {
        val cellX = (x.toInt() - 70) / 70
        val cellY = (y.toInt() - 70) / 70
        return Pair(cellX, cellY)
    }
    return Pair(0, 0)
}

fun changeColorInCells(moves: Array<Pair<Int, Int>>, colour: Colour?, board: Array<Array<Chess>>): Array<Array<Chess>> {
    for ((x, y) in moves) {
        board[x][y].changeColour(colour)
    }
    return board
}

