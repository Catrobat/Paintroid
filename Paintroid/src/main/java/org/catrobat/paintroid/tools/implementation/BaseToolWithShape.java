/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.OptionsMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.ToolWithShape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public abstract class BaseToolWithShape extends BaseTool implements
		ToolWithShape {

	protected int mPrimaryShapeColor = PaintroidApplication.applicationContext
			.getResources().getColor(R.color.rectangle_primary_color);
	protected int mSecondaryShapeColor = PaintroidApplication.applicationContext
			.getResources().getColor(R.color.rectangle_secondary_color);
	protected PointF mToolPosition;
	protected Paint mLinePaint;

	public BaseToolWithShape(Context context, ToolType toolType) {
		super(context, toolType);
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		float actionBarHeight = OptionsMenuActivity.ACTION_BAR_HEIGHT
				* metrics.density;
		mToolPosition = new PointF(display.getWidth() / 2f, display.getHeight()
				/ 2f - actionBarHeight);
		PaintroidApplication.perspective
				.convertFromScreenToCanvas(mToolPosition);
		mLinePaint = new Paint();
		mLinePaint.setColor(mPrimaryShapeColor);
	}

	@Override
	public abstract void drawShape(Canvas canvas);

	protected float getStrokeWidthForZoom(float defaultStrokeWidth,
			float minStrokeWidth, float maxStrokeWidth) {
		float displayScale = mContext.getResources().getDisplayMetrics().density;
		float strokeWidth = (defaultStrokeWidth * displayScale)
				/ PaintroidApplication.perspective.getScale();
		if (strokeWidth < minStrokeWidth) {
			strokeWidth = minStrokeWidth;
		} else if (strokeWidth > maxStrokeWidth) {
			strokeWidth = maxStrokeWidth;
		}
		return strokeWidth;
	}

	protected float getInverselyProportionalSizeForZoom(float defaultSize) {
		float displayScale = mContext.getResources().getDisplayMetrics().density;
		float applicationScale = PaintroidApplication.perspective.getScale();
		return (defaultSize * displayScale) / applicationScale;
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY,
			int viewWidth, int viewHeight) {

		int deltaX = 0;
		int deltaY = 0;
		PointF surfaceToolPosition = PaintroidApplication.perspective
				.getSurfacePointFromCanvasPoint(new PointF(mToolPosition.x,
						mToolPosition.y));

		if (surfaceToolPosition.x < mScrollTolerance) {
			deltaX = 1;
		}
		if (surfaceToolPosition.x > viewWidth - mScrollTolerance) {
			deltaX = -1;
		}

		if (surfaceToolPosition.y < mScrollTolerance) {
			deltaY = 1;
		}

		if (surfaceToolPosition.y > viewHeight - mScrollTolerance) {
			deltaY = -1;
		}

		return new Point(deltaX, deltaY);
	}

}
