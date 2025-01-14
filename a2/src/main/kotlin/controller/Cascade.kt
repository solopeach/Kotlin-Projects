package ui.lectures.javafx.mvc.javafxmvcextended.controller

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model
import java.io.File
import java.io.FileInputStream

/**
 * Activating the IncrementButton increments the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model].
 */
class Cascade(private val model: Model): ToggleButton("Cascade") {

    init {
        setSelected(true)

        setGraphic(model.getIconImage("cascade"))

        onAction = EventHandler {
            model.setViewMode("cascade")
            model.setDisableNonTileButtons(false)
            model.updateCanvasSize()
        }
    }
}