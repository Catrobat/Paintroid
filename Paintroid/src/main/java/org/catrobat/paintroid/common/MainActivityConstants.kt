/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

import androidx.annotation.IntDef
import java.lang.AssertionError

const val SAVE_IMAGE_DEFAULT = 1
const val SAVE_IMAGE_NEW_EMPTY = 2
const val SAVE_IMAGE_LOAD_NEW = 3
const val SAVE_IMAGE_FINISH = 4

const val LOAD_IMAGE_DEFAULT = 1
const val LOAD_IMAGE_IMPORT_PNG = 2
const val LOAD_IMAGE_CATROID = 3

const val CREATE_FILE_DEFAULT = 1

const val REQUEST_CODE_IMPORT_PNG = 1
const val REQUEST_CODE_LOAD_PICTURE = 2
const val REQUEST_CODE_INTRO = 3

const val PERMISSION_EXTERNAL_STORAGE_SAVE = 1
const val PERMISSION_EXTERNAL_STORAGE_SAVE_COPY = 2
const val PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW = 3
const val PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY = 4
const val PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH = 5
const val PERMISSION_REQUEST_CODE_REPLACE_PICTURE = 6
const val PERMISSION_REQUEST_CODE_IMPORT_PICTURE = 7

const val RESULT_INTRO_MW_NOT_SUPPORTED = 10

class MainActivityConstants private constructor() {
    @IntDef(
        SAVE_IMAGE_DEFAULT,
        SAVE_IMAGE_NEW_EMPTY,
        SAVE_IMAGE_LOAD_NEW,
        SAVE_IMAGE_FINISH
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class SaveImageRequestCode

    @IntDef(
        LOAD_IMAGE_DEFAULT,
        LOAD_IMAGE_IMPORT_PNG,
        LOAD_IMAGE_CATROID
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class LoadImageRequestCode

    @IntDef(CREATE_FILE_DEFAULT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class CreateFileRequestCode

    @IntDef(
        REQUEST_CODE_IMPORT_PNG,
        REQUEST_CODE_LOAD_PICTURE,
        REQUEST_CODE_INTRO
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class ActivityRequestCode

    @IntDef(
        PERMISSION_EXTERNAL_STORAGE_SAVE,
        PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
        PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
        PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
        PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
        PERMISSION_REQUEST_CODE_REPLACE_PICTURE,
        PERMISSION_REQUEST_CODE_IMPORT_PICTURE
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class PermissionRequestCode

    init {
        throw AssertionError()
    }
}
