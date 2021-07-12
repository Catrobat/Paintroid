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
package org.catrobat.paintroid.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.WelcomeActivity
import org.catrobat.paintroid.colorpicker.ColorPickerDialog
import org.catrobat.paintroid.colorpicker.OnColorPickedListener
import org.catrobat.paintroid.common.Constants
import org.catrobat.paintroid.common.MainActivityConstants
import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.dialog.AboutDialog
import org.catrobat.paintroid.dialog.AdvancedSettingsDialog
import org.catrobat.paintroid.dialog.CatrobatImageInfoDialog
import org.catrobat.paintroid.dialog.FeedbackDialog
import org.catrobat.paintroid.dialog.ImportImageDialog
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog.newInstance
import org.catrobat.paintroid.dialog.InfoDialog
import org.catrobat.paintroid.dialog.JpgInfoDialog
import org.catrobat.paintroid.dialog.LikeUsDialog
import org.catrobat.paintroid.dialog.OraInfoDialog
import org.catrobat.paintroid.dialog.OverwriteDialog
import org.catrobat.paintroid.dialog.PermanentDenialPermissionInfoDialog
import org.catrobat.paintroid.dialog.PermissionInfoDialog
import org.catrobat.paintroid.dialog.PermissionInfoDialog.PermissionType
import org.catrobat.paintroid.dialog.PngInfoDialog
import org.catrobat.paintroid.dialog.RateUsDialog
import org.catrobat.paintroid.dialog.SaveBeforeFinishDialog
import org.catrobat.paintroid.dialog.SaveBeforeFinishDialog.SaveBeforeFinishDialogType
import org.catrobat.paintroid.dialog.SaveBeforeLoadImageDialog
import org.catrobat.paintroid.dialog.SaveBeforeNewImageDialog
import org.catrobat.paintroid.dialog.SaveInformationDialog
import org.catrobat.paintroid.dialog.ScaleImageOnLoadDialog
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.ui.fragments.CatroidMediaGalleryFragment
import org.catrobat.paintroid.ui.fragments.CatroidMediaGalleryFragment.MediaGalleryListener

