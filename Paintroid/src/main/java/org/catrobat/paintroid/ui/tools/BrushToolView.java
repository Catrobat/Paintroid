/*
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

package org.catrobat.paintroid.ui.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.options.BrushToolOptionsView;
import org.catrobat.paintroid.tools.options.BrushToolPreview;

public class BrushToolView extends View implements BrushToolPreview {

	private Paint canvasPaint;
	private Paint checkeredPattern;
	private BrushToolOptionsView.OnBrushPreviewListener callback;

	public BrushToolView(Context context) {
		super(context);
		init();
	}

	public BrushToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		Bitmap checkerboard = BitmapFactory.decodeResource(getResources(), R.drawable.pocketpaint_checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard,
				Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		canvasPaint = new Paint();
		checkeredPattern = new Paint();
		checkeredPattern.setShader(shader);
	}

	private void changePaintColor(int color) {
		float strokeWidth = callback.getStrokeWidth();
		Paint.Cap strokeCap = callback.getStrokeCap();
		canvasPaint.reset();
		canvasPaint.setStyle(Paint.Style.STROKE);
		canvasPaint.setStrokeWidth(strokeWidth);
		canvasPaint.setStrokeCap(strokeCap);
		canvasPaint.setAntiAlias(true);

		if (Color.alpha(color) == 0x00) {
			canvasPaint.setShader(checkeredPattern.getShader());
			canvasPaint.setColor(Color.BLACK);
		} else {
			canvasPaint.setColor(color);
		}
	}

	private void drawEraserPreview(Canvas canvas) {
		changePaintColor(Color.TRANSPARENT);

		int startX = getRight() - getWidth() / 3;
		int startY = getTop() + getHeight() - 56;
		int endX = getRight() - getWidth() / 16;
		int endY = getTop() + getHeight() - 56;

		canvasPaint.setColor(Color.BLACK);
		canvas.drawLine(startX, startY, endX, endY, canvasPaint);
	}

	private void drawLinePreview(Canvas canvas) {
		int currentColor = callback.getColor();
		changePaintColor(currentColor);

		int startX = getRight() - getWidth() / 3;
		int startY = getTop() + getHeight() - 56;
		int endX = getRight() - getWidth() / 16;
		int endY = getTop() + getHeight() - 56;

		if (canvasPaint.getColor() == Color.WHITE) {
			canvas.drawLine(startX, startY, endX, endY, canvasPaint);
		}
		if (canvasPaint.getColor() == Color.TRANSPARENT) {
			canvasPaint.setColor(Color.BLACK);
			canvas.drawLine(startX, startY, endX, endY, canvasPaint);
			canvasPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawLine(startX, startY, endX, endY, canvasPaint);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (callback != null) {
			ToolType currentTool = callback.getToolType();

			switch (currentTool) {
				case BRUSH:
				case CURSOR:
				case LINE:
				case WATERCOLOR:
					drawLinePreview(canvas);
					break;
				case ERASER:
					drawEraserPreview(canvas);
					break;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(widthSize, (int) (widthSize * .25));
	}

	@Override
	public void setListener(BrushToolOptionsView.OnBrushPreviewListener callback) {
		this.callback = callback;
	}
}
