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

public abstract class BaseTool extends Observable implements Tool, Observer {
	// TODO maybe move to PaintroidApplication.
	public static final Paint CHECKERED_PATTERN = new Paint();
	public static final int INDEX_BUTTON_MAIN = 0;
	public static final int INDEX_BUTTON_ATTRIBUTE_1 = 1;
	public static final int INDEX_BUTTON_ATTRIBUTE_2 = 2;

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
			if (mBitmapPaint.getColor() == Color.TRANSPARENT) {
				return R.drawable.transparent_64;
			}
		} else if (buttonNumber == 2) {
			int strokeWidth = (int) mBitmapPaint.getStrokeWidth();
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
			return mBitmapPaint.getColor();
		}
		return Color.BLACK;
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
