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

	public static final String TEMP_PICTURE_NAME = "catroidTemp";
	public static final File MEDIA_DIRECTORY = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
	public static final String MEDIA_GALLEY_URL = "https://share.catrob.at/pocketcode/media-library/looks";

	public static final String ABOUT_DIALOG_FRAGMENT_TAG = "aboutdialogfragment";
	public static final String LIKE_US_DIALOG_FRAGMENT_TAG = "likeusdialogfragment";
	public static final String RATE_US_DIALOG_FRAGMENT_TAG = "rateusdialogfragment";
	public static final String FEEDBACK_DIALOG_FRAGMENT_TAG = "feedbackdialogfragment";
	public static final String SAVE_DIALOG_FRAGMENT_TAG = "savedialogerror";
	public static final String LOAD_DIALOG_FRAGMENT_TAG = "loadbitmapdialogerror";
	public static final String COLOR_PICKER_DIALOG_TAG = "ColorPickerDialogTag";
	public static final String SAVE_QUESTION_FRAGMENT_TAG = "savebeforequitfragment";
	public static final String SAVE_INFORMATION_DIALOG_TAG = "saveinformationdialogfragment";
	public static final String OVERWRITE_INFORMATION_DIALOG_TAG = "saveinformationdialogfragment";
	public static final String PNG_INFORMATION_DIALOG_TAG = "pnginformationdialogfragment";
	public static final String JPG_INFORMATION_DIALOG_TAG = "jpginformationdialogfragment";
	public static final String CATROID_MEDIA_GALLERY_FRAGMENT_TAG = "catroidmediagalleryfragment";

	public static final String PERMISSION_DIALOG_FRAGMENT_TAG = "permissiondialogfragment";

	public static final int INVALID_RESOURCE_ID = 0;

	public static final String SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG = "showlikeusdialog";
	public static final String IMAGE_NUMBER_SHARED_PREFERENCES_TAG = "imagenumbertag";

	public static final int IS_JPG = 0;
	public static final int IS_PNG = 1;
	public static final int IS_NO_FILE = -1;

	private Constants() {
		throw new AssertionError();
	}
}
