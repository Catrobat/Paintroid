/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.common;

import android.os.Environment;

import java.io.File;

public final class Constants {
	public static final String PAINTROID_PICTURE_PATH = "org.catrobat.extra.PAINTROID_PICTURE_PATH";
	public static final String PAINTROID_PICTURE_NAME = "org.catrobat.extra.PAINTROID_PICTURE_NAME";

	public static final String EXT_STORAGE_DIRECTORY_NAME = "Pocket Paint";
	public static final String TEMP_PICTURE_NAME = "catroidTemp";
	public static final float ACTION_BAR_HEIGHT = 50.0f;
	public static final File MEDIA_DIRECTORY = new File(Environment.getExternalStorageDirectory(), EXT_STORAGE_DIRECTORY_NAME);

	public static final String TOS_DIALOG_FRAGMENT_TAG = "termsofuseandservicedialogfragment";
	public static final String ABOUT_DIALOG_FRAGMENT_TAG = "aboutdialogfragment";
	public static final String SAVE_DIALOG_FRAGMENT_TAG = "savedialogerror";
	public static final String HELP_DIALOG_FRAGMENT_TAG = "helpdialogfragmenttag";
	public static final String LOAD_DIALOG_FRAGMENT_TAG = "loadbitmapdialogerror";
	public static final String COLOR_PICKER_DIALOG_TAG = "ColorPickerDialogTag";
	public static final String INDETERMINATE_FRAGMENT_TAG = "indeterminatefragment";
	public static final String SAVE_QUESTION_FRAGMENT_TAG = "savebeforequitfragment";
	public static final String CHOOSE_IMAGE_FRAGMENT_TAG = "chooseimagefragment";

	private Constants() {
		throw new AssertionError();
	}
}
