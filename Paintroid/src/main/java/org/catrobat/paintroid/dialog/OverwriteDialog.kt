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
package org.catrobat.paintroid.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.catrobat.paintroid.R

class OverwriteDialog : MainActivityDialogFragment() {
    private var permission = 0
    private var isExport = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()
        permission = arguments.getInt("permission")
        isExport = arguments.getBoolean("isExport")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setMessage(
                resources.getString(
                    R.string.pocketpaint_overwrite,
                    getString(R.string.menu_save_copy)
                )
            )
            .setTitle(R.string.pocketpaint_overwrite_title)
            .setPositiveButton(R.string.overwrite_button_text) { dialog, which ->
                presenter.switchBetweenVersions(permission, isExport)
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { dialog, which -> dismiss() }
            .create()

    companion object {
        @JvmStatic
        fun newInstance(permissionCode: Int, isExport: Boolean): OverwriteDialog {
            val dialog = OverwriteDialog()
            val bundle = Bundle()
            bundle.putInt("permission", permissionCode)
            bundle.putBoolean("isExport", isExport)
            dialog.arguments = bundle
            return dialog
        }
    }
}
