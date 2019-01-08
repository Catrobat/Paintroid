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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.catrobat.paintroid.BuildConfig;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode;
import org.catrobat.paintroid.common.MainActivityConstants.CreateFileRequestCode;
import org.catrobat.paintroid.common.MainActivityConstants.LoadImageRequestCode;
import org.catrobat.paintroid.common.MainActivityConstants.PermissionRequestCode;
import org.catrobat.paintroid.common.MainActivityConstants.SaveImageRequestCode;
import org.catrobat.paintroid.contract.MainActivityContracts.BottomBarViewHolder;
import org.catrobat.paintroid.contract.MainActivityContracts.DrawerLayoutViewHolder;
import org.catrobat.paintroid.contract.MainActivityContracts.Interactor;
import org.catrobat.paintroid.contract.MainActivityContracts.MainView;
import org.catrobat.paintroid.contract.MainActivityContracts.Model;
import org.catrobat.paintroid.contract.MainActivityContracts.NavigationDrawerViewHolder;
import org.catrobat.paintroid.contract.MainActivityContracts.Navigator;
import org.catrobat.paintroid.contract.MainActivityContracts.Presenter;
import org.catrobat.paintroid.contract.MainActivityContracts.TopBarViewHolder;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.iotasks.CreateFileAsync.CreateFileCallback;
import org.catrobat.paintroid.iotasks.LoadImageAsync.LoadImageCallback;
import org.catrobat.paintroid.iotasks.SaveImageAsync.SaveImageCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.DefaultToolFactory;
import org.catrobat.paintroid.tools.implementation.ImportTool;

import java.io.File;

import static org.catrobat.paintroid.common.Constants.EXTERNAL_STORAGE_PERMISSION_DIALOG;
import static org.catrobat.paintroid.common.MainActivityConstants.CREATE_FILE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.CREATE_FILE_TAKE_PHOTO;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_CATROID;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_IMPORTPNG;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_IMPORTPNG;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LANGUAGE;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LOAD_PICTURE;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_TAKE_PICTURE;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_BACK_TO_PC;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_LOAD_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_NEW_EMPTY;
import static org.catrobat.paintroid.tools.Tool.StateChange.NEW_IMAGE_LOADED;
import static org.catrobat.paintroid.tools.Tool.StateChange.RESET_INTERNAL_STATE;

