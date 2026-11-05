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
package org.catrobat.paintroid.presenter

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.net.Uri
import android.os.CountDownTimer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.UserPreferences
import org.catrobat.paintroid.colorpicker.ColorHistory
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.serialization.CommandSerializer
import org.catrobat.paintroid.common.CREATE_FILE_DEFAULT
import org.catrobat.paintroid.common.LOAD_IMAGE_CATROID
import org.catrobat.paintroid.common.LOAD_IMAGE_DEFAULT
import org.catrobat.paintroid.common.LOAD_IMAGE_IMPORT_PNG
import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode
import org.catrobat.paintroid.common.MainActivityConstants.CreateFileRequestCode
import org.catrobat.paintroid.common.MainActivityConstants.LoadImageRequestCode
import org.catrobat.paintroid.common.MainActivityConstants.PermissionRequestCode
import org.catrobat.paintroid.common.MainActivityConstants.SaveImageRequestCode
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY
import org.catrobat.paintroid.common.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY
import org.catrobat.paintroid.common.PERMISSION_REQUEST_CODE_IMPORT_PICTURE
import org.catrobat.paintroid.common.PERMISSION_REQUEST_CODE_REPLACE_PICTURE
import org.catrobat.paintroid.common.REQUEST_CODE_IMPORT_PNG
import org.catrobat.paintroid.common.REQUEST_CODE_INTRO
import org.catrobat.paintroid.common.REQUEST_CODE_LOAD_PICTURE
import org.catrobat.paintroid.common.RESULT_INTRO_MW_NOT_SUPPORTED
import org.catrobat.paintroid.common.SAVE_IMAGE_DEFAULT
import org.catrobat.paintroid.common.SAVE_IMAGE_FINISH
import org.catrobat.paintroid.common.SAVE_IMAGE_LOAD_NEW
import org.catrobat.paintroid.common.SAVE_IMAGE_NEW_EMPTY
import org.catrobat.paintroid.common.TEMP_PICTURE_NAME
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.contract.MainActivityContracts.Interactor
import org.catrobat.paintroid.contract.MainActivityContracts.MainView
import org.catrobat.paintroid.controller.ToolController
import org.catrobat.paintroid.dialog.PermissionInfoDialog
import org.catrobat.paintroid.iotasks.BitmapReturnValue
import org.catrobat.paintroid.iotasks.CreateFile.CreateFileCallback
import org.catrobat.paintroid.iotasks.LoadImage.LoadImageCallback
import org.catrobat.paintroid.iotasks.SaveImage.SaveImageCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.iotasks.WorkspaceReturnValue
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape
import org.catrobat.paintroid.tools.implementation.CLICK_TIMEOUT_MILLIS
import org.catrobat.paintroid.tools.implementation.CONSTANT_3
import org.catrobat.paintroid.tools.implementation.ClipboardTool
import org.catrobat.paintroid.tools.implementation.ClippingTool
import org.catrobat.paintroid.tools.implementation.LineTool
import org.catrobat.paintroid.tools.implementation.DefaultToolPaint
import org.catrobat.paintroid.tools.implementation.EraserTool
import org.catrobat.paintroid.ui.LayerAdapter
import java.io.File

