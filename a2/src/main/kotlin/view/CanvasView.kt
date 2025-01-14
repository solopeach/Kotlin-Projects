package ui.lectures.javafx.mvc.javafxmvcextended.view
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model
import java.awt.Insets
import java.util.concurrent.Flow

/**
 * StatusBarView displays the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model] on a label.
 */
class CanvasView(private val model: Model, stage: Stage) : ScrollPane(), InvalidationListener {

    var ImagesPane = Pane()
    var TilePane = FlowPane()

    init {
        model.addListener(this) // subscribe to the Model
        invalidated(null) // call to set initial text

        TilePane.setOrientation(Orientation.HORIZONTAL)
        TilePane.setPadding(javafx.geometry.Insets(10.0, 10.0, 10.0, 10.0));
        TilePane.setHgap(5.0)
        TilePane.setVgap(5.0)
        TilePane.prefWidthProperty().bind(stage.widthProperty())

        var handler = { it: MouseEvent ->

            model.updateNoSelected()
            if (model.getViewMode() == "tile") {
                it.consume()
            }
        }

        var disableDrag = { it: MouseEvent ->
            if (model.getViewMode() == "tile") {
                it.consume()
            }
        }

        addEventHandler(MouseEvent.MOUSE_CLICKED, handler)
        addEventFilter(MouseEvent.MOUSE_DRAGGED, disableDrag)

    }

    override fun invalidated(observable: Observable?) {
        // println("refresh canvas")

        if (model.getViewMode() == "cascade") {
            ImagesPane.children.clear()
            TilePane.children.clear()

            setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED)

            model.getImages().forEach() {
                ImagesPane.children.add(it)
                // println("image location: ${it.getX()} x ${it.getY()}")
            }

            ImagesPane.setPrefSize(model.maxPaneWidth, model.maxPaneHeight)
            // println("pane dims: ${model.maxPaneWidth} x ${model.maxPaneHeight}")

            setContent(ImagesPane)
        } else {
            // tile mode
            ImagesPane.children.clear()
            TilePane.children.clear()

            setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER)

            model.getImages().forEach() {
                it.rotateAngle = 0.0
                it.zoomRatio = 1.0

                it.setFitWidth(model.initialWidth)
                it.rotate = 0.0
                it.numZooms = 0
                it.setTranslateX(0.0)
                it.setTranslateY(0.0)

                TilePane.children.add(it)
                // println("image location: ${it.getX()} x ${it.getY()}")
            }

            // println("tile mode: ${TilePane.getWidth()} x ${TilePane.getHeight()}")
            // println("scroll pane: ${getWidth()} x ${getHeight()}")
            // TilePane.prefWidthProperty().bind()

            setContent(TilePane)

        }
    }
}