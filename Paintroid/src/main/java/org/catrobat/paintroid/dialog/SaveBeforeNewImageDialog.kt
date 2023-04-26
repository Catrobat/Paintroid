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
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.catrobat.paintroid.R

class SaveBeforeNewImageDialog : MainActivityDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireActivity(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.menu_new_image)
            .setMessage(R.string.dialog_warning_new_image)
            .setPositiveButton(R.string.save_button_text) { _, _ -> presenter.saveBeforeNewImage() }
            .setNegativeButton(R.string.discard_button_text) { _, _ -> presenter.onNewImage() }
            .create()
}
