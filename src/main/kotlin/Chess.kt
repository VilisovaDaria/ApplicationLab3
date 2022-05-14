import java.io.FileInputStream
import javafx.scene.image.Image

enum class Colour(private val image: Image) {
    BLACK(Image(FileInputStream("src/main/chessBlack.png"))),
    WHITE(Image(FileInputStream("src/main/chessWhite.png")));

    @JvmName("getImage1")
    fun getImage(): Image {
        return image
    }
}


class Chess(var x: Int, var y: Int, var colour: Colour?) {

    fun getCell(): Pair<Int, Int> {
        return Pair(x, y)
    }

    fun getColor(): Colour? {
        return colour
    }

}
