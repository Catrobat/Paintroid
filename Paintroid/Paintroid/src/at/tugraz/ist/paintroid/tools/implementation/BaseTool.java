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
import java.util.Observer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog.OnBrushChangedListener;
import at.tugraz.ist.paintroid.dialog.DialogProgressIntermediate;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

public abstract class BaseTool extends Observable implements Tool, Observer {
	// TODO maybe move to PaintroidApplication.
	public static final Paint CHECKERED_PATTERN = new Paint();
	protected static final int NO_BUTTON_RESOURCE = R.drawable.icon_menu_no_icon;

	protected final Paint mBitmapPaint;
	protected final Paint mCanvasPaint;
	protected ToolType mToolType;
	protected ColorPickerDialog mColorPickerDialog;
	protected BrushPickerDialog mBrushPickerDialog;
	protected Context mContext;
	protected PointF mMovedDistance;
	protected PointF mPreviousEventCoordinate;
	protected static Dialog mProgressDialog;

	protected static final PorterDuffXfermode eraseXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	public BaseTool(Context context, ToolType toolType) {
		super();
		mToolType = toolType;
		mContext = context;
		mBitmapPaint = new Paint();
		mBitmapPaint.setColor(Color.BLACK);
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
		mBitmapPaint.setStyle(Paint.Style.STROKE);
		mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
		mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
		mBitmapPaint.setStrokeWidth(Tool.stroke25);
		mCanvasPaint = new Paint(mBitmapPaint);

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

		mColorPickerDialog = new ColorPickerDialog(context, mColor);
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

		mBrushPickerDialog = new BrushPickerDialog(context, mStroke, mCanvasPaint);
		mMovedDistance = new PointF(0f, 0f);
		mPreviousEventCoordinate = new PointF(0f, 0f);
		mProgressDialog = new DialogProgressIntermediate(context);

	}

	@Override
	public void changePaintColor(int color) {
		this.mBitmapPaint.setColor(color);
		if (Color.alpha(color) == 0x00) {

			mBitmapPaint.setXfermode(eraseXfermode);
			mCanvasPaint.reset();
			mCanvasPaint.setStyle(mBitmapPaint.getStyle());
			mCanvasPaint.setStrokeJoin(mBitmapPaint.getStrokeJoin());
			mCanvasPaint.setStrokeCap(mBitmapPaint.getStrokeCap());
			mCanvasPaint.setStrokeWidth(mBitmapPaint.getStrokeWidth());
			mCanvasPaint.setShader(CHECKERED_PATTERN.getShader());
			mBitmapPaint.setAlpha(0x00);
			mCanvasPaint.setAlpha(0x00);

		} else {
			this.mBitmapPaint.setXfermode(null);
			this.mCanvasPaint.set(mBitmapPaint);
		}
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeWidth(int strokeWidth) {
		this.mBitmapPaint.setStrokeWidth(strokeWidth);
		this.mCanvasPaint.setStrokeWidth(strokeWidth);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void changePaintStrokeCap(Cap cap) {
		this.mBitmapPaint.setStrokeCap(cap);
		this.mCanvasPaint.setStrokeCap(cap);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public void setDrawPaint(Paint paint) {
		this.mBitmapPaint.set(paint);
		this.mCanvasPaint.set(paint);
		super.setChanged();
		super.notifyObservers();
	}

	@Override
	public Paint getDrawPaint() {
		return new Paint(this.mBitmapPaint);
	}

	@Override
	public abstract void draw(Canvas canvas, boolean useCanvasTransparencyPaint);

	@Override
	public ToolType getToolType() {
		return this.mToolType;
	}

	protected void showColorPicker() {
		mColorPickerDialog.show();
		mColorPickerDialog.setInitialColor(this.getDrawPaint().getColor());
	}

	protected void showBrushPicker() {
		mBrushPickerDialog.show();
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		// no default action
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
			case BUTTON_ID_TOOL:
				switch (mToolType) {
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
						return R.drawable.icon_menu_eraser;
					case FLIP:
						return R.drawable.icon_menu_flip_horizontal;
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
				return mBitmapPaint.getColor();
			default:
				return Color.BLACK;

		}
	}

	protected int getStrokeWidthResource() {
		int strokeWidth = (int) mBitmapPaint.getStrokeWidth();
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
		if (mBitmapPaint.getColor() == Color.TRANSPARENT) {
			return R.drawable.checkeredbg_repeat;
		} else {
			return NO_BUTTON_RESOURCE;
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof BaseCommand.NOTIFY_STATES) {
			if (BaseCommand.NOTIFY_STATES.COMMAND_DONE == data || BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data) {
				mProgressDialog.dismiss();
				observable.deleteObserver(this);
			}
		}
	}

}
