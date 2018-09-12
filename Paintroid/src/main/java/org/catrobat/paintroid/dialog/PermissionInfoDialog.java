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

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import org.catrobat.paintroid.R;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class PermissionInfoDialog extends AppCompatDialogFragment implements
		DialogInterface.OnClickListener {

	public static final String DRAWABLE_RESOURCE_KEY = "drawableResource";
	public static final String MESSAGE_RESOURCE_KEY = "messageResource";
	public static final String TITLE_RESOURCE_KEY = "titleResource";

	public int requestCode;

	public static PermissionInfoDialog newInstance(PermissionType permissionType, int requestCode) {
		PermissionInfoDialog permissionInfoDialog = new PermissionInfoDialog();

		permissionInfoDialog.requestCode = requestCode;
		Bundle bundle = new Bundle();
		bundle.putInt(DRAWABLE_RESOURCE_KEY, permissionType.getImageResource());
		bundle.putInt(MESSAGE_RESOURCE_KEY, permissionType.getMessageResource());
		bundle.putInt(TITLE_RESOURCE_KEY, permissionType.getTitleResource());
		permissionInfoDialog.setArguments(bundle);
		return permissionInfoDialog;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog);

		Bundle arguments = getArguments();
		if (arguments != null) {
			builder.setTitle(arguments.getInt(TITLE_RESOURCE_KEY))
					.setIcon(arguments.getInt(DRAWABLE_RESOURCE_KEY))
					.setMessage(arguments.getInt(MESSAGE_RESOURCE_KEY));
		}

		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case BUTTON_POSITIVE:
				ActivityCompat.requestPermissions(getActivity(),
						new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
						requestCode);
				dialog.cancel();
				break;
			case BUTTON_NEGATIVE:
				dialog.cancel();
				break;
		}
	}

	public enum PermissionType {
		EXTERNAL_STORAGE(R.drawable.ic_pocketpaint_dialog_info,
				R.string.permission_info_external_storage_text,
				R.string.permission_info_external_storage_title);

		private int imageResource;
		private int messageResource;
		private int titleResource;

		PermissionType(@DrawableRes int imageResource, @StringRes int messageResource, @StringRes int titleResource) {
			this.imageResource = imageResource;
			this.messageResource = messageResource;
			this.titleResource = titleResource;
		}

		public @DrawableRes int getImageResource() {
			return imageResource;
		}
		public @StringRes int getMessageResource() {
			return messageResource;
		}
		public @StringRes int getTitleResource() {
			return titleResource;
		}
	}
}
