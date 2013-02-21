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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class StampTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BOUNDS = false;

	private boolean mStampActive = false;

	public StampTool(Context context, ToolType toolType) {
		super(context, toolType);

		mStampActive = false;
		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BOUNDS);

		mDrawingBitmap = Bitmap.createBitmap((int) mBoxWidth, (int) mBoxHeight,
				Config.ARGB_8888);
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return Color.TRANSPARENT;
		default:
			return super.getAttributeButtonColor(buttonNumber);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		// no clicks wanted
	}

	private void createAndSetBitmapRotated(DrawingSurface drawingSurface) {
		Log.d("BLARGL", "begin createAndSetBitmapRotated");
		float boxRot = mBoxRotation;

		while (boxRot < 0.0) {
			boxRot = boxRot + 90;
		}

		while (boxRot > 90) {
			boxRot = boxRot - 90;
		}

		double rotationRadians = Math.toRadians(boxRot);
		double a = mBoxWidth * Math.sin(rotationRadians) + mBoxHeight
				* Math.cos(rotationRadians);

		double b = mBoxWidth * Math.cos(rotationRadians) + mBoxHeight
				* Math.sin(rotationRadians);

		if (a < 0.0) {
			a = -a;
		}

		if (b < 0.0) {
			b = -b;
		}

		Bitmap tmpBitmap = Bitmap.createBitmap((int) a, (int) b,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(tmpBitmap);

		Rect rectSource = new Rect((int) mToolPosition.x - (int) (a / 2),
				(int) mToolPosition.y - (int) (b / 2), (int) mToolPosition.x
						+ (int) (a / 2), (int) mToolPosition.y + (int) (b / 2));

		Rect rectDest = new Rect(0, 0, (int) a, (int) b);

		canvas.save();
		canvas.rotate(-mBoxRotation, (float) (a / 2), (float) (b / 2));

		canvas.drawBitmap(drawingSurface.getBitmap(), rectSource, rectDest,
				null);

		canvas.restore();

		// now get tmp back to bitmap, rotate and clip
		mDrawingBitmap = Bitmap.createBitmap((int) mBoxWidth, (int) mBoxHeight,
				Config.ARGB_8888);

		Canvas canvasDraw = new Canvas(mDrawingBitmap);

		double left = (a / 2) - (mBoxWidth / 2);
		double top = (b / 2) - (mBoxHeight / 2);
		double right = a - left;
		double bottom = b - top;
		Rect rectSource2 = new Rect((int) left, (int) top, (int) right,
				(int) bottom);

		Rect rectDest2 = new Rect(0, 0, (int) mBoxWidth, (int) mBoxHeight);

		canvasDraw.drawBitmap(tmpBitmap, rectSource2, rectDest2, null);
		tmpBitmap.recycle();
		tmpBitmap = null;

		mStampActive = true;
		Log.d("BLARGL", "end createAndSetBitmapRotated");
	}

	protected void createAndSetBitmap(DrawingSurface drawingSurface) {
		Log.d("BLARGL", "begin createAndSet");
		if (mDrawingBitmap != null) {
			Log.d("BLARGL", "mDrawing == null");
			mDrawingBitmap.recycle();
			mDrawingBitmap = null;
		}

		if (mBoxRotation != 0.0) {
			createAndSetBitmapRotated(drawingSurface);
			return;
		}

		mDrawingBitmap = Bitmap.createBitmap((int) mBoxWidth, (int) mBoxHeight,
				Config.ARGB_8888);

		Log.d(PaintroidApplication.TAG, "clip bitmap");
		Point left_top_box_bitmapcoordinates = new Point((int) mToolPosition.x
				- (int) mBoxWidth / 2, (int) mToolPosition.y - (int) mBoxHeight
				/ 2);
		Point right_bottom_box_bitmapcoordinates = new Point(
				(int) mToolPosition.x + (int) mBoxWidth / 2,
				(int) mToolPosition.y + (int) mBoxHeight / 2);
		try {
			Canvas canvas = new Canvas(mDrawingBitmap);
			Rect rectSource = new Rect(left_top_box_bitmapcoordinates.x,
					left_top_box_bitmapcoordinates.y,
					left_top_box_bitmapcoordinates.x + (int) mBoxWidth,
					left_top_box_bitmapcoordinates.y + (int) mBoxHeight);
			Rect rectDest = new Rect(0, 0, right_bottom_box_bitmapcoordinates.x
					- left_top_box_bitmapcoordinates.x,
					right_bottom_box_bitmapcoordinates.y
							- left_top_box_bitmapcoordinates.y);
			canvas.drawBitmap(drawingSurface.getBitmap(), rectSource, rectDest,
					null);

			mStampActive = true;

			Log.d(PaintroidApplication.TAG, "created bitmap");
		} catch (Exception e) {
			Log.d("BLARGL", "exception");
			Log.e(PaintroidApplication.TAG,
					"error stamping bitmap " + e.getMessage());

			if (mDrawingBitmap != null) {
				mDrawingBitmap.recycle();
				mDrawingBitmap = null;
			}
		}
		Log.d("BLARGL", "end createAndSet");
	}

	@Override
	protected void onClickInBox() {
		if (mStampActive == false) {
			mProgressDialog.show();
			createAndSetBitmap(PaintroidApplication.DRAWING_SURFACE);
			mProgressDialog.dismiss();
		} else {
			Point intPosition = new Point((int) mToolPosition.x,
					(int) mToolPosition.y);
			Command command = new StampCommand(mDrawingBitmap, intPosition,
					mBoxWidth, mBoxHeight, mBoxRotation);
			((StampCommand) command).addObserver(this);
			mProgressDialog.show();
			PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
		}

	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
		// TODO Auto-generated method stub
	}

	@Override
	public void resetInternalState() {
		// TODO Auto-generated method stub
	}
}
