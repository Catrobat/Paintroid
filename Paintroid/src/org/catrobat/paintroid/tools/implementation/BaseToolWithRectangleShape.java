package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.ui.DrawingSurface;

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
	protected boolean mUseSquareShape;
	protected boolean mUseColorChooser;

	private boolean mRotationEnabled;

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
			boolean rotationEnabled, boolean useColorChooser) {
		super(context, toolType);
		mToolType = toolType;
		mRotationEnabled = rotationEnabled;
		mUseColorChooser = useColorChooser;

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

		mUseSquareShape = false;

		initScaleDependedValues();
	}

	public BaseToolWithRectangleShape(Context context, ToolType toolType,
			boolean rotationEnabled, boolean useColorChooser,
			Bitmap drawingBitmap) {
		this(context, toolType, rotationEnabled, useColorChooser);
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

	protected void setUseSquareShape(boolean useSquareShape) {
		mUseSquareShape = useSquareShape;
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
		mMovedDistance.set(
				mMovedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				mMovedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		if (PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.x
				&& PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.y) {
			if (mDrawingBitmap == null) {
				createAndSetBitmap(PaintroidApplication.DRAWING_SURFACE);
			} else {
				Point intPosition = new Point((int) mToolPosition.x,
						(int) mToolPosition.y);
				Command command = new StampCommand(mDrawingBitmap, intPosition,
						mBoxWidth, mBoxHeight, mBoxRotation);
				((StampCommand) command).addObserver(this);
				mProgressDialog.show();
				PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
			}
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
		if (mUseColorChooser) {
			createAndSetBitmap(PaintroidApplication.DRAWING_SURFACE);
		}
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
				getInverselyProportionalSizeForZoom(PRIMARY_SHAPE_EFFECT_PHASE));
		prepareLinePaint(mPrimaryShapeColor, primaryPathEffect);
		canvas.drawRect(-mBoxWidth / 2, mBoxHeight / 2, mBoxWidth / 2,
				-mBoxHeight / 2, mLinePaint);
		if ((mDrawingBitmap != null) && mRotationEnabled) {
			canvas.drawCircle(-mBoxWidth / 2 - mRotationSymbolDistance
					- mRotationSymbolWidth / 2, -mBoxHeight / 2
					- mRotationSymbolDistance - mRotationSymbolWidth / 2,
					mRotationSymbolWidth, mLinePaint);
		}

		// draw secondary color
		PathEffect secondaryPathEffect = new DashPathEffect(
				new float[] {
						getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_INTERVAL_OFF),
						getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_INTERVAL_ON) },
				getInverselyProportionalSizeForZoom(SECONDARY_SHAPE_EFFECT_PHASE));
		prepareLinePaint(mSecondaryShapeColor, secondaryPathEffect);
		canvas.drawRect(-mBoxWidth / 2, mBoxHeight / 2, mBoxWidth / 2,
				-mBoxHeight / 2, mLinePaint);
		if ((mDrawingBitmap != null) && mRotationEnabled) {
			canvas.drawCircle(-mBoxWidth / 2 - mRotationSymbolDistance
					- mRotationSymbolWidth / 2, -mBoxHeight / 2
					- mRotationSymbolDistance - mRotationSymbolWidth / 2,
					mRotationSymbolWidth, mLinePaint);
		}
		canvas.restore();

	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		drawShape(canvas);
	}

	public boolean rotate(int degree) {
		if (mDrawingBitmap == null) {
			return false;
		}
		mBoxRotation += degree;
		return true;
	}

	protected void rotate(float delta_x, float delta_y) {
		if (mDrawingBitmap == null) {
			return;
		}
		double rotationRadiant = mBoxRotation * Math.PI / 180;
		double deltaXCcorrected = Math.cos(-rotationRadiant) * (delta_x)
				- Math.sin(-rotationRadiant) * (delta_y);
		double deltaYCorrected = Math.sin(-rotationRadiant) * (delta_x)
				+ Math.cos(-rotationRadiant) * (delta_y);

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

	protected FloatingBoxAction getAction(float clickCoordinatesX,
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

	protected void resize(float delta_x, float delta_y) {
		double rotationRadian = mBoxRotation * Math.PI / 180;
		double deltaXCorrected = Math.cos(-rotationRadian) * (delta_x)
				- Math.sin(-rotationRadian) * (delta_y);
		double deltaYCorrected = Math.sin(-rotationRadian) * (delta_x)
				+ Math.cos(-rotationRadian) * (delta_y);

		float resizeXMoveCenterX = (float) ((deltaXCorrected / 2) * Math
				.cos(rotationRadian));
		float resizeXMoveCenterY = (float) ((deltaXCorrected / 2) * Math
				.sin(rotationRadian));
		float resizeYMoveCenterX = (float) ((deltaYCorrected / 2) * Math
				.sin(rotationRadian));
		float resizeYMoveCenterY = (float) ((deltaYCorrected / 2) * Math
				.cos(rotationRadian));

		// Height
		switch (mResizeAction) {
		case TOP:
		case TOPRIGHT:
		case TOPLEFT:
			mBoxHeight -= deltaYCorrected;
			mToolPosition.x -= resizeYMoveCenterX;
			mToolPosition.y += resizeYMoveCenterY;
			if (mUseSquareShape) {
				mBoxWidth = mBoxHeight;
			}
			break;
		case BOTTOM:
		case BOTTOMLEFT:
		case BOTTOMRIGHT:
			mBoxHeight += deltaYCorrected;
			mToolPosition.x -= resizeYMoveCenterX;
			mToolPosition.y += resizeYMoveCenterY;
			if (mUseSquareShape) {
				mBoxWidth = mBoxHeight;
			}
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
			if (mUseSquareShape) {
				mBoxHeight = mBoxWidth;
			}
			break;
		case RIGHT:
		case TOPRIGHT:
		case BOTTOMRIGHT:
			mBoxWidth += deltaXCorrected;
			mToolPosition.x += resizeXMoveCenterX;
			mToolPosition.y += resizeXMoveCenterY;
			if (mUseSquareShape) {
				mBoxHeight = mBoxWidth;
			}
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

	protected abstract void createAndSetBitmap(DrawingSurface drawingSurface);
}
