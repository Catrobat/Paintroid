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

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class MainActivityConstants {
	public static final int SAVE_IMAGE_DEFAULT = 1;
	public static final int SAVE_IMAGE_NEW_EMPTY = 2;
	public static final int SAVE_IMAGE_LOAD_NEW = 3;
	public static final int SAVE_IMAGE_FINISH = 4;
	public static final int SAVE_IMAGE_BACK_TO_PC = 5;

	public static final int LOAD_IMAGE_DEFAULT = 1;
	public static final int LOAD_IMAGE_IMPORTPNG = 2;
	public static final int LOAD_IMAGE_CATROID = 3;

	public static final int CREATE_FILE_DEFAULT = 1;
	public static final int CREATE_FILE_TAKE_PHOTO = 2;

	public static final int REQUEST_CODE_IMPORTPNG = 1;
	public static final int REQUEST_CODE_LOAD_PICTURE = 2;
	public static final int REQUEST_CODE_FINISH = 3;
	public static final int REQUEST_CODE_TAKE_PICTURE = 4;
	public static final int REQUEST_CODE_LANGUAGE = 5;

	public static final int PERMISSION_EXTERNAL_STORAGE_SAVE = 2;
	public static final int PERMISSION_EXTERNAL_STORAGE_SAVE_COPY = 3;
	public static final int PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW = 4;
	public static final int PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY = 5;
	public static final int PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH = 6;
	public static final int PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC = 7;

	@IntDef({SAVE_IMAGE_DEFAULT, SAVE_IMAGE_NEW_EMPTY, SAVE_IMAGE_LOAD_NEW, SAVE_IMAGE_FINISH, SAVE_IMAGE_BACK_TO_PC})
	@Retention(RetentionPolicy.SOURCE)
	public @interface SaveImageRequestCode {
	}

	@IntDef({LOAD_IMAGE_DEFAULT, LOAD_IMAGE_IMPORTPNG, LOAD_IMAGE_CATROID})
	@Retention(RetentionPolicy.SOURCE)
	public @interface LoadImageRequestCode {
	}

	@IntDef({CREATE_FILE_DEFAULT, CREATE_FILE_TAKE_PHOTO})
	@Retention(RetentionPolicy.SOURCE)
	public @interface CreateFileRequestCode {
	}

	@IntDef({REQUEST_CODE_IMPORTPNG, REQUEST_CODE_LOAD_PICTURE, REQUEST_CODE_FINISH, REQUEST_CODE_TAKE_PICTURE, REQUEST_CODE_LANGUAGE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ActivityRequestCode {
	}

	@IntDef({PERMISSION_EXTERNAL_STORAGE_SAVE, PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
			PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
			PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC,
			PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY})
	@Retention(RetentionPolicy.SOURCE)
	public @interface PermissionRequestCode {
	}

	private MainActivityConstants() {
		throw new AssertionError();
	}
}
