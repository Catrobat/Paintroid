package org.catrobat.paintroid.model

import org.catrobat.paintroid.command.Command

data class CommandManagerModel(val initialCommand: Command, val commands: MutableList<Command>)
