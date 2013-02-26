package org.catrobat.paintroid.tools;

import org.catrobat.paintroid.R;

public enum ToolType {
	ZOOM(R.string.button_zoom, R.drawable.icon_menu_zoom, false), PIPETTE(
			R.string.button_pipette, R.drawable.icon_menu_pipette, false), BRUSH(
			R.string.button_brush, R.drawable.icon_menu_brush, true), UNDO(
			R.string.button_undo, R.drawable.icon_menu_undo, false), REDO(
			R.string.button_redo, R.drawable.icon_menu_redo, false), NONE(0, 0,
			false), FILL(R.string.button_fill, R.drawable.icon_menu_fill, true), STAMP(
			R.string.button_stamp, R.drawable.icon_menu_stamp, false), CURSOR(
			R.string.button_cursor, R.drawable.icon_menu_cursor, true), IMPORTPNG(
			R.string.button_import_image, R.drawable.icon_menu_import_image,
			false), CROP(R.string.button_crop, R.drawable.icon_menu_crop, false), ERASER(
			R.string.button_eraser, R.drawable.icon_menu_eraser, false), FLIP(
			R.string.button_flip, R.drawable.icon_menu_flip_horizontal, false), RECT(
			R.string.button_rectangle, R.drawable.icon_menu_rectangle, true), MOVE(
			R.string.button_move, R.drawable.icon_menu_move, false);

	private int mNameResource;
	private int mImageResouce;
	private boolean mAllowColorChange;

	private ToolType(int nameResource, int imageResource,
			boolean allowColorchange) {
		mNameResource = nameResource;
		mImageResouce = imageResource;
		mAllowColorChange = allowColorchange;
	}

	public int getNameResource() {
		return mNameResource;
	}

	public int getImageResource() {
		return mImageResouce;
	}

	public boolean isColorChangeAllowed() {
		return mAllowColorChange;
	}

}