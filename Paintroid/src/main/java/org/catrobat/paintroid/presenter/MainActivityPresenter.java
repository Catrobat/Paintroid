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

package org.catrobat.paintroid.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;

import org.catrobat.paintroid.FileIODataTransfer;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.UserPreferences;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode;
import org.catrobat.paintroid.common.MainActivityConstants.CreateFileRequestCode;
import org.catrobat.paintroid.common.MainActivityConstants.LoadImageRequestCode;
import org.catrobat.paintroid.common.MainActivityConstants.PermissionRequestCode;
import org.catrobat.paintroid.common.MainActivityConstants.SaveImageRequestCode;
import org.catrobat.paintroid.contract.MainActivityContracts.BottomBarViewHolder;
import org.catrobat.paintroid.contract.MainActivityContracts.BottomNavigationViewHolder;
import org.catrobat.paintroid.contract.MainActivityContracts.DrawerLayoutViewHolder;
import org.catrobat.paintroid.contract.MainActivityContracts.Interactor;
import org.catrobat.paintroid.contract.MainActivityContracts.MainView;
import org.catrobat.paintroid.contract.MainActivityContracts.Model;
import org.catrobat.paintroid.contract.MainActivityContracts.Navigator;
import org.catrobat.paintroid.contract.MainActivityContracts.Presenter;
import org.catrobat.paintroid.contract.MainActivityContracts.TopBarViewHolder;
import org.catrobat.paintroid.controller.ToolController;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.iotasks.CreateFileAsync.CreateFileCallback;
import org.catrobat.paintroid.iotasks.LoadImageAsync.LoadImageCallback;
import org.catrobat.paintroid.iotasks.SaveImageAsync.SaveImageCallback;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.ui.LayerAdapter;
import org.catrobat.paintroid.ui.Perspective;

import java.io.File;

import static org.catrobat.paintroid.common.Constants.SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG;
import static org.catrobat.paintroid.common.MainActivityConstants.CREATE_FILE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_CATROID;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_IMPORTPNG;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_REQUEST_CODE_LOAD_PICTURE;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_IMPORTPNG;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_INTRO;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LOAD_PICTURE;
import static org.catrobat.paintroid.common.MainActivityConstants.RESULT_INTRO_MW_NOT_SUPPORTED;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_LOAD_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_NEW_EMPTY;

public class MainActivityPresenter implements Presenter, SaveImageCallback, LoadImageCallback, CreateFileCallback {
	private Activity fileActivity;
	private MainView view;
	private Model model;
	private Workspace workspace;
	private Navigator navigator;
	private Interactor interactor;
	private TopBarViewHolder topBarViewHolder;
	private Perspective perspective;
	private BottomBarViewHolder bottomBarViewHolder;
	private DrawerLayoutViewHolder drawerLayoutViewHolder;
	private BottomNavigationViewHolder bottomNavigationViewHolder;
	private LayerAdapter layerAdapter;

	private CommandManager commandManager;
	private CommandFactory commandFactory;
	private boolean resetPerspectiveAfterNextCommand;
	private ToolController toolController;
	private Context context;
	private UserPreferences sharedPreferences;

	public MainActivityPresenter(MainView view, Context context, Model model, Workspace workspace, Navigator navigator,
			Interactor interactor, TopBarViewHolder topBarViewHolder, BottomBarViewHolder bottomBarViewHolder,
			DrawerLayoutViewHolder drawerLayoutViewHolder, BottomNavigationViewHolder bottomNavigationViewHolder,
			CommandFactory commandFactory, CommandManager commandManager, Perspective perspective, ToolController toolController, UserPreferences sharedPreferences) {
		this.view = view;
		this.model = model;
		this.workspace = workspace;
		this.navigator = navigator;
		this.interactor = interactor;
		this.bottomBarViewHolder = bottomBarViewHolder;
		this.drawerLayoutViewHolder = drawerLayoutViewHolder;
		this.commandManager = commandManager;
		this.topBarViewHolder = topBarViewHolder;
		this.perspective = perspective;
		this.toolController = toolController;
		this.commandFactory = commandFactory;
		this.bottomNavigationViewHolder = bottomNavigationViewHolder;
		this.context = context;
		this.sharedPreferences = sharedPreferences;
	}

