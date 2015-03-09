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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;

public class GeometricFillTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BOUNDS = false;
	private static final float SHAPE_OFFSET = 10f;

	private BaseShape mBaseShape;
	private ShapeDrawType mShapeDrawType;

	public static enum ShapeDrawType {
		OUTLINE, FILL
	};

	public static enum BaseShape {
		RECTANGLE, OVAL
	};

	public GeometricFillTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BOUNDS);

		if (toolType == ToolType.ELLIPSE) {
			mBaseShape = BaseShape.OVAL;
		} else {
			mBaseShape = BaseShape.RECTANGLE;
		}

		mShapeDrawType = ShapeDrawType.FILL;

		mColor = new OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				changePaintColor(color);
				createAndSetBitmap(PaintroidApplication.drawingSurface);
			}
		};

		createAndSetBitmap(PaintroidApplication.drawingSurface);
	}

	@Override
	public void setDrawPaint(Paint paint) {
		// necessary because of timing in MainActivity and Eraser
		super.setDrawPaint(paint);
		createAndSetBitmap(PaintroidApplication.drawingSurface);
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		createAndSetBitmap(PaintroidApplication.drawingSurface);
	}

	protected void createAndSetBitmap(DrawingSurface drawingSurface) {
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
							- (strokeWidth / 2), mBoxHeight - SHAPE_OFFSET
							- (strokeWidth / 2));
			drawPaint.setStrokeWidth(strokeWidth);
			drawPaint.setStrokeCap(Paint.Cap.BUTT);
			break;
		default:
			break;
		}

		switch (mBaseShape) {
		case RECTANGLE:
			drawCanvas.drawRect(shapeRect, drawPaint);
			break;
		case OVAL:
			drawCanvas.drawOval(shapeRect, drawPaint);
			break;
		default:
			break;
		}

		mDrawingBitmap = bitmap;
	}

	@Override
	protected void onClickInBox() {
		Point intPosition = new Point((int) mToolPosition.x,
				(int) mToolPosition.y);
		Command command = new StampCommand(mDrawingBitmap, intPosition,
				mBoxWidth, mBoxHeight, mBoxRotation);
		((StampCommand) command).addObserver(this);
		IndeterminateProgressDialog.getInstance().show();
		PaintroidApplication.commandManager.commitCommand(command);
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			showColorPicker();
			break;
		default:
			break;
		}
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return getStrokeColorResource();
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_color_palette;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
		// TODO Auto-generated method stub
	}

	@Override
	public void resetInternalState() {
	}
}
