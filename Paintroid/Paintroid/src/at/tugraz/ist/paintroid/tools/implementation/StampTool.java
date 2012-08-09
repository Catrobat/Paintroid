/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.tools.implementation;

import java.util.Observable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand.NOTIFY_STATES;
import at.tugraz.ist.paintroid.command.implementation.StampCommand;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class StampTool extends BaseToolWithShape {

	public static final PorterDuffXfermode TRANSPARENCY_XFER_MODE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	private static final int DEFAULT_RECTANGLE_MARGIN = 100;
	private static final float DEFAULT_TOOL_STROKE_WIDTH = 5f;
	private static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	private static final float MAXIMAL_TOOL_STROKE_WIDTH = 10f;
	private static final int DEFAULT_ROTATION_SYMBOL_DISTANCE = 20;
	private static final int DEFAULT_ROTATION_SYMBOL_WIDTH = 30;
	private static final int DEFAULT_BOX_RESIZE_MARGIN = 20;

	private static final float PRIMARY_SHAPE_EFFECT_INTERVAL_OFF = 20;
	private static final float PRIMARY_SHAPE_EFFECT_INTERVAL_ON = 10;
	private static final float PRIMARY_SHAPE_EFFECT_PHASE = 20;

	private static final float SECONDARY_SHAPE_EFFECT_INTERVAL_OFF = 10;
	private static final float SECONDARY_SHAPE_EFFECT_INTERVAL_ON = 20;
	private static final float SECONDARY_SHAPE_EFFECT_PHASE = 0;

	private static final Cap DEFAULT_STROKE_CAP = Cap.SQUARE;
	private static final boolean DEFAULT_ANTIALISING_ON = true;

	private float mBoxWidth;
	private float mBoxHeight;
	private float mBoxRotation; // in degree
	private float mBoxResizeMargin;
	private float mRotationSymbolDistance;
	private float mRotationSymbolWidth;
	private float mToolStrokeWidth;
	private Bitmap mStampBitmap;
	private DrawingSurface mDrawingSurface;
	private ResizeAction mResizeAction;
	private FloatingBoxAction mCurrentAction;
	private RotatePosition mRotatePosition;

	private enum FloatingBoxAction {
		NONE, MOVE, RESIZE, ROTATE;
	}

	private enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}

	private enum RotatePosition {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
	}

	public StampTool(Context context, ToolType toolType, DrawingSurface drawingSurface) {
		super(context, toolType);

		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mBoxWidth = display.getWidth() / PaintroidApplication.CURRENT_PERSPECTIVE.getScale()
				- getInverselyProportionalSizeForZoom(DEFAULT_RECTANGLE_MARGIN) * 2;
		mBoxHeight = mBoxWidth;

		mRotatePosition = RotatePosition.TOP_LEFT;
		mLinePaint = new Paint();
		mLinePaint.setDither(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeJoin(Paint.Join.ROUND);
		mResizeAction = ResizeAction.NONE;

		mDrawingSurface = drawingSurface;

		if (mStampBitmap != null) {
			mStampBitmap.recycle();
			mStampBitmap = null;
		}

		initScaleDependedValues();
	}

	private void initScaleDependedValues() {
		mToolStrokeWidth = getStrokeWidthForZoom(DEFAULT_TOOL_STROKE_WIDTH, MINIMAL_TOOL_STROKE_WIDTH,
				MAXIMAL_TOOL_STROKE_WIDTH);
		mBoxResizeMargin = getInverselyProportionalSizeForZoom(DEFAULT_BOX_RESIZE_MARGIN);
		mRotationSymbolDistance = getInverselyProportionalSizeForZoom(DEFAULT_ROTATION_SYMBOL_DISTANCE);
		mRotationSymbolWidth = getInverselyProportionalSizeForZoom(DEFAULT_ROTATION_SYMBOL_WIDTH);
	}

	public void addBitmap(Bitmap bitmapToAdd) {
		if (bitmapToAdd != null) {
			mStampBitmap = bitmapToAdd;
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		mMovedDistance.set(0, 0);
		mPreviousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		mCurrentAction = getAction(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (mPreviousEventCoordinate == null || mCurrentAction == null) {
			return false;
		}
		PointF delta = new PointF(coordinate.x - mPreviousEventCoordinate.x, coordinate.y - mPreviousEventCoordinate.y);
		mMovedDistance.set(mMovedDistance.x + Math.abs(delta.x), mMovedDistance.y + Math.abs(delta.y));
		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);
		switch (mCurrentAction) {
			case MOVE:
				mToolPosition.x += delta.x;
				mToolPosition.y += delta.y;
				break;
			case RESIZE:
				resize(delta.x, delta.y);
				break;
			case ROTATE:
				rotate(delta.x, delta.y);
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (mPreviousEventCoordinate == null) {
			return false;
		}
		mMovedDistance.set(mMovedDistance.x + Math.abs(coordinate.x - mPreviousEventCoordinate.x), mMovedDistance.y
				+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		if (PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.x
				&& PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.y) {
			if (mStampBitmap == null) {
				clipBitmap(mDrawingSurface);
			} else {
				Point intPosition = new Point((int) mToolPosition.x, (int) mToolPosition.y);
				Command command = new StampCommand(mStampBitmap, intPosition, mBoxWidth, mBoxHeight, mBoxRotation);
				((StampCommand) command).addObserver(this);
				mProgressDialog.show();
				PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
			}
		}
		return true;
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof NOTIFY_STATES) {
			if (data == NOTIFY_STATES.COMMAND_DONE || data == NOTIFY_STATES.COMMAND_FAILED) {
				mProgressDialog.dismiss();
				observable.deleteObserver(this);
			}
		}
	}

	@Override
	public void resetInternalState() {
		// empty stub
	}

	@Override
	public void drawShape(Canvas canvas) {
		initScaleDependedValues();
		canvas.translate(mToolPosition.x, mToolPosition.y);
		canvas.rotate(mBoxRotation);

		// draw bitmap
		if (mStampBitmap != null) {
			Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
			canvas.drawBitmap(mStampBitmap, null, new RectF(-mBoxWidth / 2, -mBoxHeight / 2, mBoxWidth / 2,
					mBoxHeight / 2), bitmapPaint);
		}

		// draw primary color
		PathEffect primaryPathEffect = new DashPathEffect(new float[] {
				getInverselyProportionalSizeForZoom(PRIMARY_SHAPE_EFFECT_INTERVAL_OFF),
				getInverselyProportionalSizeForZoom(PRIMARY_SHAPE_EFFECT_INTERVAL_ON) },
				getInverselyProportionalSizeForZoom(PRIMARY_SHAPE_EFFECT_PHASE));
		prepareLinePaint(primaryShapeColor, primaryPathEffect);
		canvas.drawRect(-mBoxWidth / 2, mBoxHeight / 2, mBoxWidth / 2, -mBoxHeight / 2, mLinePaint);
		if (mStampBitmap != null) {
			canvas.drawCircle(-mBoxWidth / 2 - mRotationSymbolDistance - mRotationSymbolWidth / 2, -mBoxHeight / 2
					- mRotationSymbolDistance - mRotationSymbolWidth / 2, mRotationSymbolWidth, mLinePaint);
		}

		// draw secondary color
		PathEffect secondaryPathEffect = new DashPathEffect(new float[] {
				getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_INTERVAL_OFF),
				getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_INTERVAL_ON) },
				getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_PHASE));
		prepareLinePaint(secondaryShapeColor, secondaryPathEffect);
		canvas.drawRect(-mBoxWidth / 2, mBoxHeight / 2, mBoxWidth / 2, -mBoxHeight / 2, mLinePaint);
		if (mStampBitmap != null) {
			canvas.drawCircle(-mBoxWidth / 2 - mRotationSymbolDistance - mRotationSymbolWidth / 2, -mBoxHeight / 2
					- mRotationSymbolDistance - mRotationSymbolWidth / 2, mRotationSymbolWidth, mLinePaint);
		}
		canvas.restore();

	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		drawShape(canvas);
	}

	protected void rotate(float delta_x, float delta_y) {
		if (mStampBitmap == null) {
			return;
		}
		double rotationRadiant = mBoxRotation * Math.PI / 180;
		double deltaXCcorrected = Math.cos(-rotationRadiant) * (delta_x) - Math.sin(-rotationRadiant) * (delta_y);
		double deltaYCorrected = Math.sin(-rotationRadiant) * (delta_x) + Math.cos(-rotationRadiant) * (delta_y);

		float scale = PaintroidApplication.CURRENT_PERSPECTIVE.getScale();
		deltaXCcorrected *= scale;
		deltaYCorrected *= scale;

		switch (mRotatePosition) {
			case TOP_LEFT:
				mBoxRotation += (deltaXCcorrected - deltaYCorrected) / 5;
				break;
			case TOP_RIGHT:
				mBoxRotation -= (-deltaXCcorrected - deltaYCorrected) / 5;
				break;
			case BOTTOM_LEFT:
				mBoxRotation += (-deltaXCcorrected - deltaYCorrected) / 5;
				break;
			case BOTTOM_RIGHT:
				mBoxRotation -= (deltaXCcorrected - deltaYCorrected) / 5;
				break;
		}

	}

	public boolean rotate(int degree) {
		if (mStampBitmap == null) {
			return false;
		}
		mBoxRotation += degree;
		return true;
	}

	protected void resize(float delta_x, float delta_y) {
		double rotationRadian = mBoxRotation * Math.PI / 180;
		double deltaXCorrected = Math.cos(-rotationRadian) * (delta_x) - Math.sin(-rotationRadian) * (delta_y);
		double deltaYCorrected = Math.sin(-rotationRadian) * (delta_x) + Math.cos(-rotationRadian) * (delta_y);

		float resizeXMoveCenterX = (float) ((deltaXCorrected / 2) * Math.cos(rotationRadian));
		float resizeXMoveCenterY = (float) ((deltaXCorrected / 2) * Math.sin(rotationRadian));
		float resizeYMoveCenterX = (float) ((deltaYCorrected / 2) * Math.sin(rotationRadian));
		float resizeYMoveCenterY = (float) ((deltaYCorrected / 2) * Math.cos(rotationRadian));

		// Height
		switch (mResizeAction) {
			case TOP:
			case TOPRIGHT:
			case TOPLEFT:
				mBoxHeight -= deltaYCorrected;
				mToolPosition.x -= resizeYMoveCenterX;
				mToolPosition.y += resizeYMoveCenterY;
				break;
			case BOTTOM:
			case BOTTOMLEFT:
			case BOTTOMRIGHT:
				mBoxHeight += deltaYCorrected;
				mToolPosition.x -= resizeYMoveCenterX;
				mToolPosition.y += resizeYMoveCenterY;
				break;
			default:
				break;
		}

		// Width
		switch (mResizeAction) {
			case LEFT:
			case TOPLEFT:
			case BOTTOMLEFT:
				mBoxWidth -= deltaXCorrected;
				mToolPosition.x += resizeXMoveCenterX;
				mToolPosition.y += resizeXMoveCenterY;
				break;
			case RIGHT:
			case TOPRIGHT:
			case BOTTOMRIGHT:
				mBoxWidth += deltaXCorrected;
				mToolPosition.x += resizeXMoveCenterX;
				mToolPosition.y += resizeXMoveCenterY;
				break;
			default:
				break;
		}

		// prevent that box gets too small
		if (mBoxWidth < DEFAULT_BOX_RESIZE_MARGIN) {
			mBoxWidth = DEFAULT_BOX_RESIZE_MARGIN;
		}
		if (mBoxHeight < DEFAULT_BOX_RESIZE_MARGIN) {
			mBoxHeight = DEFAULT_BOX_RESIZE_MARGIN;
		}
	}

	protected FloatingBoxAction getAction(float clickCoordinatesX, float clickCoordinatesY) {
		mResizeAction = ResizeAction.NONE;
		double rotationRadiant = mBoxRotation * Math.PI / 180;
		float clickCoordinatesRotatedX = (float) (mToolPosition.x + Math.cos(-rotationRadiant)
				* (clickCoordinatesX - mToolPosition.x) - Math.sin(-rotationRadiant)
				* (clickCoordinatesY - mToolPosition.y));
		float clickCoordinatesRotatedY = (float) (mToolPosition.y + Math.sin(-rotationRadiant)
				* (clickCoordinatesX - mToolPosition.x) + Math.cos(-rotationRadiant)
				* (clickCoordinatesY - mToolPosition.y));

		// Move (within box)
		if (clickCoordinatesRotatedX < mToolPosition.x + mBoxWidth / 2 - mBoxResizeMargin
				&& clickCoordinatesRotatedX > mToolPosition.x - mBoxWidth / 2 + mBoxResizeMargin
				&& clickCoordinatesRotatedY < mToolPosition.y + mBoxHeight / 2 - mBoxResizeMargin
				&& clickCoordinatesRotatedY > mToolPosition.y - mBoxHeight / 2 + mBoxResizeMargin) {
			return FloatingBoxAction.MOVE;
		}

		// Only allow rotation if an image is present
		if (mStampBitmap != null) {

			// rotate everywhere outside the box with the distance of the rotation symbol
			if ((clickCoordinatesRotatedX < mToolPosition.x - mBoxWidth / 2 - mRotationSymbolDistance)
					|| (clickCoordinatesRotatedX > mToolPosition.x + mBoxWidth / 2 + mRotationSymbolDistance)
					|| (clickCoordinatesRotatedY < mToolPosition.y - mBoxHeight / 2 - mRotationSymbolDistance)
					|| (clickCoordinatesRotatedY > mToolPosition.y + mBoxHeight / 2 + mRotationSymbolDistance)) {

				if ((clickCoordinatesRotatedX <= mToolPosition.x) && (clickCoordinatesRotatedY <= mToolPosition.y)) {
					mRotatePosition = RotatePosition.TOP_LEFT;
				} else if ((clickCoordinatesRotatedX > mToolPosition.x)
						&& (clickCoordinatesRotatedY <= mToolPosition.y)) {
					mRotatePosition = RotatePosition.TOP_RIGHT;
				} else if ((clickCoordinatesRotatedX <= mToolPosition.x)
						&& (clickCoordinatesRotatedY > mToolPosition.y)) {
					mRotatePosition = RotatePosition.BOTTOM_LEFT;
				} else if ((clickCoordinatesRotatedX > mToolPosition.x) && (clickCoordinatesRotatedY > mToolPosition.y)) {
					mRotatePosition = RotatePosition.BOTTOM_RIGHT;
				}

				return FloatingBoxAction.ROTATE;
			}
		}

		// Resize (on frame)
		if (clickCoordinatesRotatedX < mToolPosition.x + mBoxWidth / 2 + mBoxResizeMargin
				&& clickCoordinatesRotatedX > mToolPosition.x - mBoxWidth / 2 - mBoxResizeMargin
				&& clickCoordinatesRotatedY < mToolPosition.y + mBoxHeight / 2 + mBoxResizeMargin
				&& clickCoordinatesRotatedY > mToolPosition.y - mBoxHeight / 2 - mBoxResizeMargin) {
			if (clickCoordinatesRotatedX < mToolPosition.x - mBoxWidth / 2 + mBoxResizeMargin) {
				mResizeAction = ResizeAction.LEFT;
			} else if (clickCoordinatesRotatedX > mToolPosition.x + mBoxWidth / 2 - mBoxResizeMargin) {
				mResizeAction = ResizeAction.RIGHT;
			}
			if (clickCoordinatesRotatedY < mToolPosition.y - mBoxHeight / 2 + mBoxResizeMargin) {
				if (mResizeAction == ResizeAction.LEFT) {
					mResizeAction = ResizeAction.TOPLEFT;
				} else if (mResizeAction == ResizeAction.RIGHT) {
					mResizeAction = ResizeAction.TOPRIGHT;
				} else {
					mResizeAction = ResizeAction.TOP;
				}
			} else if (clickCoordinatesRotatedY > mToolPosition.y + mBoxHeight / 2 - mBoxResizeMargin) {
				if (mResizeAction == ResizeAction.LEFT) {
					mResizeAction = ResizeAction.BOTTOMLEFT;
				} else if (mResizeAction == ResizeAction.RIGHT) {
					mResizeAction = ResizeAction.BOTTOMRIGHT;
				} else {
					mResizeAction = ResizeAction.BOTTOM;
				}
			}
			return FloatingBoxAction.RESIZE;
		}

		// No valid click
		return FloatingBoxAction.NONE;

	}

	protected void clipBitmap(DrawingSurface drawingSurface) {
		Log.d(PaintroidApplication.TAG, "clip bitmap");
		Point left_top_box_bitmapcoordinates = new Point((int) mToolPosition.x - (int) mBoxWidth / 2,
				(int) mToolPosition.y - (int) mBoxHeight / 2);
		Point right_bottom_box_bitmapcoordinates = new Point((int) mToolPosition.x + (int) mBoxWidth / 2,
				(int) mToolPosition.y + (int) mBoxHeight / 2);
		try {
			mStampBitmap = Bitmap.createBitmap(drawingSurface.getBitmap(), left_top_box_bitmapcoordinates.x,
					left_top_box_bitmapcoordinates.y, right_bottom_box_bitmapcoordinates.x
							- left_top_box_bitmapcoordinates.x, right_bottom_box_bitmapcoordinates.y
							- left_top_box_bitmapcoordinates.y);
			Log.d(PaintroidApplication.TAG, "created bitmap");
		} catch (IllegalArgumentException e) {
			// floatingBox is outside of image
			Log.e(PaintroidApplication.TAG, "error clip bitmap " + e.getMessage());
			Log.e(PaintroidApplication.TAG, "left top box coord : " + left_top_box_bitmapcoordinates.toString());
			Log.e(PaintroidApplication.TAG, "right bottom box coord : " + right_bottom_box_bitmapcoordinates.toString());
			Log.e(PaintroidApplication.TAG, "drawing surface bitmap size : " + drawingSurface.getBitmap().getHeight()
					+ " x " + drawingSurface.getBitmap().getWidth());

			if (mStampBitmap != null) {
				mStampBitmap.recycle();
				mStampBitmap = null;
			}
		}
	}

	private void prepareLinePaint(int currentStrokeColor, PathEffect effect) {
		if (mToolStrokeWidth <= 1) {
			mLinePaint.setAntiAlias(false);
		} else {
			mLinePaint.setAntiAlias(DEFAULT_ANTIALISING_ON);
		}
		mLinePaint.setStrokeCap(DEFAULT_STROKE_CAP);
		mLinePaint.setPathEffect(effect);
		mLinePaint.setStrokeWidth(mToolStrokeWidth);
		mLinePaint.setColor(currentStrokeColor);
		mLinePaint.setStyle(Paint.Style.STROKE);
		if (currentStrokeColor == Color.TRANSPARENT) {
			mLinePaint.setXfermode(TRANSPARENCY_XFER_MODE);
		} else {
			mLinePaint.setXfermode(null);
		}
	}

	@Override
	public int getAttributeButtonColor(int buttonNumber) {

		switch (buttonNumber) {
			case INDEX_BUTTON_MAIN:
				return super.getAttributeButtonColor(buttonNumber);
			case INDEX_BUTTON_ATTRIBUTE_1:
				return Color.TRANSPARENT;
			case INDEX_BUTTON_ATTRIBUTE_2:
				return Color.TRANSPARENT;
			default:
				return Color.TRANSPARENT;
		}
	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		switch (buttonNumber) {
			case INDEX_BUTTON_MAIN:
				return R.drawable.ic_menu_more_64;
			case INDEX_BUTTON_ATTRIBUTE_1:
				return 0;
			case INDEX_BUTTON_ATTRIBUTE_2:
				return 0;
			default:
				return 0;
		}
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {
		switch (buttonNumber) {
			case INDEX_BUTTON_ATTRIBUTE_1:
			case INDEX_BUTTON_ATTRIBUTE_2:
			default:
				break;
		}
	}

}
