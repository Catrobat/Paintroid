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
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.util.DisplayMetrics;
import android.widget.Toast;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.controller.ToolController;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.iotasks.SaveImageAsync;
import org.catrobat.paintroid.presenter.MainActivityPresenter;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_INTRO;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LOAD_PICTURE;
import static org.catrobat.paintroid.common.MainActivityConstants.RESULT_INTRO_MW_NOT_SUPPORTED;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_DEFAULT;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_LOAD_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.SAVE_IMAGE_NEW_EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
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
	private Workspace workspace;
	@Mock
	private Perspective perspective;
	@Mock
	private ToolController toolController;
	@Mock
	private CommandFactory commandFactory;
	@Mock
	private CommandManager commandManager;
	@Mock
	private MainActivityContracts.BottomBarViewHolder bottomBarViewHolder;
	@Mock
	private MainActivityContracts.BottomNavigationViewHolder bottomNavigationViewHolder;
	@Mock
	private Bitmap bitmap;

	@InjectMocks
	private MainActivityPresenter presenter;

	@Before
	public void setUp() {
		when(workspace.getBitmapOfAllLayers())
				.thenReturn(bitmap);
	}

	@Test
	public void testSetUp() {
		verifyZeroInteractions(view, model, navigator, interactor, topBarViewHolder, workspace, perspective,
				drawerLayoutViewHolder, navigationDrawerViewHolder, commandFactory, commandManager, bottomBarViewHolder,
				bottomNavigationViewHolder, toolController);
	}

	@Test
	public void testNewImageClickedWhenUnchangedThenSetNewInitialState() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		when(view.getDisplayMetrics()).thenReturn(displayMetrics);
		displayMetrics.widthPixels = 300;
		displayMetrics.heightPixels = 500;
		Command command = mock(Command.class);
		when(commandFactory.createInitCommand(300, 500)).thenReturn(command);

		presenter.newImageClicked();

		verify(commandManager).setInitialStateCommand(command);
		verify(commandManager).reset();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testNewImageClickedWhenUndoAvailableAndSavedSetNewInitialState() {
		when(commandManager.isUndoAvailable()).thenReturn(true);
		when(model.isSaved()).thenReturn(true);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		when(view.getDisplayMetrics()).thenReturn(displayMetrics);
		displayMetrics.widthPixels = 200;
		displayMetrics.heightPixels = 100;
		Command command = mock(Command.class);
		when(commandFactory.createInitCommand(200, 100)).thenReturn(command);

		presenter.newImageClicked();

		verify(commandManager).setInitialStateCommand(command);
		verify(commandManager).reset();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testNewImageClickedWhenUndoAvailableAndNotSavedThenShowSaveBeforeNewImage() {
		when(commandManager.isUndoAvailable()).thenReturn(true);
		when(model.isSaved()).thenReturn(false);

		presenter.newImageClicked();

		verify(navigator).showSaveBeforeNewImageDialog();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testDiscardImageClickedThenClearsLayers() {
		Command command = mock(Command.class);
		when(commandFactory.createResetCommand()).thenReturn(command);

		presenter.discardImageClicked();

		verify(commandManager).addCommand(command);
		verifyNoMoreInteractions(commandManager, navigator);
	}

	@Test
	public void testBackToCatroidClickedWhenUnchangedThenFinishActivity() {
		presenter.backToPocketCodeClicked();

		verify(navigator).finishActivity();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testBackToCatroidClickedWhenUndoAvailableThenShowSaveDialog() {
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.backToPocketCodeClicked();

		verify(navigator).showSaveBeforeFinishDialog();
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
		when(model.isOpenedFromCatroid()).thenReturn(true);
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.backToPocketCodeClicked();

		verify(navigator).showSaveBeforeReturnToCatroidDialog();
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

		verify(navigator).showSaveBeforeLoadImageDialog();
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

		verify(interactor).saveCopy(presenter, SAVE_IMAGE_DEFAULT, bitmap);
		verifyNoMoreInteractions(interactor);
	}

	@Test
	public void testSaveImageClickedThenSaveImageWithUri() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.saveImageClicked();

		verify(interactor).saveImage(presenter, SAVE_IMAGE_DEFAULT, bitmap, uri);
		verifyNoMoreInteractions(interactor);
	}

	@Test
	public void testEnterFullscreenClicked() {
		presenter.enterFullscreenClicked();

		verify(model).setFullscreen(true);
		verify(topBarViewHolder).hide();
		verify(view).hideKeyboard();
		verify(view).enterFullscreen();
		verify(navigationDrawerViewHolder).hideEnterFullscreen();
		verify(navigationDrawerViewHolder).showExitFullscreen();
		verify(toolController).disableToolOptionsView();
		verify(perspective).enterFullscreen();
	}

	@Test
	public void testExitFullscreenClicked() {
		presenter.exitFullscreenClicked();

		verify(model).setFullscreen(false);
		verify(topBarViewHolder).show();
		verify(view).exitFullscreen();
		verify(navigationDrawerViewHolder).showEnterFullscreen();
		verify(navigationDrawerViewHolder).hideExitFullscreen();
		verify(toolController).enableToolOptionsView();
		verify(perspective).exitFullscreen();
	}

	@Test
	public void testShowHelpClickedThenStartWelcomeActivity() {
		presenter.showHelpClicked();

		verify(navigator).startWelcomeActivity(REQUEST_CODE_INTRO);
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

		presenter.onNewImage();
		presenter.onCommandPostExecute();

		verify(workspace).resetPerspective();
	}

	@Test
	public void testOnNewImageThenSetInitialStateCommand() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		when(view.getDisplayMetrics()).thenReturn(displayMetrics);
		displayMetrics.widthPixels = 300;
		displayMetrics.heightPixels = 500;
		Command command = mock(Command.class);
		when(commandFactory.createInitCommand(300, 500)).thenReturn(command);

		presenter.onNewImage();

		verify(commandManager).setInitialStateCommand(command);
	}

	@Test
	public void testOnNewImageThenResetSavedPictureUri() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);

		presenter.onNewImage();

		verify(model).setSavedPictureUri(null);
	}

	@Test
	public void testHandleActivityResultWhenUnhandledThenForwardResult() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(0, Activity.RESULT_OK, intent);

		verify(view).superHandleActivityResult(0, Activity.RESULT_OK, intent);
	}

	@Test
	public void testHandleActivityResultWhenUnhandledAndResultNotOKThenForwardResult() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(0, Activity.RESULT_CANCELED, intent);

		verify(view).superHandleActivityResult(0, Activity.RESULT_CANCELED, intent);
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
	public void testHandleActivityResultWhenResultNotOkThenDoNothing() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(0, Activity.RESULT_CANCELED, intent);

		verifyZeroInteractions(interactor, navigator);
	}

	@Test
	public void testHandleActivityResultWhenRequestIntroAndResultOKThenDoNothing() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(REQUEST_CODE_INTRO, Activity.RESULT_OK, intent);

		verifyZeroInteractions(interactor, navigator);
	}

	@Test
	public void testHandleActivityResultWhenRequestIntroAndResultSplitScreenNotSupportedThenShowToast() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(REQUEST_CODE_INTRO, RESULT_INTRO_MW_NOT_SUPPORTED, intent);

		verify(navigator).showToast(R.string.pocketpaint_intro_split_screen_not_supported, Toast.LENGTH_LONG);
		verifyZeroInteractions(interactor, navigator);
	}

	@Test
	public void testOnBackPressedWhenUntouchedThenFinishActivity() {
		when(toolController.isDefaultTool()).thenReturn(true);

		presenter.onBackPressed();

		verify(navigator).finishActivity();
	}

	@Test
	public void testOnBackPressedWhenStartDrawerOpenThenCloseDrawer() {
		when(drawerLayoutViewHolder.isDrawerOpen(GravityCompat.START)).thenReturn(true);

		presenter.onBackPressed();

		verify(drawerLayoutViewHolder).closeDrawer(GravityCompat.START, true);
	}

	@Test
	public void testOnBackPressedWhenEndDrawerOpenThenCloseDrawer() {
		when(drawerLayoutViewHolder.isDrawerOpen(GravityCompat.END)).thenReturn(true);

		presenter.onBackPressed();

		verify(drawerLayoutViewHolder).closeDrawer(GravityCompat.END, true);
	}

	@Test
	public void testOnBackPressedWhenIsFullscreenThenExitFullscreen() {
		when(model.isFullscreen()).thenReturn(true);

		presenter.onBackPressed();

		verify(model).setFullscreen(false);
	}

	@Test
	public void testOnBackPressedWhenToolOptionsShownThenHideToolOptions() {
		when(toolController.toolOptionsViewVisible()).thenReturn(true);

		presenter.onBackPressed();

		verify(toolController).hideToolOptionsView();
	}

	@Test
	public void testSaveImageConfirmClickedThenSaveImage() {
		Uri uri = mock(Uri.class);

		presenter.saveImageConfirmClicked(0, uri);

		verify(interactor).saveImage(presenter, 0, bitmap, uri);
	}

	@Test
	public void testSaveImageConfirmClickedThenUseRequestCode() {
		Uri uri = mock(Uri.class);

		presenter.saveImageConfirmClicked(-1, uri);

		verify(interactor).saveImage(presenter, -1, bitmap, uri);
	}

	@Test
	public void testUndoClickedWhenKeyboardOpenedThenCloseKeyboard() {
		when(view.isKeyboardShown()).thenReturn(true);

		presenter.undoClicked();

		verify(view).hideKeyboard();
		verifyZeroInteractions(commandManager);
	}

	@Test
	public void testUndoClickedThenExecuteUndo() {
		presenter.undoClicked();

		verify(commandManager).undo();
	}

	@Test
	public void testRedoClickedWhenKeyboardOpenedThenCloseKeyboard() {
		when(view.isKeyboardShown()).thenReturn(true);

		presenter.redoClicked();

		verify(view).hideKeyboard();
		verifyZeroInteractions(commandManager);
	}

	@Test
	public void testRedoClickedThenExecuteRedo() {
		presenter.redoClicked();

		verify(commandManager).redo();
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
		presenter.onCommandPostExecute();

		verify(model).setSaved(false);
	}

	@Test
	public void testOnCommandPostExecuteThenResetInternalToolState() {
		presenter.onCommandPostExecute();

		verify(toolController).resetToolInternalState();
	}

	@Test
	public void testOnCommandPostExecuteThenRefreshDrawingSurface() {
		presenter.onCommandPostExecute();

		verify(view).refreshDrawingSurface();
	}

	@Test
	public void testOnCommandPostExecuteThenSetUndoRedoButtons() {
		presenter.onCommandPostExecute();

		verify(topBarViewHolder).disableRedoButton();
		verify(topBarViewHolder).disableUndoButton();
	}

	@Test
	public void testOnCommandPostExecuteThenDismissDialog() {
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
		presenter.initializeFromCleanState(null, null);

		verify(model).setOpenedFromCatroid(false);
		verify(model).setSavedPictureUri(null);
	}

	@Test
	public void testInitializeFromCleanStateWhenDefaultThenResetTool() {
		presenter.initializeFromCleanState(null, null);

		verify(toolController).resetToolInternalStateOnImageLoaded();
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
		presenter.restoreState(false, false, false, false, null, null);

		verify(navigator).restoreFragmentListeners();
	}

	@Test
	public void testRestoreStateThenSetModel() {
		Uri savedPictureUri = mock(Uri.class);
		Uri cameraImageUri = mock(Uri.class);

		presenter.restoreState(false, false, false, false, savedPictureUri, cameraImageUri);

		verify(model).setFullscreen(false);
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

		presenter.restoreState(true, true, true, true, savedPictureUri, cameraImageUri);

		verify(model).setFullscreen(true);
		verify(model).setSaved(true);
		verify(model).setOpenedFromCatroid(true);
		verify(model).setInitialAnimationPlayed(true);
		verify(model).setSavedPictureUri(savedPictureUri);
		verify(model).setCameraImageUri(cameraImageUri);
	}

	@Test
	public void testRestoreStateThenResetTool() {
		presenter.restoreState(false, false, false, false, null, null);

		verify(toolController).resetToolInternalStateOnImageLoaded();
	}

	@Test
	public void testOnCreateToolCallsToolController() {
		presenter.onCreateTool();

		verify(toolController).createTool();
		verifyNoMoreInteractions(toolController);
	}

	@Test
	public void testFinishInitializeThenSetUndoRedoButtons() {
		presenter.finishInitialize();

		verify(topBarViewHolder).disableUndoButton();
		verify(topBarViewHolder).disableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenUndoAvailableThenSetUndoRedoButtons() {
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.finishInitialize();

		verify(topBarViewHolder).enableUndoButton();
		verify(topBarViewHolder).disableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenRedoAvailableThensetUndoRedoButtons() {
		when(commandManager.isRedoAvailable()).thenReturn(true);

		presenter.finishInitialize();

		verify(topBarViewHolder).disableUndoButton();
		verify(topBarViewHolder).enableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenNotFullscreenThenRestoreState() {
		presenter.finishInitialize();

		verify(view).exitFullscreen();
	}

	@Test
	public void testFinishInitializeWhenFullscreenThenRestoreState() {
		when(model.isFullscreen()).thenReturn(true);

		presenter.finishInitialize();

		verify(view).enterFullscreen();
	}

	@Test
	public void testFinishInitializeThenRestoreColorButtonColor() {
		when(model.isFullscreen()).thenReturn(true);
		when(toolController.getToolColor()).thenReturn(Color.RED);

		presenter.finishInitialize();

		verify(topBarViewHolder).setColorButtonColor(Color.RED);
	}

	@Test
	public void testFinishInitializeWhenDefaultThenInitializeActionBarDefault() {
		presenter.finishInitialize();

		verify(view).initializeActionBar(false);
	}

	@Test
	public void testFinishInitializeWhenFromCatroidThenInitializeActionBarCatroid() {
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.finishInitialize();

		verify(view).initializeActionBar(true);
	}

	@Test
	public void testFinishInitializeWhenDefaultThenRemoveCatroidNavigationItems() {
		presenter.finishInitialize();

		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_export);
		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_back_to_pocket_code);
	}

	@Test
	public void testFinishInitializeWhenFromCatroidThenRemoveSaveNavigationItems() {
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.finishInitialize();

		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_save_image);
		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_save_duplicate);
	}

	@Test
	public void testToolClickedWhenSameToolTypeThenToggleOptions() {
		when(toolController.getToolType()).thenReturn(ToolType.TEXT);
		when(toolController.hasToolOptionsView()).thenReturn(true);

		presenter.toolClicked(ToolType.TEXT);

		verify(toolController).toggleToolOptionsView();
	}

	@Test
	public void testToolClickedWhenKeyboardShownThenHideKeyboard() {
		when(view.isKeyboardShown()).thenReturn(true);

		presenter.toolClicked(ToolType.ERASER);

		verify(view).hideKeyboard();
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
	public void testOnLoadImagePreExecuteDoesNothing() {
		presenter.onLoadImagePreExecute(LOAD_IMAGE_DEFAULT);

		verifyZeroInteractions(view, model, navigator, interactor, topBarViewHolder, workspace, perspective,
				drawerLayoutViewHolder, navigationDrawerViewHolder, commandFactory, commandManager, bottomBarViewHolder,
				toolController);
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
		Command command = mock(Command.class);
		when(commandFactory.createInitCommand(bitmap)).thenReturn(command);

		presenter.onLoadImagePostExecute(LOAD_IMAGE_DEFAULT, uri, bitmap);

		verify(commandManager).setInitialStateCommand(command);
		verify(commandManager).reset();
		verifyNoMoreInteractions(commandManager);
	}

	@Test
	public void testOnLoadImagePostExecuteWhenImportThenSetBitmap() {
		Uri uri = mock(Uri.class);
		Bitmap bitmap = mock(Bitmap.class);
		when(toolController.getToolType()).thenReturn(ToolType.IMPORTPNG);

		presenter.onLoadImagePostExecute(LOAD_IMAGE_IMPORTPNG, uri, bitmap);

		verify(toolController).setBitmapFromFile(bitmap);
		verifyZeroInteractions(commandManager);
	}

	@Test
	public void testOnLoadImagePostExecuteWhenImportAndNotImportToolSetThenIgnore() {
		Uri uri = mock(Uri.class);
		Bitmap bitmap = mock(Bitmap.class);

		presenter.onLoadImagePostExecute(LOAD_IMAGE_IMPORTPNG, uri, bitmap);

		verify(toolController, never()).setBitmapFromFile(any(Bitmap.class));
		verifyZeroInteractions(commandManager);
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
		Command command = mock(Command.class);
		when(commandFactory.createInitCommand(bitmap)).thenReturn(command);

		presenter.onLoadImagePostExecute(LOAD_IMAGE_CATROID, uri, bitmap);

		verify(commandManager).setInitialStateCommand(command);
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
	public void testHandlePermissionResultSavePermissionGranted() {
		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		Uri uri = model.getSavedPictureUri();

		verify(interactor).saveImage(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_DEFAULT), eq(bitmap), eq(uri));
	}

	@Test
	public void testHandlePermissionResultSavePermissionNotGranted() {
		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE
		);
	}

	@Test
	public void testHandlePermissionResultSaveCopyPermissionGranted() {
		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		verify(interactor).saveCopy(any(SaveImageAsync.SaveImageCallback.class), eq(SAVE_IMAGE_DEFAULT), eq(bitmap));
	}

	@Test
	public void testHandlePermissionResultSaveCopyPermissionNotGranted() {
		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_COPY
		);
	}

	@Test
	public void testHandlePermissionResultSaveBeforeFinishPermissionGranted() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		verify(interactor).saveImage(presenter, SAVE_IMAGE_FINISH, bitmap, uri);
	}

	@Test
	public void testHandlePermissionResultSaveBeforeFinishPermissionNotGranted() {
		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH
		);
	}

	@Test
	public void testHandlePermissionResultSaveBeforeLoadNewPermissionNotGranted() {
		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW
		);
	}

	@Test
	public void testHandlePermissionResultSaveBeforeLoadNewPermissionGranted() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_GRANTED});

		verify(interactor).saveImage(presenter, SAVE_IMAGE_LOAD_NEW, bitmap, uri);
	}

	@Test
	public void testHandlePermissionResultSaveBeforeNewEmptyPermissionNotGranted() {
		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[] {PackageManager.PERMISSION_DENIED});

		verify(navigator).showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY
		);
	}

	@Test
	public void testHandlePermissionResultSaveBeforeNewEmptyPermissionGranted() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.handleRequestPermissionsResult(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[]{PackageManager.PERMISSION_GRANTED});

		verify(interactor).saveImage(presenter, SAVE_IMAGE_NEW_EMPTY, bitmap, uri);
	}

	@Test
	public void testHandlePermissionResultWhenStoragePermissionGrantedAndRequestCodeUnknownThenCallBaseHandle() {
		presenter.handleRequestPermissionsResult(100,
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[]{PackageManager.PERMISSION_GRANTED});

		verify(view).superHandleRequestPermissionsResult(100,
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
				new int[]{PackageManager.PERMISSION_GRANTED});
	}

	@Test
	public void testHandlePermissionResultWhenCameraPermissionGrantedAndRequestCodeUnknownThenCallBaseHandle() {
		presenter.handleRequestPermissionsResult(123,
				new String[]{Manifest.permission.CAMERA},
				new int[]{PackageManager.PERMISSION_GRANTED});

		verify(view).superHandleRequestPermissionsResult(123,
				new String[]{Manifest.permission.CAMERA},
				new int[]{PackageManager.PERMISSION_GRANTED});
	}

	@Test
	public void testHandlePermissionResultWhenMultiplePermissionsThenCallBaseHandle() {
		presenter.handleRequestPermissionsResult(456,
				new String[]{Manifest.permission.CAMERA, Manifest.permission.CAMERA},
				new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_DENIED});

		verify(view).superHandleRequestPermissionsResult(456,
				new String[]{Manifest.permission.CAMERA, Manifest.permission.CAMERA},
				new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_DENIED});
	}

	@Test
	public void testOnNavigationItemSelectedSaveCopyPermissionGranted() {
		presenter.saveCopyClicked();

		verify(interactor).saveCopy(presenter, SAVE_IMAGE_DEFAULT, bitmap);
	}

	@Test
	public void testOnNavigationItemSelectedSaveCopyPermissionNotGranted() {
		when(navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(false);
		when(navigator.isSdkAboveOrEqualM()).thenReturn(true);

		presenter.saveCopyClicked();

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_COPY);
	}

	@Test
	public void testOnNavigationItemSelectedSavePermissionGranted() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.saveImageClicked();

		verify(interactor).saveImage(presenter, SAVE_IMAGE_DEFAULT, bitmap, uri);
	}

	@Test
	public void testOnNavigationItemSelectedSavePermissionNotGranted() {
		when(navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(false);
		when(navigator.isSdkAboveOrEqualM()).thenReturn(true);

		presenter.saveImageClicked();

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE);
	}

	@Test
	public void testSaveAndFinishPermissionGranted() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.saveBeforeFinish();

		verify(interactor).saveImage(presenter, SAVE_IMAGE_FINISH, bitmap, uri);
	}

	@Test
	public void testSaveAndFinishPermissionNotGranted() {
		when(navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(false);
		when(navigator.isSdkAboveOrEqualM()).thenReturn(true);

		presenter.saveBeforeFinish();

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH);
	}

	@Test
	public void testSaveAndNewImagePermissionGranted() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.saveBeforeNewImage();

		verify(interactor).saveImage(presenter, SAVE_IMAGE_NEW_EMPTY, bitmap, uri);
	}

	@Test
	public void testSaveAndNewImagePermissionNotGranted() {
		when(navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(false);
		when(navigator.isSdkAboveOrEqualM()).thenReturn(true);

		presenter.saveBeforeNewImage();

		verify(navigator).askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY);
	}

	@Test
	public void testSaveAndLoadImagePermissionGranted() {
		Uri uri = mock(Uri.class);
		when(model.getSavedPictureUri()).thenReturn(uri);

		presenter.saveBeforeLoadImage();

		verify(interactor).saveImage(presenter, SAVE_IMAGE_LOAD_NEW, bitmap, uri);
	}

	@Test
	public void testSaveAndLoadImagePermissionNotGranted() {
		when(navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(false);
		when(navigator.isSdkAboveOrEqualM()).thenReturn(true);

		presenter.saveBeforeLoadImage();

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
		verify(model, never()).setFullscreen(anyBoolean());
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
	public void testOnSaveImagePostExecuteWhenChooseNewThenSetNewInitialState() {
		Uri uri = mock(Uri.class);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		when(view.getDisplayMetrics()).thenReturn(displayMetrics);
		displayMetrics.widthPixels = 300;
		displayMetrics.heightPixels = 500;
		Command command = mock(Command.class);
		when(commandFactory.createInitCommand(300, 500)).thenReturn(command);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_NEW_EMPTY, uri, false);

		verify(commandManager).setInitialStateCommand(command);
		verify(commandManager).reset();
	}

	@Test
	public void testOnSaveImagePostExecuteWhenDefaultThenDoNotShowDialog() {
		Uri uri = mock(Uri.class);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, false);

		verify(navigator, never()).startLoadImageActivity(anyInt());
		verify(navigator, never()).returnToPocketCode(anyString());
		verify(navigator, never()).finishActivity();
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
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, false);

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
