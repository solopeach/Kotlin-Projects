package com.example.pdfreader

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.properties.Delegates

// code for custom scrollview class from the link below:
// https://gist.github.com/chittaranjan-khuntia/42d5429ac37b7aea3cb22fb51c8729b4
class LockableScrollView : ScrollView {
    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    var isScrollable = true
        private set

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    fun setScrollingEnabled(enabled: Boolean) {
        isScrollable = enabled
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // TODO Auto-generated method stub
        Log.i("LockableScrollView", "onKeyDown onKeyDown")
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        // TODO Auto-generated method stub
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // if we can scroll pass the event to the superclass
                if (isScrollable) super.onTouchEvent(ev) else isScrollable
                // only continue to handle the touch event if scrolling enabled
                // mScrollable is always false at this point
            }

            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        return if (!isScrollable) false else super.onInterceptTouchEvent(ev)
    }
}


// PDF sample code from
// https://medium.com/@chahat.jain0/rendering-a-pdf-document-in-android-activity-fragment-using-pdfrenderer-442462cb8f9a
// Issues about cache etc. are not at all obvious from documentation, so we should expect people to need this.
// We may wish to provide this code.
class MainActivity : AppCompatActivity() {
    val LOGNAME = "pdf_viewer"
    val FILENAME = "psych11.pdf"
    val FILERESID = R.raw.psych11
    var sampleText: String = "wer"
    var justResumed: Boolean = false

    // manage the pages of the PDF, see below
    lateinit var pdfRenderer: PdfRenderer
    lateinit var parcelFileDescriptor: ParcelFileDescriptor
    var currentPage: PdfRenderer.Page? = null
    var currentPageIndex: Int = 0
    val FIRST_PAGE_INDEX: Int = 0
    var LAST_PAGE_INDEX by Delegates.notNull<Int>()
    var TOTAL_PAGES by Delegates.notNull<Int>()
    var pdfScrollView: LockableScrollView? = null
    var scrollButton: Button? = null

    // custom ImageView class that captures strokes and draws them over the image
    lateinit var pageImage: PDFimage

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("myTag", "This is my message");

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (this.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) pdfScrollView = findViewById<LockableScrollView>(R.id.pdfScrollView)

        val drawButton = findViewById<Button>(R.id.draw_button)
        drawButton.setOnClickListener {
            println("Draw Button clicked")
            pageImage.setBrushDraw()
            pageImage.setIsPan(false)
            pdfScrollView?.setScrollingEnabled(false)
        }

        val eraseButton = findViewById<Button>(R.id.erase_button)
        eraseButton.setOnClickListener {
            println("Erase Button clicked $pdfScrollView")
            pageImage.setBrushErase()
            pageImage.setIsPan(false)
            pdfScrollView?.setScrollingEnabled(false)
        }

        val highlightButton = findViewById<Button>(R.id.highlight_button)
        highlightButton.setOnClickListener {
            println("Highlight Button clicked $pdfScrollView")
            pageImage.setBrushHighlight()
            pageImage.setIsPan(false)
            pdfScrollView?.setScrollingEnabled(false)
        }

        val panButton = findViewById<Button>(R.id.pan_button)
        panButton.setOnClickListener {
            println("panButton Button clicked $pdfScrollView")
            pageImage.setIsPan(true)
            pdfScrollView?.setScrollingEnabled(false)
        }

        var scrollButton = findViewById<Button>(R.id.scroll_button)
        scrollButton.setOnClickListener {
            println("scrollButton Button clicked $pdfScrollView")
            pageImage.currentMatrix = Matrix()
            pageImage.setIsPan(true)
            pdfScrollView?.setScrollingEnabled(true)
        }
        if (this.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) scrollButton.isEnabled = false
        this.scrollButton = scrollButton

        val leftButton = findViewById<Button>(R.id.left_page_button)
        val rightButton = findViewById<Button>(R.id.right_page_button)
        val undoButton = findViewById<Button>(R.id.undo_button)
        val redoButton = findViewById<Button>(R.id.redo_button)

        leftButton.setOnClickListener {
            println("left button clicked")
            // check that there is a previous page
            if (currentPageIndex != 0) {
                currentPageIndex--
                pageImage.setCurrentPage(currentPageIndex)
                showPage(currentPageIndex)
                setPageCountText()
                rightButton.setEnabled(true)
                if (currentPageIndex == FIRST_PAGE_INDEX) {
                    leftButton.setEnabled(false)
                }
            }
        }

        rightButton.setOnClickListener {
            println("right button clicked")

            // check that there is a next page
            if (currentPageIndex != LAST_PAGE_INDEX) {
                currentPageIndex++
                pageImage.setCurrentPage(currentPageIndex)
                showPage(currentPageIndex)
                setPageCountText()
                leftButton.setEnabled(true)
                if (currentPageIndex == LAST_PAGE_INDEX) {
                    rightButton.setEnabled(false)
                }
            }
        }

