package org.catrobat.paintroid.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.text.HtmlCompat
import org.catrobat.paintroid.R

class ProjectDetailsDialog(
    private val name: String,
    private val resolution: String,
    private val formattedLastModified: String,
    private val formattedCreationDate: String,
    private val format: String,
    private val size: String
) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = getString(
            R.string.pocketpaint_project_details_dialog,
            resolution,
            formattedLastModified,
            formattedCreationDate,
            format,
            size.toString()
        )

        return AlertDialog.Builder(context, R.style.PocketPaintAlertDialog)
            .setTitle(name)
            .setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY))
            .setPositiveButton(R.string.pocketpaint_ok) { _, _ -> dismiss() }
            .create()
    }
}
