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
package org.catrobat.paintroid.contract

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.DisplayMetrics
import android.view.Menu
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode
import org.catrobat.paintroid.dialog.PermissionInfoDialog.PermissionType
import org.catrobat.paintroid.iotasks.CreateFileAsync.CreateFileCallback
import org.catrobat.paintroid.iotasks.LoadImageAsync.LoadImageCallback
import org.catrobat.paintroid.iotasks.SaveImageAsync.SaveImageCallback
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.ui.LayerAdapter
import java.io.File

interface MainActivityContracts {
    interface Navigator {
        val isSdkAboveOrEqualM: Boolean
        val isSdkAboveOrEqualQ: Boolean
        fun showColorPickerDialog()
        fun startLoadImageActivity(@ActivityRequestCode requestCode: Int)
        fun startImportImageActivity(@ActivityRequestCode requestCode: Int)
        fun showAboutDialog()
        fun showLikeUsDialog()
        fun showRateUsDialog()
        fun showFeedbackDialog()
        fun showOverwriteDialog(permissionCode: Int, isExport: Boolean)
        fun showPngInformationDialog()
        fun showJpgInformationDialog()
        fun showOraInformationDialog()
        fun sendFeedback()
        fun startWelcomeActivity(@ActivityRequestCode requestCode: Int)
        fun startShareImageActivity(bitmap: Bitmap?)
        fun showIndeterminateProgressDialog()
        fun dismissIndeterminateProgressDialog()
        fun returnToPocketCode(path: String?)
        fun showToast(msg: String, duration: Int)
        fun showToast(@StringRes resId: Int, duration: Int)
        fun showSaveErrorDialog()
        fun showLoadErrorDialog()
        fun showRequestPermissionRationaleDialog(permissionType: PermissionType, permissions: Array<String>, requestCode: Int)
        fun showRequestPermanentlyDeniedPermissionRationaleDialog()
        fun askForPermission(permissions: Array<String>, requestCode: Int)
        fun doIHavePermission(permission: String): Boolean
        fun isPermissionPermanentlyDenied(permission: Array<String>): Boolean
        fun finishActivity()
        fun showSaveBeforeFinishDialog()
        fun showSaveBeforeNewImageDialog()
        fun showSaveBeforeLoadImageDialog()
        fun showSaveImageInformationDialogWhenStandalone(permissionCode: Int, imageNumber: Int, isExport: Boolean)
        fun restoreFragmentListeners()
        fun showToolChangeToast(offset: Int, idRes: Int)
        fun broadcastAddPictureToGallery(uri: Uri?)
        fun rateUsClicked()
        fun showImageImportDialog()
        fun showCatroidMediaGallery()
        fun showScaleImageRequestDialog(uri: Uri?, requestCode: Int)
    }

    interface MainView {
        val finishing: Boolean
        val isKeyboardShown: Boolean
        val myContentResolver: ContentResolver
        val displayMetrics: DisplayMetrics
        val presenter: Presenter
        fun initializeActionBar(isOpenedFromCatroid: Boolean)
        fun superHandleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun superHandleRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
        fun getUriFromFile(file: File): Uri
        fun hideKeyboard()
        fun refreshDrawingSurface()
        fun enterFullscreen()
        fun exitFullscreen()
    }

    interface Presenter {
        val imageNumber: Int
        val bitmap: Bitmap
        val context: Context
        fun initializeFromCleanState(extraPicturePath: String?, extraPictureName: String?)
        fun restoreState(
            isFullscreen: Boolean,
            isSaved: Boolean,
            isOpenedFromCatroid: Boolean,
            wasInitialAnimationPlayed: Boolean,
            savedPictureUri: Uri?,
            cameraImageUri: Uri?
        )

        fun finishInitialize()
        fun removeMoreOptionsItems(menu: Menu?)
        fun loadImageClicked()
        fun loadNewImage()
        fun newImageClicked()
        fun discardImageClicked()
        fun saveCopyClicked(isExport: Boolean)
        fun saveImageClicked()
        fun shareImageClicked()
        fun enterFullscreenClicked()
        fun exitFullscreenClicked()
        fun backToPocketCodeClicked()
        fun showHelpClicked()
        fun showAboutClicked()
        fun showRateUsDialog()
        fun showFeedbackDialog()
        fun showOverwriteDialog(permissionCode: Int, isExport: Boolean)
        fun showPngInformationDialog()
        fun showJpgInformationDialog()
        fun showOraInformationDialog()
        fun sendFeedback()
        fun onNewImage()
        fun switchBetweenVersions(requestCode: Int, isExport: Boolean)
        fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun handleRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
        fun onBackPressed()
        fun saveImageConfirmClicked(requestCode: Int, uri: Uri?)
        fun saveCopyConfirmClicked(requestCode: Int)
        fun undoClicked()
        fun redoClicked()
        fun showColorPickerClicked()
        fun showLayerMenuClicked()
        fun onCommandPostExecute()
        fun setBottomNavigationColor(color: Int)
        fun onCreateTool()
        fun toolClicked(toolType: ToolType)
        fun saveBeforeLoadImage()
        fun saveBeforeNewImage()
        fun saveBeforeFinish()
        fun finishActivity()
        fun actionToolsClicked()
        fun actionCurrentToolClicked()
        fun rateUsClicked()
        fun importFromGalleryClicked()
        fun showImportDialog()
        fun importStickersClicked()
        fun bitmapLoadedFromSource(loadedImage: Bitmap)
        fun setLayerAdapter(layerAdapter: LayerAdapter)
        fun loadScaledImage(uri: Uri?, @ActivityRequestCode requestCode: Int)
    }

    interface Model {
        var cameraImageUri: Uri?
        var savedPictureUri: Uri?
        var isSaved: Boolean
        var isFullscreen: Boolean
        var isOpenedFromCatroid: Boolean
        fun wasInitialAnimationPlayed(): Boolean
        fun setInitialAnimationPlayed(wasInitialAnimationPlayed: Boolean)
    }

    interface Interactor {
        fun saveCopy(callback: SaveImageCallback, requestCode: Int, workspace: Workspace, context: Context)
        fun createFile(callback: CreateFileCallback, requestCode: Int, filename: String)
        fun saveImage(callback: SaveImageCallback, requestCode: Int, workspace: Workspace, uri: Uri?, context: Context)
        fun loadFile(callback: LoadImageCallback, requestCode: Int, uri: Uri?, context: Context, scaling: Boolean)
    }

    interface TopBarViewHolder {
        val height: Int
        fun enableUndoButton()
        fun disableUndoButton()
        fun enableRedoButton()
        fun disableRedoButton()
        fun hide()
        fun show()
        fun removeStandaloneMenuItems(menu: Menu?)
        fun removeCatroidMenuItems(menu: Menu?)
        fun hideTitleIfNotStandalone()
    }

    interface DrawerLayoutViewHolder {
        fun closeDrawer(gravity: Int, animate: Boolean)
        fun isDrawerOpen(gravity: Int): Boolean
        fun openDrawer(gravity: Int)
    }

    interface BottomBarViewHolder {
        val isVisible: Boolean
        fun show()
        fun hide()
    }

    interface BottomNavigationViewHolder {
        fun show()
        fun hide()
        fun showCurrentTool(toolType: ToolType)
        fun setColorButtonColor(@ColorInt color: Int)
    }

    interface BottomNavigationAppearance {
        fun showCurrentTool(toolType: ToolType)
    }
}