        undoButton.setOnClickListener {
            println("Undo Button clicked")
            pageImage.undo()
        }

        redoButton.setOnClickListener {
            println("Redo Button clicked")
            pageImage.redo()
        }

        val layout = findViewById<LinearLayout>(R.id.pdfLayout)
        layout.isEnabled = true

        pageImage = PDFimage(this)
        pageImage.minimumWidth = 1000
        pageImage.minimumHeight = 2000
        layout.addView(pageImage)

        // open page 0 of the PDF
        // it will be displayed as an image in the pageImage (above)
        try {
                println("no restoring")
                val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
                val file = File(this.cacheDir, FILENAME)
                toolbar.setTitle(file.name)
                setSupportActionBar(toolbar)

                openRenderer(this)
                LAST_PAGE_INDEX = pdfRenderer.pageCount - 1
                TOTAL_PAGES = pdfRenderer.pageCount
                setPageCountText()

                leftButton.setEnabled(false)

                pageImage.setCurrentPage(currentPageIndex)
                showPage(currentPageIndex)

                Log.i("OrientationChange", "Here again")
                // closeRenderer()
        } catch (exception: IOException) {
            Log.d(LOGNAME, "Error opening PDF")
        }
    }

    // fun restoreState(inState: Bundle?) {
    //     println("restoring state in onCreate")
    //     with (inState) {
    //         currentPageIndex = this!!.getInt("currentPageIndex")
    //         sampleText = this!!.getString("sampleText").toString()
    //     }
    // }

    fun setPageCountText() {
        val pageCountText = findViewById<TextView>(R.id.page_count_text)
        pageCountText.setText("${currentPageIndex + 1}/${TOTAL_PAGES}")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                val layout = findViewById<LinearLayout>(R.id.pdfLayout)
                layout.removeView(pageImage)
                layout.addView(pageImage)
                pageImage.resetPivot()
                pageImage.currentMatrix = Matrix()
                scrollButton?.isEnabled = true
                pdfScrollView = findViewById<LockableScrollView>(R.id.pdfScrollView)
                Log.i("OrientationChange", "Landscape: ${pageImage.getX()} ${pageImage.getY()} ${pageImage.getWidth()} ${pageImage.getHeight()}")}
            Configuration.ORIENTATION_PORTRAIT -> {
                val layout = findViewById<LinearLayout>(R.id.pdfLayout)
                layout.removeView(pageImage)
                pageImage.resetPivot()
                layout.addView(pageImage)
                pageImage.currentMatrix = Matrix()
                scrollButton?.isEnabled = false
                pdfScrollView = null
                Log.i("OrientationChange", "Portrait: ${pageImage.getX()} ${pageImage.getY()} ${pageImage.getWidth()} ${pageImage.getHeight()}") }
            else -> {
                Log.e("OrientationChange", "Whaaat...") }
        }
    }

    @Throws(IOException::class)
    private fun openRenderer(context: Context) {
        // In this sample, we read a PDF from the assets directory.
        val file = File(context.cacheDir, FILENAME)

        if (!file.exists()) {
            // pdfRenderer cannot handle the resource directly,
            // so extract it into the local cache directory.
            val asset = this.resources.openRawResource(FILERESID)
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size: Int
            while (asset.read(buffer).also { size = it } != -1) {
                output.write(buffer, 0, size)
            }
            asset.close()
            output.close()
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

        // capture PDF data
        // all this just to get a handle to the actual PDF representation
        pdfRenderer = PdfRenderer(parcelFileDescriptor)
    }

    // do this before you quit!
    @Throws(IOException::class)
    private fun closeRenderer() {
        println("closing current page ${currentPageIndex}")
        currentPage?.close()
        pdfRenderer.close()
        parcelFileDescriptor.close()
    }

    private fun showPage(index: Int) {
        if (pdfRenderer.pageCount <= index) {
            return
        }
        // Close the current page before opening another one.
        currentPage?.close()

        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index)

        if (currentPage != null) {
            // Important: the destination bitmap must be ARGB (not RGB).
            val bitmap = Bitmap.createBitmap(currentPage!!.getWidth(), currentPage!!.getHeight(), Bitmap.Config.ARGB_8888)

            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            currentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Display the page
            pageImage.setImage(bitmap)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        println("saving state in onSaveInstanceState")

        // save state
        with (outState) {
            putInt("currentPageIndex", currentPageIndex)
            putString("sampleText", "poo")
        }

        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        try {
            println("stopping!")
            closeRenderer()
        } catch (ex: IOException) {
            Log.d(LOGNAME, "Unable to close PDF renderer")
        }
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
    }

    override fun onResume() {
        super.onResume()
        // justResumed = true
        println("onResume")
    }

    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
    }
}