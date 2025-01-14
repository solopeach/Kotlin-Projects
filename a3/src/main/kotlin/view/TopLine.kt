// package ui.lectures.javafx.mvc.javafxmvc.view

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.scene.control.Label
import Model
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font

/**
 * DoubleLabel displays the value of the [Model][ui.lectures.javafx.mvc.javafxmvc.model.Model] on a label.
 */
class TopLine(private val model: Model) : HBox(), InvalidationListener {

    var scoreLabel = Label("Score: ${model.score}")
    var livesLabel = Label("Lives: ${model.lives}")
    var levelLabel = Label("Level: ${model.level}")
    var rightGroup = HBox()
    val fontSize = 17.0

    init {
        model.addListener(this) // listen to the Model
        invalidated(null) // call to set initial text

        rightGroup.children.addAll(livesLabel, levelLabel)
        rightGroup.spacing = 30.0

        children.addAll(
            scoreLabel,
            rightGroup
        )

        scoreLabel.setTextFill(Color.WHITE)
        scoreLabel.setFont(Font(fontSize))
        livesLabel.setTextFill(Color.WHITE)
        livesLabel.setFont(Font(fontSize))
        levelLabel.setTextFill(Color.WHITE)
        levelLabel.setFont(Font(fontSize))

        setPadding(Insets(0.0,20.0,0.0,20.0))

        spacing = 610.0
    }

    override fun invalidated(observable: Observable?) {
        scoreLabel.text = "Score: ${model.score}"
        livesLabel.text = "Lives: ${model.lives}"
        levelLabel.text = "Level: ${model.level}"
    }

}