/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.command.implementation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.CommandManager.CommandListener
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.CommandManagerModel

open class AsyncCommandManager(
    private val commandManager: CommandManager,
    private val layerModel: LayerContracts.Model
) : CommandManager {
    private val commandListeners: MutableList<CommandListener> = ArrayList()
    private var shuttingDown = false
    private var mutex = Mutex()

    override val isBusy: Boolean
        get() = mutex.isLocked

    override val lastExecutedCommand: Command?
        get() = commandManager.lastExecutedCommand

    override val commandManagerModel
        get() = commandManager.commandManagerModel

    override val isUndoAvailable: Boolean
        get() = commandManager.isUndoAvailable

    override val isRedoAvailable: Boolean
        get() = commandManager.isRedoAvailable

    override fun addCommandListener(commandListener: CommandListener) {
        commandListeners.add(commandListener)
    }

    override fun removeCommandListener(commandListener: CommandListener) {
        commandListeners.remove(commandListener)
    }

    override fun addCommand(command: Command?) {
        CoroutineScope(Dispatchers.Default).launch {
            mutex.withLock {
                if (!shuttingDown) {
                    synchronized(layerModel) { commandManager.addCommand(command) }
                }
                withContext(Dispatchers.Main) {
                    commandManager.adjustUndoListForClippingTool()
                    notifyCommandPostExecute()
                }
            }
        }
    }

    override fun addCommandWithoutUndo(command: Command?) {
        CoroutineScope(Dispatchers.Default).launch {
            mutex.withLock {
                if (!shuttingDown) {
                    synchronized(layerModel) { commandManager.addCommandWithoutUndo(command) }
                }
                withContext(Dispatchers.Main) {
                    notifyCommandPostExecute()
                }
            }
        }
    }

    override fun loadCommandsCatrobatImage(model: CommandManagerModel?) {
        CoroutineScope(Dispatchers.Default).launch {
            mutex.withLock {
                if (!shuttingDown) {
                    synchronized(layerModel) { commandManager.loadCommandsCatrobatImage(model) }
                }
                withContext(Dispatchers.Main) {
                    notifyCommandPostExecute()
                }
            }
        }
    }

    override fun undo() {
        manageUndoAndRedo(commandManager::undo, isUndoAvailable)
    }

    override fun redo() {
        manageUndoAndRedo(commandManager::redo, isRedoAvailable)
    }

    override fun reset() {
        synchronized(layerModel) { commandManager.reset() }
        notifyCommandPostExecute()
    }

    override fun shutdown() {
        shuttingDown = true
    }

    override fun undoIgnoringColorChanges() {
        manageUndoAndRedo(commandManager::undoIgnoringColorChanges, isUndoAvailable)
    }

    override fun undoIgnoringColorChangesAndAddCommand(command: Command) {
        CoroutineScope(Dispatchers.Default).launch {
            mutex.withLock {
                if (!shuttingDown) {
                    synchronized(layerModel) {
                        if (isUndoAvailable) {
                            commandManager.undoIgnoringColorChangesAndAddCommand(command)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    notifyCommandPostExecute()
                }
            }
        }
    }

    override fun undoInConnectedLinesMode() {
        manageUndoAndRedo(commandManager::undoInConnectedLinesMode, isUndoAvailable)
    }

    override fun redoInConnectedLinesMode() {
        manageUndoAndRedo(commandManager::redoInConnectedLinesMode, isRedoAvailable)
    }

    override fun getCommandManagerModelForCatrobatImage(): CommandManagerModel? {
        synchronized(layerModel) { return commandManager.getCommandManagerModelForCatrobatImage() }
    }

    override fun setInitialStateCommand(command: Command) {
        synchronized(layerModel) { commandManager.setInitialStateCommand(command) }
    }

    override fun adjustUndoListForClippingTool() {
        synchronized(layerModel) { commandManager.adjustUndoListForClippingTool() }
    }

    override fun undoInClippingTool() {
        synchronized(layerModel) { commandManager.undoInClippingTool() }
    }

    override fun popFirstCommandInUndo() {
        synchronized(layerModel) { commandManager.popFirstCommandInUndo() }
    }

    override fun popFirstCommandInRedo() {
        synchronized(layerModel) { commandManager.popFirstCommandInRedo() }
    }

    private fun manageUndoAndRedo(callFunction: () -> Unit, condition: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            mutex.withLock {
                if (!shuttingDown) {
                    synchronized(layerModel) {
                        if (condition) {
                            callFunction()
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    notifyCommandPostExecute()
                }
            }
        }
    }

    private fun notifyCommandPostExecute() {
        if (!shuttingDown) {
            for (listener in commandListeners) {
                listener.commandPostExecute()
            }
        }
    }

    override fun executeAllCommands() {
        CoroutineScope(Dispatchers.Default).launch {
            mutex.withLock {
                if (!shuttingDown) {
                    synchronized(layerModel) {
                        commandManager.executeAllCommands()
                    }
                }
                withContext(Dispatchers.Main) {
                    notifyCommandPostExecute()
                }
            }
        }
    }

    override fun getUndoCommandCount(): Int {
        synchronized(layerModel) { return commandManager.getUndoCommandCount() }
    }
}
