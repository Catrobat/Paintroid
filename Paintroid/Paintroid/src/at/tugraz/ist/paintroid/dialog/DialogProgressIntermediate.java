package at.tugraz.ist.paintroid.dialog;

import android.content.Context;
import android.view.Window;
import at.tugraz.ist.paintroid.R;

public class DialogProgressIntermediate extends BaseDialog {

	public DialogProgressIntermediate(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_progress_dialogue);
	}
}
