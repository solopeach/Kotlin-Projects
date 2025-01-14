package ui.lectures.javafx.mvc.javafxmvcextended.view

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.value.ChangeListener
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.control.ToolBar
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ui.lectures.javafx.mvc.javafxmvcextended.controller.*
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model

/**
 * ToolBarView displays the value of the [Model][ui.lectures.javafx.mvc.javafxmvcextended.model.Model] on a label.
 */
class ToolBarView(private val model: Model, stage: Stage) : VBox(), InvalidationListener {

    init {
        model.addListener(this) // subscribe to the Model
        invalidated(null) // call to set initial text

        val toggleGroup = ToggleGroup()
        val cascadeButton = Cascade(model)
        val tileButton = Tile(model)
        cascadeButton.setToggleGroup(toggleGroup)
        tileButton.setToggleGroup(toggleGroup)

        toggleGroup.selectedToggleProperty().addListener { observable, oldValue, newValue ->
            if (newValue == null) {
                oldValue.setSelected(true)
            }
        }

        children.addAll(
            ToolBar().apply {
                items.add(AddImage(model, stage))
                items.add(DelImage(model))
                items.add(RotateLeft(model))
                items.add(RotateRight(model))
                items.add(ZoomIn(model))
                items.add(ZoomOut(model))
                items.add(Reset(model))
                items.add(cascadeButton)
                items.add(tileButton)
            }
        )
    }

    override fun invalidated(observable: Observable?) {

    }

}