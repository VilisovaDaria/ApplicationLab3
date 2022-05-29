package utils

import com.sun.javafx.scene.control.skin.Utils.getResource
import javafx.scene.image.Image
import java.io.FileInputStream
import java.nio.file.Paths

fun getImage(fileName: String,height: Double, width: Double): Image {
    val path = Paths.get(getResource("/$fileName")!!.toURI()).toString()
    return Image(FileInputStream(path), height, width, false, true)
}

val black = getImage("chessBlack.png", 70.0, 70.0)
val white = getImage("chessWhite.png", 70.0, 70.0)
val green = getImage("green.png", 71.0, 70.0)
val blackQueen = getImage("queenBlack.png", 70.0, 70.0)
val whiteQueen = getImage("queenWhite.png", 70.0, 70.0)
val background = getImage("board.jpg", 700.0, 700.0)
val restart = getImage("restart.png", 70.0, 40.0)
val exit = getImage("exit.png", 70.0, 40.0)
val whiteWin = getImage("whiteWin.png", 700.0, 700.0)
val blackWin = getImage("blackWin.png", 700.0, 700.0)

