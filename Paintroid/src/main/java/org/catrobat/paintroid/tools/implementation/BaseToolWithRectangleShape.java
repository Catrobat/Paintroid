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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ContextCallback.ScreenOrientation;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

import androidx.annotation.ColorRes;
import androidx.annotation.VisibleForTesting;

import static org.catrobat.paintroid.common.Constants.INVALID_RESOURCE_ID;

public abstract class BaseToolWithRectangleShape extends BaseToolWithShape {

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public static final float MAXIMUM_BORDER_RATIO = 2f;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public static final int DEFAULT_BOX_RESIZE_MARGIN = 20;
	protected static final int DEFAULT_RECTANGLE_MARGIN = 100;
	protected static final float DEFAULT_TOOL_STROKE_WIDTH = 3f;
	protected static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	protected static final float MAXIMAL_TOOL_STROKE_WIDTH = 8f;
	protected static final int DEFAULT_ROTATION_SYMBOL_DISTANCE = 20;
	protected static final int DEFAULT_ROTATION_SYMBOL_WIDTH = 30;
	protected static final float DEFAULT_MAXIMUM_BOX_RESOLUTION = 0;
	protected static final int CLICK_IN_BOX_MOVE_TOLERANCE = 10;
	protected static final int DEFAULT_RECTANGLE_SHRINKING = 0;
	protected static final int HIGHLIGHT_RECTANGLE_SHRINKING = 5;

	protected static final boolean DEFAULT_ANTIALIASING_ON = true;

	private static final boolean DEFAULT_ROTATION_ENABLED = false;
	private static final boolean DEFAULT_RESIZE_POINTS_VISIBLE = true;
	private static final boolean DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO = true;
	private static final boolean DEFAULT_RESPECT_MAXIMUM_BOX_RESOLUTION = false;

	private static final int CLICK_TIMEOUT_MILLIS = 250;

	private static final String BUNDLE_BOX_WIDTH = "BOX_WIDTH";
	private static final String BUNDLE_BOX_HEIGHT = "BOX_HEIGHT";
	private static final String BUNDLE_BOX_ROTATION = "BOX_ROTATION";

	private final int rotationArrowArcStrokeWidth;
	private final int rotationArrowArcRadius;
	private final int rotationArrowHeadSize;
	private final int rotationArrowOffset;
	private final Paint arcPaint;
	private final Paint arrowPaint;
	private final Path arcPath;
	private final Path arrowPath;
	private final RectF tempDrawingRectangle;
	private final PointF tempToolPosition;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public float boxWidth;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public float boxHeight;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public float boxRotation; // in degree
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public Bitmap drawingBitmap;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public float rotationSymbolDistance;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public boolean rotationEnabled;
	protected float boxResizeMargin;
	protected float rotationSymbolWidth;
	protected float toolStrokeWidth;
	protected ResizeAction resizeAction;
	protected FloatingBoxAction currentAction;
	protected RotatePosition rotatePosition;
	protected Drawable overlayDrawable;
	protected float maximumBoxResolution;
	protected boolean resizePointsVisible;
	protected boolean respectMaximumBorderRatio;
	protected boolean respectMaximumBoxResolution;
	protected int rectangleShrinkingOnHighlight;
	private CountDownTimer downTimer;

	protected float touchDownPositionX;
	protected float touchDownPositionY;

	public BaseToolWithRectangleShape(ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);

		ScreenOrientation orientation = contextCallback.getOrientation();
		float boxSize = orientation == ScreenOrientation.PORTRAIT
				? metrics.widthPixels
				: metrics.heightPixels;
		boxWidth = boxSize / workspace.getScale()
				- getInverselyProportionalSizeForZoom(DEFAULT_RECTANGLE_MARGIN) * 2;
		boxHeight = boxWidth;

		if (DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO && (
				boxHeight > workspace.getHeight() * MAXIMUM_BORDER_RATIO
						|| boxWidth > workspace.getWidth() * MAXIMUM_BORDER_RATIO)) {
			boxHeight = workspace.getHeight() * MAXIMUM_BORDER_RATIO;
			boxWidth = workspace.getWidth() * MAXIMUM_BORDER_RATIO;
		}

		rectangleShrinkingOnHighlight = DEFAULT_RECTANGLE_SHRINKING;

		rotationArrowArcStrokeWidth = getDensitySpecificValue(2);
		rotationArrowArcRadius = getDensitySpecificValue(8);
		rotationArrowHeadSize = getDensitySpecificValue(3);
		rotationArrowOffset = getDensitySpecificValue(3);

