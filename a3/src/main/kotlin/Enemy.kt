import javafx.animation.AnimationTimer
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
class Enemy(xval: Double, yval: Double, image: Image, bulletUrl: String): ImageView() {

    val height: Double = 30.0
    val width: Double = 45.0
    var bulletURL: String = ""

    init {
        x = xval
        y = yval
        setFitHeight(height)
        setFitWidth(width)
        setImage(image)
        bulletURL = bulletUrl
    }

    fun moveLeft(speed: Double) {
        x -= speed
    }

    fun moveRight(speed: Double) {
        x += speed
    }

    fun moveDown() {
        y += Global.ALIEN_SPEEDY
    }
}