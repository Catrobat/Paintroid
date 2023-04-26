package org.catrobat.paintroid.iotasks

import org.catrobat.paintroid.colorpicker.ColorHistory
import org.catrobat.paintroid.model.CommandManagerModel

data class WorkspaceReturnValue(
    val commandManagerModel: CommandManagerModel?,
    val colorHistory: ColorHistory?
)
