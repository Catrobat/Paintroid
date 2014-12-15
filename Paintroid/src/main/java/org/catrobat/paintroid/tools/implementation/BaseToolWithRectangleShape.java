/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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
import org.catrobat.paintroid.tools.ToolType;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public abstract class BaseToolWithRectangleShape extends BaseToolWithShape {

	protected static final float MAXIMUM_BORDER_RATIO = 2f;

	protected static final int DEFAULT_RECTANGLE_MARGIN = 100;
	protected static final float DEFAULT_TOOL_STROKE_WIDTH = 3f;
	protected static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	protected static final float MAXIMAL_TOOL_STROKE_WIDTH = 8f;
	protected static final int DEFAULT_ROTATION_SYMBOL_DISTANCE = 20;
	protected static final int DEFAULT_ROTATION_SYMBOL_WIDTH = 30;
	protected static final int DEFAULT_BOX_RESIZE_MARGIN = 20;

	protected static final float PRIMARY_SHAPE_EFFECT_INTERVAL_OFF = 20;
	protected static final float PRIMARY_SHAPE_EFFECT_INTERVAL_ON = 10;
	protected static final float PRIMARY_SHAPE_EFFECT_PHASE = 20;

	protected static final float SECONDARY_SHAPE_EFFECT_INTERVAL_OFF = 10;
	protected static final float SECONDARY_SHAPE_EFFECT_INTERVAL_ON = 20;
	protected static final float SECONDARY_SHAPE_EFFECT_PHASE = 0;

	protected static final Cap DEFAULT_STROKE_CAP = Cap.SQUARE;
	protected static final boolean DEFAULT_ANTIALISING_ON = true;
	protected static final PorterDuffXfermode TRANSPARENCY_XFER_MODE = new PorterDuffXfermode(
			PorterDuff.Mode.CLEAR);

	private static final boolean DEFAULT_RESPECT_BORDERS = false;
	private static final boolean DEFAULT_ROTATION_ENABLED = false;
	private static final boolean DEFAULT_BACKGROUND_SHADOW_ENABLED = true;
	private static final boolean DEFAULT_RESIZE_POINTS_VISIBLE = true;
	private static final boolean DEFAULT_STATUS_ICON_ENABLED = false;

	private static final int RESIZE_CIRCLE_SIZE = getDensitySpecificValue(4);
	private static final int ROTATION_ARROW_ARC_STROKE_WIDTH = getDensitySpecificValue(2);
	private static final int ROTATION_ARROW_ARC_RADIUS = getDensitySpecificValue(8);
	private static final int ROTATION_ARROW_HEAD_SIZE = getDensitySpecificValue(3);
	private static final int ROTATION_ARROW_OFFSET = getDensitySpecificValue(3);

	protected float mBoxWidth;
	protected float mBoxHeight;
	protected float mBoxRotation; // in degree
	protected float mBoxResizeMargin;
	protected float mRotationSymbolDistance;
	protected float mRotationSymbolWidth;
	protected float mToolStrokeWidth;
	protected ResizeAction mResizeAction;
	protected FloatingBoxAction mCurrentAction;
	protected RotatePosition mRotatePosition;
	protected Bitmap mDrawingBitmap;

	private boolean mRespectImageBounds;
	private boolean mRotationEnabled;
	private boolean mBackgroundShadowEnabled;
	private boolean mResizePointsVisible;
	private boolean mStatusIconEnabled;

	private boolean mIsDown = false;

	private enum FloatingBoxAction {
		NONE, MOVE, RESIZE, ROTATE;
	}

	private enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}

	private enum RotatePosition {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
	}

	private static int getDensitySpecificValue(int value) {
		DisplayMetrics metrics = PaintroidApplication.applicationContext
				.getResources().getDisplayMetrics();
		int baseDensity = DisplayMetrics.DENSITY_MEDIUM;
		int density = metrics.densityDpi;
		if (density < DisplayMetrics.DENSITY_MEDIUM) {
			density = DisplayMetrics.DENSITY_MEDIUM;
		}
		return value * density / baseDensity;
	}

	public BaseToolWithRectangleShape(Context context, ToolType toolType) {
		super(context, toolType);
		mToolType = toolType;
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mBoxWidth = display.getWidth()
				/ PaintroidApplication.perspective.getScale()
				- getInverselyProportionalSizeForZoom(DEFAULT_RECTANGLE_MARGIN)
				* 2;
		mBoxHeight = mBoxWidth;

		if ((mBoxHeight > (PaintroidApplication.drawingSurface
				.getBitmapHeight() * MAXIMUM_BORDER_RATIO))
				|| mBoxWidth > (PaintroidApplication.drawingSurface
						.getBitmapWidth() * MAXIMUM_BORDER_RATIO)) {
			mBoxHeight = (PaintroidApplication.drawingSurface.getBitmapHeight() * MAXIMUM_BORDER_RATIO);
			mBoxWidth = (PaintroidApplication.drawingSurface.getBitmapWidth() * MAXIMUM_BORDER_RATIO);
		}

		mRotatePosition = RotatePosition.TOP_LEFT;
		mResizeAction = ResizeAction.NONE;

		mRespectImageBounds = DEFAULT_RESPECT_BORDERS;
		mRotationEnabled = DEFAULT_ROTATION_ENABLED;
		mBackgroundShadowEnabled = DEFAULT_BACKGROUND_SHADOW_ENABLED;
		mResizePointsVisible = DEFAULT_RESIZE_POINTS_VISIBLE;
		mStatusIconEnabled = DEFAULT_STATUS_ICON_ENABLED;

		initLinePaint();
		initScaleDependedValues();
	}

	public BaseToolWithRectangleShape(Context context, ToolType toolType,
			Bitmap drawingBitmap) {
		this(context, toolType);
		mDrawingBitmap = drawingBitmap;

	}

	private void initLinePaint() {
		mLinePaint = new Paint();
		mLinePaint.setDither(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeJoin(Paint.Join.ROUND);
	}

	private void initScaleDependedValues() {
		mToolStrokeWidth = getStrokeWidthForZoom(DEFAULT_TOOL_STROKE_WIDTH,
				MINIMAL_TOOL_STROKE_WIDTH, MAXIMAL_TOOL_STROKE_WIDTH);
		mBoxResizeMargin = getInverselyProportionalSizeForZoom(DEFAULT_BOX_RESIZE_MARGIN);
		mRotationSymbolDistance = getInverselyProportionalSizeForZoom(DEFAULT_ROTATION_SYMBOL_DISTANCE);
		mRotationSymbolWidth = getInverselyProportionalSizeForZoom(DEFAULT_ROTATION_SYMBOL_WIDTH);
	}

	public void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			mDrawingBitmap = bitmap;

			float bitmapWidth = bitmap.getWidth();
			float bitmapHeight = bitmap.getHeight();
			float ratio = bitmapWidth / bitmapHeight;

			if (ratio > 1) {
				mBoxWidth *= ratio;
			} else {
				mBoxHeight /= ratio;
			}
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		mIsDown = true;
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

		PointF delta = new PointF(coordinate.x - mPreviousEventCoordinate.x,
				coordinate.y - mPreviousEventCoordinate.y);
		mMovedDistance.set(mMovedDistance.x + Math.abs(delta.x),
				mMovedDistance.y + Math.abs(delta.y));
		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);
		switch (mCurrentAction) {
		case MOVE:
			move(delta.x, delta.y);
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
		mIsDown = false;
		if (mPreviousEventCoordinate == null) {
			return false;
		}
		mMovedDistance.set(
				mMovedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				mMovedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		if (MOVE_TOLERANCE * 2 >= mMovedDistance.x
				&& MOVE_TOLERANCE * 2 >= mMovedDistance.y
				&& isCoordinateInsideBox(coordinate)) {
			onClickInBox();
		}
		return true;
	}

	private boolean isCoordinateInsideBox(PointF coordinate) {
		if ((coordinate.x > mToolPosition.x - mBoxWidth / 2)
				&& (coordinate.x < mToolPosition.x + mBoxWidth / 2)
				&& (coordinate.y > mToolPosition.y - mBoxHeight / 2)
				&& (coordinate.y < mToolPosition.y + mBoxHeight / 2)) {
			return true;
		}

		return false;
	}

	@Override
	public void draw(Canvas canvas) {
		drawShape(canvas);
	}

	@Override
	public void drawShape(Canvas canvas) {
		initScaleDependedValues();

		canvas.translate(mToolPosition.x, mToolPosition.y);
		canvas.rotate(mBoxRotation);

		if (mBackgroundShadowEnabled) {
			drawBackgroundShadow(canvas);
		}

		if (mResizePointsVisible) {
			drawResizePoints(canvas);
		}

		if (mDrawingBitmap != null && mRotationEnabled) {
			drawRotationArrows(canvas);
		}

		if (mDrawingBitmap != null) {
			drawBitmap(canvas);
		}

		drawRectangle(canvas);
		drawToolSpecifics(canvas);

		if (mStatusIconEnabled) {
			drawStatus(canvas);
		}
	}

	private void drawBackgroundShadow(Canvas canvas) {

		Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.argb(128, 0, 0, 0));
		backgroundPaint.setStyle(Style.FILL);

		canvas.clipRect((-mBoxWidth + mToolStrokeWidth) / 2,
				(mBoxHeight - mToolStrokeWidth) / 2,
				(mBoxWidth - mToolStrokeWidth) / 2,
				(-mBoxHeight + mToolStrokeWidth) / 2, Op.DIFFERENCE);
		canvas.rotate(-mBoxRotation);
		canvas.translate(-mToolPosition.x, -mToolPosition.y);
		canvas.drawRect(0, 0,
				PaintroidApplication.drawingSurface.getBitmapWidth(),
				PaintroidApplication.drawingSurface.getBitmapHeight(),
				backgroundPaint);
		canvas.translate(mToolPosition.x, mToolPosition.y);
		canvas.rotate(mBoxRotation);

	}

	private void drawResizePoints(Canvas canvas) {
		float circleRadius = getInverselyProportionalSizeForZoom(RESIZE_CIRCLE_SIZE);
		Paint circlePaint = new Paint();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(mSecondaryShapeColor);
		circlePaint.setStyle(Style.FILL);
		canvas.drawCircle(0, -mBoxHeight / 2, circleRadius, circlePaint);
		canvas.drawCircle(mBoxWidth / 2, -mBoxHeight / 2, circleRadius,
				circlePaint);
		canvas.drawCircle(mBoxWidth / 2, 0, circleRadius, circlePaint);
		canvas.drawCircle(mBoxWidth / 2, mBoxHeight / 2, circleRadius,
				circlePaint);
		canvas.drawCircle(0, mBoxHeight / 2, circleRadius, circlePaint);
		canvas.drawCircle(-mBoxWidth / 2, mBoxHeight / 2, circleRadius,
				circlePaint);
		canvas.drawCircle(-mBoxWidth / 2, 0, circleRadius, circlePaint);
		canvas.drawCircle(-mBoxWidth / 2, -mBoxHeight / 2, circleRadius,
				circlePaint);
	}

	private void drawRotationArrows(Canvas canvas) {
		float arcStrokeWidth = getInverselyProportionalSizeForZoom(ROTATION_ARROW_ARC_STROKE_WIDTH);
		float arcRadius = getInverselyProportionalSizeForZoom(ROTATION_ARROW_ARC_RADIUS);
		float arrowSize = getInverselyProportionalSizeForZoom(ROTATION_ARROW_HEAD_SIZE);
		float offset = getInverselyProportionalSizeForZoom(ROTATION_ARROW_OFFSET);

		Paint arcPaint = new Paint();
		arcPaint.setColor(Color.WHITE);
		arcPaint.setStrokeWidth(arcStrokeWidth);
		arcPaint.setStyle(Paint.Style.STROKE);
		arcPaint.setStrokeCap(Cap.BUTT);

		Paint arrowPaint = new Paint();
		arrowPaint.setColor(Color.WHITE);
		arrowPaint.setStyle(Paint.Style.FILL);

		float tempBoxWidth = mBoxWidth;
		float tempBoxHeight = mBoxHeight;

		for (int i = 0; i < 4; i++) {

			float xBase = -tempBoxWidth / 2 - offset;
			float yBase = -tempBoxHeight / 2 - offset;

			Path arcPath = new Path();

			RectF rectF = new RectF(xBase - arcRadius, yBase - arcRadius, xBase
					+ arcRadius, yBase + arcRadius);
			arcPath.addArc(rectF, 180, 90);

			canvas.drawPath(arcPath, arcPaint);

			Path arrowPath = new Path();
			arrowPath.moveTo(xBase - arcRadius - arrowSize, yBase);
			arrowPath.lineTo(xBase - arcRadius + arrowSize, yBase);
			arrowPath.lineTo(xBase - arcRadius, yBase + arrowSize);
			arrowPath.close();

			arrowPath.moveTo(xBase, yBase - arcRadius - arrowSize);
			arrowPath.lineTo(xBase, yBase - arcRadius + arrowSize);
			arrowPath.lineTo(xBase + arrowSize, yBase - arcRadius);
			arrowPath.close();
			canvas.drawPath(arrowPath, arrowPaint);

			float tempLenght = tempBoxWidth;
			tempBoxWidth = tempBoxHeight;
			tempBoxHeight = tempLenght;
			canvas.rotate(90);
		}
	}

	private void drawBitmap(Canvas canvas) {

		Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
		canvas.clipRect(new RectF(-mBoxWidth / 2, -mBoxHeight / 2,
				mBoxWidth / 2, mBoxHeight / 2), Op.UNION);
		canvas.drawBitmap(mDrawingBitmap, null, new RectF(-mBoxWidth / 2,
				-mBoxHeight / 2, mBoxWidth / 2, mBoxHeight / 2), bitmapPaint);

	}

	private void drawRectangle(Canvas canvas) {
		mLinePaint.setStrokeWidth(mToolStrokeWidth);
		mLinePaint.setColor(mSecondaryShapeColor);
		canvas.drawRect(new RectF(-mBoxWidth / 2, -mBoxHeight / 2,
				mBoxWidth / 2, mBoxHeight / 2), mLinePaint);
	}

	private void drawStatus(Canvas canvas) {
		RectF statusRect = new RectF(-48, -48, 48, 48);
		if (mIsDown) {

			int bitmapId;
			switch (mCurrentAction) {
			case MOVE:
				bitmapId = R.drawable.def_icon_move;
				break;
			case RESIZE:
				bitmapId = R.drawable.def_icon_resize;
				break;
			case ROTATE:
				bitmapId = R.drawable.def_icon_rotate;
				break;
			default:
				bitmapId = R.drawable.icon_menu_no_icon;
				break;
			}

			if (bitmapId != R.drawable.icon_menu_no_icon) {
				Paint statusPaint = new Paint();
				statusPaint.setColor(mSecondaryShapeColor);
				canvas.clipRect(statusRect, Op.UNION);
				statusPaint.setAlpha(128);
				canvas.drawOval(statusRect, statusPaint);

				Bitmap actionBitmap = BitmapFactory.decodeResource(
						PaintroidApplication.applicationContext.getResources(),
						bitmapId);
				statusPaint.setAlpha(255);
				canvas.rotate(-mBoxRotation);
				canvas.drawBitmap(actionBitmap, -24, -24, statusPaint);
				canvas.rotate(mBoxRotation);
			}
		}

	}

	private void move(float deltaX, float deltaY) {
		float newXPos = mToolPosition.x + deltaX;
		float newYPos = mToolPosition.y + deltaY;
		if (mRespectImageBounds) {
			if (newXPos - mBoxWidth / 2 < 0) {
				newXPos = mBoxWidth / 2;
			} else if (newXPos + mBoxWidth / 2 > PaintroidApplication.drawingSurface
					.getBitmapWidth()) {
				newXPos = PaintroidApplication.drawingSurface.getBitmapWidth()
						- mBoxWidth / 2;
			}

			if (newYPos - mBoxHeight / 2 < 0) {
				newYPos = mBoxHeight / 2;
			} else if (newYPos + mBoxHeight / 2 > PaintroidApplication.drawingSurface
					.getBitmapHeight()) {
				newYPos = PaintroidApplication.drawingSurface.getBitmapHeight()
						- mBoxHeight / 2;
			}
		}
		mToolPosition.x = newXPos;
		mToolPosition.y = newYPos;
	}

	private void rotate(float deltaX, float deltaY) {
		if (mDrawingBitmap == null) {
			return;
		}

		PointF currentPoint = new PointF(deltaX + mPreviousEventCoordinate.x,
				deltaY + mPreviousEventCoordinate.y);

		double previousXLength = mPreviousEventCoordinate.x - mToolPosition.x;
		double currentXLength = currentPoint.x - mToolPosition.x;
		double previousYLength = mPreviousEventCoordinate.y - mToolPosition.y;
		double currentYLength = currentPoint.y - mToolPosition.y;

		double deltaAngle = (Math.atan(previousXLength / previousYLength) - Math
				.atan(currentXLength / currentYLength));

		mBoxRotation += deltaAngle * 180 / Math.PI;
	}

	private FloatingBoxAction getAction(float clickCoordinatesX,
			float clickCoordinatesY) {
		mResizeAction = ResizeAction.NONE;
		double rotationRadiant = mBoxRotation * Math.PI / 180;
		float clickCoordinatesRotatedX = (float) (mToolPosition.x
				+ Math.cos(-rotationRadiant)
				* (clickCoordinatesX - mToolPosition.x) - Math
				.sin(-rotationRadiant) * (clickCoordinatesY - mToolPosition.y));
		float clickCoordinatesRotatedY = (float) (mToolPosition.y
				+ Math.sin(-rotationRadiant)
				* (clickCoordinatesX - mToolPosition.x) + Math
				.cos(-rotationRadiant) * (clickCoordinatesY - mToolPosition.y));

		// Move (within box)
		if (clickCoordinatesRotatedX < mToolPosition.x + mBoxWidth / 2
				- mBoxResizeMargin
				&& clickCoordinatesRotatedX > mToolPosition.x - mBoxWidth / 2
						+ mBoxResizeMargin
				&& clickCoordinatesRotatedY < mToolPosition.y + mBoxHeight / 2
						- mBoxResizeMargin
				&& clickCoordinatesRotatedY > mToolPosition.y - mBoxHeight / 2
						+ mBoxResizeMargin) {
			return FloatingBoxAction.MOVE;
		}

		// Only allow rotation if an image is present
		if ((mDrawingBitmap != null) && mRotationEnabled) {

			// rotate everywhere outside the box with the distance of the
			// rotation symbol
			if ((clickCoordinatesRotatedX < mToolPosition.x - mBoxWidth / 2
					- mRotationSymbolDistance)
					|| (clickCoordinatesRotatedX > mToolPosition.x + mBoxWidth
							/ 2 + mRotationSymbolDistance)
					|| (clickCoordinatesRotatedY < mToolPosition.y - mBoxHeight
							/ 2 - mRotationSymbolDistance)
					|| (clickCoordinatesRotatedY > mToolPosition.y + mBoxHeight
							/ 2 + mRotationSymbolDistance)) {

				if ((clickCoordinatesRotatedX <= mToolPosition.x)
						&& (clickCoordinatesRotatedY <= mToolPosition.y)) {
					mRotatePosition = RotatePosition.TOP_LEFT;
				} else if ((clickCoordinatesRotatedX > mToolPosition.x)
						&& (clickCoordinatesRotatedY <= mToolPosition.y)) {
					mRotatePosition = RotatePosition.TOP_RIGHT;
				} else if ((clickCoordinatesRotatedX <= mToolPosition.x)
						&& (clickCoordinatesRotatedY > mToolPosition.y)) {
					mRotatePosition = RotatePosition.BOTTOM_LEFT;
				} else if ((clickCoordinatesRotatedX > mToolPosition.x)
						&& (clickCoordinatesRotatedY > mToolPosition.y)) {
					mRotatePosition = RotatePosition.BOTTOM_RIGHT;
				}

				return FloatingBoxAction.ROTATE;
			}
		}

		// Resize (on frame)
		if (clickCoordinatesRotatedX < mToolPosition.x + mBoxWidth / 2
				+ mBoxResizeMargin
				&& clickCoordinatesRotatedX > mToolPosition.x - mBoxWidth / 2
						- mBoxResizeMargin
				&& clickCoordinatesRotatedY < mToolPosition.y + mBoxHeight / 2
						+ mBoxResizeMargin
				&& clickCoordinatesRotatedY > mToolPosition.y - mBoxHeight / 2
						- mBoxResizeMargin) {
			if (clickCoordinatesRotatedX < mToolPosition.x - mBoxWidth / 2
					+ mBoxResizeMargin) {
				mResizeAction = ResizeAction.LEFT;
			} else if (clickCoordinatesRotatedX > mToolPosition.x + mBoxWidth
					/ 2 - mBoxResizeMargin) {
				mResizeAction = ResizeAction.RIGHT;
			}
			if (clickCoordinatesRotatedY < mToolPosition.y - mBoxHeight / 2
					+ mBoxResizeMargin) {
				if (mResizeAction == ResizeAction.LEFT) {
					mResizeAction = ResizeAction.TOPLEFT;
				} else if (mResizeAction == ResizeAction.RIGHT) {
					mResizeAction = ResizeAction.TOPRIGHT;
				} else {
					mResizeAction = ResizeAction.TOP;
				}
			} else if (clickCoordinatesRotatedY > mToolPosition.y + mBoxHeight
					/ 2 - mBoxResizeMargin) {
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

	private void resize(float deltaX, float deltaY) {
		double rotationRadian = mBoxRotation * Math.PI / 180;
		double deltaXCorrected = Math.cos(-rotationRadian) * (deltaX)
				- Math.sin(-rotationRadian) * (deltaY);
		double deltaYCorrected = Math.sin(-rotationRadian) * (deltaX)
				+ Math.cos(-rotationRadian) * (deltaY);

        switch (mResizeAction) {
            case TOPLEFT:
            case BOTTOMRIGHT:
                if (Math.abs(deltaXCorrected) > Math.abs(deltaYCorrected)) {
                    deltaYCorrected = (((mBoxWidth + deltaXCorrected) * mBoxHeight) / mBoxWidth) - mBoxHeight;
                }
                else {
                    deltaXCorrected = ((mBoxWidth * (mBoxHeight + deltaYCorrected)) / mBoxHeight) - mBoxWidth;
                }
                break;
            case TOPRIGHT:
            case BOTTOMLEFT:
                if (Math.abs(deltaXCorrected) > Math.abs(deltaYCorrected)) {
                    deltaYCorrected = (((mBoxWidth - deltaXCorrected) * mBoxHeight) / mBoxWidth) - mBoxHeight;
                }
                else {
                    deltaXCorrected = ((mBoxWidth * (mBoxHeight - deltaYCorrected)) / mBoxHeight) - mBoxWidth;
                }
                break;
        }

		float resizeXMoveCenterX = (float) ((deltaXCorrected / 2) * Math
				.cos(rotationRadian));
		float resizeXMoveCenterY = (float) ((deltaXCorrected / 2) * Math
				.sin(rotationRadian));
		float resizeYMoveCenterX = (float) ((deltaYCorrected / 2) * Math
				.sin(rotationRadian));
		float resizeYMoveCenterY = (float) ((deltaYCorrected / 2) * Math
				.cos(rotationRadian));

		float newHeight;
		float newWidth;

		float newPosX = mToolPosition.x;
		float newPosY = mToolPosition.y;
		float oldPosX = mToolPosition.x;
		float oldPosY = mToolPosition.y;

		// Height
		switch (mResizeAction) {
		case TOP:
		case TOPRIGHT:
		case TOPLEFT:
			newHeight = (float) (mBoxHeight - deltaYCorrected);
			newPosX = mToolPosition.x - resizeYMoveCenterX;
			newPosY = mToolPosition.y + resizeYMoveCenterY;
			if (mRespectImageBounds && (newPosY - newHeight / 2 < 0)) {
				newPosX = mToolPosition.x;
				newPosY = mToolPosition.y;
				break;
			}

			if (newHeight > (PaintroidApplication.drawingSurface
					.getBitmapHeight() * MAXIMUM_BORDER_RATIO)) {
				mBoxHeight = (PaintroidApplication.drawingSurface
						.getBitmapHeight() * MAXIMUM_BORDER_RATIO);
				break;
			}

			mBoxHeight = newHeight;
			mToolPosition.x = newPosX;
			mToolPosition.y = newPosY;

			break;
		case BOTTOM:
		case BOTTOMLEFT:
		case BOTTOMRIGHT:
			newHeight = (float) (mBoxHeight + deltaYCorrected);
			newPosX = mToolPosition.x - resizeYMoveCenterX;
			newPosY = mToolPosition.y + resizeYMoveCenterY;
			if (mRespectImageBounds
					&& (newPosY + newHeight / 2 > PaintroidApplication.drawingSurface
							.getBitmapHeight())) {
				newPosX = mToolPosition.x;
				newPosY = mToolPosition.y;
				break;
			}

			if (newHeight > (PaintroidApplication.drawingSurface
					.getBitmapHeight() * MAXIMUM_BORDER_RATIO)) {
				mBoxHeight = (PaintroidApplication.drawingSurface
						.getBitmapHeight() * MAXIMUM_BORDER_RATIO);
				break;
			}

			mBoxHeight = newHeight;
			mToolPosition.x = newPosX;
			mToolPosition.y = newPosY;

			break;
		default:
			break;
		}

		// Width
		switch (mResizeAction) {
		case LEFT:
		case TOPLEFT:
		case BOTTOMLEFT:
			newWidth = (float) (mBoxWidth - deltaXCorrected);
			newPosX = mToolPosition.x + resizeXMoveCenterX;
			newPosY = mToolPosition.y + resizeXMoveCenterY;
			if (mRespectImageBounds && (newPosX - newWidth / 2 < 0)) {
				newPosX = mToolPosition.x;
				newPosY = mToolPosition.y;
				break;
			}

			if (newWidth > (PaintroidApplication.drawingSurface
					.getBitmapWidth() * MAXIMUM_BORDER_RATIO)) {
				mBoxWidth = (PaintroidApplication.drawingSurface
						.getBitmapWidth() * MAXIMUM_BORDER_RATIO);
				break;
			}

			mBoxWidth = newWidth;
			mToolPosition.x = newPosX;
			mToolPosition.y = newPosY;

			break;
		case RIGHT:
		case TOPRIGHT:
		case BOTTOMRIGHT:
			newWidth = (float) (mBoxWidth + deltaXCorrected);
			newPosX = mToolPosition.x + resizeXMoveCenterX;
			newPosY = mToolPosition.y + resizeXMoveCenterY;
			if (mRespectImageBounds
					&& (newPosX + newWidth / 2 > PaintroidApplication.drawingSurface
							.getBitmapWidth())) {
				newPosX = mToolPosition.x;
				newPosY = mToolPosition.y;
				break;
			}

			if (newWidth > (PaintroidApplication.drawingSurface
					.getBitmapWidth() * MAXIMUM_BORDER_RATIO)) {
				mBoxWidth = (PaintroidApplication.drawingSurface
						.getBitmapWidth() * MAXIMUM_BORDER_RATIO);
				break;
			}

			mBoxWidth = newWidth;
			mToolPosition.x = newPosX;
			mToolPosition.y = newPosY;

			break;
		default:
			break;
		}

		// prevent that box gets too small
		if (mBoxWidth < DEFAULT_BOX_RESIZE_MARGIN) {
			mBoxWidth = DEFAULT_BOX_RESIZE_MARGIN;
			mToolPosition.x = oldPosX;
		}
		if (mBoxHeight < DEFAULT_BOX_RESIZE_MARGIN) {
			mBoxHeight = DEFAULT_BOX_RESIZE_MARGIN;
			mToolPosition.y = oldPosY;
		}
	}

	protected void setRespectImageBounds(boolean respectImageBounds) {
		mRespectImageBounds = respectImageBounds;
	}

	protected boolean getRespectImageBounds() {
		return mRespectImageBounds;
	}

	protected void setRotationEnabled(boolean rotationEnabled) {
		mRotationEnabled = rotationEnabled;
	}

	protected boolean isRotationEnabled() {
		return mRotationEnabled;
	}

	protected void setBackgroundShadowEnabled(boolean backgroundShadowEnabled) {
		mBackgroundShadowEnabled = backgroundShadowEnabled;
	}

	protected boolean isBackgroundShadowEnabled() {
		return mBackgroundShadowEnabled;
	}

	protected void setResizePointsVisible(boolean resizePointsVisible) {
		mResizePointsVisible = resizePointsVisible;
	}

	protected boolean getResizePointsVisible() {
		return mResizePointsVisible;
	}

	protected abstract void onClickInBox();

	protected abstract void drawToolSpecifics(Canvas canvas);

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY,
			int viewWidth, int viewHeight) {

		if (mCurrentAction == FloatingBoxAction.MOVE
				|| mCurrentAction == FloatingBoxAction.RESIZE) {

			return super.getAutoScrollDirection(pointX, pointY, viewWidth,
					viewHeight);
		}
		return new Point(0, 0);
	}
}
