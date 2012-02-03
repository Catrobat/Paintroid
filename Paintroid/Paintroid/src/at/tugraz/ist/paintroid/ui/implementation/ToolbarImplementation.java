package at.tugraz.ist.paintroid.ui.implementation;

import java.util.Observable;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.MenuTabActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
import at.tugraz.ist.paintroid.ui.Toolbar;
import at.tugraz.ist.paintroid.ui.button.AttributeButton;

public class ToolbarImplementation extends Observable implements Toolbar, OnClickListener, OnLongClickListener {

	protected TextView toolButton;
	protected AttributeButton attributeButton1;
	protected AttributeButton attributeButton2;
	protected Button undoButton;
	protected DrawingSurfaceView drawingSurface;
	protected Tool currentTool;
	protected MainActivity mainActivity;

	public ToolbarImplementation(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		currentTool = new DrawTool(mainActivity);

		toolButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		toolButton.setOnClickListener(this);
		toolButton.setOnLongClickListener(this);
		toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_menu_more_brush_64);
		toolButton.setBackgroundResource(R.drawable.attribute_button_selector);

		attributeButton1 = (AttributeButton) mainActivity.findViewById(R.id.btn_Parameter1);
		attributeButton1.setToolbar(this);
		attributeButton2 = (AttributeButton) mainActivity.findViewById(R.id.btn_Parameter2);
		attributeButton2.setToolbar(this);

		undoButton = (Button) mainActivity.findViewById(R.id.btn_Undo);
		undoButton.setOnClickListener(this);
		undoButton.setOnLongClickListener(this);
		undoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.undo64);
		undoButton.setBackgroundResource(R.drawable.attribute_button_selector);

		drawingSurface = (DrawingSurfaceView) mainActivity.findViewById(R.id.drawingSurfaceView);
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_Tool:
			Intent intent = new Intent(mainActivity, MenuTabActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			mainActivity.startActivityForResult(intent, MainActivity.REQ_TOOL_MENU);
			break;
		case R.id.btn_Parameter1:
			break;
		case R.id.btn_Parameter2:
			break;
		case R.id.btn_Undo:
			break;
		}
	}

	@Override
	public Tool getCurrentTool() {
		return this.currentTool;
	}

	@Override
	public void setTool(Tool tool) {
		PaintroidApplication.CURRENT_TOOL = tool;
		this.currentTool = tool;
		super.setChanged();
		super.notifyObservers();
	}
}
