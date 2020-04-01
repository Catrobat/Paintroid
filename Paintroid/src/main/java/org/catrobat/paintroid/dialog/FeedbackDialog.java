package org.catrobat.paintroid.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import org.catrobat.paintroid.R;

public class FeedbackDialog extends AppCompatDialogFragment {
	public static FeedbackDialog newInstance() {
		return new FeedbackDialog();
	}

	@NonNull
	@Override
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog)
				.setMessage(R.string.pocketpaint_feedback)
				.setTitle(R.string.pocketpaint_rate_us_title)
				.setPositiveButton(R.string.pocketpaint_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
				.create();
	}
}
