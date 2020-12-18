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

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.catrobat.paintroid.ui.Perspective;

import androidx.annotation.VisibleForTesting;

public abstract class BaseToolWithShape extends BaseTool {

	private static final String BUNDLE_TOOL_POSITION_X = "TOOL_POSITION_X";
	private static final String BUNDLE_TOOL_POSITION_Y = "TOOL_POSITION_Y";

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public final PointF toolPosition;

	int primaryShapeColor;
	int secondaryShapeColor;

	final Paint linePaint;
	final DisplayMetrics metrics;

	@SuppressLint("VisibleForTests")
	public BaseToolWithShape(ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);

		metrics = contextCallback.getDisplayMetrics();

		primaryShapeColor = contextCallback.getColor(R.color.pocketpaint_main_rectangle_tool_primary_color);
		secondaryShapeColor = contextCallback.getColor(R.color.pocketpaint_colorAccent);
		Perspective perspective = workspace.getPerspective();

		if (perspective.getScale() > 1) {
			toolPosition = new PointF(perspective.surfaceCenterX - perspective.surfaceTranslationX, perspective.surfaceCenterY - perspective.surfaceTranslationY);
		} else {
			toolPosition = new PointF(workspace.getWidth() / 2f, workspace.getHeight() / 2f);
		}

		linePaint = new Paint();
		linePaint.setColor(primaryShapeColor);
	}

	public abstract void drawShape(Canvas canvas);

	float getStrokeWidthForZoom(float defaultStrokeWidth, float minStrokeWidth, float maxStrokeWidth) {
		float strokeWidth = (defaultStrokeWidth * metrics.density) / workspace.getScale();
		return Math.min(maxStrokeWidth, Math.max(minStrokeWidth, strokeWidth));
	}

	float getInverselyProportionalSizeForZoom(float defaultSize) {
		float applicationScale = workspace.getScale();
		return (defaultSize * metrics.density) / applicationScale;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);

		bundle.putFloat(BUNDLE_TOOL_POSITION_X, toolPosition.x);
		bundle.putFloat(BUNDLE_TOOL_POSITION_Y, toolPosition.y);
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);

		toolPosition.x = bundle.getFloat(BUNDLE_TOOL_POSITION_X, toolPosition.x);
		toolPosition.y = bundle.getFloat(BUNDLE_TOOL_POSITION_Y, toolPosition.y);
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY, int viewWidth, int viewHeight) {
		PointF surfaceToolPosition = workspace.getSurfacePointFromCanvasPoint(toolPosition);
		return scrollBehavior.getScrollDirection(surfaceToolPosition.x, surfaceToolPosition.y, viewWidth, viewHeight);
	}

	public abstract void onClickOnButton();

	protected void drawToolSpecifics(Canvas canvas, float boxWidth, float boxHeight) {
	}
}
