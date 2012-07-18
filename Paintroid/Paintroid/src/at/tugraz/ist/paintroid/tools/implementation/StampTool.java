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
import android.widget.Toast;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.StampCommand;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class StampTool extends BaseToolWithShape {

	private static final int DEFAULT_RECTANGLE_MARGIN = 150;
	private static final float DEFAULT_TOOL_STROKE_WIDTH = 5f;
	private static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	private static final int DEFAULT_ROTATION_SYMBOL_DISTANCE = 20;
	private static final int DEFAULT_ROTATION_SYMBOL_WIDTH = 30;
	private static final int DEFAULT_FRAME_TOLERANCE = 30;

	protected int default_width = 200;
	protected int default_height = 200;
	protected int width;
	protected int height;
	// Rotation of the box in degree
	protected float rotation = 0;
	// Tolerance that the resize action is performed if the frame is touched
	protected float frameTolerance;
	// Distance from box frame to rotation symbol
	protected int roationSymbolDistance;
	protected int roationSymbolWidth;
	protected ResizeAction resizeAction;
	protected Bitmap stampBitmap = null;
	protected Paint linePaint;
	protected PointF movedDistance = new PointF(0, 0);
	protected PointF previousEventCoordinate = null;
	DrawingSurface drawingSurface;
	protected float toolStrokeWidth;
	protected FloatingBoxAction currentAction = null;

	public static final PorterDuffXfermode transparencyXferMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	public enum FloatingBoxAction {
		NONE, MOVE, RESIZE, ROTATE;
	}

	protected enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}

	public StampTool(Context context, ToolType toolType, DrawingSurface drawingSurface) {
		super(context, toolType);
		linePaint = new Paint();
		this.linePaint.setDither(true);
		this.linePaint.setStyle(Paint.Style.STROKE);
		this.linePaint.setStrokeJoin(Paint.Join.ROUND);
		resizeAction = ResizeAction.NONE;

		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		this.drawingSurface = drawingSurface;

		// init width and height of the rectangle
		float scale = PaintroidApplication.CURRENT_PERSPECTIVE.getScale();
		width = (int) ((display.getWidth() - DEFAULT_RECTANGLE_MARGIN * 2) / scale);
		height = width;

		initScaleDependedValues();

		rotation = 0;
		if (stampBitmap != null) {
			stampBitmap.recycle();
			stampBitmap = null;
		}
	};

	private void initScaleDependedValues() {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		float scale = PaintroidApplication.CURRENT_PERSPECTIVE.getScale();
		float displayScale = context.getResources().getDisplayMetrics().density;

		// init Stroke Width

		toolStrokeWidth = ((DEFAULT_TOOL_STROKE_WIDTH * displayScale) / scale);
		if (toolStrokeWidth < MINIMAL_TOOL_STROKE_WIDTH) {
			toolStrokeWidth = MINIMAL_TOOL_STROKE_WIDTH;
		} else if (toolStrokeWidth > 2 * DEFAULT_TOOL_STROKE_WIDTH) {
			toolStrokeWidth = 2 * DEFAULT_TOOL_STROKE_WIDTH;
		}

		roationSymbolDistance = (int) ((DEFAULT_ROTATION_SYMBOL_DISTANCE * displayScale) / scale);
		roationSymbolWidth = (int) ((DEFAULT_ROTATION_SYMBOL_WIDTH * displayScale) / scale);
		frameTolerance = (int) ((DEFAULT_FRAME_TOLERANCE * displayScale) / scale);
	}

	public void addBitmap(Bitmap bitmapToAdd) {
		if (bitmapToAdd != null) {
			this.stampBitmap = bitmapToAdd;
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
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
		PointF delta = new PointF(coordinate.x - previousEventCoordinate.x, coordinate.y - previousEventCoordinate.y);
		movedDistance.set(movedDistance.x + Math.abs(delta.x), movedDistance.y + Math.abs(delta.y));
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		switch (currentAction) {
			case MOVE:
				this.toolPosition.x += delta.x;
				this.toolPosition.y += delta.y;
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
		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		if (PaintroidApplication.MOVE_TOLLERANCE >= movedDistance.x
				&& PaintroidApplication.MOVE_TOLLERANCE >= movedDistance.y) {
			if (stampBitmap == null) {
				clipBitmap(drawingSurface);
			} else {
				Command command = new StampCommand(stampBitmap, this.position, width, height, rotation);
				PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
			}
		}
		return true;
	}

	@Override
	public void resetInternalState() {

	}

	@Override
	public void drawShape(Canvas canvas) {
		initScaleDependedValues();
		canvas.translate(toolPosition.x, toolPosition.y);
		canvas.rotate(rotation);
		if (stampBitmap != null) {
			Paint bitmap_paint = new Paint(Paint.DITHER_FLAG);
			canvas.drawBitmap(stampBitmap, null, new RectF(-this.width / 2, -this.height / 2, this.width / 2,
					this.height / 2), bitmap_paint);
		}
		setPaint(linePaint, Cap.ROUND, (int) toolStrokeWidth, primaryShapeColor, true, new DashPathEffect(new float[] {
				20, 10 }, 20));
		canvas.drawRect(-this.width / 2, this.height / 2, this.width / 2, -this.height / 2, linePaint);
		// Only draw rotation symbol if an image is present
		if (stampBitmap != null) {
			canvas.drawCircle(-this.width / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, -this.height
					/ 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, this.roationSymbolWidth, linePaint);
		}
		setPaint(linePaint, Cap.ROUND, (int) toolStrokeWidth, secondaryShapeColor, true, new DashPathEffect(
				new float[] { 10, 20 }, 0));
		canvas.drawRect(-this.width / 2, this.height / 2, this.width / 2, -this.height / 2, linePaint);
		// Only draw rotation symbol if an image is present
		if (stampBitmap != null) {
			canvas.drawCircle(-this.width / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, -this.height
					/ 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, this.roationSymbolWidth, linePaint);
		}
		canvas.restore();

	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		this.drawShape(canvas);
	}

	protected void rotate(float delta_x, float delta_y) {
		if (stampBitmap == null) {
			return;
		}
		double rotationRadiant = rotation * Math.PI / 180;
		double delta_x_corrected = Math.cos(-rotationRadiant) * (delta_x) - Math.sin(-rotationRadiant) * (delta_y);
		double delta_y_corrected = Math.sin(-rotationRadiant) * (delta_x) + Math.cos(-rotationRadiant) * (delta_y);

		rotation += (delta_x_corrected - delta_y_corrected) / (5);
	}

	public boolean rotate(int degree) {
		if (stampBitmap == null) {
			return false;
		}
		rotation += degree;
		return true;
	}

	protected void resize(float delta_x, float delta_y) {
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
				this.height -= delta_y_corrected;
				this.toolPosition.x -= resize_y_move_center_x;
				this.toolPosition.y += resize_y_move_center_y;
				break;
			case BOTTOM:
			case BOTTOMLEFT:
			case BOTTOMRIGHT:
				this.height += delta_y_corrected;
				this.toolPosition.x -= resize_y_move_center_x;
				this.toolPosition.y += resize_y_move_center_y;
				break;
			default:
				break;
		}

		// Width
		switch (resizeAction) {
			case LEFT:
			case TOPLEFT:
			case BOTTOMLEFT:
				this.width -= delta_x_corrected;
				this.toolPosition.x += resize_x_move_center_x;
				this.toolPosition.y += resize_x_move_center_y;
				break;
			case RIGHT:
			case TOPRIGHT:
			case BOTTOMRIGHT:
				this.width += delta_x_corrected;
				this.toolPosition.x += resize_x_move_center_x;
				this.toolPosition.y += resize_x_move_center_y;
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

	protected FloatingBoxAction getAction(float clickCoordinatesX, float clickCoordinatesY) {
		resizeAction = ResizeAction.NONE;
		double rotationRadiant = rotation * Math.PI / 180;
		float clickCoordinatesRotatedX = (float) (this.toolPosition.x + Math.cos(-rotationRadiant)
				* (clickCoordinatesX - this.toolPosition.x) - Math.sin(-rotationRadiant)
				* (clickCoordinatesY - this.toolPosition.y));
		float clickCoordinatesRotatedY = (float) (this.toolPosition.y + Math.sin(-rotationRadiant)
				* (clickCoordinatesX - this.toolPosition.x) + Math.cos(-rotationRadiant)
				* (clickCoordinatesY - this.toolPosition.y));

		// Move (within box)
		if (clickCoordinatesRotatedX < this.toolPosition.x + this.width / 2 - frameTolerance
				&& clickCoordinatesRotatedX > this.toolPosition.x - this.width / 2 + frameTolerance
				&& clickCoordinatesRotatedY < this.toolPosition.y + this.height / 2 - frameTolerance
				&& clickCoordinatesRotatedY > this.toolPosition.y - this.height / 2 + frameTolerance) {
			return FloatingBoxAction.MOVE;
		}

		// Only allow rotation if an image is present
		if (stampBitmap != null) {
			// Rotate (on symbol)
			if (clickCoordinatesRotatedX < this.toolPosition.x - this.width / 2 - roationSymbolDistance
					&& clickCoordinatesRotatedX > this.toolPosition.x - this.width / 2 - roationSymbolDistance
							- roationSymbolWidth
					&& clickCoordinatesRotatedY < this.toolPosition.y - this.height / 2 - roationSymbolDistance
					&& clickCoordinatesRotatedY > this.toolPosition.y - this.height / 2 - roationSymbolDistance
							- roationSymbolWidth) {
				return FloatingBoxAction.ROTATE;
			}
		}

		// Resize (on frame)
		if (clickCoordinatesRotatedX < this.toolPosition.x + this.width / 2 + frameTolerance
				&& clickCoordinatesRotatedX > this.toolPosition.x - this.width / 2 - frameTolerance
				&& clickCoordinatesRotatedY < this.toolPosition.y + this.height / 2 + frameTolerance
				&& clickCoordinatesRotatedY > this.toolPosition.y - this.height / 2 - frameTolerance) {
			if (clickCoordinatesRotatedX < this.toolPosition.x - this.width / 2 + frameTolerance) {
				resizeAction = ResizeAction.LEFT;
			} else if (clickCoordinatesRotatedX > this.toolPosition.x + this.width / 2 - frameTolerance) {
				resizeAction = ResizeAction.RIGHT;
			}
			if (clickCoordinatesRotatedY < this.toolPosition.y - this.height / 2 + frameTolerance) {
				if (resizeAction == ResizeAction.LEFT) {
					resizeAction = ResizeAction.TOPLEFT;
				} else if (resizeAction == ResizeAction.RIGHT) {
					resizeAction = ResizeAction.TOPRIGHT;
				} else {
					resizeAction = ResizeAction.TOP;
				}
			} else if (clickCoordinatesRotatedY > this.toolPosition.y + this.height / 2 - frameTolerance) {
				if (resizeAction == ResizeAction.LEFT) {
					resizeAction = ResizeAction.BOTTOMLEFT;
				} else if (resizeAction == ResizeAction.RIGHT) {
					resizeAction = ResizeAction.BOTTOMRIGHT;
				} else {
					resizeAction = ResizeAction.BOTTOM;
				}
			}
			Toast.makeText(context, "resize", Toast.LENGTH_LONG);
			return FloatingBoxAction.RESIZE;
		}

		// No valid click
		return FloatingBoxAction.NONE;
	}

	protected void clipBitmap(DrawingSurface drawingSurface) {
		Point left_top_box_bitmapcoordinates = new Point((int) this.toolPosition.x - this.width / 2,
				(int) this.toolPosition.y - this.height / 2);
		Point right_bottom_box_bitmapcoordinates = new Point((int) this.toolPosition.x + this.width / 2,
				(int) this.toolPosition.y + this.height / 2);
		try {
			stampBitmap = Bitmap.createBitmap(drawingSurface.getBitmap(), left_top_box_bitmapcoordinates.x,
					left_top_box_bitmapcoordinates.y, right_bottom_box_bitmapcoordinates.x
							- left_top_box_bitmapcoordinates.x, right_bottom_box_bitmapcoordinates.y
							- left_top_box_bitmapcoordinates.y);
		} catch (IllegalArgumentException e) {
			// floatingBox is outside of image
			if (stampBitmap != null) {
				stampBitmap.recycle();
				stampBitmap = null;
			}
		}
	}

	protected void setPaint(Paint paint, final Cap currentBrushType, final int currentStrokeWidth,
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

}
