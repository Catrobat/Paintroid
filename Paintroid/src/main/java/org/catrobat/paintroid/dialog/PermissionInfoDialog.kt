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
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import org.catrobat.paintroid.R

private const val PERMISSION_TYPE_KEY = "permissionTypeKey"
private const val PERMISSIONS_KEY = "permissionsKey"
private const val REQUEST_CODE_KEY = "requestCodeKey"

class PermissionInfoDialog : AppCompatDialogFragment() {
    private var requestCode = 0
    private lateinit var permissions: Array<String>
    private lateinit var permissionType: PermissionType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            requestCode = getInt(REQUEST_CODE_KEY)
            permissions = getStringArray(PERMISSIONS_KEY) as Array<String>
            permissionType = getSerializable(PERMISSION_TYPE_KEY) as PermissionType
        }
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setIcon(permissionType.iconResource)
            .setMessage(permissionType.messageResource)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions, requestCode
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()

    enum class PermissionType(@get:DrawableRes @param:DrawableRes val iconResource: Int, @get:StringRes @param:StringRes val messageResource: Int) {
        EXTERNAL_STORAGE(R.drawable.ic_pocketpaint_dialog_info, R.string.permission_info_external_storage_text);
    }

    companion object {
        @JvmStatic
        fun newInstance(permissionType: PermissionType, permissions: Array<String>, requestCode: Int):
            PermissionInfoDialog = PermissionInfoDialog().apply {
                arguments = bundleOf(
                    PERMISSION_TYPE_KEY to permissionType,
                    PERMISSIONS_KEY to permissions,
                    REQUEST_CODE_KEY to requestCode
                )
            }
    }
}
