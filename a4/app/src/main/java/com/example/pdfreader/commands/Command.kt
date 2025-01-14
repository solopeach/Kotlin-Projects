package ui.lectures.undoredo.backward.model.commands

import android.graphics.Path
import com.example.pdfreader.Page

/**
 * Encapsulates a command.
 */
interface Command {

    /**
     * Executes the command f(value).
     * @param value the current value
     * @return f(value)
     */
    fun execute()
}