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

	protected ToolButtonAdapter mToolButtonAdapter;
	private final ToolsDialogActivity mParent;

	public DialogTools(Context context, ToolsDialogActivity parent, ToolButtonAdapter toolButtonAdapter) {
		super(context);
		this.mParent = parent;
		this.mToolButtonAdapter = toolButtonAdapter;
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
		layoutParams.y = 100; // TODO distance from bottom
		layoutParams.x = 50; // TODO distance from center
		getWindow().setAttributes(layoutParams);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mParent.finish();
	}

}
