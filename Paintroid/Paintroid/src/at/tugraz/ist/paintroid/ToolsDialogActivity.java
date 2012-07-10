package at.tugraz.ist.paintroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.dialog.DialogTools;
import at.tugraz.ist.paintroid.ui.button.ToolButton;
import at.tugraz.ist.paintroid.ui.button.ToolButtonAdapter;

public class ToolsDialogActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
	public static final String EXTRA_SELECTED_TOOL = "EXTRA_SELECTED_TOOL";
	protected ToolButtonAdapter toolButtonAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean openedFromCatrobat;
		Intent intent = getIntent();
		openedFromCatrobat = intent.getExtras().getBoolean(MainActivity.EXTRA_INSTANCE_FROM_CATROBAT);

		toolButtonAdapter = new ToolButtonAdapter(this, openedFromCatrobat);

		new DialogTools(this, this, toolButtonAdapter).show();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View button, int position, long id) {
		ToolButton toolButton = toolButtonAdapter.getToolButton(position);
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_SELECTED_TOOL, toolButton.buttonId.ordinal());
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View button, int position, long id) {
		ToolButton toolButton = toolButtonAdapter.getToolButton(position);
		new DialogHelp(this, toolButton.stringId).show();
		return true;
	}
}
