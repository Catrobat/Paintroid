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

package org.catrobat.paintroid.dialog.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.implementation.BaseTool;


public class HSVColorPickerView extends View {

	private final static float BORDER_WIDTH_PX = 1;
	private float ALPHA_PANEL_HEIGHT = 21f;
	private float PANEL_SPACING = 10f;
	private float PALETTE_CIRCLE_TRACKER_RADIUS = 5f;
	private float RECTANGLE_TRACKER_OFFSET = 2f;
	private float HUE_PANEL_WIDTH = 30f;
	private float mDensity = 1f;

	private Paint mSatValPaint;
	private Paint mSatValTrackerPaint;
	private Paint mHuePaint;
	private Paint mHueTrackerPaint;
	private Paint mAlphaPaint;
	private Paint mBorderPaint;
	private Paint mCheckeredPaint;

	private Shader mValShader;
	private Shader mSatShader;
	private Shader mHueShader;
	private Shader mAlphaShader;

	private int mAlpha = 0xff;
	private float mHue = 360f;
	private float mSat = 0f;
	private float mVal = 0f;

	private int mSliderTrackerColor = 0xfff5f5f5;
	private int mBorderColor = 0xff6E6E6E;

	private OnColorChangedListener mOnColorChangedListener;

	private float mDrawingOffset;

	private RectF mDrawingRect;
	private RectF mSatValRect;
	private RectF mHueRect;
	private RectF mAlphaRect;

	private Point mStartTouchPoint = null;

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
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mDensity = getContext().getResources().getDisplayMetrics().density;
		PALETTE_CIRCLE_TRACKER_RADIUS *= mDensity;
		RECTANGLE_TRACKER_OFFSET *= mDensity;
		HUE_PANEL_WIDTH *= mDensity;
		ALPHA_PANEL_HEIGHT *= mDensity;
		PANEL_SPACING *= mDensity;
		mDrawingOffset = PALETTE_CIRCLE_TRACKER_RADIUS;

		mSatValPaint = new Paint();
		mSatValTrackerPaint = new Paint();
		mHuePaint = new Paint();
		mHueTrackerPaint = new Paint();
		mAlphaPaint = new Paint();
		mBorderPaint = new Paint();

		mSatValTrackerPaint.setStyle(Paint.Style.STROKE);
		mSatValTrackerPaint.setStrokeWidth(2f * mDensity);
		mSatValTrackerPaint.setAntiAlias(true);

		mHueTrackerPaint.setColor(mSliderTrackerColor);
		mHueTrackerPaint.setStyle(Paint.Style.STROKE);
		mHueTrackerPaint.setStrokeWidth(2f * mDensity);
		mHueTrackerPaint.setAntiAlias(true);

