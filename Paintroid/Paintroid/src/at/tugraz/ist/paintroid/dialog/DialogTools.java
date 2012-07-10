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
	private static final float TOOLBAR_HEIGHT_DIP = 80.0f;
	private static final float DIALOG_ROW_HEIGHT_DIP = 100.0f;
	private static final int DIALOG_OFFSET_DIP = 10;
	private int dialogRows;
	protected ToolButtonAdapter toolButtonAdapter;
	private final Context context;
	private final ToolsDialogActivity parent;

	public DialogTools(Context context, ToolsDialogActivity parent, ToolButtonAdapter toolButtonAdapter) {
		super(context);
		this.context = context;
		this.parent = parent;
		this.toolButtonAdapter = toolButtonAdapter;
		dialogRows = (int) (toolButtonAdapter.getCount() / 4.0f + 0.9f);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCanceledOnTouchOutside(true);

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		final float displayScale = parent.getResources().getDisplayMetrics().density;
		final int displayHeight = parent.getResources().getDisplayMetrics().heightPixels;
		int toolbarHeight = (int) (TOOLBAR_HEIGHT_DIP * displayScale + 0.5f);
		int dialogHeight = (int) (dialogRows * DIALOG_ROW_HEIGHT_DIP * displayScale + 0.5f);
		int dialogOffset = (int) (DIALOG_OFFSET_DIP * displayScale + 0.5f);
		layoutParams.y = (displayHeight / 2) - (dialogHeight / 2) - toolbarHeight - dialogOffset;
		getWindow().setAttributes(layoutParams);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tools_menu);

		GridView gridView = (GridView) findViewById(R.id.gridview_tools_menu);
		gridView.setAdapter(toolButtonAdapter);
		gridView.setOnItemClickListener(parent);
		gridView.setOnItemLongClickListener(parent);
		gridView.setColumnWidth(95);
		gridView.setGravity(Gravity.CENTER);
	}

	@Override
	protected void onStop() {
		super.onStop();
		parent.finish();
	}

}
