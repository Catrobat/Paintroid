/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.listener.BrushPickerView.OnBrushChangedListener;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;

import java.util.Observable;
import java.util.Observer;

public abstract class BaseTool extends Observable implements Tool, Observer {
	public static final Paint CHECKERED_PATTERN = new Paint();
	public static final float MOVE_TOLERANCE = 5;
	public static final int SCROLL_TOLERANCE_PERCENTAGE = 10;

	private static final int BACKGROUND_DEACTIVATED_DRAWING_SURFACE = Color.argb(0x80, 0, 0, 0);

	protected static Paint mBitmapPaint;
	protected static Paint mCanvasPaint;
	protected static boolean mToolOptionsShown = false;

	protected static LinearLayout mToolSpecificOptionsLayout;
	protected static LinearLayout mToolOptionsLayout;

	protected ToolType mToolType;
	protected Context mContext;
	protected PointF mMovedDistance;
	protected PointF mPreviousEventCoordinate;
	protected static int mScrollTolerance;

	private OnBrushChangedListener mStroke;
	protected OnColorPickedListener mColor;

	protected static final PorterDuffXfermode eraseXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	static {
		mBitmapPaint = new Paint();
		mBitmapPaint.setColor(Color.BLACK);
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
		mBitmapPaint.setStyle(Paint.Style.STROKE);
		mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
		mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
		mBitmapPaint.setStrokeWidth(Tool.stroke25);
		mCanvasPaint = new Paint(mBitmapPaint);
		Bitmap checkerboard = BitmapFactory.decodeResource(
				PaintroidApplication.applicationContext.getResources(),
				R.drawable.checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard,
				Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		CHECKERED_PATTERN.setShader(shader);
		WindowManager windowManager = (WindowManager) PaintroidApplication.applicationContext
				.getSystemService(Context.WINDOW_SERVICE);
		mScrollTolerance = windowManager.getDefaultDisplay().getWidth()
				* SCROLL_TOLERANCE_PERCENTAGE / 100;
	}

	public BaseTool(Context context, ToolType toolType) {
		super();
		mToolType = toolType;
		mContext = context;

		mColor = new OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				changePaintColor(color);
			}
		};

		mStroke = new OnBrushChangedListener() {
			@Override
			public void setCap(Cap cap) {
				changePaintStrokeCap(cap);
			}

			@Override
			public void setStroke(int strokeWidth) {
				changePaintStrokeWidth(strokeWidth);
			}
		};

		BrushPickerView.getInstance().addBrushChangedListener(mStroke);
		BrushPickerView.getInstance().setCurrentPaint(mBitmapPaint);
		ColorPickerDialog.getInstance().addOnColorPickedListener(mColor);

		mMovedDistance = new PointF(0f, 0f);
		mPreviousEventCoordinate = new PointF(0f, 0f);

