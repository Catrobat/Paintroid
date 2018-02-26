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

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorRes;
import android.support.annotation.VisibleForTesting;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

public abstract class BaseToolWithRectangleShape extends BaseToolWithShape {

	@VisibleForTesting
	public static final float MAXIMUM_BORDER_RATIO = 2f;
	@VisibleForTesting
	public static final int DEFAULT_BOX_RESIZE_MARGIN = 20;
	protected static final int DEFAULT_RECTANGLE_MARGIN = 100;
	protected static final float DEFAULT_TOOL_STROKE_WIDTH = 3f;
	protected static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	protected static final float MAXIMAL_TOOL_STROKE_WIDTH = 8f;
	protected static final int DEFAULT_ROTATION_SYMBOL_DISTANCE = 20;
	protected static final int DEFAULT_ROTATION_SYMBOL_WIDTH = 30;
	protected static final float DEFAULT_MAXIMUM_BOX_RESOLUTION = 0;
	protected static final int CLICK_IN_BOX_MOVE_TOLERANCE = 10;

	protected static final boolean DEFAULT_ANTIALIASING_ON = true;

	private static final boolean DEFAULT_RESPECT_BORDERS = false;
	private static final boolean DEFAULT_ROTATION_ENABLED = false;
	private static final boolean DEFAULT_BACKGROUND_SHADOW_ENABLED = true;
	private static final boolean DEFAULT_RESIZE_POINTS_VISIBLE = true;
	private static final boolean DEFAULT_STATUS_ICON_ENABLED = false;
	private static final boolean DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO = true;
	private static final boolean DEFAULT_RESPECT_MAXIMUM_BOX_RESOLUTION = false;

	private static final int CLICK_TIMEOUT_MILLIS = 150;

	private static final String BUNDLE_BOX_WIDTH = "BOX_WIDTH";
	private static final String BUNDLE_BOX_HEIGHT = "BOX_HEIGHT";
	private static final String BUNDLE_BOX_ROTATION = "BOX_ROTATION";

	private final int resizeCircleSize;
	private final int rotationArrowArcStrokeWidth;
	private final int rotationArrowArcRadius;
	private final int rotationArrowHeadSize;
	private final int rotationArrowOffset;

	@VisibleForTesting
	public float boxWidth;
	@VisibleForTesting
	public float boxHeight;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public float boxRotation; // in degree
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public Bitmap drawingBitmap;
	protected float boxResizeMargin;
	@VisibleForTesting
	public float rotationSymbolDistance;
	protected float rotationSymbolWidth;
	protected float toolStrokeWidth;
	protected ResizeAction resizeAction;
	protected FloatingBoxAction currentAction;
	protected RotatePosition rotatePosition;
	protected Bitmap overlayBitmap;
	protected float maximumBoxResolution;

	@VisibleForTesting
	public boolean respectImageBounds;
	@VisibleForTesting
	public boolean rotationEnabled;
	private boolean backgroundShadowEnabled;
	private boolean resizePointsVisible;
	private boolean statusIconEnabled;
	private boolean respectMaximumBorderRatio;
	private boolean respectMaximumBoxResolution;

	private boolean isDown = false;
	private CountDownTimer downTimer;

	private final Paint arcPaint;
	private final Paint arrowPaint;
	private final Path arcPath;
	private final Path arrowPath;
	private final Paint backgroundPaint;
	private final Paint circlePaint;
	private final RectF tempDrawingRectangle;
	private final PointF tempToolPosition;

