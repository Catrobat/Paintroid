/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.command

import org.catrobat.paintroid.model.CommandManagerModel

interface CommandManager {
    val isUndoAvailable: Boolean
    val isRedoAvailable: Boolean
    val lastExecutedCommand: Command?
    val isBusy: Boolean
    val commandManagerModel: CommandManagerModel?

    fun addCommandListener(commandListener: CommandListener)

    fun removeCommandListener(commandListener: CommandListener)

    fun addCommand(command: Command?)

    fun addCommandWithoutUndo(command: Command?)

    fun setInitialStateCommand(command: Command)

    fun loadCommandsCatrobatImage(model: CommandManagerModel?)

    fun undo()

    fun redo()

    fun reset()

    fun shutdown()

    fun undoIgnoringColorChanges()

    fun undoIgnoringColorChangesAndAddCommand(command: Command)

    fun undoInConnectedLinesMode()

    fun redoInConnectedLinesMode()

    fun getCommandManagerModelForCatrobatImage(): CommandManagerModel?

    fun adjustUndoListForClippingTool()

    fun undoInClippingTool()

    fun popFirstCommandInUndo()

    fun popFirstCommandInRedo()

    fun executeAllCommands()

    fun getUndoCommandCount(): Int

    fun getColorCommandCount(): Int

    fun isLastColorCommandOnTop(): Boolean

    interface CommandListener {
        fun commandPostExecute()
    }
}
