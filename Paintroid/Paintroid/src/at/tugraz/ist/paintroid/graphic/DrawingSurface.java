/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.graphic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Cap;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.graphic.listeners.BaseSurfaceListener;
import at.tugraz.ist.paintroid.graphic.listeners.DrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.listeners.FloatingBoxDrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.listeners.ToolDrawingSurfaceListener;
import at.tugraz.ist.paintroid.graphic.utilities.Brush;
import at.tugraz.ist.paintroid.graphic.utilities.Cursor;
import at.tugraz.ist.paintroid.graphic.utilities.DrawFunctions;
import at.tugraz.ist.paintroid.graphic.utilities.FloatingBox;
import at.tugraz.ist.paintroid.graphic.utilities.Tool;
import at.tugraz.ist.paintroid.graphic.utilities.UndoRedo;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;
import at.tugraz.ist.paintroid.helper.Toolbar;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
	static final String TAG = "PAINTROID";

	public static class Perspective {
		public static float zoom = 1f;
		public static PointF pivot = new PointF(0f, 0f);
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

	private Toolbar toolbar;

	private BaseSurfaceListener drawingSurfaceListener;

	private BitmapDrawable checkeredBackground;

	public boolean antialiasingFlag = true;

	// Current selected action
	ToolType action = ToolType.BRUSH;

	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);

		drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
		drawingSurfaceListener.setSurface(this);
		setOnTouchListener(drawingSurfaceListener);

		undoRedoObject = new UndoRedo(context);

		rectImage = new Rect(0, 0, STDWIDTH, STDHEIGHT);
		final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		rectCanvas = new Rect(0, 0, metrics.widthPixels, metrics.heightPixels);

		surfaceSize = new Point(0, 0);
		surfaceCenter = new Point(0, 0);

		activeTool = new Cursor();
		activeBrush = new Brush();

		pathToDraw = new Path();

		toolbar = null;

		pathPaint = new Paint();
		pathPaint.setDither(true);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setStrokeJoin(Paint.Join.ROUND);
		DrawFunctions.setPaint(pathPaint, activeBrush.cap, activeBrush.stroke, activeColor, antialiasingFlag, null);

		workingCanvas = new Canvas();
		bitmapPaint = new Paint();
		bitmapPaint.setDither(true);
		newEmptyBitmap();

		setActiveColor(getResources().getColor(R.color.std_color));

		action = ToolType.BRUSH;

		final Resources res = context.getResources();
		checkeredBackground = new BitmapDrawable(res, BitmapFactory.decodeResource(res, R.drawable.transparent));
		checkeredBackground.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	}

	/**
	 * Sets the toolbar from the gui
	 * 
	 * @param toolbar
	 *            to set
	 * @param attributeButton2
	 */
	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}

	/**
	 * Returns the type of the activated tool
	 * 
	 * @return current tool
	 */
	public ToolType getToolType() {
		return action;
	}

	public void setToolType(ToolType type) {
		if (drawingSurfaceListener.getClass() != DrawingSurfaceListener.class) {
			if (activeTool instanceof FloatingBox) {
				deactivateFloatingBox();
			} else {
				activeTool.deactivate();
				activeTool = new Cursor(activeTool);
				drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
				drawingSurfaceListener.setSurface(this);
				setOnTouchListener(drawingSurfaceListener);
			}
			invalidate();
		}
		if (type != ToolType.NONE) {
			drawingSurfaceListener.setControlType(type);
		}
		action = type;
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
			resetPerspective();
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

	public void fillWithTransparency() {
		Paint transparent = new Paint();
		transparent.setColor(Color.TRANSPARENT);
		transparent.setXfermode(DrawFunctions.transparencyXferMode);
		workingCanvas.drawPaint(transparent);
		postInvalidate();
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

		pathToDraw.quadTo(prevCoords.x, prevCoords.y, cx, cy);
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

		/* TODO: didn't work correctly with setXfermode (problem e.g black vs. transparent) */

		if (coordinatesAreOnBitmap(coords.x, coords.y)) {
			int chosen_pixel_color = workingBitmap.getPixel(coords.x, coords.y);
			int bitmapWidth = workingBitmap.getWidth();
			int bitmapHeight = workingBitmap.getHeight();
			int bitmapLength = bitmapHeight * bitmapWidth;

			int[] pixArray = new int[bitmapLength];

			workingBitmap.getPixels(pixArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
			for (int index = 0; index < bitmapLength; index++) {
				if (chosen_pixel_color == pixArray[index]) {
					pixArray[index] = activeColor;
				}
			}
			workingBitmap.setPixels(pixArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
			undoRedoObject.addDrawing(workingBitmap);

			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (workingBitmap == null) {
			return;
		}

		canvas.save();
		canvas.scale(Perspective.zoom, Perspective.zoom);

		rectImage.offsetTo((int) Perspective.scroll.x, (int) Perspective.scroll.y);

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
		postInvalidate();
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

		invalidate();
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
		if (activeTool.getState() == ToolState.INACTIVE && action != ToolType.BRUSH) {
			return true;
		}
		if (action == ToolType.FLOATINGBOX && ((FloatingBox) activeTool).hasBitmap()) {
			toolbar.activateFloatingBoxButtons();
		}
		return eventUsed;
	}

	public boolean doubleTapEvent(float x, float y) {
		boolean eventUsed = activeTool.doubleTapEvent((int) x, (int) y);
		if (eventUsed) {
			switch (activeTool.getState()) {
				case INACTIVE:
					action = ToolType.BRUSH;
					drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
					break;
				case ACTIVE:
					action = ToolType.CURSOR;
					drawingSurfaceListener = new ToolDrawingSurfaceListener(this.getContext(), activeTool);
			}
			toolbar.setTool(action);
			drawingSurfaceListener.setSurface(this);
			drawingSurfaceListener.setControlType(action);
			setOnTouchListener(drawingSurfaceListener);
			invalidate();
		}
		return eventUsed;
	}

	public void activateCursor() {
		if (action != ToolType.BRUSH) {
			activeTool.deactivate();
			activeTool = new Cursor(activeTool);
		}
		action = ToolType.CURSOR;
		drawingSurfaceListener = new ToolDrawingSurfaceListener(this.getContext(), activeTool);
		((Cursor) activeTool).activate();
		drawingSurfaceListener.setSurface(this);
		drawingSurfaceListener.setControlType(action);
		setOnTouchListener(drawingSurfaceListener);
		invalidate();
	}

	public void paintChanged() {
		DrawFunctions.setPaint(pathPaint, activeBrush.cap, activeBrush.stroke, activeColor, antialiasingFlag, null);
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

	public void activateFloatingBox() {
		switch (action) {
			case FLOATINGBOX:
				break;
			default:
				changeFloatingBox();
				break;
		}
	}

	public void deactivateFloatingBox() {
		switch (action) {
			case FLOATINGBOX:
				changeFloatingBox();
				break;
			default:
				break;
		}
	}

	protected void changeFloatingBox() {
		switch (action) {
			case FLOATINGBOX:
				activeTool.deactivate();
				activeTool = new Cursor(activeTool);
				drawingSurfaceListener = new DrawingSurfaceListener(this.getContext());
				action = ToolType.BRUSH;
				break;
			default:
				FloatingBox floatingBox = new FloatingBox(activeTool);
				activeTool = floatingBox;
				drawingSurfaceListener = new FloatingBoxDrawingSurfaceListener(this.getContext(), floatingBox);
				activeTool.activate();
				action = ToolType.FLOATINGBOX;
				break;
		}
		drawingSurfaceListener.setSurface(this);
		drawingSurfaceListener.setControlType(action);
		setOnTouchListener(drawingSurfaceListener);
		postInvalidate(); // called by robotium too
	}

	public Point getCenter() {
		return surfaceCenter;
	}

	public void addPng(String uri) {
		Bitmap newPng = DrawFunctions.createBitmapFromUri(uri);
		if (newPng == null) {
			return;
		}
		addPng(newPng);
	}

	public void addPng(Bitmap newPng) {
		if (action != ToolType.FLOATINGBOX) {
			activateFloatingBox();
		}
		FloatingBox floatingBox = (FloatingBox) activeTool;
		floatingBox.reset();
		floatingBox.addBitmap(newPng);
		toolbar.activateFloatingBoxButtons();
		//called by robotium too
		postInvalidate();
	}

	public boolean rotateFloatingBox(int degree) {
		if (action != ToolType.FLOATINGBOX) {
			return false;
		}
		FloatingBox floatingBox = (FloatingBox) activeTool;
		boolean worked = floatingBox.rotate(degree);
		postInvalidate();
		return worked;
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

	public int getPixelFromScreenCoordinates(float x, float y) {
		Point coordinates = this.getPixelCoordinates(x, y);
		return workingBitmap.getPixel(coordinates.x, coordinates.y);
	}

	public ToolState getToolState() {
		return activeTool.getState();
	}

	public Point getToolCoordinates() {
		return new Point(activeTool.getPosition());
	}

	public Point getFloatingBoxSize() {
		if (action == ToolType.FLOATINGBOX && activeTool instanceof FloatingBox) {
			FloatingBox floatingBox = (FloatingBox) activeTool;
			int width = floatingBox.getWidth();
			int height = floatingBox.getHeight();
			return new Point(width, height);
		}
		return null;
	}

	public float getFloatingBoxRotation() {
		if (action == ToolType.FLOATINGBOX && activeTool instanceof FloatingBox) {
			FloatingBox floatingBox = (FloatingBox) activeTool;
			return floatingBox.getRotation();
		}
		return 0;
	}
}
