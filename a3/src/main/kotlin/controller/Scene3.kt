package controller

import javafx.event.EventHandler
import javafx.scene.control.Button
import Model
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage

/**
 * Activating the ResetButton resets the value of the [Model][ui.lectures.javafx.mvc.javafxmvc.model.Model].
 */
class GameOverScene(private val model: Model): BorderPane(), InvalidationListener {

    val scoreLabel = Label("Final Score: ${model.score}")
    val levelLabel = Label("Level: ${model.level}")
    val title = Label()
    init {
        model.addListener(this) // listen to the Model
        invalidated(null) // call to set initial text

        title.setFont(Font(60.0))
        val instructions = Text("ENTER - Start Game\n" + "I - Back to Instructions\n"
                + "Q - Quit Game\n" + "1 or 2 or 3 - Start New Game at a specific level\n")
        instructions.setTextAlignment(TextAlignment.CENTER)
        instructions.setLineSpacing(3.0)
        instructions.setFont(Font(20.0))

        scoreLabel.setFont(Font(18.0))
        levelLabel.setFont(Font(18.0))

        val score = VBox(
            scoreLabel,
            levelLabel
        ).apply {
            alignment = Pos.CENTER
            spacing = 5.0
        }

        val content = VBox(
            title,
            score,
            instructions
        ).apply {
            alignment = Pos.CENTER
            spacing = 15.0
        }

        val bottomInsets = Insets(10.0, 10.0, 10.0, 10.0)
        content.setPadding(bottomInsets)

        center = content
    }

    override fun invalidated(observable: Observable?) {
        scoreLabel.text = "Final Score: ${model.score}"
        levelLabel.text = "Level: ${model.level}"
        if (model.gameCleared) {
            title.text = "GAME CLEARED!"
        } else {
            title.text = "GAME OVER!"
        }
    }
}