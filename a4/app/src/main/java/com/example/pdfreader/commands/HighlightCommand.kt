package ui.lectures.undoredo.backward.model.commands

import android.graphics.Path
import com.example.pdfreader.Page

/**
 * Encapsulates an increment command, i.e., a command that increments a value by 1.
 */
class HighlightCommand(stroke: Path, page: Page) : UndoableCommand {
    /**
     * Executes the command inc(value).
     * @param value the current value
     * @return inc(value)
     */

    var curPage: Page = page
    var curStroke: Path = stroke
    override fun execute() {
        println("execute highlight $curPage.index")
        curPage.highlightPaths.add(curStroke)
    }

    /**
     * Executes the command dec(value).
     * @param value the current value
     * @return dec(value)
     */
    override fun undo() {
        println("undo highlight")
        curPage.highlightPaths.remove(curStroke)
    }
}