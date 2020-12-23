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
package org.catrobat.paintroid.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import org.catrobat.paintroid.R

class PermanentDenialPermissionInfoDialog : AppCompatDialogFragment() {
    private var context: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        context = arguments!!.getString("context")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(getContext()!!, R.style.PocketPaintAlertDialog)
                .setMessage(R.string.permission_info_permanent_denial_text)
                .setPositiveButton(R.string.dialog_settings) { dialog, which -> startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$context"))) }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
    }

    companion object {
        fun newInstance(context: String?): PermanentDenialPermissionInfoDialog {
            val permissionInfoDialog = PermanentDenialPermissionInfoDialog()
            val bundle = Bundle()
            bundle.putString("context", context)
            permissionInfoDialog.arguments = bundle
            Log.d("context:", context)
            return permissionInfoDialog
        }
    }
}