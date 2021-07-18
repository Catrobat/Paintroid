/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.tools

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.INVALID_RESOURCE_ID
import org.catrobat.paintroid.tools.Tool.StateChange
import java.util.EnumSet

enum class ToolType(
    @get:StringRes val nameResource: Int,
    @get:StringRes val helpTextResource: Int,
    @get:DrawableRes val drawableResource: Int,
    private val stateChangeBehaviour: EnumSet<StateChange>,
    @get:IdRes val toolButtonID: Int,
    @get:DrawableRes val overlayDrawableResource: Int,
    private val hasOptions: Boolean
) {
    PIPETTE(
        R.string.button_pipette,
        R.string.help_content_eyedropper,
        R.drawable.ic_pocketpaint_tool_pipette,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_pipette,
        INVALID_RESOURCE_ID,
        false
    ),
    BRUSH(
        R.string.button_brush,
        R.string.help_content_brush,
        R.drawable.ic_pocketpaint_tool_brush,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_brush,
        INVALID_RESOURCE_ID,
        true
    ),
    UNDO(
        R.string.button_undo,
        R.string.help_content_undo,
        R.drawable.ic_pocketpaint_undo,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_btn_top_undo,
        INVALID_RESOURCE_ID,
        false
    ),
    REDO(
        R.string.button_redo,
        R.string.help_content_redo,
        R.drawable.ic_pocketpaint_redo,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_btn_top_redo,
        INVALID_RESOURCE_ID,
        false
    ),
    FILL(
        R.string.button_fill,
        R.string.help_content_fill,
        R.drawable.ic_pocketpaint_tool_fill,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_fill,
        INVALID_RESOURCE_ID,
        true
    ),
    STAMP(
        R.string.button_stamp,
        R.string.help_content_stamp,
        R.drawable.ic_pocketpaint_tool_stamp,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_stamp,
        INVALID_RESOURCE_ID,
        true
    ),
    LINE(
        R.string.button_line,
        R.string.help_content_line,
        R.drawable.ic_pocketpaint_tool_line,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_line,
        INVALID_RESOURCE_ID,
        true
    ),
    CURSOR(
        R.string.button_cursor,
        R.string.help_content_cursor,
        R.drawable.ic_pocketpaint_tool_cursor,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_cursor,
        INVALID_RESOURCE_ID,
        true
    ),
    IMPORTPNG(
        R.string.button_import_image,
        R.string.help_content_import_png,
        R.drawable.ic_pocketpaint_tool_import,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_import,
        INVALID_RESOURCE_ID,
        false
    ),
    TRANSFORM(
        R.string.button_transform,
        R.string.help_content_transform,
        R.drawable.ic_pocketpaint_tool_transform,
        EnumSet.of(StateChange.RESET_INTERNAL_STATE, StateChange.NEW_IMAGE_LOADED),
        R.id.pocketpaint_tools_transform,
        INVALID_RESOURCE_ID,
        true
    ),
    ERASER(
        R.string.button_eraser,
        R.string.help_content_eraser,
        R.drawable.ic_pocketpaint_tool_eraser,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_eraser,
        INVALID_RESOURCE_ID,
        true
    ),
    SHAPE(
        R.string.button_shape,
        R.string.help_content_shape,
        R.drawable.ic_pocketpaint_tool_rectangle,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_rectangle,
        INVALID_RESOURCE_ID,
        true
    ),
    TEXT(
        R.string.button_text,
        R.string.help_content_text,
        R.drawable.ic_pocketpaint_tool_text,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_text,
        INVALID_RESOURCE_ID,
        true
    ),
    LAYER(
        R.string.layers_title,
        R.string.help_content_layer,
        R.drawable.ic_pocketpaint_layers,
        EnumSet.of(StateChange.ALL),
        INVALID_RESOURCE_ID,
        INVALID_RESOURCE_ID,
        false
    ),
    COLORCHOOSER(
        R.string.color_picker_title,
        R.string.help_content_color_chooser,
        R.drawable.ic_pocketpaint_color_palette,
        EnumSet.of(StateChange.ALL),
        INVALID_RESOURCE_ID,
        INVALID_RESOURCE_ID,
        false
    ),
    HAND(
        R.string.button_hand,
        R.string.help_content_hand,
        R.drawable.ic_pocketpaint_tool_hand,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_hand,
        INVALID_RESOURCE_ID,
        false
    ),
    SPRAY(
        R.string.button_spray_can,
        R.string.help_content_spray_can,
        R.drawable.ic_pocketpaint_tool_spray_can,
        EnumSet.of(StateChange.ALL),
        R.id.pocketpaint_tools_spray_can,
        INVALID_RESOURCE_ID,
        true
    );

    fun shouldReactToStateChange(stateChange: StateChange): Boolean =
        stateChangeBehaviour.contains(StateChange.ALL) || stateChangeBehaviour.contains(
            stateChange
        )

    fun hasOptions(): Boolean = hasOptions
}
