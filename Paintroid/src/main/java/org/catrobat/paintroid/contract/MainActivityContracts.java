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
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Menu;

import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.iotasks.CreateFileAsync;
import org.catrobat.paintroid.iotasks.LoadImageAsync;
import org.catrobat.paintroid.iotasks.SaveImageAsync;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.LayerAdapter;

import java.io.File;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public interface MainActivityContracts {
	interface Navigator {
		void showColorPickerDialog();

		void startLoadImageActivity(@ActivityRequestCode int requestCode);

		void startImportImageActivity(@ActivityRequestCode int requestCode);

		void showAboutDialog();

		void showLikeUsDialog();

		void showRateUsDialog();

		void showFeedbackDialog();

		void showOverwriteDialog(int permissionCode);

		void showPngInformationDialog();

		void showJpgInformationDialog();

		void sendFeedback();

		void startWelcomeActivity(@ActivityRequestCode int requestCode);

		void startShareImageActivity(Bitmap bitmap);

		void showIndeterminateProgressDialog();

		void dismissIndeterminateProgressDialog();

		void returnToPocketCode(String path);

		void showToast(@StringRes int resId, int duration);

		void showSaveErrorDialog();

		void showLoadErrorDialog();

		void showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType permissionType, String[] permissions, int requestCode);

		void showRequestPermanentlyDeniedPermissionRationaleDialog();

		void askForPermission(String[] permissions, int requestCode);

		boolean isSdkAboveOrEqualM();

		boolean isSdkAboveOrEqualQ();

		boolean doIHavePermission(String permission);

		boolean isPermissionPermanentlyDenied(String[] permission);

		void finishActivity();

		void showSaveBeforeFinishDialog();

		void showSaveBeforeNewImageDialog();

		void showSaveBeforeLoadImageDialog();

		void showSaveImageInformationDialogWhenStandalone(int permissionCode, int imageNumber, boolean isExport);

		void restoreFragmentListeners();

		void showToolChangeToast(int offset, int idRes);

		void broadcastAddPictureToGallery(Uri uri);

		void rateUsClicked();

		void showImageImportDialog();

		void showCatroidMediaGallery();
	}

	interface MainView {
		Presenter getPresenter();

		boolean isFinishing();

		ContentResolver getContentResolver();

		DisplayMetrics getDisplayMetrics();

		void initializeActionBar(boolean isOpenedFromCatroid);

		void superHandleActivityResult(int requestCode, int resultCode, Intent data);

		void superHandleRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

		Uri getUriFromFile(File file);

		void hideKeyboard();

		boolean isKeyboardShown();

		void refreshDrawingSurface();

		void enterFullscreen();

		void exitFullscreen();
	}

	interface Presenter {
		void initializeFromCleanState(String extraPicturePath, String extraPictureName);

		void restoreState(boolean isFullscreen, boolean isSaved, boolean isOpenedFromCatroid,
				boolean wasInitialAnimationPlayed, @Nullable Uri savedPictureUri, @Nullable Uri cameraImageUri);

		void finishInitialize();

		void removeMoreOptionsItems(Menu menu);

		void loadImageClicked();

		void loadNewImage();

		void newImageClicked();

		void discardImageClicked();

		void saveCopyClicked(boolean isExport);

		void saveImageClicked();

		void shareImageClicked();

		void enterFullscreenClicked();

		void exitFullscreenClicked();

		void backToPocketCodeClicked();

		void showHelpClicked();

		void showAboutClicked();

		void showRateUsDialog();

		void showFeedbackDialog();

		void showOverwriteDialog(int permissionCode);

		void showPngInformationDialog();

		void showJpgInformationDialog();

		void sendFeedback();

		void onNewImage();

		void switchBetweenVersions(int requestCode);

		void handleActivityResult(int requestCode, int resultCode, Intent data);

		void handleRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

		void onBackPressed();

		void saveImageConfirmClicked(int requestCode, Uri uri);

		void undoClicked();

		void redoClicked();

		void showColorPickerClicked();

		void showLayerMenuClicked();

		void onCommandPostExecute();

		void setBottomNavigationColor(int color);

		void onCreateTool();

		void toolClicked(ToolType toolType);

		void saveBeforeLoadImage();

		void saveBeforeNewImage();

		void saveBeforeFinish();

		void finishActivity();

		void actionToolsClicked();

		void actionCurrentToolClicked();

		void rateUsClicked();

		void importFromGalleryClicked();

		void showImportDialog();

		void importStickersClicked();

		void bitmapLoadedFromSource(Bitmap loadedImage);

		void setLayerAdapter(LayerAdapter layerAdapter);

		int getImageNumber();

		Bitmap getBitmap();
	}

	interface Model {
		Uri getCameraImageUri();

		void setCameraImageUri(Uri cameraImageUri);

		Uri getSavedPictureUri();

		void setSavedPictureUri(Uri savedPictureUri);

		boolean isSaved();

		void setSaved(boolean saved);

		boolean isFullscreen();

		void setFullscreen(boolean fullscreen);

		boolean isOpenedFromCatroid();

		void setOpenedFromCatroid(boolean openedFromCatroid);

		boolean wasInitialAnimationPlayed();

		void setInitialAnimationPlayed(boolean wasInitialAnimationPlayed);
	}

	interface Interactor {
		void saveCopy(SaveImageAsync.SaveImageCallback callback, int requestCode, Bitmap bitmap);

		void createFile(CreateFileAsync.CreateFileCallback callback, int requestCode, @Nullable String filename);

		void saveImage(SaveImageAsync.SaveImageCallback callback, int requestCode, Bitmap bitmap, Uri uri);

		void loadFile(LoadImageAsync.LoadImageCallback callback, int requestCode, Uri uri);

		void loadFile(LoadImageAsync.LoadImageCallback callback, int requestCode, int maxWidth, int maxHeight, Uri uri);
	}

	interface TopBarViewHolder {
		void enableUndoButton();

		void disableUndoButton();

		void enableRedoButton();

		void disableRedoButton();

		void hide();

		void show();

		int getHeight();

		void removeStandaloneMenuItems(Menu menu);

		void removeCatroidMenuItems(Menu menu);

		void hideTitleIfNotStandalone();
	}

	interface DrawerLayoutViewHolder {

		void closeDrawer(int gravity, boolean animate);

		boolean isDrawerOpen(int gravity);

		void openDrawer(int gravity);
	}

	interface BottomBarViewHolder {
		void show();

		void hide();

		boolean isVisible();
	}

	interface BottomNavigationViewHolder {
		void show();

		void hide();

		void showCurrentTool(ToolType toolType);

		void setColorButtonColor(@ColorInt int color);
	}

	interface BottomNavigationAppearance {
		void showCurrentTool(ToolType toolType);
	}
}
