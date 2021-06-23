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
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import org.catrobat.paintroid.R

class InfoDialog : AppCompatDialogFragment(), DialogInterface.OnClickListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
        arguments?.let {
            builder.setTitle(it.getInt(TITLE_RESOURCE_KEY))
                .setIcon(it.getInt(DRAWABLE_RESOURCE_KEY))
                .setMessage(it.getInt(MESSAGE_RESOURCE_KEY))
        }
        builder.setPositiveButton(android.R.string.ok, this)
        return builder.create()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        dialog.cancel()
    }

    enum class DialogType(@get:DrawableRes @param:DrawableRes val imageResource: Int) {
        INFO(R.drawable.ic_pocketpaint_dialog_info), WARNING(R.drawable.ic_pocketpaint_dialog_warning);
    }

    companion object {
        const val DRAWABLE_RESOURCE_KEY = "drawableResource"
        const val MESSAGE_RESOURCE_KEY = "messageResource"
        const val TITLE_RESOURCE_KEY = "titleResource"

        @JvmStatic
        fun newInstance(
            dialogType: DialogType,
            @StringRes messageResource: Int,
            @StringRes titleResource: Int
        ): InfoDialog {
            return InfoDialog().apply {
                arguments = bundleOf(
                    DRAWABLE_RESOURCE_KEY to dialogType.imageResource,
                    MESSAGE_RESOURCE_KEY to messageResource,
                    TITLE_RESOURCE_KEY to titleResource
                )
            }
        }
    }
}
