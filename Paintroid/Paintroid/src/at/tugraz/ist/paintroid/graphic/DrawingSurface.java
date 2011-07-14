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
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.paintroid.graphic.listeners.BaseSurfaceOnTouchListener;
import at.tugraz.ist.paintroid.graphic.listeners.DrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.listeners.FloatingBoxDrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.listeners.ToolDrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.utilities.Cursor;
import at.tugraz.ist.paintroid.graphic.utilities.DrawFunctions;
import at.tugraz.ist.paintroid.graphic.utilities.FloatingBox;
import at.tugraz.ist.paintroid.graphic.utilities.Middlepoint;
import at.tugraz.ist.paintroid.graphic.utilities.Tool;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;
import at.tugraz.ist.paintroid.graphic.utilities.UndoRedo;
import at.tugraz.ist.zoomscroll.ZoomStatus;

/**
 * This Class is the main drawing surface and handles all drawing elements
 * 
 * Status: refactored 20.02.2011
 * 
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class DrawingSurface extends SurfaceView implements Observer, SurfaceHolder.Callback {

	class PathDrawingThread extends Thread {

		private Path path;
		private Paint paint;
		private Canvas draw_canvas;
		private boolean run = false;
		private SurfaceView surface_view;

		public PathDrawingThread(Path path, Paint paint, Canvas canvas, SurfaceView surfaceView) {
			this.path = path;
			this.draw_canvas = canvas;
			this.paint = paint;
			this.surface_view = surfaceView;
		}

		@Override
		public void run() {
			while (this.run) {
				try {
					synchronized (this) {
						// Wait for work
						this.wait();
						// Do actual path drawing
						doDraw();
						// Force view to redraw
						this.surface_view.postInvalidate();
					}
				} catch (InterruptedException e) {
				}
			}
		}

		private void doDraw() {
			if (this.draw_canvas != null) {
				this.draw_canvas.drawPath(this.path, this.paint);
			}
		}

		public synchronized void setPaint(Paint paint) {
			this.paint = paint;
		}

		public synchronized void setCanvas(Canvas canvas) {
			this.draw_canvas = canvas;
		}

		public synchronized void setRunning(boolean state) {
			this.run = state;
		}
	}

	private volatile Bitmap canvasBitmap;
	private Vector<Integer> bitmapCoords = new Vector<Integer>();
	private Paint bitmapPaint;

	public enum ActionType {
		ZOOM, SCROLL, DRAW, CHOOSE, UNDO, REDO, NONE, MAGIC, RESET
	}

	public enum Mode {
		DRAW, CURSOR, CENTERPOINT, FLOATINGBOX
	}

	private Mode activeMode;
	private ActionType activeAction = ActionType.SCROLL;
	private ZoomStatus zoomStatus;

	private Rect rectImage = new Rect(); // For image cropping
	private Rect rectCanvas = new Rect(); // For specify the canvas drawing area

	private float aspectRatio;
	private int activeColor;
	private int activeStroke;
	private Cap activeShape = Cap.ROUND;

	private boolean useAntiAliasing = true;

	private Path pathToDraw;

	// Paint used for drawing the path
	private Paint pathPaint;

	// Canvas used for drawing on the bitmap
	private Canvas drawingSurfaceCanvas = null;

	// UndoRedoObject
	private UndoRedo undo_redo_object;

	// Tool object
	private Tool activeTool;

	private Point drawingSurfaceCenter;

	private BaseSurfaceOnTouchListener drawingSurfaceOnTouchListener;

	private PathDrawingThread pathDrawingThread;

	// -----------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 */
	public DrawingSurface(Context context, AttributeSet attrs) {

		super(context, attrs);
		getHolder().addCallback(this);

		this.bitmapPaint = new Paint(Paint.DITHER_FLAG);

		this.undo_redo_object = new UndoRedo(this.getContext());

		this.activeTool = new Cursor();
		this.drawingSurfaceCenter = new Point(0, 0);

		this.pathToDraw = new Path();
		this.pathToDraw.reset();

		this.pathPaint = new Paint();
		this.pathPaint.setDither(true);
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setStrokeJoin(Paint.Join.ROUND);

		this.activeMode = Mode.DRAW;
		this.drawingSurfaceOnTouchListener = new DrawingSurfaceListener(this.getContext());
		this.drawingSurfaceOnTouchListener.setSurface(this);
		setOnTouchListener(this.drawingSurfaceOnTouchListener);

		this.pathDrawingThread = new PathDrawingThread(this.pathToDraw, this.pathPaint, this.drawingSurfaceCanvas, this);
		this.pathDrawingThread.setRunning(true);
		this.pathDrawingThread.start();
	}

	/**
	 * Destructor
	 * 
	 * Kills the path drawing thread
	 * 
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable {
		boolean retry = true;
		pathDrawingThread.setRunning(false);
		synchronized (pathDrawingThread) {
			pathDrawingThread.notify();
		}
		while (retry) {
			try {
				pathDrawingThread.join();
				retry = false;
			} catch (InterruptedException e) {
				synchronized (pathDrawingThread) {
					pathDrawingThread.notify();
				}
			}
		}
		super.finalize();
	}

	/**
	 * Sets the type of activated action
	 * 
	 * @param type
	 *            Action type to set
	 */
	public void setActionType(ActionType type) {
		if (drawingSurfaceOnTouchListener.getClass() != DrawingSurfaceListener.class) {
			if (activeTool instanceof Middlepoint) {
				changeMiddlepointMode();
			} else if (activeTool instanceof FloatingBox) {
				changeFloatingBoxMode();
			} else {
				activeTool.deactivate();
				activeTool = new Cursor(activeTool);
				drawingSurfaceOnTouchListener = new DrawingSurfaceListener(this.getContext());
				drawingSurfaceOnTouchListener.setSurface(this);
				drawingSurfaceOnTouchListener.setZoomStatus(zoomStatus);
				setOnTouchListener(drawingSurfaceOnTouchListener);
			}
			invalidate();
		}
		if (type != ActionType.NONE) {
			drawingSurfaceOnTouchListener.setControlType(type);
		}
		activeAction = type;
	}

	/**
	 * Sets the bitmap
	 * 
	 * @param bit
	 *            Bitmap to set
	 */
	public void setBitmap(Bitmap bit) {
		canvasBitmap = bit;
		if (canvasBitmap != null) {
			drawingSurfaceCanvas = new Canvas(canvasBitmap);
			pathDrawingThread.setCanvas(drawingSurfaceCanvas);
			undo_redo_object.addDrawing(canvasBitmap);
		}
		calculateAspect();
		invalidate(); // Set the view to invalid -> onDraw() will be called
	}

	/**
	 * Gets the bitmap
	 * 
	 * @return Bitmap
	 */
	public Bitmap getBitmap() {
		return canvasBitmap;
	}

	/**
	 * Sets the color chosen in ColorDialog
	 * 
	 * @param color
	 *            Color to set
	 */
	public void setColor(int color) {
		activeColor = color;
		paintChanged();
		invalidate();
	}

	/**
	 * Sets the Stroke width chosen in StrokeDialog
	 * 
	 * @param stroke
	 *            Stroke width to set
	 */
	public void setStroke(int stroke) {
		activeStroke = stroke;
		paintChanged();
		invalidate();
	}

	/**
	 * Sets the Shape which is chosen in the StrokenDialog
	 * 
	 * @param type
	 *            Shape to set
	 */
	public void setShape(Cap type) {
		activeShape = type;
		paintChanged();
		invalidate();
	}

	/**
	 * If true antialiasing will be used while drawing
	 * 
	 * @param type
	 *            Shape to set
	 */
	public void setAntiAliasing(boolean antiAliasingFlag) {
		useAntiAliasing = antiAliasingFlag;
	}

	/**
	 * Calculates the Aspect ratio
	 * 
	 */
	private void calculateAspect() {
		if (canvasBitmap != null) { // Do this only when picture is in bitmap
			float width = canvasBitmap.getWidth();
			float height = canvasBitmap.getHeight();
			aspectRatio = (width / height) / (((float) getWidth()) / (float) getHeight());
		}
	}

	/**
	 * Sets the ZoomStatus
	 * 
	 * @param status
	 *            Zoom status to set
	 */
	public void setZoomStatus(ZoomStatus status) {
		// Delete observer when already used
		if (zoomStatus != null) {
			zoomStatus.deleteObserver(this);
		}
		zoomStatus = status;
		drawingSurfaceOnTouchListener.setZoomStatus(zoomStatus);
		zoomStatus.addObserver(this);
		invalidate(); // Set the view to invalid -> onDraw() will be called
	}

	/**
	 * Calculates aspect ration and calls super class method
	 * 
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calculateAspect();
	}

	/**
	 * Implement observer
	 * 
	 * Invalidates the view
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		invalidate(); // Set the view to invalid -> onDraw() will be called
	}

	/**
	 * Gets the pixel color
	 * 
	 * @param x
	 *            Coordinate of the pixel
	 * @param y
	 *            Coordinate of the pixel
	 */
	public void getPixelColor(float x, float y) {
		// Get real pixel coordinates on bitmap
		bitmapCoords = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		if (!coordinatesWithinBitmap(bitmapCoords.elementAt(0), bitmapCoords.elementAt(1))) {
			return;
		}
		if (canvasBitmap != null && zoomStatus != null) {
			try {
				int color = canvasBitmap.getPixel(bitmapCoords.elementAt(0), bitmapCoords.elementAt(1));
				// Set the listener ColorChanged
				colorListener.colorChanged(color);
			} catch (Exception e) {
			}
		}
		invalidate(); // Set the view to invalid -> onDraw() will be called
	}

	/**
	 * Called by the drawing surface listener on the touch up event.
	 * 
	 */
	public void drawPathOnSurface(float x, float y) {
		if (drawingSurfaceCanvas == null) {
			Log.d("PAINTROID", "drawOnSurface: Bitmap not set");
			return;
		}
		bitmapCoords = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		int imageX = bitmapCoords.elementAt(0).intValue();
		int imageY = bitmapCoords.elementAt(1).intValue();
		pathToDraw.lineTo(imageX, imageY);
		DrawFunctions.setPaint(pathPaint, activeShape, activeStroke, activeColor, useAntiAliasing, null);
		synchronized (pathDrawingThread) {
			pathDrawingThread.notify();
		}
		if (pathIsOnBitmap()) {
			undo_redo_object.addPath(pathToDraw, pathPaint);
		}

		pathToDraw.reset();
	}

	/**
	 * Called by the drawing surface listener on the touch up event if no move appeared.
	 * 
	 */
	public void drawPaintOnSurface(float x, float y) {
		if (drawingSurfaceCanvas == null) {
			Log.d("PAINTROID", "drawOnSurface: Bitmap not set");
			return;
		}
		bitmapCoords = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		int imageX = bitmapCoords.elementAt(0).intValue();
		int imageY = bitmapCoords.elementAt(1).intValue();

		DrawFunctions.setPaint(pathPaint, activeShape, activeStroke, activeColor, useAntiAliasing, null);
		if (coordinatesWithinBitmap(imageX, imageY)) {
			drawingSurfaceCanvas.drawPoint(imageX, imageY, pathPaint);
			undo_redo_object.addPoint(imageX, imageY, pathPaint);
		}
		pathToDraw.reset();
		invalidate();
	}

	/**
	 * Called by the drawing surface listener when using the magic wand tool.
	 * 
	 * @param x
	 *            Screen coordinate
	 * @param y
	 *            Screen coordinate
	 */
	public void replaceColorOnSurface(float x, float y) {
		bitmapCoords = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		float imageX = bitmapCoords.elementAt(0);
		float imageY = bitmapCoords.elementAt(1);

		if (coordinatesWithinBitmap((int) imageX, (int) imageY)) {
			int chosen_pixel_color = canvasBitmap.getPixel((int) imageX, (int) imageY);

			Paint replaceColorPaint = new Paint();
			replaceColorPaint.setColor(activeColor);
			replaceColorPaint.setXfermode(new AvoidXfermode(chosen_pixel_color, 250, AvoidXfermode.Mode.TARGET));

			Canvas replaceColorCanvas = new Canvas();
			replaceColorCanvas.setBitmap(canvasBitmap);
			replaceColorCanvas.drawPaint(replaceColorPaint);

			undo_redo_object.addDrawing(canvasBitmap);
			invalidate(); // Set the view to invalid -> onDraw() will be called
		}
		setActionType(ActionType.NONE);
	}

	/**
	 * This method is ALWAYS called when something
	 * (even buttons etc.) is drawn on screen.
	 * 
	 * Sets the image and canvas rectangles and draws the bitmap
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (canvasBitmap == null || zoomStatus == null) {
			return;
		}

		//		float x = zoomStatus.getX();
		//		float y = zoomStatus.getY();

		//		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);

		// Get actual height and width Values
		int bitmapWidth = canvasBitmap.getWidth();
		int bitmapHeight = canvasBitmap.getHeight();
		int viewWidth = this.getWidth();
		int viewHeight = this.getHeight();

		// Get scroll-window position
		float scrollX = zoomStatus.getScrollX();
		float scrollY = zoomStatus.getScrollY();

		final float zoomX = getZoomX();
		final float zoomY = getZoomY();

		// Setup image and canvas rectangles
		rectImage.left = (int) (scrollX * bitmapWidth - viewWidth / (zoomX * 2));
		rectImage.top = (int) (scrollY * bitmapHeight - viewHeight / (zoomY * 2));
		rectImage.right = (int) (rectImage.left + viewWidth / zoomX);
		rectImage.bottom = (int) (rectImage.top + viewHeight / zoomY);
		rectCanvas.left = getLeft();
		rectCanvas.top = getTop();
		rectCanvas.right = getRight();
		rectCanvas.bottom = getBottom();

		// Adjust rectangle so that it fits within the source image.
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

		DrawFunctions.setPaint(pathPaint, activeShape, activeStroke, activeColor, useAntiAliasing, null);

		// make a ckeckerboard pattern background
		Bitmap bm = Bitmap.createBitmap(new int[] { 0xFFFFFFFF, 0xFFCCCCCC, 0xFFCCCCCC, 0xFFFFFFFF }, 2, 2,
				Bitmap.Config.RGB_565);
		Shader mBG = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		Matrix m = new Matrix();
		m.setScale(6, 6);
		mBG.setLocalMatrix(m);
		Paint paint = new Paint();
		paint.setShader(mBG);
		canvas.drawRect(rectCanvas, paint);

		canvas.drawBitmap(canvasBitmap, rectImage, rectCanvas, bitmapPaint);

		activeTool.draw(canvas, activeShape, activeStroke, activeColor);
	}

	/**
	 * called if surface changes
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * called when surface is created
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	/**
	 * called when surface is destroyed
	 * 
	 * ends path drawing thread
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	/**
	 * Custom Listener for Color-Pickup Event
	 * 
	 */
	public interface ColorPickupListener {
		void colorChanged(int color);
	}

	// Variable that holds the Listener
	private ColorPickupListener colorListener = null;

	/**
	 * Allows to set an Listener and react to the event
	 * 
	 * @param listener
	 *            The ColorPickupListener to use
	 */
	public void setColorPickupListener(ColorPickupListener listener) {
		colorListener = listener;
	}

	/**
	 * Sets starting point of the path
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	public void setPath(float x, float y) {
		bitmapCoords = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		int imageX = bitmapCoords.elementAt(0).intValue();
		int imageY = bitmapCoords.elementAt(1).intValue();
		pathToDraw.reset();
		pathToDraw.moveTo(imageX, imageY);
		synchronized (pathDrawingThread) {
			pathDrawingThread.notify();
		}
	}

	/**
	 * Sets the actual drawn path
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @param x
	 *            the previous x-coordinate
	 * @param y
	 *            the previous y-coordinate
	 */
	public void setPath(float x, float y, float prev_x, float prev_y) {
		bitmapCoords = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		float imageX = bitmapCoords.elementAt(0).intValue();
		float imageY = bitmapCoords.elementAt(1).intValue();
		bitmapCoords = DrawFunctions.RealCoordinateValue(prev_x, prev_y, rectImage, rectCanvas);
		float prevImageX = bitmapCoords.elementAt(0).intValue();
		float prevImageY = bitmapCoords.elementAt(1).intValue();

		pathToDraw.quadTo(prevImageX, prevImageY, (imageX + prevImageX) / 2, (imageY + prevImageY) / 2);
		synchronized (pathDrawingThread) {
			pathDrawingThread.notify();
		}
	}

	/**
	 * Undo last step
	 */
	public void undoOneStep() {
		Bitmap undoBitmap = undo_redo_object.undo();
		if (undoBitmap != null) {
			canvasBitmap = undoBitmap;
			drawingSurfaceCanvas = new Canvas(canvasBitmap);
			pathDrawingThread.setCanvas(drawingSurfaceCanvas);
			calculateAspect();
			invalidate();
		}
	}

	/**
	 * Redo last undone step
	 */
	public void redoOneStep() {
		Bitmap redoBitmap = undo_redo_object.redo();
		if (redoBitmap != null) {
			canvasBitmap = redoBitmap;
			drawingSurfaceCanvas = new Canvas(canvasBitmap);
			pathDrawingThread.setCanvas(drawingSurfaceCanvas);
			calculateAspect();
			invalidate();
		}
	}

	/**
	 * clear undo and redo stack
	 */
	public void clearUndoRedo() {
		undo_redo_object.clear();
	}

	/**
	 * 
	 */
	public void addDrawingToUndoRedo() {
		undo_redo_object.addDrawing(canvasBitmap);
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
		if (activeTool.getState() == ToolState.INACTIVE && activeAction != ActionType.DRAW) {
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
					drawingSurfaceOnTouchListener = new DrawingSurfaceListener(this.getContext());
					break;
				case ACTIVE:
					activeMode = Mode.CURSOR;
					drawingSurfaceOnTouchListener = new ToolDrawingSurfaceListener(this.getContext(), activeTool);
			}
			drawingSurfaceOnTouchListener.setSurface(this);
			drawingSurfaceOnTouchListener.setZoomStatus(zoomStatus);
			drawingSurfaceOnTouchListener.setControlType(activeAction);
			setOnTouchListener(drawingSurfaceOnTouchListener);
			invalidate();
		}
		return eventUsed;
	}

	/**
	 * called of the paint gets changed
	 * 
	 * used to draw a point on the actual position of the cursor
	 * if activated
	 * 
	 */
	public void paintChanged() {
		if (activeTool.getState() == ToolState.DRAW) {
			Point cursorPosition = activeTool.getPosition();
			drawPaintOnSurface(cursorPosition.x, cursorPosition.y);
		}
	}

	/**
	 * gets the actual surface listener
	 * 
	 * @return the listener to the surface
	 */
	public BaseSurfaceOnTouchListener getDrawingSurfaceListener() {
		return drawingSurfaceOnTouchListener;
	}

	/**
	 * sets the screen size
	 * 
	 * @param screenSize
	 *            the device's screen size
	 */
	public void setScreenSize(Point screenSize) {
		activeTool.setScreenSize(screenSize);
	}

	/**
	 * checks if at least a part of the
	 * path is drawn on the bitmap
	 * 
	 * @return true if a part of the path
	 *         is on the bitmap, else
	 *         false
	 */
	protected boolean pathIsOnBitmap() {
		if (canvasBitmap == null) {
			return false;
		}
		RectF pathBoundary = new RectF();
		pathToDraw.computeBounds(pathBoundary, true);
		RectF bitmapBoundary = new RectF(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight());
		return pathBoundary.intersect(bitmapBoundary);
	}

	/**
	 * checks if coordinates are on the bitmap
	 * 
	 * @param imageX
	 *            x-coordinate
	 * @param imageY
	 *            y-coordinate
	 * @return true if coordinates are on bitmap,
	 *         else false
	 */
	protected boolean coordinatesWithinBitmap(int imageX, int imageY) {
		if (canvasBitmap == null) {
			return false;
		} else {
			return imageX >= 0 && imageY >= 0 && imageX < canvasBitmap.getWidth() && imageY < canvasBitmap.getHeight();
		}
	}

	/**
	 * Activated or deactivates the middle point mode
	 */
	public void changeMiddlepointMode() {
		switch (activeMode) {
			case CENTERPOINT:
				activeTool.deactivate();
				activeTool = new Cursor(activeTool);
				drawingSurfaceOnTouchListener = new DrawingSurfaceListener(this.getContext());
				activeMode = Mode.DRAW;
				break;
			default:
				activeTool = new Middlepoint(activeTool);
				drawingSurfaceOnTouchListener = new ToolDrawingSurfaceListener(this.getContext(), activeTool);
				activeTool.activate(drawingSurfaceCenter);
				activeMode = Mode.CENTERPOINT;
				break;
		}
		drawingSurfaceOnTouchListener.setSurface(this);
		drawingSurfaceOnTouchListener.setZoomStatus(zoomStatus);
		drawingSurfaceOnTouchListener.setControlType(activeAction);
		setOnTouchListener(drawingSurfaceOnTouchListener);
		invalidate();
	}

	/**
	 * Activated or deactivates the floating box mode
	 */
	public void changeFloatingBoxMode() {
		switch (activeMode) {
			case FLOATINGBOX:
				activeTool.deactivate();
				activeTool = new Cursor(activeTool);
				drawingSurfaceOnTouchListener = new DrawingSurfaceListener(this.getContext());
				activeMode = Mode.DRAW;
				break;
			default:
				FloatingBox floatingBox = new FloatingBox(activeTool);
				activeTool = floatingBox;
				drawingSurfaceOnTouchListener = new FloatingBoxDrawingSurfaceListener(this.getContext(), floatingBox);
				activeTool.activate();
				activeMode = Mode.FLOATINGBOX;
				break;
		}
		drawingSurfaceOnTouchListener.setSurface(this);
		drawingSurfaceOnTouchListener.setZoomStatus(zoomStatus);
		drawingSurfaceOnTouchListener.setControlType(activeAction);
		setOnTouchListener(drawingSurfaceOnTouchListener);
		//called by robotium too
		postInvalidate();
	}

	/**
	 * Sets the center
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 */
	public void setCenter(int x, int y) {
		this.drawingSurfaceCenter.x = x;
		this.drawingSurfaceCenter.y = y;
	}

	/**
	 * getter for the center
	 * 
	 * @return center coordinates
	 */
	public Point getCenter() {
		return this.drawingSurfaceCenter;
	}

	/**
	 * Puts a bitmap into the floating box
	 * 
	 * @param newPng
	 *            bitmap to put into the floating box
	 */
	public void addPng(Bitmap newPng) {
		if (activeMode != Mode.FLOATINGBOX) {
			changeFloatingBoxMode();
		}
		FloatingBox floatingBox = (FloatingBox) activeTool;
		floatingBox.reset();
		floatingBox.addBitmap(newPng);
		//called by robotium too
		postInvalidate();
	}

	/**
	 * Calculates the coordinates on the bitmap from the screen coordinates
	 * 
	 * @param x
	 *            screen coordinate
	 * @param y
	 *            screen coordinate
	 * @return bitmap coordinates
	 */
	public Point getPixelCoordinates(float x, float y) {
		bitmapCoords = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		int imageX = bitmapCoords.elementAt(0).intValue();
		int imageY = bitmapCoords.elementAt(1).intValue();
		imageX = imageX < 0 ? 0 : imageX;
		imageY = imageY < 0 ? 0 : imageY;
		imageX = imageX >= canvasBitmap.getWidth() ? canvasBitmap.getWidth() - 1 : imageX;
		imageY = imageY >= canvasBitmap.getHeight() ? canvasBitmap.getHeight() - 1 : imageY;
		return new Point(imageX, imageY);
	}

	/**
	 * Calculates the X zoom level
	 * 
	 * @return zoom level
	 */
	public float getZoomX() {
		if (canvasBitmap != null) {
			return zoomStatus.getZoomInX(aspectRatio) * getWidth() / canvasBitmap.getWidth();
		} else {
			return 0;
		}
	}

	/**
	 * Calculates the Y zoom level
	 * 
	 * @return zoom level
	 */
	public float getZoomY() {
		return zoomStatus.getZoomInY(aspectRatio) * getHeight() / canvasBitmap.getHeight();
	}

	//------------------------------Methods For JUnit TESTING---------------------------------------

	public int getPixelFromScreenCoordinates(float x, float y) {
		Point coordinates = this.getPixelCoordinates(x, y);
		return canvasBitmap.getPixel(coordinates.x, coordinates.y);
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
