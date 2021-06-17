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
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.MainActivityConstants.LoadImageRequestCode

class ScaleImageOnLoadDialog : MainActivityDialogFragment() {
    private var uri: Uri? = null
    private var requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()
        uri = Uri.parse(arguments.getString("Uri"))
        requestCode = arguments.getInt("requestCode")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireActivity(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.dialog_scale_title)
            .setMessage(R.string.dialog_scale_message)
            .setPositiveButton(android.R.string.ok) { dialog, id ->
                presenter.loadScaledImage(
                    uri,
                    requestCode
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()

    companion object {
        @JvmStatic
        fun newInstance(uri: Uri, @LoadImageRequestCode requestCode: Int): ScaleImageOnLoadDialog {
            val scaleImageDialog = ScaleImageOnLoadDialog()
            val bundle = Bundle()
            bundle.putString("Uri", uri.toString())
            bundle.putInt("requestCode", requestCode)
            scaleImageDialog.arguments = bundle
            return scaleImageDialog
        }
    }
}
