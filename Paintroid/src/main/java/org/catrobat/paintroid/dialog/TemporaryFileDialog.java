package org.catrobat.paintroid.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import org.catrobat.paintroid.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class TemporaryFileDialog extends MainActivityDialogFragment {
	public static TemporaryFileDialog newInstance() {
		return new TemporaryFileDialog();
	}

	@NonNull
	@Override
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog)
				.setMessage(getString(R.string.pocketpaint_temporary_file_dialog))
				.setTitle(getString(R.string.pocketpaint_temporary_files_title))
				.setPositiveButton(R.string.pocketpaint_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getPresenter().openTempFile();
						dismiss();
					}
				})
				.setNegativeButton(R.string.pocketpaint_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
				.create();
	}
}
