package ui.lectures.javafx.mvc.javafxmvcextended.controller

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model

/**
 * Activating the IncrementButton increments the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model].
 */
class Reset(private val model: Model): Button("Reset") {

    init {
        setGraphic(model.getIconImage("reset"))
        disableProperty().bind(model.booleanBinding)

        onAction = EventHandler {

            val image = model.getSelectedImage()
            if (image != null) {
                model.resetImage(image)
            }
            model.updateCanvasSize()
        }
    }
}