@SuppressWarnings("LongParameterList", "LargeClass", "ThrowingExceptionsWithoutMessageOrCause")
open class MainActivityPresenter(
    override val fileActivity: Activity?,
    private val view: MainView,
    private val model: MainActivityContracts.Model,
    private val workspace: Workspace,
    private val navigator: MainActivityContracts.Navigator,
    private val interactor: Interactor,
    private val topBarViewHolder: MainActivityContracts.TopBarViewHolder,
    private val bottomBarViewHolder: MainActivityContracts.BottomBarViewHolder,
    private val drawerLayoutViewHolder: MainActivityContracts.DrawerLayoutViewHolder,
    private val bottomNavigationViewHolder: MainActivityContracts.BottomNavigationViewHolder,
    private val commandFactory: CommandFactory,
    private val commandManager: CommandManager,
    private val toolController: ToolController,
    private val sharedPreferences: UserPreferences,
    private val idlingResource: CountingIdlingResource,
    override val context: Context,
    private val internalMemoryPath: File,
    private val commandSerializer: CommandSerializer
) : MainActivityContracts.Presenter, SaveImageCallback, LoadImageCallback, CreateFileCallback {
    private var downTimer: CountDownTimer? = null
    private var layerAdapter: LayerAdapter? = null
    private var resetPerspectiveAfterNextCommand = false
    private var isExport = false
    private var wasImageLoaded = false
    private val isImageUnchanged: Boolean
        get() = !commandManager.isUndoAvailable

    override val bitmap: Bitmap?
        get() = workspace.bitmapOfAllLayers

    override val isFinishing: Boolean
        get() = view.finishing

    override val contentResolver: ContentResolver
        get() = view.myContentResolver

    override val imageNumber: Int
        get() {
            val imageNumber = sharedPreferences.preferenceImageNumber
            if (imageNumber == 0) {
                countUpImageNumber()
            }
            return sharedPreferences.preferenceImageNumber
        }

    var clippingToolInUseAndUndoRedoClicked = false
    var clippingToolPaint = Paint()
    var toolOptionsViewWasShown = false

    override fun replaceImageClicked() {
        checkIfClippingToolNeedsAdjustment()
        switchBetweenVersions(PERMISSION_REQUEST_CODE_REPLACE_PICTURE, false)
        setFirstCheckBoxInLayerMenu()
    }

    override fun addImageToCurrentLayerClicked() {
        checkIfClippingToolNeedsAdjustment()
        setTool(ToolType.IMPORTPNG)
        switchBetweenVersions(PERMISSION_REQUEST_CODE_IMPORT_PICTURE)
    }

    private fun setFirstCheckBoxInLayerMenu() {
        layerAdapter?.getViewHolderAt(0)?.apply { setLayerVisibilityCheckbox(true) }
    }

    override fun saveBeforeLoadImage() {
        navigator.showSaveImageInformationDialogWhenStandalone(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
            imageNumber,
            false
        )
    }

    override fun loadNewImage() {
        checkIfClippingToolNeedsAdjustment()
        navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE)
        setFirstCheckBoxInLayerMenu()
    }

    override fun newImageClicked() {
        checkIfClippingToolNeedsAdjustment()
        if (isImageUnchanged || model.isSaved) {
            onNewImage()
            setFirstCheckBoxInLayerMenu()
        } else {
            navigator.showSaveBeforeNewImageDialog()
            setFirstCheckBoxInLayerMenu()
        }
    }

    override fun saveBeforeNewImage() {
        checkIfClippingToolNeedsAdjustment()
        navigator.showSaveImageInformationDialogWhenStandalone(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
            imageNumber,
            false
        )
    }

    private fun showSecurityQuestionBeforeExit() {
        if ((isImageUnchanged || model.isSaved) && (!model.isOpenedFromCatroid || !wasImageLoaded)) {
            finishActivity()
        } else if (model.isOpenedFromCatroid) {
            saveBeforeFinish()
        } else {
            navigator.showSaveBeforeFinishDialog()
        }
    }

    override fun finishActivity() {
        navigator.finishActivity()
    }

    override fun saveBeforeFinish() {
        checkIfClippingToolNeedsAdjustment()
        navigator.showSaveImageInformationDialogWhenStandalone(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
            imageNumber,
            false
        )
    }

    override fun saveCopyClicked(isExport: Boolean) {
        navigator.showSaveImageInformationDialogWhenStandalone(
            PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
            imageNumber,
            isExport
        )
    }

    override fun saveImageClicked() {
        navigator.showSaveImageInformationDialogWhenStandalone(
            PERMISSION_EXTERNAL_STORAGE_SAVE,
            imageNumber,
            false
        )
    }

    override fun shareImageClicked() {
        checkIfClippingToolNeedsAdjustment()
        view.refreshDrawingSurface()
        val bitmap: Bitmap? = workspace.bitmapOfAllLayers
        navigator.startShareImageActivity(bitmap)
    }

    private fun showLikeUsDialogIfFirstTimeSave() {
        val dialogHasBeenShown = sharedPreferences.preferenceLikeUsDialogValue
        if (!dialogHasBeenShown && !model.isOpenedFromCatroid) {
            navigator.showLikeUsDialog()
            sharedPreferences.setPreferenceLikeUsDialogValue()
        }
    }

    private fun countUpImageNumber() {
        var imageNumber = sharedPreferences.preferenceImageNumber
        imageNumber++
        sharedPreferences.preferenceImageNumber = imageNumber
    }

    override fun enterHideButtonsClicked() {
        model.isFullscreen = true
        enterHideButtons()
    }

    override fun exitHideButtonsClicked() {
        model.isFullscreen = false
        exitHideButtons()
    }

    override fun backToPocketCodeClicked() {
        showSecurityQuestionBeforeExit()
    }

    override fun showHelpClicked() {
        navigator.startWelcomeActivity(REQUEST_CODE_INTRO)
    }

    override fun showAboutClicked() {
        navigator.showAboutDialog()
    }

    override fun showZoomWindowSettingsClicked(sharedPreferences: UserPreferences) {
        navigator.showZoomWindowSettingsDialog(sharedPreferences)
    }

    override fun showAdvancedSettingsClicked() {
        navigator.showAdvancedSettingsDialog()
    }

    override fun showRateUsDialog() {
        navigator.showRateUsDialog()
    }

    override fun showFeedbackDialog() {
        navigator.showFeedbackDialog()
    }

    override fun showOverwriteDialog(permissionCode: Int, isExport: Boolean) {
        navigator.showOverwriteDialog(permissionCode, isExport)
    }

    override fun showPngInformationDialog() {
        navigator.showPngInformationDialog()
    }

    override fun showJpgInformationDialog() {
        navigator.showJpgInformationDialog()
    }

    override fun showOraInformationDialog() {
        navigator.showOraInformationDialog()
    }

    override fun showCatrobatInformationDialog() {
        navigator.showCatrobatInformationDialog()
    }

    override fun sendFeedback() {
        navigator.sendFeedback()
    }

    override fun onNewImage() {
        val metrics = view.displayMetrics
        resetPerspectiveAfterNextCommand = true
        model.savedPictureUri = null
        FileIO.filename = "image"
        FileIO.compressFormat = Bitmap.CompressFormat.PNG
        FileIO.fileType = FileIO.FileType.PNG
        FileIO.deleteTempFile(internalMemoryPath)
        val initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels)
        commandManager.setInitialStateCommand(initCommand)
        commandManager.reset()
    }

    override fun discardImageClicked() {
        commandManager.addCommand(commandFactory.createResetCommand())
    }

    fun switchBetweenVersions(@PermissionRequestCode requestCode: Int) {
        switchBetweenVersions(requestCode, false)
    }

    override fun switchBetweenVersions(@PermissionRequestCode requestCode: Int, isExport: Boolean) {
        this.isExport = isExport

        if (model.isOpenedFromCatroid) {
            FileIO.storeImageUri = model.savedPictureUri
        }

        if (navigator.isSdkAboveOrEqualM) {
            askForReadAndWriteExternalStoragePermission(requestCode)
            when (requestCode) {
                PERMISSION_REQUEST_CODE_REPLACE_PICTURE,
                PERMISSION_REQUEST_CODE_IMPORT_PICTURE -> Unit
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH,
                PERMISSION_EXTERNAL_STORAGE_SAVE_COPY,
                PERMISSION_EXTERNAL_STORAGE_SAVE -> checkForDefaultFilename()
            }
        } else {
            if (requestCode == PERMISSION_REQUEST_CODE_REPLACE_PICTURE) {
                if (isImageUnchanged || model.isSaved) {
                    navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE)
                    setFirstCheckBoxInLayerMenu()
                } else {
                    navigator.showSaveBeforeLoadImageDialog()
                    setFirstCheckBoxInLayerMenu()
                }
            } else {
                askForReadAndWriteExternalStoragePermission(requestCode)
            }
        }
    }

    private fun askForReadAndWriteExternalStoragePermission(@PermissionRequestCode requestCode: Int) {
        if (model.isOpenedFromCatroid && requestCode == PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH) {
            handleRequestPermissionsResult(
                requestCode,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                intArrayOf(PackageManager.PERMISSION_GRANTED)
            )
            return
        }

        @TargetApi(Build.VERSION_CODES.TIRAMISU)
        if (navigator.isSdkAboveOrEqualT) {
            if (!navigator.doIHavePermission(Manifest.permission.READ_MEDIA_IMAGES)) {
                navigator.askForPermission(
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        requestCode
                )
            } else {
                handleRequestPermissionsResult(
                        requestCode,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        intArrayOf(PackageManager.PERMISSION_GRANTED)
                )
            }
        } else if (navigator.isSdkAboveOrEqualQ) {
            if (!navigator.doIHavePermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                navigator.askForPermission(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    requestCode
                )
            } else {
                handleRequestPermissionsResult(
                    requestCode,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    intArrayOf(PackageManager.PERMISSION_GRANTED)
                )
            }
        } else {
            if (navigator.isSdkAboveOrEqualM && !navigator.doIHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                navigator.askForPermission(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode
                )
            } else {
                handleRequestPermissionsResult(
                    requestCode,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    intArrayOf(PackageManager.PERMISSION_GRANTED)
                )
            }
        }
    }

    private fun checkForDefaultFilename() {
        val standard = "image$imageNumber"
        if (FileIO.filename == standard) {
            countUpImageNumber()
        }
    }

    override fun handleActivityResult(
        @ActivityRequestCode requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val imageUri = data?.data
        when (requestCode) {
            REQUEST_CODE_IMPORT_PNG -> {
                if (resultCode != Activity.RESULT_OK) {
                    return
                }
                setTool(ToolType.IMPORTPNG)
                toolController.switchTool(ToolType.IMPORTPNG)
                interactor.loadFile(
                    this,
                    LOAD_IMAGE_IMPORT_PNG,
                    imageUri,
                    context,
                    false,
                    commandSerializer
                )
            }
            REQUEST_CODE_LOAD_PICTURE -> {
                if (resultCode != Activity.RESULT_OK) {
                    return
                }
                interactor.loadFile(
                    this,
                    LOAD_IMAGE_DEFAULT,
                    imageUri,
                    context,
                    false,
                    commandSerializer
                )
            }
            REQUEST_CODE_INTRO -> if (resultCode == RESULT_INTRO_MW_NOT_SUPPORTED) {
                navigator.showToast(
                    R.string.pocketpaint_intro_split_screen_not_supported,
                    Toast.LENGTH_LONG
                )
            }
            else -> view.superHandleActivityResult(requestCode, resultCode, data)
        }
    }

    override fun handleRequestPermissionsResult(
        @PermissionRequestCode requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (permissions.size == 1 && (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE ||
                        permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE ||
                        permissions[0] == Manifest.permission.READ_MEDIA_IMAGES)) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                when (requestCode) {
                    PERMISSION_EXTERNAL_STORAGE_SAVE -> {
                        saveImageConfirmClicked(
                            SAVE_IMAGE_DEFAULT,
                            FileIO.storeImageUri
                        )
                        checkForDefaultFilename()
                        showLikeUsDialogIfFirstTimeSave()
                    }
                    PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH -> {
                        saveImageConfirmClicked(
                            SAVE_IMAGE_FINISH,
                            FileIO.storeImageUri
                        )
                        checkForDefaultFilename()
                    }
                    PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW -> {
                        saveImageConfirmClicked(
                            SAVE_IMAGE_LOAD_NEW,
                            FileIO.storeImageUri
                        )
                        checkForDefaultFilename()
                    }
                    PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY -> {
                        saveImageConfirmClicked(
                            SAVE_IMAGE_NEW_EMPTY,
                            FileIO.storeImageUri
                        )
                        checkForDefaultFilename()
                    }
                    PERMISSION_EXTERNAL_STORAGE_SAVE_COPY -> {
                        saveCopyConfirmClicked(
                            SAVE_IMAGE_DEFAULT,
                            FileIO.storeImageUri
                        )
                        checkForDefaultFilename()
                    }
                    PERMISSION_REQUEST_CODE_REPLACE_PICTURE ->
                        if (isImageUnchanged || model.isSaved) {
                            navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE)
                        } else {
                            navigator.showSaveBeforeLoadImageDialog()
                        }
                    PERMISSION_REQUEST_CODE_IMPORT_PICTURE -> navigator.startImportImageActivity(
                        REQUEST_CODE_IMPORT_PNG
                    )
                    else -> view.superHandleRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults
                    )
                }
            } else {
                if (navigator.isPermissionPermanentlyDenied(permissions)) {
                    navigator.showRequestPermanentlyDeniedPermissionRationaleDialog()
                } else {
                    navigator.showRequestPermissionRationaleDialog(
                        PermissionInfoDialog.PermissionType.EXTERNAL_STORAGE,
                        permissions, requestCode
                    )
                }
            }
        } else {
            view.superHandleRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onBackPressed() {
        if (drawerLayoutViewHolder.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutViewHolder.closeDrawer(Gravity.START, true)
        } else if (drawerLayoutViewHolder.isDrawerOpen(GravityCompat.END)) {
            drawerLayoutViewHolder.closeDrawer(Gravity.END, true)
        } else if (model.isFullscreen) {
            exitHideButtonsClicked()
        } else if (!toolController.isDefaultTool) {
            if (toolController.currentTool?.toolType == ToolType.CLIP) toolController.adjustClippingToolOnBackPressed(true)
            switchTool(ToolType.BRUSH)
        } else {
            showSecurityQuestionBeforeExit()
        }
    }

    override fun saveImageConfirmClicked(requestCode: Int, uri: Uri?) {
        checkIfClippingToolNeedsAdjustment()
        view.refreshDrawingSurface()
        interactor.saveImage(this, requestCode, workspace.layerModel, commandSerializer, uri, context)
    }

    override fun saveCopyConfirmClicked(requestCode: Int, uri: Uri?) {
        checkIfClippingToolNeedsAdjustment()
        view.refreshDrawingSurface()
        interactor.saveCopy(this, requestCode, workspace.layerModel, commandSerializer, uri, context)
    }

    override fun undoClicked() {
        idlingResource.increment()
        if (view.isKeyboardShown) {
            view.hideKeyboard()
        } else {
            if (toolController.currentTool !is EraserTool && (commandManager.isLastColorCommandOnTop() || commandManager.getColorCommandCount() == 0)) {
                toolController.currentTool?.changePaintColor(Color.BLACK)
                setBottomNavigationColor(Color.BLACK)
            }
            if (toolController.currentTool is ClippingTool) {
                val clippingTool = toolController.currentTool as ClippingTool
                clippingToolPaint = clippingTool.drawPaint
                commandManager.undo()
                clippingToolInUseAndUndoRedoClicked = true
            } else {
                if (toolController.currentTool is LineTool) {
                    (toolController.currentTool as LineTool).undoChangePaintColor(Color.BLACK, false)
                } else {
                    if (toolController.currentTool is EraserTool) {
                        commandManager.undoIgnoringColorChanges()
                    } else {
                        commandManager.undo()
                    }
                }
            }
        }
        idlingResource.decrement()
    }

    override fun redoClicked() {
        idlingResource.increment()
        if (view.isKeyboardShown) {
            view.hideKeyboard()
        } else {
            if (toolController.currentTool is LineTool) {
                (toolController.currentTool as LineTool).redoLineTool()
            } else {
                commandManager.redo()
                if (toolController.currentTool is ClippingTool) {
                    clippingToolInUseAndUndoRedoClicked = true
                }
            }
        }
        idlingResource.decrement()
    }

    override fun showColorPickerClicked() {
        navigator.showColorPickerDialog()
    }

    override fun showLayerMenuClicked() {
        idlingResource.increment()
        layerAdapter?.apply {
            for (position in 0 until itemCount) {
                val currentHolder = getViewHolderAt(position)
                currentHolder?.let {
                    val layer = presenter.getLayerItem(position)
                    if (it.bitmap != null && layer != null) {
                        it.updateImageView(layer)
                    }
                }
            }
        }
        checkIfClippingToolNeedsAdjustment()
        drawerLayoutViewHolder.openDrawer(Gravity.END)
        idlingResource.decrement()
    }

    override fun onCommandPostExecute() {
        navigator.dismissIndeterminateProgressDialog()
        if (resetPerspectiveAfterNextCommand) {
            resetPerspectiveAfterNextCommand = false
            workspace.resetPerspective()
        }
        model.isSaved = false
        if (clippingToolInUseAndUndoRedoClicked) {
            adjustClippingToolPostCommandExecute()
        }
        toolController.resetToolInternalState()
        view.refreshDrawingSurface()
        refreshTopBarButtons()
    }

    fun adjustClippingToolPostCommandExecute() {
        val clippingTool = toolController.currentTool as ClippingTool
        if (clippingTool.areaClosed) {
            commandManager.popFirstCommandInRedo()
        }
        clippingTool.areaClosed = false
        clippingTool.pathToDraw.rewind()
        clippingTool.pointArray.clear()
        clippingTool.initialEventCoordinate = null
        clippingTool.previousEventCoordinate = null
        clippingTool.changePaintColor(clippingToolPaint.color)
        clippingTool.mainActivity.bottomNavigationViewHolder
            .setColorButtonColor(clippingToolPaint.color)
        (toolController.currentTool as ClippingTool).wasRecentlyApplied = true
        clippingToolInUseAndUndoRedoClicked = false
    }

    override fun setBottomNavigationColor(color: Int) {
        bottomNavigationViewHolder.setColorButtonColor(color)
    }

    override fun initializeFromCleanState(extraPicturePath: String?, extraPictureName: String?) {
        model.isOpenedFromCatroid = extraPicturePath != null
        wasImageLoaded = false
        if (extraPicturePath != null) {
            val imageFile = File(extraPicturePath)
            if (imageFile.exists()) {
                model.savedPictureUri = view.getUriFromFile(imageFile)
                interactor.loadFile(
                    this,
                    LOAD_IMAGE_CATROID,
                    model.savedPictureUri,
                    context,
                    false,
                    commandSerializer
                )
            } else if (extraPictureName != null) {
                interactor.createFile(
                    this,
                    CREATE_FILE_DEFAULT,
                    extraPictureName
                )
            }
        } else {
            toolController.resetToolInternalStateOnImageLoaded()
            model.savedPictureUri = null
        }
    }

    override fun finishInitialize() {
        refreshTopBarButtons()
        toolController.toolColor?.let { bottomNavigationViewHolder.setColorButtonColor(it) }
        bottomNavigationViewHolder.showCurrentTool(toolController.toolType)
        if (model.isFullscreen) {
            enterHideButtons()
        } else {
            exitHideButtons()
        }
        view.initializeActionBar(model.isOpenedFromCatroid)
        if (commandManager.isBusy) {
            navigator.showIndeterminateProgressDialog()
        }
    }

    override fun removeMoreOptionsItems(menu: Menu?) {
        if (model.isOpenedFromCatroid) {
            topBarViewHolder.removeStandaloneMenuItems(menu)
            topBarViewHolder.hideTitleIfNotStandalone()
        } else {
            topBarViewHolder.removeCatroidMenuItems(menu)
        }
    }

    private fun exitHideButtons() {
        view.exitHideButtons()
        topBarViewHolder.show()
        bottomNavigationViewHolder.show()
        toolController.enableToolOptionsView()
        toolController.enableHideOption()
        if (toolOptionsViewWasShown) {
            toolController.showToolOptionsView()
            toolOptionsViewWasShown = false
        }
    }

    private fun enterHideButtons() {
        if (toolController.toolOptionsViewVisible()) {
            toolOptionsViewWasShown = true
            toolController.hideToolOptionsViewForShapeTools()
        }
        view.hideKeyboard()
        view.enterHideButtons()
        topBarViewHolder.hide()
        bottomBarViewHolder.hide()
        bottomNavigationViewHolder.hide()
        toolController.disableToolOptionsView()
        toolController.disableHideOption()
    }

    override fun restoreState(
        isFullscreen: Boolean,
        isSaved: Boolean,
        isOpenedFromCatroid: Boolean,
        isOpenedFromFormulaEditorInCatroid: Boolean,
        savedPictureUri: Uri?,
        cameraImageUri: Uri?
    ) {
        model.isFullscreen = isFullscreen
        model.isSaved = isSaved
        model.isOpenedFromCatroid = isOpenedFromCatroid
        model.isOpenedFromFormulaEditorInCatroid = isOpenedFromFormulaEditorInCatroid
        model.savedPictureUri = savedPictureUri
        model.cameraImageUri = cameraImageUri
        navigator.restoreFragmentListeners()
        toolController.resetToolInternalStateOnImageLoaded()
        if (model.isFullscreen) {
            toolController.adaptLayoutForFullScreen()
        }
    }

    override fun onCreateTool() {
        toolController.createTool()
    }

    private fun refreshTopBarButtons() {
        if (commandManager.isUndoAvailable) {
            topBarViewHolder.enableUndoButton()
        } else {
            topBarViewHolder.disableUndoButton()
        }
        if (commandManager.isRedoAvailable) {
            topBarViewHolder.enableRedoButton()
        } else {
            topBarViewHolder.disableRedoButton()
        }
    }

    override fun toolClicked(toolType: ToolType) {
        idlingResource.increment()
        bottomBarViewHolder.hide()
        if (toolController.toolType === toolType && toolController.hasToolOptionsView()) {
            toolController.toggleToolOptionsView()
        } else {
            if (autoSave) checkForImplicitToolApplication()
            switchTool(toolType)
        }
        idlingResource.decrement()
    }

    private fun checkForImplicitToolApplication() {
        val currentTool = toolController.currentTool
        val currentToolType = currentTool?.toolType
        if (toolController.toolsThatUseCheckmark.contains(currentToolType) || currentTool is ClipboardTool) {
            val toolToApply = currentTool as BaseToolWithShape
            toolToApply.onClickOnButton()
        } else if (currentToolType == ToolType.CLIP) (currentTool as ClippingTool).onClickOnButton()
    }

    private fun switchTool(type: ToolType) {
        navigator.setMaskFilterToNull()
        view.hideKeyboard()
        downTimer = object :
            CountDownTimer(
                if (toolController.toolsThatUseCheckmark.contains(toolController.currentTool?.toolType)) CLICK_TIMEOUT_MILLIS else 0L,
                CLICK_TIMEOUT_MILLIS / CONSTANT_3
            ) {
            override fun onTick(millisUntilFinished: Long) {
                workspace.invalidate()
            }
            override fun onFinish() {
                downTimer?.cancel()
                workspace.invalidate()
                setTool(type)
                toolController.switchTool(type)
                if (type === ToolType.IMPORTPNG) {
                    showImportDialog()
                } else if (type == ToolType.CLIP) {
                    (toolController.currentTool as ClippingTool).copyBitmapOfCurrentLayer()
                }
            }
        }.start()
    }

    private fun setTool(toolType: ToolType) {
        idlingResource.increment()
        bottomBarViewHolder.hide()
        bottomNavigationViewHolder.showCurrentTool(toolType)
        val offset = topBarViewHolder.height
        navigator.showToolChangeToast(offset, toolType.nameResource)
        idlingResource.decrement()
    }

    override fun onCreateFilePostExecute(@CreateFileRequestCode requestCode: Int, file: File?) {
        if (file == null) {
            navigator.showSaveErrorDialog()
            return
        }
        if (requestCode == CREATE_FILE_DEFAULT) {
            model.savedPictureUri = view.getUriFromFile(file)
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun loadScaledImage(uri: Uri?, @LoadImageRequestCode requestCode: Int) {
        when (requestCode) {
            LOAD_IMAGE_IMPORT_PNG -> {
                setTool(ToolType.IMPORTPNG)
                toolController.switchTool(ToolType.IMPORTPNG)
                interactor.loadFile(
                    this,
                    LOAD_IMAGE_IMPORT_PNG,
                    uri,
                    context,
                    true,
                    commandSerializer
                )
            }
            LOAD_IMAGE_CATROID, LOAD_IMAGE_DEFAULT -> interactor.loadFile(
                this,
                LOAD_IMAGE_DEFAULT,
                uri,
                context,
                true,
                commandSerializer
            )
            else -> Log.e(MainActivity.TAG, "wrong request code for loading pictures")
        }
    }

    override fun onLoadImagePostExecute(
        @LoadImageRequestCode requestCode: Int,
        uri: Uri?,
        result: BitmapReturnValue?
    ) {
        if (result == null) {
            navigator.showLoadErrorDialog()
            return
        }
        if (result.model != null) {
            commandManager.loadCommandsCatrobatImage(result.model)
            resetPerspectiveAfterNextCommand = true
            setColorHistoryAfterLoadImage(result.colorHistory)
            FileIO.fileType = FileIO.FileType.CATROBAT
            if (uri != null) {
                val name = getFileName(uri)
                if (name != null) {
                    FileIO.filename = name.substring(0, name.length - FileIO.fileType.toExtension().length)
                }
            }
            return
        }
        if (result.toBeScaled) {
            navigator.showScaleImageRequestDialog(uri, requestCode)
            return
        }
        when (requestCode) {
            LOAD_IMAGE_DEFAULT -> {
                resetPerspectiveAfterNextCommand = true
                if (result.bitmap != null) {
                    result.bitmap?.let {
                        commandManager.setInitialStateCommand(commandFactory.createInitCommand(it))
                    }
                } else {
                    result.layerList?.let {
                        commandManager.setInitialStateCommand(commandFactory.createInitCommand(it))
                    }
                }
                commandManager.reset()
                if (!model.isOpenedFromCatroid) {
                    model.savedPictureUri = null
                }
                model.cameraImageUri = null
                wasImageLoaded = true
                if (uri != null) {
                    val name = getFileName(uri)
                    if (name != null) {
                        if (name.endsWith(FileIO.FileType.JPG.value) || name.endsWith("jpeg")) {
                            FileIO.compressFormat = Bitmap.CompressFormat.JPEG
                            FileIO.fileType = FileIO.FileType.JPG
                        } else if (name.endsWith(FileIO.FileType.PNG.value)) {
                            FileIO.compressFormat = Bitmap.CompressFormat.PNG
                            FileIO.fileType = FileIO.FileType.PNG
                        } else {
                            FileIO.fileType = FileIO.FileType.ORA
                        }
                        FileIO.filename = name.substring(0, name.length - FileIO.fileType.toExtension().length)
                    }
                }
            }
            LOAD_IMAGE_IMPORT_PNG ->
                if (toolController.toolType === ToolType.IMPORTPNG) {
                    toolController.setBitmapFromSource(result.bitmap)
                } else {
                    Log.e(
                        MainActivity.TAG,
                        "importPngToFloatingBox: Current tool is no ImportTool as required"
                    )
                }
            LOAD_IMAGE_CATROID -> {
                resetPerspectiveAfterNextCommand = true
                result.bitmap?.let {
                    commandManager.setInitialStateCommand(commandFactory.createInitCommand(it))
                }
                commandManager.reset()
                model.savedPictureUri = uri
                model.cameraImageUri = null
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onLoadImagePreExecute(@LoadImageRequestCode requestCode: Int) = Unit

    override fun onSaveImagePreExecute(@SaveImageRequestCode requestCode: Int) {
        view.showContentLoadingProgressBar()
    }

    override fun onSaveImagePostExecute(
        @SaveImageRequestCode requestCode: Int,
        uri: Uri?,
        saveAsCopy: Boolean
    ) {
        view.hideContentLoadingProgressBar()
        if (uri == null) {
            navigator.showSaveErrorDialog()
            return
        }
        if (saveAsCopy) {
            if (model.isOpenedFromCatroid && !isExport) {
                navigator.showToast(R.string.copy, Toast.LENGTH_LONG)
            } else {
                var msg: String? = context.getString(R.string.copy_to)
                fileActivity?.let {
                    msg += getPathFromUri(it, uri)
                }
                navigator.showToast(msg ?: "null", Toast.LENGTH_LONG)
            }
        } else {
            if (model.isOpenedFromCatroid && !isExport) {
                navigator.showToast(R.string.saved, Toast.LENGTH_LONG)
            } else {
                var msg: String? = context.getString(R.string.saved_to)
                fileActivity?.let {
                    msg += getPathFromUri(it, uri)
                }
                navigator.showToast(msg ?: "null", Toast.LENGTH_LONG)
            }
            model.savedPictureUri = uri
            model.isSaved = true
        }
        if (!model.isOpenedFromCatroid || saveAsCopy) {
            navigator.broadcastAddPictureToGallery(uri)
        }
        when (requestCode) {
            SAVE_IMAGE_NEW_EMPTY -> onNewImage()
            SAVE_IMAGE_DEFAULT -> {
            }
            SAVE_IMAGE_FINISH -> {
                if (model.isOpenedFromCatroid) {
                    navigator.returnToPocketCode(uri.path)
                } else {
                    navigator.finishActivity()
                }
                return
            }
            SAVE_IMAGE_LOAD_NEW -> navigator.startLoadImageActivity(
                REQUEST_CODE_LOAD_PICTURE
            )
            else -> throw IllegalArgumentException()
        }
    }

    override fun actionToolsClicked() {
        if (toolController.toolOptionsViewVisible()) {
            toolController.hideToolOptionsView()
        }
        if (bottomBarViewHolder.isVisible) {
            bottomBarViewHolder.hide()
        } else {
            val layer = layerAdapter?.presenter?.getLayerItem(workspace.currentLayerIndex)
            if (layer != null && !layer.isVisible) {
                navigator.showToast(R.string.no_tools_on_hidden_layer, Toast.LENGTH_SHORT)
                return
            }
            bottomBarViewHolder.show()
        }
    }

    override fun actionCurrentToolClicked() {
        if (toolController.toolType === ToolType.IMPORTPNG) {
            showImportDialog()
            return
        }
        if (bottomBarViewHolder.isVisible) {
            bottomBarViewHolder.hide()
        }
        if (toolController.toolOptionsViewVisible()) {
            toolController.hideToolOptionsView()
        } else {
            if (toolController.hasToolOptionsView()) {
                toolController.showToolOptionsView()
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = fileActivity?.contentResolver?.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        return result
    }

    override fun rateUsClicked() {
        navigator.rateUsClicked()
    }

    override fun setLayerAdapter(layerAdapter: LayerAdapter) {
        this.layerAdapter = layerAdapter
    }

    override fun importFromGalleryClicked() {
        switchBetweenVersions(PERMISSION_REQUEST_CODE_IMPORT_PICTURE)
    }

    override fun showImportDialog() {
        navigator.showImageImportDialog()
    }

    override fun importStickersClicked() {
        navigator.showCatroidMediaGallery()
        if (!checkForInternet(context)) {
            Toast.makeText(context, context.getString(R.string.no_connection_sticker), Toast.LENGTH_LONG).show()
        }
    }

    override fun bitmapLoadedFromSource(loadedImage: Bitmap) {
        toolController.setBitmapFromSource(loadedImage)
    }

    override fun setAntialiasingOnOkClicked() {
        navigator.setAntialiasingOnToolPaint()
    }

    override fun saveNewTemporaryImage() {
        FileIO.saveTemporaryPictureFile(internalMemoryPath, commandSerializer)
    }

    override fun openTemporaryFile(): WorkspaceReturnValue? =
        FileIO.openTemporaryPictureFile(commandSerializer)

    override fun checkForTemporaryFile(): Boolean =
        FileIO.checkForTemporaryFile(internalMemoryPath)

    override fun setColorHistoryAfterLoadImage(colorHistory: ColorHistory?) {
        var history = colorHistory
        history = history ?: ColorHistory()
        model.colorHistory = history
        var newPaintColor: Int = DefaultToolPaint(context).color
        if (history.colors.isNotEmpty()) {
            newPaintColor = history.colors.last()
        }
        toolController.currentTool?.changePaintColor(newPaintColor)
        setBottomNavigationColor(newPaintColor)
    }

    fun checkIfClippingToolNeedsAdjustment() {
        if (toolController.currentTool is ClippingTool) {
            val clippingTool = toolController.currentTool as ClippingTool
            if (clippingTool.areaClosed) {
                clippingTool.handleDown(
                    clippingTool.initialEventCoordinate?.x?.let {
                        clippingTool.initialEventCoordinate?.y?.let { it1 ->
                            PointF(
                                it,
                                it1
                            )
                        }
                    }
                )
                (toolController.currentTool as ClippingTool).wasRecentlyApplied = true
                clippingTool.resetInternalState(Tool.StateChange.NEW_IMAGE_LOADED)
            } else {
                (toolController.currentTool as ClippingTool).wasRecentlyApplied = true
                clippingTool.resetInternalState(Tool.StateChange.NEW_IMAGE_LOADED)
            }
        }
    }

    fun hideBottomBarViewHolder() {
        if (bottomBarViewHolder.isVisible) {
            bottomBarViewHolder.hide()
        }
    }

    companion object {
        var autoSave = false

        @JvmStatic
        fun getPathFromUri(context: Context, uri: Uri): String {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        id.toLong()
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val contentUri: Uri? = when (split[0]) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return if (isGooglePhotosUri(uri)) {
                    uri.lastPathSegment.toString()
                } else getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path.toString()
            }
            return ""
        }

        @SuppressWarnings("SwallowedException")
        private fun getDataColumn(
            context: Context,
            uri: Uri?,
            selection: String?,
            selectionArgs: Array<String>?
        ): String {
            uri ?: return ""
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor =
                    context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } catch (e: IllegalArgumentException) {
                val file = File(context.cacheDir, TEMP_PICTURE_NAME)
                FileIO.saveFileFromUri(uri, file, context)
                return file.absolutePath
            } finally {
                cursor?.close()
            }
            return ""
        }

        private fun checkForInternet(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } else {
                @Suppress("DEPRECATION") val networkInfo =
                        connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION")
                return networkInfo.isConnected
            }
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean =
            "com.android.externalstorage.documents" == uri.authority

        private fun isDownloadsDocument(uri: Uri): Boolean =
            "com.android.providers.downloads.documents" == uri.authority

        private fun isMediaDocument(uri: Uri): Boolean =
            "com.android.providers.media.documents" == uri.authority

        private fun isGooglePhotosUri(uri: Uri): Boolean =
            "com.google.android.apps.photos.content" == uri.authority
    }
}
