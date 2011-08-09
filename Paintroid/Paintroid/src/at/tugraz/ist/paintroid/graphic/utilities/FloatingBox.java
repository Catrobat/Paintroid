/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.graphic.utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

public class FloatingBox extends Tool {

	protected int default_width = 200;
	protected int default_height = 200;
	protected int width;
	protected int height;
	protected float rotation = 0;
	protected float frameTolerance = 30;
	protected int roationSymbolDistance = 30;
	protected int roationSymbolWidth = 40;
	protected ResizeAction resizeAction;
	protected Bitmap floatingBoxBitmap = null;

	public enum FloatingBoxAction {
		NONE, MOVE, RESIZE, ROTATE;
	}

	protected enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}

	public FloatingBox(Tool tool) {
		super(tool);
		resizeAction = ResizeAction.NONE;
		reset();
	}

	@Override
	public boolean singleTapEvent(DrawingSurface drawingSurface) {
		if (state == ToolState.ACTIVE) {
			if (floatingBoxBitmap == null) {
				clipBitmap(drawingSurface);
			} else {
				stampBitmap(drawingSurface);
			}
		}
		return true;
	}

	protected void clipBitmap(DrawingSurface drawingSurface) {
		Point left_top_box_bitmapcoordinates = drawingSurface.getPixelCoordinates(this.position.x - this.width / 2,
				this.position.y - this.height / 2);
		Point right_bottom_box_bitmapcoordinates = drawingSurface.getPixelCoordinates(this.position.x + this.width / 2,
				this.position.y + this.height / 2);
		try {
			floatingBoxBitmap = Bitmap.createBitmap(drawingSurface.getBitmap(), left_top_box_bitmapcoordinates.x,
					left_top_box_bitmapcoordinates.y, right_bottom_box_bitmapcoordinates.x
							- left_top_box_bitmapcoordinates.x, right_bottom_box_bitmapcoordinates.y
							- left_top_box_bitmapcoordinates.y);
		} catch (IllegalArgumentException e) {
			floatingBoxBitmap = null;
		}
	}

	protected void stampBitmap(DrawingSurface drawingSurface) {
		Canvas drawingCanvas = new Canvas(drawingSurface.getBitmap());
		Paint bitmap_paint = new Paint(Paint.DITHER_FLAG);
		final float zoomX = DrawingSurface.Perspective.zoom;
		Point box_position_bitmapcoordinates = drawingSurface.getPixelCoordinates(this.position.x, this.position.y);
		PointF size_bitmapcoordinates = new PointF(((this.width) / zoomX), ((this.height) / zoomX));
		drawingCanvas.translate(box_position_bitmapcoordinates.x, box_position_bitmapcoordinates.y);
		drawingCanvas.rotate(rotation);
		drawingCanvas.drawBitmap(floatingBoxBitmap, null, new RectF(-size_bitmapcoordinates.x / 2,
				-size_bitmapcoordinates.y / 2, size_bitmapcoordinates.x / 2, size_bitmapcoordinates.y / 2),
				bitmap_paint);
		drawingSurface.addDrawingToUndoRedo();
	}

	public void addBitmap(Bitmap bitmap) {
		int bitmapHeight = bitmap.getHeight();
		int bitmapWidth = bitmap.getWidth();
		this.height = (int) ((float) this.width * (float) bitmapHeight / bitmapWidth);
		if (this.height >= surfaceSize.y - distanceFromScreenEdgeToScroll) {
			this.height = surfaceSize.y - distanceFromScreenEdgeToScroll - 1;
			this.width = (int) ((float) this.height * (float) bitmapWidth / bitmapHeight);
		}
		floatingBoxBitmap = bitmap;
	}

	@Override
	public void draw(Canvas view_canvas, Cap shape, int stroke_width, int color) {
		if (state == ToolState.ACTIVE) {
			view_canvas.save();
			view_canvas.translate(position.x, position.y);
			view_canvas.rotate(rotation);
			if (floatingBoxBitmap != null) {
				Paint bitmap_paint = new Paint(Paint.DITHER_FLAG);
				view_canvas.drawBitmap(floatingBoxBitmap, null, new RectF(-this.width / 2, -this.height / 2,
						this.width / 2, this.height / 2), bitmap_paint);
			}
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, primaryColor, true, new DashPathEffect(
					new float[] { 20, 10 }, 20));
			view_canvas.drawRect(-this.width / 2, this.height / 2, this.width / 2, -this.height / 2, linePaint);

			if (floatingBoxBitmap != null) {
				view_canvas.drawCircle(-this.width / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2,
						-this.height / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2,
						this.roationSymbolWidth, linePaint);
			}
			DrawFunctions.setPaint(linePaint, Cap.ROUND, toolStrokeWidth, secundaryColor, true, new DashPathEffect(
					new float[] { 10, 20 }, 0));
			view_canvas.drawRect(-this.width / 2, this.height / 2, this.width / 2, -this.height / 2, linePaint);

			if (floatingBoxBitmap != null) {
				view_canvas.drawCircle(-this.width / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2,
						-this.height / 2 - this.roationSymbolDistance - this.roationSymbolWidth / 2,
						this.roationSymbolWidth, linePaint);
			}
			view_canvas.restore();
		}
	}

	public void rotate(float delta_x, float delta_y) {
		double rotationRadiant = rotation * Math.PI / 180;
		double delta_x_corrected = Math.cos(-rotationRadiant) * (delta_x) - Math.sin(-rotationRadiant) * (delta_y);
		double delta_y_corrected = Math.sin(-rotationRadiant) * (delta_x) + Math.cos(-rotationRadiant) * (delta_y);

		rotation += (delta_x_corrected - delta_y_corrected) / (5);
	}

	public void resize(float delta_x, float delta_y) {
		double rotationRadian = rotation * Math.PI / 180;
		double delta_x_corrected = Math.cos(-rotationRadian) * (delta_x) - Math.sin(-rotationRadian) * (delta_y);
		double delta_y_corrected = Math.sin(-rotationRadian) * (delta_x) + Math.cos(-rotationRadian) * (delta_y);

		float resize_x_move_center_x = (float) ((delta_x_corrected / 2) * Math.cos(rotationRadian));
		float resize_x_move_center_y = (float) ((delta_x_corrected / 2) * Math.sin(rotationRadian));
		float resize_y_move_center_x = (float) ((delta_y_corrected / 2) * Math.sin(rotationRadian));
		float resize_y_move_center_y = (float) ((delta_y_corrected / 2) * Math.cos(rotationRadian));

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

		if (this.width < frameTolerance) {
			this.width = (int) frameTolerance;
		}
		if (this.height < frameTolerance) {
			this.height = (int) frameTolerance;
		}
	}

	public void reset() {
		this.width = default_width;
		this.height = default_width;
		this.position.x = this.surfaceSize.x / 2;
		this.position.y = this.surfaceSize.y / 2;
		this.rotation = 0;
		this.floatingBoxBitmap = null;
	}

	public FloatingBoxAction getAction(float clickCoordinatesX, float clickCoordinatesY) {
		resizeAction = ResizeAction.NONE;
		double rotationRadiant = rotation * Math.PI / 180;
		float clickCoordinatesRotatedX = (float) (this.position.x + Math.cos(-rotationRadiant)
				* (clickCoordinatesX - this.position.x) - Math.sin(-rotationRadiant)
				* (clickCoordinatesY - this.position.y));
		float clickCoordinatesRotatedY = (float) (this.position.y + Math.sin(-rotationRadiant)
				* (clickCoordinatesX - this.position.x) + Math.cos(-rotationRadiant)
				* (clickCoordinatesY - this.position.y));

		if (clickCoordinatesRotatedX < this.position.x + this.width / 2 - frameTolerance
				&& clickCoordinatesRotatedX > this.position.x - this.width / 2 + frameTolerance
				&& clickCoordinatesRotatedY < this.position.y + this.height / 2 - frameTolerance
				&& clickCoordinatesRotatedY > this.position.y - this.height / 2 + frameTolerance) {
			return FloatingBoxAction.MOVE;
		}

		if (floatingBoxBitmap != null) {
			if (clickCoordinatesRotatedX < this.position.x - this.width / 2 - roationSymbolDistance
					&& clickCoordinatesRotatedX > this.position.x - this.width / 2 - roationSymbolDistance
							- roationSymbolWidth
					&& clickCoordinatesRotatedY < this.position.y - this.height / 2 - roationSymbolDistance
					&& clickCoordinatesRotatedY > this.position.y - this.height / 2 - roationSymbolDistance
							- roationSymbolWidth) {
				return FloatingBoxAction.ROTATE;
			}
		}

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
