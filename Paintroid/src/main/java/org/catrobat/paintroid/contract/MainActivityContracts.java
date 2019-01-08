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

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.iotasks.CreateFileAsync;
import org.catrobat.paintroid.iotasks.LoadImageAsync;
import org.catrobat.paintroid.iotasks.SaveImageAsync;
import org.catrobat.paintroid.tools.ToolType;

import java.io.File;

public interface MainActivityContracts {
	interface Navigator {
		void showColorPickerDialog();

		void startLoadImageActivity(int requestCode);

		void startTakePictureActivity(int requestCode, Uri cameraImageUri);

		void startImportImageActivity(int requestCode);

		void showAboutDialog();

		void startWelcomeActivity();

		void showIndeterminateProgressDialog();

		void dismissIndeterminateProgressDialog();

		void returnToPocketCode(String path);

		void showToast(@StringRes int resId, int duration);

		void showSaveErrorDialog();

		void showLoadErrorDialog();

		void showPermissionDialog(PermissionInfoDialog.PermissionType permissionType, String dialogTag, int requestCode);

		void askForPermission(String[] permissions, int requestCode);

		boolean isSdkAboveOrEqualM();

		boolean doIHavePermission(String permission);

		void finishActivity();

		void recreateActivity();

		void showSaveBeforeReturnToCatroidDialog(int requestCode, Uri savedPictureUri);

		void showSaveBeforeFinishDialog(int requestCode, Uri savedPictureUri);

		void showSaveBeforeNewImageDialog(int requestCode, Uri savedPictureUri);

		void showChooseNewImageDialog();

		void showSaveBeforeLoadImageDialog(int requestCode, Uri uri);

		void restoreFragmentListeners();

		void showToolChangeToast(int offset, int idRes);

		void broadcastAddPictureToGallery(Uri uri);
	}

	interface MainView {
		Presenter getPresenter();

		boolean isFinishing();

		ContentResolver getContentResolver();

		DisplayMetrics getDisplayMetrics();

		void initializeActionBar(boolean isOpenedFromCatroid);

		void forwardActivityResult(int requestCode, int resultCode, Intent data);

		Uri getUriFromFile(File file);

		void hideKeyboard();

		boolean isKeyboardShown();

		void refreshDrawingSurface();

		void enterFullScreen();

		void exitFullScreen();

		Uri getFileProviderUriFromFile(File file);

		File getExternalDirPictureFile();
	}

	interface Presenter {
		void initializeFromCleanState(String extraPicturePath, String extraPictureName);

		void restoreState(boolean isFullScreen, boolean isSaved, boolean isOpenedFromCatroid,
				boolean wasInitialAnimationPlayed, @Nullable Uri savedPictureUri, @Nullable Uri cameraImageUri);

		void finishInitialize();

		void checkPermissionAndForward(int requestCode, Uri uri);

		void loadImageClicked();

		void loadNewImage();

		void newImageClicked();

		void chooseNewImage();

		void saveCopyClicked();

		void saveImageClicked();

		void enterFullscreenClicked();

		void exitFullscreenClicked();

		void backToPocketCodeClicked();

		void showHelpClicked();

		void showAboutClicked();

		void onNewImage();

		void onNewImageFromCamera();

		void handleActivityResult(int requestCode, int resultCode, Intent data);

		void handlePermissionRequestResults(int requestCode, String[] permissions, int[] grantResults);

		void onBackPressed();

		void saveImageConfirmClicked(int requestCode, Uri uri);

		void undoClicked();

		void redoClicked();

		void showColorPickerClicked();

		void showLayerMenuClicked();

		void onCommandPreExecute();

		void onCommandPostExecute();

		void setTopBarColor(int color);

		void onCreateTool();

		void toolClicked(ToolType toolType);

		void gotFocus();
	}

	interface Model {
		Uri getCameraImageUri();

		void setCameraImageUri(Uri cameraImageUri);

		Uri getSavedPictureUri();

		void setSavedPictureUri(Uri savedPictureUri);

		boolean isSaved();

		void setSaved(boolean saved);

		boolean isFullScreen();

		void setFullScreen(boolean fullScreen);

		boolean isOpenedFromCatroid();

		void setOpenedFromCatroid(boolean openedFromCatroid);

		boolean wasInitialAnimationPlayed();

		void setInitialAnimationPlayed(boolean wasInitialAnimationPlayed);
	}

	interface Interactor {
		void saveCopy(SaveImageAsync.SaveImageCallback callback, int requestCode);

		void createFile(CreateFileAsync.CreateFileCallback callback, int requestCode, @Nullable String filename);

		void saveImage(SaveImageAsync.SaveImageCallback callback, int requestCode, Uri uri);

		void loadFile(LoadImageAsync.LoadImageCallback callback, int requestCode, Uri uri);

		void loadFile(LoadImageAsync.LoadImageCallback callback, int requestCode, int maxWidth, int maxHeight, Uri uri);
	}

	interface TopBarViewHolder {
		void enableUndoButton();

		void disableUndoButton();

		void enableRedoButton();

		void disableRedoButton();

		void setColorButtonColor(@ColorInt int color);

		void hide();

		void show();

		int getHeight();
	}

	interface DrawerLayoutViewHolder {

		void closeDrawer(int gravity, boolean animate);

		boolean isDrawerOpen(int gravity);

		void openDrawer(int gravity);
	}

	interface NavigationDrawerViewHolder {
		void removeItem(@IdRes int id);

		void setVersion(String versionString);

		void showExitFullScreen();

		void hideExitFullScreen();

		void showEnterFullScreen();

		void hideEnterFullScreen();
	}

	interface BottomBarViewHolder {
		void show();

		void hide();

		void startAnimation(ToolType toolType);

		void selectToolButton(ToolType toolType);

		void deSelectToolButton(ToolType toolType);

		void cancelAnimation();

		void scrollToButton(ToolType toolType, boolean animate);
	}
}
