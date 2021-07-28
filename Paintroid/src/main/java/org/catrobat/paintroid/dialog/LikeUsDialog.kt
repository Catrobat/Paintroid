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
import androidx.appcompat.app.AlertDialog
import org.catrobat.paintroid.R

class LikeUsDialog : MainActivityDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setMessage(getString(R.string.pocketpaint_like_us))
            .setTitle(getString(R.string.pocketpaint_rate_us_title))
            .setPositiveButton(R.string.pocketpaint_yes) { _, _ ->
                presenter.showRateUsDialog()
                dismiss()
            }
            .setNegativeButton(R.string.pocketpaint_no) { _, _ ->
                presenter.showFeedbackDialog()
                dismiss()
            }
            .create()
    }

    companion object {
        @JvmStatic
        fun newInstance(): LikeUsDialog = LikeUsDialog()
    }
}
