// package ui.lectures.javafx.mvc.javafxmvc.model

import controller.TitleScene
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.math.max
import kotlin.math.min

/**
 * Model represents the "Model" in "Model-View-Controller".
 */
class Model : Observable {

    // a list of all subscribed views / views that listen to the model / views that observe the model
    private val views = mutableListOf<InvalidationListener?>()

    /**
     * Add listener to receive notifications about changes in the [Model].
     * @param listener the listener that is added to the [Model]
     */
    override fun addListener(listener: InvalidationListener?) {
        views.add(listener)
    }

    /**
     * Remove listener to stop receiving notifications about changes in the [Model].
     * @param listener the listener that is removed from the [Model]
     */
    override fun removeListener(listener: InvalidationListener?) {
        views.remove(listener)
    }

    // the current value of the Model
    private var myValue = 0.0
    enum class SCENES {SCENE1, SCENE2}

    var level = 1
    var score = 0
    var lives = Global.STARTING_LIVES
    var gameCleared = false

    fun updateGameCleared() {
        gameCleared = true
        views.forEach { it?.invalidated(this) }
    }

    fun updateGameFailed() {
        gameCleared = false
        views.forEach{ it?.invalidated(this)}
    }

    fun restartGame(newLevel: Int) {
        level = newLevel
        score = 0
        lives = 3
        views.forEach { it?.invalidated(this) }
    }

    fun updateScore() {
        score += Global.SCORE_ADDITION
        views.forEach { it?.invalidated(this) }
    }

    fun lostLife() {
        lives = max(0, lives - 1)
        views.forEach { it?.invalidated(this) }
    }

    fun nextLevel(nextLevel: Int) {
        level = min(3, nextLevel)
        views.forEach { it?.invalidated(this) }
    }

    fun setScene(stage: Stage, sceneType: SCENES, actualScene: Scene) {

        stage.apply {
            title = "Space Invaders"
            scene = actualScene
        }.show()

        /** when (sceneType) {
            SCENES.SCENE1 ->
                stage.apply {
                    title = "Space Invaders"
                    scene = actualScene
                }.show()

            SCENES.SCENE2 ->
                stage.apply {
                    title = "Space Invaders"
                    scene = actualScene
                }.show()
        } **/
    }
}