/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.dialog.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import org.catrobat.paintroid.R;

public class HSVColorPickerView extends View implements ColorPickerContract.ColorPickerView {

	private float borderWidth;
	private float alphaPanelHeight;
	private float panelSpacing;
	private float satTrackerRadiusOuter;
	private float satTrackerRadiusInner;
	private float rectangleTrackerOffset;
	private float huePanelWidth;

	private Paint satValPaint;
	private Paint satValTrackerPaintInner;
	private Paint satValTrackerPaintOuter;
	private Paint huePaint;
	private Paint hueTrackerPaint;
	private Paint alphaPaint;
	private Paint borderPaint;
	private Paint checkeredPaint;

	private Shader valShader;
	private Shader satShader;
	private Shader hueShader;
	private Shader alphaShader;

	private int alpha = 0xff;
	private float hue = 360f;
	private float sat = 0f;
	private float val = 0f;

	private ColorPickerContract.ColorPickerViewListener onColorChangedListener;

	private float drawingOffset;

	private RectF drawingRect;
	private RectF satValRect;
	private RectF hueRect;
	private RectF alphaRect;

	private Point startTouchPoint = null;

	public HSVColorPickerView(Context context) {
		super(context);
		init();
	}

	public HSVColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HSVColorPickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private static int dp(Context context, int value) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
				context.getResources().getDisplayMetrics()));
	}

	private void init() {
		Context context = getContext();
		Resources resources = getResources();

		satTrackerRadiusOuter = resources.getDimension(R.dimen.pocketpaint_color_chooser_tracker_radius_outer);
		satTrackerRadiusInner = resources.getDimension(R.dimen.pocketpaint_color_chooser_tracker_radius_inner);
		rectangleTrackerOffset = resources.getDimension(R.dimen.pocketpaint_color_chooser_tracker_offset);
		float trackerStrokeWidth = resources.getDimension(R.dimen.pocketpaint_color_chooser_tracker_stroke_width);
		huePanelWidth = resources.getDimension(R.dimen.pocketpaint_color_chooser_hue_panel_width);
		alphaPanelHeight = resources.getDimension(R.dimen.pocketpaint_color_chooser_alpha_panel_height);
		panelSpacing = resources.getDimension(R.dimen.pocketpaint_color_chooser_panel_spacing);
		drawingOffset = resources.getDimension(R.dimen.pocketpaint_color_chooser_drawing_offset);
		borderWidth = resources.getDimension(R.dimen.pocketpaint_color_chooser_border_width);

		@ColorInt
		int borderColor = ContextCompat.getColor(context, R.color.pocketpaint_color_chooser_border);
		@ColorInt
		int sliderTrackerColor = ContextCompat.getColor(context, R.color.pocketpaint_color_chooser_slider_tracker);
		@ColorInt
		int satTrackerOuterColor = ContextCompat.getColor(context, R.color.pocketpaint_color_chooser_sat_tracker_outer);
		@ColorInt
		int satTrackerInnerColor = ContextCompat.getColor(context, R.color.pocketpaint_color_chooser_sat_tracker_inner);

		satValPaint = new Paint();
		satValTrackerPaintInner = new Paint();
		satValTrackerPaintOuter = new Paint();
		huePaint = new Paint();
		hueTrackerPaint = new Paint();
		alphaPaint = new Paint();
		borderPaint = new Paint();
		checkeredPaint = new Paint();

		borderPaint.setColor(borderColor);

		satValTrackerPaintInner.setStyle(Paint.Style.STROKE);
		satValTrackerPaintInner.setStrokeWidth(trackerStrokeWidth);
		satValTrackerPaintInner.setAntiAlias(true);
		satValTrackerPaintInner.setColor(satTrackerInnerColor);

		satValTrackerPaintOuter.setStyle(Paint.Style.STROKE);
		satValTrackerPaintOuter.setStrokeWidth(trackerStrokeWidth);
		satValTrackerPaintOuter.setAntiAlias(true);
		satValTrackerPaintOuter.setColor(satTrackerOuterColor);

		hueTrackerPaint.setStyle(Paint.Style.STROKE);
		hueTrackerPaint.setStrokeWidth(trackerStrokeWidth);
		hueTrackerPaint.setAntiAlias(true);
		hueTrackerPaint.setColor(sliderTrackerColor);

		Bitmap checkerboard = BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg);
		BitmapShader checkeredShader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkeredPaint.setShader(checkeredShader);

		drawingRect = new RectF();
		satValRect = new RectF();
		hueRect = new RectF();
		alphaRect = new RectF();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (drawingRect.width() <= 0 || drawingRect.height() <= 0) {
			return;
		}

		drawBorder(canvas, satValRect);
		drawSatValPanel(canvas);
		drawBorder(canvas, hueRect);
		drawHuePanel(canvas);
		drawBorder(canvas, alphaRect);
		drawAlphaPanel(canvas);
	}

	private void drawAlphaPanel(Canvas canvas) {
		canvas.drawRect(alphaRect, checkeredPaint);

		float[] hsv = new float[]{hue, sat, val};
		int color = Color.HSVToColor(hsv);
		int acolor = Color.HSVToColor(0, hsv);

		alphaShader = new LinearGradient(alphaRect.left, alphaRect.top, alphaRect.right,
				alphaRect.top, color, acolor, Shader.TileMode.CLAMP);
		alphaPaint.setShader(alphaShader);
		canvas.drawRect(alphaRect, alphaPaint);

		float rectWidth = dp(getContext(), 2);

		Point p = alphaToPoint(alpha);

		RectF r = new RectF();
		r.left = p.x - rectWidth;
		r.right = p.x + rectWidth;
		r.top = alphaRect.top - rectangleTrackerOffset;
		r.bottom = alphaRect.bottom + rectangleTrackerOffset;

		canvas.drawRoundRect(r, rectWidth, rectWidth, hueTrackerPaint);
	}

	private void drawSatValPanel(Canvas canvas) {
		if (valShader == null) {
			valShader = new LinearGradient(satValRect.left, satValRect.top, satValRect.left,
					satValRect.bottom, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
		}

		int rgb = Color.HSVToColor(new float[]{hue, 1f, 1f});

		satShader = new LinearGradient(satValRect.left, satValRect.top, satValRect.right,
				satValRect.bottom, Color.WHITE, rgb, Shader.TileMode.CLAMP);

		satValPaint.setXfermode(null);
		satValPaint.setShader(valShader);
		canvas.drawRect(satValRect, satValPaint);
		satValPaint.setShader(satShader);
		satValPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
		canvas.drawRect(satValRect, satValPaint);

		Point p = satValToPoint(sat, val);
		canvas.drawCircle(p.x, p.y, satTrackerRadiusInner, satValTrackerPaintInner);
		canvas.drawCircle(p.x, p.y, satTrackerRadiusOuter, satValTrackerPaintOuter);
	}

	private void drawHuePanel(Canvas canvas) {
		if (hueShader == null) {
			hueShader = new LinearGradient(hueRect.left, hueRect.top, hueRect.left, hueRect.bottom,
					buildHueColorArray(), null, Shader.TileMode.CLAMP);
			huePaint.setShader(hueShader);
		}

		canvas.drawRect(hueRect, huePaint);

		float rectHeight = dp(getContext(), 2);
		Point p = hueToPoint(hue);
		RectF r = new RectF();
		r.left = hueRect.left - rectangleTrackerOffset;
		r.right = hueRect.right + rectangleTrackerOffset;
		r.top = p.y - rectHeight;
		r.bottom = p.y + rectHeight;

		canvas.drawRoundRect(r, rectHeight, rectHeight, hueTrackerPaint);
	}

	private void drawBorder(Canvas canvas, RectF rect) {
		canvas.drawRect(rect.left - borderWidth,
				rect.top - borderWidth,
				rect.right + borderWidth,
				rect.bottom + borderWidth, borderPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean update = false;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startTouchPoint = new Point((int) event.getX(), (int) event.getY());
				update = moveTrackersIfNeeded(event);
				break;

			case MotionEvent.ACTION_MOVE:
				update = moveTrackersIfNeeded(event);
				break;

			case MotionEvent.ACTION_UP:
				startTouchPoint = null;
				update = moveTrackersIfNeeded(event);
				break;
		}

		if (update) {
			invalidate();
			onColorChanged();
			return true;
		}
		return super.onTouchEvent(event);
	}

	private boolean moveTrackersIfNeeded(MotionEvent event) {
		if (startTouchPoint == null) {
			return false;
		}
		boolean update = true;

		int startX = startTouchPoint.x;
		int startY = startTouchPoint.y;

		if (hueRect.contains(startX, startY)) {
			hue = pointToHue(event.getY());
		} else if (satValRect.contains(startX, startY)) {
			float[] result = pointToSatVal(event.getX(), event.getY());
			sat = result[0];
			val = result[1];
		} else if (alphaRect != null && alphaRect.contains(startX, startY)) {
			alpha = pointToAlpha((int) event.getX());
		} else {
			update = false;
		}

		return update;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		drawingRect.set(0, 0, w, h);
		drawingRect.inset(drawingOffset, drawingOffset);
		drawingRect.left += getPaddingLeft();
		drawingRect.right -= getPaddingRight();
		drawingRect.top += getPaddingTop();
		drawingRect.bottom -= getPaddingBottom();

		setUpSatValRect();
		setUpHueRect();
		setUpAlphaRect();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int size = MeasureSpec.getSize(widthMeasureSpec) - getPaddingStart() - getPaddingEnd();
		setMeasuredDimension(size, size);
	}

	private static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	private int pointToAlpha(int x) {
		final RectF rect = alphaRect;
		final int width = (int) rect.width();

		x = (int) clamp(x - rect.left, 0, width);
		return 0xff - (x * 0xff / width);
	}

	private Point satValToPoint(float sat, float val) {
		final RectF rect = satValRect;
		final float height = rect.height();
		final float width = rect.width();

		Point p = new Point();
		p.x = (int) (sat * width + rect.left);
		p.y = (int) ((1f - val) * height + rect.top);

		return p;
	}

	private Point hueToPoint(float hue) {
		final RectF rect = hueRect;
		final float height = rect.height();

		Point p = new Point();
		p.y = (int) (height - (hue * height / 360f) + rect.top);
		p.x = (int) rect.left;

		return p;
	}

	private Point alphaToPoint(int alpha) {
		final RectF rect = alphaRect;
		final float width = rect.width();

		Point p = new Point();
		p.x = (int) (width - (alpha * width / 0xff) + rect.left);
		p.y = (int) rect.top;

		return p;
	}

	private float[] pointToSatVal(float x, float y) {
		float width = satValRect.width();
		float height = satValRect.height();

		x = clamp(x - satValRect.left, 0, width);
		y = clamp(y - satValRect.top, 0, height);

		float[] result = new float[2];
		result[0] = 1.f / width * x;
		result[1] = 1.f - (1.f / height * y);

		return result;
	}

	private float pointToHue(float y) {
		final RectF rect = hueRect;
		float height = rect.height();

		y = clamp(y - rect.top, 0, height);
		return 360f - (y * 360f / height);
	}

	private int[] buildHueColorArray() {
		int[] hue = new int[361];

		int count = 0;
		for (int i = hue.length - 1; i >= 0; i--, count++) {
			hue[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
		}
		return hue;
	}

	private void setUpSatValRect() {
		float panelContentHeight = drawingRect.height() - 2 * borderWidth - panelSpacing - alphaPanelHeight;
		float panelContentWidth = drawingRect.width() - 2 * borderWidth - panelSpacing - huePanelWidth;

		float left = drawingRect.left + borderWidth;
		float top = drawingRect.top + borderWidth;
		float bottom = top + panelContentHeight;
		float right = left + panelContentWidth;
		satValRect.set(left, top, right, bottom);
	}

	private void setUpHueRect() {
		float left = drawingRect.right - huePanelWidth;
		float top = drawingRect.top;
		float bottom = drawingRect.bottom - (panelSpacing + alphaPanelHeight);
		float right = drawingRect.right;

		hueRect.set(left, top, right, bottom);
		hueRect.inset(borderWidth, borderWidth);
	}

	private void setUpAlphaRect() {
		alphaRect.set(drawingRect.left, 0, drawingRect.right, alphaPanelHeight);
		alphaRect.offset(0, drawingRect.bottom - alphaPanelHeight);
		alphaRect.inset(borderWidth, borderWidth);
	}

	@Override
	public void setOnColorChangedListener(ColorPickerContract.ColorPickerViewListener listener) {
		this.onColorChangedListener = listener;
	}

	private void onColorChanged() {
		if (onColorChangedListener != null) {
			onColorChangedListener.colorChanged(getSelectedColor());
		}
	}

	private int getSelectedColor() {
		float[] hsv = {hue, sat, val};
		return Color.HSVToColor(alpha, hsv);
	}

	@Override
	public void setSelectedColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		alpha = Color.alpha(color);
		hue = hsv[0];
		sat = hsv[1];
		val = hsv[2];

		invalidate();
	}
}