class MainActivityNavigator(
    private val mainActivity: MainActivity,
    private val toolReference: ToolReference
) : MainActivityContracts.Navigator {
    private var progressDialog: AppCompatDialog? = null

    override val isSdkAboveOrEqualM: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    override val isSdkAboveOrEqualQ: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private fun showFragment(
        fragment: Fragment,
        tag: String = Constants.CATROID_MEDIA_GALLERY_FRAGMENT_TAG
    ) {
        val fragmentManager = mainActivity.supportFragmentManager
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_to_top,
                R.anim.slide_to_bottom,
                R.anim.slide_to_top,
                R.anim.slide_to_bottom
            )
            .addToBackStack(null)
            .add(R.id.fragment_container, fragment, tag)
            .commit()
    }

    private fun showDialogFragmentSafely(dialog: DialogFragment, tag: String) {
        val fragmentManager = mainActivity.supportFragmentManager
        if (!fragmentManager.isStateSaved) {
            dialog.show(fragmentManager, tag)
        }
    }

    private fun findFragmentByTag(tag: String): Fragment? =
        mainActivity.supportFragmentManager.findFragmentByTag(tag)

    private fun setupColorPickerDialogListeners(dialog: ColorPickerDialog) {
        dialog.addOnColorPickedListener(object : OnColorPickedListener {
            override fun colorChanged(color: Int) {
                toolReference.get().changePaintColor(color)
                mainActivity.presenter.setBottomNavigationColor(color)
            }
        })
        dialog.setBitmap(mainActivity.presenter.bitmap)
    }

    private fun setupCatroidMediaGalleryListeners(dialog: CatroidMediaGalleryFragment) {
        dialog.setMediaGalleryListener(object : MediaGalleryListener {
            override fun bitmapLoadedFromSource(loadedBitmap: Bitmap) {
                mainActivity.presenter.bitmapLoadedFromSource(loadedBitmap)
            }

            override fun showProgressDialog() {
                showIndeterminateProgressDialog()
            }

            override fun dismissProgressDialog() {
                dismissIndeterminateProgressDialog()
            }
        })
    }

    @SuppressWarnings("SwallowedException")
    private fun openPlayStore(applicationId: String) {
        val uriPlayStore = Uri.parse("market://details?id=$applicationId")
        val openPlayStore = Intent(Intent.ACTION_VIEW, uriPlayStore)
        try {
            mainActivity.startActivity(openPlayStore)
        } catch (e: ActivityNotFoundException) {
            val uriNoPlayStore =
                Uri.parse("http://play.google.com/store/apps/details?id=$applicationId")
            val noPlayStoreInstalled = Intent(Intent.ACTION_VIEW, uriNoPlayStore)
            val activityInfo = noPlayStoreInstalled.resolveActivityInfo(
                mainActivity.packageManager, noPlayStoreInstalled.flags
            )
            if (activityInfo.exported) {
                mainActivity.startActivity(noPlayStoreInstalled)
            }
        }
    }

    private fun getFileName(uri: Uri?): String? {
        uri ?: return null
        var result: String? = null
        if (uri.scheme == "content") {
            val queryCursor = mainActivity.contentResolver.query(uri, null, null, null, null)
            queryCursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    override fun showColorPickerDialog() {
        if (findFragmentByTag(Constants.COLOR_PICKER_DIALOG_TAG) == null) {
            val dialog = ColorPickerDialog.newInstance(toolReference.get().drawPaint.color)
            setupColorPickerDialogListeners(dialog)
            showDialogFragmentSafely(dialog, Constants.COLOR_PICKER_DIALOG_TAG)
        }
    }

    override fun showCatroidMediaGallery() {
        if (findFragmentByTag(Constants.CATROID_MEDIA_GALLERY_FRAGMENT_TAG) == null) {
            val fragment = CatroidMediaGalleryFragment()
            fragment.setMediaGalleryListener(object : MediaGalleryListener {
                override fun bitmapLoadedFromSource(loadedBitmap: Bitmap) {
                    mainActivity.presenter.bitmapLoadedFromSource(loadedBitmap)
                }

                override fun showProgressDialog() {
                    showIndeterminateProgressDialog()
                }

                override fun dismissProgressDialog() {
                    dismissIndeterminateProgressDialog()
                }
            })
            showFragment(fragment)
        }
    }

    override fun startLoadImageActivity(@ActivityRequestCode requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        }
        mainActivity.startActivityForResult(intent, requestCode)
    }

    override fun startImportImageActivity(@ActivityRequestCode requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        }
        mainActivity.startActivityForResult(intent, requestCode)
    }

    override fun startWelcomeActivity(@ActivityRequestCode requestCode: Int) {
        val intent = Intent(mainActivity.applicationContext, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        mainActivity.startActivityForResult(intent, requestCode)
    }

    override fun startShareImageActivity(bitmap: Bitmap?) {
        val uri = FileIO.saveBitmapToCache(bitmap, mainActivity) ?: return
        val shareIntent = Intent().apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            setDataAndType(uri, mainActivity.contentResolver.getType(uri))
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            action = Intent.ACTION_SEND
        }
        val chooserTitle = mainActivity.resources.getString(R.string.share_image_via_text)
        mainActivity.startActivity(Intent.createChooser(shareIntent, chooserTitle))
    }

    override fun showAboutDialog() {
        val about = AboutDialog()
        about.show(mainActivity.supportFragmentManager, Constants.ABOUT_DIALOG_FRAGMENT_TAG)
    }

    override fun showLikeUsDialog() {
        val likeUsDialog = LikeUsDialog()
        likeUsDialog.show(
            mainActivity.supportFragmentManager,
            Constants.LIKE_US_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showRateUsDialog() {
        val rateUsDialog = RateUsDialog.newInstance()
        rateUsDialog.show(
            mainActivity.supportFragmentManager,
            Constants.RATE_US_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showFeedbackDialog() {
        val feedbackDialog = FeedbackDialog()
        feedbackDialog.show(
            mainActivity.supportFragmentManager,
            Constants.FEEDBACK_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showAdvancedSettingsDialog() {
        val advancedSettingsDialog = AdvancedSettingsDialog()
        advancedSettingsDialog.show(
            mainActivity.supportFragmentManager,
            Constants.ADVANCED_SETTINGS_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showOverwriteDialog(permissionCode: Int, isExport: Boolean) {
        val overwriteDialog = OverwriteDialog.newInstance(permissionCode, isExport)
        overwriteDialog.show(
            mainActivity.supportFragmentManager,
            Constants.OVERWRITE_INFORMATION_DIALOG_TAG
        )
    }

    override fun showPngInformationDialog() {
        val pngInfoDialog = PngInfoDialog()
        pngInfoDialog.show(
            mainActivity.supportFragmentManager,
            Constants.PNG_INFORMATION_DIALOG_TAG
        )
    }

    override fun showJpgInformationDialog() {
        val jpgInfoDialog = JpgInfoDialog()
        jpgInfoDialog.show(
            mainActivity.supportFragmentManager,
            Constants.JPG_INFORMATION_DIALOG_TAG
        )
    }

    override fun showOraInformationDialog() {
        val oraInfoDialog = OraInfoDialog()
        oraInfoDialog.show(
            mainActivity.supportFragmentManager,
            Constants.ORA_INFORMATION_DIALOG_TAG
        )
    }

    override fun showCatrobatInformationDialog() {
        val catrobatInfoDialog = CatrobatImageInfoDialog()
        catrobatInfoDialog.show(
            mainActivity.supportFragmentManager,
            Constants.CATROBAT_INFORMATION_DIALOG_TAG
        )
    }

    override fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO)
        val data = Uri.parse("mailto:support-paintroid@catrobat.org")
        intent.data = data
        mainActivity.startActivity(intent)
    }

    override fun showImageImportDialog() {
        val importImage = ImportImageDialog()
        importImage.show(mainActivity.supportFragmentManager, Constants.ABOUT_DIALOG_FRAGMENT_TAG)
    }

    override fun showIndeterminateProgressDialog() {
        if (progressDialog == null) {
            progressDialog = newInstance(mainActivity)
        }
        progressDialog?.show()
    }

    override fun dismissIndeterminateProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun returnToPocketCode(path: String?) {
        val resultIntent = Intent()
        resultIntent.putExtra(Constants.PAINTROID_PICTURE_PATH, path)
        mainActivity.setResult(Activity.RESULT_OK, resultIntent)
        mainActivity.finish()
    }

    override fun showToast(resId: Int, duration: Int) {
        ToastFactory.makeText(mainActivity, resId, duration).show()
    }

    override fun showToast(msg: String, duration: Int) {
        ToastFactory.makeText(mainActivity, msg, duration).show()
    }

    override fun showSaveErrorDialog() {
        val dialog: AppCompatDialogFragment = InfoDialog.newInstance(
            InfoDialog.DialogType.WARNING,
            R.string.dialog_error_sdcard_text, R.string.dialog_error_save_title
        )
        showDialogFragmentSafely(dialog, Constants.SAVE_DIALOG_FRAGMENT_TAG)
    }

    override fun showLoadErrorDialog() {
        val dialog: AppCompatDialogFragment = InfoDialog.newInstance(
            InfoDialog.DialogType.WARNING,
            R.string.dialog_loading_image_failed_title, R.string.dialog_loading_image_failed_text
        )
        showDialogFragmentSafely(dialog, Constants.LOAD_DIALOG_FRAGMENT_TAG)
    }

    override fun showRequestPermissionRationaleDialog(
        permissionType: PermissionType,
        permissions: Array<String>,
        requestCode: Int
    ) {
        val dialog: AppCompatDialogFragment =
            PermissionInfoDialog.newInstance(permissionType, permissions, requestCode)
        showDialogFragmentSafely(dialog, Constants.PERMISSION_DIALOG_FRAGMENT_TAG)
    }

    override fun showRequestPermanentlyDeniedPermissionRationaleDialog() {
        val dialog: AppCompatDialogFragment = PermanentDenialPermissionInfoDialog.newInstance(
            mainActivity.packageName
        )
        showDialogFragmentSafely(dialog, Constants.PERMISSION_DIALOG_FRAGMENT_TAG)
    }

    override fun askForPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(mainActivity, permissions, requestCode)
    }

    override fun doIHavePermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            mainActivity,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    override fun isPermissionPermanentlyDenied(permissions: Array<String>): Boolean =
        !ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, permissions[0])

    override fun finishActivity() {
        mainActivity.finish()
    }

    override fun showSaveBeforeFinishDialog() {
        val dialog: AppCompatDialogFragment = SaveBeforeFinishDialog.newInstance(
            SaveBeforeFinishDialogType.FINISH
        )
        showDialogFragmentSafely(dialog, Constants.SAVE_QUESTION_FRAGMENT_TAG)
    }

    override fun showSaveBeforeNewImageDialog() {
        val dialog: AppCompatDialogFragment = SaveBeforeNewImageDialog.newInstance()
        showDialogFragmentSafely(dialog, Constants.SAVE_QUESTION_FRAGMENT_TAG)
    }

    override fun showSaveBeforeLoadImageDialog() {
        val dialog: AppCompatDialogFragment = SaveBeforeLoadImageDialog.newInstance()
        showDialogFragmentSafely(dialog, Constants.SAVE_QUESTION_FRAGMENT_TAG)
    }

    override fun showScaleImageRequestDialog(uri: Uri?, requestCode: Int) {
        uri ?: return
        val dialog: AppCompatDialogFragment = ScaleImageOnLoadDialog.newInstance(uri, requestCode)
        showDialogFragmentSafely(dialog, Constants.SCALE_IMAGE_FRAGMENT_TAG)
    }

    @SuppressLint("VisibleForTests")
    override fun showSaveImageInformationDialogWhenStandalone(
        permissionCode: Int,
        imageNumber: Int,
        isExport: Boolean
    ) {
        val uri = mainActivity.model.savedPictureUri
        if (uri != null && permissionCode != MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY) {
            FileIO.parseFileName(uri, mainActivity.contentResolver)
        }
        if (!isExport && mainActivity.model.isOpenedFromCatroid) {
            val name = getFileName(uri)
            if (name != null && (name.endsWith("jpg") || name.endsWith("jpeg"))) {
                FileIO.compressFormat = Bitmap.CompressFormat.JPEG
                FileIO.ending = ".jpg"
            } else {
                FileIO.compressFormat = Bitmap.CompressFormat.PNG
                FileIO.ending = ".png"
            }
            FileIO.filename = "image$imageNumber"
            FileIO.catroidFlag = true
            FileIO.isCatrobatImage = false
            mainActivity.presenter.switchBetweenVersions(permissionCode, isExport)
            return
        }
        var isStandard = false
        if (permissionCode == MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY) {
            isStandard = true
        }
        val saveInfoDialog =
            SaveInformationDialog.newInstance(permissionCode, imageNumber, isStandard, isExport)
        saveInfoDialog.show(
            mainActivity.supportFragmentManager,
            Constants.SAVE_INFORMATION_DIALOG_TAG
        )
    }

    override fun showToolChangeToast(offset: Int, idRes: Int) {
        var offset = offset
        val toolNameToast = ToastFactory.makeText(mainActivity, idRes, Toast.LENGTH_SHORT)
        val gravity = Gravity.TOP or Gravity.CENTER
        if (mainActivity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            offset = 0
        }
        toolNameToast.setGravity(gravity, 0, offset)
        toolNameToast.show()
    }

    override fun broadcastAddPictureToGallery(uri: Uri) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = uri
        mainActivity.sendBroadcast(mediaScanIntent)
    }

    override fun restoreFragmentListeners() {
        var fragment = findFragmentByTag(Constants.COLOR_PICKER_DIALOG_TAG)
        if (fragment != null) {
            setupColorPickerDialogListeners(fragment as ColorPickerDialog)
        }
        fragment = findFragmentByTag(Constants.CATROID_MEDIA_GALLERY_FRAGMENT_TAG)
        if (fragment != null) {
            setupCatroidMediaGalleryListeners(fragment as CatroidMediaGalleryFragment)
        }
    }

    override fun rateUsClicked() {
        openPlayStore(mainActivity.packageName)
    }

    override fun setAntialiasingOnToolPaint() {
        mainActivity.toolPaint.setAntialiasing()
    }
}
