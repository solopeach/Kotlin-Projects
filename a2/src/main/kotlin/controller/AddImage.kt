package ui.lectures.javafx.mvc.javafxmvcextended.controller

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.stage.FileChooser
import javafx.stage.Stage
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model
import java.io.FileInputStream
import java.nio.file.Path

/**
 * Activating the IncrementButton increments the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model].
 */
class ImageViewZ(var zindex: Int, val path: String): ImageView() {

    var zoomRatio = 1.0
    var numZooms = 0
    var rotateAngle = 0.0
    var startDragX = 0.0
    var startDragY = 0.0
    init {
        // println("new image + ${zindex}")
    }
}

class AddImage(private val model: Model, stage: Stage): Button("Add Image") {

    init {
        setGraphic(model.getIconImage("add"))

        onAction = EventHandler {

            val imageChooser = FileChooser()
            imageChooser.setTitle("Select an image")

            imageChooser.getExtensionFilters().addAll(
                FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp"),
            );

            val selectedImage = imageChooser.showOpenDialog(stage)

            if (selectedImage != null) {
                val stream = FileInputStream(selectedImage.path)
                val image = Image(stream)

                model.incrementTopZIndex()
                val newImage = ImageViewZ(model.getTopZIndex(), selectedImage.name)
                newImage.setImage(image)

                var setOnMouseDragged = { event: MouseEvent ->
                     // println(event)
                     val newX = event.getSceneX() - newImage.startDragX;
                     val newY = event.getSceneY() - newImage.startDragY;
                     newImage.setTranslateX(newX)
                     newImage.setTranslateY(newY)

                }

                var setOnMousePressed = { event: MouseEvent ->
                    // println(event)
                    newImage.startDragX = event.getSceneX() - newImage.getTranslateX()
                    newImage.startDragY = event.getSceneY() - newImage.getTranslateY()

                    model.updateSelectedImage(newImage)
                    event.consume()
                }

                // set on mouse released was overpowering the MouseClicked event, leading to the
                // ScrollPane consuming the MouseClicked event, which happens every time
                // var setOnMouseReleased = { event: MouseEvent ->
                    // model.invalidate()
                  //  println(event)
                 //   println("mouse released image " + "source: ${it.source.javaClass}" + "target: ${it.target.javaClass}")
                //   event.consume()
                 //   model.updateCanvasSize()

                // }

                var setOnMouseClicked = { event: MouseEvent ->
                    // println(event)
                    model.updateSelectedImage(newImage)
                    model.updateCanvasSize()
                    event.consume()
                }

                newImage.addEventHandler(MouseEvent.MOUSE_CLICKED, setOnMouseClicked)
                // newImage.addEventHandler(MouseEvent.MOUSE_RELEASED, setOnMouseReleased)
                newImage.addEventHandler(MouseEvent.MOUSE_DRAGGED, setOnMouseDragged)
                newImage.addEventHandler(MouseEvent.MOUSE_PRESSED, setOnMousePressed)

                model.updateSelectedImage(newImage)
                model.addImage(newImage, stage.getWidth(), stage.getHeight())
            }
        }
    }
}