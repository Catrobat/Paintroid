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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.tools.ToolType;


public class DrawerPreview extends View {

	private final int BORDER = 2;

	private Paint mCanvasPaint;
	private Paint CHECKERED_PATTERN = new Paint();
	private ColorPickerDialog.OnColorPickedListener mColorPickedListener;

	public DrawerPreview(Context context) {
		super(context);
		init();
	}

	public DrawerPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		Bitmap checkerboard = BitmapFactory.decodeResource(
				PaintroidApplication.applicationContext.getResources(),
				R.drawable.checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard,
				Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		CHECKERED_PATTERN.setShader(shader);
		mCanvasPaint = new Paint();

		mColorPickedListener = new ColorPickerDialog.OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				invalidate();
			}
		};
		ColorPickerDialog.getInstance().addOnColorPickedListener(mColorPickedListener);

	}

	private void changePaintColor(int color) {
		int strokeWidth = BrushPickerView.getInstance().getStrokeWidth();
		Paint.Cap strokeCap = PaintroidApplication.currentTool.getDrawPaint().getStrokeCap();
		if (Color.alpha(color) == 0x00) {
			mCanvasPaint.reset();
			mCanvasPaint.setStyle(Paint.Style.STROKE);
			mCanvasPaint.setStrokeWidth(strokeWidth);
			mCanvasPaint.setColor(color);
			mCanvasPaint.setStrokeCap(strokeCap);
			mCanvasPaint.setAntiAlias(true);
			mCanvasPaint.setShader(CHECKERED_PATTERN.getShader());
			mCanvasPaint.setColor(Color.BLACK);
			mCanvasPaint.setAlpha(0x00);
		} else {
			mCanvasPaint.reset();
			mCanvasPaint.setStyle(Paint.Style.STROKE);
			mCanvasPaint.setStrokeWidth(strokeWidth);
			mCanvasPaint.setStrokeCap(strokeCap);
			mCanvasPaint.setColor(color);
			mCanvasPaint.setAntiAlias(true);
		}
	}

	private void drawDrawerPreview(Canvas canvas) {
		int currentColor = PaintroidApplication.colorPickerInitialColor;
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

		if (mCanvasPaint.getColor() == Color.WHITE) {
			drawBorder(canvas);
			canvas.drawPath(path, mCanvasPaint);
		}
		if (mCanvasPaint.getColor() == Color.TRANSPARENT) {
			mCanvasPaint.setColor(Color.BLACK);
			canvas.drawPath(path, mCanvasPaint);
			mCanvasPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawPath(path, mCanvasPaint);
		}
	}

	private void drawBorder(Canvas canvas) {
		Paint borderPaint = new Paint();
		int strokeWidth = BrushPickerView.getInstance().getStrokeWidth();
		Paint.Cap strokeCap = PaintroidApplication.currentTool.getDrawPaint().getStrokeCap();
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

		mCanvasPaint.setColor(Color.BLACK);
		canvas.drawPath(path, mCanvasPaint);
	}

	private void drawLinePreview(Canvas canvas) {
		int currentColor = PaintroidApplication.colorPickerInitialColor;
		changePaintColor(currentColor);

		int startX = getLeft() + getWidth() / 8;
		int startY = getTop() + getHeight() / 2;
		int endX = getRight() - getWidth() / 8;
		int endY = getTop() + getHeight() / 2;

		if (mCanvasPaint.getColor() == Color.WHITE) {
			drawBorder(canvas);
			canvas.drawLine(startX, startY, endX, endY, mCanvasPaint);
		}
		if (mCanvasPaint.getColor() == Color.TRANSPARENT) {
			mCanvasPaint.setColor(Color.BLACK);
			canvas.drawLine(startX, startY, endX, endY, mCanvasPaint);
			mCanvasPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawLine(startX, startY, endX, endY, mCanvasPaint);
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

		int height = (int) (getMeasuredHeight() * 0.2);
		setMeasuredDimension(widthMeasureSpec, height);
	}

}
