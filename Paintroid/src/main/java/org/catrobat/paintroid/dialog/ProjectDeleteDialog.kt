package org.catrobat.paintroid.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.text.HtmlCompat
import org.catrobat.paintroid.LandingPageActivity.Companion.projectAdapter
import org.catrobat.paintroid.LandingPageActivity.Companion.projectDB
import org.catrobat.paintroid.R

class ProjectDeleteDialog(private val projectId: Int, private val name: String) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = getString(R.string.pocketpaint_project_delete_title_dialog, name)

        return AlertDialog.Builder(context, R.style.PocketPaintAlertDialog)
            .setTitle(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY))
            .setMessage(R.string.pocketpaint_project_delete_message_dialog)
            .setPositiveButton(R.string.pocketpaint_ok) { _, _ ->
                projectDB.dao.deleteProject(projectId)
                projectAdapter.removeProject(projectId)
                projectAdapter.notifyDataSetChanged()
            }
            .setNegativeButton(R.string.pocketpaint_cancel) { _, _ -> dismiss() }
            .create()
    }
}