package ui.lectures.javafx.mvc.javafxmvcextended.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model

/**
 * Activating the IncrementButton increments the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model].
 */
class ZoomOut(private val model: Model): Button("Zoom Out") {

    fun getScaledImageWidth(selectedImage: ImageViewZ): Double {

        selectedImage.numZooms--

        selectedImage.zoomRatio = selectedImage.zoomRatio * 0.75
        return model.initialWidth * selectedImage.zoomRatio

    }

    init {
        disableProperty().bind(model.booleanBinding)
        setGraphic(model.getIconImage("zoomOut"))

        onAction = EventHandler {

            val selectedImage = model.getSelectedImage()

            if (selectedImage != null) {
                if (selectedImage.numZooms > -model.maxNumZoomOuts) {
                    selectedImage.setFitWidth(getScaledImageWidth(selectedImage))

                    model.updateCanvasSize()
                }
            }
        }
    }
}