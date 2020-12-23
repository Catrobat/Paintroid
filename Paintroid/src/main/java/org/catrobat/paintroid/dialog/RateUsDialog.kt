package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.catrobat.paintroid.R

class RateUsDialog : MainActivityDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!, R.style.PocketPaintAlertDialog)
                .setMessage(getString(R.string.pocketpaint_rate_us))
                .setTitle(getString(R.string.pocketpaint_rate_us_title))
                .setPositiveButton(R.string.pocketpaint_yes) { dialog, which ->
                    presenter!!.rateUsClicked()
                    dismiss()
                }
                .setNegativeButton(R.string.pocketpaint_not_now) { dialog, which -> dismiss() }
                .create()
    }

    companion object {
        fun newInstance(): RateUsDialog {
            return RateUsDialog()
        }
    }
}