		rotatePosition = RotatePosition.TOP_LEFT;
		resizeAction = ResizeAction.NONE;

		rotationEnabled = DEFAULT_ROTATION_ENABLED;
		resizePointsVisible = DEFAULT_RESIZE_POINTS_VISIBLE;
		respectMaximumBorderRatio = DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO;
		respectMaximumBoxResolution = DEFAULT_RESPECT_MAXIMUM_BOX_RESOLUTION;
		maximumBoxResolution = DEFAULT_MAXIMUM_BOX_RESOLUTION;

		initScaleDependedValues();
		createOverlayDrawable();

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

		workspace.invalidate();
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		movedDistance.set(0, 0);
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		currentAction = getAction(coordinate.x, coordinate.y);
		touchDownPositionX = toolPosition.x;
		touchDownPositionY = toolPosition.y;
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
		if (previousEventCoordinate == null) {
			return false;
		}
		movedDistance.set(
				movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		if (CLICK_IN_BOX_MOVE_TOLERANCE >= movedDistance.x && CLICK_IN_BOX_MOVE_TOLERANCE >= movedDistance.y) {
			toolPosition.x = touchDownPositionX;
			toolPosition.y = touchDownPositionY;
		}
		return true;
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public boolean boxContainsPoint(PointF coordinate) {
		float relativeToOriginX = coordinate.x - toolPosition.x;
		float relativeToOriginY = coordinate.y - toolPosition.y;

		double radians = -(boxRotation * Math.PI / 180);

		float rotatedX = (float) (relativeToOriginX * Math.cos(radians) - relativeToOriginY * Math.sin(radians)) + toolPosition.x;
		float rotatedY = (float) (relativeToOriginX * Math.sin(radians) + relativeToOriginY * Math.cos(radians)) + toolPosition.y;

		return rotatedX > toolPosition.x - boxWidth / 2
				&& rotatedX < toolPosition.x + boxWidth / 2
				&& rotatedY > toolPosition.y - boxHeight / 2
				&& rotatedY < toolPosition.y + boxHeight / 2;
	}

	protected boolean boxIntersectsWorkspace() {
		return toolPosition.x - boxWidth / 2 < workspace.getWidth()
				&& toolPosition.y - boxHeight / 2 < workspace.getHeight()
				&& toolPosition.x + boxWidth / 2 >= 0
				&& toolPosition.y + boxHeight / 2 >= 0;
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

		if (resizePointsVisible) {
			drawToolSpecifics(canvas, boxWidth, boxHeight);
		}

		if (rotationEnabled) {
			drawRotationArrows(canvas, boxWidth, boxHeight);
		}

		drawBitmap(canvas, boxWidth, boxHeight);

		if (overlayDrawable != null) {
			drawOverlayDrawable(canvas, boxWidth, boxHeight, boxRotation);
		}

		drawRectangle(canvas, boxWidth, boxHeight);
		drawToolSpecifics(canvas, boxWidth, boxHeight);

		canvas.restore();
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
		if (drawingBitmap != null) {
			tempDrawingRectangle.set(-boxWidth / 2, -boxHeight / 2,
					boxWidth / 2, boxHeight / 2);
			canvas.clipRect(tempDrawingRectangle);
			canvas.drawBitmap(drawingBitmap, null, tempDrawingRectangle, null);
		}
	}

	private void drawOverlayDrawable(Canvas canvas, float boxWidth, float boxHeight, float boxRotation) {
		int size = (int) (Math.min(boxWidth, boxHeight) / 8);

		canvas.save();
		canvas.rotate(-boxRotation);
		overlayDrawable.setBounds(-size, -size, size, size);
		overlayDrawable.draw(canvas);
		canvas.restore();
	}

	private void drawRectangle(Canvas canvas, float boxWidth, float boxHeight) {
		linePaint.setStrokeWidth(toolStrokeWidth);
		linePaint.setColor(secondaryShapeColor);
		tempDrawingRectangle.set(-boxWidth / 2 + rectangleShrinkingOnHighlight, -boxHeight / 2 + rectangleShrinkingOnHighlight,
				boxWidth / 2 - rectangleShrinkingOnHighlight, boxHeight / 2 - rectangleShrinkingOnHighlight);
		canvas.drawRect(tempDrawingRectangle, linePaint);
	}

	private void move(float deltaX, float deltaY) {
		toolPosition.x += deltaX;
		toolPosition.y += deltaY;
	}

	private void rotate(float deltaX, float deltaY) {
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

		if (rotationEnabled) {
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
		return clickCoordinatesRotatedX > rotationPoint.x - rotationSymbolDistance / 2
				&& clickCoordinatesRotatedX < rotationPoint.x + rotationSymbolDistance / 2
				&& clickCoordinatesRotatedY > rotationPoint.y - rotationSymbolDistance / 2
				&& clickCoordinatesRotatedY < rotationPoint.y + rotationSymbolDistance / 2;
	}

	private void resize(float deltaX, float deltaY) {
		final float maximumBorderRatioWidth = workspace.getWidth() * MAXIMUM_BORDER_RATIO;
		final float maximumBorderRatioHeight = workspace.getHeight() * MAXIMUM_BORDER_RATIO;

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

	protected void preventThatBoxGetsTooLarge(float oldWidth, float oldHeight, float oldPosX, float oldPosY) {
		boxWidth = oldWidth;
		boxHeight = oldHeight;
		toolPosition.x = oldPosX;
		toolPosition.y = oldPosY;
	}

	private void createOverlayDrawable() {
		int overlayDrawableResource = getToolType().getOverlayDrawableResource();
		if (overlayDrawableResource != INVALID_RESOURCE_ID) {
			overlayDrawable = contextCallback.getDrawable(overlayDrawableResource);
			if (overlayDrawable != null) {
				overlayDrawable.setFilterBitmap(false);
			}
		}
	}

	void highlightBox() {
		downTimer = new CountDownTimer(CLICK_TIMEOUT_MILLIS, CLICK_TIMEOUT_MILLIS / 3) {
			@Override
			public void onTick(long millisUntilFinished) {
				highlightBoxWhenClickInBox(true);
				workspace.invalidate();
			}

			@Override
			public void onFinish() {
				highlightBoxWhenClickInBox(false);
				workspace.invalidate();
				downTimer.cancel();
			}
		}.start();
	}

	void highlightBoxWhenClickInBox(boolean highlight) {
		final @ColorRes int colorId = highlight
				? R.color.pocketpaint_main_rectangle_tool_highlight_color
				: R.color.pocketpaint_main_rectangle_tool_accent_color;
		secondaryShapeColor = contextCallback.getColor(colorId);

		rectangleShrinkingOnHighlight = highlight
				? HIGHLIGHT_RECTANGLE_SHRINKING
				: DEFAULT_RECTANGLE_SHRINKING;
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY, int viewWidth, int viewHeight) {
		if (currentAction == FloatingBoxAction.MOVE || currentAction == FloatingBoxAction.RESIZE) {
			return super.getAutoScrollDirection(pointX, pointY, viewWidth, viewHeight);
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
	protected void drawToolSpecifics(Canvas canvas, float boxWidth, float boxHeight) {
		linePaint.setColor(primaryShapeColor);
		linePaint.setStrokeWidth(toolStrokeWidth * 2);

		PointF rightTopPoint = new PointF(-boxWidth / 2 + rectangleShrinkingOnHighlight, -boxHeight / 2 + rectangleShrinkingOnHighlight);

		for (int lines = 0; lines < 4; lines++) {
			float resizeLineLengthHeight = boxHeight / 10;
			float resizeLineLengthWidth = boxWidth / 10;

			canvas.drawLine(rightTopPoint.x - toolStrokeWidth / 2,
					rightTopPoint.y, rightTopPoint.x + resizeLineLengthWidth,
					rightTopPoint.y, linePaint);

			canvas.drawLine(rightTopPoint.x, rightTopPoint.y
							- toolStrokeWidth / 2, rightTopPoint.x,
					rightTopPoint.y + resizeLineLengthHeight, linePaint);

			canvas.drawLine(rightTopPoint.x + boxWidth / 2
							- resizeLineLengthWidth, rightTopPoint.y, rightTopPoint.x
							+ boxWidth / 2 + resizeLineLengthWidth, rightTopPoint.y,
					linePaint);
			canvas.rotate(90);
			float tempX = rightTopPoint.x;
			rightTopPoint.x = rightTopPoint.y;
			rightTopPoint.y = tempX;
			float tempHeight = boxHeight;
			boxHeight = boxWidth;
			boxWidth = tempHeight;
		}
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
