package controller

import javafx.event.EventHandler
import javafx.scene.control.Button
import Model
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage

/**
 * Activating the ResetButton resets the value of the [Model][ui.lectures.javafx.mvc.javafxmvc.model.Model].
 */
class TitleScene(private val model: Model): BorderPane() {

    init {


        val credits = Label("Implemented by Roni Wu for CS 349, University of Waterloo, S23")
        credits.alignment = Pos.CENTER

        val instructionsTitle = Text("Instructions")
        instructionsTitle.setFont(Font( 30.0))
        // instructionsTitle.setFont(Font("Arial", FontWeight.BOLD, 30.0))
        // make bold?? FontWeight.BOLD

        val instructions = Text("ENTER - Start Game\n" + "A or Left Arrow Key, D or Right Arrow Key - Move ship left or right\n"
                + "SPACE - Fire!\n" + "Q - Quit Game\n" + "1 or 2 or 3 - Start Game at a specific level")
        instructions.setTextAlignment(TextAlignment.CENTER)
        instructions.setLineSpacing(3.0)

        val titleScreen = VBox(
            instructionsTitle,
            instructions
        ).apply {
            alignment = Pos.CENTER
            spacing = 10.0
        }

        val bottomInsets = Insets(10.0, 10.0, 10.0, 10.0)
        val bottomCredits = VBox(credits).apply {
            alignment = Pos.CENTER
        }
        bottomCredits.setPadding(bottomInsets)

        // val root = BorderPane().apply {
        val logo = VBox(ImageView(Image("images/logo.png"))).apply {
            alignment = Pos.CENTER
        }
        logo.setPadding(bottomInsets)
        top = logo
        center = titleScreen
        bottom = bottomCredits
       //  }

        var setOnMouseClicked = { event: MouseEvent ->
            println(event)
            // model.setScene(stage, )
            event.consume()
        }

        addEventHandler(MouseEvent.MOUSE_CLICKED, setOnMouseClicked)

        // onAction = EventHandler {
        //     model.reset()
        // }
    }
}