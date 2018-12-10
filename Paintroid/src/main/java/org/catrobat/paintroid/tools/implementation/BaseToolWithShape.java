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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.CurrentToolWrapper;
import org.catrobat.paintroid.DrawingSurfaceWrapper;
import org.catrobat.paintroid.LayerModelWrapper;
import org.catrobat.paintroid.PerspectiveWrapper;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.ToolWithShape;

public abstract class BaseToolWithShape extends BaseTool implements
		ToolWithShape {

	private static final String BUNDLE_TOOL_POSITION_X = "TOOL_POSITION_X";
	private static final String BUNDLE_TOOL_POSITION_Y = "TOOL_POSITION_Y";

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public final PointF toolPosition;

	int primaryShapeColor;
	int secondaryShapeColor;

	final Paint linePaint;
	final DisplayMetrics metrics;

	public BaseToolWithShape(Context context, ToolType toolType, DrawingSurfaceWrapper drawingSurfaceWrapper,
							CurrentToolWrapper currentToolWrapper, PerspectiveWrapper perspectiveWrapper, LayerModelWrapper layerModelWrapper,
							CommandManager commandManager) {
		super(context, toolType, drawingSurfaceWrapper, currentToolWrapper, perspectiveWrapper, layerModelWrapper, commandManager);

		final Resources resources = context.getResources();
		metrics = resources.getDisplayMetrics();

		primaryShapeColor = ResourcesCompat.getColor(resources, R.color.pocketpaint_main_rectangle_tool_primary_color, null);
		secondaryShapeColor = ResourcesCompat.getColor(resources, R.color.pocketpaint_colorAccent, null);
		float actionBarHeight = Constants.ACTION_BAR_HEIGHT * metrics.density;
		PointF surfaceToolPosition = new PointF(metrics.widthPixels / 2f, metrics.heightPixels
				/ 2f - actionBarHeight);
		toolPosition = perspectiveWrapper.getCanvasPointFromSurfacePoint(surfaceToolPosition);
		linePaint = new Paint();
		linePaint.setColor(primaryShapeColor);
	}

	@Override
	public abstract void drawShape(Canvas canvas);

	float getStrokeWidthForZoom(float defaultStrokeWidth, float minStrokeWidth, float maxStrokeWidth) {
		float strokeWidth = (defaultStrokeWidth * metrics.density)
				/ perspectiveWrapper.getScale();
		return Math.min(maxStrokeWidth, Math.max(minStrokeWidth, strokeWidth));
	}

	float getInverselyProportionalSizeForZoom(float defaultSize) {
		float applicationScale = perspectiveWrapper.getScale();
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
	public Point getAutoScrollDirection(float pointX, float pointY,
			int viewWidth, int viewHeight) {

		int deltaX = 0;
		int deltaY = 0;
		PointF surfaceToolPosition = perspectiveWrapper
				.getSurfacePointFromCanvasPoint(new PointF(toolPosition.x,
						toolPosition.y));

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
	protected abstract void onClickInBox();

	protected void drawToolSpecifics(Canvas canvas, float boxWidth, float boxHeight) {
	}
}
