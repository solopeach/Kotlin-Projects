package ui.lectures.undoredo.backward.model.commands

import android.graphics.Path
import com.example.pdfreader.Page

/**
 * Encapsulates a reset command, i.e., a command that resets a value to its original state.
 */
class DrawCommand(stroke: Path, page: Page) : UndoableCommand {

    var curPage: Page = page
    var curStroke: Path = stroke
    /**
     * Returns the [resetValue].
     * @param value the current value
     * @return [resetValue]
     */
    override fun execute() {
        println("execute draw")
        curPage.drawPaths.add(curStroke)
    }

    /**
     * Returns the value before the reset.
     * @param value ignored
     * @return value before the reset
     */
    override fun undo() {
        println("undo draw")
        curPage.drawPaths.remove(curStroke)
    }
}