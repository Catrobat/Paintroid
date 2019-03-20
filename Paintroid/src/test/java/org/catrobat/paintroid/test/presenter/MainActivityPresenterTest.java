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

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.iotasks.SaveImageAsync;
import org.catrobat.paintroid.presenter.MainActivityPresenter;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolReference;
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
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY;
import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_FINISH;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LANGUAGE;
import static org.catrobat.paintroid.common.MainActivityConstants.REQUEST_CODE_LOAD_PICTURE;
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
	private ToolReference toolReference;

	@Mock
	private Tool tool;

	@Mock
	private CommandManager commandManager;

	@Mock
	private MainActivityContracts.BottomBarViewHolder bottomBarViewHolder;

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
				drawerLayoutViewHolder, navigationDrawerViewHolder, commandManager, bottomBarViewHolder,
				toolReference, tool);
	}

	@Test
	public void testNewImageClickedWhenUnchangedThenSetNewInitialState() {
		when(view.getDisplayMetrics()).thenReturn(new DisplayMetrics());

		presenter.newImageClicked();

		verify(commandManager).setInitialStateCommand(any(Command.class));
		verify(commandManager).reset();
		verifyNoMoreInteractions(navigator);
	}

	@Test
	public void testNewImageClickedWhenUndoAvailableAndSavedSetNewInitialState() {
		when(commandManager.isUndoAvailable()).thenReturn(true);
		when(model.isSaved()).thenReturn(true);
		when(view.getDisplayMetrics()).thenReturn(new DisplayMetrics());

		presenter.newImageClicked();

		verify(commandManager).setInitialStateCommand(any(Command.class));
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
		presenter.discardImageClicked();

		verify(commandManager).addCommand(any(Command.class));
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
		when(toolReference.get()).thenReturn(tool);

		presenter.enterFullscreenClicked();

		verify(model).setFullscreen(true);
		verify(topBarViewHolder).hide();
		verify(view).enterFullscreen();
		verify(navigationDrawerViewHolder).hideEnterFullscreen();
		verify(navigationDrawerViewHolder).showExitFullscreen();
		verify(tool).hide();
		verify(perspective).enterFullscreen();
	}

	@Test
	public void testExitFullscreenClicked() {
		when(toolReference.get()).thenReturn(tool);

		presenter.exitFullscreenClicked();

		verify(model).setFullscreen(false);
		verify(topBarViewHolder).show();
		verify(view).exitFullscreen();
		verify(navigationDrawerViewHolder).showEnterFullscreen();
		verify(navigationDrawerViewHolder).hideExitFullscreen();
		verify(perspective).exitFullscreen();
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
		when(toolReference.get()).thenReturn(tool);

		presenter.onNewImage();
		presenter.onCommandPostExecute();

		verify(workspace).resetPerspective();
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
	public void testHandleActivityResultWhenUnhandledThenForwardResult() {
		DisplayMetrics metrics = mock(DisplayMetrics.class);
		when(view.getDisplayMetrics()).thenReturn(metrics);
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(0, Activity.RESULT_OK, intent);

		verify(view).superHandleActivityResult(0, Activity.RESULT_OK, intent);
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
	public void testHandleActivityResultWhenResultNotOkThenDoNothing() {
		Intent intent = mock(Intent.class);

		presenter.handleActivityResult(0, Activity.RESULT_CANCELED, intent);

		verifyZeroInteractions(view, interactor, navigator);
	}

	@Test
	public void testOnBackPressedWhenUntouchedThenFinishActivity() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.BRUSH);

		presenter.onBackPressed();

		verify(navigator).finishActivity();
	}

	@Test
	public void testOnBackPressedWhenStartDrawerOpenThenCloseDrawer() {
		when(toolReference.get()).thenReturn(tool);
		when(drawerLayoutViewHolder.isDrawerOpen(GravityCompat.START)).thenReturn(true);

		presenter.onBackPressed();

		verify(drawerLayoutViewHolder).closeDrawer(GravityCompat.START, true);
	}

	@Test
	public void testOnBackPressedWhenEndDrawerOpenThenCloseDrawer() {
		when(toolReference.get()).thenReturn(tool);
		when(drawerLayoutViewHolder.isDrawerOpen(GravityCompat.END)).thenReturn(true);

		presenter.onBackPressed();

		verify(drawerLayoutViewHolder).closeDrawer(GravityCompat.END, true);
	}

	@Test
	public void testOnBackPressedWhenIsFullscreenThenExitFullscreen() {
		when(toolReference.get()).thenReturn(tool);
		when(model.isFullscreen()).thenReturn(true);

		presenter.onBackPressed();

		verify(model).setFullscreen(false);
	}

	@Test
	public void testOnBackPressedWhenToolOptionsShownThenHideToolOptions() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolOptionsAreShown()).thenReturn(true);

		presenter.onBackPressed();

		verify(tool).toggleShowToolOptions();
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
	public void testUndoClickedThenExecuteUndo() {
		when(toolReference.get()).thenReturn(tool);

		presenter.undoClicked();

		verify(commandManager).undo();
	}

	@Test
	public void testRedoClickedThenExecuteRedo() {
		when(toolReference.get()).thenReturn(tool);

		presenter.redoClicked();

		verify(commandManager).redo();
	}

	@Test
	public void testShowColorPickerClickedWhenColorChangeAllowedThenShowColorPickerDialog() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.BRUSH);

		presenter.showColorPickerClicked();

		verify(navigator).showColorPickerDialog();
	}

	@Test
	public void testShowColorPickerClickedWhenNoColorChangeAllowedThenIgnore() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.PIPETTE);

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
		when(toolReference.get()).thenReturn(tool);

		presenter.onCommandPostExecute();

		verify(model).setSaved(false);
	}

	@Test
	public void testOnCommandPostExecuteThenResetInternalToolState() {
		when(toolReference.get()).thenReturn(tool);

		presenter.onCommandPostExecute();

		verify(tool).resetInternalState(RESET_INTERNAL_STATE);
	}

	@Test
	public void testOnCommandPostExecuteThenRefreshDrawingSurface() {
		when(toolReference.get()).thenReturn(tool);

		presenter.onCommandPostExecute();

		verify(view).refreshDrawingSurface();
	}

	@Test
	public void testOnCommandPostExecuteThenSetUndoRedoButtons() {
		when(toolReference.get()).thenReturn(tool);

		presenter.onCommandPostExecute();

		verify(topBarViewHolder).disableRedoButton();
		verify(topBarViewHolder).disableUndoButton();
	}

	@Test
	public void testOnCommandPostExecuteThenDismissDialog() {
		when(toolReference.get()).thenReturn(tool);

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
		when(toolReference.get()).thenReturn(tool);

		presenter.initializeFromCleanState(null, null);

		verify(model).setOpenedFromCatroid(false);
		verify(model).setSavedPictureUri(null);
	}

	@Test
	public void testInitializeFromCleanStateWhenDefaultThenResetTool() {
		when(toolReference.get()).thenReturn(tool);

		presenter.initializeFromCleanState(null, null);

		verify(tool).resetInternalState(NEW_IMAGE_LOADED);
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
		when(toolReference.get()).thenReturn(tool);

		presenter.restoreState(false, false, false, false, null, null);

		verify(navigator).restoreFragmentListeners();
	}

	@Test
	public void testRestoreStateThenSetModel() {
		Uri savedPictureUri = mock(Uri.class);
		Uri cameraImageUri = mock(Uri.class);
		when(toolReference.get()).thenReturn(tool);

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
		when(toolReference.get()).thenReturn(tool);

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
		when(toolReference.get()).thenReturn(tool);

		presenter.restoreState(false, false, false, false, null, null);

		verify(tool).resetInternalState(NEW_IMAGE_LOADED);
	}

	@Test
	public void testFinishInitializeThensetUndoRedoButtons() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));

		presenter.finishInitialize();

		verify(topBarViewHolder).disableUndoButton();
		verify(topBarViewHolder).disableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenUndoAvailableThensetUndoRedoButtons() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(commandManager.isUndoAvailable()).thenReturn(true);

		presenter.finishInitialize();

		verify(topBarViewHolder).enableUndoButton();
		verify(topBarViewHolder).disableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenRedoAvailableThensetUndoRedoButtons() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(commandManager.isRedoAvailable()).thenReturn(true);

		presenter.finishInitialize();

		verify(topBarViewHolder).disableUndoButton();
		verify(topBarViewHolder).enableRedoButton();
	}

	@Test
	public void testFinishInitializeWhenNotFullscreenThenRestoreState() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));

		presenter.finishInitialize();

		verify(view).exitFullscreen();
	}

	@Test
	public void testFinishInitializeWhenFullscreenThenRestoreState() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(model.isFullscreen()).thenReturn(true);

		presenter.finishInitialize();

		verify(view).enterFullscreen();
	}

	@Test
	public void testFinishInitializeThenRestoreColorButtonColor() {
		Paint paint = mock(Paint.class);
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(paint);
		when(model.isFullscreen()).thenReturn(true);
		when(paint.getColor()).thenReturn(Color.RED);

		presenter.finishInitialize();

		verify(topBarViewHolder).setColorButtonColor(Color.RED);
	}

	@Test
	public void testFinishInitializeThenRestoreSelectedTool() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(tool.getToolType()).thenReturn(ToolType.TEXT);

		presenter.finishInitialize();

		verify(bottomBarViewHolder).selectToolButton(ToolType.TEXT);
	}

	@Test
	public void testFinishInitializeWhenDefaultThenInitializeActionBarDefault() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));

		presenter.finishInitialize();

		verify(view).initializeActionBar(false);
	}

	@Test
	public void testFinishInitializeWhenFromCatroidThenInitializeActionBarCatroid() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.finishInitialize();

		verify(view).initializeActionBar(true);
	}

	@Test
	public void testFinishInitializeWhenDefaultThenRemoveCatroidNavigationItems() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));

		presenter.finishInitialize();

		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_export);
		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_back_to_pocket_code);
	}

	@Test
	public void testFinishInitializeWhenFromCatroidThenRemoveSaveNavigationItems() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(mock(Paint.class));
		when(model.isOpenedFromCatroid()).thenReturn(true);

		presenter.finishInitialize();

		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_save_image);
		verify(navigationDrawerViewHolder).removeItem(R.id.pocketpaint_nav_save_duplicate);
	}

	@Test
	public void testToolClickedThenCancelAnimation() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.BRUSH);

		presenter.toolClicked(ToolType.BRUSH);

		verify(bottomBarViewHolder).cancelAnimation();
	}

	@Test
	public void testToolClickedWhenSameToolTypeThenToggleOptions() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.BRUSH);

		presenter.toolClicked(ToolType.BRUSH);

		verify(tool).toggleShowToolOptions();
	}

	@Test
	public void testToolClickedWhenKeyboardShownThenHideKeyboard() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.BRUSH);
		when(view.isKeyboardShown()).thenReturn(true);

		presenter.toolClicked(ToolType.ERASER);

		verify(view).hideKeyboard();
	}

	@Test
	public void testGotFocusThenPlayInitialAnimation() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.PIPETTE);

		presenter.gotFocus();

		verify(bottomBarViewHolder).startAnimation(ToolType.PIPETTE);
		verify(model).setInitialAnimationPlayed(true);
	}

	@Test
	public void testGotFocusWhenAlreadyPlayedThenScrollToTool() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.ERASER);
		when(model.wasInitialAnimationPlayed()).thenReturn(true);

		presenter.gotFocus();

		verify(bottomBarViewHolder).scrollToButton(ToolType.ERASER, false);
		verify(model, never()).setInitialAnimationPlayed(anyBoolean());
	}

	@Test
	public void testGotFocusWhenGotFocusBeforeThenDoNothing() {
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.LINE);

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
		when(view.getDisplayMetrics()).thenReturn(new DisplayMetrics());

		presenter.onSaveImagePostExecute(SAVE_IMAGE_NEW_EMPTY, uri, false);

		verify(commandManager).setInitialStateCommand(any(Command.class));
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
