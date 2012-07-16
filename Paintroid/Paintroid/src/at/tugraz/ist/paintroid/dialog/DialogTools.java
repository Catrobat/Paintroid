package at.tugraz.ist.paintroid.dialog;

import android.content.Context;
import android.os.Bundle;
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
	private static final int COLUMN_WIDTH_DP = 95;
	private static final int ROW_BOTTOM_MARGIN_DP = 12;
	private static final int LAYOUT_MARGIN_HORIZONTAL_DP = 10;
	private static final float ROUND_VALUE = 0.5f;
	private static final float ROUND_VALUE_UP = 0.9f;

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
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		// get display properties
		float displayScale = mParent.getResources().getDisplayMetrics().density;
		int displayHeight = mParent.getResources().getDisplayMetrics().heightPixels;

		int layoutMargin = (int) (LAYOUT_MARGIN_HORIZONTAL_DP * displayScale + ROUND_VALUE);
		int displayWidth = mParent.getResources().getDisplayMetrics().widthPixels;
		displayWidth -= layoutMargin;

		// calculate position of dialog
		int columnWidth = (int) (COLUMN_WIDTH_DP * displayScale + ROUND_VALUE);
		int columns = displayWidth / columnWidth;
		int dialogRows = (int) (mToolButtonAdapter.getCount() / (float) columns + ROUND_VALUE_UP);
		int toolbarHeight = (int) (TOOLBAR_HEIGHT_DIP * displayScale + ROUND_VALUE);
		int rowBottomMargin = (int) (ROW_BOTTOM_MARGIN_DP * displayScale + ROUND_VALUE);
		int dialogHeight = (int) (dialogRows * DIALOG_ROW_HEIGHT_DIP * displayScale + ROUND_VALUE);
		dialogHeight += (rowBottomMargin * dialogRows);
		int dialogOffset = (int) (DIALOG_OFFSET_DIP * displayScale + ROUND_VALUE);
		// layoutParams.height = dialogHeight;
		layoutParams.y = (displayHeight / 2) - (dialogHeight / 2) - toolbarHeight - dialogOffset;
		getWindow().setAttributes(layoutParams);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tools_menu);
		getWindow().setBackgroundDrawableResource(R.color.transparent);

		GridView gridView = (GridView) findViewById(R.id.gridview_tools_menu);
		gridView.setAdapter(mToolButtonAdapter);

		gridView.setOnItemClickListener(mParent);
		gridView.setOnItemLongClickListener(mParent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mParent.finish();
	}

}
