package org.catrobat.paintroid.tools;

import org.catrobat.paintroid.R;

public enum ToolType {
	ZOOM(R.string.button_zoom, R.drawable.icon_menu_zoom), PIPETTE(
			R.string.button_pipette, R.drawable.icon_menu_pipette), BRUSH(
			R.string.button_brush, R.drawable.icon_menu_brush), UNDO(
			R.string.button_undo, R.drawable.icon_menu_undo), REDO(
			R.string.button_redo, R.drawable.icon_menu_redo), NONE(0, 0), FILL(
			R.string.button_fill, R.drawable.icon_menu_fill), STAMP(
			R.string.button_stamp, R.drawable.icon_menu_stamp), CURSOR(
			R.string.button_cursor, R.drawable.icon_menu_cursor), IMPORTPNG(
			R.string.button_import_image, R.drawable.icon_menu_import_image), CROP(
			R.string.button_crop, R.drawable.icon_menu_crop), ERASER(
			R.string.button_eraser, R.drawable.icon_menu_eraser), FLIP(
			R.string.button_flip, R.drawable.icon_menu_flip_horizontal), RECT(
			R.string.button_rectangle, R.drawable.icon_menu_rectangle), MOVE(
			R.string.button_move, R.drawable.icon_menu_move);

	private int mNameResource;
	private int mImageResouce;

	private ToolType(int nameResource, int imageResource) {
		mNameResource = nameResource;
		mImageResouce = imageResource;
	}

	public int getNameResource() {
		return mNameResource;
	}

	public int getImageResource() {
		return mImageResouce;
	}

}