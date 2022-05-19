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

        val board = fillBoard()
        var ready = mutableListOf<Any?>(Chess(-1, -1, Colour.WHITE), false) //показывает, выделена ли какая-то клетка


        stage.scene.onMousePressed =
            EventHandler<MouseEvent> { event ->
                val (x, y) = cellCoordinatesFromClick(event.sceneX, event.sceneY)
                val cell = board[x][y]


                val readyChip = (ready[0] as Chess)
                val attackColour = readyChip.getColour()
                val isReady = (ready[1] as Boolean)

                val attackCells = canAttackAround(attackColour!!, board)

                if (attackCells.isNotEmpty()) {
                    for (chipInfo in attackCells) {
                        if (cell == chipInfo.first || cell.getColour() == Colour.GREEN) {

                            if (cell.getColour() != Colour.GREEN) {
                                //отрисовка зеленых клеток при нажатии и их исчезновении при повторном нажатии (нельзя выбрать другие клетки, если одна выбрана)
                                if (!isReady) { //если ни одна клетка не выделена
                                    ready = mutableListOf(cell, true) //выделяем её
                                    for ((x, y) in chipInfo.second) {
                                        board[x][y].changeColour(Colour.GREEN) //рисуем зеленые клетки
                                    }
                                    repaintScene(board) //перерисовываем сцену

                                } else //повторное нажатие, если же выделена клетка
                                    if (cell == readyChip) { //если нажали на выделенную клетку
                                        ready =
                                            mutableListOf(
                                                Chess(-1, -1, cell.getColour()),
                                                false
                                            ) //отменяем выделение
                                        for ((x, y) in chipInfo.second) {
                                            board[x][y].changeColour(null) //убираем зеленые клетки
                                        }
                                        repaintScene(board) //перерисовываю
                                    }


                            } else { //если клетка зеленая
                                val oldChip = chipInfo.first
                                val oldMoves = chipInfo.second

                                val (x1, y1) = cell.getXY()
                                val (x2, y2) = (ready[0] as Chess).getXY()
                                val x3 = (x1 + x2) / 2
                                val y3 = (y1 + y2) / 2

                                board[x3][y3].changeColour(null)

                                for ((x, y) in oldMoves) {
                                    board[x][y].changeColour(null) //меняю цвет возможных ходов старой клетки на null
                                }
                                cell.changeColour(readyChip.getColour()) //меняю цвет выбранной зеленой клетки на цвет старой клетки
                                readyChip.changeColour(null) //меняю цвет старой клетки на null
                                ready = mutableListOf(
                                    Chess(-1, -1, cell.opposite()),
                                    false
                                ) //отменяю выделение клетки, меняю цвет следующей ходящей


                                    //здесь сделать ещё одну проверку canAttack(), чтобы продолжить бить остальные шашки
                                repaintScene(board) //перерисовываю
                            }


                        }
                    }


                } else
                    if (cell.getColour() != Colour.GREEN) {
                        if (attackColour == cell.getColour()) { //если цвет клетки соответствует ходящей
                            //отрисовка зеленых клеток при нажатии и их исчезновении при повторном нажатии (нельзя выбрать другие клетки, если одна выбрана)
                            val cellsCanMove = cell.canMove(board) //находим возможные пути клетки
                            if (cellsCanMove.isNotEmpty()) { //если есть куда ходить
                                if (!isReady) { //если ни одна клетка не выделена
                                    ready = mutableListOf(cell, true) //выделяем её
                                    for ((x, y) in cellsCanMove) {
                                        board[x][y].changeColour(Colour.GREEN) //рисуем зеленые клетки
                                    }
                                    repaintScene(board) //перерисовываем сцену

                                } else //повторное нажатие, если же выделена клетка
                                    if (cell == readyChip) { //если нажали на выделенную клетку
                                        ready =
                                            mutableListOf(Chess(-1, -1, cell.getColour()), false) //отменяем выделение
                                        for ((x, y) in cellsCanMove) {
                                            board[x][y].changeColour(null) //убираем зеленые клетки
                                        }
                                        repaintScene(board) //перерисовываю
                                    }
                            }
                        }
                    } else { //если клетка зеленая
                        val oldMoves = readyChip.canMove(board) //нахожу её возможные ходы
                        for ((x, y) in oldMoves) {
                            board[x][y].changeColour(null) //меняю цвет возможных ходов старой клетки на null
                        }
                        cell.changeColour(readyChip.getColour()) //меняю цвет выбранной зеленой клетки на цвет старой клетки
                        readyChip.changeColour(null) //меняю цвет старой клетки на null
                        ready = mutableListOf(
                            Chess(-1, -1, cell.opposite()),
                            false
                        ) //отменяю выделение клетки, меняю цвет следующей ходящей

                        repaintScene(board) //перерисовываю
                    }


            }

        repaintScene(board)
        stage.show()
    }
}

fun cellCoordinatesFromClick(x: Double, y: Double): Pair<Int, Int> {
    if (x in 70.0..630.0 && y in 70.0..630.0) {
        val cellX = (x.toInt() - 70) / 70
        val cellY = (y.toInt() - 70) / 70
        return Pair(cellX, cellY)
    }
    return Pair(0, 0)
}


