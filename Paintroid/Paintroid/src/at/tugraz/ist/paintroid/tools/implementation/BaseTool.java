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

package at.tugraz.ist.paintroid.tools.implementation;

import java.util.Observable;

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
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog.OnBrushChangedListener;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

public abstract class BaseTool extends Observable implements Tool {
	// TODO maybe move to PaintroidApplication.
	public static final Paint CHECKERED_PATTERN = new Paint();
	protected static final int NO_BUTTON_RESOURCE = 0;

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
			this.bitmapPaint.setAlpha(0x00);
			this.canvasPaint.setAlpha(0x00);
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
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		// no default action
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
			case BUTTON_ID_TOOL:
				switch (toolType) {
					case BRUSH:
						return R.drawable.icon_menu_brush;
					case CROP:
						return R.drawable.icon_menu_crop;
					case CURSOR:
						return R.drawable.icon_menu_cursor;
					case MAGIC:
						return R.drawable.icon_menu_magic;
					case PIPETTE:
						return R.drawable.icon_menu_pipette;
					case STAMP:
						return R.drawable.icon_menu_stamp;
					case ERASER:
						return R.drawable.ic_menu_more_eraser_64;
					default:
						return R.drawable.icon_menu_brush;
				}
			default:
				return NO_BUTTON_RESOURCE;
		}
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
			case BUTTON_ID_PARAMETER_TOP_2:
				return bitmapPaint.getColor();
			default:
				return Color.BLACK;
		}
	}

	protected int getStrokeWidthResource() {
		int strokeWidth = (int) bitmapPaint.getStrokeWidth();
		if (strokeWidth < 25) {
			return R.drawable.icon_menu_stroke_width_1;
		} else if (strokeWidth < 50) {
			return R.drawable.icon_menu_stroke_width_2;
		} else if (strokeWidth < 75) {
			return R.drawable.icon_menu_stroke_width_3;
		} else {
			return R.drawable.icon_menu_stroke_width_4;
		}
	}

	protected int getStrokeColorResource() {
		if (bitmapPaint.getColor() == Color.TRANSPARENT) {
			return R.drawable.transparent_32;
		} else {
			return NO_BUTTON_RESOURCE;
		}
	}

}
