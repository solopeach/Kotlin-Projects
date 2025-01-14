package ui.lectures.javafx.mvc.javafxmvcextended.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model

/**
 * Activating the IncrementButton increments the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model].
 */
class ZoomIn(private val model: Model): Button("Zoom In") {

    fun getScaledImageWidth(selectedImage: ImageViewZ): Double {

        selectedImage.numZooms++

        selectedImage.zoomRatio = selectedImage.zoomRatio * 1.25
        val newWidth = model.initialWidth * selectedImage.zoomRatio

        return newWidth
    }

    init {
        disableProperty().bind(model.booleanBinding)
        setGraphic(model.getIconImage("zoomIn"))

        onAction = EventHandler {

            val selectedImage = model.getSelectedImage()

            if (selectedImage != null) {
                if (selectedImage.numZooms < model.maxNumZoomIns) {
                    selectedImage.setFitWidth(getScaledImageWidth(selectedImage))
                    model.updateCanvasSize()

                    // println("zooms: " + selectedImage.numZooms)
                }
            }

            model.updateCanvasSize()
        }
    }
}