package ui.lectures.undoredo.backward.model.commands

import android.graphics.Path
import com.example.pdfreader.Page

/**
 * Encapsulates an undo-able command.
 */
interface UndoableCommand : Command {

    /**
     * Executes the command f^-1(value), i.e., undo f(value).
     * @param value the current value
     * @return f^-1(value)
     */
    fun undo()
}