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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.paintroid.graphic.utilities.Cursor;
import at.tugraz.ist.paintroid.graphic.utilities.Cursor.CursorState;
import at.tugraz.ist.paintroid.graphic.utilities.DrawFunctions;
import at.tugraz.ist.paintroid.graphic.utilities.UndoRedo;
import at.tugraz.ist.zoomscroll.ZoomStatus;

/**
 * This Class is the main drawing surface and handles all drawing elements
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public class DrawingSurface extends SurfaceView implements Observer, SurfaceHolder.Callback {
	/**
	 * Thread used for drawing the path
	 *
	 */
	class PathDrawingThread extends Thread {

		private Path path;
		
		private Paint paint;
		
		// Canvas thats bound to the bitmap
		private Canvas draw_canvas;
		
		// if true thread work, else thread finishes
		private boolean work = false;
		
		private SurfaceView surface_view;
		
		/**
		 * Constructor
		 * 
		 * @param path path to draw
		 * @param paint paint used to draw the path
		 * @param canvas canvas to draw path on
		 */
		public PathDrawingThread(Path path, Paint paint, Canvas canvas, SurfaceView surfaceView)
		{
	        this.path = path;
	        this.draw_canvas = canvas;
	        this.paint = paint;
	        this.surface_view = surfaceView;
		}
		
		@Override
        public void run() {
			while(this.work)
			{
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
		
		/**
		 * Draws the path on the canvas
		 */
		private void doDraw()
		{
			if(this.draw_canvas != null)
			{
				this.draw_canvas.drawPath(this.path, this.paint);
			}
		}
		
		/**
		 * Sets the paint
		 * 
		 * @param paint to set
		 */
		public synchronized void setPaint(Paint paint)
		{
			this.paint = paint;
		}
		
		/**
		 * Sets the canvas
		 * 
		 * @param canvas to set
		 */
		public synchronized void setCanvas(Canvas canvas)
		{
			this.draw_canvas = canvas;
		}
		
		/**
		 * Sets the running state
		 * 
		 * @param state to set
		 */
		public synchronized void setRunning(boolean state)
		{
			this.work = state;
		}
	}
	
	// The bitmap which will be edited
	private volatile Bitmap bitmap;

	// General vector for real coordinates on the bitmap
	private Vector<Integer> bitmap_coordinates = new Vector<Integer>();

	// Paint for drawing the bitmap
	private Paint bitmap_paint;

	// What type of action is activated
	public enum ActionType {
		ZOOM, SCROLL, DRAW, CHOOSE, UNDO, REDO, NONE, MAGIC, RESET
	}

	// Current selected action
	ActionType action = ActionType.SCROLL;

	// Zoom status
	private ZoomStatus zoomStatus;

	// Rectangles used for zoom and scroll
	private Rect rectImage = new Rect(); // For image cropping
	private Rect rectCanvas = new Rect(); // For specify the canvas drawing area

	// Aspect ratio (aspect ratio content) / (aspect ratio view)
	private float aspect;

	// Current color
	private int currentColor;

	// Current stroke width
	private int current_stroke;

	// Current shape
	private Cap current_shape = Cap.ROUND;
	
	private boolean useAntiAliasing = true;
	
	// Drawn path on the bitmap
	private Path draw_path;
	
	// Paint used for drawing the path
	private Paint path_paint;
	
	// Canvas used for drawing on the bitmap
	private Canvas draw_canvas = null;
	
	// UndoRedoObject
	private UndoRedo undo_redo_object;
	
	// UndoRedoObject
	private Cursor cursor;
	
	// Surface Listener
	private BaseSurfaceListener drawingSurfaceListener;
	
	private PathDrawingThread path_drawing_thread;

	// -----------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 */
	public DrawingSurface(Context context, AttributeSet attrs) {

		super(context, attrs);
		getHolder().addCallback(this);

		bitmap_paint = new Paint(Paint.DITHER_FLAG);
		
		undo_redo_object = new UndoRedo(this.getContext());
		
		cursor = new Cursor();
		
		draw_path = new Path();
		draw_path.reset();
		
		path_paint = new Paint();
		path_paint.setDither(true);
		path_paint.setStyle(Paint.Style.STROKE);
		path_paint.setStrokeJoin(Paint.Join.ROUND);
		
		drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
		drawingSurfaceListener.setSurface(this);
		setOnTouchListener(drawingSurfaceListener);
		
		path_drawing_thread = new PathDrawingThread(draw_path, path_paint, draw_canvas, this);
		path_drawing_thread.setRunning(true);
		path_drawing_thread.start();
	}
	
	/**
	 * Destructor
	 * 
	 * Kills the path drawing thread
	 * 
	 * @throws Throwable
	 */
	protected void finalize() throws Throwable
	{
		boolean retry = true;
		path_drawing_thread.setRunning(false);
		synchronized (path_drawing_thread) {
			path_drawing_thread.notify();
		}
		while(retry)
		{
			try {
				path_drawing_thread.join();
				retry = false;
			} catch (InterruptedException e) {
				synchronized (path_drawing_thread) {
					path_drawing_thread.notify();
				}
			}
		}
		super.finalize();
	} 

	/**
	 * Sets the type of activated action
	 * 
	 * @param type Action type to set
	 */
	public void setActionType(ActionType type) {
		if(drawingSurfaceListener.getClass() != DrawingSurfaceListener.class)
		{
			cursor.deactivate();
			drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
			drawingSurfaceListener.setSurface(this);
			drawingSurfaceListener.setZoomStatus(zoomStatus);
			setOnTouchListener(drawingSurfaceListener);
			invalidate();
		}
		drawingSurfaceListener.setControlType(type);
		action = type;
	}

	/**
	 * Sets the bitmap
	 * 
	 * @param bit Bitmap to set
	 */
	public void setBitmap(Bitmap bit) {
		bitmap = bit;
		if(bitmap != null)
		{
		  draw_canvas = new Canvas(bitmap);
		  path_drawing_thread.setCanvas(draw_canvas);
		  undo_redo_object.addDrawing(bitmap);
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
		return bitmap;
	}

	/**
	 * Sets the color chosen in ColorDialog
	 * 
	 * @param color Color to set
	 */
	public void setColor(int color) {
		currentColor = color;
		invalidate();
	}

	/**
	 * Sets the Stroke width chosen in StrokeDialog
	 * 
	 * @param stroke Stroke width to set
	 */
	public void setStroke(int stroke) {
		current_stroke = stroke;
		invalidate();
	}

	/**
	 * Sets the Shape which is chosen in the StrokenDialog
	 * 
	 * @param type Shape to set
	 */
	public void setShape(Cap type) {
		current_shape = type;
		invalidate();
	}
	
	/**
	 * If true antialiasing will be used while drawing
	 * 
	 * @param type Shape to set
	 */
	public void setAntiAliasing(boolean antiAliasingFlag) {
		useAntiAliasing = antiAliasingFlag;
	}

	/**
	 * Calculates the Aspect ratio
	 * 
	 */
	private void calculateAspect() {
		if (bitmap != null) { // Do this only when picture is in bitmap
			float width = bitmap.getWidth();
			float height = bitmap.getHeight();
			aspect = (width / height)
					/ (((float) getWidth()) / (float) getHeight());
		}
	}

	/**
	 * Sets the ZoomStatus
	 * 
	 * @param status Zoom status to set
	 */
	public void setZoomStatus(ZoomStatus status) {
		// Delete observer when already used
		if (zoomStatus != null) {
			zoomStatus.deleteObserver(this);
		}
		zoomStatus = status;
		drawingSurfaceListener.setZoomStatus(zoomStatus);
		zoomStatus.addObserver(this);
		invalidate(); // Set the view to invalid -> onDraw() will be called
	}

	/**
	 * Calculates aspect ration and calls super class method
	 * 
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
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
	 * @param x Coordinate of the pixel
	 * @param y Coordinate of the pixel
	 */
	public void getPixelColor(float x, float y) {
		// Get real pixel coordinates on bitmap
		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		if(!coordinatesWithinBitmap((int) bitmap_coordinates.elementAt(0), (int) bitmap_coordinates.elementAt(1)))
		{
			return;
		}
		if (bitmap != null && zoomStatus != null) {
			try {
				int color = bitmap.getPixel(bitmap_coordinates.elementAt(0),
					bitmap_coordinates.elementAt(1));
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
	protected void drawPathOnSurface(float x, float y) {
		if(draw_canvas == null)
		{
			Log.d("PAINTROID", "drawOnSurface: Bitmap not set");
			return;			
		}
		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		int imageX = bitmap_coordinates.elementAt(0).intValue();
		int imageY = bitmap_coordinates.elementAt(1).intValue();
		draw_path.lineTo(imageX, imageY);
		DrawFunctions.setPaint(path_paint, current_shape, current_stroke, currentColor, useAntiAliasing);
		synchronized (path_drawing_thread) {
			path_drawing_thread.notify();
		}
		if(pathIsOnBitmap())
		{
			undo_redo_object.addPath(draw_path, path_paint);
		}
		
		draw_path.reset();
	}
	
	/**
	 * Called by the drawing surface listener on the touch up event if no move appeared.
	 * 
	 */
	protected void drawPaintOnSurface(float x, float y) {
		if(draw_canvas == null)
		{
			Log.d("PAINTROID", "drawOnSurface: Bitmap not set");
			return;			
		}
		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		int imageX = bitmap_coordinates.elementAt(0).intValue();
		int imageY = bitmap_coordinates.elementAt(1).intValue();
		
		DrawFunctions.setPaint(path_paint, current_shape, current_stroke, currentColor, useAntiAliasing);
		if(coordinatesWithinBitmap(imageX, imageY))
		{
			draw_canvas.drawPoint(imageX, imageY, path_paint);
			undo_redo_object.addPoint(imageX, imageY, path_paint);
		}
		draw_path.reset();
		invalidate();
	}

	/**
	 * Called by the drawing surface listener when using the magic wand tool.
	 * 
	 * @param x Screen coordinate
	 * @param y Screen coordinate
	 */
	protected void replaceColorOnSurface(float x, float y) {
		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		float imageX = bitmap_coordinates.elementAt(0);
		float imageY = bitmap_coordinates.elementAt(1);
		
		if(coordinatesWithinBitmap((int) imageX, (int) imageY))
		{
			int chosen_pixel_color = bitmap.getPixel((int) imageX, (int) imageY);
				
			Paint replaceColorPaint = new Paint();
			replaceColorPaint.setColor(currentColor);
			replaceColorPaint.setXfermode(new AvoidXfermode(chosen_pixel_color, 250, AvoidXfermode.Mode.TARGET));
				
			Canvas replaceColorCanvas = new Canvas();
			replaceColorCanvas.setBitmap(bitmap);
			replaceColorCanvas.drawPaint(replaceColorPaint);
	
			undo_redo_object.addDrawing(bitmap);
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
		if (bitmap == null || zoomStatus == null) {
			return;
		}

		float x = zoomStatus.getX();
		float y = zoomStatus.getY();

		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);

		// Get actual height and width Values
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int viewWidth = getWidth();
		int viewHeight = getHeight();

		// Get scroll-window position
		float scrollX = zoomStatus.getScrollX();
		float scrollY = zoomStatus.getScrollY();
		
		final float zoomX = zoomStatus.getZoomInX(aspect) * viewWidth
				/ bitmapWidth;
		final float zoomY = zoomStatus.getZoomInY(aspect) * viewHeight
				/ bitmapHeight;

		// Setup image and canvas rectangles
		rectImage.left = (int) (scrollX * bitmapWidth - viewWidth
				/ (zoomX * 2));
		rectImage.top = (int) (scrollY * bitmapHeight - viewHeight
				/ (zoomY * 2));
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

		DrawFunctions.setPaint(path_paint, current_shape, current_stroke, currentColor, useAntiAliasing);
		canvas.drawBitmap(bitmap, rectImage, rectCanvas, bitmap_paint);
		
		cursor.draw(canvas, current_shape, current_stroke, currentColor);
	}

	/**
	 * called if surface changes
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * called when surface is created
	 */
	public void surfaceCreated(SurfaceHolder holder) {

	}

	/**
	 * called when surface is destroyed
	 * 
	 * ends path drawing thread
	 */
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
	 * @param listener The ColorPickupListener to use
	 */
	public void setColorPickupListener(ColorPickupListener listener) {
		colorListener = listener;
	}
	
	/**
	 * Sets starting point of the path
	 * 
	 * @param path drawn path
	 */
	public void setPath(float x, float y)
	{
		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		int imageX = bitmap_coordinates.elementAt(0).intValue();
		int imageY = bitmap_coordinates.elementAt(1).intValue();
		draw_path.reset();
		draw_path.moveTo(imageX, imageY);
		synchronized (path_drawing_thread) {
			path_drawing_thread.notify();
		}
	}
	
	/**
	 * Sets the actual drawn path
	 * 
	 * @param path drawn path
	 */
	public void setPath(float x, float y, float prev_x, float prev_y)
	{
		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		float imageX = bitmap_coordinates.elementAt(0).intValue();
		float imageY = bitmap_coordinates.elementAt(1).intValue();
		bitmap_coordinates = DrawFunctions.RealCoordinateValue(prev_x, prev_y, rectImage, rectCanvas);
		float prevImageX = bitmap_coordinates.elementAt(0).intValue();
		float prevImageY = bitmap_coordinates.elementAt(1).intValue();
		
        draw_path.quadTo(prevImageX, prevImageY, (imageX + prevImageX)/2, (imageY + prevImageY)/2);
        synchronized (path_drawing_thread) {
			path_drawing_thread.notify();
		}
	}

	/**
	 * Undo last step
	 */
	public void undoOneStep()
	{
		Bitmap undoBitmap = undo_redo_object.undo();
		if(undoBitmap != null)
		{
			bitmap = undoBitmap;
		  	draw_canvas = new Canvas(bitmap);
		  	path_drawing_thread.setCanvas(draw_canvas);
		  	calculateAspect();
			invalidate();
		}
	}
	
	/**
	 * Redo last undone step
	 */
	public void redoOneStep()
	{
		Bitmap redoBitmap = undo_redo_object.redo();
		if(redoBitmap != null)
		{
			bitmap = redoBitmap;
		  	draw_canvas = new Canvas(bitmap);
		  	path_drawing_thread.setCanvas(draw_canvas);
		  	calculateAspect();
		  	invalidate();
		}
	}
	
	/**
	 * clear undo and redo stack
	 */
	public void clearUndoRedo()
	{
		undo_redo_object.clear();
	}
	
	public boolean singleTapEvent()
	{
		boolean eventUsed = cursor.singleTapEvent();
		if(eventUsed)
		{
			if(cursor.getState() == CursorState.DRAW)
			{
				Point cursorPosition = cursor.getPosition();
				drawPaintOnSurface(cursorPosition.x, cursorPosition.y);
			}
			invalidate();
		}
		return eventUsed;
	}
	
	public boolean doubleTapEvent(float x, float y)
	{
		boolean eventUsed = cursor.doubleTapEvent((int)x, (int)y, zoomStatus.getZoomLevel());
		if(eventUsed)
		{
			switch(cursor.getState())
			{
			case INACTIVE:
				drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
				break;
			case ACTIVE:
				drawingSurfaceListener = new CursorDrawingSurfaceListener(this.getContext(), cursor);
			}
			drawingSurfaceListener.setSurface(this);
			drawingSurfaceListener.setZoomStatus(zoomStatus);
			drawingSurfaceListener.setControlType(action);
			setOnTouchListener(drawingSurfaceListener);
			invalidate();
		}
		return eventUsed;
	}
	
	public BaseSurfaceListener getDrawingSurfaceListener()
	{
		return drawingSurfaceListener;
	}
	
	public void setScreenSize(Point screenSize) {
		cursor.setScreenSize(screenSize);
	}
	
	/**
	 * checks if at least a part of the
	 * path is drawn on the bitmap
	 * 
	 * @return true if a part of the path
	 * 		   is on the bitmap, else
	 * 		   false
	 */
	protected boolean pathIsOnBitmap()
	{
		RectF pathBoundary = new RectF();
		draw_path.computeBounds(pathBoundary, true);
		RectF bitmapBoundary = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		return pathBoundary.intersect(bitmapBoundary);
	}
	
	/**
	 * checks if coordinates are on the bitmap
	 * 
	 * @param imageX x-coordinate
	 * @param imageY y-coordinate
	 * @return true if coordinates are on bitmap,
	 * 		   else false
	 */
	protected boolean coordinatesWithinBitmap(int imageX, int imageY)
	{
		return imageX >= 0 && imageY >= 0 && imageX < bitmap.getWidth() && imageY < bitmap.getHeight();
	}

	//------------------------------Methods For JUnit TESTING---------------------------------------
	
	public Vector<Integer> getPixelCoordinates(float x, float y)
	{
		bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
		int imageX = bitmap_coordinates.elementAt(0).intValue();
		int imageY = bitmap_coordinates.elementAt(1).intValue();
		imageX = imageX < 0 ? 0 : imageX;
		imageY = imageY < 0 ? 0 : imageY;
		imageX = imageX >= bitmap.getWidth() ? bitmap.getWidth()-1 : imageX;
		imageY = imageY >= bitmap.getHeight() ? bitmap.getHeight()-1 : imageY;
		bitmap_coordinates.setElementAt(imageX, 0);
		bitmap_coordinates.setElementAt(imageY, 1);
		return bitmap_coordinates;
	}
	
	public int getPixelFromScreenCoordinates(float x, float y)
	{
		this.getPixelCoordinates(x, y);
		return bitmap.getPixel(bitmap_coordinates.elementAt(0).intValue(), bitmap_coordinates.elementAt(1).intValue());
	}
	
	public CursorState getCursorState()
	{
		return cursor.getState();
	}
}
