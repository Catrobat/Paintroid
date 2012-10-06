/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolWithShape;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public abstract class BaseToolWithShape extends BaseTool implements
		ToolWithShape {

	protected int mPrimaryShapeColor = PaintroidApplication.APPLICATION_CONTEXT
			.getResources().getColor(R.color.custom_background_color);
	protected int mSecondaryShapeColor = ~Color.alpha(mPrimaryShapeColor)
			| mPrimaryShapeColor;
	protected PointF mToolPosition;
	protected Paint mLinePaint;

	public BaseToolWithShape(Context context, ToolType toolType) {
		super(context, toolType);
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mToolPosition = new PointF(display.getWidth() / 2f,
				display.getHeight() / 2f);
		PaintroidApplication.CURRENT_PERSPECTIVE
				.convertFromScreenToCanvas(mToolPosition);
		mLinePaint = new Paint();
		mLinePaint.setColor(mPrimaryShapeColor);
	}

	@Override
	public abstract void drawShape(Canvas canvas);

	static protected float getStrokeWidthForZoom(
			final float defaultStrokeWidth, float minStrokeWidth,
			final float maxStrokeWidth) {
		Log.i(PaintroidApplication.TAG, "Base Tool getStrokeWidthForZoom");
		float displayScale = mContext.getResources().getDisplayMetrics().density;
		Log.i(PaintroidApplication.TAG, "Base Tool getStrokeWidthForZoom 1");
		float strokeWidth = (defaultStrokeWidth * displayScale)
				/ PaintroidApplication.CURRENT_PERSPECTIVE.getScale();
		Log.i(PaintroidApplication.TAG,
				"Base Tool getStrokeWidthForZoom 2 strokeWidth:" + strokeWidth
						+ " minStrokeWidth:" + minStrokeWidth
						+ " maxStrokeWidth:" + maxStrokeWidth);
		if (strokeWidth < minStrokeWidth) {
			Log.i(PaintroidApplication.TAG,
					"Base Tool getStrokeWidthForZoom 2 strokeWidth < minStrokeWidth-> strokeWidth:"
							+ strokeWidth + " minStrokeWidth:" + minStrokeWidth
							+ " maxStrokeWidth:" + maxStrokeWidth);
			strokeWidth = minStrokeWidth;
		}
		Log.i(PaintroidApplication.TAG, "Base Tool getStrokeWidthForZoom 3");
		if (strokeWidth > maxStrokeWidth) {
			Log.i(PaintroidApplication.TAG,
					"Base Tool getStrokeWidthForZoom 3 strokeWidth > maxStrokeWidth-> strokeWidth:"
							+ strokeWidth + " minStrokeWidth:" + minStrokeWidth
							+ " maxStrokeWidth:" + maxStrokeWidth);
			strokeWidth = maxStrokeWidth;
		}
		Log.i(PaintroidApplication.TAG, "Base Tool getStrokeWidthForZoom 4");
		return strokeWidth;
	}

	static protected float getInverselyProportionalSizeForZoom(float defaultSize) {
		float displayScale = mContext.getResources().getDisplayMetrics().density;
		float applicationScale = PaintroidApplication.CURRENT_PERSPECTIVE
				.getScale();
		return (defaultSize * displayScale) / applicationScale;
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		// TODO Auto-generated method stub
		super.attributeButtonClick(buttonNumber);
	}

	@Override
	protected void finalize() throws Throwable {
		Log.i(PaintroidApplication.TAG, "finalize BaseToolWithShape");
		super.finalize();
	}

}
