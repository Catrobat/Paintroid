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

import java.util.EnumSet;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Tool.StateChange;

public enum ToolType {
	ELLIPSE(R.string.button_ellipse, R.drawable.icon_menu_ellipse,
			R.string.help_content_ellipse, true, EnumSet.of(StateChange.ALL)), ZOOM(
			R.string.button_zoom, R.drawable.icon_menu_zoom,
			R.string.help_content_zoom, false, EnumSet.of(StateChange.ALL)), PIPETTE(
			R.string.button_pipette, R.drawable.icon_menu_pipette,
			R.string.help_content_eyedropper, false, EnumSet
					.of(StateChange.ALL)), BRUSH(R.string.button_brush,
			R.drawable.icon_menu_brush, R.string.help_content_brush, true,
			EnumSet.of(StateChange.ALL)), UNDO(R.string.button_undo,
			R.drawable.icon_menu_undo, R.string.help_content_undo, false,
			EnumSet.of(StateChange.ALL)), REDO(R.string.button_redo,
			R.drawable.icon_menu_redo, R.string.help_content_redo, false,
			EnumSet.of(StateChange.ALL)), NONE(0, 0, 0, false, EnumSet
			.of(StateChange.ALL)), FILL(R.string.button_fill,
			R.drawable.icon_menu_bucket, R.string.help_content_fill, true,
			EnumSet.of(StateChange.ALL)), STAMP(R.string.button_stamp,
			R.drawable.icon_menu_stamp, R.string.help_content_stamp, false,
			EnumSet.of(StateChange.ALL)), LINE(R.string.button_line,
			R.drawable.icon_menu_straight_line, R.string.help_content_line,
			true, EnumSet.of(StateChange.ALL)), CURSOR(R.string.button_cursor,
			R.drawable.icon_menu_cursor, R.string.help_content_cursor, true,
			EnumSet.of(StateChange.ALL)), IMPORTPNG(
			R.string.button_import_image, R.drawable.icon_menu_import_image,
			R.string.help_content_import_png, false, EnumSet
					.of(StateChange.ALL)), CROP(R.string.button_crop,
			R.drawable.icon_menu_crop, R.string.help_content_crop, false,
			EnumSet.of(StateChange.RESET_INTERNAL_STATE,
					StateChange.NEW_IMAGE_LOADED)), ERASER(
			R.string.button_eraser, R.drawable.icon_menu_eraser,
			R.string.help_content_eraser, false, EnumSet.of(StateChange.ALL)), FLIP(
			R.string.button_flip, R.drawable.icon_menu_flip_horizontal,
			R.string.help_content_flip, false, EnumSet.of(StateChange.ALL)), RECT(
			R.string.button_rectangle, R.drawable.icon_menu_rectangle,
			R.string.help_content_rectangle, true, EnumSet.of(StateChange.ALL)), MOVE(
			R.string.button_move, R.drawable.icon_menu_move,
			R.string.help_content_move, false, EnumSet.of(StateChange.ALL)), ROTATE(
			R.string.button_rotate, R.drawable.icon_menu_rotate_left,
			R.string.help_content_rotate, false, EnumSet.of(StateChange.ALL));

	private int mNameResource;
	private int mImageResouce;
	private int mHelpTextResource;
	private boolean mAllowColorChange;
	private EnumSet<StateChange> mStateChangeBehaviour;

	private ToolType(int nameResource, int imageResource, int helpTextResource,
			boolean allowColorchange, EnumSet<StateChange> stateChangeBehaviour) {
		mNameResource = nameResource;
		mImageResouce = imageResource;
		mHelpTextResource = helpTextResource;
		mAllowColorChange = allowColorchange;
		mStateChangeBehaviour = stateChangeBehaviour;
	}

	public int getNameResource() {
		return mNameResource;
	}

	public int getImageResource() {
		return mImageResouce;
	}

	public int getHelpTextResource() {
		return mHelpTextResource;
	}

	public boolean isColorChangeAllowed() {
		return mAllowColorChange;
	}

	public boolean shouldReactToStateChange(StateChange stateChange) {
		if (mStateChangeBehaviour.contains(StateChange.ALL)) {
			return (true);
		} else if (mStateChangeBehaviour.contains(stateChange)) {
			return (true);
		} else {
			return (false);
		}
	}
}
