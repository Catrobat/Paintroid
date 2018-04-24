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

package org.catrobat.paintroid.ui.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;

public class DrawerPreview extends View {

	private static final int BORDER = 2;

	private Paint canvasPaint;
	private Paint checkeredPattern = new Paint();

	public DrawerPreview(Context context) {
		super(context);
		init();
	}

	public DrawerPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		Bitmap checkerboard = BitmapFactory.decodeResource(getResources(), R.drawable.checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard,
				Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkeredPattern.setShader(shader);
		canvasPaint = new Paint();
	}

	private void changePaintColor(int color) {
		Paint drawPaint = PaintroidApplication.currentTool.getDrawPaint();
		float strokeWidth = drawPaint.getStrokeWidth();
		Paint.Cap strokeCap = drawPaint.getStrokeCap();
		if (Color.alpha(color) == 0x00) {
			canvasPaint.reset();
			canvasPaint.setStyle(Paint.Style.STROKE);
			canvasPaint.setStrokeWidth(strokeWidth);
			canvasPaint.setColor(color);
			canvasPaint.setStrokeCap(strokeCap);
			canvasPaint.setAntiAlias(true);
			canvasPaint.setShader(checkeredPattern.getShader());
			canvasPaint.setColor(Color.BLACK);
			canvasPaint.setAlpha(0x00);
		} else {
			canvasPaint.reset();
			canvasPaint.setStyle(Paint.Style.STROKE);
			canvasPaint.setStrokeWidth(strokeWidth);
			canvasPaint.setStrokeCap(strokeCap);
			canvasPaint.setColor(color);
			canvasPaint.setAntiAlias(true);
		}
	}

	private void drawDrawerPreview(Canvas canvas) {
		int currentColor = MainActivity.colorPickerInitialColor;
		changePaintColor(currentColor);

		int centerX = getLeft() + getWidth() / 2;
		int centerY = getTop() + getHeight() / 2;
		int startX = getLeft() + getWidth() / 8;
		int startY = centerY;
		int endX = getRight() - getWidth() / 8;
		int endY = centerY;

		Path path = new Path();
		path.moveTo(startX, startY);
		float x2 = getLeft() + getWidth() / 4;
		float y2 = getTop();
		float x4 = getRight() - getWidth() / 4;
		float y4 = getBottom();
		path.cubicTo(startX, startY, x2, y2, centerX, centerY);
		path.cubicTo(centerX, centerY, x4, y4, endX, endY);

		if (canvasPaint.getColor() == Color.WHITE) {
			drawBorder(canvas);
			canvas.drawPath(path, canvasPaint);
		}
		if (canvasPaint.getColor() == Color.TRANSPARENT) {
			canvasPaint.setColor(Color.BLACK);
			canvas.drawPath(path, canvasPaint);
			canvasPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawPath(path, canvasPaint);
		}
	}

	private void drawBorder(Canvas canvas) {
		Paint borderPaint = new Paint();
		Paint drawPaint = PaintroidApplication.currentTool.getDrawPaint();
		float strokeWidth = drawPaint.getStrokeWidth();
		Paint.Cap strokeCap = drawPaint.getStrokeCap();
		int startX;
		int startY;
		int endX;
		int endY;

		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeCap(strokeCap);
		borderPaint.setStrokeWidth(strokeWidth + BORDER);
		borderPaint.setColor(Color.BLACK);
		borderPaint.setAntiAlias(true);

		if (PaintroidApplication.currentTool.getToolType() == ToolType.LINE) {
			startX = getLeft() + getWidth() / 8 - BORDER;
			startY = getTop() + getHeight() / 2;
			endX = getRight() - getWidth() / 8 + BORDER;
			endY = getTop() + getHeight() / 2;
			canvas.drawLine(startX, startY, endX, endY, borderPaint);
		} else {
			int centerX = getLeft() + getWidth() / 2;
			int centerY = getTop() + getHeight() / 2;
			float x2 = getLeft() + getWidth() / 4;
			float y2 = getTop() - BORDER;
			float x4 = getRight() - getWidth() / 4;
			float y4 = getBottom() + BORDER;

			startX = getLeft() + getWidth() / 8 - BORDER;
			startY = centerY + BORDER;
			endX = getRight() - getWidth() / 8 + BORDER;
			endY = centerY - BORDER;

			Path borderPath = new Path();
			borderPath.moveTo(startX, startY);
			borderPath.cubicTo(startX, startY, x2, y2, centerX, centerY);
			borderPath.cubicTo(centerX, centerY, x4, y4, endX, endY);
			canvas.drawPath(borderPath, borderPaint);
		}
	}

	private void drawEraserPreview(Canvas canvas) {
		changePaintColor(Color.TRANSPARENT);

		int centerX = getLeft() + getWidth() / 2;
		int centerY = getTop() + getHeight() / 2;
		int startX = getLeft() + getWidth() / 8;
		int startY = centerY;
		int endX = getRight() - getWidth() / 8;
		int endY = centerY;

		Path path = new Path();
		path.moveTo(startX, startY);
		float x2 = getLeft() + getWidth() / 4;
		float y2 = getTop();
		float x4 = getRight() - getWidth() / 4;
		float y4 = getBottom();
		path.cubicTo(startX, startY, x2, y2, centerX, centerY);
		path.cubicTo(centerX, centerY, x4, y4, endX, endY);

		canvasPaint.setColor(Color.BLACK);
		canvas.drawPath(path, canvasPaint);
	}

	private void drawLinePreview(Canvas canvas) {
		int currentColor = MainActivity.colorPickerInitialColor;
		changePaintColor(currentColor);

		int startX = getLeft() + getWidth() / 8;
		int startY = getTop() + getHeight() / 2;
		int endX = getRight() - getWidth() / 8;
		int endY = getTop() + getHeight() / 2;

		if (canvasPaint.getColor() == Color.WHITE) {
			drawBorder(canvas);
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
		ToolType currentTool = PaintroidApplication.currentTool.getToolType();

		switch (currentTool) {
			case BRUSH:
			case CURSOR:
				drawDrawerPreview(canvas);
				break;
			case LINE:
				drawLinePreview(canvas);
				break;
			case ERASER:
				drawEraserPreview(canvas);
				break;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(widthSize, (int) (widthSize * .25));
	}
}
