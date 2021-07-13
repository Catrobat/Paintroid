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

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import org.catrobat.paintroid.R

class ImportImageDialog : MainActivityDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val importGallery = view.findViewById<LinearLayout>(R.id.pocketpaint_dialog_import_gallery)
        val importStickers =
            view.findViewById<LinearLayout>(R.id.pocketpaint_dialog_import_stickers)
        importGallery.setOnClickListener {
            presenter.importFromGalleryClicked()
            dismiss()
        }
        importStickers.setOnClickListener {
            presenter.importStickersClicked()
            dismiss()
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pocketpaint_import_image, null)
        onViewCreated(layout, savedInstanceState)
        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.dialog_import_image_title)
            .setView(layout)
            .setNegativeButton(R.string.pocketpaint_cancel) { _, _ -> dismiss() }
            .create()
    }
}
