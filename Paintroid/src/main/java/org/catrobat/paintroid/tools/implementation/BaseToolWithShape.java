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
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolWithShape;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;

public abstract class BaseToolWithShape extends BaseTool implements ToolWithShape {

	private static final String BUNDLE_TOOL_POSITION_X = "TOOL_POSITION_X";
	private static final String BUNDLE_TOOL_POSITION_Y = "TOOL_POSITION_Y";

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public final PointF toolPosition;

	int primaryShapeColor;
	int secondaryShapeColor;

	Paint linePaint;
	DisplayMetrics metrics;

	public BaseToolWithShape(ContextCallback contextCallback, ToolOptionsControllerContract toolOptionsViewHolder, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager, CommandFactory commandFactory) {
		super(contextCallback, toolOptionsViewHolder, toolPaint, workspace, commandManager, commandFactory);

		metrics = contextCallback.getDisplayMetrics();

		primaryShapeColor = contextCallback.getColor(R.color.pocketpaint_main_rectangle_tool_primary_color);
		secondaryShapeColor = contextCallback.getColor(R.color.pocketpaint_colorAccent);

		toolPosition = new PointF(workspace.getWidth() / 2f, workspace.getHeight() / 2f);
		linePaint = new Paint();
		linePaint.setColor(primaryShapeColor);
	}

	@Override
	public abstract void drawShape(Canvas canvas);

	float getStrokeWidthForZoom(float defaultStrokeWidth, float minStrokeWidth, float maxStrokeWidth) {
		float strokeWidth = (defaultStrokeWidth * metrics.density) / workspace.getScale();
		return Math.min(maxStrokeWidth, Math.max(minStrokeWidth, strokeWidth));
	}

	float getInverselyProportionalSizeForZoom(float size) {
		return (size * metrics.density) / workspace.getScale();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle bundle) {
		super.onSaveInstanceState(bundle);

		bundle.putFloat(BUNDLE_TOOL_POSITION_X, toolPosition.x);
		bundle.putFloat(BUNDLE_TOOL_POSITION_Y, toolPosition.y);
	}

	@Override
	public void onRestoreInstanceState(@NonNull Bundle bundle) {
		super.onRestoreInstanceState(bundle);

		toolPosition.x = bundle.getFloat(BUNDLE_TOOL_POSITION_X, toolPosition.x);
		toolPosition.y = bundle.getFloat(BUNDLE_TOOL_POSITION_Y, toolPosition.y);
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY, int viewWidth, int viewHeight) {

		int deltaX = 0;
		int deltaY = 0;
		PointF surfaceToolPosition = workspace.getSurfacePointFromCanvasPoint(new PointF(toolPosition.x, toolPosition.y));

		if (surfaceToolPosition.x < scrollTolerance) {
			deltaX = 1;
		}
		if (surfaceToolPosition.x > viewWidth - scrollTolerance) {
			deltaX = -1;
		}

		if (surfaceToolPosition.y < scrollTolerance) {
			deltaY = 1;
		}

		if (surfaceToolPosition.y > viewHeight - scrollTolerance) {
			deltaY = -1;
		}

		return new Point(deltaX, deltaY);
	}

	protected void drawToolSpecifics(Canvas canvas, float boxWidth, float boxHeight) {
	}
}
