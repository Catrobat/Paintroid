/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.common

import android.os.Environment
import java.io.File

const val PAINTROID_PICTURE_PATH = "org.catrobat.extra.PAINTROID_PICTURE_PATH"
const val PAINTROID_PICTURE_NAME = "org.catrobat.extra.PAINTROID_PICTURE_NAME"
const val TEMP_PICTURE_NAME = "catroidTemp"
const val MEDIA_GALLEY_URL = "https://share.catrob.at/pocketcode/media-library/looks"
const val ABOUT_DIALOG_FRAGMENT_TAG = "aboutdialogfragment"
const val LIKE_US_DIALOG_FRAGMENT_TAG = "likeusdialogfragment"
const val RATE_US_DIALOG_FRAGMENT_TAG = "rateusdialogfragment"
const val FEEDBACK_DIALOG_FRAGMENT_TAG = "feedbackdialogfragment"
const val ZOOM_WINDOW_SETTINGS_DIALOG_FRAGMENT_TAG = "zoomwindowsettingsdialogfragment"
const val ADVANCED_SETTINGS_DIALOG_FRAGMENT_TAG = "advancedsettingsdialogfragment"
const val SAVE_DIALOG_FRAGMENT_TAG = "savedialogerror"
const val LOAD_DIALOG_FRAGMENT_TAG = "loadbitmapdialogerror"
const val COLOR_PICKER_DIALOG_TAG = "ColorPickerDialogTag"
const val SAVE_QUESTION_FRAGMENT_TAG = "savebeforequitfragment"
const val SAVE_INFORMATION_DIALOG_TAG = "saveinformationdialogfragment"
const val OVERWRITE_INFORMATION_DIALOG_TAG = "saveinformationdialogfragment"
const val PNG_INFORMATION_DIALOG_TAG = "pnginformationdialogfragment"
const val JPG_INFORMATION_DIALOG_TAG = "jpginformationdialogfragment"
const val ORA_INFORMATION_DIALOG_TAG = "orainformationdialogfragment"
const val CATROBAT_INFORMATION_DIALOG_TAG = "catrobatinformationdialogfragment"
const val CATROID_MEDIA_GALLERY_FRAGMENT_TAG = "catroidmediagalleryfragment"
const val PERMISSION_DIALOG_FRAGMENT_TAG = "permissiondialogfragment"
const val SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG = "showlikeusdialog"
const val ZOOM_WINDOW_ENABLED_SHARED_PREFERENCES_TAG = "zoomwindowenabled"
const val ZOOM_WINDOW_ZOOM_PERCENTAGE_SHARED_PREFERENCES_TAG = "zoomwindowzoompercentage"
const val IMAGE_NUMBER_SHARED_PREFERENCES_TAG = "imagenumbertag"
const val SCALE_IMAGE_FRAGMENT_TAG = "showscaleimagedialog"
const val INDETERMINATE_PROGRESS_DIALOG_TAG = "indeterminateprogressdialogfragment"
const val INVALID_RESOURCE_ID = 0
const val MAX_LAYERS = 100
const val MEGABYTE_IN_BYTE = 1_048_576L
const val MINIMUM_HEAP_SPACE_FOR_NEW_LAYER = 40
const val CATROBAT_IMAGE_ENDING = "catrobat-image"
const val TEMP_IMAGE_DIRECTORY_NAME = "TemporaryImages"
const val TEMP_IMAGE_NAME = "temporaryImage"
const val ITALIC_FONT_BOX_ADJUSTMENT = 1.2f
const val TEMP_IMAGE_PATH = "$TEMP_IMAGE_DIRECTORY_NAME/$TEMP_IMAGE_NAME.$CATROBAT_IMAGE_ENDING"
const val TEMP_IMAGE_TEMP_PATH = "$TEMP_IMAGE_DIRECTORY_NAME/${TEMP_IMAGE_NAME}1.$CATROBAT_IMAGE_ENDING"
const val SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME = "Ownfiletypepreferences"
const val ANIMATION_DURATION: Long = 250

object Constants {
    @JvmField
    val PICTURES_DIRECTORY = File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES)
    @JvmField
    val DOWNLOADS_DIRECTORY = File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS)
}
