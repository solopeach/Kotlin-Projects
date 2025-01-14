package ui.lectures.javafx.mvc.javafxmvcextended.model

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.effect.Shadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.math.BigDecimal
import java.math.RoundingMode
import ui.lectures.javafx.mvc.javafxmvcextended.controller.ImageViewZ
import java.io.File
import java.io.FileInputStream
import java.lang.Math.random
import kotlin.math.max
import kotlin.math.round

import kotlin.math.roundToInt
import kotlin.random.Random

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

    val initialWidth = 200.0

    // the current value of the Model
    private var viewMode = "cascade"
    private var images = mutableListOf<ImageViewZ>()
    private var topZIndex = 0
    private var selectedImage: ImageViewZ? = null
    var isDelButtonDisabled = true
    val maxNumZoomIns = 4
    val maxNumZoomOuts = 4

    var maxPaneWidth: Double = 0.0
    var maxPaneHeight: Double = 0.0

    var disableNonTileButtons = SimpleBooleanProperty()
    var disableButtonsWhenNoSelectedImage = SimpleBooleanProperty()
    var booleanBinding = disableNonTileButtons.or(disableButtonsWhenNoSelectedImage)

    fun setDisableNonTileButtons(value: Boolean) {
        disableNonTileButtons.set(value)
    }

    fun getDisableNonTileButtons(): Boolean {
        return disableNonTileButtons.get()
    }

    fun setDisableButtonsWhenNoSelectedImage(value: Boolean) {
        disableButtonsWhenNoSelectedImage.set(value)
    }

    fun getDisableButtonsWhenNoSelectedImage(): Boolean {
        return disableButtonsWhenNoSelectedImage.get()
    }

    fun getSelectedImage() : ImageViewZ? {
        return selectedImage
    }

    fun resetImage(image: ImageViewZ) {
        image.rotateAngle = 0.0
        image.zoomRatio = 1.0

        image.setFitWidth(initialWidth)
        image.rotate = 0.0
        image.numZooms = 0

        views.forEach { it?.invalidated(this) }
    }

    fun getTopZIndex() : Int {
        return topZIndex
    }

    fun getNumImages(): Int {
        return images.size
    }

    fun getViewMode(): String {
        return viewMode
    }

    fun getImages(): MutableList<ImageViewZ> {
        return images
    }

    fun setViewMode(newViewMode: String) {
        // println(newViewMode)
        viewMode = newViewMode
        views.forEach { it?.invalidated(this) }
    }

    fun setImages(newImages: MutableList<ImageViewZ>) {
        images = newImages
    }

    fun updateNoSelected() {
        // println("update no selected")
        setDisableButtonsWhenNoSelectedImage(true)

        selectedImage?.setEffect(null)
        selectedImage = null
        isDelButtonDisabled = true
        views.forEach { it?.invalidated(this) }
    }

    fun updateCanvasSize() {

        maxPaneHeight = 0.0
        maxPaneWidth = 0.0

        images.forEach() {
            var idk = it.getBoundsInParent()
            // println("idk: " + idk)

            if (idk != null) {
                if (idk.getMaxX() > maxPaneWidth) {
                    maxPaneWidth = idk.getMaxX()
                }

                if (idk.getMaxY() > maxPaneHeight) {
                    maxPaneHeight = idk.getMaxY()
                }
            }
        }
        views.forEach { it?.invalidated(this) }
    }

    fun updateSelectedImage(activeImage: ImageViewZ) {
        setDisableButtonsWhenNoSelectedImage(false)
        incrementTopZIndex()

        images.map{
            if (it == activeImage) {
                it.zindex = topZIndex
            }
        }

        val dropShadow = DropShadow()
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.rgb(76, 158, 224));

        selectedImage?.setEffect(null)
        activeImage.setEffect(dropShadow)

        activeImage.zindex = topZIndex
        // println("Handler target: ${activeImage.zindex}")

        // println("update image order")

        if (viewMode == "cascade") {
            setImages(images.sortedBy { imageviewz -> imageviewz.zindex }.toMutableList())
        }
        selectedImage = activeImage
        views.forEach { it?.invalidated(this) }
    }

    fun getIconImage(name: String): ImageView {
        val icon = ImageView(Image("${name}.png"))
        return icon
    }

    val widthBuffer = 20.0
    var heightBuffer = 100.0

    fun addImage(image: ImageViewZ, stageWidth: Double, stageHeight: Double) {
        image.setFitWidth(initialWidth)
        image.setPreserveRatio(true)

        val aspectRatio = image.getImage().getWidth() / image.getImage().getHeight()

        val scaledHeight = initialWidth / aspectRatio

        // println("${stageWidth} x ${stageHeight}")
        // println("${image.getImage().getWidth()} x ${ image.getImage().getHeight()}")
        // println("${initialWidth} x ${scaledHeight}")

        images.add(image)
        isDelButtonDisabled = false
        // println("image added")
        // println(images.size)

        val randomX = Math.random()*(stageWidth - image.fitWidth - widthBuffer) // initialWidth
        val randomY = Math.random()*(stageHeight - scaledHeight - heightBuffer)

        // image.setX(stageWidth - image.fitWidth - 20.0)
        // image.setY(stageHeight - scaledHeight - 100.0)
        image.setX(randomX)
        image.setY(randomY)

        // println("${randomX} x ${randomY}")

        updateCanvasSize()

        views.forEach { it?.invalidated(this) }
    }

    fun incrementTopZIndex() {
        topZIndex++
    }
}