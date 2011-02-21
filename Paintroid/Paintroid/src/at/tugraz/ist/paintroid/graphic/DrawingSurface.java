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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import at.tugraz.ist.paintroid.graphic.utilities.DrawFunctions;
import at.tugraz.ist.zoomscroll.ZoomStatus;

/**
 * This Class is the main drawing surface and handles all drawing elements
 * 
 * Status: refactored 04.02.2011
 * @author PaintroidTeam
 * @version 6.0b
 */
public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
  /**
   * Thread used for drawing
   *
   */
  public class DrawingSurfaceThread extends Thread implements Observer {
      private SurfaceHolder surfaceHolder;
      
      private Context context;
      
      private boolean running;

      private DrawingSurface surface;
      
      // The bitmap which will be edited
      private Bitmap bitmap;

      // General vector for real coordinates on the bitmap
      private Vector<Integer> bitmap_coordinates = new Vector<Integer>();

      // Paint for drawing the bitmap
      private Paint bitmap_paint;

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
      
      /**
       * Constructor
       * 
       * @param surfaceHolder surface holder
       * @param context application context
       */
      public DrawingSurfaceThread(SurfaceHolder surfaceHolder, Context context) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        
        bitmap_paint = new Paint(Paint.DITHER_FLAG);
        
        undo_redo_object = new UndoRedo(this.context);
        
        draw_path = new Path();
        draw_path.reset();
        
        path_paint = new Paint();
        path_paint.setDither(true);
        path_paint.setStyle(Paint.Style.STROKE);
        path_paint.setStrokeJoin(Paint.Join.ROUND);
      }
      
      @Override
      public void run() {
          while (running) {
              Canvas canvas = null;
              try {
                  canvas = surfaceHolder.lockCanvas();
                  synchronized (surfaceHolder) {
                      doDraw(canvas);
                  }
              } finally {
                  if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                  }
              }
//              synchronized (surfaceHolder) {
//                try {
//                  surfaceHolder.wait();
//                } catch (InterruptedException e) {
//                }
//              }
          }
      }
      
      /**
       * Sets the state of the thread
       * 
       * @param state true thread runs, else thread stops
       */
      public void setRunning(boolean state)
      {
        this.running = state;
      }
      
      /**
       * Sets the type of activated action
       * 
       * @param type Action type to set
       */
      public void setActionType(ActionType type) {
        this.action = type;
      }

      /**
       * Sets the bitmap
       * 
       * @param bit Bitmap to set
       */
      public void setBitmap(Bitmap bit) {
        synchronized (surfaceHolder) {
          bitmap = bit;
          if(bitmap != null)
          {
            draw_canvas = new Canvas(bitmap);
            undo_redo_object.addDrawing(bitmap);
          }
        }
        calculateAspect();
//        notify();
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
        synchronized (surfaceHolder) {
          currentColor = color;
        }
      }

      /**
       * Sets the Stroke width chosen in StrokeDialog
       * 
       * @param stroke Stroke width to set
       */
      public void setStroke(int stroke) {
        synchronized (surfaceHolder) {
          current_stroke = stroke;
        }
      }

      /**
       * Sets the Shape which is chosen in the StrokenDialog
       * 
       * @param type Shape to set
       */
      public void setShape(Cap type) {
        synchronized (surfaceHolder) {
          current_shape = type;
        }
      }
      
      /**
       * If true antialiasing will be used while drawing
       * 
       * @param type Shape to set
       */
      public void setAntiAliasing(boolean antiAliasingFlag) {
        synchronized (surfaceHolder) {
          useAntiAliasing = antiAliasingFlag;
        }
      }

      /**
       * Calculates the Aspect ratio
       * 
       */
      private void calculateAspect() {
        synchronized (surfaceHolder) {
          if (bitmap != null) { // Do this only when picture is in bitmap
            float width = bitmap.getWidth();
            float height = bitmap.getHeight();
            aspect = (width / height)
                / (((float) getWidth()) / (float) getHeight());
          }
        }
      }

      /**
       * Sets the ZoomStatus
       * 
       * @param status Zoom status to set
       */
      public void setZoomStatus(ZoomStatus status) {
        synchronized (surfaceHolder) {
          // Delete observer when already used
          if (zoomStatus != null) {
            zoomStatus.deleteObserver(this);
          }
  
          zoomStatus = status;
          zoomStatus.addObserver(this);
//          notify();
        }
      }
      
      /**
       * Sets starting point of the path
       * 
       * @param path drawn path
       */
      public void setPath(float x, float y)
      {
        synchronized (surfaceHolder) {
          bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
          int imageX = bitmap_coordinates.elementAt(0).intValue();
          int imageY = bitmap_coordinates.elementAt(1).intValue();
          draw_path.reset();
          draw_path.moveTo(imageX, imageY);
        }
      }
      
      /**
       * Sets the actual drawn path
       * 
       * @param path drawn path
       */
      public void setPath(float x, float y, float prev_x, float prev_y)
      {
        synchronized (surfaceHolder) {
          bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
          float imageX = bitmap_coordinates.elementAt(0).intValue();
          float imageY = bitmap_coordinates.elementAt(1).intValue();
          bitmap_coordinates = DrawFunctions.RealCoordinateValue(prev_x, prev_y, rectImage, rectCanvas);
          float prevImageX = bitmap_coordinates.elementAt(0).intValue();
          float prevImageY = bitmap_coordinates.elementAt(1).intValue();
        
          draw_path.quadTo(prevImageX, prevImageY, (imageX + prevImageX)/2, (imageY + prevImageY)/2);
        }
      }
      
      /**
       * Implement observer
       * 
       * redraws the surface
       */
      @Override
      public void update(Observable arg0, Object arg1) {
//        notify();
      }

      /**
       * Gets the pixel color
       * 
       * @param x Coordinate of the pixel
       * @param y Coordinate of the pixel
       */
      public void getPixelColor(float x, float y) {
        synchronized (surfaceHolder) {
          // Get real pixel coordinates on bitmap
          bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
          if (bitmap != null && zoomStatus != null) {
            try {
              int color = bitmap.getPixel(bitmap_coordinates.elementAt(0),
              bitmap_coordinates.elementAt(1));
              // Set the listener ColorChanged
              colorListener.colorChanged(color); 
            } catch (Exception e) {
            }
          }
        }
        invalidate();
      }

      /**
       * Called by the drawing surface listener on the touch up event.
       * 
       */
      protected void drawPathOnSurface(float x, float y) {
        synchronized (surfaceHolder) {
          if(draw_canvas == null)
          {
            Log.d("PAINTROID", "drawOnSurface: Bitmap not set");
            return;     
          }
          bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
          int imageX = bitmap_coordinates.elementAt(0).intValue();
          int imageY = bitmap_coordinates.elementAt(1).intValue();
          draw_path.lineTo(imageX, imageY);
          path_paint = DrawFunctions.setPaint(path_paint, current_shape, current_stroke, currentColor, useAntiAliasing);
          draw_canvas.drawPath(draw_path, path_paint);
          undo_redo_object.addPath(draw_path, path_paint);
        
          draw_path.reset();
        }
//        notify();
      }
      
      /**
       * Called by the drawing surface listener on the touch up event if no move appeared.
       * 
       */
      protected void drawPaintOnSurface(float x, float y) {
        synchronized (surfaceHolder) {
          if(draw_canvas == null)
          {
            Log.d("PAINTROID", "drawOnSurface: Bitmap not set");
            return;     
          }
          bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
          int imageX = bitmap_coordinates.elementAt(0).intValue();
          int imageY = bitmap_coordinates.elementAt(1).intValue();
          
          path_paint = DrawFunctions.setPaint(path_paint, current_shape, current_stroke, currentColor, useAntiAliasing);
          draw_canvas.drawPoint(imageX, imageY, path_paint);
          undo_redo_object.addPoint(imageX, imageY, path_paint);
          draw_path.reset();
        }
//        notify();
      }

      /**
       * Called by the drawing surface listener when using the magic wand tool.
       * 
       * @param x Screen coordinate
       * @param y Screen coordinate
       */
      protected void replaceColorOnSurface(float x, float y) {
        synchronized (surfaceHolder) {
          bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
          float imageX = bitmap_coordinates.elementAt(0);
          float imageY = bitmap_coordinates.elementAt(1);
  
          int end_x = bitmap.getWidth();
          int end_y = bitmap.getHeight();
  
          int chosen_pixel = bitmap.getPixel((int) imageX, (int) imageY);
  
          for (int x_replace = 0; x_replace < end_x; x_replace++) {
            for (int y_replace = 0; y_replace < end_y; y_replace++) {
              int bitmap_pixel = bitmap.getPixel(x_replace, y_replace);
  
              if (bitmap_pixel != currentColor
                  && bitmap_pixel == chosen_pixel) {
                bitmap.setPixel(x_replace, y_replace, currentColor);
              }
            }
          }
  
          undo_redo_object.addDrawing(bitmap);
          chosen_pixel = 0;
          setActionType(ActionType.NONE);
        }
//        notify();
      }

      /**
       * This method is called when something has to be drawn on screen.
       * 
       * Sets the image and canvas rectangles and draws the bitmap
       */
      protected void doDraw(Canvas canvas) {
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

        path_paint = DrawFunctions.setPaint(path_paint, current_shape, current_stroke, currentColor, useAntiAliasing);
        draw_canvas.drawPath(draw_path, path_paint);

        canvas.drawBitmap(bitmap, rectImage, rectCanvas, bitmap_paint);
      }

      public void undoOneStep()
      {
        Bitmap undoBitmap = undo_redo_object.undo();
        if(undoBitmap != null)
        {
            bitmap = undoBitmap;
            draw_canvas = new Canvas(bitmap);
            calculateAspect();
//            notify();
        }
      }
      
      public void redoOneStep()
      {
        Bitmap redoBitmap = undo_redo_object.redo();
        if(redoBitmap != null)
        {
          bitmap = redoBitmap;
            draw_canvas = new Canvas(bitmap);
            calculateAspect();
//            notify();
        }
      }
      
      public void clearUndoRedo()
      {
        undo_redo_object.clear();
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

      //------------------------------Methods For JUnit TESTING---------------------------------------
      
      public Vector<Integer> getPixelCoordinates(float x, float y)
      {
        synchronized (surfaceHolder) {
          bitmap_coordinates = DrawFunctions.RealCoordinateValue(x, y, rectImage, rectCanvas);
          int imageX = bitmap_coordinates.elementAt(0).intValue();
          int imageY = bitmap_coordinates.elementAt(1).intValue();
          imageX = imageX < 0 ? 0 : imageX;
          imageY = imageY < 0 ? 0 : imageY;
          imageX = imageX >= bitmap.getWidth() ? bitmap.getWidth()-1 : imageX;
          imageY = imageY >= bitmap.getHeight() ? bitmap.getHeight()-1 : imageY;
          bitmap_coordinates.setElementAt(imageX, 0);
          bitmap_coordinates.setElementAt(imageY, 1);
        }
        return bitmap_coordinates;
      }
      
      public int getPixelFromScreenCoordinates(float x, float y)
      {
        synchronized (surfaceHolder) {
          this.getPixelCoordinates(x, y);
          return bitmap.getPixel(bitmap_coordinates.elementAt(0).intValue(), bitmap_coordinates.elementAt(1).intValue());
        }
      }
      
  }
  
  /**
   * Custom Listener for Color-Pickup Event
   * 
   */
  public interface ColorPickupListener {
    void colorChanged(int color);
  }

  private DrawingSurfaceThread drawingThread;
  
	// What type of action is activated
	public enum ActionType {
		ZOOM, SCROLL, DRAW, CHOOSE, UNDO, REDO, NONE, MAGIC, RESET
	}

	

	// -----------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 */
	public DrawingSurface(Context context, AttributeSet attrs) {

		super(context, attrs);
		// register our interest in hearing about changes to our surface
    SurfaceHolder holder = getHolder();
    holder.addCallback(this);

    // create thread only; it's started in surfaceCreated()
    drawingThread = new DrawingSurfaceThread(holder, context);
	}

	// TODO remove
//	/**
//	 * Calculates aspect ration and calls super class method
//	 * 
//	 */
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//		calculateAspect();
//	}

	/**
	 * Calles when surface dimensions change
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/**
	 * Called when surface is created
	 */
	public void surfaceCreated(SurfaceHolder holder) {
	  drawingThread.setRunning(true);
	  drawingThread.start();
	}

	/**
	 * Called when surface is destroyed
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
    boolean retry = true;
    drawingThread.setRunning(false);
    while (retry) {
        try {
            drawingThread.join();
            retry = false;
        } catch (InterruptedException e) {
        }
    }
	}	
	
	public DrawingSurfaceThread getDrawingThread()
	{
	  return drawingThread;
	}

}
