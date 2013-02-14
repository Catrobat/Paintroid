package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FillCommand;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

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
		int bitmapHeight = PaintroidApplication.DRAWING_SURFACE
				.getBitmapHeight();
		int bitmapWidth = PaintroidApplication.DRAWING_SURFACE.getBitmapWidth();

		if ((coordinate.x > bitmapWidth) || (coordinate.y > bitmapHeight)) {
			return false;
		}

		if (mBitmapPaint.getColor() == PaintroidApplication.DRAWING_SURFACE
				.getBitmapColor(coordinate)) {
			return false;
		}

		Command command = new FillCommand(new Point((int) coordinate.x,
				(int) coordinate.y), mBitmapPaint);
		mProgressDialog.show();
		((FillCommand) command).addObserver(this);
		PaintroidApplication.COMMAND_MANAGER.commitCommand(command);

		return true;
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return getStrokeColorResource();
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_color_palette;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			showColorPicker();
			break;
		default:
			super.attributeButtonClick(buttonNumber);
			break;
		}
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void draw(Canvas canvas) {
	}
}
