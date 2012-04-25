/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catroid.paintroid.tools.implementation;

import java.util.Observable;

import org.catroid.paintroid.R;
import org.catroid.paintroid.dialog.BrushPickerDialog;
import org.catroid.paintroid.dialog.BrushPickerDialog.OnBrushChangedListener;
import org.catroid.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catroid.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import org.catroid.paintroid.tools.Tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

public abstract class BaseTool extends Observable implements Tool {
	// TODO maybe move to PaintroidApplication.
	public static final Paint CHECKERED_PATTERN = new Paint();

	protected Point position;
	protected final Paint bitmapPaint;
	protected final Paint canvasPaint;
	protected ToolType toolType;
	protected ColorPickerDialog colorPicker;
	protected BrushPickerDialog brushPicker;
	protected Context context;
	protected static final PorterDuffXfermode eraseXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	public BaseTool(Context context, ToolType toolType) {
		super();
		this.toolType = toolType;
		this.context = context;
		bitmapPaint = new Paint();
		bitmapPaint.setColor(Color.BLACK);
		bitmapPaint.setAntiAlias(true);
		bitmapPaint.setDither(true);
		bitmapPaint.setStyle(Paint.Style.STROKE);
		bitmapPaint.setStrokeJoin(Paint.Join.ROUND);
		bitmapPaint.setStrokeCap(Paint.Cap.ROUND);
		bitmapPaint.setStrokeWidth(Tool.stroke25);
		canvasPaint = new Paint(bitmapPaint);

		Bitmap checkerboard = BitmapFactory.decodeResource(context.getResources(), R.drawable.checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		CHECKERED_PATTERN.setShader(shader);

		final BaseTool self = this;
		OnColorPickedListener mColor = new OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				self.changePaintColor(color);
			}
		};

		colorPicker = new ColorPickerDialog(context, mColor);
		OnBrushChangedListener mStroke = new OnBrushChangedListener() {
			@Override
			public void setCap(Cap cap) {
				self.changePaintStrokeCap(cap);
			}

			@Override
			public void setStroke(int strokeWidth) {
				self.changePaintStrokeWidth(strokeWidth);
			}
		};

		brushPicker = new BrushPickerDialog(context, mStroke, canvasPaint);
		this.position = new Point(0, 0);
	}

	@Override
	public void changePaintColor(int color) {
		this.bitmapPaint.setColor(color);
		if (Color.alpha(color) == 0x00) {
			this.bitmapPaint.setXfermode(eraseXfermode);
			this.canvasPaint.reset();
			this.canvasPaint.setStyle(bitmapPaint.getStyle());
			this.canvasPaint.setStrokeJoin(bitmapPaint.getStrokeJoin());
			this.canvasPaint.setStrokeCap(bitmapPaint.getStrokeCap());
			this.canvasPaint.setStrokeWidth(bitmapPaint.getStrokeWidth());
			this.canvasPaint.setShader(CHECKERED_PATTERN.getShader());
		} else {
			this.bitmapPaint.setXfermode(null);
			this.canvasPaint.set(bitmapPaint);
		}
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeWidth(int strokeWidth) {
		this.bitmapPaint.setStrokeWidth(strokeWidth);
		this.canvasPaint.setStrokeWidth(strokeWidth);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeCap(Cap cap) {
		this.bitmapPaint.setStrokeCap(cap);
		this.canvasPaint.setStrokeCap(cap);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void setDrawPaint(Paint paint) {
		this.bitmapPaint.set(paint);
		this.canvasPaint.set(paint);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public Paint getDrawPaint() {
		return new Paint(this.bitmapPaint);
	}

	@Override
	public abstract void draw(Canvas canvas, boolean useCanvasTransparencyPaint);

	@Override
	public ToolType getToolType() {
		return this.toolType;
	}

	protected void showColorPicker() {
		colorPicker.show();
		colorPicker.setInitialColor(this.getDrawPaint().getColor());
	}

	protected void showBrushPicker() {
		brushPicker.show();
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {
		switch (buttonNumber) {
			case 1:
				showColorPicker();
				break;
			case 2:
				showBrushPicker();
				break;
		}
	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		if (buttonNumber == 0) {
			return R.drawable.ic_menu_more_64;
		} else if (buttonNumber == 1) {
			if (bitmapPaint.getColor() == Color.TRANSPARENT) {
				return R.drawable.transparent_64;
			}
		} else if (buttonNumber == 2) {
			int strokeWidth = (int) bitmapPaint.getStrokeWidth();
			switch (this.getDrawPaint().getStrokeCap()) {
				case SQUARE:
					if (strokeWidth < 25) {
						return R.drawable.rect_1_32;
					} else if (strokeWidth < 50) {
						return R.drawable.rect_2_32;
					} else if (strokeWidth < 75) {
						return R.drawable.rect_3_32;
					} else {
						return R.drawable.rect_4_32;
					}
				case ROUND:
					if (strokeWidth < 25) {
						return R.drawable.circle_1_32;
					} else if (strokeWidth < 50) {
						return R.drawable.circle_2_32;
					} else if (strokeWidth < 75) {
						return R.drawable.circle_3_32;
					} else {
						return R.drawable.circle_4_32;
					}
				default:
					break;
			}
		}
		return 0;
	}

	@Override
	public int getAttributeButtonColor(int buttonNumber) {
		if (buttonNumber == 1) {
			return bitmapPaint.getColor();
		}
		return Color.BLACK;
	}
}
