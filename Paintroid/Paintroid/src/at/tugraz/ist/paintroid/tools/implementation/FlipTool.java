package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.FlipCommand;
import at.tugraz.ist.paintroid.command.implementation.FlipCommand.FlipDirection;

public class FlipTool extends BaseTool {

	public FlipTool(Context context, ToolType toolType) {
		super(context, toolType);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		if (buttonNumber == 0) {
			return R.drawable.ic_hand;
		} else if (buttonNumber == 1) {
			return R.drawable.ic_flip_horizontal;
		} else if (buttonNumber == 2) {
			return R.drawable.ic_flip_vertical;
		}
		return 0;
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {
		FlipDirection flipDirection = null;
		if (buttonNumber == 1) {
			flipDirection = FlipDirection.FLIP_HORIZONTAL;
		} else if (buttonNumber == 2) {
			flipDirection = FlipDirection.FLIP_VERTICAL;
		}

		Command command = new FlipCommand(flipDirection);
		mProgressDialog.show();
		((FlipCommand) command).addObserver(this);
		PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
	}

}