		mToolOptionsLayout = (LinearLayout) ((Activity) context).findViewById(R.id.layout_tool_options);
		mToolSpecificOptionsLayout = (LinearLayout) ((Activity) context).findViewById(R.id.layout_tool_specific_options);
		resetAndInitializeToolOptions();
	}

	@Override
	public void changePaintColor(int color) {
		mBitmapPaint.setColor(color);
		if (Color.alpha(color) == 0x00) {
			mBitmapPaint.setXfermode(eraseXfermode);
			mCanvasPaint.reset();
			mCanvasPaint.setStyle(mBitmapPaint.getStyle());
			mCanvasPaint.setStrokeJoin(mBitmapPaint.getStrokeJoin());
			mCanvasPaint.setStrokeCap(mBitmapPaint.getStrokeCap());
			mCanvasPaint.setStrokeWidth(mBitmapPaint.getStrokeWidth());
			mCanvasPaint.setShader(CHECKERED_PATTERN.getShader());
			mCanvasPaint.setColor(Color.BLACK);
			mBitmapPaint.setAlpha(0x00);
			mCanvasPaint.setAlpha(0x00);
		} else {
			mBitmapPaint.setXfermode(null);
			mCanvasPaint.set(mBitmapPaint);
		}
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeWidth(int strokeWidth) {
		mBitmapPaint.setStrokeWidth(strokeWidth);
		mCanvasPaint.setStrokeWidth(strokeWidth);
		boolean antiAliasing = (strokeWidth > 1);
		mBitmapPaint.setAntiAlias(antiAliasing);
		mCanvasPaint.setAntiAlias(antiAliasing);

		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeCap(Cap cap) {
		mBitmapPaint.setStrokeCap(cap);
		mCanvasPaint.setStrokeCap(cap);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void setDrawPaint(Paint paint) {
		mBitmapPaint.set(paint);
		mCanvasPaint.set(paint);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public Paint getDrawPaint() {
		return new Paint(mBitmapPaint);
	}

	@Override
	public abstract void draw(Canvas canvas);

	@Override
	public ToolType getToolType() {
		return this.mToolType;
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof BaseCommand.NOTIFY_STATES &&
				(BaseCommand.NOTIFY_STATES.COMMAND_DONE == data ||
						BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data)) {

				IndeterminateProgressDialog.getInstance().dismiss();
				observable.deleteObserver(this);
		}
	}

	protected abstract void resetInternalState();

	@Override
	public void resetInternalState(StateChange stateChange) {
		if (getToolType().shouldReactToStateChange(stateChange)) {
			resetInternalState();
		}
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY, int viewWidth, int viewHeight) {
		int deltaX = 0;
		int deltaY = 0;

		if (pointX < mScrollTolerance) {
			deltaX = 1;
		}
		if (pointX > viewWidth - mScrollTolerance) {
			deltaX = -1;
		}

		if (pointY < mScrollTolerance) {
			deltaY = 1;
		}

		if (pointY > viewHeight - mScrollTolerance) {
			deltaY = -1;
		}

		return new Point(deltaX, deltaY);
	}

	protected boolean checkPathInsideBitmap(PointF coordinate) {
		if ((coordinate.x < PaintroidApplication.drawingSurface.getBitmapWidth()) &&
				(coordinate.y < PaintroidApplication.drawingSurface.getBitmapHeight()) &&
				(coordinate.x > 0) && (coordinate.y > 0)) {
			return true;
		}
		return false;
	}

	private void resetAndInitializeToolOptions() {
		mToolOptionsShown = false;
		((Activity)(mContext)).findViewById(R.id.main_tool_options).setVisibility(View.INVISIBLE);
		dimBackground(false);

		((Activity)(mContext)).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToolSpecificOptionsLayout.removeAllViews();
				TextView toolOptionsName = (TextView) mToolOptionsLayout.findViewById(R.id.layout_tool_options_name);
				toolOptionsName.setText(mContext.getResources().getString(mToolType.getNameResource()));
			}
		});

	}

	protected void addBrushPickerToToolOptions() {
		mToolSpecificOptionsLayout.addView(BrushPickerView.getInstance().getBrushPickerView());
	}

	@Override
	public boolean handleTouch(PointF coordinate, int motionEventType) {
		if (coordinate == null) {
			return false;
		}

		if (mToolOptionsShown) {
			if (motionEventType == MotionEvent.ACTION_UP) {
				PointF surfacePoint = PaintroidApplication.perspective.getSurfacePointFromCanvasPoint(coordinate);
				float toolOptionsOnSurfaceY = ((Activity)mContext).findViewById(R.id.main_tool_options).getY()
						- ((Activity)mContext).findViewById(R.id.toolbar).getHeight();
				if (surfacePoint.y < toolOptionsOnSurfaceY) {
					toggleShowToolOptions();
				}
			}
			return true;
		}

		switch (motionEventType) {
			case MotionEvent.ACTION_DOWN:
				return handleDown(coordinate);
			case MotionEvent.ACTION_MOVE:
				return handleMove(coordinate);
			case MotionEvent.ACTION_UP:
				return handleUp(coordinate);

			default:
				Log.e("Handling Touch Event", "Unexpected motion event!");
				return false;
		}
	}

	@Override
	public void hide(){
		LinearLayout mainToolOptions = (LinearLayout) ((Activity)(mContext)).findViewById(R.id.main_tool_options);
		mainToolOptions.setVisibility(View.GONE);
		dimBackground(false);
		mToolOptionsShown = false;
	}

	@Override
	public void toggleShowToolOptions() {
		LinearLayout mainToolOptions = (LinearLayout) ((Activity)(mContext)).findViewById(R.id.main_tool_options);
		LinearLayout mainBottomBar = (LinearLayout) ((Activity)(mContext)).findViewById(R.id.main_bottom_bar);
		int orientation = PaintroidApplication.applicationContext.getResources().getConfiguration().orientation;

		if (!mToolOptionsShown) {
			mainToolOptions.setY(mainBottomBar.getY() + mainBottomBar.getHeight());
			mainToolOptions.setVisibility(View.VISIBLE);
			float yPos = 0;
			if(orientation == Configuration.ORIENTATION_PORTRAIT) {
				yPos = mainBottomBar.getY() - mainToolOptions.getHeight();
			} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				yPos = mainBottomBar.getHeight() - mainToolOptions.getHeight();
			}
			mainToolOptions.animate().y(yPos);
			dimBackground(true);
			mToolOptionsShown = true;

		} else {
			mainToolOptions.animate().y(mainBottomBar.getY() + mainBottomBar.getHeight());
			dimBackground(false);
			mToolOptionsShown = false;
		}
	}

	void dimBackground(boolean darken) {
		View drawingSurfaceView = ((Activity)(mContext)).findViewById(R.id.drawingSurfaceView);
		int colorFrom = ((ColorDrawable) drawingSurfaceView.getBackground()).getColor();
		int colorTo;

		if (darken) {
			colorTo = BACKGROUND_DEACTIVATED_DRAWING_SURFACE;
		} else {
			colorTo = mContext.getResources().getColor(R.color.transparent);
		}

		ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(
				drawingSurfaceView, "backgroundColor", new ArgbEvaluator(), colorFrom, colorTo);
		backgroundColorAnimator.setDuration(250);
		backgroundColorAnimator.start();
	}

	@Override
	public boolean getToolOptionsAreShown() {
		return mToolOptionsShown;
	}

	@Override
	public void startTool() {
		BrushPickerView.getInstance().getDrawerPreview().invalidate();
		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}

	@Override
	public void leaveTool() {
		ColorPickerDialog.getInstance().removeOnColorPickedListener(mColor);
		BrushPickerView.getInstance().removeBrushChangedListener(mStroke);
	}

}
