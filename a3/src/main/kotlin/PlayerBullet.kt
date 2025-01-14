import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.scene.shape.Rectangle

class PlayerBullet(xval: Double, yval: Double): ImageView("images/player_bullet.png") {

    val height: Double = 20.0
    val width: Double = 7.0

    init {
        x = xval - width / 2
        y = yval
        setFitHeight(height)
        setFitWidth(width)
    }
}