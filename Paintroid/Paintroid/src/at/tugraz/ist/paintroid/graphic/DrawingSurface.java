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

package at.tugraz.ist.paintroid.graphic;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.paintroid.MainActivity.ToolbarItem;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.listeners.BaseSurfaceListener;
import at.tugraz.ist.paintroid.graphic.listeners.DrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.listeners.FloatingBoxDrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.listeners.ToolDrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.utilities.Brush;
import at.tugraz.ist.paintroid.graphic.utilities.Cursor;
import at.tugraz.ist.paintroid.graphic.utilities.DrawFunctions;
import at.tugraz.ist.paintroid.graphic.utilities.FloatingBox;
import at.tugraz.ist.paintroid.graphic.utilities.Middlepoint;
import at.tugraz.ist.paintroid.graphic.utilities.Tool;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;
import at.tugraz.ist.paintroid.graphic.utilities.UndoRedo;
import at.tugraz.ist.zoomscroll.ZoomStatus;

public class DrawingSurface extends SurfaceView implements Observer, SurfaceHolder.Callback {
	public static final int STDWIDTH = 320;
	public static final int STDHEIGHT = 480;
	public static final int STDCOLOR = Color.BLACK;

	private volatile Bitmap workingBitmap;
	private static Canvas workingCanvas = new Canvas();
	private static Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);

	public enum Mode {
		DRAW, CURSOR, CENTERPOINT, FLOATINGBOX
	}

	private Mode activeMode;
	private ToolbarItem activeAction = ToolbarItem.HAND;
	private ZoomStatus zoomStatus;

	private Rect rectImage;
	private Rect rectCanvas;

	private float aspectRatio;
	private int activeColor;
	//	private int activeStroke;
	//	private Cap activeShape = Cap.ROUND;
	private Brush activeBrush;
	private boolean useAntiAliasing = true;
	private Path pathToDraw;
	private Paint pathPaint;
	private UndoRedo undoRedoObject;
	private Tool activeTool;
	private Point surfaceSize;
	private Point surfaceCenter;
	//	private Point drawingSurfaceCenter;
	private BaseSurfaceListener drawingSurfaceListener;

	private BitmapDrawable checkeredBackground;

	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);

		activeMode = Mode.DRAW;
		drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
		drawingSurfaceListener.setSurface(this);
		setOnTouchListener(this.drawingSurfaceListener);

		undoRedoObject = new UndoRedo(context);

		rectImage = new Rect();
		rectCanvas = new Rect();

		activeTool = new Cursor();
		surfaceSize = new Point(0, 0);
		surfaceCenter = new Point(0, 0);
		//		drawingSurfaceCenter = new Point(0, 0);

		activeBrush = new Brush();

		pathToDraw = new Path();
		pathToDraw.reset();

		pathPaint = new Paint();
		pathPaint.setDither(true);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setStrokeJoin(Paint.Join.ROUND);
		DrawFunctions.setPaint(pathPaint, activeBrush.cap, activeBrush.stroke, activeColor, useAntiAliasing, null);

		final Resources rsc = context.getResources();
		checkeredBackground = new BitmapDrawable(rsc, BitmapFactory.decodeResource(rsc, R.drawable.transparent));
		checkeredBackground.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);

		newEmptyBitmap();

		setZoomStatus(new ZoomStatus());
		zoomStatus.resetZoomState();

		setActiveColor(STDCOLOR);
		setBackgroundColor(Color.rgb(190, 190, 190));
	}

	public void setActionType(ToolbarItem type) {
		if (drawingSurfaceListener.getClass() != DrawingSurfaceListener.class) {
			if (activeTool instanceof Middlepoint) {
				changeCenterpointMode();
			} else if (activeTool instanceof FloatingBox) {
				toggleFloatingBoxMode();
			} else {
				activeTool.deactivate();
				activeTool = new Cursor(activeTool);
				drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
				drawingSurfaceListener.setSurface(this);
				drawingSurfaceListener.setZoomStatus(zoomStatus);
				setOnTouchListener(drawingSurfaceListener);
			}
			invalidate();
		}
		if (type != ToolbarItem.NONE) {
			drawingSurfaceListener.setControlType(type);
		}
		activeAction = type;
	}

	public void newEmptyBitmap() {
		clearUndoRedo();
		Bitmap bitmap = Bitmap.createBitmap(STDWIDTH, STDHEIGHT, Bitmap.Config.ARGB_8888);
		workingCanvas.setBitmap(bitmap);
		workingCanvas.drawColor(Color.WHITE);
		setBitmap(bitmap);
	}

	public void setBitmap(Bitmap bitmap) {
		workingBitmap = bitmap;
		if (workingBitmap != null) {
			workingCanvas.setBitmap(workingBitmap);
			undoRedoObject.addDrawing(workingBitmap);
		}
		calculateAspectRatio();
		invalidate();
	}

	public Bitmap getBitmap() {
		if (workingBitmap == null) {
			Log.w("PAINTROID", "drawPointOnSurface: Bitmap not set");
		}
		return workingBitmap;
	}

	public void setActiveColor(int color) {
		activeColor = color;
		paintChanged();
		invalidate();
	}

	public int getActiveColor() {
		return activeColor;
	}

	public void setActiveBrush(Cap cap) {
		setActiveBrush(cap, activeBrush.stroke);
	}

	public void setActiveBrush(int stroke) {
		setActiveBrush(activeBrush.cap, stroke);
	}

	public void setActiveBrush(Cap cap, int stroke) {
		activeBrush.cap = cap;
		activeBrush.stroke = stroke;
		paintChanged();
		invalidate();
	}

	public Brush getActiveBrush() {
		return activeBrush;
	}

	public void setAntiAliasing(boolean aa) {
		useAntiAliasing = aa;
	}

	private void calculateAspectRatio() {
		if (workingBitmap != null) {
			float width = workingBitmap.getWidth();
			float height = workingBitmap.getHeight();
			aspectRatio = (width / height) / (((float) getWidth()) / (float) getHeight());
		}
	}

	public void setZoomStatus(ZoomStatus status) {
		// Delete observer when already used
		if (zoomStatus != null) {
			zoomStatus.deleteObserver(this);
		}
		zoomStatus = status;
		drawingSurfaceListener.setZoomStatus(zoomStatus);
		zoomStatus.addObserver(this);
		invalidate();
	}

	public ZoomStatus getZoomStatus() {
		return zoomStatus;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calculateAspectRatio();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		invalidate();
	}

	public void getPixelColor(float x, float y) {
		Vector<Integer> bitmapCoords = DrawFunctions.screenToImageCoordinates(x, y, rectImage, rectCanvas);
		if (!coordinatesWithinBitmap(bitmapCoords.elementAt(0), bitmapCoords.elementAt(1))) {
			return;
		}
		if (workingBitmap != null && zoomStatus != null) {
			try {
				int color = workingBitmap.getPixel(bitmapCoords.elementAt(0), bitmapCoords.elementAt(1));
				colorListener.colorChanged(color);
			} catch (Exception e) {
			}
		}
		invalidate();
	}

	public void startPath(float x, float y) {
		Vector<Integer> bitmapCoords = DrawFunctions.screenToImageCoordinates(x, y, rectImage, rectCanvas);
		int imageX = bitmapCoords.elementAt(0).intValue();
		int imageY = bitmapCoords.elementAt(1).intValue();
		pathToDraw.reset();
		pathToDraw.moveTo(imageX, imageY);

		workingCanvas.drawPath(pathToDraw, pathPaint);
		invalidate();
	}

	public void updatePath(float x, float y, float prev_x, float prev_y) {
		Vector<Integer> bitmapCoords = DrawFunctions.screenToImageCoordinates(x, y, rectImage, rectCanvas);
		float imageX = bitmapCoords.get(0).intValue();
		float imageY = bitmapCoords.get(1).intValue();
		bitmapCoords = DrawFunctions.screenToImageCoordinates(prev_x, prev_y, rectImage, rectCanvas);
		float prevImageX = bitmapCoords.get(0).intValue();
		float prevImageY = bitmapCoords.get(1).intValue();
		pathToDraw.quadTo(prevImageX, prevImageY, (imageX + prevImageX) / 2, (imageY + prevImageY) / 2);

		workingCanvas.drawPath(pathToDraw, pathPaint);
	}

	public void drawPathOnSurface(float x, float y) {
		if (workingBitmap == null) {
			return;
		}
		Vector<Integer> bitmapCoords = DrawFunctions.screenToImageCoordinates(x, y, rectImage, rectCanvas);
		int imageX = bitmapCoords.get(0).intValue();
		int imageY = bitmapCoords.get(1).intValue();
		pathToDraw.lineTo(imageX, imageY);

		workingCanvas.drawPath(pathToDraw, pathPaint);
		invalidate();

		RectF pathBoundary = new RectF();
		pathToDraw.computeBounds(pathBoundary, true);
		RectF bitmapBoundary = new RectF(0, 0, workingBitmap.getWidth(), workingBitmap.getHeight());
		if (pathBoundary.intersect(bitmapBoundary)) {
			undoRedoObject.addPath(pathToDraw, pathPaint);
		}
	}

	public void drawPointOnSurface(float x, float y) {
		if (workingCanvas == null) {
			Log.w("PAINTROID", "drawPointOnSurface: Bitmap not set");
			return;
		}
		Vector<Integer> bitmapCoords = DrawFunctions.screenToImageCoordinates(x, y, rectImage, rectCanvas);
		int imageX = bitmapCoords.elementAt(0).intValue();
		int imageY = bitmapCoords.elementAt(1).intValue();

		DrawFunctions.setPaint(pathPaint, activeBrush.cap, activeBrush.stroke, activeColor, useAntiAliasing, null);
		if (coordinatesWithinBitmap(imageX, imageY)) {
			workingCanvas.drawPoint(imageX, imageY, pathPaint);
			undoRedoObject.addPoint(imageX, imageY, pathPaint);
		}
		pathToDraw.reset();
		invalidate();
	}

	public void replaceColorOnSurface(float x, float y) {
		Vector<Integer> bitmapCoords = DrawFunctions.screenToImageCoordinates(x, y, rectImage, rectCanvas);
		float imageX = bitmapCoords.elementAt(0);
		float imageY = bitmapCoords.elementAt(1);

		if (coordinatesWithinBitmap((int) imageX, (int) imageY)) {
			int chosen_pixel_color = workingBitmap.getPixel((int) imageX, (int) imageY);

			Paint replaceColorPaint = new Paint();
			replaceColorPaint.setColor(activeColor);
			replaceColorPaint.setXfermode(new AvoidXfermode(chosen_pixel_color, 250, AvoidXfermode.Mode.TARGET));

			Canvas replaceColorCanvas = new Canvas();
			replaceColorCanvas.setBitmap(workingBitmap);
			replaceColorCanvas.drawPaint(replaceColorPaint);

			undoRedoObject.addDrawing(workingBitmap);
			invalidate();
		}
		setActionType(ToolbarItem.NONE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (workingBitmap == null || zoomStatus == null) {
			return;
		}

		int bitmapWidth = workingBitmap.getWidth();
		int bitmapHeight = workingBitmap.getHeight();
		int viewWidth = this.getWidth();
		int viewHeight = this.getHeight();

		float scrollX = zoomStatus.getScrollX();
		float scrollY = zoomStatus.getScrollY();

		final float zoomX = getZoomX();
		final float zoomY = getZoomY();

		rectImage.left = (int) (scrollX * bitmapWidth - viewWidth / (zoomX * 2));
		rectImage.top = (int) (scrollY * bitmapHeight - viewHeight / (zoomY * 2));
		rectImage.right = (int) (rectImage.left + viewWidth / zoomX);
		rectImage.bottom = (int) (rectImage.top + viewHeight / zoomY);
		rectCanvas.left = getLeft();
		rectCanvas.top = getTop();
		rectCanvas.right = getRight();
		rectCanvas.bottom = getBottom();

		if (rectImage.left < 0) {
			rectCanvas.left += -rectImage.left * zoomX;
			rectImage.left = 0;
		}
		if (rectImage.right > bitmapWidth) {
			rectCanvas.right -= (rectImage.right - bitmapWidth) * zoomX;
			rectImage.right = bitmapWidth;
		}
		if (rectImage.top < 0) {
			rectCanvas.top += -rectImage.top * zoomY;
			rectImage.top = 0;
		}
		if (rectImage.bottom > bitmapHeight) {
			rectCanvas.bottom -= (rectImage.bottom - bitmapHeight) * zoomY;
			rectImage.bottom = bitmapHeight;
		}

		// make a ckeckerboard pattern background
		checkeredBackground.setBounds(rectCanvas);
		checkeredBackground.draw(canvas);

		canvas.drawBitmap(workingBitmap, rectImage, rectCanvas, bitmapPaint);

		activeTool.draw(canvas, activeBrush.cap, activeBrush.stroke, activeColor);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		surfaceSize.x = width;
		surfaceSize.y = height;
		surfaceCenter.x = width / 2;
		surfaceCenter.y = height / 2;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	public interface ColorPickupListener {
		void colorChanged(int color);
	}

	private ColorPickupListener colorListener;

	public void setColorPickupListener(ColorPickupListener listener) {
		colorListener = listener;
	}

	public void undoOneStep() {
		Bitmap undoBitmap = undoRedoObject.undo();
		if (undoBitmap != null) {
			workingBitmap = undoBitmap;
			workingCanvas.setBitmap(workingBitmap);
			calculateAspectRatio();
			invalidate();
		}
	}

	public void redoOneStep() {
		Bitmap redoBitmap = undoRedoObject.redo();
		if (redoBitmap != null) {
			workingBitmap = redoBitmap;
			workingCanvas.setBitmap(workingBitmap);
			calculateAspectRatio();
			invalidate();
		}
	}

	/**
	 * clear undo and redo stack
	 */
	public void clearUndoRedo() {
		undoRedoObject.clear();
	}

	/**
	 * 
	 */
	public void addDrawingToUndoRedo() {
		undoRedoObject.addDrawing(workingBitmap);
	}

	/**
	 * delegates action when single tap event occurred
	 * 
	 * @return true if the event is consumed, else false
	 */
	public boolean singleTapEvent() {
		boolean eventUsed = activeTool.singleTapEvent(this);
		if (eventUsed) {
			paintChanged();
			invalidate();
		}
		if (activeTool.getState() == ToolState.INACTIVE && activeAction != ToolbarItem.BRUSH) {
			return true;
		}
		return eventUsed;
	}

	/**
	 * sets the surface listener in order of state when double tap event occurred
	 * 
	 * @param x
	 *            the x-coordinate of the tap
	 * @param y
	 *            the y-coordinate of the tap
	 * @return true if the event is consumed, else false
	 */
	public boolean doubleTapEvent(float x, float y) {
		boolean eventUsed = activeTool.doubleTapEvent((int) x, (int) y, getZoomX(), getZoomY());
		if (eventUsed) {
			switch (activeTool.getState()) {
				case INACTIVE:
					activeMode = Mode.DRAW;
					drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
					break;
				case ACTIVE:
					activeMode = Mode.CURSOR;
					drawingSurfaceListener = new ToolDrawingSurfaceListener(this.getContext(), activeTool);
			}
			drawingSurfaceListener.setSurface(this);
			drawingSurfaceListener.setZoomStatus(zoomStatus);
			drawingSurfaceListener.setControlType(activeAction);
			setOnTouchListener(drawingSurfaceListener);
			invalidate();
		}
		return eventUsed;
	}

	public void paintChanged() {
		DrawFunctions.setPaint(pathPaint, activeBrush.cap, activeBrush.stroke, activeColor, useAntiAliasing, null);
		if (activeTool.getState() == ToolState.DRAW) {
			Point cursorPosition = activeTool.getPosition();
			drawPointOnSurface(cursorPosition.x, cursorPosition.y);
		}
	}

	public BaseSurfaceListener getDrawingSurfaceListener() {
		return drawingSurfaceListener;
	}

	public void setScreenSize(Point screenSize) {
		activeTool.setScreenSize(screenSize);
	}

	private boolean coordinatesWithinBitmap(int imageX, int imageY) {
		if (workingBitmap == null) {
			return false;
		} else {
			return imageX >= 0 && imageY >= 0 && imageX < workingBitmap.getWidth()
					&& imageY < workingBitmap.getHeight();
		}
	}

	public void changeCenterpointMode() {
		switch (activeMode) {
			case CENTERPOINT:
				activeTool.deactivate();
				activeTool = new Cursor(activeTool);
				drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
				activeMode = Mode.DRAW;
				break;
			default:
				activeTool = new Middlepoint(activeTool);
				drawingSurfaceListener = new ToolDrawingSurfaceListener(this.getContext(), activeTool);
				activeTool.activate(surfaceCenter);
				activeMode = Mode.CENTERPOINT;
				break;
		}
		drawingSurfaceListener.setSurface(this);
		drawingSurfaceListener.setZoomStatus(zoomStatus);
		drawingSurfaceListener.setControlType(activeAction);
		setOnTouchListener(drawingSurfaceListener);
		invalidate();
	}

	public void toggleFloatingBoxMode() {
		if (activeMode == Mode.FLOATINGBOX) {
			activeTool.deactivate();
			activeTool = new Cursor(activeTool);
			drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
			activeMode = Mode.DRAW;
		} else {
			FloatingBox floatingBox = new FloatingBox(activeTool);
			activeTool = floatingBox;
			drawingSurfaceListener = new FloatingBoxDrawingSurfaceListener(this.getContext(), floatingBox);
			activeTool.activate();
			activeMode = Mode.FLOATINGBOX;
		}
		drawingSurfaceListener.setSurface(this);
		drawingSurfaceListener.setZoomStatus(zoomStatus);
		drawingSurfaceListener.setControlType(activeAction);
		setOnTouchListener(drawingSurfaceListener);
		postInvalidate(); // called by robotium too
	}

	//	public void setCenter(int x, int y) {
	//		this.drawingSurfaceCenter.x = x;
	//		this.drawingSurfaceCenter.y = y;
	//	}

	public Point getCenter() {
		return surfaceCenter;
	}

	public void addPng(Bitmap newPng) {
		if (activeMode != Mode.FLOATINGBOX) {
			toggleFloatingBoxMode();
		}
		FloatingBox floatingBox = (FloatingBox) activeTool;
		floatingBox.reset();
		floatingBox.addBitmap(newPng);
		postInvalidate(); //called by robotium too
	}

	public Point getPixelCoordinates(float x, float y) {
		Vector<Integer> bitmapCoords = DrawFunctions.screenToImageCoordinates(x, y, rectImage, rectCanvas);
		int imageX = bitmapCoords.elementAt(0).intValue();
		int imageY = bitmapCoords.elementAt(1).intValue();
		imageX = imageX < 0 ? 0 : imageX;
		imageY = imageY < 0 ? 0 : imageY;
		imageX = imageX >= workingBitmap.getWidth() ? workingBitmap.getWidth() - 1 : imageX;
		imageY = imageY >= workingBitmap.getHeight() ? workingBitmap.getHeight() - 1 : imageY;
		return new Point(imageX, imageY);
	}

	public float getZoomX() {
		if (workingBitmap != null) {
			return zoomStatus.getZoomInX(aspectRatio) * getWidth() / workingBitmap.getWidth();
		} else {
			return 0;
		}
	}

	public float getZoomY() {
		return zoomStatus.getZoomInY(aspectRatio) * getHeight() / workingBitmap.getHeight();
	}

	//------------------------------methods for testing---------------------------------------

	public int getPixelFromScreenCoordinates(float x, float y) {
		Point coordinates = this.getPixelCoordinates(x, y);
		return workingBitmap.getPixel(coordinates.x, coordinates.y);
	}

	public Mode getMode() {
		return activeMode;
	}

	public ToolState getToolState() {
		return activeTool.getState();
	}

	public Point getFloatingBoxCoordinates() {
		return activeTool.getPosition();
	}

	public Point getFloatingBoxSize() {
		if (activeMode == Mode.FLOATINGBOX && activeTool instanceof FloatingBox) {
			FloatingBox floatingBox = (FloatingBox) activeTool;
			int width = floatingBox.getWidth();
			int height = floatingBox.getHeight();
			return new Point(width, height);
		}
		return null;
	}

	public float getFloatingBoxRotation() {
		if (activeMode == Mode.FLOATINGBOX && activeTool instanceof FloatingBox) {
			FloatingBox floatingBox = (FloatingBox) activeTool;
			return floatingBox.getRotation();
		}
		return 0;
	}
}
