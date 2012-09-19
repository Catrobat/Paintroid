package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.ui.DrawingSurface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.Display;
import android.view.WindowManager;

public abstract class BaseToolWithRectangleShape extends BaseToolWithShape {

	protected static final int DEFAULT_RECTANGLE_MARGIN = 100;
	protected static final float DEFAULT_TOOL_STROKE_WIDTH = 5f;
	protected static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	protected static final float MAXIMAL_TOOL_STROKE_WIDTH = 10f;
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

	private boolean mRespectBorders;
	private boolean mRotationEnabled;
	private boolean mMoveBorder;

	private int mPrimaryPhaseCount = 0;
	private int mSecondaryPhaseCount = 0;

	private enum FloatingBoxAction {
		NONE, MOVE, RESIZE, ROTATE;
	}

	private enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}

	private enum RotatePosition {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
	}

	public BaseToolWithRectangleShape(Context context, ToolType toolType,
			boolean rotationEnabled, boolean respectBorders) {
		super(context, toolType);
		mToolType = toolType;
		mRotationEnabled = rotationEnabled;
		mRespectBorders = respectBorders;
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mBoxWidth = display.getWidth()
				/ PaintroidApplication.CURRENT_PERSPECTIVE.getScale()
				- getInverselyProportionalSizeForZoom(DEFAULT_RECTANGLE_MARGIN)
				* 2;
		mBoxHeight = mBoxWidth;

		mRotatePosition = RotatePosition.TOP_LEFT;
		mLinePaint = new Paint();
		mLinePaint.setDither(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeJoin(Paint.Join.ROUND);
		mResizeAction = ResizeAction.NONE;

		initScaleDependedValues();
	}

	public BaseToolWithRectangleShape(Context context, ToolType toolType,
			boolean rotationEnabled, boolean respectBorders,
			Bitmap drawingBitmap) {
		this(context, toolType, rotationEnabled, respectBorders);
		mDrawingBitmap = drawingBitmap;
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
		if (mPreviousEventCoordinate == null) {
			return false;
		}
		mMovedDistance.set(
				mMovedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				mMovedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		if (PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.x
				&& PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.y) {
			onClickInBox();
		}
		return true;
	}

	@Override
	public void resetInternalState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawShape(Canvas canvas) {
		initScaleDependedValues();
		canvas.translate(mToolPosition.x, mToolPosition.y);
		canvas.rotate(mBoxRotation);

		// draw bitmap
		if (mDrawingBitmap != null) {
			Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
			canvas.drawBitmap(mDrawingBitmap, null, new RectF(-mBoxWidth / 2,
					-mBoxHeight / 2, mBoxWidth / 2, mBoxHeight / 2),
					bitmapPaint);
		}

		// draw primary color
		PathEffect primaryPathEffect = new DashPathEffect(
				new float[] {
						getInverselyProportionalSizeForZoom(PRIMARY_SHAPE_EFFECT_INTERVAL_OFF),
						getInverselyProportionalSizeForZoom(PRIMARY_SHAPE_EFFECT_INTERVAL_ON) },
				getInverselyProportionalSizeForZoom(PRIMARY_SHAPE_EFFECT_PHASE)
						+ mPrimaryPhaseCount);

		if (mPrimaryPhaseCount == Integer.MAX_VALUE) {
			mPrimaryPhaseCount = 0;
		}

		prepareLinePaint(mPrimaryShapeColor, primaryPathEffect);
		if (mPrimaryPhaseCount % 20 == 1) {
			Paint fontPaint = new Paint();
			fontPaint.setTextSize(50);
			canvas.drawText("CatroAD(tm)", -mBoxWidth / 2, 0, fontPaint);

		}
		canvas.drawRect(-mBoxWidth / 2, mBoxHeight / 2, mBoxWidth / 2,
				-mBoxHeight / 2, mLinePaint);
		// if ((mDrawingBitmap != null) && mRotationEnabled) {
		// canvas.drawCircle(-mBoxWidth / 2 - mRotationSymbolDistance
		// - mRotationSymbolWidth / 2, -mBoxHeight / 2
		// - mRotationSymbolDistance - mRotationSymbolWidth / 2,
		// mRotationSymbolWidth, mLinePaint);
		// }

		// draw secondary color
		PathEffect secondaryPathEffect = new DashPathEffect(
				new float[] {
						getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_INTERVAL_OFF),
						getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_INTERVAL_ON) },
				getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_PHASE)
						+ mSecondaryPhaseCount);
		if (mMoveBorder) {
			mPrimaryPhaseCount++;
			mSecondaryPhaseCount++;
		}
		if (mSecondaryPhaseCount == Integer.MAX_VALUE) {
			mSecondaryPhaseCount = 0;
		}
		prepareLinePaint(mSecondaryShapeColor, secondaryPathEffect);
		canvas.drawRect(-mBoxWidth / 2, mBoxHeight / 2, mBoxWidth / 2,
				-mBoxHeight / 2, mLinePaint);
		// if ((mDrawingBitmap != null) && mRotationEnabled) {
		// canvas.drawCircle(-mBoxWidth / 2 - mRotationSymbolDistance
		// - mRotationSymbolWidth / 2, -mBoxHeight / 2
		// - mRotationSymbolDistance - mRotationSymbolWidth / 2,
		// mRotationSymbolWidth, mLinePaint);
		// }
		canvas.restore();

	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		drawShape(canvas);
	}

	private void move(float deltaX, float deltaY) {
		float newXPos = mToolPosition.x + deltaX;
		float newYPos = mToolPosition.y + deltaY;
		if (mRespectBorders) {
			if (newXPos - mBoxWidth / 2 < 0) {
				newXPos = mBoxWidth / 2;
			} else if (newXPos + mBoxWidth / 2 > PaintroidApplication.DRAWING_SURFACE
					.getBitmapWidth()) {
				newXPos = PaintroidApplication.DRAWING_SURFACE.getBitmapWidth()
						- mBoxWidth / 2;
			}

			if (newYPos - mBoxHeight / 2 < 0) {
				newYPos = mBoxHeight / 2;
			} else if (newYPos + mBoxHeight / 2 > PaintroidApplication.DRAWING_SURFACE
					.getBitmapHeight()) {
				newYPos = PaintroidApplication.DRAWING_SURFACE
						.getBitmapHeight() - mBoxHeight / 2;
			}
		}
		mToolPosition.x = newXPos;
		mToolPosition.y = newYPos;
	}

	public boolean rotate(int degree) {
		if (mDrawingBitmap == null) {
			return false;
		}
		mBoxRotation += degree;
		return true;
	}

	private void rotate(float deltaX, float deltaY) {
		if (mDrawingBitmap == null) {
			return;
		}
		double rotationRadiant = mBoxRotation * Math.PI / 180;
		double deltaXCcorrected = Math.cos(-rotationRadiant) * (deltaX)
				- Math.sin(-rotationRadiant) * (deltaY);
		double deltaYCorrected = Math.sin(-rotationRadiant) * (deltaX)
				+ Math.cos(-rotationRadiant) * (deltaY);

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
			if (mRespectBorders && (newPosY - newHeight / 2 < 0)) {
				newPosX = mToolPosition.x;
				newPosY = mToolPosition.y;
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
			if (mRespectBorders
					&& (newPosY + newHeight / 2 > PaintroidApplication.DRAWING_SURFACE
							.getBitmapHeight())) {
				newPosX = mToolPosition.x;
				newPosY = mToolPosition.y;
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
			if (mRespectBorders && (newPosX - newWidth / 2 < 0)) {
				newPosX = mToolPosition.x;
				newPosY = mToolPosition.y;
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
			if (mRespectBorders
					&& (newPosX + newWidth / 2 > PaintroidApplication.DRAWING_SURFACE
							.getBitmapWidth())) {
				newPosX = mToolPosition.x;
				newPosY = mToolPosition.y;
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

	protected void setMoveBorder(boolean moveBorder) {
		mMoveBorder = moveBorder;
	}

	protected abstract void createAndSetBitmap(DrawingSurface drawingSurface);

	protected abstract void onClickInBox();
}
