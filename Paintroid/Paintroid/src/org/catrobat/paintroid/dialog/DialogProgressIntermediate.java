package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.R;

import android.content.Context;
import android.view.Window;

public class DialogProgressIntermediate extends BaseDialog {

	public DialogProgressIntermediate(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_progress_dialogue);
	}
}
