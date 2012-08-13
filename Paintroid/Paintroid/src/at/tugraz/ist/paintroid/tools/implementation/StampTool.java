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
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.StampCommand;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

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
	protected Bitmap stampBitmap = null;
	protected Paint linePaint;
	protected PointF movedDistance = new PointF(0, 0);
	protected PointF previousEventCoordinate = null;
	DrawingSurface drawingSurface;
	protected final int toolStrokeWidth = 5;
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
		position.x = display.getWidth() / 2;
		position.y = display.getHeight() / 2;
		this.drawingSurface = drawingSurface;
		width = default_width;
		height = default_width;
		// position.x = surfaceSize.x / 2;
		// position.y = surfaceSize.y / 2;
		rotation = 0;
		if (stampBitmap != null) {
			stampBitmap.recycle();
			stampBitmap = null;
		}
	};

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
				this.position.x += delta.x;
				this.position.y += delta.y;
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
		canvas.translate(position.x, position.y);
		canvas.rotate(rotation);
		if (stampBitmap != null) {
			Paint bitmap_paint = new Paint(Paint.DITHER_FLAG);
			canvas.drawBitmap(stampBitmap, null, new RectF(-this.width / 2, -this.height / 2, this.width / 2,
					this.height / 2), bitmap_paint);
		}
		setPaint(linePaint, Cap.ROUND, toolStrokeWidth, primaryShapeColor, true, new DashPathEffect(new float[] { 20,
				10 }, 20));
		canvas.drawRect(-this.width / 2, this.height / 2, this.width / 2, -this.height / 2, linePaint);
		// Only draw rotation symbol if an image is present
		if (stampBitmap != null) {
			canvas.drawCircle(-this.width / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, -this.height
					/ 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2, this.roationSymbolWidth, linePaint);
		}
		setPaint(linePaint, Cap.ROUND, toolStrokeWidth, secondaryShapeColor, true, new DashPathEffect(new float[] { 10,
				20 }, 0));
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

	protected FloatingBoxAction getAction(float clickCoordinatesX, float clickCoordinatesY) {
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

	protected void clipBitmap(DrawingSurface drawingSurface) {
		Point left_top_box_bitmapcoordinates = new Point(this.position.x - this.width / 2, this.position.y
				- this.height / 2);
		Point right_bottom_box_bitmapcoordinates = new Point(this.position.x + this.width / 2, this.position.y
				+ this.height / 2);
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

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
			case BUTTON_ID_PARAMETER_TOP_1:
			case BUTTON_ID_PARAMETER_TOP_2:
				return Color.TRANSPARENT;
			default:
				return super.getAttributeButtonColor(buttonNumber);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		// no clicks wanted
	}
}
