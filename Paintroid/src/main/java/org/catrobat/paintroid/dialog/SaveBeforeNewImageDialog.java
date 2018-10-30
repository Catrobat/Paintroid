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

package org.catrobat.paintroid.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;

public class SaveBeforeNewImageDialog extends AppCompatDialogFragment {
	private static final String EXTRA_URI = "arguri";
	private static final String EXTRA_REQUEST = "argrequest";

	public static SaveBeforeNewImageDialog newInstance(int requestCode, Uri uri) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_REQUEST, requestCode);
		args.putParcelable(EXTRA_URI, uri);

		SaveBeforeNewImageDialog dialog = new SaveBeforeNewImageDialog();
		dialog.setArguments(args);
		return dialog;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final MainActivity activity = (MainActivity) getActivity();

		Bundle arguments = getArguments();
		final int requestCode = arguments.getInt(EXTRA_REQUEST);
		final Uri uri = arguments.getParcelable(EXTRA_URI);

		return new AlertDialog.Builder(activity, R.style.PocketPaintAlertDialog)
				.setTitle(R.string.menu_new_image)
				.setMessage(R.string.dialog_warning_new_image)
				.setCancelable(true)
				.setPositiveButton(R.string.save_button_text, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						MainActivityContracts.Presenter presenter = activity.getPresenter();
						presenter.checkPermissionAndForward(requestCode, uri);
					}
				})
				.setNegativeButton(R.string.discard_button_text, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						MainActivityContracts.Presenter presenter = activity.getPresenter();
						presenter.chooseNewImage();
					}
				})
				.create();
	}
}
