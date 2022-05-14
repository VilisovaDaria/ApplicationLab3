import java.io.FileInputStream
import javafx.scene.image.Image

enum class Color(private val image: Image) {
    BLACK(Image(FileInputStream("src/main/chessBlack.png"))),
    WHITE(Image(FileInputStream("src/main/chessWhite.png")));

    @JvmName("getImage1")
    fun getImage(): Image {
        return image
    }
}


class Chess(var x: Int, var y: Int, color: Color?) {
    // у шашки есть цвет
    private val white = 1
    private val black = -1

    //функция получения ячейки
    fun getCell(): Set<Double> {
        return drawChips().keys
    }

    //получение координат
    fun getXY(): Collection<Pair<Double, Double>> {
        return drawChips().values
    }

    //функции: если ячейка пустая и соседняя возможен ход;
    //если на стыке шашки 0 и 1 (разные цвета), то можно бить;

}
