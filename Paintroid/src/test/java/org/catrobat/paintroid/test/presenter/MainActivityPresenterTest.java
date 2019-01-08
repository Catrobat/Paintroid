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

package org.catrobat.paintroid.test.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.util.DisplayMetrics;
import android.widget.Toast;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.iotasks.SaveImageAsync;
import org.catrobat.paintroid.presenter.MainActivityPresenter;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.catrobat.paintroid.common.Constants.EXTERNAL_STORAGE_PERMISSION_DIALOG;
import static org.catrobat.paintroid.common.MainActivityConstants.CREATE_FILE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.CREATE_FILE_TAKE_PHOTO;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_CATROID;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_FINISH;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityPresenterTest {
	@Mock
	private MainActivityContracts.MainView view;

	@Mock
	private MainActivityContracts.Model model;

	@Mock
	private MainActivityContracts.Navigator navigator;

	@Mock
	private MainActivityContracts.Interactor interactor;

	@Mock
	private MainActivityContracts.TopBarViewHolder topBarViewHolder;

	@Mock
	private MainActivityContracts.DrawerLayoutViewHolder drawerLayoutViewHolder;

	@Mock
	private MainActivityContracts.NavigationDrawerViewHolder navigationDrawerViewHolder;

	@Mock
	private CommandManager commandManager;

	@Mock
	private MainActivityContracts.BottomBarViewHolder bottomBarViewHolder;

	@InjectMocks
	private MainActivityPresenter presenter;

	@Test
	public void testSetUp() {
		verifyZeroInteractions(view, model, navigator, interactor, topBarViewHolder,
				drawerLayoutViewHolder, navigationDrawerViewHolder, commandManager, bottomBarViewHolder);
	}

	@Test
	public void testNewImageClickedWhenUnchangedThenShowNewImageDialog() {
		presenter.newImageClicked();

		verify(navigator).showChooseNewImageDialog();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testNewImageClickedWhenUndoAvailableAndSavedThenShowNewImageDialog() {
		when(commandManager.isUndoAvailable()).thenReturn(true);
		when(model.isSaved()).thenReturn(true);

		presenter.newImageClicked();

		verify(navigator).showChooseNewImageDialog();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testNewImageClickedWhenUndoAvailableAndNotSavedThenShowSaveBeforeNewImage() {
		when(commandManager.isUndoAvailable()).thenReturn(true);
		when(model.isSaved()).thenReturn(false);

		presenter.newImageClicked();

		verify(navigator).showSaveBeforeNewImageDialog(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY, model.getSavedPictureUri());
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testChooseNewImageThenShowNewImageDialog() {
		presenter.chooseNewImage();

		verify(navigator).showChooseNewImageDialog();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testBackToCatroidClickedWhenUnchangedThenFinishActivity() {
		presenter.backToPocketCodeClicked();

		verify(navigator).finishActivity();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testOnCreateFilePostExecuteWhenTakePictureThenStartActivity() {
		File file = mock(File.class);

		presenter.onCreateFilePostExecute(CREATE_FILE_TAKE_PHOTO, file);

		Uri uri = view.getFileProviderUriFromFile(file);

		verify(model).setCameraImageUri(uri);
		verify(navigator).startTakePictureActivity(REQUEST_CODE_TAKE_PICTURE, uri);
		verifyNoMoreInteractions(navigator);
		verify(model, never()).setSavedPictureUri(any(Uri.class));
	}

	@Test
	public void testBackToCatroidClickedWhenUndoAvailableThenShowSaveDialog() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.backToPocketCodeClicked();

		verify(navigator).showSaveBeforeFinishDialog(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH, uri);
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testBackToCatroidClickedWhenUndoAvailableAndSavedThenFinishActivity() {
		when(model.isSaved()).thenReturn(true);
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.backToPocketCodeClicked();

		verify(navigator).finishActivity();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testBackToCatroidClickedWhenUndoAvailableAndOpenedFromCatroidThenShowSaveBeforeReturnDialog() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);
		when(model.isOpenedFromCatroid()).thenReturn(true);
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.backToPocketCodeClicked();

		verify(navigator).showSaveBeforeReturnToCatroidDialog(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC, uri);
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testLoadImageClickedLoad() {

		presenter.loadImageClicked();

		verify(navigator).startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
		verifyNoMoreInteractions(interactor);
	}

	@Test
	public void testLoadImageClickedSaveFirst() {
		when(commandManager.isUndoAvailable()).thenReturn(true);
		when(model.isSaved()).thenReturn(false);
		presenter.loadImageClicked();

		verify(navigator).showSaveBeforeLoadImageDialog(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW, model.getSavedPictureUri());
		verifyNoMoreInteractions(interactor);
	}

	@Test
	public void testLoadNewImage() {
		presenter.loadNewImage();

		verify(navigator).startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
		verifyNoMoreInteractions(interactor);
	}

	@Test
	public void testSaveCopyClickedThenSaveImage() {
		presenter.saveCopyClicked();

		verify(interactor).saveCopy(presenter, SAVE_IMAGE_DEFAULT);
		verifyNoMoreInteractions(interactor);
	}

	@Test
	public void testSaveImageClickedThenSaveImageWithUri() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.saveImageClicked();

		verify(interactor).saveImage(presenter, SAVE_IMAGE_DEFAULT, uri);
		verifyNoMoreInteractions(interactor);
	}

	@Test
	public void testEnterFullscreenClicked() {
		PaintroidApplication.currentTool = mock(Tool.class);
		PaintroidApplication.perspective = mock(Perspective.class);

		presenter.enterFullscreenClicked();

		verify(model).setFullScreen(true);
		verify(topBarViewHolder).hide();
		verify(view).enterFullScreen();
		verify(navigationDrawerViewHolder).hideEnterFullScreen();
		verify(navigationDrawerViewHolder).showExitFullScreen();
		verify(PaintroidApplication.currentTool).hide();
		verify(PaintroidApplication.perspective).setFullscreen(true);
	}

	@Test
	public void testExitFullscreenClicked() {
		PaintroidApplication.currentTool = mock(Tool.class);
		PaintroidApplication.perspective = mock(Perspective.class);

		presenter.exitFullscreenClicked();

		verify(model).setFullScreen(false);
		verify(topBarViewHolder).show();
		verify(view).exitFullScreen();
		verify(navigationDrawerViewHolder).showEnterFullScreen();
		verify(navigationDrawerViewHolder).hideExitFullScreen();
		verify(PaintroidApplication.perspective).setFullscreen(false);
	}

	@Test
	public void testShowHelpClickedThenStartWelcomeActivity() {
		presenter.showHelpClicked();

		verify(navigator).startWelcomeActivity();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testShowAboutClickedThenShowAboutDialog() {
		presenter.showAboutClicked();

		verify(navigator).showAboutDialog();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testOnNewImageThenResetCommandManager() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);

		presenter.onNewImage();

		verify(commandManager).reset();
	}

	@Test
	public void testOnNewImageWhenCommandReturnsThenResetPerspective() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		PaintroidApplication.perspective = mock(Perspective.class);

		presenter.onNewImage();
		presenter.onCommandPostExecute();

		verify(PaintroidApplication.perspective).resetScaleAndTranslation();
	}

	@Test
	public void testOnNewImageThenSetInitialStateCommand() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);

		presenter.onNewImage();

		verify(commandManager).setInitialStateCommand(any(Command.class));
	}

	@Test
	public void testOnNewImageThenResetSavedPictureUri() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);

		presenter.onNewImage();

		verify(model).setSavedPictureUri(null);
	}

	@Test
	public void testOnNewImageFromCameraThenCreateFile() {
		presenter.onNewImageFromCamera();

		verify(interactor).createFile(presenter, CREATE_FILE_TAKE_PHOTO, null);
	}

	@Test
	public void testHandleActivityResultWhenUnhandledThenForwardResult() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(0, Activity.RESULT_OK, intent);

		verify(view).forwardActivityResult(0, Activity.RESULT_OK, intent);
	}

	@Test
	public void testHandleActivityResultWhenFinishThenFinishActivity() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(REQUEST_CODE_FINISH, Activity.RESULT_OK, intent);

		verify(navigator).finishActivity();
	}

	@Test
	public void testHandleActivityResultWhenLanguageThenRecreateActivity() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(REQUEST_CODE_LANGUAGE, Activity.RESULT_OK, intent);

		verify(navigator).recreateActivity();
	}

	@Test
	public void testHandleActivityResultWhenLoadPictureThenLoadIntentPicture() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		metrics.widthPixels = 13;
		metrics.heightPixels = 17;
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);
		Uri uri = mock(Uri.class);
		when(intent.getData()).thenReturn(uri);

		presenter.handleActivityResult(REQUEST_CODE_LOAD_PICTURE, Activity.RESULT_OK, intent);

		verify(interactor).loadFile(presenter, LOAD_IMAGE_DEFAULT, 13, 17, uri);
	}

	@Test
	public void testHandleActivityResultWhenTakePictureThenLoadCameraUriPicture() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		metrics.widthPixels = 13;
		metrics.heightPixels = 17;
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);
		Uri uri = mock(Uri.class);
		when(model.getCameraImageUri()).thenReturn(uri);

		presenter.handleActivityResult(REQUEST_CODE_TAKE_PICTURE, Activity.RESULT_OK, intent);

		verify(interactor).loadFile(presenter, LOAD_IMAGE_DEFAULT, 13, 17, uri);
	}

	@Test
	public void testHandleActivityResultWhenResultNotOkThenDoNothing() {
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(0, Activity.RESULT_CANCELED, intent);

		verifyZeroInteractions(view, interactor, navigator);
	}

	@Test
	public void testOnBackPressedWhenUntouchedThenFinishActivity() {
		PaintroidApplication.currentTool = mock(Tool.class);
		when(PaintroidApplication.currentTool.getToolType()).thenReturn(ToolType.BRUSH);

		presenter.onBackPressed();

		verify(navigator).finishActivity();
	}

	@Test
	public void testOnBackPressedWhenStartDrawerOpenThenCloseDrawer() {
		PaintroidApplication.currentTool = mock(Tool.class);
		when(drawerLayoutViewHolder.isDrawerOpen(GravityCompat.START)).thenReturn(true);

		presenter.onBackPressed();

		verify(drawerLayoutViewHolder).closeDrawer(GravityCompat.START, true);
	}

	@Test
	public void testOnBackPressedWhenEndDrawerOpenThenCloseDrawer() {
		PaintroidApplication.currentTool = mock(Tool.class);
		when(drawerLayoutViewHolder.isDrawerOpen(GravityCompat.END)).thenReturn(true);

		presenter.onBackPressed();

		verify(drawerLayoutViewHolder).closeDrawer(GravityCompat.END, true);
	}

	@Test
	public void testOnBackPressedWhenIsFullScreenThenExitFullscreen() {
		PaintroidApplication.currentTool = mock(Tool.class);
		when(model.isFullScreen()).thenReturn(true);

		presenter.onBackPressed();

		verify(model).setFullScreen(false);
	}

	@Test
	public void testOnBackPressedWhenToolOptionsShownThenHideToolOptions() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolOptionsAreShown()).thenReturn(true);

		presenter.onBackPressed();

		verify(currentTool).toggleShowToolOptions();
	}

	@Test
	public void testSaveImageConfirmClickedThenSaveImage() {
		Uri uri = mock(Uri.class);

		presenter.saveImageConfirmClicked(0, uri);

		verify(interactor).saveImage(presenter, 0, uri);
	}

	@Test
	public void testSaveImageConfirmClickedThenUseRequestCode() {
		Uri uri = mock(Uri.class);

		presenter.saveImageConfirmClicked(-1, uri);

		verify(interactor).saveImage(presenter, -1, uri);
	}

	@Test
	public void testUndoClickedThenExecuteUndo() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.undoClicked();

		verify(commandManager).undo();
	}

	@Test
	public void testRedoClickedThenExecuteRedo() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.redoClicked();

		verify(commandManager).redo();
	}

	@Test
	public void testShowColorPickerClickedWhenColorChangeAllowedThenShowColorPickerDialog() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolType()).thenReturn(ToolType.BRUSH);

		presenter.showColorPickerClicked();

		verify(navigator).showColorPickerDialog();
	}

	@Test
	public void testShowColorPickerClickedWhenNoColorChangeAllowedThenIgnore() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolType()).thenReturn(ToolType.PIPETTE);

		presenter.showColorPickerClicked();

		verifyZeroInteractions(navigator, interactor, commandManager);
	}

	@Test
	public void testShowLayerMenuClickedThenShowLayerDrawer() {
		presenter.showLayerMenuClicked();

		verify(drawerLayoutViewHolder).openDrawer(GravityCompat.END);
	}

	@Test
	public void testOnCommandPreExecuteThenShowProgressDialog() {
		presenter.onCommandPreExecute();

		verify(navigator).showIndeterminateProgressDialog();
	}

	@Test
	public void testOnCommandPostExecuteThenSetModelUnsaved() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.onCommandPostExecute();

		verify(model).setSaved(false);
	}

	@Test
	public void testOnCommandPostExecuteThenResetInternalToolState() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.onCommandPostExecute();

		verify(PaintroidApplication.currentTool).resetInternalState(RESET_INTERNAL_STATE);
	}

	@Test
	public void testOnCommandPostExecuteThenRefreshDrawingSurface() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.onCommandPostExecute();

		verify(view).refreshDrawingSurface();
	}

	@Test
	public void testOnCommandPostExecuteThenSetUndoRedoButtons() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.onCommandPostExecute();

		verify(topBarViewHolder).disableRedoButton();
		verify(topBarViewHolder).disableUndoButton();
	}

	@Test
	public void testOnCommandPostExecuteThenDismissDialog() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.onCommandPostExecute();

		verify(navigator).dismissIndeterminateProgressDialog();
	}

	@Test
	public void testSetTopBarColorThenSetColorButtonColor() {
		presenter.setTopBarColor(Color.GREEN);

		verify(topBarViewHolder).setColorButtonColor(Color.GREEN);
	}

	@Test
	public void testInitializeFromCleanStateWhenDefaultThenUnsetSavedPictureUri() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.initializeFromCleanState(null, null);

		verify(model).setOpenedFromCatroid(false);
		verify(model).setSavedPictureUri(null);
	}

	@Test
	public void testInitializeFromCleanStateWhenDefaultThenResetTool() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;

		presenter.initializeFromCleanState(null, null);

		verify(currentTool).resetInternalState(NEW_IMAGE_LOADED);
	}

	@Test
	public void testInitializeFromCleanStateWhenFromCatroidAndPathNotExistentThenCreateFile() {
		presenter.initializeFromCleanState("testPath", "testName");

		verify(model).setOpenedFromCatroid(true);
		verify(interactor).createFile(presenter, CREATE_FILE_DEFAULT, "testName");
	}

	@Test
	public void testInitializeFromCleanStateWhenFromCatroidAndPathExistsThenLoadFile() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);
		when(view.getUriFromFile(any(File.class))).thenReturn(uri);

		presenter.initializeFromCleanState("/", "testName");

		verify(model).setOpenedFromCatroid(true);
		verify(model).setSavedPictureUri(uri);
		verify(interactor).loadFile(presenter, LOAD_IMAGE_CATROID, uri);
	}

	@Test
	public void testRestoreStateThenRestoreFragmentListeners() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.restoreState(false, false, false, false, null, null);

		verify(navigator).restoreFragmentListeners();
	}

	@Test
	public void testRestoreStateThenSetModel() {
		Uri savedPictureUri = mock(Uri.class);
		Uri cameraImageUri = mock(Uri.class);
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.restoreState(false, false, false, false, savedPictureUri, cameraImageUri);

		verify(model).setFullScreen(false);
		verify(model).setSaved(false);
		verify(model).setOpenedFromCatroid(false);
		verify(model).setInitialAnimationPlayed(false);
		verify(model).setSavedPictureUri(savedPictureUri);
		verify(model).setCameraImageUri(cameraImageUri);
	}

	@Test
	public void testRestoreStateWhenStatesSetThenSetModel() {
		Uri savedPictureUri = mock(Uri.class);
		Uri cameraImageUri = mock(Uri.class);
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.restoreState(true, true, true, true, savedPictureUri, cameraImageUri);

		verify(model).setFullScreen(true);
		verify(model).setSaved(true);
		verify(model).setOpenedFromCatroid(true);
		verify(model).setInitialAnimationPlayed(true);
		verify(model).setSavedPictureUri(savedPictureUri);
		verify(model).setCameraImageUri(cameraImageUri);
	}

	@Test
	public void testRestoreStateThenResetTool() {
		PaintroidApplication.currentTool = mock(Tool.class);

		presenter.restoreState(false, false, false, false, null, null);

		verify(PaintroidApplication.currentTool).resetInternalState(NEW_IMAGE_LOADED);
	}

	@Test
	public void testFinishInitializeThensetUndoRedoButtons() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));

		presenter.finishInitialize();

		verify(topBarViewHolder).disableUndoButton();
		verify(topBarViewHolder).disableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenUndoAvailableThensetUndoRedoButtons() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.finishInitialize();

		verify(topBarViewHolder).enableUndoButton();
		verify(topBarViewHolder).disableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenRedoAvailableThensetUndoRedoButtons() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(commandManager.isRedoAvailable()).thenReturn(true);

		presenter.finishInitialize();

		verify(topBarViewHolder).disableUndoButton();
		verify(topBarViewHolder).enableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenNotFullscreenThenRestoreState() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));

		presenter.finishInitialize();

		verify(view).exitFullScreen();
	}

	@Test
	public void testFinishInitializeWhenFullscreenThenRestoreState() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(model.isFullScreen()).thenReturn(true);

		presenter.finishInitialize();

		verify(view).enterFullScreen();
	}

	@Test
	public void testFinishInitializeThenRestoreColorButtonColor() {
		Tool currentTool = mock(Tool.class);
		Paint paint = mock(Paint.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(paint);
		when(model.isFullScreen()).thenReturn(true);
		when(paint.getColor()).thenReturn(Color.RED);

		presenter.finishInitialize();

		verify(topBarViewHolder).setColorButtonColor(Color.RED);
	}

	@Test
	public void testFinishInitializeThenRestoreSelectedTool() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(currentTool.getToolType()).thenReturn(ToolType.TEXT);

		presenter.finishInitialize();

		verify(bottomBarViewHolder).selectToolButton(ToolType.TEXT);
	}

	@Test
	public void testFinishInitializeWhenDefaultThenInitializeActionBarDefault() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));

		presenter.finishInitialize();

		verify(view).initializeActionBar(false);
	}

	@Test
	public void testFinishInitializeWhenFromCatroidThenInitializeActionBarCatroid() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.finishInitialize();

		verify(view).initializeActionBar(true);
	}

	@Test
	public void testFinishInitializeWhenDefaultThenRemoveCatroidNavigationItems() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));

		presenter.finishInitialize();

		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_export);
		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_back_to_pocket_code);
	}

	@Test
	public void testFinishInitializeWhenFromCatroidThenRemoveSaveNavigationItems() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = mock(Perspective.class);
		when(currentTool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.finishInitialize();

		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_save_image);
		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_save_duplicate);
	}

	@Test
	public void testToolClickedThenCancelAnimation() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolType()).thenReturn(ToolType.BRUSH);

		presenter.toolClicked(ToolType.BRUSH);

		verify(bottomBarViewHolder).cancelAnimation();
	}

	@Test
	public void testToolClickedWhenSameToolTypeThenToggleOptions() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolType()).thenReturn(ToolType.BRUSH);

		presenter.toolClicked(ToolType.BRUSH);

		verify(currentTool).toggleShowToolOptions();
	}

	@Test
	public void testToolClickedWhenKeyboardShownThenHideKeyboard() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolType()).thenReturn(ToolType.BRUSH);
		when(view.isKeyboardShown()).thenReturn(true);

		presenter.toolClicked(ToolType.ERASER);

		verify(view).hideKeyboard();
	}

	@Test
	public void testGotFocusThenPlayInitialAnimation() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolType()).thenReturn(ToolType.PIPETTE);

		presenter.gotFocus();

		verify(bottomBarViewHolder).startAnimation(ToolType.PIPETTE);
		verify(model).setInitialAnimationPlayed(true);
	}

	@Test
	public void testGotFocusWhenAlreadyPlayedThenScrollToTool() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolType()).thenReturn(ToolType.ERASER);
		when(model.wasInitialAnimationPlayed()).thenReturn(true);

		presenter.gotFocus();

		verify(bottomBarViewHolder).scrollToButton(ToolType.ERASER, false);
		verify(model, never()).setInitialAnimationPlayed(anyBoolean());
	}

	@Test
	public void testGotFocusWhenGotFocusBeforeThenDoNothing() {
		Tool currentTool = mock(Tool.class);
		PaintroidApplication.currentTool = currentTool;
		when(currentTool.getToolType()).thenReturn(ToolType.LINE);

		presenter.gotFocus();
		presenter.gotFocus();

		verify(bottomBarViewHolder).startAnimation(ToolType.LINE);
		verify(model).setInitialAnimationPlayed(true);
	}

	@Test
	public void testOnCreateFilePostExecuteWhenFailedThenShowDialog() {
		presenter.onCreateFilePostExecute(0, null);

		verify(navigator).showSaveErrorDialog();
	}

	@Test
	public void testOnCreateFilePostExecuteWhenDefaultThenSetUri() {
		File file = mock(File.class);
		Uri uri = mock(Uri.class);
		when(view.getUriFromFile(file)).thenReturn(uri);

		presenter.onCreateFilePostExecute(CREATE_FILE_DEFAULT, file);

		verify(model).setSavedPictureUri(uri);
		verifyZeroInteractions(navigator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnCreateFilePostExecuteWhenInvalidRequestThenThrowException() {
		File file = mock(File.class);

		presenter.onCreateFilePostExecute(0, file);
	}

	@Test
	public void testOnLoadImagePostExecuteWhenFailedThenShowDialog() {
		presenter.onLoadImagePostExecute(0, null, null);

		verify(navigator).showLoadErrorDialog();
	}

	@Test
	public void testOnLoadImagePostExecuteWhenDefaultThenResetModelUris() {
		Uri uri = mock(Uri.class);
		Bitmap bitmap = mock(Bitmap.class);

		presenter.onLoadImagePostExecute(LOAD_IMAGE_DEFAULT, uri, bitmap);

		verify(model).setSavedPictureUri(null);
		verify(model).setCameraImageUri(null);
	}

	@Test
	public void testOnLoadImagePostExecuteWhenDefaultThenResetCommandManager() {
		Uri uri = mock(Uri.class);
		Bitmap bitmap = mock(Bitmap.class);

		presenter.onLoadImagePostExecute(LOAD_IMAGE_DEFAULT, uri, bitmap);

		verify(commandManager).setInitialStateCommand(any(Command.class));
		verify(commandManager).reset();
		verifyNoMoreInteractions(commandManager);
	}

	@Test
	public void testOnLoadImagePostExecuteWhenCatroidThenSetModelUris() {
		Uri uri = mock(Uri.class);
		Bitmap bitmap = mock(Bitmap.class);

		presenter.onLoadImagePostExecute(LOAD_IMAGE_CATROID, uri, bitmap);

		verify(model).setSavedPictureUri(uri);
		verify(model).setCameraImageUri(null);
		verifyNoMoreInteractions(model);
	}

	@Test
	public void testOnLoadImagePostExecuteWhenCatroidThenResetCommandManager() {
		Uri uri = mock(Uri.class);
		Bitmap bitmap = mock(Bitmap.class);

		presenter.onLoadImagePostExecute(LOAD_IMAGE_CATROID, uri, bitmap);

		verify(commandManager).setInitialStateCommand(any(Command.class));
		verify(commandManager).reset();
		verifyNoMoreInteractions(commandManager);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnLoadImagePostExecuteWhenInvalidRequestThenThrowException() {
		Uri uri = mock(Uri.class);
		Bitmap bitmap = mock(Bitmap.class);

		presenter.onLoadImagePostExecute(0, uri, bitmap);
	}

	@Test
	public void testOnSaveImagePreExecuteThenShowProgressDialog() {
		presenter.onSaveImagePreExecute(0);

		verify(navigator).showIndeterminateProgressDialog();
	}

	@Test
	public void testOnSaveImagePostExecuteWhenFailedThenShowDialog() {
		presenter.onSaveImagePostExecute(0, null, false);

		verify(navigator).showSaveErrorDialog();
	}

	@Test
	public void testHandlePermissionResultsSavePermissionGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_DEFAULT), eq(uri));
	}

	@Test
	public void testHandlePermissionResultsSavePermissionNotGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showPermissionDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				EXTERNAL_STORAGE_PERMISSION_DIALOG, PERMISSION_EXTERNAL_STORAGE_SAVE);
	}

	@Test
	public void testHandlePermissionResultsSaveCopyPermissionGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		verify(interactor).saveCopy(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_DEFAULT));
	}

	@Test
	public void testHandlePermissionResultsSaveCopyPermissionNotGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showPermissionDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				EXTERNAL_STORAGE_PERMISSION_DIALOG, PERMISSION_EXTERNAL_STORAGE_SAVE_COPY);
	}

	@Test
	public void testHandlePermissionResultsSaveBeforeBackToPCPermissionGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_BACK_TO_PC), eq(uri));
	}

	@Test
	public void testHandlePermissionResultsSaveBeforeBackToPCPermissionNotGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showPermissionDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				EXTERNAL_STORAGE_PERMISSION_DIALOG, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC);
	}

	@Test
	public void testHandlePermissionResultsSaveBeforeFinishPermissionGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_FINISH), eq(uri));
	}

	@Test
	public void testHandlePermissionResultsSaveBeforeFinishPermissionNotGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showPermissionDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				EXTERNAL_STORAGE_PERMISSION_DIALOG, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH);
	}

	@Test
	public void testHandlePermissionResultsSaveBeforeLoadNewPermissionNotGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showPermissionDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				EXTERNAL_STORAGE_PERMISSION_DIALOG, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW);
	}

	@Test
	public void testHandlePermissionResultsSaveBeforeLoadNewPermissionGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_LOAD_NEW), eq(uri));
	}

	@Test
	public void testHandlePermissionResultsSaveBeforeNewEmptyPermissionNotGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showPermissionDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				EXTERNAL_STORAGE_PERMISSION_DIALOG, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY);
	}

	@Test
	public void testHandlePermissionResultsSaveBeforeNewEmptyPermissionGranted() {
		presenter.handlePermissionRequestResults(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[]{PackageManager.PERMISSION_GRANTED});

		verify(navigator).showChooseNewImageDialog();
	}

	@Test
	public void testOnNavigationItemSelectedSaveCopyPermissionGranted() {
		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY, null);

		verify(interactor).saveCopy(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_DEFAULT));
	}

	@Test
	public void testOnNavigationItemSelectedSaveCopyPermissionNotGranted() {
		doReturn(false).when(navigator).doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		doReturn(true).when(navigator).isSdkAboveOrEqualM();

		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY, null);

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_COPY);
	}

	@Test
	public void testOnNavigationItemSelectedSavePermissionGranted() {
		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE, null);

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_DEFAULT), eq(uri));
	}

	@Test
	public void testOnNavigationItemSelectedSavePermissionNotGranted() {
		doReturn(false).when(navigator).doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		doReturn(true).when(navigator).isSdkAboveOrEqualM();

		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE, null);

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE);
	}

	@Test
	public void testSaveAndBackToPocketCodePermissionGranted() {
		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC, null);

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_BACK_TO_PC), eq(uri));
	}

	@Test
	public void testSaveAndBackToPocketCodePermissionNotGranted() {
		doReturn(false).when(navigator).doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		doReturn(true).when(navigator).isSdkAboveOrEqualM();

		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC, null);

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_BACK_TO_PC);
	}

	@Test
	public void testSaveAndFinishPermissionGranted() {
		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH, null);

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_FINISH), eq(uri));
	}

	@Test
	public void testSaveAndFinishPermissionNotGranted() {
		doReturn(false).when(navigator).doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		doReturn(true).when(navigator).isSdkAboveOrEqualM();

		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH, null);

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH);
	}

	@Test
	public void testSaveAndNewImagePermissionGranted() {
		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY, null);

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_NEW_EMPTY), eq(uri));
	}

	@Test
	public void testSaveAndNewImagePermissionNotGranted() {
		doReturn(false).when(navigator).doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		doReturn(true).when(navigator).isSdkAboveOrEqualM();

		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY, null);

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY);
	}

	@Test
	public void testSaveAndLoadImagePermissionGranted() {
		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW, null);

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_LOAD_NEW), eq(uri));
	}

	@Test
	public void testSaveAndLoadImagePermissionNotGranted() {
		doReturn(false).when(navigator).doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		doReturn(true).when(navigator).isSdkAboveOrEqualM();

		presenter.checkPermissionAndForward(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW, null);

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW);
	}

	@Test
	public void testOnSaveImagePostExecuteThenDismissProgressDialog() {
		presenter.onSaveImagePostExecute(0, null, false);

		verify(navigator).dismissIndeterminateProgressDialog();
	}

	@Test
	public void testOnSaveImagePostExecuteWhenNotSavedAsCopyThenShowSaveToast() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, false);

		verify(navigator).showToast(R.string.saved, Toast.LENGTH_LONG);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenNotSavedAsCopyThenSetModelUri() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, false);

		verify(model).setSavedPictureUri(uri);
		verify(model).setSaved(true);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenSavedAsCopyThenShowCopyToast() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, true);

		verify(navigator).showToast(R.string.copy, Toast.LENGTH_LONG);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenSavedAsCopyThenDoNotTouchModel() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, true);

		verify(model, never()).setSavedPictureUri(any(Uri.class));
		verify(model, never()).setCameraImageUri(any(Uri.class));
		verify(model, never()).setSaved(anyBoolean());
		verify(model, never()).setOpenedFromCatroid(anyBoolean());
		verify(model, never()).setInitialAnimationPlayed(anyBoolean());
		verify(model, never()).setFullScreen(anyBoolean());
	}

	@Test
	public void testOnSaveImagePostExecuteWhenSavedThenBroadcastToPictureGallery() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, false);

		verify(navigator).broadcastAddPictureToGallery(uri);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenSavedAsCopyThenBroadcastToPictureGallery() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, true);

		verify(navigator).broadcastAddPictureToGallery(uri);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenSavedFromCatroidThenDoNotBroadcastToPictureGallery() {
		Uri uri = mock(Uri.class);
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, false);

		verify(navigator, never()).broadcastAddPictureToGallery(uri);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenSavedAsCopyFromCatroidThenBroadcastToPictureGallery() {
		Uri uri = mock(Uri.class);
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, true);

		verify(navigator).broadcastAddPictureToGallery(uri);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenChooseNewThenShowDialog() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_NEW_EMPTY, uri, false);

		verify(navigator).showChooseNewImageDialog();
	}

	@Test
	public void testOnSaveImagePostExecuteWhenDefaultThenDoNotShowDialog() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, false);

		verify(navigator, never()).startLoadImageActivity(anyInt());
		verify(navigator, never()).returnToPocketCode(anyString());
		verify(navigator, never()).finishActivity();
		verify(navigator, never()).showChooseNewImageDialog();
	}

	@Test
	public void testOnSaveImagePostExecuteWhenFinishThenFinishActivity() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, false);

		verify(navigator).finishActivity();
	}

	@Test
	public void testOnSaveImagePostExecuteWhenLoadThenStartActivity() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_LOAD_NEW, uri, false);

		verify(navigator).startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenExitToCatroidThenReturnToCatroid() {
		Uri uri = mock(Uri.class);
		when(uri.getPath()).thenReturn("testPath");

		presenter.onSaveImagePostExecute(SAVE_IMAGE_BACK_TO_PC, uri, false);

		verify(navigator).returnToPocketCode("testPath");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnSaveImagePostExecuteWhenInvalidRequestThenThrowException() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(0, uri, false);
	}

	@Test
	public void testGetContentResolver() {
		ContentResolver resolver = mock(ContentResolver.class);
		when(view.getContentResolver()).thenReturn(resolver);

		ContentResolver result = presenter.getContentResolver();

		assertEquals(resolver, result);
	}

	@Test
	public void testIsFinishing() {
		when(view.isFinishing()).thenReturn(true, false);

		assertTrue(presenter.isFinishing());
		assertFalse(presenter.isFinishing());
	}
}
