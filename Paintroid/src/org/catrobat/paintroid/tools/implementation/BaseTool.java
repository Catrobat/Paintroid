/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import java.util.Observable;
import java.util.Observer;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.dialog.BrushPickerDialog;
import org.catrobat.paintroid.dialog.BrushPickerDialog.OnBrushChangedListener;
import org.catrobat.paintroid.dialog.DialogProgressIntermediate;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Statusbar.ToolButtonIDs;

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

public abstract class BaseTool extends Observable implements Tool, Observer {
	// TODO maybe move to PaintroidApplication.
	public static final Paint CHECKERED_PATTERN = new Paint();
	protected static final int NO_BUTTON_RESOURCE = R.drawable.icon_menu_no_icon;
	public static final float MOVE_TOLERANCE = 5;

	protected static Paint mBitmapPaint;
	protected static Paint mCanvasPaint;
	protected ToolType mToolType;
	protected Context mContext;
	protected PointF mMovedDistance;
	protected PointF mPreviousEventCoordinate;
	protected static Dialog mProgressDialog;

	private OnBrushChangedListener mStroke;
	protected OnColorPickedListener mColor;

	protected static final PorterDuffXfermode eraseXfermode = new PorterDuffXfermode(
			PorterDuff.Mode.CLEAR);

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
	}

	public BaseTool(Context context, ToolType toolType) {
		super();
		mToolType = toolType;
		mContext = context;
		Bitmap checkerboard = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard,
				Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		CHECKERED_PATTERN.setShader(shader);

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

		BrushPickerDialog.getInstance().addBrushChangedListener(mStroke);
		ColorPickerDialog.getInstance().addOnColorPickedListener(mColor);

		mMovedDistance = new PointF(0f, 0f);
		mPreviousEventCoordinate = new PointF(0f, 0f);
		mProgressDialog = new DialogProgressIntermediate(context);

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

	protected void showColorPicker() {
		ColorPickerDialog.getInstance().addOnColorPickedListener(mColor);
		ColorPickerDialog.getInstance().show();
		ColorPickerDialog.getInstance().setInitialColor(
				getDrawPaint().getColor());

	}

	protected void showBrushPicker() {
		BrushPickerDialog.getInstance().addBrushChangedListener(mStroke);
		BrushPickerDialog.getInstance().setCurrentPaint(mBitmapPaint);
		BrushPickerDialog.getInstance().show(
				((MainActivity) mContext).getSupportFragmentManager(),
				"brushpicker");
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
			case FILL:
				return R.drawable.icon_menu_fill;
			case PIPETTE:
				return R.drawable.icon_menu_pipette;
			case RECT:
				return R.drawable.icon_menu_rectangle;
			case STAMP:
				return R.drawable.icon_menu_stamp;
			case ERASER:
				return R.drawable.icon_menu_eraser;
			case FLIP:
				return R.drawable.icon_menu_flip_horizontal;
			case MOVE:
				return R.drawable.icon_menu_move;
			case ZOOM:
				return R.drawable.icon_menu_zoom;
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
		case BUTTON_ID_PARAMETER_TOP:
			return mBitmapPaint.getColor();
		default:
			return Color.BLACK;

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
			if (BaseCommand.NOTIFY_STATES.COMMAND_DONE == data
					|| BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data) {
				mProgressDialog.dismiss();
				observable.deleteObserver(this);
			}
		}
	}

}