	public BaseToolWithRectangleShape(Context context, ToolType toolType) {
		super(context, toolType);

		final Resources resources = context.getResources();
		int orientation = resources.getConfiguration().orientation;
		float boxSize = orientation == Configuration.ORIENTATION_PORTRAIT
				? metrics.widthPixels
				: metrics.heightPixels;
		boxWidth = boxSize / PaintroidApplication.perspective.getScale()
				- getInverselyProportionalSizeForZoom(DEFAULT_RECTANGLE_MARGIN) * 2;
		boxHeight = boxWidth;

		if (DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO && !PaintroidApplication.drawingSurface.isBitmapNull() && (
				boxHeight > PaintroidApplication.drawingSurface
						.getBitmapHeight() * MAXIMUM_BORDER_RATIO
						|| boxWidth > PaintroidApplication.drawingSurface
						.getBitmapWidth() * MAXIMUM_BORDER_RATIO)) {
			boxHeight = PaintroidApplication.drawingSurface.getBitmapHeight() * MAXIMUM_BORDER_RATIO;
			boxWidth = PaintroidApplication.drawingSurface.getBitmapWidth() * MAXIMUM_BORDER_RATIO;
		}

		resizeCircleSize = getDensitySpecificValue(4);
		rotationArrowArcStrokeWidth = getDensitySpecificValue(2);
		rotationArrowArcRadius = getDensitySpecificValue(8);
		rotationArrowHeadSize = getDensitySpecificValue(3);
		rotationArrowOffset = getDensitySpecificValue(3);

		rotatePosition = RotatePosition.TOP_LEFT;
		resizeAction = ResizeAction.NONE;

		respectImageBounds = DEFAULT_RESPECT_BORDERS;
		rotationEnabled = DEFAULT_ROTATION_ENABLED;
		backgroundShadowEnabled = DEFAULT_BACKGROUND_SHADOW_ENABLED;
		resizePointsVisible = DEFAULT_RESIZE_POINTS_VISIBLE;
		statusIconEnabled = DEFAULT_STATUS_ICON_ENABLED;
		respectMaximumBorderRatio = DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO;
		respectMaximumBoxResolution = DEFAULT_RESPECT_MAXIMUM_BOX_RESOLUTION;
		maximumBoxResolution = DEFAULT_MAXIMUM_BOX_RESOLUTION;

		initScaleDependedValues();

		linePaint.reset();
		linePaint.setDither(true);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeJoin(Paint.Join.ROUND);

		arcPaint = new Paint();
		arcPaint.setColor(Color.WHITE);
		arcPaint.setStyle(Paint.Style.STROKE);
		arcPaint.setStrokeCap(Cap.BUTT);

		arrowPaint = new Paint();
		arrowPaint.setColor(Color.WHITE);
		arrowPaint.setStyle(Paint.Style.FILL);

		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.argb(128, 0, 0, 0));
		backgroundPaint.setStyle(Style.FILL);

		circlePaint = new Paint();
		circlePaint.setStyle(Style.FILL);

