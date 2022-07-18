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

import android.graphics.Color
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.CommandManager.CommandListener
import org.catrobat.paintroid.common.CommonFactory
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.CommandManagerModel
import java.util.ArrayDeque
import java.util.Collections
import java.util.Deque
import kotlin.collections.ArrayList

const val FIVE = 5

class DefaultCommandManager(
    private val commonFactory: CommonFactory,
    private val layerModel: LayerContracts.Model
) : CommandManager {
    private val commandListeners: MutableList<CommandListener> = ArrayList()
    private val redoCommandList: Deque<Command> = ArrayDeque()
    private val undoCommandList: Deque<Command> = ArrayDeque()
    private var initialStateCommand: Command? = null

    override val isBusy: Boolean
        get() = false

    override val isUndoAvailable: Boolean
        get() = !undoCommandList.isEmpty()

    override val isRedoAvailable: Boolean
        get() = !redoCommandList.isEmpty()

    override val commandManagerModel: CommandManagerModel?
        get() {
            val commandList = ArrayList<Command>()
            val it = redoCommandList.descendingIterator()
            while (it.hasNext()) {
                commandList.add(it.next())
            }
            commandList.addAll(undoCommandList)
            var model: CommandManagerModel? = null
            initialStateCommand?.let { initialCommand ->
                model = CommandManagerModel(initialCommand, commandList)
            }
            return model
        }

    override fun addCommandListener(commandListener: CommandListener) {
        commandListeners.add(commandListener)
    }

    override fun removeCommandListener(commandListener: CommandListener) {
        commandListeners.remove(commandListener)
    }

    override fun addCommand(command: Command?) {
        redoCommandList.clear()
        command?.let { undoCommandList.addFirst(it) }
        executeCommand(command)
        notifyCommandExecuted()
    }

    override fun addCommandWithoutUndo(command: Command?) {
        redoCommandList.clear()
        executeCommand(command)
        notifyCommandExecuted()
    }

    private fun executeCommand(command: Command?) {
        val currentLayer = layerModel.currentLayer
        val canvas = commonFactory.createCanvas()
        canvas.setBitmap(currentLayer?.bitmap)
        command?.run(canvas, layerModel)
    }

    override fun loadCommandsCatrobatImage(model: CommandManagerModel?) {
        model ?: return
        setInitialStateCommand(model.initialCommand)
        reset()
        for (command in model.commands) {
            addCommand(command)
        }
    }

    override fun undo() {
        val command = undoCommandList.pop()
        redoCommandList.addFirst(command)

        handleUndo(command)

        notifyCommandExecuted()
    }

    private fun handleUndo(command: Command) {
        var success = true
        var layerCount = layerModel.layerCount
        val currentCommandName = command.javaClass.simpleName
        val addEmptyLayerCommandRegex = AddEmptyLayerCommand::class.java.simpleName.toRegex()
        val mergeLayerCommandRegex = MergeLayersCommand::class.java.simpleName.toRegex()

        var backupLayer: LayerContracts.Layer? = null
        if (currentCommandName.matches(addEmptyLayerCommandRegex)) {
            layerCount--
            backupLayer = layerModel.getLayerAt(0)
            success = layerModel.removeLayerAt(0)
        }

        val checkBoxes: MutableList<Boolean> = ArrayList(Collections.nCopies(layerCount, true))

        if (!currentCommandName.matches(mergeLayerCommandRegex)) {
            success = if (!backUpCheckBoxes(layerCount, checkBoxes)) false else success
        }

        if (!success) {
            backupLayer?.apply {
                if (layerModel.getLayerIndexOf(this) == -1) {
                    layerModel.addLayerAt(0, this)
                }
            }
            val commandToRestore = redoCommandList.pop()
            undoCommandList.addFirst(commandToRestore)

            return
        }

        layerModel.reset()

        val canvas = commonFactory.createCanvas()

        initialStateCommand?.run(canvas, layerModel)

        val iterator = undoCommandList.descendingIterator()
        while (iterator.hasNext()) {
            val currentLayer = layerModel.currentLayer
            canvas.setBitmap(currentLayer?.bitmap)
            iterator.next().run(canvas, layerModel)
        }

        if (!currentCommandName.matches(mergeLayerCommandRegex)) {
            fetchBackCheckBoxes(layerCount, checkBoxes)
        }
    }

    private fun executeAllCommands() {
        val layerCount = layerModel.layerCount
        val checkBoxes: MutableList<Boolean> = ArrayList(Collections.nCopies(layerCount, true))

        backUpCheckBoxes(layerCount, checkBoxes)

        layerModel.reset()

        val canvas = commonFactory.createCanvas()

        initialStateCommand?.run(canvas, layerModel)

        val iterator = undoCommandList.descendingIterator()
        while (iterator.hasNext()) {
            val currentLayer = layerModel.currentLayer
            canvas.setBitmap(currentLayer?.bitmap)
            iterator.next().run(canvas, layerModel)
        }
        fetchBackCheckBoxes(layerCount, checkBoxes)
    }

    override fun redo() {
        val command = redoCommandList.pop()
        undoCommandList.addFirst(command)

        val currentLayer = layerModel.currentLayer
        val canvas = commonFactory.createCanvas()
        if (currentLayer != null) {
            if (currentLayer.isVisible) {
                canvas.setBitmap(currentLayer.bitmap)
            } else {
                canvas.setBitmap(currentLayer.transparentBitmap)
            }
        }

        command.run(canvas, layerModel)
        notifyCommandExecuted()
    }

    override fun reset() {
        undoCommandList.clear()
        redoCommandList.clear()
        layerModel.reset()
        if (initialStateCommand != null) {
            val canvas = commonFactory.createCanvas()
            initialStateCommand?.run(canvas, layerModel)
        }

        notifyCommandExecuted()
    }

    override fun shutdown() = Unit

    override fun undoIgnoringColorChanges() {
        addAndExecuteCommands(separateColorCommandsAndUndo())
        notifyCommandExecuted()
    }

    override fun undoIgnoringColorChangesAndAddCommand(command: Command) {
        val colorCommandList = separateColorCommandsAndUndo()
        undoCommandList.addFirst(command)
        redoCommandList.clear()
        executeCommand(command)
        addAndExecuteCommands(colorCommandList)
        notifyCommandExecuted()
    }

    private fun separateColorCommandsAndUndo(): Deque<Command> {
        val colorCommandList = removeColorCommands()
        if (undoCommandList.isNotEmpty() && undoCommandList.first != null) {
            val command = undoCommandList.pop()
            redoCommandList.addFirst(command)
            handleUndo(command)
        }
        return colorCommandList
    }

    override fun undoInConnectedLinesMode() {
        val colorCommandList = removeColorCommands()
        if (undoCommandList.isNotEmpty()) {
            val commandForUndo: Command
            if (colorCommandList.isNotEmpty()) {
                commandForUndo = colorCommandList.pop()
            } else {
                commandForUndo = undoCommandList.pop()
            }
            redoCommandList.addFirst(commandForUndo)
            handleUndo(commandForUndo)
            recolorLastLine(colorCommandList)
        } else {
            if (colorCommandList.isNotEmpty()) {
                addCommands(colorCommandList)
            }
            if (undoCommandList.isNotEmpty()) {
                undo()
            }
        }
    }

    private fun recolorLastLine(colorCommandList: Deque<Command>) {
        if (undoCommandList.isNotEmpty() && undoCommandList.first !is ColorChangedCommand) {
            val firstNonColorCommand = undoCommandList.pop()
            val colorCommand: Command
            var color = Color.BLACK
            if (colorCommandList.isNotEmpty()) {
                colorCommand = colorCommandList.first
                if (colorCommand is ColorChangedCommand) {
                    color = colorCommand.color
                }
            } else if (undoCommandList.isNotEmpty()) {
                val iterator = undoCommandList.iterator()
                while (iterator.hasNext()) {
                    val possibleInitialColor = iterator.next()
                    if (possibleInitialColor is ColorChangedCommand) {
                        color = possibleInitialColor.color
                        break
                    }
                }
            }
            if (firstNonColorCommand is PathCommand) {
                handleUndo(firstNonColorCommand)
                firstNonColorCommand.paint.color = color
                undoCommandList.addFirst(firstNonColorCommand)
                executeCommand(firstNonColorCommand)
            } else if (firstNonColorCommand is PointCommand) {
                handleUndo(firstNonColorCommand)
                firstNonColorCommand.paint.color = color
                undoCommandList.addFirst(firstNonColorCommand)
                executeCommand(firstNonColorCommand)
            }
        }
        addAndExecuteCommands(colorCommandList)
        notifyCommandExecuted()
    }

    override fun redoInConnectedLinesMode() {
        val command = redoCommandList.pop()
        if (command is ColorChangedCommand) {
            val colorCommandList = removeColorCommands()
            if (undoCommandList.isNotEmpty()) {
                val firstNonColorCommand = undoCommandList.first
                val color = command.color

                if (firstNonColorCommand is PathCommand) {
                    firstNonColorCommand.paint.color = color
                } else if (firstNonColorCommand is PointCommand) {
                    firstNonColorCommand.paint.color = color
                }
                executeAllCommands()
            }
            addAndExecuteCommands(colorCommandList)
        }
        undoCommandList.addFirst(command)

        executeCommand(command)
        notifyCommandExecuted()
    }

    override fun getCommandManagerModelForCatrobatImage(): CommandManagerModel? {
        var adaptedModel: CommandManagerModel? = null
        commandManagerModel?.let { it1 ->
            adaptedModel = CommandManagerModel(
                it1.initialCommand,
                it1.commands.filter {
                    it !is ColorChangedCommand
                }.toMutableList()
            )
        }
        return adaptedModel
    }

    override fun adjustUndoListForClippingTool() {
        val commandName = undoCommandList.first.toString().split(".", "@")[FIVE]
        if (commandName == ClippingCommand::class.java.simpleName) {
            val clippingCommand = undoCommandList.pop()
            undoCommandList.pop()
            undoCommandList.addFirst(clippingCommand)
        }
    }

    override fun undoInClippingTool() {
        val command = undoCommandList.pop()
        handleUndo(command)
        notifyCommandExecuted()
    }

    override fun popFirstCommandInUndo() {
        undoCommandList.pop()
    }

    override fun popFirstCommandInRedo() {
        redoCommandList.pop()
    }

    override fun setInitialStateCommand(command: Command) {
        initialStateCommand = command
    }

    private fun notifyCommandExecuted() {
        for (listener in commandListeners) {
            listener.commandPostExecute()
        }
    }

    private fun backUpCheckBoxes(layerCount: Int, checkBoxes: MutableList<Boolean>): Boolean {
        var success = true
        if (layerCount > 1) {
            for (index in layerCount - 1 downTo 0) {
                layerModel.getLayerAt(index)?.let {
                    checkBoxes[index] = it.isVisible
                } ?: run { success = false }
            }
        } else {
            layerModel.getLayerAt(0)?.let {
                checkBoxes[0] = it.isVisible
            } ?: run { success = false }
        }

        return success
    }

    private fun fetchBackCheckBoxes(layerCount: Int, checkBoxes: List<Boolean>) {
        if (layerCount > 1) {
            for (index in layerCount - 1 downTo 0) {
                val destinationLayer = layerModel.getLayerAt(index)
                if (!checkBoxes[index]) {
                    destinationLayer?.let {
                        it.switchBitmaps(false)
                        it.isVisible = false
                    }
                }
            }
        } else {
            val destinationLayer = layerModel.currentLayer
            if (destinationLayer != null && !checkBoxes[0]) {
                destinationLayer.switchBitmaps(false)
                destinationLayer.isVisible = false
            }
        }
    }

    private fun removeColorCommands(): Deque<Command> {
        val colorCommandList: Deque<Command> = ArrayDeque()
        while (undoCommandList.isNotEmpty() && undoCommandList.first is ColorChangedCommand) {
            colorCommandList.addLast(undoCommandList.pop())
        }
        return colorCommandList
    }

    private fun addAndExecuteCommands(commands: Deque<Command>) {
        val iterator = commands.descendingIterator()
        while (iterator.hasNext()) {
            val command = iterator.next()
            undoCommandList.addFirst(command)
            executeCommand(command)
        }
    }

    private fun addCommands(commands: Deque<Command>) {
        val iterator = commands.descendingIterator()
        while (iterator.hasNext()) {
            val command = iterator.next()
            undoCommandList.addFirst(command)
        }
    }
}
