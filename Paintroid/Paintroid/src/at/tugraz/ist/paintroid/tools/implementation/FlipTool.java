package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.FlipCommand;
import at.tugraz.ist.paintroid.command.implementation.FlipCommand.FlipDirection;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

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
	public int getAttributeButtonResource(ToolButtonIDs toolButtonID) {
		switch (toolButtonID) {
			case BUTTON_ID_PARAMETER_BOTTOM_1:
				return R.drawable.icon_menu_flip_horizontal;
			case BUTTON_ID_PARAMETER_BOTTOM_2:
				return R.drawable.icon_menu_flip_vertical;
			default:
				return super.getAttributeButtonResource(toolButtonID);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs toolButtonID) {
		FlipDirection flipDirection = null;
		switch (toolButtonID) {
			case BUTTON_ID_PARAMETER_BOTTOM_1:
				flipDirection = FlipDirection.FLIP_HORIZONTAL;
				break;
			case BUTTON_ID_PARAMETER_BOTTOM_2:
				flipDirection = FlipDirection.FLIP_VERTICAL;
				break;
			default:
				return;
		}

		Command command = new FlipCommand(flipDirection);
		mProgressDialog.show();
		((FlipCommand) command).addObserver(this);
		PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
			case BUTTON_ID_PARAMETER_TOP_1:
			case BUTTON_ID_PARAMETER_TOP_2:
				return Color.TRANSPARENT;
			default:
				return super.getAttributeButtonColor(buttonNumber);
		}
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
	}

}
