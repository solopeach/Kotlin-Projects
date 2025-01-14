package com.example.pdfreader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import ui.lectures.undoredo.backward.model.commands.DrawCommand
import ui.lectures.undoredo.backward.model.commands.EraseCommand
import ui.lectures.undoredo.backward.model.commands.HighlightCommand
import ui.lectures.undoredo.backward.model.commands.UndoableCommand


class Page(index: Int) {

    var drawPaths = mutableListOf<Path?>()
    var highlightPaths = mutableListOf<Path?>()

    // to track undo redo history
    var allPaths = mutableListOf<Path?>()

    var index: Int

    // undo redo stuff
    private val undoCommands = mutableListOf<Any>()
    private val redoCommands = mutableListOf<Any>()

    init {
        this.index = index
    }

    fun addHightlightStroke(path: Path) {
        // make new DrawCommand
        val newCommand = HighlightCommand(path, this)
        undoCommands.add(newCommand)
        newCommand.execute()
        redoCommands.clear()
        println("add highlight stroke ${undoCommands.size} ${redoCommands.size}")
    }

    fun addDrawStroke(path: Path) {
        // make new DrawCommand
        val newCommand = DrawCommand(path, this)
        undoCommands.add(newCommand)
        newCommand.execute()
        redoCommands.clear()
        println("add draw stroke ${undoCommands.size} ${redoCommands.size}")
    }

    fun addEraseStroke(path: Path) {
        // make new EraseCommand
        val newCommand = EraseCommand(path, this)
        undoCommands.add(newCommand)
        newCommand.execute()
        redoCommands.clear()
        println("add erase stroke ${undoCommands.size} ${redoCommands.size}")
    }

    fun undo() {
        println("undo on page $index")
        undoCommands.removeLastOrNull()?.apply {
            redoCommands.add(this)
            (this as UndoableCommand).undo()
        }
    }
    fun redo() {
        println("redo on page $index")
        redoCommands.removeLastOrNull()?.apply {
            undoCommands.add(this)
            (this as UndoableCommand).execute()
        }
    }
}

