package ui.lectures.javafx.mvc.javafxmvcextended.controller

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ToggleButton
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model

/**
 * Activating the IncrementButton increments the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model].
 */
class Tile(private val model: Model): ToggleButton("Tile") {

    init {
        setGraphic(model.getIconImage("tile"))

        onAction = EventHandler {
            model.setViewMode("tile")
            model.setDisableNonTileButtons(true)
        }
    }
}