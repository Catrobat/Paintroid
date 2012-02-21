package at.tugraz.ist.paintroid.tools.implementation;

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
import at.tugraz.ist.paintroid.MainActivity.ToolType;

public class StampTool extends BaseToolWithShape {

	protected int default_width = 200;
	protected int default_height = 200;
	protected int width;
	protected int height;
	// Rotation of the box in degree
	protected float rotation = 0;
	// Tolerance that the resize action is performed if the frame is touched
	protected float frameTolerance = 30;
	// Distance from box frame to rotation symbol
	protected int roationSymbolDistance = 30;
	protected int roationSymbolWidth = 40;
	protected ResizeAction resizeAction;
	protected Bitmap stampBitmap;
	protected Paint linePaint;

	public static final PorterDuffXfermode transparencyXferMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	public enum FloatingBoxAction {
		NONE, MOVE, RESIZE, ROTATE;
	}

	protected enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}

	public StampTool(Context context, ToolType toolType) {
		super(context, toolType);
		linePaint = new Paint();
		linePaint.setStrokeWidth(Math.max((drawPaint.getStrokeWidth() / 2f), 1f));
		resizeAction = ResizeAction.NONE;
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		position.x = display.getWidth() / 2;
		position.y = display.getHeight() / 2;

		this.resetInternalState();

	};

	@Override
	public boolean handleDown(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetInternalState() {
		width = default_width;
		height = default_width;
		// position.x = surfaceSize.x / 2;
		// position.y = surfaceSize.y / 2;
		rotation = 0;
		if (stampBitmap != null) {
			stampBitmap.recycle();
			stampBitmap = null;
		}

	}

	@Override
	public void drawShape(Canvas canvas) {
		float strokeWidth = Math.max((drawPaint.getStrokeWidth() / 2f), 1f);
		canvas.translate(position.x, position.y);
		canvas.rotate(rotation);
		if (stampBitmap != null) {
			Paint bitmap_paint = new Paint(Paint.DITHER_FLAG);
			canvas.drawBitmap(stampBitmap, null, new RectF(-this.width / 2, -this.height / 2, this.width / 2,
					this.height / 2), bitmap_paint);
		}
		this.setPaint(linePaint, Cap.ROUND, strokeWidth, primaryShapeColor, true, new DashPathEffect(new float[] { 20,
				10 }, 20));
		canvas.drawRect(-this.width / 2, this.height / 2, this.width / 2, -this.height / 2, linePaint);
		// Only draw rotation symbol if an image is present
		if (stampBitmap != null) {
			canvas.drawCircle(-this.width / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, -this.height
					/ 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, this.roationSymbolWidth, linePaint);
		}
		this.setPaint(linePaint, Cap.ROUND, strokeWidth, secondaryShapeColor, true, new DashPathEffect(new float[] {
				10, 20 }, 0));
		canvas.drawRect(-this.width / 2, this.height / 2, this.width / 2, -this.height / 2, linePaint);
		// Only draw rotation symbol if an image is present
		if (stampBitmap != null) {
			canvas.drawCircle(-this.width / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, -this.height
					/ 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, this.roationSymbolWidth, linePaint);
		}
		// canvas.restore();

	}

	private void setPaint(Paint paint, final Cap currentBrushType, final float currentStrokeWidth,
			final int currentStrokeColor, boolean antialiasingFlag, PathEffect effect) {
		if (currentStrokeWidth == 1) {
			paint.setAntiAlias(false);
			paint.setStrokeCap(Cap.SQUARE);
		} else {
			paint.setAntiAlias(antialiasingFlag);
			paint.setStrokeCap(currentBrushType);
		}
		paint.setPathEffect(effect);
		paint.setStrokeWidth(currentStrokeWidth);
		paint.setColor(currentStrokeColor);
		if (currentStrokeColor == Color.TRANSPARENT) {
			paint.setXfermode(transparencyXferMode);
		} else {
			paint.setXfermode(null);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		this.drawShape(canvas);

	}

	/**
	 * Rotates the box
	 * 
	 * @param delta_x move in direction x
	 * @param delta_y move in direction y
	 */
	public void rotate(float delta_x, float delta_y) {
		if (stampBitmap == null) {
			return;
		}
		double rotationRadiant = rotation * Math.PI / 180;
		double delta_x_corrected = Math.cos(-rotationRadiant) * (delta_x) - Math.sin(-rotationRadiant) * (delta_y);
		double delta_y_corrected = Math.sin(-rotationRadiant) * (delta_x) + Math.cos(-rotationRadiant) * (delta_y);

		rotation += (delta_x_corrected - delta_y_corrected) / (5);
	}

	/**
	 * Rotates the box in degree
	 * 
	 * @param degree
	 * @return true if it worked, else false
	 */
	public boolean rotate(int degree) {
		if (stampBitmap == null) {
			return false;
		}
		rotation += degree;
		return true;
	}

	/**
	 * Resizes the box
	 * 
	 * @param delta_x resize width
	 * @param delta_y resize height
	 */
	public void resize(float delta_x, float delta_y) {
		double rotationRadian = rotation * Math.PI / 180;
		double delta_x_corrected = Math.cos(-rotationRadian) * (delta_x) - Math.sin(-rotationRadian) * (delta_y);
		double delta_y_corrected = Math.sin(-rotationRadian) * (delta_x) + Math.cos(-rotationRadian) * (delta_y);

		float resize_x_move_center_x = (float) ((delta_x_corrected / 2) * Math.cos(rotationRadian));
		float resize_x_move_center_y = (float) ((delta_x_corrected / 2) * Math.sin(rotationRadian));
		float resize_y_move_center_x = (float) ((delta_y_corrected / 2) * Math.sin(rotationRadian));
		float resize_y_move_center_y = (float) ((delta_y_corrected / 2) * Math.cos(rotationRadian));

		// Height
		switch (resizeAction) {
			case TOP:
			case TOPRIGHT:
			case TOPLEFT:
				this.height -= (int) delta_y_corrected;
				this.position.x -= (int) resize_y_move_center_x;
				this.position.y += (int) resize_y_move_center_y;
				break;
			case BOTTOM:
			case BOTTOMLEFT:
			case BOTTOMRIGHT:
				this.height += (int) delta_y_corrected;
				this.position.x -= (int) resize_y_move_center_x;
				this.position.y += (int) resize_y_move_center_y;
				break;
			default:
				break;
		}

		// Width
		switch (resizeAction) {
			case LEFT:
			case TOPLEFT:
			case BOTTOMLEFT:
				this.width -= (int) delta_x_corrected;
				this.position.x += (int) resize_x_move_center_x;
				this.position.y += (int) resize_x_move_center_y;
				break;
			case RIGHT:
			case TOPRIGHT:
			case BOTTOMRIGHT:
				this.width += (int) delta_x_corrected;
				this.position.x += (int) resize_x_move_center_x;
				this.position.y += (int) resize_x_move_center_y;
				break;
			default:
				break;
		}

		// prevent that box gets too small
		if (this.width < frameTolerance) {
			this.width = (int) frameTolerance;
		}
		if (this.height < frameTolerance) {
			this.height = (int) frameTolerance;
		}
	}

	/**
	 * Gets the action the user has selected through clicking on a specific position of the floating box
	 * 
	 * @param clickCoordinates coordinates the user has touched
	 * @return action to perform
	 */
	public FloatingBoxAction getAction(float clickCoordinatesX, float clickCoordinatesY) {
		resizeAction = ResizeAction.NONE;
		double rotationRadiant = rotation * Math.PI / 180;
		float clickCoordinatesRotatedX = (float) (this.position.x + Math.cos(-rotationRadiant)
				* (clickCoordinatesX - this.position.x) - Math.sin(-rotationRadiant)
				* (clickCoordinatesY - this.position.y));
		float clickCoordinatesRotatedY = (float) (this.position.y + Math.sin(-rotationRadiant)
				* (clickCoordinatesX - this.position.x) + Math.cos(-rotationRadiant)
				* (clickCoordinatesY - this.position.y));

		// Move (within box)
		if (clickCoordinatesRotatedX < this.position.x + this.width / 2 - frameTolerance
				&& clickCoordinatesRotatedX > this.position.x - this.width / 2 + frameTolerance
				&& clickCoordinatesRotatedY < this.position.y + this.height / 2 - frameTolerance
				&& clickCoordinatesRotatedY > this.position.y - this.height / 2 + frameTolerance) {
			return FloatingBoxAction.MOVE;
		}

		// Only allow rotation if an image is present
		if (stampBitmap != null) {
			// Rotate (on symbol)
			if (clickCoordinatesRotatedX < this.position.x - this.width / 2 - roationSymbolDistance
					&& clickCoordinatesRotatedX > this.position.x - this.width / 2 - roationSymbolDistance
							- roationSymbolWidth
					&& clickCoordinatesRotatedY < this.position.y - this.height / 2 - roationSymbolDistance
					&& clickCoordinatesRotatedY > this.position.y - this.height / 2 - roationSymbolDistance
							- roationSymbolWidth) {
				return FloatingBoxAction.ROTATE;
			}
		}

		// Resize (on frame)
		if (clickCoordinatesRotatedX < this.position.x + this.width / 2 + frameTolerance
				&& clickCoordinatesRotatedX > this.position.x - this.width / 2 - frameTolerance
				&& clickCoordinatesRotatedY < this.position.y + this.height / 2 + frameTolerance
				&& clickCoordinatesRotatedY > this.position.y - this.height / 2 - frameTolerance) {
			if (clickCoordinatesRotatedX < this.position.x - this.width / 2 + frameTolerance) {
				resizeAction = ResizeAction.LEFT;
			} else if (clickCoordinatesRotatedX > this.position.x + this.width / 2 - frameTolerance) {
				resizeAction = ResizeAction.RIGHT;
			}
			if (clickCoordinatesRotatedY < this.position.y - this.height / 2 + frameTolerance) {
				if (resizeAction == ResizeAction.LEFT) {
					resizeAction = ResizeAction.TOPLEFT;
				} else if (resizeAction == ResizeAction.RIGHT) {
					resizeAction = ResizeAction.TOPRIGHT;
				} else {
					resizeAction = ResizeAction.TOP;
				}
			} else if (clickCoordinatesRotatedY > this.position.y + this.height / 2 - frameTolerance) {
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

		// No valid click
		return FloatingBoxAction.NONE;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getRotation() {
		return rotation;
	}

}
