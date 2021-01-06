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