@SuppressLint("AppCompatCustomView")
class PDFimage  // constructor
    (context: Context?) : ImageView(context) {
    // we save a lot of points because they need to be processed
    // during touch events e.g. ACTION_MOVE
    var x1 = 0f
    var x2 = 0f
    var y1 = 0f
    var y2 = 0f
    var old_x1 = 0f
    var old_y1 = 0f
    var old_x2 = 0f
    var old_y2 = 0f
    var mid_x = -1f
    var mid_y = -1f
    var old_mid_x = -1f
    var old_mid_y = -1f
    var p1_id = 0
    var p1_index = 0
    var p2_id = 0
    var p2_index = 0
    var old_x = 0f
    var old_y = 0f

    // store cumulative transformations
    // the inverse matrix is used to align points with the transformations - see below
    var currentMatrix = Matrix()
    var inverse = Matrix()

    val LOGNAME = "pdf_image"

    // define pages
    var pages = mutableListOf<Page>()

    // drawing path
    var path: Path? = null
    // var drawPaths = mutableListOf<Path?>()
    // var highlightPaths = mutableListOf<Path?>()
    var erasePath: Path? = null
    var currentPage: Page? = null

    // image to display
    var bitmap: Bitmap? = null
    var paint = Paint()
    val drawPaint = Paint()
    val highlightPaint = Paint()
    val erasePaint = Paint()

    // scaling
    var mScaleFactor = 1f
    var isPan = true
    val scaleLimitMax = 30f
    val scaleLimitMin = -10f
    var scaleSum = 0f

    fun setIsPan(bool: Boolean) {
        isPan = bool
    }

    init {
        drawPaint.setStrokeWidth(8.0F)
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setColor(Color.CYAN)
        drawPaint.setAlpha(800)

        highlightPaint.setStrokeWidth(16.0F)
        highlightPaint.setAntiAlias(true);
        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setColor(Color.rgb(255, 247, 0))
        highlightPaint.setAlpha(80)

        erasePaint.setStrokeWidth(12.0F)
        erasePaint.setAntiAlias(true);
        erasePaint.setStyle(Paint.Style.STROKE);
        erasePaint.setColor(Color.TRANSPARENT)
        erasePaint.setAlpha(255)
    }

    fun undo() {
        currentPage?.undo()
    }
    fun redo() {
        currentPage?.redo()
    }

    fun setBrushDraw() {
        setBrush(drawPaint)
        erasePath = null
    }

    fun setBrushHighlight() {
        setBrush(highlightPaint)
        erasePath = null
    }

    fun setBrushErase() {
        println("set erase")
        setBrush(erasePaint)
        erasePath = null
    }

    fun resetCoordinates() {
        old_x1 = -1f
        old_y1 = -1f
        old_x2 = -1f
        old_y2 = -1f
        old_mid_x = -1f
        old_mid_y = -1f
    }

    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        var inverted = floatArrayOf()
        when (event.getPointerCount()) {
            2 -> {
                // point 1
                p1_id = event.getPointerId(0)
                p1_index = event.findPointerIndex(p1_id)

                // mapPoints returns values in-place
                inverted = floatArrayOf(event.getX(p1_index), event.getY(p1_index))
                inverse.mapPoints(inverted)

                // first pass, initialize the old == current value
                if (old_x1 < 0 || old_y1 < 0) {
                    x1 = inverted.get(0)
                    old_x1 = x1
                    y1 = inverted.get(1)
                    old_y1 = y1
                } else {
                    old_x1 = x1
                    old_y1 = y1
                    x1 = inverted.get(0)
                    y1 = inverted.get(1)
                }

                // point 2
                p2_id = event.getPointerId(1)
                p2_index = event.findPointerIndex(p2_id)

                // mapPoints returns values in-place
                inverted = floatArrayOf(event.getX(p2_index), event.getY(p2_index))
                inverse.mapPoints(inverted)

                // first pass, initialize the old == current value
                if (old_x2 < 0 || old_y2 < 0) {
                    x2 = inverted.get(0)
                    old_x2 = x2
                    y2 = inverted.get(1)
                    old_y2 = y2
                } else {
                    old_x2 = x2
                    old_y2 = y2
                    x2 = inverted.get(0)
                    y2 = inverted.get(1)
                }

                // midpoint
                mid_x = (x1 + x2) / 2
                mid_y = (y1 + y2) / 2
                old_mid_x = (old_x1 + old_x2) / 2
                old_mid_y = (old_y1 + old_y2) / 2

                // distance
                val d_old =
                    Math.sqrt(Math.pow((old_x1 - old_x2).toDouble(), 2.0) + Math.pow((old_y1 - old_y2).toDouble(), 2.0))
                        .toFloat()
                val d = Math.sqrt(Math.pow((x1 - x2).toDouble(), 2.0) + Math.pow((y1 - y2).toDouble(), 2.0))
                    .toFloat()

                // pan and zoom during MOVE event
                if (event.action == MotionEvent.ACTION_MOVE) {

                    // zoom == change of spread between p1 and p2
                    var scale = d / d_old
                    // if ((scaleSum >= scaleLimitMax && scale >= 1) || (scaleSum <= scaleLimitMin && scale <= 1)) {
                        // dont scale
                    //     Log.d(LOGNAME, "dont scale: $scaleSum $scale")
                    // } else {
                    //     if (scale < 1) {
                   //          scaleSum -= scale
                    //     } else {
                    //         scaleSum += scale
                    //     }
                        scale = Math.max(0f, scale)
                        currentMatrix.preScale(scale, scale, mid_x, mid_y)
                        Log.d(LOGNAME, "scale: $scaleSum $scale")
                    // }

                    // reset on up
                } else if (event.action == MotionEvent.ACTION_UP) {
                    resetCoordinates()
                }
            }

            1 -> {
                p1_id = event.getPointerId(0)
                p1_index = event.findPointerIndex(p1_id)

                // invert using the current matrix to account for pan/scale
                // inverts in-place and returns boolean
                inverse = Matrix()
                currentMatrix.invert(inverse)

                // mapPoints returns values in-place
                inverted = floatArrayOf(event.getX(p1_index), event.getY(p1_index))
                inverse.mapPoints(inverted)
                x1 = inverted[0]
                y1 = inverted[1]

                // println("${mid_x} ${mid_y} old: ${old_mid_x} ${old_mid_y}")

                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        Log.d(LOGNAME, "Action down")
                        if (!isPan) {
                            path = Path()
                            if (paint == drawPaint) {
                                Log.d(LOGNAME, "draw down")
                                currentPage!!.addDrawStroke(path!!)
                            }
                            if (paint == highlightPaint) {
                                Log.d(LOGNAME, "highlight down")
                                currentPage!!.addHightlightStroke(path!!)
                            }
                            if (paint == erasePaint) {
                                Log.d(LOGNAME, "erase down")
                                erasePath = path
                            }
                            path!!.moveTo(x1, y1)
                        }
                        old_x = x1
                        old_y = y1
                    }

                    MotionEvent.ACTION_MOVE -> {

                        if (!isPan) {
                            Log.d(LOGNAME, "Action move")
                            path!!.lineTo(x1, y1)
                        } else {
                            // pan == translate of midpoint
                            val dx = x1 - old_x
                            val dy = y1 - old_y
                            currentMatrix.preTranslate(dx, dy)
                            Log.d(LOGNAME, "translate: $dx,$dy")
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        Log.d(LOGNAME, "Action up")

                        if (isPan) {
                            resetCoordinates()
                            Log.d(LOGNAME, "Multitouch move")
                            // pan == translate of midpoint
                            // old_x = event.x
                            // old_y = event.y
                        } else if  (paint == erasePaint && erasePath != null) {
                            // println("check intersection")
                            currentPage!!.addEraseStroke(path!!)
                        }
                    }
                }
            }
        }

        return true
    }

    // set image as background
    fun setImage(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    // set brush characteristics
    // e.g. color, thickness, alpha
    fun setBrush(paint: Paint) {
        this.paint = paint
    }

    fun setCurrentPage(index: Int) {

        for (page in pages) {
            if (page.index == index) {
                currentPage = page
                return
            }
        }

        // no page found
        val newPage = Page(index)
        pages.add(newPage)
        currentPage = newPage
    }

    override fun onDraw(canvas: Canvas) {

        // draw background
        if (bitmap != null) {
            setImageBitmap(bitmap)
        }

        // apply transformations from the event handler above
        // scaleImage()
        // this.setScaleType(ImageView.ScaleType.MATRIX)
        // this.setImageMatrix(currentMatrix)
        canvas.concat(currentMatrix)

        super.onDraw(canvas)

        // draw lines over it
        if (currentPage != null) {
            for (path in currentPage!!.drawPaths) {
                var bounds = RectF()
                // path!!.computeBounds(bounds, false)
                // println("drawns path: ${bounds.centerX()} ${bounds.centerY()}")
                path?.let { canvas.drawPath(it, drawPaint) }
                // path!!.moveTo(bounds.centerX(), bounds.centerY())
                // path.offset(bounds.centerX(), bounds.centerY())

                // println("drawn path: ${bounds.centerX()} ${bounds.centerY()}")
                // println("sizes: $this.width $this.height")
                // println("-----------------------------------------")
            }

            for (path in currentPage!!.highlightPaths) {
                path?.let { canvas.drawPath(it, highlightPaint) }
            }

            // println("${currentPage!!.drawPaths.size}")
        }
    }
}
