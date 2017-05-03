package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand.RotateDirection;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class RotationTool extends BaseTool {

	private ImageButton mRotationButtonLeft;
	private ImageButton mRotationButtonRight;
	private LinearLayout mRotationButtonsLayout;

	public RotationTool(Context context, ToolType toolType) {
		super(context, toolType);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		return false;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return false;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		return false;
	}

	@Override
	public void resetInternalState() {

	}

	@Override
	public void draw(Canvas canvas) {

	}

	private void rotate(RotateDirection rotateDirection) {
		Command command = new RotateCommand(rotateDirection);
		IndeterminateProgressDialog.getInstance().show();
		((RotateCommand) command).addObserver(this);
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
	}

	@Override
	public void setupToolOptions() {
		mRotationButtonsLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.rotation_tool_buttons, null);

		mRotationButtonLeft = (ImageButton) mRotationButtonsLayout.findViewById(R.id.rotate_left_btn);
		mRotationButtonLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.bottom_bar_button_activated));
						break;
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
						rotate(RotateDirection.ROTATE_LEFT);
						break;
					default:
						return false;
				}
				return true;
			}
		});
		mRotationButtonRight = (ImageButton) mRotationButtonsLayout.findViewById(R.id.rotate_right_btn);
		mRotationButtonRight.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.bottom_bar_button_activated));
						break;
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
						rotate(RotateDirection.ROTATE_RIGHT);
						break;
					default:
						return false;
				}
				return true;
			}
		});

		mToolSpecificOptionsLayout.addView(mRotationButtonsLayout);
		mToolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				toggleShowToolOptions();
			}
		});
	}

}