	private boolean isImageUnchanged() {
		return !commandManager.isUndoAvailable();
	}

	@Override
	public void loadImageClicked() {
		switchBetweenVersions(PERMISSION_REQUEST_CODE_LOAD_PICTURE);
		setFirstCheckBoxInLayerMenu();
	}

	public void setFirstCheckBoxInLayerMenu() {
		if (layerAdapter != null && layerAdapter.getViewHolderAt(0) != null) {
			layerAdapter.getViewHolderAt(0).setCheckBox(true);
		}
	}

	@Override
	public void saveBeforeLoadImage() {
		switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW);
	}

	@Override
	public void loadNewImage() {
		navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
		setFirstCheckBoxInLayerMenu();
	}

	@Override
	public Context getContext() {
		return this.context;
	}


	@Override
	public void newImageClicked() {
		if (isImageUnchanged() || model.isSaved()) {
			onNewImage();
			setFirstCheckBoxInLayerMenu();
		} else {
			navigator.showSaveBeforeNewImageDialog();
			setFirstCheckBoxInLayerMenu();
		}
	}

	@Override
	public void saveBeforeNewImage() {
		switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY);
	}

	private void showSecurityQuestionBeforeExit() {
		if (isImageUnchanged() || model.isSaved()) {
			finishActivity();
		} else if (model.isOpenedFromCatroid()) {
			navigator.showSaveBeforeReturnToCatroidDialog();
		} else {
			navigator.showSaveBeforeFinishDialog();
		}
	}

	@Override
	public void finishActivity() {
		navigator.finishActivity();
	}

	@Override
	public void saveBeforeFinish() {
		switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH);
	}

	@Override
	public void saveCopyClicked() {
		switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY);
	}

	@Override
	public void saveImageClicked() {
		switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE);
		showLikeUsDialogIfFirstTimeSave();
	}

	@Override
	public void shareImageClicked() {
		Bitmap bitmap = workspace.getBitmapOfAllLayers();
		navigator.startShareImageActivity(bitmap);
	}

	private void showLikeUsDialogIfFirstTimeSave() {
		boolean dialogHasBeenShown = sharedPreferences.getBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, false);

		if (!dialogHasBeenShown && !model.isOpenedFromCatroid()) {
			navigator.showLikeUsDialog();

			sharedPreferences.setBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, true);
		}
	}

	@Override
	public void enterFullscreenClicked() {
		model.setFullscreen(true);
		enterFullscreen();
	}

	@Override
	public void exitFullscreenClicked() {
		model.setFullscreen(false);
		exitFullscreen();
	}

	@Override
	public void backToPocketCodeClicked() {
		showSecurityQuestionBeforeExit();
	}

	@Override
	public void showHelpClicked() {
		navigator.startWelcomeActivity(REQUEST_CODE_INTRO);
	}

	@Override
	public void showAboutClicked() {
		navigator.showAboutDialog();
	}

	@Override
	public void showRateUsDialog() {
		navigator.showRateUsDialog();
	}

	@Override
	public void showFeedbackDialog() {
		navigator.showFeedbackDialog();
	}

	@Override
	public void sendFeedback() {
		navigator.sendFeedback();
	}

	@Override
	public void onNewImage() {
		DisplayMetrics metrics = view.getDisplayMetrics();
		resetPerspectiveAfterNextCommand = true;
		model.setSavedPictureUri(null);
		Command initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels);
		commandManager.setInitialStateCommand(initCommand);
		commandManager.reset();
	}

	@Override
	public void discardImageClicked() {
		commandManager.addCommand(commandFactory.createResetCommand());
	}

	private void switchBetweenVersions(@PermissionRequestCode int requestCode) {
		if (navigator.isSdkAboveOrEqualQ()) {
			switch (requestCode) {
				case PERMISSION_REQUEST_CODE_LOAD_PICTURE:
					askForReadAndWriteExternalStoragePermission(PERMISSION_REQUEST_CODE_LOAD_PICTURE);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE:
					saveImageConfirmClicked(SAVE_IMAGE_DEFAULT, model.getSavedPictureUri());
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_COPY:
					Bitmap bitmap = workspace.getBitmapOfAllLayers();
					interactor.saveCopy(this, SAVE_IMAGE_DEFAULT, bitmap);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW:
					saveImageConfirmClicked(SAVE_IMAGE_LOAD_NEW, model.getSavedPictureUri());
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY:
					saveImageConfirmClicked(SAVE_IMAGE_NEW_EMPTY, model.getSavedPictureUri());
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH:
					saveImageConfirmClicked(SAVE_IMAGE_FINISH, model.getSavedPictureUri());
					break;
			}
		} else {
			if (requestCode == PERMISSION_REQUEST_CODE_LOAD_PICTURE) {
				if (isImageUnchanged() || model.isSaved()) {
					navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
					setFirstCheckBoxInLayerMenu();
				} else {
					navigator.showSaveBeforeLoadImageDialog();
					setFirstCheckBoxInLayerMenu();
				}
			} else {
				askForReadAndWriteExternalStoragePermission(requestCode);
			}
		}
	}

	private void askForReadAndWriteExternalStoragePermission(@PermissionRequestCode int requestCode) {
		if (model.isOpenedFromCatroid() && requestCode == PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH) {
			if (!navigator.isSdkAboveOrEqualQ()) {
				handleRequestPermissionsResult(requestCode,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						new int[]{PackageManager.PERMISSION_GRANTED});
			}

			return;
		}

		if (navigator.isSdkAboveOrEqualQ()) {
			if (!navigator.doIHavePermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
				navigator.askForPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
			} else {
				handleRequestPermissionsResult(requestCode,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						new int[]{PackageManager.PERMISSION_GRANTED});
			}
		} else {
			if (navigator.isSdkAboveOrEqualM() && !navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				navigator.askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
			} else {
				handleRequestPermissionsResult(requestCode,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						new int[]{PackageManager.PERMISSION_GRANTED});
			}
		}
	}

	@Override
	public void handleActivityResult(@ActivityRequestCode int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_IMPORTPNG:
				if (resultCode != Activity.RESULT_OK) {
					return;
				}
				Uri selectedGalleryImageUri = data.getData();
				setTool(ToolType.IMPORTPNG);
				toolController.switchTool(ToolType.IMPORTPNG);
				interactor.loadFile(this, LOAD_IMAGE_IMPORTPNG, selectedGalleryImageUri, getContext(), false);
				break;
			case REQUEST_CODE_LOAD_PICTURE:
				if (resultCode != Activity.RESULT_OK) {
					return;
				}
				interactor.loadFile(this, LOAD_IMAGE_DEFAULT, data.getData(), getContext(), false);
				break;
			case REQUEST_CODE_INTRO:
				if (resultCode == RESULT_INTRO_MW_NOT_SUPPORTED) {
					navigator.showToast(R.string.pocketpaint_intro_split_screen_not_supported, Toast.LENGTH_LONG);
				}
				break;
			default:
				view.superHandleActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void loadScaledImage(Uri uri, @LoadImageRequestCode int requestCode) {
		switch (requestCode) {
			case LOAD_IMAGE_IMPORTPNG:
				setTool(ToolType.IMPORTPNG);
				toolController.switchTool(ToolType.IMPORTPNG);
				interactor.loadFile(this, LOAD_IMAGE_IMPORTPNG, uri, context, true);
				break;
			case LOAD_IMAGE_CATROID:
			case LOAD_IMAGE_DEFAULT:
				interactor.loadFile(this, LOAD_IMAGE_DEFAULT, uri, context, true);
				break;
			default:
				Log.e(MainActivity.TAG, "wrong request code for loading pictures");
				break;
		}
	}

	@Override
	public void handleRequestPermissionsResult(@PermissionRequestCode int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (permissions.length == 1 && (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
				|| permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Bitmap bitmap;
				switch (requestCode) {
					case PERMISSION_EXTERNAL_STORAGE_SAVE:
						bitmap = workspace.getBitmapOfAllLayers();
						interactor.saveImage(this, SAVE_IMAGE_DEFAULT, bitmap, model.getSavedPictureUri());
						break;
					case PERMISSION_EXTERNAL_STORAGE_SAVE_COPY:
						bitmap = workspace.getBitmapOfAllLayers();
						interactor.saveCopy(this, SAVE_IMAGE_DEFAULT, bitmap);
						break;
					case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH:
						saveImageConfirmClicked(SAVE_IMAGE_FINISH, model.getSavedPictureUri());
						break;
					case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW:
						saveImageConfirmClicked(SAVE_IMAGE_LOAD_NEW, model.getSavedPictureUri());
						break;
					case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY:
						saveImageConfirmClicked(SAVE_IMAGE_NEW_EMPTY, model.getSavedPictureUri());
						break;
					case PERMISSION_REQUEST_CODE_LOAD_PICTURE:
						if (isImageUnchanged() || model.isSaved()) {
							navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
						} else {
							navigator.showSaveBeforeLoadImageDialog();
						}
						break;
					default:
						view.superHandleRequestPermissionsResult(requestCode, permissions, grantResults);
						break;
				}
			} else {
				if (navigator.isPermissionPermanentlyDenied(permissions)) {
					navigator.showRequestPermanentlyDeniedPermissionRationaleDialog();
				} else {
					navigator.showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
							permissions, requestCode);
				}
			}
		} else {
			view.superHandleRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayoutViewHolder.isDrawerOpen(GravityCompat.START)) {
			drawerLayoutViewHolder.closeDrawer(Gravity.START, true);
		} else if (drawerLayoutViewHolder.isDrawerOpen(GravityCompat.END)) {
			drawerLayoutViewHolder.closeDrawer(Gravity.END, true);
		} else if (model.isFullscreen()) {
			exitFullscreenClicked();
		} else if (toolController.toolOptionsViewVisible()) {
			toolController.hideToolOptionsView();
		} else if (!toolController.isDefaultTool()) {
			setTool(ToolType.BRUSH);
			toolController.switchTool(ToolType.BRUSH);
		} else {
			showSecurityQuestionBeforeExit();
		}
	}

	@Override
	public void saveImageConfirmClicked(int requestCode, Uri uri) {
		Bitmap bitmap = workspace.getBitmapOfAllLayers();
		interactor.saveImage(this, requestCode, bitmap, uri);
	}

	@Override
	public void undoClicked() {
		if (view.isKeyboardShown()) {
			view.hideKeyboard();
		} else if (toolController.toolOptionsViewVisible()) {
			toolController.hideToolOptionsView();
		} else {
			commandManager.undo();
		}
	}

	@Override
	public void redoClicked() {
		if (view.isKeyboardShown()) {
			view.hideKeyboard();
		} else if (toolController.toolOptionsViewVisible()) {
			toolController.hideToolOptionsView();
		} else {
			commandManager.redo();
		}
	}

	@Override
	public void showColorPickerClicked() {
		navigator.showColorPickerDialog();
	}

	@Override
	public void showLayerMenuClicked() {
		drawerLayoutViewHolder.openDrawer(Gravity.END);
	}

	@Override
	public void onCommandPreExecute() {
		navigator.showIndeterminateProgressDialog();
	}

	@Override
	public void onCommandPostExecute() {
		if (resetPerspectiveAfterNextCommand) {
			resetPerspectiveAfterNextCommand = false;
			workspace.resetPerspective();
		}

		model.setSaved(false);
		toolController.resetToolInternalState();
		view.refreshDrawingSurface();
		refreshTopBarButtons();

		navigator.dismissIndeterminateProgressDialog();
	}

	@Override
	public void setBottomNavigationColor(int color) {
		bottomNavigationViewHolder.setColorButtonColor(color);
	}

	@Override
	public void initializeFromCleanState(String extraPicturePath, String extraPictureName) {
		boolean isOpenedFromCatroid = extraPicturePath != null;
		model.setOpenedFromCatroid(isOpenedFromCatroid);
		if (isOpenedFromCatroid) {
			File imageFile = new File(extraPicturePath);
			if (imageFile.exists()) {
				model.setSavedPictureUri(view.getUriFromFile(imageFile));
				interactor.loadFile(this, LOAD_IMAGE_CATROID, model.getSavedPictureUri(), context, false);
			} else {
				interactor.createFile(this, CREATE_FILE_DEFAULT, extraPictureName);
			}
		} else {
			toolController.resetToolInternalStateOnImageLoaded();
			model.setSavedPictureUri(null);
		}
	}

	@Override
	public void finishInitialize() {
		refreshTopBarButtons();
		bottomNavigationViewHolder.setColorButtonColor(toolController.getToolColor());
		bottomNavigationViewHolder.showCurrentTool(toolController.getToolType());

		if (model.isFullscreen()) {
			enterFullscreen();
		} else {
			exitFullscreen();
		}

		view.initializeActionBar(model.isOpenedFromCatroid());

		if (commandManager.isBusy()) {
			navigator.showIndeterminateProgressDialog();
		}
	}

	@Override
	public void removeMoreOptionsItems(Menu menu) {
		if (model.isOpenedFromCatroid()) {
			topBarViewHolder.removeStandaloneMenuItems(menu);
			topBarViewHolder.hideTitleIfNotStandalone();
		} else {
			topBarViewHolder.removeCatroidMenuItems(menu);
		}
	}

	private void exitFullscreen() {
		view.exitFullscreen();
		topBarViewHolder.show();
		bottomNavigationViewHolder.show();
		toolController.enableToolOptionsView();
		perspective.exitFullscreen();
	}

	private void enterFullscreen() {
		view.hideKeyboard();
		view.enterFullscreen();
		topBarViewHolder.hide();
		bottomBarViewHolder.hide();
		bottomNavigationViewHolder.hide();
		toolController.disableToolOptionsView();
		perspective.enterFullscreen();
	}

	@Override
	public void restoreState(boolean isFullscreen, boolean isSaved, boolean isOpenedFromCatroid,
	                         boolean wasInitialAnimationPlayed, @Nullable Uri savedPictureUri, @Nullable Uri cameraImageUri) {
		model.setFullscreen(isFullscreen);
		model.setSaved(isSaved);
		model.setOpenedFromCatroid(isOpenedFromCatroid);
		model.setInitialAnimationPlayed(wasInitialAnimationPlayed);
		model.setSavedPictureUri(savedPictureUri);
		model.setCameraImageUri(cameraImageUri);

		navigator.restoreFragmentListeners();

		toolController.resetToolInternalStateOnImageLoaded();
	}

	@Override
	public void onCreateTool() {
		toolController.createTool();
	}

	private void refreshTopBarButtons() {
		if (commandManager.isUndoAvailable()) {
			topBarViewHolder.enableUndoButton();
		} else {
			topBarViewHolder.disableUndoButton();
		}
		if (commandManager.isRedoAvailable()) {
			topBarViewHolder.enableRedoButton();
		} else {
			topBarViewHolder.disableRedoButton();
		}
	}

	@Override
	public void toolClicked(ToolType type) {
		bottomBarViewHolder.hide();

		if (toolController.getToolType() == type && toolController.hasToolOptionsView()) {
			toolController.toggleToolOptionsView();
		} else if (view.isKeyboardShown()) {
			view.hideKeyboard();
		} else {
			switchTool(type);
		}
	}

	private void switchTool(ToolType type) {
		setTool(type);
		toolController.switchTool(type);

		if (type == ToolType.IMPORTPNG) {
			showImportDialog();
		}
	}

	private void setTool(ToolType toolType) {
		bottomBarViewHolder.hide();
		bottomNavigationViewHolder.showCurrentTool(toolType);

		int offset = topBarViewHolder.getHeight();
		navigator.showToolChangeToast(offset, toolType.getNameResource());
	}

	@Override
	public void onCreateFilePostExecute(@CreateFileRequestCode int requestCode, File file) {
		if (file == null) {
			navigator.showSaveErrorDialog();
			return;
		}

		if (requestCode == CREATE_FILE_DEFAULT) {
			model.setSavedPictureUri(view.getUriFromFile(file));
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void onLoadImagePostExecute(@LoadImageRequestCode int requestCode, Uri uri, FileIODataTransfer result) {

		if (result.getToBeScaled()) {
			navigator.showScaleImageRequestDialog(uri, requestCode);
			return;
		}
		if (result.getBitmap() == null) {
			navigator.showLoadErrorDialog();
			return;
		}
		Bitmap bitmap = result.getBitmap();

		switch (requestCode) {
			case LOAD_IMAGE_DEFAULT:
				resetPerspectiveAfterNextCommand = true;
				commandManager.setInitialStateCommand(commandFactory.createInitCommand(bitmap));
				commandManager.reset();
				model.setSavedPictureUri(null);
				model.setCameraImageUri(null);
				break;
			case LOAD_IMAGE_IMPORTPNG:
				if (toolController.getToolType() == ToolType.IMPORTPNG) {
					toolController.setBitmapFromSource(bitmap);
				} else {
					Log.e(MainActivity.TAG, "importPngToFloatingBox: Current tool is no ImportTool as required");
				}
				break;
			case LOAD_IMAGE_CATROID:
				resetPerspectiveAfterNextCommand = true;
				commandManager.setInitialStateCommand(commandFactory.createInitCommand(bitmap));
				commandManager.reset();
				model.setSavedPictureUri(uri);
				model.setCameraImageUri(null);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public void onLoadImagePreExecute(@LoadImageRequestCode int requestCode) {
	}

	@Override
	public void onSaveImagePreExecute(@SaveImageRequestCode int requestCode) {
		navigator.showIndeterminateProgressDialog();
	}

	@Override
	public void onSaveImagePostExecute(@SaveImageRequestCode int requestCode, Uri uri, boolean saveAsCopy) {
		navigator.dismissIndeterminateProgressDialog();

		if (uri == null) {
			navigator.showSaveErrorDialog();
			return;
		}

		if (saveAsCopy) {
			navigator.showToast(R.string.copy, Toast.LENGTH_LONG);
		} else {
			navigator.showToast(R.string.saved, Toast.LENGTH_LONG);
			model.setSavedPictureUri(uri);
			model.setSaved(true);
		}

		if (!model.isOpenedFromCatroid() || saveAsCopy) {
			navigator.broadcastAddPictureToGallery(uri);
		}

		switch (requestCode) {
			case SAVE_IMAGE_NEW_EMPTY:
				onNewImage();
				break;
			case SAVE_IMAGE_DEFAULT:
				break;
			case SAVE_IMAGE_FINISH:
				if (model.isOpenedFromCatroid()) {
					navigator.returnToPocketCode(uri.getPath());
				} else {
					navigator.finishActivity();
				}
				return;
			case SAVE_IMAGE_LOAD_NEW:
				navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public ContentResolver getContentResolver() {
		return view.getContentResolver();
	}

	@Override
	public Activity getFileActivity() {
		return fileActivity;
	}

	@Override
	public boolean isFinishing() {
		return view.isFinishing();
	}

	@Override
	public void actionToolsClicked() {
		if (toolController.toolOptionsViewVisible()) {
			toolController.hideToolOptionsView();
		}

		if (bottomBarViewHolder.isVisible()) {
			bottomBarViewHolder.hide();
		} else {
			if (!layerAdapter.getPresenter().getLayerItem(workspace.getCurrentLayerIndex()).getCheckBox()) {
				navigator.showToast(R.string.no_tools_on_hidden_layer, Toast.LENGTH_SHORT);
				return;
			}
			bottomBarViewHolder.show();
		}
	}

	@Override
	public void actionCurrentToolClicked() {
		if (toolController.getToolType() == ToolType.IMPORTPNG) {
			showImportDialog();
			return;
		}

		if (bottomBarViewHolder.isVisible()) {
			bottomBarViewHolder.hide();
		}

		if (toolController.toolOptionsViewVisible()) {
			toolController.hideToolOptionsView();
		} else {
			if (toolController.hasToolOptionsView()) {
				toolController.showToolOptionsView();
			}
		}
	}

	@Override
	public void rateUsClicked() {
		navigator.rateUsClicked();
	}

	public void setLayerAdapter(LayerAdapter layerAdapter) {
		this.layerAdapter = layerAdapter;
	}

	@Override
	public void importFromGalleryClicked() {
		navigator.startImportImageActivity(REQUEST_CODE_IMPORTPNG);
	}

	@Override
	public void showImportDialog() {
		navigator.showImageImportDialog();
	}

	@Override
	public void importStickersClicked() {
		navigator.showCatroidMediaGallery();
	}

	@Override
	public void bitmapLoadedFromSource(Bitmap loadedImage) {
		toolController.setBitmapFromSource(loadedImage);
	}
}
