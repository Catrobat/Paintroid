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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.catrobat.paintroid.BuildConfig;
import org.catrobat.paintroid.MainActivity;
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
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.DefaultToolFactory;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.ui.Perspective;

import java.io.File;

import static org.catrobat.paintroid.common.MainActivityConstants.CREATE_FILE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_CATROID;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_IMPORTPNG;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_IMPORTPNG;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LANGUAGE;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LOAD_PICTURE;
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
	private Workspace workspace;
	private ToolReference toolReference;
	private Navigator navigator;
	private Interactor interactor;
	private TopBarViewHolder topBarViewHolder;
	private ToolPaint toolPaint;
	private Perspective perspective;
	private BottomBarViewHolder bottomBarViewHolder;
	private DrawerLayoutViewHolder drawerLayoutViewHolder;
	private NavigationDrawerViewHolder navigationDrawerViewHolder;

	private CommandManager commandManager;
	private CommandFactory commandFactory = new DefaultCommandFactory();
	private boolean resetPerspectiveAfterNextCommand;
	private Bundle toolBundle = new Bundle();
	private ToolFactory toolFactory = new DefaultToolFactory();
	private boolean focusAfterRecreate = true;

	public MainActivityPresenter(MainView view, Model model, Workspace workspace,
			ToolReference toolReference, Navigator navigator, Interactor interactor,
			TopBarViewHolder topBarViewHolder, BottomBarViewHolder bottomBarViewHolder,
			DrawerLayoutViewHolder drawerLayoutViewHolder,
			NavigationDrawerViewHolder navigationDrawerViewHolder, CommandManager commandManager,
			ToolPaint toolPaint, Perspective perspective) {
		this.view = view;
		this.model = model;
		this.workspace = workspace;
		this.toolReference = toolReference;
		this.navigator = navigator;
		this.interactor = interactor;
		this.bottomBarViewHolder = bottomBarViewHolder;
		this.drawerLayoutViewHolder = drawerLayoutViewHolder;
		this.navigationDrawerViewHolder = navigationDrawerViewHolder;
		this.commandManager = commandManager;
		this.topBarViewHolder = topBarViewHolder;
		this.toolPaint = toolPaint;
		this.perspective = perspective;
	}

	private boolean isImageUnchanged() {
		return !commandManager.isUndoAvailable();
	}

	@Override
	public void loadImageClicked() {
		if (isImageUnchanged() || model.isSaved()) {
			navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
		} else {
			navigator.showSaveBeforeLoadImageDialog();
		}
	}

	@Override
	public void saveBeforeLoadImage() {
		askForWriteExternalStoragePermission(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW);
	}

	@Override
	public void loadNewImage() {
		navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
	}

	@Override
	public void newImageClicked() {
		if (isImageUnchanged() || model.isSaved()) {
			onNewImage();
		} else {
			navigator.showSaveBeforeNewImageDialog();
		}
	}

	@Override
	public void saveBeforeNewImage() {
		askForWriteExternalStoragePermission(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY);
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
		askForWriteExternalStoragePermission(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH);
	}

	@Override
	public void saveCopyClicked() {
		askForWriteExternalStoragePermission(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY);
	}

	@Override
	public void saveImageClicked() {
		askForWriteExternalStoragePermission(PERMISSION_EXTERNAL_STORAGE_SAVE);
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
	public void discardImageClicked() {
		commandManager.addCommand(commandFactory.createResetCommand());
	}

	private void askForWriteExternalStoragePermission(@PermissionRequestCode int requestCode) {
		if (navigator.isSdkAboveOrEqualM() && !navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			navigator.askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
		} else {
			handleRequestPermissionsResult(requestCode,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					new int[]{PackageManager.PERMISSION_GRANTED});
		}
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
				Tool tool = toolFactory.createTool(ToolType.IMPORTPNG, (Activity) view, commandManager, workspace, toolPaint);
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
			default:
				view.superHandleActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void handleRequestPermissionsResult(@PermissionRequestCode int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (permissions.length == 1 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
					default:
						view.superHandleRequestPermissionsResult(requestCode, permissions, grantResults);
						break;
				}
			} else {
				navigator.showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
						permissions, requestCode);
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
		} else if (toolReference.get().getToolOptionsAreShown()) {
			toolReference.get().toggleShowToolOptions();
		} else if (toolReference.get().getToolType() != ToolType.BRUSH) {
			switchTool(ToolType.BRUSH);
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
		if (toolReference.get().getToolOptionsAreShown()) {
			toolReference.get().hide();
		} else {
			commandManager.undo();
		}
	}

	@Override
	public void redoClicked() {
		if (toolReference.get().getToolOptionsAreShown()) {
			toolReference.get().hide();
		} else {
			commandManager.redo();
		}
	}

	@Override
	public void showColorPickerClicked() {
		if (toolReference.get().getToolType().isColorChangeAllowed()) {
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
			workspace.resetPerspective();
		}

		model.setSaved(false);
		toolReference.get().resetInternalState(RESET_INTERNAL_STATE);
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
			toolReference.get().resetInternalState(NEW_IMAGE_LOADED);
			model.setSavedPictureUri(null);
		}
	}

	@Override
	public void finishInitialize() {
		refreshTopBarButtons();
		topBarViewHolder.setColorButtonColor(toolReference.get().getDrawPaint().getColor());
		bottomBarViewHolder.selectToolButton(toolReference.get().getToolType());

		if (model.isFullscreen()) {
			enterFullscreen();
		} else {
			exitFullscreen();
		}

		if (model.isOpenedFromCatroid()) {
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_save_image);
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_save_duplicate);
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_new_image);
		} else {
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_back_to_pocket_code);
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_export);
			navigationDrawerViewHolder.removeItem(R.id.pocketpaint_nav_discard_image);
		}
		navigationDrawerViewHolder.setVersion(BuildConfig.VERSION_NAME);

		view.initializeActionBar(model.isOpenedFromCatroid());

		if (!commandManager.isBusy()) {
			navigator.dismissIndeterminateProgressDialog();
		}
	}

	private void exitFullscreen() {
		view.exitFullscreen();
		topBarViewHolder.show();
		bottomBarViewHolder.show();
		navigationDrawerViewHolder.hideExitFullscreen();
		navigationDrawerViewHolder.showEnterFullscreen();

		toolReference.get().resetToggleOptions();
		perspective.exitFullscreen();
	}

	private void enterFullscreen() {
		view.enterFullscreen();
		topBarViewHolder.hide();
		bottomBarViewHolder.hide();
		navigationDrawerViewHolder.showExitFullscreen();
		navigationDrawerViewHolder.hideEnterFullscreen();

		toolReference.get().hide();
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

		toolReference.get().resetInternalState(NEW_IMAGE_LOADED);
	}

	@Override
	public void onCreateTool() {
		Bundle bundle = new Bundle();
		ToolFactory toolFactory = new DefaultToolFactory();

		if (toolReference.get() == null) {
			toolReference.set(toolFactory.createTool(ToolType.BRUSH, (Activity) view, commandManager, workspace, toolPaint));
			toolReference.get().startTool();
		} else {
			Paint paint = toolReference.get().getDrawPaint();
			toolReference.get().leaveTool();
			toolReference.get().onSaveInstanceState(bundle);
			toolReference.set(toolFactory.createTool(toolReference.get().getToolType(), (Activity) view, commandManager, workspace, toolPaint));
			toolReference.get().onRestoreInstanceState(bundle);
			toolReference.get().startTool();
			toolReference.get().setDrawPaint(paint);
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

		if (toolReference.get().getToolType() == type && type.hasOptions()) {
			toolReference.get().toggleShowToolOptions();
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
			Tool tool = toolFactory.createTool(type, (Activity) view, commandManager, workspace, toolPaint);
			switchTool(tool);
		}
	}

	@Override
	public void gotFocus() {
		ToolType currentToolType = toolReference.get().getToolType();
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
		Tool currentTool = toolReference.get();
		Paint tempPaint = currentTool.getDrawPaint();

		currentTool.leaveTool();
		if (currentTool.getToolType() == tool.getToolType()) {
			currentTool.onSaveInstanceState(toolBundle);
			setTool(tool.getToolType());
			toolReference.set(tool);
			tool.onRestoreInstanceState(toolBundle);
		} else {
			toolBundle.clear();
			setTool(tool.getToolType());
			toolReference.set(tool);
		}
		tool.startTool();
		tool.setDrawPaint(tempPaint);
	}

	private void setTool(ToolType toolType) {
		final ToolType previousToolType = toolReference.get().getToolType();

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
				if (toolReference.get() instanceof ImportTool) {
					((ImportTool) toolReference.get()).setBitmapFromFile(bitmap);
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
	public boolean isFinishing() {
		return view.isFinishing();
	}
}
