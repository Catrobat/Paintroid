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
	private static int Y_SCREEN_OFFSET = 100;
	protected ToolButtonAdapter toolButtonAdapter;
	private final Context context;
	private final ToolsDialogActivity parent;

	public DialogTools(Context context, ToolsDialogActivity parent, ToolButtonAdapter toolButtonAdapter) {
		super(context);
		this.context = context;
		this.parent = parent;
		this.toolButtonAdapter = toolButtonAdapter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCanceledOnTouchOutside(true);
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.y = layoutParams.y + Y_SCREEN_OFFSET;
		getWindow().setAttributes(layoutParams);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tools_menu);

		toolButtonAdapter = new ToolButtonAdapter(context);

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
