/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import org.catrobat.paintroid.R

private const val CONTEXT = "context"

class PermanentDenialPermissionInfoDialog : AppCompatDialogFragment() {
    private var context: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireArguments().getString(CONTEXT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setMessage(R.string.permission_info_permanent_denial_text)
            .setPositiveButton(R.string.dialog_settings) { _, _ -> startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$context"))) }
            .setNegativeButton(android.R.string.cancel, null)
            .create()

    companion object {
        @JvmStatic
        fun newInstance(context: String):
            PermanentDenialPermissionInfoDialog = PermanentDenialPermissionInfoDialog().apply {
                arguments = bundleOf(CONTEXT to context)
            }
    }
}
