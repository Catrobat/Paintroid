package at.tugraz.ist.paintroid.dialog;

import android.app.Dialog;
import android.content.Context;
import at.tugraz.ist.paintroid.R;

public class BaseDialog extends Dialog {

	public BaseDialog(Context context) {
		super(context, R.style.CustomPaintroidDialog);
	}

	public BaseDialog(Context context, int theme) {
		super(context, theme);
	}

}