		arcPath = new Path();
		arrowPath = new Path();
		tempDrawingRectangle = new RectF();
		tempToolPosition = new PointF();
	}

	private int getDensitySpecificValue(int value) {
		int baseDensity = DisplayMetrics.DENSITY_MEDIUM;
		int density = Math.max(metrics.densityDpi, DisplayMetrics.DENSITY_MEDIUM);
		return value * density / baseDensity;
	}

	private void initScaleDependedValues() {
		toolStrokeWidth = getStrokeWidthForZoom(DEFAULT_TOOL_STROKE_WIDTH,
				MINIMAL_TOOL_STROKE_WIDTH, MAXIMAL_TOOL_STROKE_WIDTH);
		boxResizeMargin = getInverselyProportionalSizeForZoom(DEFAULT_BOX_RESIZE_MARGIN);
		rotationSymbolDistance = getInverselyProportionalSizeForZoom(DEFAULT_ROTATION_SYMBOL_DISTANCE) * 2;
		rotationSymbolWidth = getInverselyProportionalSizeForZoom(DEFAULT_ROTATION_SYMBOL_WIDTH);
	}

	public void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			drawingBitmap = bitmap;
		}

		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		isDown = true;
		movedDistance.set(0, 0);
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		currentAction = getAction(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (previousEventCoordinate == null || currentAction == null) {
			return false;
		}

		PointF delta = new PointF(coordinate.x - previousEventCoordinate.x,
				coordinate.y - previousEventCoordinate.y);
		movedDistance.set(movedDistance.x + Math.abs(delta.x),
				movedDistance.y + Math.abs(delta.y));
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		switch (currentAction) {
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
		isDown = false;
		if (previousEventCoordinate == null) {
			return false;
		}
		movedDistance.set(
				movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		if (CLICK_IN_BOX_MOVE_TOLERANCE >= movedDistance.x && CLICK_IN_BOX_MOVE_TOLERANCE >= movedDistance.y
				&& isCoordinateInsideBox(coordinate)) {
			onClickInBox();
		}
		return true;
	}

	protected boolean isCoordinateInsideBox(PointF coordinate) {
		if (coordinate.x > toolPosition.x - boxWidth / 2
				&& coordinate.x < toolPosition.x + boxWidth / 2
				&& coordinate.y > toolPosition.y - boxHeight / 2
				&& coordinate.y < toolPosition.y + boxHeight / 2) {
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

		float boxWidth = this.boxWidth;
		float boxHeight = this.boxHeight;
		float boxRotation = this.boxRotation;
		tempToolPosition.set(this.toolPosition.x, this.toolPosition.y);

		canvas.save();

		canvas.translate(tempToolPosition.x, tempToolPosition.y);
		canvas.rotate(boxRotation);

		if (backgroundShadowEnabled) {
			drawBackgroundShadow(canvas, boxWidth, boxHeight, boxRotation, tempToolPosition);
		}

		if (resizePointsVisible) {
			drawResizePoints(canvas, boxWidth, boxHeight);
		}

		if (drawingBitmap != null && rotationEnabled) {
			drawRotationArrows(canvas, boxWidth, boxHeight);
		}

		if (drawingBitmap != null) {
			drawBitmap(canvas, boxWidth, boxHeight);
		}
		if (overlayBitmap != null) {
			drawOverlayBitmap(canvas, boxWidth, boxHeight);
		}

		drawRectangle(canvas, boxWidth, boxHeight);
		drawToolSpecifics(canvas, boxWidth, boxHeight);

		if (statusIconEnabled) {
			drawStatus(canvas);
		}

		canvas.restore();
	}

	private void drawBackgroundShadow(Canvas canvas, float boxWidth, float boxHeight, float boxRotation, PointF toolPosition) {
		canvas.save();
		canvas.clipRect((-boxWidth + toolStrokeWidth) / 2,
				(boxHeight - toolStrokeWidth) / 2,
				(boxWidth - toolStrokeWidth) / 2,
				(-boxHeight + toolStrokeWidth) / 2, Op.DIFFERENCE);
		canvas.rotate(-boxRotation);
		canvas.translate(-toolPosition.x, -toolPosition.y);
		canvas.drawRect(0, 0,
				PaintroidApplication.drawingSurface.getBitmapWidth(),
				PaintroidApplication.drawingSurface.getBitmapHeight(), backgroundPaint);
		canvas.restore();
	}

	private void drawResizePoints(Canvas canvas, float boxWidth, float boxHeight) {
		float circleRadius = getInverselyProportionalSizeForZoom(resizeCircleSize);
		circlePaint.setColor(secondaryShapeColor);
		canvas.drawCircle(0, -boxHeight / 2, circleRadius, circlePaint);
		canvas.drawCircle(boxWidth / 2, -boxHeight / 2, circleRadius,
				circlePaint);
		canvas.drawCircle(boxWidth / 2, 0, circleRadius, circlePaint);
		canvas.drawCircle(boxWidth / 2, boxHeight / 2, circleRadius,
				circlePaint);
		canvas.drawCircle(0, boxHeight / 2, circleRadius, circlePaint);
		canvas.drawCircle(-boxWidth / 2, boxHeight / 2, circleRadius,
				circlePaint);
		canvas.drawCircle(-boxWidth / 2, 0, circleRadius, circlePaint);
		canvas.drawCircle(-boxWidth / 2, -boxHeight / 2, circleRadius,
				circlePaint);
	}

	private void drawRotationArrows(Canvas canvas, float boxWidth, float boxHeight) {
		float arcStrokeWidth = getInverselyProportionalSizeForZoom(rotationArrowArcStrokeWidth);
		float arcRadius = getInverselyProportionalSizeForZoom(rotationArrowArcRadius);
		float arrowSize = getInverselyProportionalSizeForZoom(rotationArrowHeadSize);
		float offset = getInverselyProportionalSizeForZoom(rotationArrowOffset);

		arcPaint.setStrokeWidth(arcStrokeWidth);

		for (int i = 0; i < 4; i++) {

			float xBase = -boxWidth / 2 - offset;
			float yBase = -boxHeight / 2 - offset;

			arcPath.reset();

			tempDrawingRectangle.set(xBase - arcRadius, yBase - arcRadius, xBase
					+ arcRadius, yBase + arcRadius);
			arcPath.addArc(tempDrawingRectangle, 180, 90);

			canvas.drawPath(arcPath, arcPaint);

			arrowPath.reset();
			arrowPath.moveTo(xBase - arcRadius - arrowSize, yBase);
			arrowPath.lineTo(xBase - arcRadius + arrowSize, yBase);
			arrowPath.lineTo(xBase - arcRadius, yBase + arrowSize);
			arrowPath.close();

			arrowPath.moveTo(xBase, yBase - arcRadius - arrowSize);
			arrowPath.lineTo(xBase, yBase - arcRadius + arrowSize);
			arrowPath.lineTo(xBase + arrowSize, yBase - arcRadius);
			arrowPath.close();
			canvas.drawPath(arrowPath, arrowPaint);

			float tempLength = boxWidth;
			boxWidth = boxHeight;
			boxHeight = tempLength;
			canvas.rotate(90);
		}
	}

	protected void drawBitmap(Canvas canvas, float boxWidth, float boxHeight) {
		tempDrawingRectangle.set(-boxWidth / 2, -boxHeight / 2,
				boxWidth / 2, boxHeight / 2);
		canvas.clipRect(tempDrawingRectangle, Op.UNION);
		canvas.drawBitmap(drawingBitmap, null, tempDrawingRectangle, null);
	}

	private void drawOverlayBitmap(Canvas canvas, float boxWidth, float boxHeight) {
		float size = Math.min(boxWidth, boxHeight) / 8;
		tempDrawingRectangle.set(-size, -size, size, size);
		canvas.drawBitmap(overlayBitmap, null, tempDrawingRectangle, null);
	}

	private void drawRectangle(Canvas canvas, float boxWidth, float boxHeight) {
		linePaint.setStrokeWidth(toolStrokeWidth);
		linePaint.setColor(secondaryShapeColor);
		tempDrawingRectangle.set(-boxWidth / 2, -boxHeight / 2,
				boxWidth / 2, boxHeight / 2);
		canvas.drawRect(tempDrawingRectangle, linePaint);
	}

	private void drawStatus(Canvas canvas) {
		RectF statusRect = new RectF(-48, -48, 48, 48);
		if (isDown) {

			int bitmapId;
			switch (currentAction) {
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
				statusPaint.setColor(secondaryShapeColor);
				canvas.clipRect(statusRect, Op.UNION);
				statusPaint.setAlpha(128);
				canvas.drawOval(statusRect, statusPaint);

				Bitmap actionBitmap = BitmapFactory.decodeResource(context.getResources(), bitmapId);
				statusPaint.setAlpha(255);
				canvas.rotate(-boxRotation);
				canvas.drawBitmap(actionBitmap, -24, -24, statusPaint);
				canvas.rotate(boxRotation);
			}
		}
	}

	private void move(float deltaX, float deltaY) {
		float newXPos = toolPosition.x + deltaX;
		float newYPos = toolPosition.y + deltaY;
		if (respectImageBounds) {
			if (newXPos - boxWidth / 2 < 0) {
				newXPos = boxWidth / 2;
			} else if (newXPos + boxWidth / 2 > PaintroidApplication.drawingSurface
					.getBitmapWidth()) {
				newXPos = PaintroidApplication.drawingSurface.getBitmapWidth()
						- boxWidth / 2;
			}

			if (newYPos - boxHeight / 2 < 0) {
				newYPos = boxHeight / 2;
			} else if (newYPos + boxHeight / 2 > PaintroidApplication.drawingSurface
					.getBitmapHeight()) {
				newYPos = PaintroidApplication.drawingSurface.getBitmapHeight()
						- boxHeight / 2;
			}
		}
		toolPosition.x = newXPos;
		toolPosition.y = newYPos;
	}

	private void rotate(float deltaX, float deltaY) {
		if (drawingBitmap == null) {
			return;
		}

		PointF currentPoint = new PointF(previousEventCoordinate.x, previousEventCoordinate.y);

		double previousXLength = previousEventCoordinate.x - deltaX - toolPosition.x;
		double previousYLength = previousEventCoordinate.y - deltaY - toolPosition.y;
		double currentXLength = currentPoint.x - toolPosition.x;
		double currentYLength = currentPoint.y - toolPosition.y;

		double rotationAnglePrevious = Math.atan2(previousYLength, previousXLength);
		double rotationAngleCurrent = Math.atan2(currentYLength, currentXLength);
		double deltaAngle = -(rotationAnglePrevious - rotationAngleCurrent);

		boxRotation += (float) Math.toDegrees(deltaAngle) + 360;
		boxRotation = boxRotation % 360;
		if (boxRotation > 180) {
			boxRotation -= 360;
		}
	}

	private FloatingBoxAction getAction(float clickCoordinatesX,
			float clickCoordinatesY) {
		resizeAction = ResizeAction.NONE;
		double rotationRadiant = boxRotation * Math.PI / 180;
		float clickCoordinatesRotatedX = (float) (toolPosition.x
				+ Math.cos(-rotationRadiant)
				* (clickCoordinatesX - toolPosition.x) - Math
				.sin(-rotationRadiant) * (clickCoordinatesY - toolPosition.y));
		float clickCoordinatesRotatedY = (float) (toolPosition.y
				+ Math.sin(-rotationRadiant)
				* (clickCoordinatesX - toolPosition.x) + Math
				.cos(-rotationRadiant) * (clickCoordinatesY - toolPosition.y));

		// Move (within box)
		if (clickCoordinatesRotatedX < toolPosition.x + boxWidth / 2
				- boxResizeMargin
				&& clickCoordinatesRotatedX > toolPosition.x - boxWidth / 2
				+ boxResizeMargin
				&& clickCoordinatesRotatedY < toolPosition.y + boxHeight / 2
				- boxResizeMargin
				&& clickCoordinatesRotatedY > toolPosition.y - boxHeight / 2
				+ boxResizeMargin) {
			return FloatingBoxAction.MOVE;
		}

		// Resize (on frame)
		if (clickCoordinatesRotatedX < toolPosition.x + boxWidth / 2
				+ boxResizeMargin
				&& clickCoordinatesRotatedX > toolPosition.x - boxWidth / 2
				- boxResizeMargin
				&& clickCoordinatesRotatedY < toolPosition.y + boxHeight / 2
				+ boxResizeMargin
				&& clickCoordinatesRotatedY > toolPosition.y - boxHeight / 2
				- boxResizeMargin) {
			if (clickCoordinatesRotatedX < toolPosition.x - boxWidth / 2
					+ boxResizeMargin) {
				resizeAction = ResizeAction.LEFT;
			} else if (clickCoordinatesRotatedX > toolPosition.x + boxWidth
					/ 2 - boxResizeMargin) {
				resizeAction = ResizeAction.RIGHT;
			}
			if (clickCoordinatesRotatedY < toolPosition.y - boxHeight / 2
					+ boxResizeMargin) {
				if (resizeAction == ResizeAction.LEFT) {
					resizeAction = ResizeAction.TOPLEFT;
				} else if (resizeAction == ResizeAction.RIGHT) {
					resizeAction = ResizeAction.TOPRIGHT;
				} else {
					resizeAction = ResizeAction.TOP;
				}
			} else if (clickCoordinatesRotatedY > toolPosition.y + boxHeight
					/ 2 - boxResizeMargin) {
				if (resizeAction == ResizeAction.LEFT) {
					resizeAction = ResizeAction.BOTTOMLEFT;
				} else if (resizeAction == ResizeAction.RIGHT) {
					resizeAction = ResizeAction.BOTTOMRIGHT;
				} else {
					resizeAction = ResizeAction.BOTTOM;
				}
			}
			return FloatingBoxAction.RESIZE;
		}

		// Only allow rotation if an image is present
		if (drawingBitmap != null && rotationEnabled) {
			PointF topLeftRotationPoint = new PointF(toolPosition.x - boxWidth / 2 - rotationSymbolDistance / 2,
					toolPosition.y - boxHeight / 2 - rotationSymbolDistance / 2);
			PointF topRightRotationPoint = new PointF(toolPosition.x + boxWidth / 2 + rotationSymbolDistance / 2,
					toolPosition.y - boxHeight / 2 - rotationSymbolDistance / 2);
			PointF bottomLeftRotationPoint = new PointF(toolPosition.x - boxWidth / 2 - rotationSymbolDistance / 2,
					toolPosition.y + boxHeight / 2 + rotationSymbolDistance / 2);
			PointF bottomRightRotationPoint = new PointF(toolPosition.x + boxWidth / 2 + rotationSymbolDistance / 2,
					toolPosition.y + boxHeight / 2 + rotationSymbolDistance / 2);

			if (checkRotationPoints(clickCoordinatesRotatedX, clickCoordinatesRotatedY, topLeftRotationPoint)
					|| checkRotationPoints(clickCoordinatesRotatedX, clickCoordinatesRotatedY, topRightRotationPoint)
					|| checkRotationPoints(clickCoordinatesRotatedX, clickCoordinatesRotatedY, bottomLeftRotationPoint)
					|| checkRotationPoints(clickCoordinatesRotatedX, clickCoordinatesRotatedY, bottomRightRotationPoint)) {

				return FloatingBoxAction.ROTATE;
			}
		}
		return FloatingBoxAction.MOVE;
	}

	private boolean checkRotationPoints(float clickCoordinatesRotatedX, float clickCoordinatesRotatedY, PointF rotationPoint) {
		if (clickCoordinatesRotatedX > rotationPoint.x - rotationSymbolDistance / 2
				&& clickCoordinatesRotatedX < rotationPoint.x + rotationSymbolDistance / 2
				&& clickCoordinatesRotatedY > rotationPoint.y - rotationSymbolDistance / 2
				&& clickCoordinatesRotatedY < rotationPoint.y + rotationSymbolDistance / 2) {
			return true;
		}
		return false;
	}

	private void resize(float deltaX, float deltaY) {
		final DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
		final int drawingSurfaceBitmapWidth = drawingSurface.getBitmapWidth();
		final int drawingSurfaceBitmapHeight = drawingSurface.getBitmapHeight();
		final float maximumBorderRatioWidth = drawingSurfaceBitmapWidth * MAXIMUM_BORDER_RATIO;
		final float maximumBorderRatioHeight = drawingSurfaceBitmapHeight * MAXIMUM_BORDER_RATIO;

		double rotationRadian = Math.toRadians(boxRotation);
		double deltaXCorrected = Math.cos(-rotationRadian) * deltaX
				- Math.sin(-rotationRadian) * deltaY;
		double deltaYCorrected = Math.sin(-rotationRadian) * deltaX
				+ Math.cos(-rotationRadian) * deltaY;

		switch (resizeAction) {
			case TOPLEFT:
			case BOTTOMRIGHT:
				if (Math.abs(deltaXCorrected) > Math.abs(deltaYCorrected)) {
					deltaYCorrected = (boxWidth + deltaXCorrected) * boxHeight / boxWidth - boxHeight;
				} else {
					deltaXCorrected = boxWidth * (boxHeight + deltaYCorrected) / boxHeight - boxWidth;
				}
				break;
			case TOPRIGHT:
			case BOTTOMLEFT:
				if (Math.abs(deltaXCorrected) > Math.abs(deltaYCorrected)) {
					deltaYCorrected = (boxWidth - deltaXCorrected) * boxHeight / boxWidth - boxHeight;
				} else {
					deltaXCorrected = boxWidth * (boxHeight - deltaYCorrected) / boxHeight - boxWidth;
				}
				break;
		}

		float resizeXMoveCenterX = (float) (deltaXCorrected / 2 * Math.cos(rotationRadian));
		float resizeXMoveCenterY = (float) (deltaXCorrected / 2 * Math.sin(rotationRadian));
		float resizeYMoveCenterX = (float) (deltaYCorrected / 2 * Math.sin(rotationRadian));
		float resizeYMoveCenterY = (float) (deltaYCorrected / 2 * Math.cos(rotationRadian));

		float newHeight;
		float newWidth;
		float oldHeight = boxHeight;
		float oldWidth = boxWidth;

		float newPosX;
		float newPosY;
		float oldPosX = toolPosition.x;
		float oldPosY = toolPosition.y;

		// Height
		switch (resizeAction) {
			case TOP:
			case TOPRIGHT:
			case TOPLEFT:
				newHeight = (float) (boxHeight - deltaYCorrected);
				newPosX = toolPosition.x - resizeYMoveCenterX;
				newPosY = toolPosition.y + resizeYMoveCenterY;
				if (respectImageBounds && newPosY - newHeight / 2 < 0) {
					break;
				}

				if (respectMaximumBorderRatio && newHeight > maximumBorderRatioHeight) {
					boxHeight = maximumBorderRatioHeight;
					break;
				}

				boxHeight = newHeight;
				toolPosition.x = newPosX;
				toolPosition.y = newPosY;

				break;
			case BOTTOM:
			case BOTTOMLEFT:
			case BOTTOMRIGHT:
				newHeight = (float) (boxHeight + deltaYCorrected);
				newPosX = toolPosition.x - resizeYMoveCenterX;
				newPosY = toolPosition.y + resizeYMoveCenterY;
				if (respectImageBounds && newPosY + newHeight / 2 > drawingSurfaceBitmapHeight) {
					break;
				}

				if (respectMaximumBorderRatio && newHeight > maximumBorderRatioHeight) {
					boxHeight = maximumBorderRatioHeight;
					break;
				}

				boxHeight = newHeight;
				toolPosition.x = newPosX;
				toolPosition.y = newPosY;

				break;
			default:
				break;
		}

		// Width
		switch (resizeAction) {
			case LEFT:
			case TOPLEFT:
			case BOTTOMLEFT:
				newWidth = (float) (boxWidth - deltaXCorrected);
				newPosX = toolPosition.x + resizeXMoveCenterX;
				newPosY = toolPosition.y + resizeXMoveCenterY;
				if (respectImageBounds && newPosX - newWidth / 2 < 0) {
					break;
				}

				if (respectMaximumBorderRatio && newWidth > maximumBorderRatioWidth) {
					boxWidth = maximumBorderRatioWidth;
					break;
				}

				boxWidth = newWidth;
				toolPosition.x = newPosX;
				toolPosition.y = newPosY;

				break;
			case RIGHT:
			case TOPRIGHT:
			case BOTTOMRIGHT:
				newWidth = (float) (boxWidth + deltaXCorrected);
				newPosX = toolPosition.x + resizeXMoveCenterX;
				newPosY = toolPosition.y + resizeXMoveCenterY;
				if (respectImageBounds && newPosX + newWidth / 2 > drawingSurfaceBitmapWidth) {
					break;
				}

				if (respectMaximumBorderRatio && newWidth > maximumBorderRatioWidth) {
					boxWidth = maximumBorderRatioWidth;
					break;
				}

				boxWidth = newWidth;
				toolPosition.x = newPosX;
				toolPosition.y = newPosY;

				break;
			default:
				break;
		}

		// prevent that box gets too small
		if (boxWidth < DEFAULT_BOX_RESIZE_MARGIN) {
			boxWidth = DEFAULT_BOX_RESIZE_MARGIN;
			toolPosition.x = oldPosX;
		}
		if (boxHeight < DEFAULT_BOX_RESIZE_MARGIN) {
			boxHeight = DEFAULT_BOX_RESIZE_MARGIN;
			toolPosition.y = oldPosY;
		}

		if (respectMaximumBoxResolution && maximumBoxResolution > 0
				&& boxWidth * boxHeight > maximumBoxResolution) {
			preventThatBoxGetsTooLarge(oldWidth, oldHeight, oldPosX, oldPosY);
		}
	}

	protected void setRespectImageBounds(boolean respectImageBounds) {
		this.respectImageBounds = respectImageBounds;
	}

	protected void setRotationEnabled(boolean rotationEnabled) {
		this.rotationEnabled = rotationEnabled;
	}

	protected void setResizePointsVisible(boolean resizePointsVisible) {
		this.resizePointsVisible = resizePointsVisible;
	}

	protected void setRespectMaximumBorderRatio(boolean respectMaximumBorderRatio) {
		this.respectMaximumBorderRatio = respectMaximumBorderRatio;
	}

	protected void setRespectMaximumBoxResolution(boolean respectMaximumBoxResolution) {
		this.respectMaximumBoxResolution = respectMaximumBoxResolution;
	}

	protected void setMaximumBoxResolution(float maximumBoxResolution) {
		this.maximumBoxResolution = maximumBoxResolution;
	}

	protected abstract void onClickInBox();

	protected void drawToolSpecifics(Canvas canvas, float boxWidth, float boxHeight) {
	}

	protected void preventThatBoxGetsTooLarge(float oldWidth, float oldHeight,
			float oldPosX, float oldPosY) {
		boxWidth = oldWidth;
		boxHeight = oldHeight;
		toolPosition.x = oldPosX;
		toolPosition.y = oldPosY;
	}

	void createOverlayBitmap() {
		overlayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon_overlay_button);
	}

	void highlightBox() {
		downTimer = new CountDownTimer(CLICK_TIMEOUT_MILLIS, CLICK_TIMEOUT_MILLIS / 3) {
			@Override
			public void onTick(long millisUntilFinished) {
				highlightBoxWhenClickInBox(true);
				PaintroidApplication.drawingSurface.refreshDrawingSurface();
			}

			@Override
			public void onFinish() {
				highlightBoxWhenClickInBox(false);
				PaintroidApplication.drawingSurface.refreshDrawingSurface();
				downTimer.cancel();
			}
		}.start();
	}

	void highlightBoxWhenClickInBox(boolean highlight) {
		final Resources resources = context.getResources();
		final @ColorRes int colorId = highlight
				? R.color.color_highlight_box
				: R.color.rectangle_secondary_color;
		secondaryShapeColor = resources.getColor(colorId);
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY,
			int viewWidth, int viewHeight) {

		if (currentAction == FloatingBoxAction.MOVE
				|| currentAction == FloatingBoxAction.RESIZE) {

			return super.getAutoScrollDirection(pointX, pointY, viewWidth,
					viewHeight);
		}
		return new Point(0, 0);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putFloat(BUNDLE_BOX_WIDTH, boxWidth);
		bundle.putFloat(BUNDLE_BOX_HEIGHT, boxHeight);
		bundle.putFloat(BUNDLE_BOX_ROTATION, boxRotation);
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		boxWidth = bundle.getFloat(BUNDLE_BOX_WIDTH, boxWidth);
		boxHeight = bundle.getFloat(BUNDLE_BOX_HEIGHT, boxHeight);
		boxRotation = bundle.getFloat(BUNDLE_BOX_ROTATION, boxRotation);
	}

	@Override
	public void setupToolOptions() {
	}

	private enum FloatingBoxAction {
		NONE, MOVE, RESIZE, ROTATE
	}

	private enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT
	}

	private enum RotatePosition {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}
}
