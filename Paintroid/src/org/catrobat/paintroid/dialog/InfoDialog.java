package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

@SuppressLint("ValidFragment")
public class InfoDialog extends DialogFragment implements
		DialogInterface.OnClickListener {

	private static final int NULL_RESOURCE = 0x0;

	public enum DialogType {
		INFO(android.R.drawable.ic_dialog_info, R.string.help_title), WARNING(
				android.R.drawable.ic_dialog_alert,
				android.R.string.dialog_alert_title);

		private int mImageResource;
		private int mTitleResource;

		private DialogType(int imageResource, int titleResource) {
			mImageResource = imageResource;
			mTitleResource = titleResource;
		}

		public int getImageResource() {
			return mImageResource;
		}

		public int getTitleResource() {
			return mTitleResource;
		}
	}

	private DialogType mDialogType;
	private int mMessageResource;
	private int mTitleResource;

	@SuppressLint("ValidFragment")
	public InfoDialog(DialogType dialogType, int messageResource) {
		this(dialogType, messageResource, dialogType.getTitleResource());
	}

	public InfoDialog(DialogType dialogType, int messageResource,
			int titleResource) {
		mDialogType = dialogType;
		mMessageResource = messageResource;
		mTitleResource = titleResource;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(getActivity());
		} else {
			builder = new AlertDialog.Builder(getActivity(),
					AlertDialog.THEME_HOLO_DARK);
		}

		if (mTitleResource != NULL_RESOURCE) {
			builder.setTitle(mTitleResource);
		}

		int imageResource = mDialogType.getImageResource();
		if (imageResource != NULL_RESOURCE) {
			builder.setIcon(imageResource);
		}

		if (mMessageResource != NULL_RESOURCE) {
			builder.setMessage(mMessageResource);
		}

		builder.setNeutralButton(android.R.string.ok, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.cancel();
	}

}