public class MainActivityPresenter implements Presenter, SaveImageCallback, LoadImageCallback,
		CreateFileCallback {

	private MainView view;
	private Model model;
	private Navigator navigator;
	private Interactor interactor;
	private TopBarViewHolder topBarViewHolder;
	private BottomBarViewHolder bottomBarViewHolder;
	private DrawerLayoutViewHolder drawerLayoutViewHolder;
	private NavigationDrawerViewHolder navigationDrawerViewHolder;

	private CommandManager commandManager;
	private CommandFactory commandFactory = new DefaultCommandFactory();
	private boolean resetPerspectiveAfterNextCommand;
	private Bundle toolBundle = new Bundle();
	private ToolFactory toolFactory = new DefaultToolFactory();
	private boolean focusAfterRecreate = true;
	private Uri saveUriWhilePermissionHandling;

	public MainActivityPresenter(MainView view, Model model, Navigator navigator, Interactor interactor,
			TopBarViewHolder topBarViewHolder, BottomBarViewHolder bottomBarViewHolder,
			DrawerLayoutViewHolder drawerLayoutViewHolder,
			NavigationDrawerViewHolder navigationDrawerViewHolder, CommandManager commandManager) {
		this.view = view;
		this.model = model;
		this.navigator = navigator;
		this.interactor = interactor;
		this.bottomBarViewHolder = bottomBarViewHolder;
		this.drawerLayoutViewHolder = drawerLayoutViewHolder;
		this.navigationDrawerViewHolder = navigationDrawerViewHolder;
		this.commandManager = commandManager;
		this.topBarViewHolder = topBarViewHolder;
	}

	private boolean isImageUnchanged() {
		return !commandManager.isUndoAvailable();
	}

	@Override
	public void loadImageClicked() {
		if (isImageUnchanged() || model.isSaved()) {
			navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
		} else {
			navigator.showSaveBeforeLoadImageDialog(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW, model.getSavedPictureUri());
		}
	}

	@Override
	public void loadNewImage() {
		navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
	}

	@Override
	public void newImageClicked() {
		if (isImageUnchanged() && !model.isOpenedFromCatroid() || model.isSaved()) {
			navigator.showChooseNewImageDialog();
		} else {
			navigator.showSaveBeforeNewImageDialog(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY, model.getSavedPictureUri());
		}
	}

	@Override
	public void chooseNewImage() {
		navigator.showChooseNewImageDialog();
	}

	private void showSecurityQuestionBeforeExit() {
		if (isImageUnchanged() || model.isSaved()) {
			navigator.finishActivity();
		} else if (model.isOpenedFromCatroid()) {
			navigator.showSaveBeforeReturnToCatroidDialog(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC, model.getSavedPictureUri());
		} else {
			navigator.showSaveBeforeFinishDialog(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH, model.getSavedPictureUri());
		}
	}

	@Override
	public void saveCopyClicked() {
		checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY, null);
	}

	@Override
	public void saveImageClicked() {
		checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE, null);
	}

	@Override
	public void enterFullscreenClicked() {
		model.setFullScreen(true);
		enterFullScreen();
	}

	@Override
	public void exitFullscreenClicked() {
		model.setFullScreen(false);
		exitFullScreen();
	}

	@Override
	public void backToPocketCodeClicked() {
		showSecurityQuestionBeforeExit();
	}

	@Override
	public void showHelpClicked() {
		navigator.startWelcomeActivity();
	}

	@Override
	public void showAboutClicked() {
		navigator.showAboutDialog();
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
	public void onNewImageFromCamera() {
		interactor.createFile(this, CREATE_FILE_TAKE_PHOTO, null);
	}

	@Override
	public void handleActivityResult(@ActivityRequestCode int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			Log.d(MainActivity.TAG, "handleActivityResult: result not ok, most likely a dialog has been canceled");
			return;
		}

		DisplayMetrics metrics = view.getDisplayMetrics();
		int maxWidth = metrics.widthPixels;
		int maxHeight = metrics.heightPixels;
		switch (requestCode) {
			case REQUEST_CODE_IMPORTPNG:
				Uri selectedGalleryImageUri = data.getData();
				Tool tool = toolFactory.createTool((Activity) view, ToolType.IMPORTPNG);
				switchTool(tool);
				interactor.loadFile(this, LOAD_IMAGE_IMPORTPNG, maxWidth, maxHeight, selectedGalleryImageUri);
				break;
			case REQUEST_CODE_FINISH:
				navigator.finishActivity();
				break;
			case REQUEST_CODE_LANGUAGE:
				navigator.recreateActivity();
				break;
			case REQUEST_CODE_LOAD_PICTURE:
				interactor.loadFile(this, LOAD_IMAGE_DEFAULT, maxWidth, maxHeight, data.getData());
				break;
			case REQUEST_CODE_TAKE_PICTURE:
				interactor.loadFile(this, LOAD_IMAGE_DEFAULT, maxWidth, maxHeight, model.getCameraImageUri());
				break;
			default:
				view.forwardActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void handlePermissionRequestResults(@PermissionRequestCode int requestCode, String[] permissions, int[] grantResults) {
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			switch (requestCode) {
				case PERMISSION_EXTERNAL_STORAGE_SAVE:
					interactor.saveImage(this, SAVE_IMAGE_DEFAULT, model.getSavedPictureUri());
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_COPY:
					interactor.saveCopy(this, SAVE_IMAGE_DEFAULT);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC:
					saveImageConfirmClicked(SAVE_IMAGE_BACK_TO_PC, saveUriWhilePermissionHandling);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH:
					saveImageConfirmClicked(SAVE_IMAGE_FINISH, saveUriWhilePermissionHandling);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW:
					saveImageConfirmClicked(SAVE_IMAGE_LOAD_NEW, saveUriWhilePermissionHandling);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY:
					navigator.showChooseNewImageDialog();
					break;
				default:
					Log.d(MainActivity.TAG, "handlePermissionRequestResults: permission granted not handled");
			}
		} else {
			navigator.showPermissionDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
							EXTERNAL_STORAGE_PERMISSION_DIALOG, requestCode);
		}
	}

	@Override
	public void checkPermissionAndForward(@PermissionRequestCode int requestCode, Uri uri) {
		if (navigator.isSdkAboveOrEqualM() && !navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			saveUriWhilePermissionHandling = uri;
			navigator.askForPermission(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
		} else {
			switch (requestCode) {
				case PERMISSION_EXTERNAL_STORAGE_SAVE_COPY:
					interactor.saveCopy(this, SAVE_IMAGE_DEFAULT);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE:
					interactor.saveImage(this, SAVE_IMAGE_DEFAULT, model.getSavedPictureUri());
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC:
					saveImageConfirmClicked(SAVE_IMAGE_BACK_TO_PC, uri);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH:
					saveImageConfirmClicked(SAVE_IMAGE_FINISH, uri);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW:
					saveImageConfirmClicked(SAVE_IMAGE_LOAD_NEW, uri);
					break;
				case PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY:
					saveImageConfirmClicked(SAVE_IMAGE_NEW_EMPTY, uri);
					break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayoutViewHolder.isDrawerOpen(GravityCompat.START)) {
			drawerLayoutViewHolder.closeDrawer(Gravity.START, true);
		} else if (drawerLayoutViewHolder.isDrawerOpen(GravityCompat.END)) {
			drawerLayoutViewHolder.closeDrawer(Gravity.END, true);
		} else if (model.isFullScreen()) {
			exitFullscreenClicked();
		} else if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.toggleShowToolOptions();
		} else if (PaintroidApplication.currentTool.getToolType() != ToolType.BRUSH) {
			switchTool(ToolType.BRUSH);
		} else {
			showSecurityQuestionBeforeExit();
		}
	}

	@Override
	public void saveImageConfirmClicked(int requestCode, Uri uri) {
		interactor.saveImage(this, requestCode, uri);
	}

	@Override
	public void undoClicked() {
		if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.hide();
		} else {
			commandManager.undo();
		}
	}

	@Override
	public void redoClicked() {
		if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.hide();
		} else {
			commandManager.redo();
		}
	}

	@Override
	public void showColorPickerClicked() {
		if (PaintroidApplication.currentTool.getToolType().isColorChangeAllowed()) {
			navigator.showColorPickerDialog();
		}
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
			PaintroidApplication.perspective.resetScaleAndTranslation();
		}

		model.setSaved(false);
		PaintroidApplication.currentTool.resetInternalState(RESET_INTERNAL_STATE);
		view.refreshDrawingSurface();
		refreshTopBarButtons();

		navigator.dismissIndeterminateProgressDialog();
	}

	@Override
	public void setTopBarColor(int color) {
		topBarViewHolder.setColorButtonColor(color);
	}

	@Override
	public void initializeFromCleanState(String extraPicturePath, String extraPictureName) {
		boolean isOpenedFromCatroid = extraPicturePath != null;
		model.setOpenedFromCatroid(isOpenedFromCatroid);
		if (isOpenedFromCatroid) {
			File imageFile = new File(extraPicturePath);
			if (imageFile.exists()) {
				model.setSavedPictureUri(view.getUriFromFile(imageFile));
				interactor.loadFile(this, LOAD_IMAGE_CATROID, model.getSavedPictureUri());
			} else {
				interactor.createFile(this, CREATE_FILE_DEFAULT, extraPictureName);
			}
		} else {
			PaintroidApplication.currentTool.resetInternalState(NEW_IMAGE_LOADED);
			model.setSavedPictureUri(null);
		}
	}

	@Override
	public void finishInitialize() {
		refreshTopBarButtons();
		topBarViewHolder.setColorButtonColor(PaintroidApplication.currentTool.getDrawPaint().getColor());
		bottomBarViewHolder.selectToolButton(PaintroidApplication.currentTool.getToolType());

		if (model.isFullScreen()) {
			enterFullScreen();
		} else {
			exitFullScreen();
		}

		if (model.isOpenedFromCatroid()) {
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_save_image);
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_save_duplicate);
		} else {
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_back_to_pocket_code);
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_export);
		}
		navigationDrawerViewHolder.setVersion(BuildConfig.VERSION_NAME);

		view.initializeActionBar(model.isOpenedFromCatroid());
	}

	private void exitFullScreen() {
		view.exitFullScreen();
		topBarViewHolder.show();
		bottomBarViewHolder.show();
		navigationDrawerViewHolder.hideExitFullScreen();
		navigationDrawerViewHolder.showEnterFullScreen();
		PaintroidApplication.currentTool.resetToggleOptions();

		PaintroidApplication.perspective.setFullscreen(false);
	}

	private void enterFullScreen() {
		view.enterFullScreen();
		topBarViewHolder.hide();
		bottomBarViewHolder.hide();
		navigationDrawerViewHolder.showExitFullScreen();
		navigationDrawerViewHolder.hideEnterFullScreen();

		PaintroidApplication.currentTool.hide();
		PaintroidApplication.perspective.setFullscreen(true);
	}

	@Override
	public void restoreState(boolean isFullScreen, boolean isSaved, boolean isOpenedFromCatroid,
			boolean wasInitialAnimationPlayed, @Nullable Uri savedPictureUri, @Nullable Uri cameraImageUri) {
		model.setFullScreen(isFullScreen);
		model.setSaved(isSaved);
		model.setOpenedFromCatroid(isOpenedFromCatroid);
		model.setInitialAnimationPlayed(wasInitialAnimationPlayed);
		model.setSavedPictureUri(savedPictureUri);
		model.setCameraImageUri(cameraImageUri);

		navigator.restoreFragmentListeners();

		PaintroidApplication.currentTool.resetInternalState(NEW_IMAGE_LOADED);
	}

	@Override
	public void onCreateTool() {
		Bundle bundle = new Bundle();
		ToolFactory toolFactory = new DefaultToolFactory();

		if (PaintroidApplication.currentTool == null) {
			PaintroidApplication.currentTool = toolFactory.createTool((Activity) view, ToolType.BRUSH);
			PaintroidApplication.currentTool.startTool();
		} else {
			Paint paint = PaintroidApplication.currentTool.getDrawPaint();
			PaintroidApplication.currentTool.leaveTool();
			PaintroidApplication.currentTool.onSaveInstanceState(bundle);
			PaintroidApplication.currentTool = toolFactory.createTool((Activity) view, PaintroidApplication.currentTool.getToolType());
			PaintroidApplication.currentTool.onRestoreInstanceState(bundle);
			PaintroidApplication.currentTool.startTool();
			PaintroidApplication.currentTool.setDrawPaint(paint);
		}
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
		bottomBarViewHolder.cancelAnimation();

		if (PaintroidApplication.currentTool.getToolType() == type) {
			PaintroidApplication.currentTool.toggleShowToolOptions();
		} else if (view.isKeyboardShown()) {
			view.hideKeyboard();
		} else {
			switchTool(type);
		}
	}

	private void switchTool(ToolType type) {
		if (type == ToolType.IMPORTPNG) {
			navigator.startImportImageActivity(REQUEST_CODE_IMPORTPNG);
		} else {
			Tool tool = toolFactory.createTool((Activity) view, type);
			switchTool(tool);
		}
	}

	@Override
	public void gotFocus() {
		ToolType currentToolType = PaintroidApplication.currentTool.getToolType();
		if (focusAfterRecreate) {
			if (model.wasInitialAnimationPlayed()) {
				bottomBarViewHolder.scrollToButton(currentToolType, false);
			} else {
				bottomBarViewHolder.startAnimation(currentToolType);
				model.setInitialAnimationPlayed(true);
			}
			focusAfterRecreate = false;
		}
	}

	private void switchTool(Tool tool) {
		Tool currentTool = PaintroidApplication.currentTool;
		Paint tempPaint = currentTool.getDrawPaint();

		currentTool.leaveTool();
		if (currentTool.getToolType() == tool.getToolType()) {
			currentTool.onSaveInstanceState(toolBundle);
			setTool(tool.getToolType());
			PaintroidApplication.currentTool = tool;
			tool.onRestoreInstanceState(toolBundle);
		} else {
			toolBundle.clear();
			setTool(tool.getToolType());
			PaintroidApplication.currentTool = tool;
		}
		tool.startTool();
		tool.setDrawPaint(tempPaint);
	}

	private void setTool(ToolType toolType) {
		final ToolType previousToolType = PaintroidApplication.currentTool.getToolType();

		bottomBarViewHolder.deSelectToolButton(previousToolType);
		bottomBarViewHolder.selectToolButton(toolType);
		bottomBarViewHolder.scrollToButton(toolType, true);

		int offset = topBarViewHolder.getHeight();
		navigator.showToolChangeToast(offset, toolType.getNameResource());
	}

	@Override
	public void onCreateFilePostExecute(@CreateFileRequestCode int requestCode, File file) {
		if (file == null) {
			navigator.showSaveErrorDialog();
			return;
		}

		switch (requestCode) {
			case CREATE_FILE_DEFAULT:
				model.setSavedPictureUri(view.getUriFromFile(file));
				break;
			case CREATE_FILE_TAKE_PHOTO:
				File tempImageFile = view.getExternalDirPictureFile();
				Uri uri = view.getFileProviderUriFromFile(tempImageFile);
				model.setCameraImageUri(uri);
				navigator.startTakePictureActivity(REQUEST_CODE_TAKE_PICTURE, uri);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public void onLoadImagePostExecute(@LoadImageRequestCode int requestCode, Uri uri, Bitmap bitmap) {
		if (bitmap == null) {
			navigator.showLoadErrorDialog();
			return;
		}

		switch (requestCode) {
			case LOAD_IMAGE_DEFAULT:
				resetPerspectiveAfterNextCommand = true;
				commandManager.setInitialStateCommand(commandFactory.createInitCommand(bitmap));
				commandManager.reset();
				model.setSavedPictureUri(null);
				model.setCameraImageUri(null);
				break;
			case LOAD_IMAGE_IMPORTPNG:
				if (PaintroidApplication.currentTool instanceof ImportTool) {
					((ImportTool) PaintroidApplication.currentTool).setBitmapFromFile(bitmap);
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
				navigator.showChooseNewImageDialog();
				break;
			case SAVE_IMAGE_DEFAULT:
				break;
			case SAVE_IMAGE_FINISH:
				navigator.finishActivity();
				return;
			case SAVE_IMAGE_LOAD_NEW:
				navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
				break;
			case SAVE_IMAGE_BACK_TO_PC:
				navigator.returnToPocketCode(uri.getPath());
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
	public boolean isFinishing() {
		return view.isFinishing();
	}
}
