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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
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

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
	static final String TAG = "PAINTROID";

	public static class Perspective {
		public static float zoom = 1f;
		public static PointF zoomPivot = new PointF(0f, 0f);
		public static PointF scroll = new PointF(0f, 0f);
	}

	// translate coordinates onto the bitmap
	public Point translate2Image(float x, float y) {
		Point point = new Point();
		point.x = Math.round((x / Perspective.zoom) - rectImage.left);
		point.y = Math.round((y / Perspective.zoom) - rectImage.top);
		return point;
	}

	public void translate2Image(PointF point) {
		point.x = Math.round((point.x / Perspective.zoom) - rectImage.left);
		point.y = Math.round((point.y / Perspective.zoom) - rectImage.top);
	}

	public static final int STDWIDTH = 300;
	public static final int STDHEIGHT = 400;

	private volatile Bitmap workingBitmap;
	private Canvas workingCanvas;
	private Paint bitmapPaint;

	public enum Mode {
		DRAW, CURSOR, CENTERPOINT, FLOATINGBOX
	}

	private Mode activeMode;
	private ToolbarItem activeAction;

	private Rect rectImage;
	private Rect rectCanvas;

	private int activeColor;
	private Paint pathPaint;
	private Path pathToDraw;
	private Brush activeBrush;
	private Tool activeTool;
	private Point surfaceSize;
	private Point surfaceCenter;
	private UndoRedo undoRedoObject;
	private boolean useAntiAliasing = true;
	private BaseSurfaceListener drawingSurfaceListener;

	private BitmapDrawable checkeredBackground;

	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);

		drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
		drawingSurfaceListener.setSurface(this);
		setOnTouchListener(drawingSurfaceListener);

		undoRedoObject = new UndoRedo(context);

		rectImage = new Rect();
		rectCanvas = new Rect();

		surfaceSize = new Point(0, 0);
		surfaceCenter = new Point(0, 0);

		activeMode = Mode.DRAW;
		activeAction = ToolbarItem.BRUSH;
		activeTool = new Cursor();
		activeBrush = new Brush();

		pathToDraw = new Path();

		pathPaint = new Paint();
		pathPaint.setDither(true);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setStrokeJoin(Paint.Join.ROUND);
		DrawFunctions.setPaint(pathPaint, activeBrush.cap, activeBrush.stroke, activeColor, useAntiAliasing, null);

		workingCanvas = new Canvas();
		bitmapPaint = new Paint();
		bitmapPaint.setDither(true);
		newEmptyBitmap();

		setActiveColor(getResources().getColor(R.color.std_color));

		final Resources res = context.getResources();
		checkeredBackground = new BitmapDrawable(res, BitmapFactory.decodeResource(res, R.drawable.transparent));
		checkeredBackground.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	}

	public void setActionType(ToolbarItem type) {
		if (drawingSurfaceListener.getClass() != DrawingSurfaceListener.class) {
			if (activeTool instanceof Middlepoint) {
				toggleCenterpointMode();
			} else if (activeTool instanceof FloatingBox) {
				toggleFloatingBoxMode();
			} else {
				activeTool.deactivate();
				activeTool = new Cursor(activeTool);
				drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
				drawingSurfaceListener.setSurface(this);
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
		int dpWidth = DrawFunctions.dp2px(getContext(), STDWIDTH);
		int dpHeight = DrawFunctions.dp2px(getContext(), STDHEIGHT);
		Bitmap bitmap = Bitmap.createBitmap(dpWidth, dpHeight, Bitmap.Config.ARGB_8888);
		setBitmap(bitmap);
	}

	public void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			workingBitmap = bitmap;

			workingCanvas.setBitmap(workingBitmap);
			undoRedoObject.addDrawing(workingBitmap);

			rectImage.setEmpty();
			rectImage.right = workingBitmap.getWidth();
			rectImage.bottom = workingBitmap.getHeight();
			Perspective.zoomPivot.x = rectImage.exactCenterX();
			Perspective.zoomPivot.y = rectImage.exactCenterY();

			postInvalidate();
		} else {
			Log.e("PAINTROID", "Cannot set bitmap null, use clearBitmap() instead!");
		}
	}

	public void clearBitmap() {
		rectImage.setEmpty();
		workingBitmap.recycle();
		workingBitmap = null;
		invalidate();
	}

	public Bitmap getBitmap() {
		return workingBitmap;
	}

	public Rect getRectImage() {
		return rectImage;
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

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	public void getPixelColor(float x, float y) {
		Point coords = translate2Image(x, y);

		if (!coordinatesAreOnBitmap(coords.x, coords.y)) {
			return;
		}
		if (workingBitmap != null) {
			try {
				int color = workingBitmap.getPixel(coords.x, coords.y);
				colorListener.colorChanged(color);
			} catch (Exception e) {
			}
		}
		invalidate();
	}

	public void startPath(float x, float y) {
		Point coords = translate2Image(x, y);

		pathToDraw.reset();
		pathToDraw.moveTo(coords.x, coords.y);
		workingCanvas.drawPath(pathToDraw, pathPaint);

		invalidate();
	}

	public void updatePath(float x, float y, float prevX, float prevY) {
		Point coords = translate2Image(x, y);
		Point prevCoords = translate2Image(prevX, prevY);

		final float cx = (prevCoords.x + coords.x) / 2;
		final float cy = (prevCoords.y + coords.y) / 2;

		pathToDraw.quadTo(cx, cy, coords.x, coords.y);
		workingCanvas.drawPath(pathToDraw, pathPaint);

		invalidate();
	}

	public void drawPathOnSurface(float x, float y) {
		if (workingBitmap == null) {
			return;
		}

		Point coords = translate2Image(x, y);

		pathToDraw.lineTo(coords.x, coords.y);

		workingCanvas.drawPath(pathToDraw, pathPaint);
		invalidate();

		RectF pathBoundary = new RectF();
		pathToDraw.computeBounds(pathBoundary, true);
		RectF bitmapBoundary = new RectF(0, 0, workingBitmap.getWidth(), workingBitmap.getHeight());
		if (pathBoundary.intersect(bitmapBoundary)) {
			undoRedoObject.addPath(pathToDraw, pathPaint);
		}

		invalidate();
	}

	public void drawPointOnSurface(float x, float y) {
		if (workingCanvas == null) {
			Log.w("PAINTROID", "drawPointOnSurface: Bitmap not set");
			return;
		}

		Point coords = translate2Image(x, y);

		if (coordinatesAreOnBitmap(coords.x, coords.y)) {
			workingCanvas.drawPoint(coords.x, coords.y, pathPaint);
			undoRedoObject.addPoint(coords.x, coords.y, pathPaint);
		}
		pathToDraw.reset();

		invalidate();
	}

	public void replaceColorOnSurface(float x, float y) {
		Point coords = translate2Image(x, y);

		if (coordinatesAreOnBitmap(coords.x, coords.y)) {
			int chosen_pixel_color = workingBitmap.getPixel(coords.x, coords.y);

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
		if (workingBitmap == null) {
			return;
		}

		canvas.save();
		canvas.scale(Perspective.zoom, Perspective.zoom, Perspective.zoomPivot.x, Perspective.zoomPivot.y);

		final int bitmapWidth = workingBitmap.getWidth();
		final int bitmapHeight = workingBitmap.getHeight();

		rectImage.left = (int) (rectCanvas.left + Perspective.scroll.x);
		rectImage.top = (int) (rectCanvas.top + Perspective.scroll.y);
		rectImage.right = (rectImage.left + bitmapWidth);
		rectImage.bottom = (rectImage.top + bitmapHeight);

		// make a ckeckerboard pattern background
		checkeredBackground.setBounds(rectImage);
		checkeredBackground.draw(canvas);

		canvas.drawBitmap(workingBitmap, null, rectImage, bitmapPaint);
		canvas.restore();
		activeTool.draw(canvas, activeBrush.cap, activeBrush.stroke, activeColor);
	}

	public void resetPerspective() {
		Perspective.zoom = 1f;
		int dX = rectCanvas.width() - rectImage.width();
		int dY = rectCanvas.height() - 50 - rectImage.height(); // TODO: compensate for toolbar
		Perspective.scroll.x = dX / 2;
		Perspective.scroll.y = dY / 2;
		invalidate();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, "surface changed. width: " + width + " height: " + height);
		surfaceSize.x = width;
		surfaceSize.y = height;
		surfaceCenter.x = width / 2;
		surfaceCenter.y = height / 2;
		activeTool.setSurfaceSize(surfaceSize);

		rectCanvas.left = getLeft();
		rectCanvas.top = getTop();
		rectCanvas.right = getRight();
		rectCanvas.bottom = getBottom();

		resetPerspective();
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
			invalidate();
		}
	}

	public void redoOneStep() {
		Bitmap redoBitmap = undoRedoObject.redo();
		if (redoBitmap != null) {
			workingBitmap = redoBitmap;
			workingCanvas.setBitmap(workingBitmap);
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

	public boolean doubleTapEvent(float x, float y) {
		boolean eventUsed = activeTool.doubleTapEvent((int) x, (int) y);
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
		activeTool.setSurfaceSize(screenSize);
	}

	public boolean coordinatesAreOnBitmap(int imageX, int imageY) {
		if (workingBitmap == null) {
			return false;
		} else {
			return imageX >= 0 && imageY >= 0 && imageX < workingBitmap.getWidth()
					&& imageY < workingBitmap.getHeight();
		}
	}

	public void toggleCenterpointMode() {
		if (activeMode == Mode.CENTERPOINT) {
			Log.w(TAG, "center point mode OFF");
			activeTool.deactivate();
			activeTool = new Cursor(activeTool);
			drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
			activeMode = Mode.DRAW;
		} else {
			Log.w(TAG, "center point mode ON");
			activeTool = new Middlepoint(activeTool);
			drawingSurfaceListener = new ToolDrawingSurfaceListener(this.getContext(), activeTool);
			activeTool.activate(surfaceCenter);
			activeMode = Mode.CENTERPOINT;
		}
		drawingSurfaceListener.setSurface(this);
		drawingSurfaceListener.setControlType(activeAction);
		setOnTouchListener(drawingSurfaceListener);
		invalidate();
	}

	public void toggleFloatingBoxMode() {
		if (activeMode == Mode.FLOATINGBOX) {
			Log.w(TAG, "floating box mode OFF");
			activeTool.deactivate();
			activeTool = new Cursor(activeTool);
			drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
			activeMode = Mode.DRAW;
		} else {
			Log.w(TAG, "floating box mode ON");
			FloatingBox floatingBox = new FloatingBox(activeTool);
			activeTool = floatingBox;
			drawingSurfaceListener = new FloatingBoxDrawingSurfaceListener(this.getContext(), floatingBox);
			activeTool.activate(surfaceCenter);
			activeMode = Mode.FLOATINGBOX;
		}
		drawingSurfaceListener.setSurface(this);
		drawingSurfaceListener.setControlType(activeAction);
		setOnTouchListener(drawingSurfaceListener);
		postInvalidate(); // called by robotium too
	}

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

	public void addPng(String uri) {
		Bitmap newPng = DrawFunctions.createBitmapFromUri(uri);
		if (newPng != null) {
			addPng(newPng);
		}
	}

	public Point getPixelCoordinates(float x, float y) {
		Point coords = translate2Image(x, y);

		int imageX = coords.x;
		int imageY = coords.y;

		imageX = imageX < 0 ? 0 : imageX;
		imageY = imageY < 0 ? 0 : imageY;
		imageX = imageX >= workingBitmap.getWidth() ? workingBitmap.getWidth() - 1 : imageX;
		imageY = imageY >= workingBitmap.getHeight() ? workingBitmap.getHeight() - 1 : imageY;
		return new Point(imageX, imageY);
	}

	//------------------------------methods for testing---------------------------------------

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
