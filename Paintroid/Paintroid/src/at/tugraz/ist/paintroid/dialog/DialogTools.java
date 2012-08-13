package at.tugraz.ist.paintroid.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
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
		super.onCreate(savedInstanceState);
		setCanceledOnTouchOutside(true);
		getWindow().setBackgroundDrawable(null);
		getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tools_menu);
		getWindow().setBackgroundDrawableResource(R.color.transparent);

		GridView gridView = (GridView) findViewById(R.id.gridview_tools_menu);
		gridView.setAdapter(mToolButtonAdapter);

		gridView.setOnItemClickListener(mParent);
		gridView.setOnItemLongClickListener(mParent);

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.y = mActionBarHeight - (int) (mActionBarHeight * DIALOG_OFFSET_PERCENTAGE);
		layoutParams.x = mParent.getResources().getDisplayMetrics().widthPixels / 2 / NUMBER_OF_ICONS;
		getWindow().setAttributes(layoutParams);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mParent.finish();
	}

}
