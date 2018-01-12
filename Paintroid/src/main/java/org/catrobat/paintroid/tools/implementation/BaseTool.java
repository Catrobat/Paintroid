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
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.VisibleForTesting;
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
import org.catrobat.paintroid.ui.DrawingSurface;

import java.util.Observable;
import java.util.Observer;

public abstract class BaseTool extends Observable implements Tool, Observer {
	public static final Paint CHECKERED_PATTERN = new Paint();
	public static final float MOVE_TOLERANCE = 5;
	public static final int SCROLL_TOLERANCE_PERCENTAGE = 10;
	protected static final PorterDuffXfermode ERASE_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	private static final int BACKGROUND_DEACTIVATED_DRAWING_SURFACE = Color.argb(0x80, 0, 0, 0);
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public static Paint bitmapPaint;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public static Paint canvasPaint;
	protected static boolean toolOptionsShown = false;
	protected static LinearLayout toolSpecificOptionsLayout;
	protected static LinearLayout toolOptionsLayout;
	protected static int scrollTolerance;

	static {
		bitmapPaint = new Paint();
		bitmapPaint.setColor(Color.BLACK);
		bitmapPaint.setAntiAlias(true);
		bitmapPaint.setDither(true);
		bitmapPaint.setStyle(Paint.Style.STROKE);
		bitmapPaint.setStrokeJoin(Paint.Join.ROUND);
		bitmapPaint.setStrokeCap(Paint.Cap.ROUND);
		bitmapPaint.setStrokeWidth(Tool.STROKE_25);
		canvasPaint = new Paint(bitmapPaint);
		Bitmap checkerboard = BitmapFactory.decodeResource(
				PaintroidApplication.applicationContext.getResources(),
				R.drawable.checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard,
				Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		CHECKERED_PATTERN.setShader(shader);
		WindowManager windowManager = (WindowManager) PaintroidApplication.applicationContext
				.getSystemService(Context.WINDOW_SERVICE);
		scrollTolerance = windowManager.getDefaultDisplay().getWidth()
				* SCROLL_TOLERANCE_PERCENTAGE / 100;
	}

	protected ToolType toolType;
	protected Context context;
	protected PointF movedDistance;
	protected PointF previousEventCoordinate;
	protected OnColorPickedListener color;
	private OnBrushChangedListener stroke;

	public BaseTool(Context context, ToolType toolType) {
		super();
		this.toolType = toolType;
		this.context = context;

		color = new OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				changePaintColor(color);
			}
		};

		stroke = new OnBrushChangedListener() {
			@Override
			public void setCap(Cap cap) {
				changePaintStrokeCap(cap);
			}

			@Override
			public void setStroke(int strokeWidth) {
				changePaintStrokeWidth(strokeWidth);
			}
		};

		BrushPickerView.getInstance().addBrushChangedListener(stroke);
		BrushPickerView.getInstance().setCurrentPaint(bitmapPaint);
		ColorPickerDialog.getInstance().addOnColorPickedListener(color);

		movedDistance = new PointF(0f, 0f);
		previousEventCoordinate = new PointF(0f, 0f);

