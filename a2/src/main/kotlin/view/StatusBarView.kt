package ui.lectures.javafx.mvc.javafxmvcextended.view
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model

/**
 * StatusBarView displays the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model] on a label.
 */
class StatusBarView(private val model: Model) : HBox(), InvalidationListener {

    var statusBarLabel = Label()

    init {
        model.addListener(this) // subscribe to the Model
        invalidated(null) // call to set initial text

        var backgroundFill = BackgroundFill(Color.valueOf("#ffffff"), CornerRadii(0.0), Insets(0.0))
        var background = Background(backgroundFill)
        setBackground(background)
        children.add(statusBarLabel)
    }

    override fun invalidated(observable: Observable?) {
        var selectedImageText = ""
        if (model.getSelectedImage() != null) {
            selectedImageText = ", selected image: ${model.getSelectedImage()?.path}"
        } else {
            selectedImageText = ", no image selected"
        }
        statusBarLabel.text = "${model.getNumImages()} images loaded" + selectedImageText // set text of label if notified by the Model
    }
}