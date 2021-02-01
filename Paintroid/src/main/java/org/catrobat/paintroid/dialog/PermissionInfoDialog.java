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
import android.os.Bundle;

import org.catrobat.paintroid.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;

public class PermissionInfoDialog extends AppCompatDialogFragment {

	private static final String PERMISSION_TYPE_KEY = "permissionTypeKey";
	private static final String PERMISSIONS_KEY = "permissionsKey";
	private static final String REQUEST_CODE_KEY = "requestCodeKey";

	private int requestCode;
	private String[] permissions;
	private PermissionType permissionType;

	public static PermissionInfoDialog newInstance(PermissionType permissionType, String[] permissions, int requestCode) {
		PermissionInfoDialog permissionInfoDialog = new PermissionInfoDialog();

		Bundle bundle = new Bundle();
		bundle.putSerializable(PERMISSION_TYPE_KEY, permissionType);
		bundle.putStringArray(PERMISSIONS_KEY, permissions);
		bundle.putInt(REQUEST_CODE_KEY, requestCode);
		permissionInfoDialog.setArguments(bundle);
		return permissionInfoDialog;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		requestCode = arguments.getInt(REQUEST_CODE_KEY);
		permissions = arguments.getStringArray(PERMISSIONS_KEY);
		permissionType = (PermissionType) arguments.getSerializable(PERMISSION_TYPE_KEY);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog)
				.setIcon(permissionType.getIconResource())
				.setMessage(permissionType.getMessageResource())
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create();
	}

	public enum PermissionType {
		EXTERNAL_STORAGE(R.drawable.ic_pocketpaint_dialog_info,
				R.string.permission_info_external_storage_text);

		private int iconResource;
		private int messageResource;

		PermissionType(@DrawableRes int iconResource, @StringRes int messageResource) {
			this.iconResource = iconResource;
			this.messageResource = messageResource;
		}

		public @DrawableRes int getIconResource() {
			return iconResource;
		}
		public @StringRes int getMessageResource() {
			return messageResource;
		}
	}
}
