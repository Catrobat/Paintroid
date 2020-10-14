package org.catrobat.paintroid.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import org.catrobat.paintroid.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class OverwriteDialog extends MainActivityDialogFragment {
	private int permission;

	public static OverwriteDialog newInstance(int permissionCode) {
		OverwriteDialog dialog = new OverwriteDialog();
		Bundle bundle = new Bundle();

		bundle.putInt("permission", permissionCode);
		dialog.setArguments(bundle);

		return dialog;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		permission = arguments.getInt("permission");
	}

	@NonNull
	@Override
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		return new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog)
				.setMessage(getResources().getString(R.string.pocketpaint_overwrite, getString(R.string.menu_save_copy)))
				.setTitle(R.string.pocketpaint_overwrite_title)
				.setPositiveButton(R.string.overwrite_button_text, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getPresenter().switchBetweenVersions(permission);
						dismiss();
					}
				})
				.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
				.create();
	}
}
