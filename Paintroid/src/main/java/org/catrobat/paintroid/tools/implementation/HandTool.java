package org.catrobat.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.PointF;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsController;

public class HandTool extends BaseTool {
	public HandTool(ContextCallback contextCallback, ToolOptionsController toolOptionsController,
					ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
	}

	@Override
	public void draw(Canvas canvas) { }

	@Override
	public void resetInternalState() { }

	@Override
	public boolean handleDown(PointF coordinate) {
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		return true;
	}

	@Override
	public ToolType getToolType() {
		return ToolType.HAND;
	}

	@Override
	public void setupToolOptions() { }

	@Override
	public boolean handToolMode() {
		return true;
	}
}
