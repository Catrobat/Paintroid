package at.tugraz.ist.paintroid.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.ToolsDialogActivity;
import at.tugraz.ist.paintroid.ui.button.ToolButtonAdapter;

public class DialogTools extends BaseDialog {

	private static final float DIALOG_OFFSET_PERCENTAGE = 0.2f;
	private static final int NUMBER_OF_ICONS = 4;
	private ToolButtonAdapter mToolButtonAdapter;
	private int mActionBarHeight;
	private final ToolsDialogActivity mParent;

	public DialogTools(Context context, ToolsDialogActivity parent, ToolButtonAdapter toolButtonAdapter,
			int actionBarHeight) {
		super(context);
		mParent = parent;
		mToolButtonAdapter = toolButtonAdapter;
		mActionBarHeight = actionBarHeight;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Log.i(PaintroidApplication.TAG, "onCreate: " + getClass().getName());
		setContentView(R.layout.tools_menu);
		setCanceledOnTouchOutside(true);
		Log.i(PaintroidApplication.TAG, "0: " + getClass().getName());
		getWindow().setBackgroundDrawable(null);
		getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
		Log.i(PaintroidApplication.TAG, "1: " + getClass().getName());
		getWindow().setBackgroundDrawableResource(R.color.transparent);
		Log.i(PaintroidApplication.TAG, "2: " + getClass().getName());
		GridView gridView = (GridView) findViewById(R.id.gridview_tools_menu);
		Log.i(PaintroidApplication.TAG, "3: " + getClass().getName());
		gridView.setAdapter(mToolButtonAdapter);
		Log.i(PaintroidApplication.TAG, "4: " + getClass().getName());

		gridView.setOnItemClickListener(mParent);
		Log.i(PaintroidApplication.TAG, "5: " + getClass().getName());
		gridView.setOnItemLongClickListener(mParent);
		Log.i(PaintroidApplication.TAG, "6: " + getClass().getName());

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		Log.i(PaintroidApplication.TAG, "7: " + getClass().getName());
		layoutParams.y = mActionBarHeight - (int) (mActionBarHeight * DIALOG_OFFSET_PERCENTAGE);
		Log.i(PaintroidApplication.TAG, "8: " + getClass().getName());
		layoutParams.x = mParent.getResources().getDisplayMetrics().widthPixels / 2 / NUMBER_OF_ICONS;
		Log.i(PaintroidApplication.TAG, "9: " + getClass().getName());
		getWindow().setAttributes(layoutParams);
		Log.i(PaintroidApplication.TAG, "10: " + getClass().getName());
	}

	@Override
	protected void onStop() {
		Log.i(PaintroidApplication.TAG, getClass().getName() + " onStop()");
		super.onStop();
		Log.i(PaintroidApplication.TAG, getClass().getName() + " super.onStop() ");
		mParent.finish();
		Log.i(PaintroidApplication.TAG, getClass().getName() + " parent.finish()");
	}

}
