import javafx.animation.AnimationTimer
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse

class Player(): ImageView(Image("images/player.png")) {

    val height: Double = Global.PLAYER_HEIGHT
    val width: Double = Global.PLAYER_WIDTH
    val speed: Double = 10.0
    var leftKeyPressed: Boolean = false
    var rightKeyPressed: Boolean = false
    var movement: Double = 0.0

    init {
        x = Global.windowWidth/2
        y = Global.LOWEST_POINT_PLAYER
        setFitHeight(height)
        setFitWidth(width)
    }

    fun restartPosition() {
        x = Global.windowWidth/2
        y = Global.LOWEST_POINT_PLAYER
        leftKeyPressed = false
        rightKeyPressed = false
    }

    fun moveLeft() {
        movement = -speed
        leftKeyPressed = true
    }

    fun moveRight() {
        movement = speed
        rightKeyPressed = true
    }

    fun stopLeft() {
        leftKeyPressed = false
        if (rightKeyPressed) {
            moveRight()
        }
    }

    fun stopRight() {
        rightKeyPressed = false
        if (leftKeyPressed) {
            moveLeft()
        }
    }

    fun update() {
        if (leftKeyPressed || rightKeyPressed) {
            x += movement
        }

        if (x < Global.LEFT_WALL_BUFFER) {
            x = Global.LEFT_WALL_BUFFER
        } else if (x > Global.RIGHT_WALL_BUFFER) {
            x = Global.RIGHT_WALL_BUFFER
        }
    }
}