		toolOptionsLayout = (LinearLayout) ((Activity) context).findViewById(R.id.layout_tool_options);
		toolSpecificOptionsLayout = (LinearLayout) ((Activity) context).findViewById(R.id.layout_tool_specific_options);
		resetAndInitializeToolOptions();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
	}

	@Override
	public void changePaintColor(@ColorInt int color) {
		setPaintColor(color);
		super.setChanged();
		super.notifyObservers();
	}

	void setPaintColor(@ColorInt int color) {
		bitmapPaint.setColor(color);
		if (Color.alpha(color) == 0x00) {
			bitmapPaint.setXfermode(ERASE_XFERMODE);
			canvasPaint.reset();
			canvasPaint.setStyle(bitmapPaint.getStyle());
			canvasPaint.setStrokeJoin(bitmapPaint.getStrokeJoin());
			canvasPaint.setStrokeCap(bitmapPaint.getStrokeCap());
			canvasPaint.setStrokeWidth(bitmapPaint.getStrokeWidth());
			canvasPaint.setShader(CHECKERED_PATTERN.getShader());
			canvasPaint.setColor(Color.BLACK);
			bitmapPaint.setAlpha(0x00);
			canvasPaint.setAlpha(0x00);
		} else {
			bitmapPaint.setXfermode(null);
			canvasPaint.set(bitmapPaint);
		}
	}

	@Override
	public void changePaintStrokeWidth(int strokeWidth) {
		bitmapPaint.setStrokeWidth(strokeWidth);
		canvasPaint.setStrokeWidth(strokeWidth);
		boolean antiAliasing = (strokeWidth > 1);
		bitmapPaint.setAntiAlias(antiAliasing);
		canvasPaint.setAntiAlias(antiAliasing);

		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeCap(Cap cap) {
		bitmapPaint.setStrokeCap(cap);
		canvasPaint.setStrokeCap(cap);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public Paint getDrawPaint() {
		return new Paint(bitmapPaint);
	}

	@Override
	public void setDrawPaint(Paint paint) {
		bitmapPaint.set(paint);
		canvasPaint.set(paint);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public abstract void draw(Canvas canvas);

	@Override
	public ToolType getToolType() {
		return this.toolType;
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof BaseCommand.NotifyStates
				&& (BaseCommand.NotifyStates.COMMAND_DONE == data || BaseCommand.NotifyStates.COMMAND_FAILED == data)) {

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

		if (pointX < scrollTolerance) {
			deltaX = 1;
		}
		if (pointX > viewWidth - scrollTolerance) {
			deltaX = -1;
		}

		if (pointY < scrollTolerance) {
			deltaY = 1;
		}

		if (pointY > viewHeight - scrollTolerance) {
			deltaY = -1;
		}

		return new Point(deltaX, deltaY);
	}

	protected boolean checkPathInsideBitmap(PointF coordinate) {
		final DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
		return (coordinate.x < drawingSurface.getBitmapWidth())
				&& (coordinate.y < drawingSurface.getBitmapHeight())
				&& (coordinate.x > 0) && (coordinate.y > 0);
	}

	private void resetAndInitializeToolOptions() {
		toolOptionsShown = false;
		((Activity) (context)).findViewById(R.id.main_tool_options).setVisibility(View.INVISIBLE);
		dimBackground(false);

		((Activity) (context)).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				toolSpecificOptionsLayout.removeAllViews();
				TextView toolOptionsName = (TextView) toolOptionsLayout.findViewById(R.id.layout_tool_options_name);
				toolOptionsName.setText(context.getResources().getString(toolType.getNameResource()));
			}
		});
	}

	protected void addBrushPickerToToolOptions() {
		toolSpecificOptionsLayout.addView(BrushPickerView.getInstance().getBrushPickerView());
	}

	@Override
	public boolean handleTouch(PointF coordinate, int motionEventType) {
		if (coordinate == null) {
			return false;
		}

		if (toolOptionsShown) {
			if (motionEventType == MotionEvent.ACTION_UP) {
				PointF surfacePoint = PaintroidApplication.perspective.getSurfacePointFromCanvasPoint(coordinate);
				float toolOptionsOnSurfaceY = ((Activity) context).findViewById(R.id.main_tool_options).getY()
						- ((Activity) context).findViewById(R.id.toolbar).getHeight();
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
	public void hide() {
		LinearLayout mainToolOptions = (LinearLayout) ((Activity) (context)).findViewById(R.id.main_tool_options);
		mainToolOptions.setVisibility(View.GONE);
		dimBackground(false);
		toolOptionsShown = false;
	}

	@Override
	public void toggleShowToolOptions() {
		LinearLayout mainToolOptions = (LinearLayout) ((Activity) (context)).findViewById(R.id.main_tool_options);
		LinearLayout mainBottomBar = (LinearLayout) ((Activity) (context)).findViewById(R.id.main_bottom_bar);
		int orientation = PaintroidApplication.applicationContext.getResources().getConfiguration().orientation;

		if (!toolOptionsShown) {
			mainToolOptions.setY(mainBottomBar.getY() + mainBottomBar.getHeight());
			mainToolOptions.setVisibility(View.VISIBLE);
			float yPos = 0;
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				yPos = mainBottomBar.getY() - mainToolOptions.getHeight();
			} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				yPos = mainBottomBar.getHeight() - mainToolOptions.getHeight();
			}
			mainToolOptions.animate().y(yPos);
			dimBackground(true);
			toolOptionsShown = true;
		} else {
			mainToolOptions.animate().y(mainBottomBar.getY() + mainBottomBar.getHeight());
			dimBackground(false);
			toolOptionsShown = false;
		}
	}

	void dimBackground(boolean darken) {
		View drawingSurfaceView = ((Activity) (context)).findViewById(R.id.drawingSurfaceView);
		int colorFrom = ((ColorDrawable) drawingSurfaceView.getBackground()).getColor();
		int colorTo;

		if (darken) {
			colorTo = BACKGROUND_DEACTIVATED_DRAWING_SURFACE;
		} else {
			colorTo = context.getResources().getColor(R.color.transparent);
		}

		ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(
				drawingSurfaceView, "backgroundColor", new ArgbEvaluator(), colorFrom, colorTo);
		backgroundColorAnimator.setDuration(250);
		backgroundColorAnimator.start();
	}

	@Override
	public boolean getToolOptionsAreShown() {
		return toolOptionsShown;
	}

	@Override
	public void startTool() {
		BrushPickerView.getInstance().getDrawerPreview().invalidate();
		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}

	@Override
	public void leaveTool() {
		ColorPickerDialog.getInstance().removeOnColorPickedListener(color);
		BrushPickerView.getInstance().removeBrushChangedListener(stroke);
	}
}
