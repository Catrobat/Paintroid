/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.GeometricFillCommand;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.listener.ShapeToolOptionsListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class GeometricFillTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BOUNDS = false;
	private static final float SHAPE_OFFSET = 10f;

	private static final String BUNDLE_BASE_SHAPE = "BASE_SHAPE";
	private static final String BUNDLE_SHAPE_DRAW_TYPE = "SHAPE_DRAW_TYPE";

	@VisibleForTesting
	public BaseShape baseShape;
	private ShapeDrawType shapeDrawType;
	private ShapeToolOptionsListener shapeToolOptionsListener;
	private Paint geometricFillCommandPaint;

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
				});
	}

	private void createAndSetBitmap() {
		Bitmap bitmap = Bitmap.createBitmap((int) boxWidth, (int) boxHeight,
				Bitmap.Config.ARGB_8888);
		Canvas drawCanvas = new Canvas(bitmap);

		RectF shapeRect = new RectF(SHAPE_OFFSET, SHAPE_OFFSET, boxWidth
				- SHAPE_OFFSET, boxHeight - SHAPE_OFFSET);

		Paint drawPaint = new Paint();
		drawPaint.setColor(CANVAS_PAINT.getColor());
		drawPaint.setAntiAlias(DEFAULT_ANTIALIASING_ON);

		switch (shapeDrawType) {
			case FILL:
				drawPaint.setStyle(Style.FILL);
				break;
			case OUTLINE:
				drawPaint.setStyle(Style.STROKE);
				float strokeWidth = BITMAP_PAINT.getStrokeWidth();
				shapeRect = new RectF(SHAPE_OFFSET + strokeWidth / 2,
						SHAPE_OFFSET + strokeWidth / 2, boxWidth - SHAPE_OFFSET
						- strokeWidth / 2, boxHeight - SHAPE_OFFSET - strokeWidth / 2);
				drawPaint.setStrokeWidth(strokeWidth);
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

		switch (baseShape) {
			case RECTANGLE:
				drawCanvas.drawRect(shapeRect, drawPaint);
				break;
			case OVAL:
				drawCanvas.drawOval(shapeRect, drawPaint);
				break;
			case STAR:
				drawShape(drawCanvas, shapeRect, drawPaint, R.drawable.ic_star_black_48dp);
				break;
			case HEART:
				drawShape(drawCanvas, shapeRect, drawPaint, R.drawable.ic_heart_black_48dp);
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
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);

		BaseShape baseShape = (BaseShape) bundle.getSerializable(BUNDLE_BASE_SHAPE);
		ShapeDrawType shapeDrawType = (ShapeDrawType) bundle.getSerializable(BUNDLE_SHAPE_DRAW_TYPE);

		if (baseShape != null && shapeDrawType != null
				&& (this.baseShape != baseShape || this.shapeDrawType != shapeDrawType)) {
			this.baseShape = baseShape;
			this.shapeDrawType = shapeDrawType;

			shapeToolOptionsListener.setShapeActivated(baseShape);
			createAndSetBitmap();
		}
	}

	private void drawShape(Canvas drawCanvas, RectF shapeRect, Paint drawPaint, int drawableId) {
		Rect rect = new Rect((int) shapeRect.left, (int) shapeRect.top, (int) shapeRect.right, (int) shapeRect.bottom);

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, rect.width(), rect.height(), true);
		Paint colorChangePaint = new Paint(drawPaint);

		if (Color.alpha(CANVAS_PAINT.getColor()) == 0x00) {
			int colorWithMaxAlpha = Color.BLACK;
			colorChangePaint.setColor(colorWithMaxAlpha);
		}

		Bitmap checkeredBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
		Canvas checkeredCanvas = new Canvas(checkeredBitmap);
		checkeredCanvas.drawPaint(drawPaint);
		drawCanvas.drawBitmap(checkeredBitmap, shapeRect.left, shapeRect.top, drawPaint);

		colorChangePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
		drawCanvas.drawBitmap(scaledBitmap, shapeRect.left, shapeRect.top, colorChangePaint);
	}

	@Override
	protected void onClickInBox() {
		Point intPosition = new Point((int) toolPosition.x, (int) toolPosition.y);
		int bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		int bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();

		if (!(toolPosition.x - boxWidth / 2 > bitmapWidth || toolPosition.y - boxHeight / 2 > bitmapHeight
				|| toolPosition.x + boxWidth / 2 < 0 || toolPosition.y + boxHeight / 2 < 0)) {

			Command command = new GeometricFillCommand(drawingBitmap, intPosition,
					boxWidth, boxHeight, boxRotation, geometricFillCommandPaint);
			((GeometricFillCommand) command).addObserver(this);

			IndeterminateProgressDialog.getInstance().show();
			Layer layer = LayerListener.getInstance().getCurrentLayer();
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
			highlightBox();
		}
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void setupToolOptions() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View shapeToolOptionView = inflater.inflate(R.layout.dialog_shapes, toolSpecificOptionsLayout);

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
