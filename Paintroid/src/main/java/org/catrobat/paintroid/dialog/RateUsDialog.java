package org.catrobat.paintroid.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import org.catrobat.paintroid.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class RateUsDialog extends MainActivityDialogFragment {
	public static RateUsDialog newInstance() {
		return new RateUsDialog();
	}

	@NonNull
	@Override
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog)
				.setMessage(getString(R.string.pocketpaint_rate_us))
				.setTitle(getString(R.string.pocketpaint_rate_us_title))
				.setPositiveButton(R.string.pocketpaint_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getPresenter().rateUsClicked();
						dismiss();
					}
				})
				.setNegativeButton(R.string.pocketpaint_not_now, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
				.create();
	}
}
