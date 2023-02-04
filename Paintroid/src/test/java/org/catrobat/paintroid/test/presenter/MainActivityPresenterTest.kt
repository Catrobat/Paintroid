/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.test.presenter

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.DisplayMetrics
import android.view.Menu
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.nhaarman.mockitokotlin2.any
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.UserPreferences
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.serialization.CommandSerializer
import org.catrobat.paintroid.common.CREATE_FILE_DEFAULT
import org.catrobat.paintroid.common.LOAD_IMAGE_CATROID
import org.catrobat.paintroid.common.LOAD_IMAGE_DEFAULT
import org.catrobat.paintroid.common.LOAD_IMAGE_IMPORT_PNG
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY
import org.catrobat.paintroid.common.PERMISSION_REQUEST_CODE_REPLACE_PICTURE
import org.catrobat.paintroid.common.REQUEST_CODE_INTRO
import org.catrobat.paintroid.common.REQUEST_CODE_LOAD_PICTURE
import org.catrobat.paintroid.common.RESULT_INTRO_MW_NOT_SUPPORTED
import org.catrobat.paintroid.common.SAVE_IMAGE_DEFAULT
import org.catrobat.paintroid.common.SAVE_IMAGE_FINISH
import org.catrobat.paintroid.common.SAVE_IMAGE_LOAD_NEW
import org.catrobat.paintroid.common.SAVE_IMAGE_NEW_EMPTY
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.contract.MainActivityContracts.Interactor
import org.catrobat.paintroid.contract.MainActivityContracts.MainView
import org.catrobat.paintroid.controller.ToolController
import org.catrobat.paintroid.dialog.PermissionInfoDialog
import org.catrobat.paintroid.iotasks.BitmapReturnValue
import org.catrobat.paintroid.iotasks.SaveImage.SaveImageCallback
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.presenter.MainActivityPresenter
import org.catrobat.paintroid.presenter.MainActivityPresenter.Companion.getPathFromUri
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.ui.Perspective
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner.Silent::class)
class MainActivityPresenterTest {
    @Mock
    private val view: MainView? = null

    @Mock
    private val model: MainActivityContracts.Model? = null

    @Mock
    private val navigator: MainActivityContracts.Navigator? = null

    @Mock
    private val interactor: Interactor? = null

    @Mock
    private val topBarViewHolder: MainActivityContracts.TopBarViewHolder? = null

    @Mock
    private val drawerLayoutViewHolder: MainActivityContracts.DrawerLayoutViewHolder? = null

    @Mock
    private val workspace: Workspace? = null

    @Mock
    private val commandSerializer: CommandSerializer? = null

    @Mock
    private val perspective: Perspective? = null

    @Mock
    private val toolController: ToolController? = null

    @Mock
    private val commandFactory: CommandFactory? = null

    @Mock
    private val commandManager: CommandManager? = null

    @Mock
    private val bottomBarViewHolder: MainActivityContracts.BottomBarViewHolder? = null

    @Mock
    private val bottomNavigationViewHolder: MainActivityContracts.BottomNavigationViewHolder? = null

    @Mock
    private val bitmap: Bitmap? = null

    @Mock
    private val menu: Menu? = null

    @Mock
    private val sharedPreferences: UserPreferences? = null

    @Mock
    private val context: Context? = null

    @Mock
    private val internalMemoryPath: File? = null
    private var presenter: MainActivityPresenter? = null
    @Before
    fun setUp() {
        val layerModel = LayerModel()
        layerModel.addLayerAt(0, Layer(bitmap!!))
        Mockito.`when`(workspace!!.layerModel)
            .thenReturn(layerModel)
        val activity = MainActivity()
        val idlingResource = activity.idlingResource
        presenter = MainActivityPresenter(
            activity,
            view!!,
            model!!,
            workspace,
            navigator!!,
            interactor!!,
            topBarViewHolder!!,
            bottomBarViewHolder!!,
            drawerLayoutViewHolder!!,
            bottomNavigationViewHolder!!,
            commandFactory!!,
            commandManager!!,
            perspective!!,
            toolController!!,
            sharedPreferences!!, idlingResource,
            context!!, internalMemoryPath!!, commandSerializer!!
        )
    }

    @Test
    fun testSetUp() {
        Mockito.verifyZeroInteractions(
            view,
            model,
            navigator,
            interactor,
            topBarViewHolder,
            workspace,
            perspective,
            drawerLayoutViewHolder,
            commandFactory,
            commandManager,
            bottomBarViewHolder,
            bottomNavigationViewHolder,
            toolController,
            sharedPreferences,
            internalMemoryPath,
            commandSerializer
        )
    }

    @Test
    fun testNewImageClickedWhenUnchangedThenSetNewInitialState() {
        val displayMetrics = DisplayMetrics()
        Mockito.`when`(view!!.displayMetrics).thenReturn(displayMetrics)
        displayMetrics.widthPixels = 300
        displayMetrics.heightPixels = 500
        val command = Mockito.mock(
            Command::class.java
        )
        Mockito.`when`(commandFactory!!.createInitCommand(300, 500)).thenReturn(command)
        presenter!!.newImageClicked()
        Mockito.verify(commandManager)?.setInitialStateCommand(command)
        Mockito.verify(commandManager)?.reset()
        Mockito.verifyNoMoreInteractions(navigator)
    }

    @Test
    fun testNewImageClickedWhenUndoAvailableAndSavedSetNewInitialState() {
        Mockito.`when`(commandManager!!.isUndoAvailable).thenReturn(true)
        Mockito.`when`(model!!.isSaved).thenReturn(true)
        val displayMetrics = DisplayMetrics()
        Mockito.`when`(view!!.displayMetrics).thenReturn(displayMetrics)
        displayMetrics.widthPixels = 200
        displayMetrics.heightPixels = 100
        val command = Mockito.mock(
            Command::class.java
        )
        Mockito.`when`(commandFactory!!.createInitCommand(200, 100)).thenReturn(command)
        presenter!!.newImageClicked()
        Mockito.verify(commandManager).setInitialStateCommand(command)
        Mockito.verify(commandManager).reset()
        Mockito.verifyNoMoreInteractions(navigator)
    }

    @Test
    fun testNewImageClickedWhenUndoAvailableAndNotSavedThenShowSaveBeforeNewImage() {
        Mockito.`when`(commandManager!!.isUndoAvailable).thenReturn(true)
        Mockito.`when`(model!!.isSaved).thenReturn(false)
        presenter!!.newImageClicked()
        Mockito.verify(navigator)?.showSaveBeforeNewImageDialog()
        Mockito.verifyNoMoreInteractions(navigator)
    }

