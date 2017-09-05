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
import android.view.LayoutInflater;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.GeometricFillCommand;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.listener.ShapeToolOptionsListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class GeometricFillTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BOUNDS = false;
	private static final float SHAPE_OFFSET = 10f;

	private BaseShape mBaseShape;
	private ShapeDrawType mShapeDrawType;
	private ShapeToolOptionsListener.OnShapeToolOptionsChangedListener mOnShapeToolOptionsChangedListener;
	private View mShapeToolOptionView;
	private Paint mGeometricFillCommandPaint;

	public static enum ShapeDrawType {
		OUTLINE, FILL
	}

	public static enum BaseShape {
		RECTANGLE, OVAL, HEART, STAR
	}

	public BaseShape getBaseShape() {
		return mBaseShape;
	}

	public GeometricFillTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BOUNDS);

		if(mBaseShape == null)
			mBaseShape = BaseShape.RECTANGLE;

		mShapeDrawType = ShapeDrawType.FILL;

		mColor = new OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				changePaintColor(color);
				createAndSetBitmap();
			}
		};

		createAndSetBitmap();
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

	protected void setupOnShapeToolDialogChangedListener() {
		mOnShapeToolOptionsChangedListener = new ShapeToolOptionsListener.OnShapeToolOptionsChangedListener() {
			@Override
			public void setToolType(BaseShape shape) {
				mBaseShape = shape;
				createAndSetBitmap();
			}
		};
		ShapeToolOptionsListener.getInstance().setOnShapeToolOptionsChangedListener(mOnShapeToolOptionsChangedListener);
	}

	protected void createAndSetBitmap() {
		Bitmap bitmap = Bitmap.createBitmap((int) mBoxWidth, (int) mBoxHeight,
				Bitmap.Config.ARGB_8888);
		Canvas drawCanvas = new Canvas(bitmap);

		RectF shapeRect = new RectF(SHAPE_OFFSET, SHAPE_OFFSET, mBoxWidth
				- SHAPE_OFFSET, mBoxHeight - SHAPE_OFFSET);

		Paint drawPaint = new Paint();
		drawPaint.setColor(mCanvasPaint.getColor());
		drawPaint.setAntiAlias(DEFAULT_ANTIALISING_ON);

		switch (mShapeDrawType) {
			case FILL:
				drawPaint.setStyle(Style.FILL);
				break;
			case OUTLINE:
				drawPaint.setStyle(Style.STROKE);
				float strokeWidth = mBitmapPaint.getStrokeWidth();
				shapeRect = new RectF(SHAPE_OFFSET + (strokeWidth / 2),
						SHAPE_OFFSET + (strokeWidth / 2), mBoxWidth - SHAPE_OFFSET
						- (strokeWidth / 2), mBoxHeight - SHAPE_OFFSET - (strokeWidth / 2));
				drawPaint.setStrokeWidth(strokeWidth);
				drawPaint.setStrokeCap(Paint.Cap.BUTT);
				break;
			default:
				break;
		}

		mGeometricFillCommandPaint = new Paint(Paint.DITHER_FLAG);
		if (Color.alpha(mCanvasPaint.getColor()) == 0x00) {
			int colorWithMaxAlpha = Color.BLACK;
			mGeometricFillCommandPaint.setColor(colorWithMaxAlpha);
			mGeometricFillCommandPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
			mGeometricFillCommandPaint.setAntiAlias(DEFAULT_ANTIALISING_ON);

			drawPaint.reset();
			drawPaint.setAntiAlias(DEFAULT_ANTIALISING_ON);
			drawPaint.setShader(CHECKERED_PATTERN.getShader());
		}

		switch (mBaseShape) {
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

		createOverlayButton();
		setBitmap(bitmap);
	}

	private void drawShape(Canvas drawCanvas, RectF shapeRect, Paint drawPaint, int drawableId) {
		Rect rect = new Rect((int)shapeRect.left, (int)shapeRect.top, (int)shapeRect.right, (int)shapeRect.bottom);

		Bitmap bmp = BitmapFactory.decodeResource(PaintroidApplication.applicationContext.getResources(), drawableId);
		Bitmap scaled_bmp = Bitmap.createScaledBitmap(bmp, rect.width(), rect.height(), true);
		Paint colorChangePaint = new Paint(drawPaint);

		if (Color.alpha(mCanvasPaint.getColor()) == 0x00) {
			int colorWithMaxAlpha = Color.BLACK;
			colorChangePaint.setColor(colorWithMaxAlpha);
		}

		Bitmap checkeredBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
		Canvas checkeredCanvas = new Canvas(checkeredBitmap);
		checkeredCanvas.drawPaint(drawPaint);
		drawCanvas.drawBitmap(checkeredBitmap, shapeRect.left, shapeRect.top, drawPaint);

		colorChangePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
		drawCanvas.drawBitmap(scaled_bmp, shapeRect.left, shapeRect.top, colorChangePaint);
	}

	@Override
	protected void onClickInBox() {
		Point intPosition = new Point((int) mToolPosition.x, (int) mToolPosition.y);
		int bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		int bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();

		if (!((mToolPosition.x - mBoxWidth / 2 > bitmapWidth) || (mToolPosition.y - mBoxHeight / 2 > bitmapHeight)
				|| (mToolPosition.x + mBoxWidth / 2 < 0) || (mToolPosition.y + mBoxHeight / 2 < 0))) {

			Command command = new GeometricFillCommand(mDrawingBitmap, intPosition,
					mBoxWidth, mBoxHeight, mBoxRotation, mGeometricFillCommandPaint);
			((GeometricFillCommand) command).addObserver(this);

			IndeterminateProgressDialog.getInstance().show();
			Layer layer = LayerListener.getInstance().getCurrentLayer();
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
			highlightBox();

		}
	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
		// TODO Auto-generated method stub
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void setupToolOptions() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mShapeToolOptionView = inflater.inflate(R.layout.dialog_shapes, null);

		mToolSpecificOptionsLayout.addView(mShapeToolOptionView);
		ShapeToolOptionsListener.init(mContext, mShapeToolOptionView);
		setupOnShapeToolDialogChangedListener();
		mToolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				toggleShowToolOptions();
			}
		});
	}

}
