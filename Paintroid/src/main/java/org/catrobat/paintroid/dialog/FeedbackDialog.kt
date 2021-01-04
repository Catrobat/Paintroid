package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import org.catrobat.paintroid.R

class FeedbackDialog : AppCompatDialogFragment() {
	@SuppressLint("InflateParams")
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
				.setMessage(R.string.pocketpaint_feedback)
				.setTitle(R.string.pocketpaint_rate_us_title)
				.setPositiveButton(R.string.pocketpaint_ok)  { dialog, which -> dismiss() }
				.create()
	}

	companion object {
		@JvmStatic
		fun newInstance(): FeedbackDialog = FeedbackDialog()
	}
}