		mCheckeredPaint = new Paint(BaseTool.CHECKERED_PATTERN);
		Bitmap checkerboard = BitmapFactory.decodeResource(
				PaintroidApplication.applicationContext.getResources(), R.drawable.checkeredbg);
		BitmapShader checkeredShader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		mCheckeredPaint.setShader(checkeredShader);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mDrawingRect.width() <= 0 || mDrawingRect.height() <= 0) {
			return;
		}
		drawSatValPanel(canvas);
		drawHuePanel(canvas);
		drawAlphaPanel(canvas);
	}

	private void drawAlphaPanel(Canvas canvas) {
		if (mAlphaRect == null) {
			return;
		}

		final RectF rect = mAlphaRect;

		if (BORDER_WIDTH_PX > 0) {
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(rect.left - BORDER_WIDTH_PX, rect.top
					- BORDER_WIDTH_PX, rect.right + BORDER_WIDTH_PX,
					rect.bottom + BORDER_WIDTH_PX, mBorderPaint);
		}

		canvas.drawRect(rect, mCheckeredPaint);

		float[] hsv = new float[]{mHue, mSat, mVal};
		int color = Color.HSVToColor(hsv);
		int acolor = Color.HSVToColor(0, hsv);

		mAlphaShader = new LinearGradient(rect.left, rect.top, rect.right,
				rect.top, color, acolor, Shader.TileMode.CLAMP);
		mAlphaPaint.setShader(mAlphaShader);
		canvas.drawRect(rect, mAlphaPaint);

		float rectWidth = 4 * mDensity / 2;

		Point p = alphaToPoint(mAlpha);

		RectF r = new RectF();
		r.left = p.x - rectWidth;
		r.right = p.x + rectWidth;
		r.top = rect.top - RECTANGLE_TRACKER_OFFSET;
		r.bottom = rect.bottom + RECTANGLE_TRACKER_OFFSET;

		canvas.drawRoundRect(r, 2, 2, mHueTrackerPaint);

	}

	private void drawSatValPanel(Canvas canvas) {
		final RectF rect = mSatValRect;

		// draw border
		if (BORDER_WIDTH_PX > 0) {
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(mDrawingRect.left, mDrawingRect.top, rect.right
					+ BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX,
					mBorderPaint);
		}

		if (mValShader == null) {
			mValShader = new LinearGradient(rect.left, rect.top, rect.left,
					rect.bottom, 0xffffffff, 0xff000000, Shader.TileMode.CLAMP);
		}

		int rgb = Color.HSVToColor(new float[]{mHue, 1f, 1f});

		mSatShader = new LinearGradient(rect.left, rect.top, rect.right,
				rect.bottom, Color.WHITE, rgb, Shader.TileMode.CLAMP);
		ComposeShader mShader = new ComposeShader(mValShader, mSatShader,
				PorterDuff.Mode.MULTIPLY);
		mSatValPaint.setShader(mShader);

		canvas.drawRect(rect, mSatValPaint);

		// draw the picker`s tracker
		Point p = satValToPoint(mSat, mVal);
		mSatValTrackerPaint.setColor(0xff000000);
		canvas.drawCircle(p.x, p.y, PALETTE_CIRCLE_TRACKER_RADIUS - 1f
				* mDensity, mSatValTrackerPaint);

		mSatValTrackerPaint.setColor(0xffdddddd);
		canvas.drawCircle(p.x, p.y, PALETTE_CIRCLE_TRACKER_RADIUS,
				mSatValTrackerPaint);

	}

	private void drawHuePanel(Canvas canvas) {
		final RectF rect = mHueRect;

		if (BORDER_WIDTH_PX > 0) {
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(rect.left - BORDER_WIDTH_PX, rect.top
					- BORDER_WIDTH_PX, rect.right + BORDER_WIDTH_PX,
					rect.bottom + BORDER_WIDTH_PX, mBorderPaint);
		}

		if (mHueShader == null) {
			mHueShader = new LinearGradient(rect.left, rect.top, rect.left,
					rect.bottom, buildHueColorArray(), null,
					Shader.TileMode.CLAMP);
			mHuePaint.setShader(mHueShader);
		}

		canvas.drawRect(rect, mHuePaint);

		float rectHeight = 4 * mDensity / 2;
		Point p = hueToPoint(mHue);
		RectF r = new RectF();
		r.left = rect.left - RECTANGLE_TRACKER_OFFSET;
		r.right = rect.right + RECTANGLE_TRACKER_OFFSET;
		r.top = p.y - rectHeight;
		r.bottom = p.y + rectHeight;

		canvas.drawRoundRect(r, 2, 2, mHueTrackerPaint);

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean update = false;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mStartTouchPoint = new Point((int) event.getX(), (int) event.getY());
				update = moveTrackersIfNeeded(event);
				break;

			case MotionEvent.ACTION_MOVE:
				update = moveTrackersIfNeeded(event);
				break;

			case MotionEvent.ACTION_UP:
				mStartTouchPoint = null;
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
		if (mStartTouchPoint == null) {
			return false;
		}
		boolean update = false;

		int startX = mStartTouchPoint.x;
		int startY = mStartTouchPoint.y;

		if (mHueRect.contains(startX, startY)) {
			mHue = pointToHue(event.getY());
			update = true;
		} else if (mSatValRect.contains(startX, startY)) {
			float[] result = pointToSatVal(event.getX(), event.getY());
			mSat = result[0];
			mVal = result[1];
			update = true;
		} else if (mAlphaRect != null && mAlphaRect.contains(startX, startY)) {
			mAlpha = pointToAlpha((int) event.getX());
			update = true;
		}

		return update;

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mDrawingRect = new RectF();
		mDrawingRect.left = mDrawingOffset + getPaddingLeft();
		mDrawingRect.right = w - mDrawingOffset - getPaddingRight();
		mDrawingRect.top = mDrawingOffset + getPaddingTop();
		mDrawingRect.bottom = h - mDrawingOffset - getPaddingBottom();

		setUpSatValRect();
		setUpHueRect();
		setUpAlphaRect();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 0;
		int height = 0;

		int widthAllowed = MeasureSpec.getSize(widthMeasureSpec);
		int heightAllowed = MeasureSpec.getSize(heightMeasureSpec);

		width = (int) (heightAllowed - ALPHA_PANEL_HEIGHT + HUE_PANEL_WIDTH);

		if (width > widthAllowed) {
			width = widthAllowed;
			height = (int) (widthAllowed - HUE_PANEL_WIDTH + ALPHA_PANEL_HEIGHT);
		} else {
			height = heightAllowed;
		}

		setMeasuredDimension(width, height);
	}

	private int pointToAlpha(int x) {
		final RectF rect = mAlphaRect;
		final int width = (int) rect.width();

		if (x < rect.left) {
			x = 0;
		} else if (x > rect.right) {
			x = width;
		} else {
			x = x - (int) rect.left;
		}

		return 0xff - (x * 0xff / width);
	}


	private Point satValToPoint(float sat, float val) {
		final RectF rect = mSatValRect;
		final float height = rect.height();
		final float width = rect.width();

		Point p = new Point();
		p.x = (int) (sat * width + rect.left);
		p.y = (int) ((1f - val) * height + rect.top);

		return p;
	}

	private Point hueToPoint(float hue) {
		final RectF rect = mHueRect;
		final float height = rect.height();

		Point p = new Point();
		p.y = (int) (height - (hue * height / 360f) + rect.top);
		p.x = (int) rect.left;

		return p;
	}

	private Point alphaToPoint(int alpha) {
		final RectF rect = mAlphaRect;
		final float width = rect.width();

		Point p = new Point();
		p.x = (int) (width - (alpha * width / 0xff) + rect.left);
		p.y = (int) rect.top;

		return p;
	}

	private float[] pointToSatVal(float x, float y) {

		final RectF rect = mSatValRect;
		float[] result = new float[2];

		float width = rect.width();
		float height = rect.height();

		if (x < rect.left) {
			x = 0f;
		} else if (x > rect.right) {
			x = width;
		} else {
			x = x - rect.left;
		}

		if (y < rect.top) {
			y = 0f;
		} else if (y > rect.bottom) {
			y = height;
		} else {
			y = y - rect.top;
		}

		result[0] = 1.f / width * x;
		result[1] = 1.f - (1.f / height * y);

		return result;
	}

	private float pointToHue(float y) {
		final RectF rect = mHueRect;
		float height = rect.height();

		if (y < rect.top) {
			y = 0f;
		} else if (y > rect.bottom) {
			y = height;
		} else {
			y = y - rect.top;
		}

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
		final RectF dRect = mDrawingRect;
		float panelContentLength = dRect.height() - BORDER_WIDTH_PX * 2;

		panelContentLength -= PANEL_SPACING + ALPHA_PANEL_HEIGHT;
		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = top + panelContentLength;
		float right = left + panelContentLength;
		mSatValRect = new RectF(left, top, right, bottom);
	}

	private void setUpHueRect() {
		final RectF dRect = mDrawingRect;
		float left = dRect.right - HUE_PANEL_WIDTH + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX
				- (PANEL_SPACING + ALPHA_PANEL_HEIGHT);
		float right = dRect.right - BORDER_WIDTH_PX;

		mHueRect = new RectF(left, top, right, bottom);
	}

	private void setUpAlphaRect() {
		final RectF dRect = mDrawingRect;
		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.bottom - ALPHA_PANEL_HEIGHT + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX;
		float right = dRect.right - BORDER_WIDTH_PX;

		mAlphaRect = new RectF(left, top, right, bottom);

	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.mOnColorChangedListener = listener;
	}

	public interface OnColorChangedListener {
		public void colorChanged(int color);
	}

	private void onColorChanged() {
		if (mOnColorChangedListener != null) {
			mOnColorChangedListener.colorChanged(getSelectedColor());
		}
	}

	public int getSelectedColor() {
		float[] hsv = {mHue, mSat, mVal};
		return Color.HSVToColor(mAlpha, hsv);
	}

	public void setSelectedColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color,hsv);
		mAlpha = Color.alpha(color);
		mHue = hsv[0];
		mSat = hsv[1];
		mVal = hsv[2];

		invalidate();
	}


}
