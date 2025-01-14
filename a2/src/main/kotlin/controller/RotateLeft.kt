package ui.lectures.javafx.mvc.javafxmvcextended.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.transform.Rotate
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model

/**
 * Activating the IncrementButton increments the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model].
 */
class RotateLeft(private val model: Model): Button("Rotate Left") {

    init {
        disableProperty().bind(model.booleanBinding)
        setGraphic(model.getIconImage("rotateLeft"))

        onAction = EventHandler {
            var image = model.getSelectedImage()
            if (image != null) {
                image.rotateAngle -= 10.0
                image.rotate = image.rotateAngle

                model.updateCanvasSize()
            }
        }
    }
}