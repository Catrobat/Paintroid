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
import androidx.core.os.bundleOf
import org.catrobat.paintroid.R

private const val PERMISSION = "permission"
private const val IS_EXPORT = "isExport"

class OverwriteDialog : MainActivityDialogFragment() {
    private var permission = 0
    private var isExport = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()
        permission = arguments.getInt(PERMISSION)
        isExport = arguments.getBoolean(IS_EXPORT)
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
            .setPositiveButton(R.string.overwrite_button_text) { _, _ ->
                presenter.switchBetweenVersions(permission, isExport)
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ -> dismiss() }
            .create()

    companion object {
        fun newInstance(permissionCode: Int, isExport: Boolean): OverwriteDialog =
            OverwriteDialog().apply {
                arguments = bundleOf(PERMISSION to permissionCode, IS_EXPORT to isExport)
            }
    }
}
