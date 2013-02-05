package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FillCommand;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;

public class FillTool extends BaseTool {

	public FillTool(Context context, ToolType toolType) {
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
		Command command = new FillCommand(new Point((int) coordinate.x,
				(int) coordinate.y), mBitmapPaint);
		mProgressDialog.show();
		((FillCommand) command).addObserver(this);
		PaintroidApplication.COMMAND_MANAGER.commitCommand(command);

		return true;
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void draw(Canvas canvas) {
	}
}
