/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

public enum ToolType {
	ZOOM(R.string.button_zoom, R.drawable.icon_menu_zoom,
			R.string.help_content_zoom, false), PIPETTE(
			R.string.button_pipette, R.drawable.icon_menu_pipette,
			R.string.help_content_eyedropper, false), BRUSH(
			R.string.button_brush, R.drawable.icon_menu_brush,
			R.string.help_content_brush, true), UNDO(R.string.button_undo,
			R.drawable.icon_menu_undo, R.string.help_content_undo, false), REDO(
			R.string.button_redo, R.drawable.icon_menu_redo,
			R.string.help_content_redo, false), NONE(0, 0, 0, false), FILL(
			R.string.button_fill, R.drawable.icon_menu_bucket,
			R.string.help_content_fill, true), STAMP(R.string.button_stamp,
			R.drawable.icon_menu_stamp, R.string.help_content_stamp, false), CURSOR(
			R.string.button_cursor, R.drawable.icon_menu_cursor,
			R.string.help_content_cursor, true), IMPORTPNG(
			R.string.button_import_image, R.drawable.icon_menu_import_image,
			R.string.help_content_import_png, false), CROP(
			R.string.button_crop, R.drawable.icon_menu_crop,
			R.string.help_content_crop, false), ERASER(R.string.button_eraser,
			R.drawable.icon_menu_eraser, R.string.help_content_eraser, false), FLIP(
			R.string.button_flip, R.drawable.icon_menu_flip_horizontal,
			R.string.help_content_flip, false), RECT(R.string.button_rectangle,
			R.drawable.icon_menu_rectangle, R.string.help_content_rectangle,
			true), MOVE(R.string.button_move, R.drawable.icon_menu_move,
			R.string.help_content_move, false);

	private int mNameResource;
	private int mImageResouce;
	private int mHelpTextResource;
	private boolean mAllowColorChange;

	private ToolType(int nameResource, int imageResource, int helpTextResource,
			boolean allowColorchange) {
		mNameResource = nameResource;
		mImageResouce = imageResource;
		mHelpTextResource = helpTextResource;
		mAllowColorChange = allowColorchange;
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

}
