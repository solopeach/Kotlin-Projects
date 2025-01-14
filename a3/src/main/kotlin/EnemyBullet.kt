import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.scene.shape.Rectangle

class EnemyBullet(xval: Double, yval: Double, image: Image): ImageView() {

    val height: Double = 30.0
    val width: Double = 15.0

    init {
        x = xval - width / 2
        y = yval
        setFitHeight(height)
        setFitWidth(width)
        setImage(image)
    }
}