/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Tool.StateChange;

import java.util.EnumSet;

public enum ToolType {
	PIPETTE(R.string.button_pipette, R.string.help_content_eyedropper, false, EnumSet.of(StateChange.ALL), R.id.tools_pipette),
	BRUSH(R.string.button_brush, R.string.help_content_brush, true, EnumSet.of(StateChange.ALL), R.id.tools_brush),
	UNDO(R.string.button_undo, R.string.help_content_undo, false, EnumSet.of(StateChange.ALL), R.id.btn_top_undo),
	REDO(R.string.button_redo, R.string.help_content_redo, false, EnumSet.of(StateChange.ALL), R.id.btn_top_redo),
	FILL(R.string.button_fill, R.string.help_content_fill, true, EnumSet.of(StateChange.ALL), R.id.tools_fill),
	STAMP(R.string.button_stamp, R.string.help_content_stamp, false, EnumSet.of(StateChange.ALL), R.id.tools_stamp),
	LINE(R.string.button_line, R.string.help_content_line, true, EnumSet.of(StateChange.ALL), R.id.tools_line),
	CURSOR(R.string.button_cursor, R.string.help_content_cursor, true, EnumSet.of(StateChange.ALL), R.id.tools_cursor),
	IMPORTPNG(R.string.button_import_image, R.string.help_content_import_png, false, EnumSet.of(StateChange.ALL), R.id.tools_import),
	TRANSFORM(R.string.button_transform, R.string.help_content_transform, false, EnumSet.of(StateChange.RESET_INTERNAL_STATE, StateChange.NEW_IMAGE_LOADED), R.id.tools_transform),
	ERASER(R.string.button_eraser, R.string.help_content_eraser, false, EnumSet.of(StateChange.ALL), R.id.tools_eraser),
	SHAPE(R.string.button_shape, R.string.help_content_shape, true, EnumSet.of(StateChange.ALL), R.id.tools_rectangle),
	TEXT(R.string.button_text, R.string.help_content_text, true, EnumSet.of(StateChange.ALL), R.id.tools_text),
	LAYER(R.string.layers_title, R.string.layers_title, false, EnumSet.of(StateChange.ALL), R.id.btn_top_layers),
	COLORCHOOSER(R.string.color_chooser_title, R.string.color_chooser_title, true, EnumSet.of(StateChange.ALL), R.id.btn_top_color_frame);

	private int mNameResource;
	private int mHelpTextResource;
	private boolean mAllowColorChange;
	private EnumSet<StateChange> mStateChangeBehaviour;
	private int mToolButtonID;

	ToolType(int nameResource, int helpTextResource, boolean allowColorchange,
			 EnumSet<StateChange> stateChangeBehaviour, int toolButtonID) {
		mNameResource = nameResource;
		mHelpTextResource = helpTextResource;
		mAllowColorChange = allowColorchange;
		mStateChangeBehaviour = stateChangeBehaviour;
		mToolButtonID = toolButtonID;
	}

	public int getNameResource() {
		return mNameResource;
	}

	public int getHelpTextResource() {
		return mHelpTextResource;
	}

	public boolean isColorChangeAllowed() {
		return mAllowColorChange;
	}

	public boolean shouldReactToStateChange(StateChange stateChange) {
		return mStateChangeBehaviour.contains(StateChange.ALL) ||
				mStateChangeBehaviour.contains(stateChange);
	}

	public int getToolButtonID() {
		return mToolButtonID;
	}

}
