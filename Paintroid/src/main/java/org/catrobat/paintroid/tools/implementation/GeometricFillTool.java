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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.listener.ShapeToolOptionsListener;
import org.catrobat.paintroid.tools.ToolType;

public class GeometricFillTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BOUNDS = false;
	private static final float SHAPE_OFFSET = 10f;

	private static final String BUNDLE_BASE_SHAPE = "BASE_SHAPE";
	private static final String BUNDLE_SHAPE_DRAW_TYPE = "SHAPE_DRAW_TYPE";
	private static final String BUNDLE_OUTLINE_WIDTH = "OUTLINE_WIDTH";

	@VisibleForTesting
	public BaseShape baseShape;
	private int shapeOutlineWidth = 25;
	private ShapeDrawType shapeDrawType;
	private ShapeToolOptionsListener shapeToolOptionsListener;
	private Paint geometricFillCommandPaint;
	private float previousBoxWidth;
	private float previousBoxHeight;

	public GeometricFillTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BOUNDS);

		if (baseShape == null) {
			baseShape = BaseShape.RECTANGLE;
		}

		shapeDrawType = ShapeDrawType.FILL;

		createOverlayBitmap();
		createAndSetBitmap();
	}

	public BaseShape getBaseShape() {
		return baseShape;
	}

	@Override
	public void setDrawPaint(Paint paint) {
		// necessary because of timing in MainActivity and Eraser
		super.setDrawPaint(paint);
		createAndSetBitmap();
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		createAndSetBitmap();
	}

	private void setupOnShapeToolDialogChangedListener() {
		shapeToolOptionsListener.setOnShapeToolOptionsChangedListener(
				new ShapeToolOptionsListener.OnShapeToolOptionsChangedListener() {
					@Override
					public void setToolType(BaseShape shape) {
						baseShape = shape;
						createAndSetBitmap();
					}
					@Override
					public void setDrawType(ShapeDrawType drawType) {
						shapeDrawType = drawType;
						createAndSetBitmap();
					}
					@Override
					public void setOutlineWidth(int outlineWidth) {
						shapeOutlineWidth = outlineWidth;
						createAndSetBitmap();
					}
				});
	}

	private void createAndSetBitmap() {
		previousBoxHeight = boxHeight;
		previousBoxWidth = boxWidth;
		Bitmap bitmap = Bitmap.createBitmap((int) boxWidth, (int) boxHeight,
				Bitmap.Config.ARGB_8888);
		Canvas drawCanvas = new Canvas(bitmap);

		RectF shapeRect;

		Paint drawPaint = new Paint();
		drawPaint.setColor(CANVAS_PAINT.getColor());
		drawPaint.setAntiAlias(DEFAULT_ANTIALIASING_ON);

		switch (shapeDrawType) {
			case FILL:
				drawPaint.setStyle(Style.FILL);
				break;
			case OUTLINE:
				drawPaint.setStyle(Style.STROKE);
				drawPaint.setStrokeWidth(shapeOutlineWidth);
				drawPaint.setStrokeCap(Paint.Cap.BUTT);
				break;
			default:
				break;
		}

		geometricFillCommandPaint = new Paint(Paint.DITHER_FLAG);
		if (Color.alpha(CANVAS_PAINT.getColor()) == 0x00) {
			int colorWithMaxAlpha = Color.BLACK;
			geometricFillCommandPaint.setColor(colorWithMaxAlpha);
			geometricFillCommandPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
			geometricFillCommandPaint.setAntiAlias(DEFAULT_ANTIALIASING_ON);

			drawPaint.reset();
			drawPaint.setAntiAlias(DEFAULT_ANTIALIASING_ON);
			drawPaint.setShader(checkeredPattern.getShader());
		}

		if (drawPaint.getStyle() == Style.STROKE) {
			shapeRect = new RectF(SHAPE_OFFSET + shapeOutlineWidth / 2,
					SHAPE_OFFSET + shapeOutlineWidth / 2,
					boxWidth - SHAPE_OFFSET - shapeOutlineWidth / 2,
					boxHeight - SHAPE_OFFSET - shapeOutlineWidth / 2);
		} else {
			shapeRect = new RectF(SHAPE_OFFSET,
					SHAPE_OFFSET,
					boxWidth - SHAPE_OFFSET,
					boxHeight - SHAPE_OFFSET);
		}

		switch (baseShape) {
			case RECTANGLE:
				drawCanvas.drawRect(shapeRect, drawPaint);
				break;
			case OVAL:
				drawCanvas.drawOval(shapeRect, drawPaint);
				break;
			case STAR:
				drawCanvas.drawPath(getSpecialPath(BaseShape.STAR, shapeRect, drawPaint), drawPaint);
				break;
			case HEART:
				drawCanvas.drawPath(getSpecialPath(BaseShape.HEART, shapeRect, drawPaint), drawPaint);
				break;
			default:
				break;
		}
		setBitmap(bitmap);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);

		bundle.putSerializable(BUNDLE_BASE_SHAPE, baseShape);
		bundle.putSerializable(BUNDLE_SHAPE_DRAW_TYPE, shapeDrawType);
		bundle.putInt(BUNDLE_OUTLINE_WIDTH, shapeOutlineWidth);
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);

		BaseShape baseShape = (BaseShape) bundle.getSerializable(BUNDLE_BASE_SHAPE);
		ShapeDrawType shapeDrawType = (ShapeDrawType) bundle.getSerializable(BUNDLE_SHAPE_DRAW_TYPE);
		int shapeOutlineWidth = bundle.getInt(BUNDLE_OUTLINE_WIDTH);

		if (baseShape != null && shapeDrawType != null
				&& (this.baseShape != baseShape || this.shapeDrawType != shapeDrawType)) {
			this.baseShape = baseShape;
			this.shapeDrawType = shapeDrawType;
			this.shapeOutlineWidth = shapeOutlineWidth;

			shapeToolOptionsListener.setShapeActivated(baseShape);
			shapeToolOptionsListener.setDrawTypeActivated(shapeDrawType);
			shapeToolOptionsListener.setShapeOutlineWidth(shapeOutlineWidth);
			createAndSetBitmap();
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if(boxHeight < previousBoxHeight || boxHeight > previousBoxHeight ||
				boxWidth < previousBoxWidth || boxWidth > previousBoxWidth) {
			createAndSetBitmap();
		}
	}

	private Path getSpecialPath(BaseShape type, RectF shapeRect, Paint drawPaint) {

		float stroke = drawPaint.getStrokeWidth();
		Style fillType = drawPaint.getStyle();

		float midWidth = shapeRect.width() / 2;
		float midHeight = shapeRect.height() / 2;
		float height = shapeRect.height();
		float width = shapeRect.width();
		float zeroWidth = 0;
		float zeroHeight = 0;

		Path path = new Path();

		switch (type){
			case STAR:
				path.moveTo(midWidth, zeroHeight);
				path.lineTo(midWidth + width / 8, midHeight - height / 8);
				path.lineTo(width, midHeight - height / 8);
				path.lineTo(midWidth + 1.8f * width / 8, midHeight + 1 * height / 8);
				path.lineTo(midWidth + 3 * width / 8, height);
				path.lineTo(midWidth, midHeight + 2 * height / 8);
				path.lineTo(midWidth - 3 * width / 8, height);
				path.lineTo(midWidth - 1.8f * width / 8, midHeight + 1 * height / 8);
				path.lineTo(zeroWidth, midHeight - height / 8);
				path.lineTo(midWidth - width / 8, midHeight - height / 8);
				path.lineTo(midWidth, zeroHeight);
				path.close();

				if (fillType == Style.STROKE) {
					drawPaint.setStrokeWidth(stroke / 2);
					drawPaint.setStrokeJoin(Paint.Join.ROUND);
					path.offset(SHAPE_OFFSET + stroke / 2, SHAPE_OFFSET + stroke / 2);
				} else {
					path.offset(SHAPE_OFFSET, SHAPE_OFFSET);
				}

				break;

			case HEART:
				path.moveTo(midWidth, height);
				path.cubicTo(-0.2f * width, 4.5f * height / 8,
						0.8f * width / 8, -1.5f * height / 8,
						midWidth, 1.5f * height / 8);
				path.cubicTo(7.2f * width / 8, -1.5f * height / 8,
						1.2f * width, 4.5f * height / 8,
						midWidth, height);
				path.close();

				if (fillType == Style.STROKE) {
					drawPaint.setStrokeWidth(stroke / 2);
					drawPaint.setStrokeJoin(Paint.Join.ROUND);
					path.offset(SHAPE_OFFSET + stroke / 2, SHAPE_OFFSET + stroke / 2);
				} else {
					path.offset(SHAPE_OFFSET, SHAPE_OFFSET);
				}

				break;

			default:
				break;
		}

		return path;
	}

	@Override
	protected void onClickInBox() {
		Point intPosition = new Point((int) toolPosition.x, (int) toolPosition.y);
		int bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		int bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();

		if (!(toolPosition.x - boxWidth / 2 > bitmapWidth || toolPosition.y - boxHeight / 2 > bitmapHeight
				|| toolPosition.x + boxWidth / 2 < 0 || toolPosition.y + boxHeight / 2 < 0)) {

			Command command = commandFactory.createGeometricFillCommand(drawingBitmap, intPosition,
					boxWidth, boxHeight, boxRotation, geometricFillCommandPaint);
			PaintroidApplication.commandManager.addCommand(command);
			highlightBox();
		}
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void setupToolOptions() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View shapeToolOptionView = inflater.inflate(R.layout.dialog_pocketpaint_shapes, toolSpecificOptionsLayout);

		shapeToolOptionsListener = new ShapeToolOptionsListener(shapeToolOptionView);
		setupOnShapeToolDialogChangedListener();
		toolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				toggleShowToolOptions();
			}
		});
	}

	public enum ShapeDrawType {
		OUTLINE, FILL
	}

	public enum BaseShape {
		RECTANGLE, OVAL, HEART, STAR
	}
}
