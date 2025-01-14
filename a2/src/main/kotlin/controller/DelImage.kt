package ui.lectures.javafx.mvc.javafxmvcextended.controller

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model

/**
 * Activating the IncrementButton increments the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model].
 */
class DelImage(private val model: Model): Button("Del Image") {

    init {
        disableProperty().bind(model.disableButtonsWhenNoSelectedImage)
        setGraphic(model.getIconImage("delete"))

        onAction = EventHandler {
            model.getImages().map{
                if (it == model.getSelectedImage()) {
                    it.setImage(null)
                }
            }
            model.getImages().remove(model.getSelectedImage())

            model.updateCanvasSize()
        }
    }
}