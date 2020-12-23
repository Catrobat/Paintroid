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
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import org.catrobat.paintroid.R

class SaveBeforeFinishDialog : MainActivityDialogFragment() {
    private var dialogType: SaveBeforeFinishDialogType? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        dialogType = arguments!!.getSerializable(DIALOG_TYPE) as SaveBeforeFinishDialogType?
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity!!, R.style.PocketPaintAlertDialog)
                .setTitle(dialogType!!.titleResource)
                .setMessage(dialogType!!.messageResource)
                .setPositiveButton(R.string.save_button_text) { dialog, id -> presenter!!.saveBeforeFinish() }
                .setNegativeButton(R.string.discard_button_text) { dialog, id -> presenter!!.finishActivity() }
                .create()
    }

    enum class SaveBeforeFinishDialogType(@param:StringRes val titleResource: Int, @param:StringRes val messageResource: Int) {
        BACK_TO_POCKET_CODE(R.string.closing_catroid_security_question_title, R.string.closing_security_question), FINISH(R.string.closing_security_question_title, R.string.closing_security_question);

    }

    companion object {
        private const val DIALOG_TYPE = "argDialogType"
        fun newInstance(dialogType: SaveBeforeFinishDialogType?): SaveBeforeFinishDialog {
            val args = Bundle()
            args.putSerializable(DIALOG_TYPE, dialogType)
            val dialog = SaveBeforeFinishDialog()
            dialog.arguments = args
            return dialog
        }
    }
}