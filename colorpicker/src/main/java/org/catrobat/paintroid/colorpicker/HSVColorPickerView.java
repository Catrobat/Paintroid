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

package org.catrobat.paintroid.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HSVColorPickerView extends View {

	private static final float BORDER_WIDTH_PX = 1;

	private float panelSpacing = 10f;
	private float paletteCircleTrackerRadius = 5f;
	private float rectangleTrackerOffset = 2f;
	private float huePanelWidth = 30f;
	private float density = 1f;

	private Paint satValPaint;
	private Paint satValTrackerPaint;
	private Paint huePaint;
	private Paint hueTrackerPaint;
	private Paint borderPaint;

	private Shader valShader;
	private Shader hueShader;

	private int alpha = 0xff;
	private float hue = 360f;
	private float sat = 0f;
	private float val = 0f;

	private int borderColor = 0xff6E6E6E;

	private OnColorChangedListener onColorChangedListener;

	private float drawingOffset;

	private RectF drawingRect;
	private RectF satValRect;
	private RectF hueRect;

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

	private void init() {
		density = getContext().getResources().getDisplayMetrics().density;
		paletteCircleTrackerRadius *= density;
		rectangleTrackerOffset *= density;
		huePanelWidth *= density;
		panelSpacing *= density;
		drawingOffset = paletteCircleTrackerRadius;

		satValPaint = new Paint();
		satValTrackerPaint = new Paint();
		huePaint = new Paint();
		hueTrackerPaint = new Paint();
		borderPaint = new Paint();

		satValTrackerPaint.setStyle(Paint.Style.STROKE);
		satValTrackerPaint.setStrokeWidth(2f * density);
		satValTrackerPaint.setAntiAlias(true);

		int sliderTrackerColor = 0xfff5f5f5;
		hueTrackerPaint.setColor(sliderTrackerColor);
		hueTrackerPaint.setStyle(Paint.Style.STROKE);
		hueTrackerPaint.setStrokeWidth(2f * density);
		hueTrackerPaint.setAntiAlias(true);

		drawingRect = new RectF();
		satValRect = new RectF();
		hueRect = new RectF();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (drawingRect.width() <= 0 || drawingRect.height() <= 0) {
			return;
		}
		drawSatValPanel(canvas);
		drawHuePanel(canvas);
	}

	private void drawSatValPanel(Canvas canvas) {
		final RectF rect = satValRect;

		borderPaint.setColor(borderColor);
		canvas.drawRect(drawingRect.left, drawingRect.top, rect.right
						+ BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX,
				borderPaint);

		if (valShader == null) {
			valShader = new LinearGradient(rect.left, rect.top, rect.left,
					rect.bottom, 0xffffffff, 0xff000000, Shader.TileMode.CLAMP);
		}

		int rgb = Color.HSVToColor(new float[]{hue, 1f, 1f});

		Shader satShader = new LinearGradient(rect.left, rect.top, rect.right,
				rect.bottom, Color.WHITE, rgb, Shader.TileMode.CLAMP);

		satValPaint.setXfermode(null);
		satValPaint.setShader(valShader);
		canvas.drawRect(rect, satValPaint);
		satValPaint.setShader(satShader);
		satValPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
		canvas.drawRect(rect, satValPaint);

		Point p = satValToPoint(sat, val);
		satValTrackerPaint.setColor(0xff000000);
		canvas.drawCircle(p.x, p.y, paletteCircleTrackerRadius - 1f
				* density, satValTrackerPaint);

		satValTrackerPaint.setColor(0xffdddddd);
		canvas.drawCircle(p.x, p.y, paletteCircleTrackerRadius,
				satValTrackerPaint);
	}

	private void drawHuePanel(Canvas canvas) {
		final RectF rect = hueRect;

		borderPaint.setColor(borderColor);
		canvas.drawRect(rect.left - BORDER_WIDTH_PX, rect.top
						- BORDER_WIDTH_PX, rect.right + BORDER_WIDTH_PX,
				rect.bottom + BORDER_WIDTH_PX, borderPaint);

		if (hueShader == null) {
			hueShader = new LinearGradient(rect.left, rect.top, rect.left,
					rect.bottom, buildHueColorArray(), null,
					Shader.TileMode.CLAMP);
			huePaint.setShader(hueShader);
		}

		canvas.drawRect(rect, huePaint);

		float rectHeight = 4 * density / 2;
		Point p = hueToPoint(hue);
		RectF r = new RectF();
		r.left = rect.left - rectangleTrackerOffset;
		r.right = rect.right + rectangleTrackerOffset;
		r.top = p.y - rectHeight;
		r.bottom = p.y + rectHeight;

		canvas.drawRoundRect(r, 2, 2, hueTrackerPaint);
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
		} else {
			update = false;
		}

		return update;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		drawingRect = new RectF();
		drawingRect.left = drawingOffset + getPaddingLeft();
		drawingRect.right = w - drawingOffset - getPaddingRight();
		drawingRect.top = drawingOffset + getPaddingTop();
		drawingRect.bottom = h - drawingOffset - getPaddingBottom();

		setUpSatValRect();
		setUpHueRect();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int size = MeasureSpec.getSize(widthMeasureSpec) - getPaddingStart() - getPaddingEnd();
		setMeasuredDimension(size, size);
	}

	private static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
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
		final RectF dRect = drawingRect;
		float panelContentHeight = dRect.height() - 2 * BORDER_WIDTH_PX;
		float panelContentWidth = dRect.width() - 2 * BORDER_WIDTH_PX - panelSpacing - huePanelWidth;

		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = top + panelContentHeight;
		float right = left + panelContentWidth;
		satValRect.set(left, top, right, bottom);
	}

	private void setUpHueRect() {
		final RectF dRect = drawingRect;
		float left = dRect.right - huePanelWidth + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX;
		float right = dRect.right - BORDER_WIDTH_PX;

		hueRect.set(left, top, right, bottom);
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.onColorChangedListener = listener;
	}

	private void onColorChanged() {
		if (onColorChangedListener != null) {
			onColorChangedListener.colorChanged(getSelectedColor());
		}
	}

	public int getSelectedColor() {
		float[] hsv = {hue, sat, val};
		return Color.HSVToColor(alpha, hsv);
	}

	public void setSelectedColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		alpha = Color.alpha(color);
		hue = hsv[0];
		sat = hsv[1];
		val = hsv[2];

		invalidate();
	}

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}
}
