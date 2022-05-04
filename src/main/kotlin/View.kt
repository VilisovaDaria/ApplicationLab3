import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.stage.Stage
import java.io.FileInputStream


fun main() {
    Application.launch(MyFirstChart::class.java)
}

class MyFirstChart: Application() {

    override fun start(stage: Stage) {

        val root = Group()
        stage.scene = Scene(root)
        stage.title = "Русские шашки"

        val canvasNew = Canvas(700.0, 700.0)
        root.children.add(canvasNew)

        val gc = canvasNew.graphicsContext2D
        val image = Image(FileInputStream("src/main/board.jpg"))
        gc.drawImage(image, 0.0, 0.0)

        stage.icons.add(Image(FileInputStream("src/main/1.png")))

        stage.show()
    }

}