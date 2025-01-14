package ui.lectures.undoredo.backward.model.commands

import android.graphics.Path
import android.util.Log
import com.example.pdfreader.Page

/**
 * Encapsulates an increment command, i.e., a command that increments a value by 1.
 */
class EraseCommand(stroke: Path, page: Page) : UndoableCommand {

    var curPage: Page = page
    var curStroke: Path = stroke
    var erasedDrawPaths: MutableList<Path> = mutableListOf()
    var erasedHightlightPaths: MutableList<Path> = mutableListOf()
    /**
     * Executes the command inc(value).
     * @param value the current value
     * @return inc(value)
     */
    override fun execute() {
        println("execute erase")
        checkEraseIntersection(curPage.drawPaths, 0)
        checkEraseIntersection(curPage.highlightPaths, 1)
    }

    fun checkEraseIntersection(paths: MutableList<Path?>, strokeType: Int) {
        Log.d("erase","bedge!, ${paths.size}")
        val pathsToRemove = mutableListOf<Path>()
        for (path in paths) {
            var result = Path()
            if (result.op(path!!, curStroke!!, Path.Op.INTERSECT) == true) {
                if (!result.isEmpty) {
                    Log.d("erase", "one erased")
                    pathsToRemove.add(path)
                }
            }
        }
        if (strokeType == 0) {
            erasedDrawPaths = pathsToRemove
        } else if (strokeType == 1) {
            erasedHightlightPaths = pathsToRemove
        }
        paths.removeAll(pathsToRemove)
    }

    /**
     * Executes the command dec(value).
     * @param value the current value
     * @return dec(value)
     */
    override fun undo() {
        println("undo erase $erasedDrawPaths.size $erasedHightlightPaths.size")
        curPage.drawPaths.addAll(erasedDrawPaths)
        curPage.highlightPaths.addAll(erasedHightlightPaths)
    }
}