package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand.RotateDirection;
import org.catrobat.paintroid.dialog.ProgressIntermediateDialog;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

public class RotationTool extends BaseTool {

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

	@Override
	public int getAttributeButtonResource(ToolButtonIDs toolButtonID) {
		switch (toolButtonID) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			return R.drawable.icon_menu_rotate_left;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_rotate_right;
		default:
			return super.getAttributeButtonResource(toolButtonID);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs toolButtonID) {
		RotateDirection rotateDirection = null;
		switch (toolButtonID) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			rotateDirection = RotateDirection.ROTATE_LEFT;
			break;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			rotateDirection = RotateDirection.ROTATE_RIGHT;
			break;
		default:
			return;
		}

		Command command = new RotateCommand(rotateDirection);
		ProgressIntermediateDialog.getInstance().show();
		((RotateCommand) command).addObserver(this);
		PaintroidApplication.commandManager.commitCommand(command);
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return Color.TRANSPARENT;
		default:
			return super.getAttributeButtonColor(buttonNumber);
		}
	}

}
