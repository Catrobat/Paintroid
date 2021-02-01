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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

public class SaveBeforeFinishDialog extends MainActivityDialogFragment {
	private static final String DIALOG_TYPE = "argDialogType";
	private SaveBeforeFinishDialogType dialogType;

	public static SaveBeforeFinishDialog newInstance(SaveBeforeFinishDialogType dialogType) {
		Bundle args = new Bundle();
		args.putSerializable(DIALOG_TYPE, dialogType);

		SaveBeforeFinishDialog dialog = new SaveBeforeFinishDialog();
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		dialogType = (SaveBeforeFinishDialogType) arguments.getSerializable(DIALOG_TYPE);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity(), R.style.PocketPaintAlertDialog)
				.setTitle(dialogType.getTitleResource())
				.setMessage(dialogType.getMessageResource())
				.setPositiveButton(R.string.save_button_text, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						getPresenter().saveBeforeFinish();
					}
				})
				.setNegativeButton(R.string.discard_button_text, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						getPresenter().finishActivity();
					}
				})
				.create();
	}

	public enum SaveBeforeFinishDialogType {
		FINISH(R.string.closing_security_question_title, R.string.closing_security_question);

		private final int titleResource;
		private final int messageResource;

		SaveBeforeFinishDialogType(@StringRes int titleResource, @StringRes int messageResource) {
			this.titleResource = titleResource;
			this.messageResource = messageResource;
		}

		public int getTitleResource() {
			return titleResource;
		}

		public int getMessageResource() {
			return messageResource;
		}
	}
}