    @Test
    fun testDiscardImageClickedThenClearsLayers() {
        val command = Mockito.mock(
            Command::class.java
        )
        Mockito.`when`(commandFactory!!.createResetCommand()).thenReturn(command)
        presenter!!.discardImageClicked()
        Mockito.verify(commandManager)?.addCommand(command)
        Mockito.verifyNoMoreInteractions(commandManager, navigator)
    }

    @Test
    fun testBackToCatroidClickedWhenUnchangedThenFinishActivity() {
        presenter!!.backToPocketCodeClicked()
        Mockito.verify(navigator)?.finishActivity()
        Mockito.verifyNoMoreInteractions(navigator)
    }

    @Test
    fun testBackToCatroidClickedWhenUndoAvailableThenShowSaveDialog() {
        Mockito.`when`(commandManager!!.isUndoAvailable).thenReturn(true)
        presenter!!.backToPocketCodeClicked()
        Mockito.verify(navigator)?.showSaveBeforeFinishDialog()
        Mockito.verifyNoMoreInteractions(navigator)
    }

    @Test
    fun testBackToCatroidClickedWhenUndoAvailableAndSavedThenFinishActivity() {
        Mockito.`when`(model!!.isSaved).thenReturn(true)
        Mockito.`when`(commandManager!!.isUndoAvailable).thenReturn(true)
        presenter!!.backToPocketCodeClicked()
        Mockito.verify(navigator)?.finishActivity()
        Mockito.verifyNoMoreInteractions(navigator)
    }

