/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.widget.Toast
import androidx.core.view.GravityCompat
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.UserPreferences
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
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
import org.catrobat.paintroid.common.PERMISSION_REQUEST_CODE_LOAD_PICTURE
import org.catrobat.paintroid.common.REQUEST_CODE_IMPORT_PNG
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
import org.catrobat.paintroid.iotasks.CreateFile.CreateFileCallback
import org.catrobat.paintroid.iotasks.LoadImage.LoadImageCallback
import org.catrobat.paintroid.iotasks.SaveImage.SaveImageCallback
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.ui.LayerAdapter
import org.catrobat.paintroid.ui.Perspective
import java.io.File
import java.lang.IllegalArgumentException

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
    private val perspective: Perspective,
    private val toolController: ToolController,
    private val sharedPreferences: UserPreferences,
    override val context: Context,
) : MainActivityContracts.Presenter, SaveImageCallback, LoadImageCallback, CreateFileCallback {
    private var layerAdapter: LayerAdapter? = null
    private var resetPerspectiveAfterNextCommand = false
    private var isExport = false
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

    override fun loadImageClicked() {
        switchBetweenVersions(PERMISSION_REQUEST_CODE_LOAD_PICTURE)
        setFirstCheckBoxInLayerMenu()
    }

    private fun setFirstCheckBoxInLayerMenu() {
        layerAdapter?.getViewHolderAt(0)?.apply { setCheckBox(true) }
    }

    override fun saveBeforeLoadImage() {
        navigator.showSaveImageInformationDialogWhenStandalone(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW,
            imageNumber,
            false
        )
    }

    override fun loadNewImage() {
        navigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE)
        setFirstCheckBoxInLayerMenu()
    }

    override fun newImageClicked() {
        if (isImageUnchanged || model.isSaved) {
            onNewImage()
            setFirstCheckBoxInLayerMenu()
        } else {
            navigator.showSaveBeforeNewImageDialog()
            setFirstCheckBoxInLayerMenu()
        }
    }

    override fun saveBeforeNewImage() {
        navigator.showSaveImageInformationDialogWhenStandalone(
            PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY,
            imageNumber,
            false
        )
    }

    private fun showSecurityQuestionBeforeExit() {
        if ((isImageUnchanged || model.isSaved) && (!model.isOpenedFromCatroid || !FileIO.wasImageLoaded)) {
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

    override fun enterFullscreenClicked() {
        model.isFullscreen = true
        enterFullscreen()
    }

    override fun exitFullscreenClicked() {
        model.isFullscreen = false
        exitFullscreen()
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
        FileIO.uriFileJpg = null
        FileIO.uriFilePng = null
        FileIO.currentFileNameJpg = null
        FileIO.currentFileNamePng = null
        FileIO.compressFormat = Bitmap.CompressFormat.PNG
        FileIO.ending = ".png"
        FileIO.isCatrobatImage = false
        val initCommand =
            commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels)
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
        if (navigator.isSdkAboveOrEqualM) {
            askForReadAndWriteExternalStoragePermission(requestCode)
            when (requestCode) {
                PERMISSION_REQUEST_CODE_LOAD_PICTURE -> {
                }
                PERMISSION_REQUEST_CODE_IMPORT_PICTURE -> {
                }
                PERMISSION_EXTERNAL_STORAGE_SAVE -> {
                    checkForDefaultFilename()
                    showLikeUsDialogIfFirstTimeSave()
                }
                PERMISSION_EXTERNAL_STORAGE_SAVE_COPY -> checkForDefaultFilename()
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW -> checkForDefaultFilename()
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY -> checkForDefaultFilename()
                PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH -> checkForDefaultFilename()
            }
        } else {
            if (requestCode == PERMISSION_REQUEST_CODE_LOAD_PICTURE) {
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
        if (navigator.isSdkAboveOrEqualQ) {
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
                toolController.switchTool(ToolType.IMPORTPNG, false)
                interactor.loadFile(
                    this,
                    LOAD_IMAGE_IMPORT_PNG,
                    imageUri,
                    context,
                    false,
                    workspace
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
                    workspace
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
        if (permissions.size == 1 && (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE || permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                when (requestCode) {
                    PERMISSION_EXTERNAL_STORAGE_SAVE -> {
                        saveImageConfirmClicked(
                            SAVE_IMAGE_DEFAULT,
                            model.savedPictureUri
                        )
                        checkForDefaultFilename()
                        showLikeUsDialogIfFirstTimeSave()
                    }
                    PERMISSION_EXTERNAL_STORAGE_SAVE_COPY -> {
                        saveCopyConfirmClicked(SAVE_IMAGE_DEFAULT)
                        checkForDefaultFilename()
                    }
                    PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_FINISH -> {
                        saveImageConfirmClicked(
                            SAVE_IMAGE_FINISH,
                            model.savedPictureUri
                        )
                        checkForDefaultFilename()
                    }
                    PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_LOAD_NEW -> {
                        saveImageConfirmClicked(
                            SAVE_IMAGE_LOAD_NEW,
                            model.savedPictureUri
                        )
                        checkForDefaultFilename()
                    }
                    PERMISSION_EXTERNAL_STORAGE_SAVE_CONFIRMED_NEW_EMPTY -> {
                        saveImageConfirmClicked(
                            SAVE_IMAGE_NEW_EMPTY,
                            model.savedPictureUri
                        )
                        checkForDefaultFilename()
                    }
                    PERMISSION_REQUEST_CODE_LOAD_PICTURE ->
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
            exitFullscreenClicked()
        } else if (!toolController.isDefaultTool) {
            setTool(ToolType.BRUSH)
            toolController.switchTool(ToolType.BRUSH, true)
        } else {
            showSecurityQuestionBeforeExit()
        }
    }

    override fun saveImageConfirmClicked(requestCode: Int, uri: Uri?) {
        interactor.saveImage(this, requestCode, workspace, uri, context)
    }

    override fun saveCopyConfirmClicked(requestCode: Int) {
        interactor.saveCopy(this, requestCode, workspace, context)
    }

    override fun undoClicked() {
        if (view.isKeyboardShown) {
            view.hideKeyboard()
        } else {
            commandManager.undo()
        }
    }

    override fun redoClicked() {
        if (view.isKeyboardShown) {
            view.hideKeyboard()
        } else {
            commandManager.redo()
        }
    }

    override fun showColorPickerClicked() {
        navigator.showColorPickerDialog()
    }

    override fun showLayerMenuClicked() {
        layerAdapter?.apply {
            setDrawerLayoutOpen(true)
            for (i in 0 until count) {
                val currentHolder = getViewHolderAt(i)
                currentHolder?.let {
                    if (it.bitmap != null) {
                        it.updateImageView(it.bitmap, true)
                    }
                }
            }
        }
        drawerLayoutViewHolder.openDrawer(Gravity.END)
    }

    override fun onCommandPostExecute() {
        navigator.dismissIndeterminateProgressDialog()
        if (resetPerspectiveAfterNextCommand) {
            resetPerspectiveAfterNextCommand = false
            workspace.resetPerspective()
        }
        model.isSaved = false
        toolController.resetToolInternalState()
        view.refreshDrawingSurface()
        refreshTopBarButtons()
    }

    override fun setBottomNavigationColor(color: Int) {
        bottomNavigationViewHolder.setColorButtonColor(color)
    }

    override fun initializeFromCleanState(extraPicturePath: String?, extraPictureName: String?) {
        model.isOpenedFromCatroid = extraPicturePath != null
        FileIO.wasImageLoaded = false
        if (extraPictureName != null) {
            val imageFile = extraPicturePath?.let { File(it) }
            if (imageFile != null && imageFile.exists()) {
                model.savedPictureUri = view.getUriFromFile(imageFile)
                interactor.loadFile(
                    this,
                    LOAD_IMAGE_CATROID,
                    model.savedPictureUri,
                    context,
                    false,
                    workspace
                )
            } else {
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
            enterFullscreen()
        } else {
            exitFullscreen()
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

    private fun exitFullscreen() {
        view.exitFullscreen()
        topBarViewHolder.show()
        bottomNavigationViewHolder.show()
        toolController.enableToolOptionsView()
        perspective.exitFullscreen()
    }

    private fun enterFullscreen() {
        view.hideKeyboard()
        view.enterFullscreen()
        topBarViewHolder.hide()
        bottomBarViewHolder.hide()
        bottomNavigationViewHolder.hide()
        toolController.disableToolOptionsView()
        perspective.enterFullscreen()
    }

    override fun restoreState(
        isFullscreen: Boolean,
        isSaved: Boolean,
        isOpenedFromCatroid: Boolean,
        wasInitialAnimationPlayed: Boolean,
        savedPictureUri: Uri?,
        cameraImageUri: Uri?
    ) {
        model.isFullscreen = isFullscreen
        model.isSaved = isSaved
        model.isOpenedFromCatroid = isOpenedFromCatroid
        model.setInitialAnimationPlayed(wasInitialAnimationPlayed)
        model.savedPictureUri = savedPictureUri
        model.cameraImageUri = cameraImageUri
        navigator.restoreFragmentListeners()
        toolController.resetToolInternalStateOnImageLoaded()
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
        bottomBarViewHolder.hide()
        if (toolController.toolType === toolType && toolController.hasToolOptionsView()) {
            toolController.toggleToolOptionsView()
        } else if (view.isKeyboardShown) {
            view.hideKeyboard()
        } else {
            switchTool(toolType)
        }
    }

    private fun switchTool(type: ToolType) {
        setTool(type)
        toolController.switchTool(type, false)
        if (type === ToolType.IMPORTPNG) {
            showImportDialog()
        }
    }

    private fun setTool(toolType: ToolType) {
        bottomBarViewHolder.hide()
        bottomNavigationViewHolder.showCurrentTool(toolType)
        val offset = topBarViewHolder.height
        navigator.showToolChangeToast(offset, toolType.nameResource)
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
                toolController.switchTool(ToolType.IMPORTPNG, false)
                interactor.loadFile(
                    this,
                    LOAD_IMAGE_IMPORT_PNG,
                    uri,
                    context,
                    true,
                    workspace
                )
            }
            LOAD_IMAGE_CATROID, LOAD_IMAGE_DEFAULT -> interactor.loadFile(
                this,
                LOAD_IMAGE_DEFAULT,
                uri,
                context,
                true,
                workspace
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
                    result.bitmapList?.let {
                        commandManager.setInitialStateCommand(commandFactory.createInitCommand(it))
                    }
                }
                commandManager.reset()
                if (!model.isOpenedFromCatroid) {
                    model.savedPictureUri = null
                }
                model.cameraImageUri = null
                FileIO.wasImageLoaded = true
                if (uri != null) {
                    val name = getFileName(uri)
                    if (name != null) {
                        if (name.endsWith("jpg") || name.endsWith("jpeg")) {
                            FileIO.compressFormat = Bitmap.CompressFormat.JPEG
                            FileIO.ending = ".jpg"
                            FileIO.isCatrobatImage = false
                        } else if (name.endsWith("png")) {
                            FileIO.compressFormat = Bitmap.CompressFormat.PNG
                            FileIO.ending = ".png"
                            FileIO.isCatrobatImage = false
                        } else {
                            FileIO.ending = ".ora"
                            FileIO.isCatrobatImage = true
                        }
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
            if (!layerAdapter!!.presenter.getLayerItem(workspace.currentLayerIndex).checkBox) {
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
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
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
    }

    override fun bitmapLoadedFromSource(loadedImage: Bitmap) {
        toolController.setBitmapFromSource(loadedImage)
    }

    override fun setAntialiasingOnOkClicked() {
        navigator.setAntialiasingOnToolPaint()
    }

    companion object {
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
                val file = File(context.cacheDir, "tmp")
                FileIO.saveFileFromUri(uri, file, context)
                return file.absolutePath
            } finally {
                cursor?.close()
            }
            return ""
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
