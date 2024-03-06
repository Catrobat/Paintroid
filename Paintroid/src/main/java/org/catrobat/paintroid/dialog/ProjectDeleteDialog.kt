package org.catrobat.paintroid.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.text.HtmlCompat
import org.catrobat.paintroid.LandingPageActivity.Companion.projectAdapter
import org.catrobat.paintroid.LandingPageActivity.Companion.projectDB
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.CATROBAT_IMAGE_ENDING
import org.catrobat.paintroid.common.PNG_IMAGE_ENDING
import java.io.File

class ProjectDeleteDialog(private val projectId: Int, private val name: String, private val imageUri: Uri?, private val position: Int) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = getString(R.string.pocketpaint_project_delete_title_dialog, name)

        return AlertDialog.Builder(context, R.style.PocketPaintAlertDialog)
            .setTitle(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY))
            .setMessage(R.string.pocketpaint_project_delete_message_dialog)
            .setPositiveButton(R.string.pocketpaint_ok) { _, _ ->
                projectDB.dao.deleteProject(projectId)
                if (imageUri != null) {
                    deleteProjectFile(name, imageUri)
                    deleteProjectImage(name, imageUri)
                }
                projectAdapter.removeProject(projectId, position)
            }
            .setNegativeButton(R.string.pocketpaint_cancel) { _, _ -> dismiss() }
            .create()
    }

    private fun deleteProjectFile(name: String, uri: Uri) {
        val file = getFile(name, uri, Environment.DIRECTORY_DOWNLOADS, CATROBAT_IMAGE_ENDING)
        deleteFile(file)
    }

    private fun deleteProjectImage(name: String, uri: Uri) {
        val file = getFile(name, uri, Environment.DIRECTORY_PICTURES, PNG_IMAGE_ENDING)
        deleteFile(file)
    }

    private fun getFile(name: String, uri: Uri, directoryType: String, fileExtension: String): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val dir = Environment.getExternalStoragePublicDirectory(directoryType)
            File(dir, "$name.$fileExtension")
        } else {
            File(uri.path.toString())
        }
    }

    private fun deleteFile(file: File?) {
        file?.let {
            if (it.exists()) {
                it.delete()
            }
        }
    }
}
