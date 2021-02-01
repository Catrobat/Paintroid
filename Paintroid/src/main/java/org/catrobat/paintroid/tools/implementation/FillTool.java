/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.PointF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.FillToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

import androidx.annotation.VisibleForTesting;

public class FillTool extends BaseTool {

	public static final int DEFAULT_TOLERANCE_IN_PERCENT = 12;
	public static final int MAX_ABSOLUTE_TOLERANCE = 510;

	@VisibleForTesting
	public float colorTolerance = MAX_ABSOLUTE_TOLERANCE * DEFAULT_TOLERANCE_IN_PERCENT / 100.0f;

	public FillTool(FillToolOptionsView fillToolOptionsView, ContextCallback contextCallback,
			ToolOptionsVisibilityController toolOptionsViewController, ToolPaint toolPaint, Workspace workspace,
			CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);

		fillToolOptionsView.setCallback(new FillToolOptionsView.Callback() {
			@Override
			public void onColorToleranceChanged(int colorTolerance) {
				updateColorTolerance(colorTolerance);
			}
		});
	}

	public void updateColorTolerance(int colorToleranceInPercent) {
		colorTolerance = getToleranceAbsoluteValue(colorToleranceInPercent);
	}

	public float getToleranceAbsoluteValue(int toleranceInPercent) {
		return MAX_ABSOLUTE_TOLERANCE * toleranceInPercent / 100.0f;
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
		if (workspace.contains(coordinate)) {
			Command command = commandFactory.createFillCommand((int) coordinate.x, (int) coordinate.y, toolPaint.getPaint(), colorTolerance);
			commandManager.addCommand(command);
			return true;
		}

		return false;
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public ToolType getToolType() {
		return ToolType.FILL;
	}
}
