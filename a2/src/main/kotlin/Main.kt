// remove this
// package ui.lectures.javafx.mvc.javafxmvc

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import ui.lectures.javafx.mvc.javafxmvcextended.controller.Tile
import ui.lectures.javafx.mvc.javafxmvcextended.model.Model
import ui.lectures.javafx.mvc.javafxmvcextended.view.*

class Main : Application() {

    override fun start(stage: Stage) {

        val myModel = Model()

        // put the panels side-by-side in a container
        val root = BorderPane().apply {
            center = CanvasView(myModel, stage)
            top = ToolBarView(myModel, stage)
            bottom = StatusBarView(myModel)
        }

        stage?.run {
            title = "LightBox - Roni Wu"
            scene = Scene(root, 1000.0, 600.0)
            minHeight = 300.0
            minWidth = 400.0
            show()

        }
    }
}