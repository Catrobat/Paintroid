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

package org.catrobat.paintroid.contract;

import android.net.Uri;
import android.support.annotation.StringRes;

public interface MainActivityContracts {
	interface Navigator {
		void showColorPickerDialog();

		void startLanguageActivity(int requestCode);

		void startLoadImageActivity(int requestCode);

		void startTakePictureActivity(int requestCode, Uri cameraImageUri);

		void startImportImageActivity(int requestCode);

		void showAboutDialog();

		void startWelcomeActivity();

		void showTermsOfServiceDialog();

		void showIndeterminateProgressDialog();

		void dismissIndeterminateProgressDialog();

		void returnToPocketCode(String path);

		void showToast(@StringRes int resId, int duration);

		void showSaveErrorDialog();

		void showLoadErrorDialog();

		void finishActivity();

		void showSaveBeforeReturnToCatroidDialog(int requestCode, Uri savedPictureUri);

		void showSaveBeforeFinishDialog(int requestCode, Uri savedPictureUri);

		void showSaveBeforeNewImageDialog(int requestCode, Uri savedPictureUri);

		void showChooseNewImageDialog();

		void showSaveBeforeLoadImageDialog(int requestCode, Uri uri);
	}
}