    @Test
    fun testLoadImageClickedLoad() {
        presenter!!.replaceImageClicked()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE)
        Mockito.verifyNoMoreInteractions(interactor)
    }

    @Test
    fun testLoadImageClickedSaveFirst() {
        Mockito.`when`(commandManager!!.isUndoAvailable).thenReturn(true)
        Mockito.`when`(model!!.isSaved).thenReturn(false)
        presenter!!.replaceImageClicked()
        Mockito.verify(navigator)?.showSaveBeforeLoadImageDialog()
        Mockito.verifyNoMoreInteractions(interactor)
    }

    @Test
    fun testLoadNewImage() {
        presenter!!.loadNewImage()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE)
        Mockito.verifyNoMoreInteractions(interactor)
    }

    @Test
    fun testSaveCopyClickedThenSaveImage() {
        presenter!!.saveCopyClicked(false)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY)
        Mockito.verify<Interactor?>(interactor).saveCopy(
            presenter!!, SAVE_IMAGE_DEFAULT, workspace!!.layerModel,
            commandSerializer!!, null,
            context!!
        )
        Mockito.verifyNoMoreInteractions(interactor)
    }

    @Test
    fun testSaveImageClickedThenSaveImage() {
        presenter!!.saveImageClicked()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE)
        Mockito.verify<Interactor?>(interactor).saveImage(
            presenter!!, SAVE_IMAGE_DEFAULT, workspace!!.layerModel,
            commandSerializer!!, null,
            context!!
        )
        Mockito.verifyNoMoreInteractions(interactor)
    }

    @Test
    fun testEnterFullscreenClicked() {
        presenter!!.enterFullscreenClicked()
        Mockito.verify(model)?.isFullscreen = true
        Mockito.verify(topBarViewHolder)?.hide()
        Mockito.verify(view)?.hideKeyboard()
        Mockito.verify(view)?.enterFullscreen()
        Mockito.verify(toolController)?.disableToolOptionsView()
        Mockito.verify(perspective)?.enterFullscreen()
    }

    @Test
    fun testExitFullscreenClicked() {
        presenter!!.exitFullscreenClicked()
        Mockito.verify(model)?.isFullscreen = false
        Mockito.verify(topBarViewHolder)?.show()
        Mockito.verify(view)?.exitFullscreen()
        Mockito.verify(toolController)?.enableToolOptionsView()
        Mockito.verify(perspective)?.exitFullscreen()
    }

    @Test
    fun testShowHelpClickedThenStartWelcomeActivity() {
        presenter!!.showHelpClicked()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .startWelcomeActivity(REQUEST_CODE_INTRO)
        Mockito.verifyNoMoreInteractions(navigator)
    }

    @Test
    fun testShowAboutClickedThenShowAboutDialog() {
        presenter!!.showAboutClicked()
        Mockito.verify(navigator)?.showAboutDialog()
        Mockito.verifyNoMoreInteractions(navigator)
    }

    @Test
    fun testOnNewImageThenResetCommandManager() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        presenter!!.onNewImage()
        Mockito.verify(commandManager)?.reset()
    }

    @Test
    fun testOnNewImageWhenCommandReturnsThenResetPerspective() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        presenter!!.onNewImage()
        presenter!!.onCommandPostExecute()
        Mockito.verify(workspace)?.resetPerspective()
    }

    @Test
    fun testOnNewImageThenSetInitialStateCommand() {
        val displayMetrics = DisplayMetrics()
        Mockito.`when`(view!!.displayMetrics).thenReturn(displayMetrics)
        displayMetrics.widthPixels = 300
        displayMetrics.heightPixels = 500
        val command = Mockito.mock(
            Command::class.java
        )
        Mockito.`when`(commandFactory!!.createInitCommand(300, 500)).thenReturn(command)
        presenter!!.onNewImage()
        Mockito.verify(commandManager)?.setInitialStateCommand(command)
    }

    @Test
    fun testOnNewImageThenResetSavedPictureUri() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        presenter!!.onNewImage()
        Mockito.verify(model)?.savedPictureUri = null
    }

    @Test
    fun testHandleActivityResultWhenUnhandledThenForwardResult() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        val intent = Mockito.mock(Intent::class.java)
        presenter!!.handleActivityResult(0, Activity.RESULT_OK, intent)
        Mockito.verify(view).superHandleActivityResult(0, Activity.RESULT_OK, intent)
    }

    @Test
    fun testHandleActivityResultWhenUnhandledAndResultNotOKThenForwardResult() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        val intent = Mockito.mock(Intent::class.java)
        presenter!!.handleActivityResult(0, Activity.RESULT_CANCELED, intent)
        Mockito.verify(view).superHandleActivityResult(0, Activity.RESULT_CANCELED, intent)
    }

    @Test
    fun testHandleActivityResultWhenLoadPictureThenLoadIntentPicture() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        metrics.widthPixels = 13
        metrics.heightPixels = 17
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        val intent = Mockito.mock(Intent::class.java)
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(intent.data).thenReturn(uri)
        presenter!!.handleActivityResult(REQUEST_CODE_LOAD_PICTURE, Activity.RESULT_OK, intent)
        Mockito.verify<Interactor?>(interactor).loadFile(
            presenter!!, LOAD_IMAGE_DEFAULT, uri,
            context!!, false,
            commandSerializer!!
        )
    }

    @Test
    fun testHandleActivityResultWhenResultNotOkThenDoNothing() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        val intent = Mockito.mock(Intent::class.java)
        presenter!!.handleActivityResult(0, Activity.RESULT_CANCELED, intent)
        Mockito.verifyZeroInteractions(interactor, navigator)
    }

    @Test
    fun testHandleActivityResultWhenRequestIntroAndResultOKThenDoNothing() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        val intent = Mockito.mock(Intent::class.java)
        presenter!!.handleActivityResult(REQUEST_CODE_INTRO, Activity.RESULT_OK, intent)
        Mockito.verifyZeroInteractions(interactor, navigator)
    }

    @Test
    fun testHandleActivityResultWhenRequestIntroAndResultSplitScreenNotSupportedThenShowToast() {
        val metrics = Mockito.mock(DisplayMetrics::class.java)
        Mockito.`when`(view!!.displayMetrics).thenReturn(metrics)
        val intent = Mockito.mock(Intent::class.java)
        presenter!!.handleActivityResult(REQUEST_CODE_INTRO, RESULT_INTRO_MW_NOT_SUPPORTED, intent)
        Mockito.verify(navigator)
            ?.showToast(R.string.pocketpaint_intro_split_screen_not_supported, Toast.LENGTH_LONG)
        Mockito.verifyZeroInteractions(interactor, navigator)
    }

    @Test
    fun testOnBackPressedWhenUntouchedThenFinishActivity() {
        Mockito.`when`(toolController!!.isDefaultTool).thenReturn(true)
        presenter!!.onBackPressed()
        Mockito.verify(navigator)?.finishActivity()
    }

    @Test
    fun testOnBackPressedWhenStartDrawerOpenThenCloseDrawer() {
        Mockito.`when`(drawerLayoutViewHolder!!.isDrawerOpen(GravityCompat.START)).thenReturn(true)
        presenter!!.onBackPressed()
        Mockito.verify(drawerLayoutViewHolder).closeDrawer(GravityCompat.START, true)
    }

    @Test
    fun testOnBackPressedWhenEndDrawerOpenThenCloseDrawer() {
        Mockito.`when`(drawerLayoutViewHolder!!.isDrawerOpen(GravityCompat.END)).thenReturn(true)
        presenter!!.onBackPressed()
        Mockito.verify(drawerLayoutViewHolder).closeDrawer(GravityCompat.END, true)
    }

    @Test
    fun testOnBackPressedWhenIsFullscreenThenExitFullscreen() {
        Mockito.`when`(model!!.isFullscreen).thenReturn(true)
        presenter!!.onBackPressed()
        Mockito.verify(model).isFullscreen = false
    }

    @Test
    fun testSaveImageConfirmClickedThenSaveImage() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.saveImageConfirmClicked(0, uri)
        Mockito.verify(interactor)?.saveImage(
            presenter!!, 0, workspace!!.layerModel,
            commandSerializer!!, uri,
            context!!
        )
    }

    @Test
    fun testSaveImageConfirmClickedThenUseRequestCode() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.saveImageConfirmClicked(-1, uri)
        Mockito.verify(interactor)?.saveImage(
            presenter!!, -1, workspace!!.layerModel,
            commandSerializer!!, uri,
            context!!
        )
    }

    @Test
    fun testSaveCopyConfirmCLickedThenSaveImage() {
        presenter!!.saveCopyConfirmClicked(0, null)
        Mockito.verify(interactor)?.saveCopy(
            presenter!!, 0, workspace!!.layerModel,
            commandSerializer!!, null,
            context!!
        )
    }

    @Test
    fun testSaveCopyConfirmClickedThenUseRequestCode() {
        presenter!!.saveCopyConfirmClicked(-1, null)
        Mockito.verify(interactor)?.saveCopy(
            presenter!!, -1, workspace!!.layerModel,
            commandSerializer!!, null,
            context!!
        )
    }

    @Test
    fun testUndoClickedWhenKeyboardOpenedThenCloseKeyboard() {
        Mockito.`when`(view!!.isKeyboardShown).thenReturn(true)
        presenter!!.undoClicked()
        Mockito.verify(view).hideKeyboard()
        Mockito.verifyZeroInteractions(commandManager)
    }

    @Test
    fun testUndoClickedThenExecuteUndo() {
        presenter!!.undoClicked()
        Mockito.verify(commandManager)?.undo()
    }

    @Test
    fun testRedoClickedWhenKeyboardOpenedThenCloseKeyboard() {
        Mockito.`when`(view!!.isKeyboardShown).thenReturn(true)
        presenter!!.redoClicked()
        Mockito.verify(view).hideKeyboard()
        Mockito.verifyZeroInteractions(commandManager)
    }

    @Test
    fun testRedoClickedThenExecuteRedo() {
        presenter!!.redoClicked()
        Mockito.verify(commandManager)?.redo()
    }

    @Test
    fun testShowLayerMenuClickedThenShowLayerDrawer() {
        presenter!!.showLayerMenuClicked()
        Mockito.verify(drawerLayoutViewHolder)?.openDrawer(GravityCompat.END)
    }

    @Test
    fun testOnCommandPostExecuteThenSetModelUnsaved() {
        presenter!!.onCommandPostExecute()
        Mockito.verify(model)?.isSaved = false
    }

    @Test
    fun testOnCommandPostExecuteThenResetInternalToolState() {
        presenter!!.onCommandPostExecute()
        Mockito.verify(toolController)?.resetToolInternalState()
    }

    @Test
    fun testOnCommandPostExecuteThenRefreshDrawingSurface() {
        presenter!!.onCommandPostExecute()
        Mockito.verify(view)?.refreshDrawingSurface()
    }

    @Test
    fun testOnCommandPostExecuteThenSetUndoRedoButtons() {
        presenter!!.onCommandPostExecute()
        Mockito.verify(topBarViewHolder)?.disableRedoButton()
        Mockito.verify(topBarViewHolder)?.disableUndoButton()
    }

    @Test
    fun testSetTopBarColorThenSetColorButtonColor() {
        presenter!!.setBottomNavigationColor(Color.GREEN)
        Mockito.verify(bottomNavigationViewHolder)?.setColorButtonColor(Color.GREEN)
    }

    @Test
    fun testInitializeFromCleanStateWhenDefaultThenUnsetSavedPictureUri() {
        presenter!!.initializeFromCleanState(null, null)
        Mockito.verify(model)?.isOpenedFromCatroid = false
        Mockito.verify(model)?.savedPictureUri = null
    }

    @Test
    fun testInitializeFromCleanStateWhenDefaultThenResetTool() {
        presenter!!.initializeFromCleanState(null, null)
        Mockito.verify(toolController)?.resetToolInternalStateOnImageLoaded()
    }

    @Test
    fun testInitializeFromCleanStateWhenFromCatroidAndPathNotExistentThenCreateFile() {
        presenter!!.initializeFromCleanState("testPath", "testName")
        Mockito.verify(model)?.isOpenedFromCatroid = true
        Mockito.verify<Interactor?>(interactor)
            .createFile(presenter!!, CREATE_FILE_DEFAULT, "testName")
    }

    @Test
    fun testInitializeFromCleanStateWhenFromCatroidAndPathExistsThenLoadFile() {
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(model!!.savedPictureUri).thenReturn(uri)
        Mockito.`when`(
            view!!.getUriFromFile(
                any<File>()
            )
        ).thenReturn(uri)
        presenter!!.initializeFromCleanState("/", "testName")
        Mockito.verify(model).isOpenedFromCatroid = true
        Mockito.verify(model).savedPictureUri = uri
        Mockito.verify<Interactor?>(interactor).loadFile(
            presenter!!, LOAD_IMAGE_CATROID, uri,
            context!!, false,
            commandSerializer!!
        )
    }

    @Test
    fun testRestoreStateThenRestoreFragmentListeners() {
        presenter!!.restoreState(false, false, false, false, null, null)
        Mockito.verify(navigator)?.restoreFragmentListeners()
    }

    @Test
    fun testRestoreStateThenSetModel() {
        val savedPictureUri = Mockito.mock(Uri::class.java)
        val cameraImageUri = Mockito.mock(Uri::class.java)
        presenter!!.restoreState(false, false, false, false, savedPictureUri, cameraImageUri)
        Mockito.verify(model)?.isFullscreen = false
        Mockito.verify(model)?.isSaved = false
        Mockito.verify(model)?.isOpenedFromCatroid = false
        Mockito.verify(model)?.isOpenedFromFormulaEditorInCatroid = false
        Mockito.verify(model)?.savedPictureUri = savedPictureUri
        Mockito.verify(model)?.cameraImageUri = cameraImageUri
    }

    @Test
    fun testRestoreStateWhenStatesSetThenSetModel() {
        val savedPictureUri = Mockito.mock(Uri::class.java)
        val cameraImageUri = Mockito.mock(Uri::class.java)
        presenter!!.restoreState(true, true, true, true, savedPictureUri, cameraImageUri)
        Mockito.verify(model)?.isFullscreen = true
        Mockito.verify(model)?.isSaved = true
        Mockito.verify(model)?.isOpenedFromCatroid = true
        Mockito.verify(model)?.isOpenedFromFormulaEditorInCatroid = true
        Mockito.verify(model)?.savedPictureUri = savedPictureUri
        Mockito.verify(model)?.cameraImageUri = cameraImageUri
    }

    @Test
    fun testRestoreStateThenResetTool() {
        presenter!!.restoreState(false, false, false, false, null, null)
        Mockito.verify(toolController)?.resetToolInternalStateOnImageLoaded()
    }

    @Test
    fun testOnCreateToolCallsToolController() {
        presenter!!.onCreateTool()
        Mockito.verify(toolController)?.createTool()
        Mockito.verifyNoMoreInteractions(toolController)
    }

    @Test
    fun testFinishInitializeThenSetUndoRedoButtons() {
        presenter!!.finishInitialize()
        Mockito.verify(topBarViewHolder)?.disableUndoButton()
        Mockito.verify(topBarViewHolder)?.disableRedoButton()
    }

    @Test
    fun testFinishInitializeWhenUndoAvailableThenSetUndoRedoButtons() {
        Mockito.`when`(commandManager!!.isUndoAvailable).thenReturn(true)
        presenter!!.finishInitialize()
        Mockito.verify(topBarViewHolder)?.enableUndoButton()
        Mockito.verify(topBarViewHolder)?.disableRedoButton()
    }

    @Test
    fun testFinishInitializeWhenRedoAvailableThenSetUndoRedoButtons() {
        Mockito.`when`(commandManager!!.isRedoAvailable).thenReturn(true)
        presenter!!.finishInitialize()
        Mockito.verify(topBarViewHolder)?.disableUndoButton()
        Mockito.verify(topBarViewHolder)?.enableRedoButton()
    }

    @Test
    fun testFinishInitializeWhenNotFullscreenThenRestoreState() {
        presenter!!.finishInitialize()
        Mockito.verify(view)?.exitFullscreen()
    }

    @Test
    fun testFinishInitializeWhenFullscreenThenRestoreState() {
        Mockito.`when`(model!!.isFullscreen).thenReturn(true)
        presenter!!.finishInitialize()
        Mockito.verify(view)?.enterFullscreen()
    }

    @Test
    fun testFinishInitializeThenRestoreColorButtonColor() {
        Mockito.`when`(model!!.isFullscreen).thenReturn(true)
        Mockito.`when`(toolController!!.toolColor).thenReturn(Color.RED)
        presenter!!.finishInitialize()
        Mockito.verify(bottomNavigationViewHolder)?.setColorButtonColor(Color.RED)
    }

    @Test
    fun testFinishInitializeWhenDefaultThenInitializeActionBarDefault() {
        presenter!!.finishInitialize()
        Mockito.verify(view)?.initializeActionBar(false)
    }

    @Test
    fun testFinishInitializeWhenFromCatroidThenInitializeActionBarCatroid() {
        Mockito.`when`(model!!.isOpenedFromCatroid).thenReturn(true)
        presenter!!.finishInitialize()
        Mockito.verify(view)?.initializeActionBar(true)
    }

    @Test
    fun testFinishInitializeWhenDefaultThenRemoveCatroidNavigationItems() {
        presenter!!.removeMoreOptionsItems(menu)
        Mockito.verify(topBarViewHolder)?.removeCatroidMenuItems(menu)
    }

    @Test
    fun testFinishInitializeWhenFromCatroidThenRemoveSaveNavigationItems() {
        Mockito.`when`(model!!.isOpenedFromCatroid).thenReturn(true)
        presenter!!.removeMoreOptionsItems(menu)
        Mockito.verify(topBarViewHolder)?.removeStandaloneMenuItems(menu)
    }

    @Test
    fun testFinishInitializeWhenCommandManagerBusyRestoreProgressDialog() {
        Mockito.`when`(commandManager!!.isBusy).thenReturn(true)
        presenter!!.finishInitialize()
        Mockito.verify(navigator)?.showIndeterminateProgressDialog()
    }

    @Test
    fun testFinishInitializeWhenCommandManagerIdleThenDoNothing() {
        presenter!!.finishInitialize()
        Mockito.verify(navigator, Mockito.never())?.showIndeterminateProgressDialog()
    }

    @Test
    fun testToolClickedWhenSameToolTypeThenToggleOptions() {
        Mockito.`when`(toolController!!.toolType).thenReturn(ToolType.TEXT)
        Mockito.`when`(toolController.hasToolOptionsView()).thenReturn(true)
        presenter!!.toolClicked(ToolType.TEXT)
        Mockito.verify(toolController).toggleToolOptionsView()
    }

    @Test
    fun testToolClickedWhenKeyboardShownThenHideKeyboard() {
        Mockito.`when`(view!!.isKeyboardShown).thenReturn(true)
        presenter!!.toolClicked(ToolType.ERASER)
        Mockito.verify(view).hideKeyboard()
    }

    @Test
    fun testOnCreateFilePostExecuteWhenFailedThenShowDialog() {
        presenter!!.onCreateFilePostExecute(CREATE_FILE_DEFAULT, null)
        Mockito.verify(navigator)?.showSaveErrorDialog()
    }

    @Test
    fun testOnCreateFilePostExecuteWhenDefaultThenSetUri() {
        val file = Mockito.mock(File::class.java)
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(view!!.getUriFromFile(file)).thenReturn(uri)
        presenter!!.onCreateFilePostExecute(CREATE_FILE_DEFAULT, file)
        Mockito.verify(model)?.savedPictureUri = uri
        Mockito.verifyZeroInteractions(navigator)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testOnCreateFilePostExecuteWhenInvalidRequestThenThrowException() {
        val file = Mockito.mock(File::class.java)
        presenter!!.onCreateFilePostExecute(0, file)
    }

    @Test
    fun testOnLoadImagePreExecuteDoesNothing() {
        presenter!!.onLoadImagePreExecute(LOAD_IMAGE_DEFAULT)
        Mockito.verifyZeroInteractions(
            view, model, navigator, interactor, topBarViewHolder, workspace, perspective,
            drawerLayoutViewHolder, commandFactory, commandManager, bottomBarViewHolder,
            toolController
        )
    }

    @Test
    fun testOnLoadImagePostExecuteWhenFailedThenShowDialog() {
        presenter!!.onLoadImagePostExecute(0, null, null)
        Mockito.verify(navigator)?.showLoadErrorDialog()
    }

    @Test
    fun testOnLoadImagePostExecuteWhenDefaultThenResetModelUris() {
        val uri = Mockito.mock(Uri::class.java)
        val bitmap = Mockito.mock(Bitmap::class.java)
        val returnValue = BitmapReturnValue(null, bitmap, false)
        presenter!!.onLoadImagePostExecute(LOAD_IMAGE_DEFAULT, uri, returnValue)
        Mockito.verify(model)?.savedPictureUri = null
        Mockito.verify(model)?.cameraImageUri = null
    }

    @Test
    fun testOnLoadImagePostExecuteWhenDefaultThenResetCommandManager() {
        val uri = Mockito.mock(Uri::class.java)
        val bitmap = Mockito.mock(Bitmap::class.java)
        val returnValue = BitmapReturnValue(null, bitmap, false)
        val command = Mockito.mock(
            Command::class.java
        )
        Mockito.`when`(commandFactory!!.createInitCommand(bitmap)).thenReturn(command)
        presenter!!.onLoadImagePostExecute(LOAD_IMAGE_DEFAULT, uri, returnValue)
        Mockito.verify(commandManager)?.setInitialStateCommand(command)
        Mockito.verify(commandManager)?.reset()
        Mockito.verifyNoMoreInteractions(commandManager)
    }

    @Test
    fun testOnLoadImagePostExecuteWhenImportThenSetBitmap() {
        val uri = Mockito.mock(Uri::class.java)
        val bitmap = Mockito.mock(Bitmap::class.java)
        val returnValue = BitmapReturnValue(null, bitmap, false)
        Mockito.`when`(toolController!!.toolType).thenReturn(ToolType.IMPORTPNG)
        presenter!!.onLoadImagePostExecute(LOAD_IMAGE_IMPORT_PNG, uri, returnValue)
        Mockito.verify(toolController).setBitmapFromSource(bitmap)
        Mockito.verifyZeroInteractions(commandManager)
    }

    @Test
    fun testOnLoadImagePostExecuteWhenImportAndNotImportToolSetThenIgnore() {
        val uri = Mockito.mock(Uri::class.java)
        val bitmap = Mockito.mock(Bitmap::class.java)
        val returnValue = BitmapReturnValue(null, bitmap, false)
        presenter!!.onLoadImagePostExecute(LOAD_IMAGE_IMPORT_PNG, uri, returnValue)
        Mockito.verify(toolController, Mockito.never())?.setBitmapFromSource(
            ArgumentMatchers.any(
                Bitmap::class.java
            )
        )
        Mockito.verifyZeroInteractions(commandManager)
    }

    @Test
    fun testOnLoadImagePostExecuteWhenCatroidThenSetModelUris() {
        val uri = Mockito.mock(Uri::class.java)
        val bitmap = Mockito.mock(Bitmap::class.java)
        val returnValue = BitmapReturnValue(null, bitmap, false)
        presenter!!.onLoadImagePostExecute(LOAD_IMAGE_CATROID, uri, returnValue)
        Mockito.verify(model)?.savedPictureUri = uri
        Mockito.verify(model)?.cameraImageUri = null
        Mockito.verifyNoMoreInteractions(model)
    }

    @Test
    fun testOnLoadImagePostExecuteWhenCatroidThenResetCommandManager() {
        val uri = Mockito.mock(Uri::class.java)
        val bitmap = Mockito.mock(Bitmap::class.java)
        val returnValue = BitmapReturnValue(null, bitmap, false)
        val command = Mockito.mock(
            Command::class.java
        )
        Mockito.`when`(commandFactory!!.createInitCommand(bitmap)).thenReturn(command)
        presenter!!.onLoadImagePostExecute(LOAD_IMAGE_CATROID, uri, returnValue)
        Mockito.verify(commandManager)?.setInitialStateCommand(command)
        Mockito.verify(commandManager)?.reset()
        Mockito.verifyNoMoreInteractions(commandManager)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testOnLoadImagePostExecuteWhenInvalidRequestThenThrowException() {
        val uri = Mockito.mock(Uri::class.java)
        val bitmap = Mockito.mock(Bitmap::class.java)
        val returnValue = BitmapReturnValue(null, bitmap, false)
        presenter!!.onLoadImagePostExecute(0, uri, returnValue)
    }

    @Test
    fun testOnSaveImagePreExecuteThenShowProgressDialog() {
        presenter!!.onSaveImagePreExecute(0)
        Mockito.verify(view)?.showContentLoadingProgressBar()
    }

    @Test
    fun testOnSaveImagePostExecuteWhenFailedThenShowDialog() {
        presenter!!.onSaveImagePostExecute(0, null, false)
        Mockito.verify(navigator)?.showSaveErrorDialog()
    }

    @Test
    fun testHandlePermissionResultLoadPermissionGranted() {
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_REQUEST_CODE_REPLACE_PICTURE, arrayOf<String>(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE)
    }

    @Test
    fun testHandlePermissionResultLoadPermissionPermanentlyDenied() {
        val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(true)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_REQUEST_CODE_REPLACE_PICTURE,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify(navigator).showRequestPermanentlyDeniedPermissionRationaleDialog()
    }

    @Test
    fun testHandlePermissionResultLoadPermissionNotGranted() {
        val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(false)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_REQUEST_CODE_REPLACE_PICTURE,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showRequestPermissionRationaleDialog(
                PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
                permission,
                PERMISSION_REQUEST_CODE_REPLACE_PICTURE
            )
    }

    @Test
    fun testHandlePermissionResultSavePermissionGranted() {
        Mockito.`when`(workspace!!.layerModel).thenReturn(
            Mockito.mock(
                LayerModel::class.java
            )
        )
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE, arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        val uri = model!!.savedPictureUri
        Mockito.verify<Interactor?>(interactor).saveImage(
            any<SaveImageCallback>(),
            ArgumentMatchers.eq(SAVE_IMAGE_DEFAULT),
            any<LayerModel>(),
            any<CommandSerializer>(),
            ArgumentMatchers.eq<Uri?>(null),
            any<Context>(),
        )
    }

    @Test
    fun testHandlePermissionResultSavePermissionPermanentlyDenied() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(true)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify(navigator).showRequestPermanentlyDeniedPermissionRationaleDialog()
    }

    @Test
    fun testHandlePermissionResultSavePermissionNotGranted() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(false)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showRequestPermissionRationaleDialog(
                PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
                permission,
                PERMISSION_EXTERNAL_STORAGE_SAVE
            )
    }

    @Test
    fun testHandlePermissionResultSaveCopyPermissionGranted() {
        Mockito.`when`(workspace!!.layerModel).thenReturn(
            Mockito.mock(
                LayerModel::class.java
            )
        )
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_COPY, arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        Mockito.verify<Interactor?>(interactor).saveCopy(
            any<SaveImageCallback>(),
            ArgumentMatchers.eq(SAVE_IMAGE_DEFAULT),
            any<LayerModel>(),
            any<CommandSerializer>(),
            ArgumentMatchers.eq<Uri?>(null),
            any<Context>(),
        )
    }

    @Test
    fun testHandlePermissionResultSaveCopyPermissionNotGranted() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(false)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showRequestPermissionRationaleDialog(
                PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
                permission,
                PERMISSION_EXTERNAL_STORAGE_SAVE_COPY
            )
    }

    @Test
    fun testHandlePermissionResultSaveCopyPermissionPermanentlyDenied() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(true)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify(navigator).showRequestPermanentlyDeniedPermissionRationaleDialog()
    }

    @Test
    fun testHandlePermissionResultSaveBeforeFinishPermissionGranted() {
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH, arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        Mockito.verify<Interactor?>(interactor).saveImage(
            presenter!!, SAVE_IMAGE_FINISH, workspace!!.layerModel,
            commandSerializer!!, FileIO.storeImageUri,
            context!!
        )
    }

    @Test
    fun testHandlePermissionResultSaveBeforeFinishPermissionNotGranted() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(false)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showRequestPermissionRationaleDialog(
                PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
                permission,
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH
            )
    }

    @Test
    fun testHandlePermissionResultSaveBeforeFinishPermissionPermanentlyDenied() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(true)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify(navigator).showRequestPermanentlyDeniedPermissionRationaleDialog()
    }

    @Test
    fun testHandlePermissionResultSaveBeforeLoadNewPermissionNotGranted() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(false)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showRequestPermissionRationaleDialog(
                PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
                permission,
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW
            )
    }

    @Test
    fun testHandlePermissionResultSaveBeforeLoadNewPermissionPermanentlyDenied() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(true)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify(navigator).showRequestPermanentlyDeniedPermissionRationaleDialog()
    }

    @Test
    fun testHandlePermissionResultSaveBeforeLoadNewPermissionGranted() {
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW, arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        Mockito.verify<Interactor?>(interactor).saveImage(
            presenter!!, SAVE_IMAGE_LOAD_NEW, workspace!!.layerModel,
            commandSerializer!!, FileIO.storeImageUri,
            context!!
        )
    }

    @Test
    fun testHandlePermissionResultSaveBeforeNewEmptyPermissionNotGranted() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(false)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showRequestPermissionRationaleDialog(
                PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
                permission,
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY
            )
    }

    @Test
    fun testHandlePermissionResultSaveBeforeNewEmptyPermissionPermanentlyDenied() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Mockito.`when`(navigator!!.isPermissionPermanentlyDenied(permission)).thenReturn(true)
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
            permission, intArrayOf(PackageManager.PERMISSION_DENIED)
        )
        Mockito.verify(navigator).showRequestPermanentlyDeniedPermissionRationaleDialog()
    }

    @Test
    fun testHandlePermissionResultSaveBeforeNewEmptyPermissionGranted() {
        presenter!!.handleRequestPermissionsResult(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY, arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        Mockito.verify<Interactor?>(interactor).saveImage(
            presenter!!, SAVE_IMAGE_NEW_EMPTY, workspace!!.layerModel,
            commandSerializer!!, FileIO.storeImageUri,
            context!!
        )
    }

    @Test
    fun testHandlePermissionResultWhenStoragePermissionGrantedAndRequestCodeUnknownThenCallBaseHandle() {
        presenter!!.handleRequestPermissionsResult(
            100,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        Mockito.verify(view)?.superHandleRequestPermissionsResult(
            100,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
    }

    @Test
    fun testHandlePermissionResultWhenCameraPermissionGrantedAndRequestCodeUnknownThenCallBaseHandle() {
        presenter!!.handleRequestPermissionsResult(
            123,
            arrayOf(Manifest.permission.CAMERA),
            intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
        Mockito.verify(view)?.superHandleRequestPermissionsResult(
            123,
            arrayOf(Manifest.permission.CAMERA),
            intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
    }

    @Test
    fun testHandlePermissionResultWhenMultiplePermissionsThenCallBaseHandle() {
        presenter!!.handleRequestPermissionsResult(
            456,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.CAMERA
            ),
            intArrayOf(
                PackageManager.PERMISSION_GRANTED,
                PackageManager.PERMISSION_DENIED
            )
        )
        Mockito.verify(view)?.superHandleRequestPermissionsResult(
            456,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.CAMERA),
            intArrayOf(PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_DENIED)
        )
    }

    @Test
    fun testOnNavigationItemSelectedSaveCopyPermissionGranted() {
        presenter!!.saveCopyClicked(false)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY)
        Mockito.verify<Interactor?>(interactor).saveCopy(
            presenter!!, SAVE_IMAGE_DEFAULT, workspace!!.layerModel,
            commandSerializer!!, null,
            context!!
        )
    }

    @Test
    fun testOnNavigationItemSelectedSaveCopyPermissionNotGranted() {
        Mockito.`when`(navigator!!.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .thenReturn(false)
        Mockito.`when`(navigator.isSdkAboveOrEqualM).thenReturn(false).thenReturn(true)
        presenter!!.saveCopyClicked(false)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator).askForPermission(
            arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_EXTERNAL_STORAGE_SAVE_COPY
        )
    }

    @Test
    fun testNoPermissionCheckOnSaveBeforeFinishWhenOpenedFromCatroid() {
        Mockito.`when`(workspace!!.layerModel).thenReturn(
            Mockito.mock(
                LayerModel::class.java
            )
        )
        Mockito.`when`(navigator!!.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .thenReturn(false)
        Mockito.`when`(navigator.isSdkAboveOrEqualM).thenReturn(true)
        Mockito.`when`(model!!.isOpenedFromCatroid).thenReturn(true)
        presenter!!.saveBeforeFinish()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH)

        Mockito.verify(interactor)?.saveImage(
            any<MainActivityPresenter>(),
            anyInt(),
            any<LayerModel>(),
            any<CommandSerializer>(),
            ArgumentMatchers.eq(null as Uri?),
            any<Context>()
        )
    }

    @Test
    fun testPermissionCheckOnExportWhenOpenedFromCatroid() {
        Mockito.`when`(navigator!!.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .thenReturn(false)
        Mockito.`when`(navigator.isSdkAboveOrEqualM).thenReturn(false).thenReturn(true)
        Mockito.`when`(model!!.isOpenedFromCatroid).thenReturn(true)
        presenter!!.saveCopyClicked(false)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_COPY)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator).askForPermission(
            arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_EXTERNAL_STORAGE_SAVE_COPY
        )
    }

    @Test
    fun testOnNavigationItemSelectedSavePermissionGranted() {
        presenter!!.saveImageClicked()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE)
        Mockito.verify<Interactor?>(interactor).saveImage(
            presenter!!, SAVE_IMAGE_DEFAULT, workspace!!.layerModel,
            commandSerializer!!, FileIO.storeImageUri,
            context!!
        )
    }

    @Test
    fun testOnNavigationItemSelectedSavePermissionNotGranted() {
        Mockito.`when`(navigator!!.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .thenReturn(false)
        Mockito.`when`(navigator.isSdkAboveOrEqualM).thenReturn(false).thenReturn(true)
        presenter!!.saveImageClicked()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator).askForPermission(
            arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_EXTERNAL_STORAGE_SAVE
        )
    }

    @Test
    fun testSaveAndFinishPermissionGranted() {
        presenter!!.saveBeforeFinish()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH)
        Mockito.verify<Interactor?>(interactor).saveImage(
            presenter!!, SAVE_IMAGE_FINISH, workspace!!.layerModel,
            commandSerializer!!, FileIO.storeImageUri,
            context!!
        )
    }

    @Test
    fun testSaveAndFinishPermissionNotGranted() {
        Mockito.`when`(navigator!!.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .thenReturn(false)
        Mockito.`when`(navigator.isSdkAboveOrEqualM).thenReturn(false).thenReturn(true)
        presenter!!.saveBeforeFinish()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator).askForPermission(
            arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH
        )
    }

    @Test
    fun testSaveAndNewImagePermissionGranted() {
        presenter!!.saveBeforeNewImage()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY)
        Mockito.verify<Interactor?>(interactor).saveImage(
            presenter!!, SAVE_IMAGE_NEW_EMPTY, workspace!!.layerModel,
            commandSerializer!!, FileIO.storeImageUri,
            context!!
        )
    }

    @Test
    fun testSaveAndNewImagePermissionNotGranted() {
        Mockito.`when`(navigator!!.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .thenReturn(false)
        Mockito.`when`(navigator.isSdkAboveOrEqualM).thenReturn(false).thenReturn(true)
        presenter!!.saveBeforeNewImage()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator).askForPermission(
            arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY
        )
    }

    @Test
    fun testSaveAndLoadImagePermissionGranted() {
        presenter!!.saveBeforeLoadImage()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW)
        Mockito.verify<Interactor?>(interactor).saveImage(
            presenter!!, SAVE_IMAGE_LOAD_NEW, workspace!!.layerModel,
            commandSerializer!!, FileIO.storeImageUri,
            context!!
        )
    }

    @Test
    fun testSaveAndLoadImagePermissionNotGranted() {
        Mockito.`when`(navigator!!.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .thenReturn(false)
        Mockito.`when`(navigator.isSdkAboveOrEqualM).thenReturn(false).thenReturn(true)
        presenter!!.saveBeforeLoadImage()
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showSaveImageInformationDialogWhenStandalone(
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
                sharedPreferences!!.preferenceImageNumber,
                false
            )
        presenter!!.switchBetweenVersions(PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator).askForPermission(
            arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW
        )
    }

    @Test
    fun testOnSaveImagePostExecuteThenDismissProgressDialog() {
        presenter!!.onSaveImagePostExecute(0, null, false)
        Mockito.verify(view)?.hideContentLoadingProgressBar()
    }

    @Test
    fun testOnSaveImagePostExecuteWhenNotSavedAsCopyThenShowSaveToast() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, false)
        if (!model!!.isOpenedFromCatroid) {
            val path = getPathFromUri(context!!, uri)
            Mockito.verify(navigator)?.showToast(
                context.getString(R.string.saved_to) + path, Toast.LENGTH_LONG
            )
        } else {
            Mockito.verify(navigator)?.showToast(R.string.saved, Toast.LENGTH_LONG)
        }
    }

    @Test
    fun testOnSaveImagePostExecuteWhenNotSavedAsCopyThenSetModelUri() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, false)
        Mockito.verify(model)?.savedPictureUri = uri
        Mockito.verify(model)?.isSaved = true
    }

    @Test
    fun testOnSaveImagePostExecuteWhenSavedAsCopyThenShowCopyToast() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, true)
        if (!model!!.isOpenedFromCatroid) {
            val path = getPathFromUri(context!!, uri)
            Mockito.verify(navigator)?.showToast(
                context.getString(R.string.copy_to) + path, Toast.LENGTH_LONG
            )
        } else {
            Mockito.verify(navigator)?.showToast(R.string.copy, Toast.LENGTH_LONG)
        }
    }

    @Test
    fun testOnSaveImagePostExecuteWhenSavedAsCopyThenDoNotTouchModel() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, true)
        Mockito.verify(model, Mockito.never())?.savedPictureUri = ArgumentMatchers.any(
            Uri::class.java
        )
        Mockito.verify(model, Mockito.never())?.cameraImageUri = ArgumentMatchers.any(
            Uri::class.java
        )
        Mockito.verify(model, Mockito.never())?.isSaved = ArgumentMatchers.anyBoolean()
        Mockito.verify(model, Mockito.never())?.isOpenedFromCatroid = ArgumentMatchers.anyBoolean()
        Mockito.verify(model, Mockito.never())?.isFullscreen = ArgumentMatchers.anyBoolean()
    }

    @Test
    fun testOnSaveImagePostExecuteWhenSavedThenBroadcastToPictureGallery() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, false)
        Mockito.verify(navigator)?.broadcastAddPictureToGallery(uri)
    }

    @Test
    fun testOnSaveImagePostExecuteWhenSavedAsCopyThenBroadcastToPictureGallery() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, true)
        Mockito.verify(navigator)?.broadcastAddPictureToGallery(uri)
    }

    @Test
    fun testOnSaveImagePostExecuteWhenSavedFromCatroidThenDoNotBroadcastToPictureGallery() {
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(model!!.isOpenedFromCatroid).thenReturn(true)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, false)
        Mockito.verify(navigator, Mockito.never())?.broadcastAddPictureToGallery(uri)
    }

    @Test
    fun testOnSaveImagePostExecuteWhenSavedAsCopyFromCatroidThenBroadcastToPictureGallery() {
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(model!!.isOpenedFromCatroid).thenReturn(true)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, true)
        Mockito.verify(navigator)?.broadcastAddPictureToGallery(uri)
    }

    @Test
    fun testOnSaveImagePostExecuteWhenChooseNewThenSetNewInitialState() {
        val uri = Mockito.mock(Uri::class.java)
        val displayMetrics = DisplayMetrics()
        Mockito.`when`(view!!.displayMetrics).thenReturn(displayMetrics)
        displayMetrics.widthPixels = 300
        displayMetrics.heightPixels = 500
        val command = Mockito.mock(
            Command::class.java
        )
        Mockito.`when`(commandFactory!!.createInitCommand(300, 500)).thenReturn(command)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_NEW_EMPTY, uri, false)
        Mockito.verify(commandManager)?.setInitialStateCommand(command)
        Mockito.verify(commandManager)?.reset()
    }

    @Test
    fun testOnSaveImagePostExecuteWhenDefaultThenDoNotShowDialog() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_DEFAULT, uri, false)
        Mockito.verify(navigator, Mockito.never())?.startLoadImageActivity(ArgumentMatchers.anyInt())
        Mockito.verify(navigator, Mockito.never())?.returnToPocketCode(ArgumentMatchers.anyString())
        Mockito.verify(navigator, Mockito.never())?.finishActivity()
    }

    @Test
    fun testOnSaveImagePostExecuteWhenFinishThenFinishActivity() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, false)
        Mockito.verify(navigator)?.finishActivity()
    }

    @Test
    fun testOnSaveImagePostExecuteWhenLoadThenStartActivity() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_LOAD_NEW, uri, false)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE)
    }

    @Test
    fun testOnSaveImagePostExecuteWhenExitToCatroidThenReturnToCatroid() {
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(uri.path).thenReturn("testPath")
        Mockito.`when`(model!!.isOpenedFromCatroid).thenReturn(true)
        presenter!!.onSaveImagePostExecute(SAVE_IMAGE_FINISH, uri, false)
        Mockito.verify(navigator)?.returnToPocketCode("testPath")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testOnSaveImagePostExecuteWhenInvalidRequestThenThrowException() {
        val uri = Mockito.mock(Uri::class.java)
        presenter!!.onSaveImagePostExecute(0, uri, false)
    }

    @Test
    fun testOnRateUsClicked() {
        presenter!!.rateUsClicked()
        Mockito.verify(navigator)?.rateUsClicked()
    }

    @Test
    fun testGetContentResolver() {
        val resolver = Mockito.mock(ContentResolver::class.java)
        Mockito.`when`(view!!.myContentResolver).thenReturn(resolver)
        val result = presenter!!.contentResolver
        Assert.assertEquals(resolver, result)
    }

    @Test
    fun testIsFinishing() {
        Mockito.`when`(view!!.finishing).thenReturn(true, false)
        Assert.assertTrue(presenter!!.isFinishing)
        Assert.assertFalse(presenter!!.isFinishing)
    }

    @Test
    fun testShowScaleDialogWhenNotEnoughMemory() {
        val bmr = BitmapReturnValue(
            workspace!!.layerModel.layers,
            workspace.layerModel.getBitmapOfAllLayers(),
            true
        )
        presenter!!.onLoadImagePostExecute(LOAD_IMAGE_IMPORT_PNG, null, bmr)
        Mockito.verify<MainActivityContracts.Navigator?>(navigator)
            .showScaleImageRequestDialog(null, LOAD_IMAGE_IMPORT_PNG)
    }
}