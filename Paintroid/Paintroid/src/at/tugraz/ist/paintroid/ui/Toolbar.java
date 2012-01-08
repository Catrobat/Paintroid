package at.tugraz.ist.paintroid.ui;

import java.util.Observable;

import roboguice.inject.InjectView;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceView;

public class Toolbar extends Observable implements OnClickListener, OnLongClickListener {
	@InjectView(R.id.btn_Tool)
	TextView toolButton;
	@InjectView(R.id.btn_Parameter1)
	TextView attributeButton1;
	@InjectView(R.id.btn_Parameter2)
	protected TextView attributeButton2;
	@InjectView(R.id.btn_Undo)
	protected Button undoButton;
	@InjectView(R.id.drawingSurfaceView)
	protected DrawingSurfaceView drawingSurface;
	protected Tool currentTool;

	protected MainActivity activity;

	public Toolbar(MainActivity activity) {
		this.activity = activity;
		Paint defaultPaint = new Paint();
		defaultPaint.setColor(Color.BLACK);
		defaultPaint.setStrokeCap(Cap.ROUND);
		defaultPaint.setStrokeWidth(Tool.stroke1); // TODO
		currentTool = new DrawTool(defaultPaint);

		toolButton.setOnClickListener(this);
		toolButton.setOnLongClickListener(this);
		toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_menu_more_brush_64);
		toolButton.setBackgroundResource(R.drawable.attribute_button_selector);

		undoButton.setOnClickListener(this);
		undoButton.setOnLongClickListener(this);
		undoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.undo64);
		undoButton.setBackgroundResource(R.drawable.attribute_button_selector);
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public Tool getCurrentTool() {
		return this.currentTool;
	}

}
