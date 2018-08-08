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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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
import org.catrobat.paintroid.presenter.MainActivityPresenter;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.catrobat.paintroid.common.MainActivityConstants.CREATE_FILE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.CREATE_FILE_TAKE_PHOTO;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_CATROID;
import static org.catrobat.paintroid.common.MainActivityConstants.LOAD_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LANGUAGE;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LOAD_PICTURE;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_TAKE_PICTURE;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_CHOOSE_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_EXIT_CATROID;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_LOAD_NEW;
import static org.catrobat.paintroid.tools.Tool.StateChange.NEW_IMAGE_LOADED;
import static org.catrobat.paintroid.tools.Tool.StateChange.RESET_INTERNAL_STATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
	public void testLoadImageClickedWhenUnchangedThenStartLoadActivity() {
		presenter.loadImageClicked();

		verify(navigator).startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testLoadImageClickedWhenUndoAvailableThenShowSaveDialog() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.loadImageClicked();

		verify(navigator).showSaveBeforeLoadImageDialog(SAVE_IMAGE_LOAD_NEW, uri);
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testLoadImageClickedWhenUndoAvailableAndSavedThenStartLoadActivity() {
		when(commandManager.isUndoAvailable()).thenReturn(true);
		when(model.isSaved()).thenReturn(true);

		presenter.loadImageClicked();

		verify(navigator).startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testLoadNewImageThenStartLoadActivity() {
		presenter.loadNewImage();

		verify(navigator).startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testNewImageClickedWhenUnchangedThenShowNewImageDialog() {
		presenter.newImageClicked();

		verify(navigator).showChooseNewImageDialog();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testNewImageClickedWhenUnchangedAndOpenedFromCatroidThenShowSaveDialog() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.newImageClicked();

		verify(navigator).showSaveBeforeNewImageDialog(SAVE_IMAGE_CHOOSE_NEW, uri);
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testNewImageClickedWhenUndoAvailableThenShowSaveDialog() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.newImageClicked();

		verify(navigator).showSaveBeforeNewImageDialog(SAVE_IMAGE_CHOOSE_NEW, uri);
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
	public void testBackToCatroidClickedWhenUndoAvailableThenShowSaveDialog() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.backToPocketCodeClicked();

		verify(navigator).showSaveBeforeFinishDialog(SAVE_IMAGE_FINISH, uri);
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

		verify(navigator).showSaveBeforeReturnToCatroidDialog(SAVE_IMAGE_EXIT_CATROID, uri);
		verifyNoMoreInteractions(navigator);
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
	public void testShowTermsOfServiceClickedThenShowTosDialog() {
		presenter.showTermsOfServiceClicked();

		verify(navigator).showTermsOfServiceDialog();
		verifyNoMoreInteractions(navigator);
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
	public void testSelectLanguageClickedThenStartLanguageActivity() {
		presenter.selectLanguageClicked();

		verify(navigator).startLanguageActivity(REQUEST_CODE_LANGUAGE);
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
		PaintroidApplication.perspective = mock(Perspective.class);

		presenter.onNewImage();

		verify(commandManager).setInitialStateCommand(any(Command.class));
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
	public void testToolLongClickedThenShowInfoDialog() {
		presenter.toolLongClicked(ToolType.FILL);

		verify(navigator).showToolInfoDialog(ToolType.FILL);
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
		Uri uri = mock(Uri.class);

		presenter.onCreateFilePostExecute(CREATE_FILE_DEFAULT, uri);

		verify(model).setSavedPictureUri(uri);
		verifyZeroInteractions(navigator);
	}

	@Test
	public void testOnCreateFilePostExecuteWhenTakePictureThenStartActivity() {
		Uri uri = mock(Uri.class);

		presenter.onCreateFilePostExecute(CREATE_FILE_TAKE_PHOTO, uri);

		verify(model).setCameraImageUri(uri);
		verify(navigator).startTakePictureActivity(REQUEST_CODE_TAKE_PICTURE, uri);
		verifyNoMoreInteractions(navigator);
		verify(model, never()).setSavedPictureUri(any(Uri.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnCreateFilePostExecuteWhenInvalidRequestThenThrowException() {
		Uri uri = mock(Uri.class);

		presenter.onCreateFilePostExecute(0, uri);
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

		verifyZeroInteractions(model);
	}

	@Test
	public void testOnSaveImagePostExecuteWhenChooseNewThenShowDialog() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_CHOOSE_NEW, uri, false);

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

		presenter.onSaveImagePostExecute(SAVE_IMAGE_EXIT_CATROID, uri, false);

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
