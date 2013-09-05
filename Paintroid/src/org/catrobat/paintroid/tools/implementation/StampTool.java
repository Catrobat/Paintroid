/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
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
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

public class StampTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BOUNDS = false;
	private static CreateAndSetBitmapAsyncTask mCreateAndSetBitmapAsync = null;

	private boolean mStampActive = false;

	public StampTool(Context context, ToolType toolType) {
		super(context, toolType);

		mStampActive = false;
		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BOUNDS);

		mDrawingBitmap = Bitmap.createBitmap((int) mBoxWidth, (int) mBoxHeight,
				Config.ARGB_8888);

		mCreateAndSetBitmapAsync = new CreateAndSetBitmapAsyncTask();
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

	public void setBitmapFromFile(Bitmap bitmap) {
		super.setBitmap(bitmap);
		mStampActive = true;
	}

	private void createAndSetBitmapRotated() {
		float boxRotation = mBoxRotation;

		while (boxRotation < 0.0) {
			boxRotation = boxRotation + 90;
		}

		while (boxRotation > 90) {
			boxRotation = boxRotation - 90;
		}

		double rotationRadians = Math.toRadians(boxRotation);
		double boundingBoxX = mBoxWidth * Math.sin(rotationRadians)
				+ mBoxHeight * Math.cos(rotationRadians);

		double boundingBoxY = mBoxWidth * Math.cos(rotationRadians)
				+ mBoxHeight * Math.sin(rotationRadians);

		if (boundingBoxX < 0.0) {
			boundingBoxX = -boundingBoxX;
		}

		if (boundingBoxY < 0.0) {
			boundingBoxY = -boundingBoxY;
		}

		double distanceToMassCentre = Math.sqrt(Math.pow(
				(mToolPosition.x + boundingBoxX / 2), 2)
				+ Math.pow((mToolPosition.y + boundingBoxY / 2), 2));

		Bitmap tmpBitmap = Bitmap.createBitmap((int) distanceToMassCentre * 2,
				(int) distanceToMassCentre * 2, Config.ARGB_8888);

		Canvas tmpCanvas = new Canvas(tmpBitmap);

		Rect rectSource = new Rect((int) mToolPosition.x
				- (int) (distanceToMassCentre), (int) mToolPosition.y
				- (int) (distanceToMassCentre), (int) mToolPosition.x
				+ (int) (distanceToMassCentre), (int) mToolPosition.y
				+ (int) (distanceToMassCentre));

		Rect rectDest = new Rect(0, 0, (int) distanceToMassCentre * 2,
				(int) distanceToMassCentre * 2);

		tmpCanvas.save();
		tmpCanvas.rotate(-mBoxRotation, (float) (distanceToMassCentre),
				(float) (distanceToMassCentre));

		Bitmap copyOfCurrentDrawingSurfaceBitmap = PaintroidApplication.drawingSurface
				.getBitmapCopy();
		if (copyOfCurrentDrawingSurfaceBitmap == null
				|| copyOfCurrentDrawingSurfaceBitmap.isRecycled()) {
			return;
		}
		tmpCanvas.drawBitmap(copyOfCurrentDrawingSurfaceBitmap, rectSource,
				rectDest, null);
		copyOfCurrentDrawingSurfaceBitmap.recycle();
		copyOfCurrentDrawingSurfaceBitmap = null;

		tmpCanvas.restore();

		// now get tmp back to bitmap, rotate and clip
		if (canUseOldDrawingBitmap()) {
			mDrawingBitmap = Bitmap.createBitmap((int) mBoxWidth,
					(int) mBoxHeight, Config.ARGB_8888);
		}
		Canvas canvasDraw = new Canvas(mDrawingBitmap);

		double left = (distanceToMassCentre) - (mBoxWidth / 2);
		double top = (distanceToMassCentre) - (mBoxHeight / 2);
		double right = (distanceToMassCentre * 2) - left;
		double bottom = (distanceToMassCentre * 2) - top;
		Rect rectSourceResult = new Rect((int) left, (int) top, (int) right,
				(int) bottom);

		Rect rectDestResult = new Rect(0, 0, (int) mBoxWidth, (int) mBoxHeight);

		canvasDraw
				.drawBitmap(tmpBitmap, rectSourceResult, rectDestResult, null);

		tmpCanvas = null;
		tmpBitmap.recycle();
		tmpBitmap = null;

		mStampActive = true;
		System.gc();
	}

	protected void createAndSetBitmap() {
		if (mBoxRotation != 0.0) {
			createAndSetBitmapRotated();
			return;
		}

		if (canUseOldDrawingBitmap()) {
			mDrawingBitmap = Bitmap.createBitmap((int) mBoxWidth,
					(int) mBoxHeight, Config.ARGB_8888);
		}

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

			Bitmap copyOfCurrentDrawingSurfaceBitmap = PaintroidApplication.drawingSurface
					.getBitmapCopy();
			if (copyOfCurrentDrawingSurfaceBitmap == null
					|| copyOfCurrentDrawingSurfaceBitmap.isRecycled()) {
				copyOfCurrentDrawingSurfaceBitmap = null;
				return;
			}

			canvas.drawBitmap(copyOfCurrentDrawingSurfaceBitmap, rectSource,
					rectDest, null);
			copyOfCurrentDrawingSurfaceBitmap.recycle();
			copyOfCurrentDrawingSurfaceBitmap = null;
			mStampActive = true;

			Log.d(PaintroidApplication.TAG, "created bitmap");
		} catch (Exception e) {
			Log.e(PaintroidApplication.TAG,
					"error stamping bitmap " + e.getMessage());

			if (mDrawingBitmap != null) {
				mDrawingBitmap.recycle();
				mDrawingBitmap = null;
				System.gc();
			}
		}
	}

	@Override
	protected void onClickInBox() {
		if (mStampActive == false) {
			if (mCreateAndSetBitmapAsync.getStatus() != AsyncTask.Status.RUNNING) {
				mCreateAndSetBitmapAsync = new CreateAndSetBitmapAsyncTask();
				mCreateAndSetBitmapAsync.execute();
			}
		} else if (mDrawingBitmap != null && !mDrawingBitmap.isRecycled()) {
			Point intPosition = new Point((int) mToolPosition.x,
					(int) mToolPosition.y);
			Command command = new StampCommand(mDrawingBitmap, intPosition,
					mBoxWidth, mBoxHeight, mBoxRotation);
			((StampCommand) command).addObserver(this);
			mProgressDialog.show();
			PaintroidApplication.commandManager.commitCommand(command);
		}
	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
	}

	@Override
	public void resetInternalState() {
	}

	private boolean canUseOldDrawingBitmap() {
		if (mDrawingBitmap != null && !mDrawingBitmap.isRecycled()
				&& mDrawingBitmap.getWidth() == (int) mBoxWidth
				&& mDrawingBitmap.getHeight() == (int) mBoxHeight) {
			mDrawingBitmap.eraseColor(Color.TRANSPARENT);
			return false;
		}
		return true;
	}

	protected class CreateAndSetBitmapAsyncTask extends
			AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if (PaintroidApplication.drawingSurface
					.isDrawingSurfaceBitmapValid()) {
				createAndSetBitmap();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing) {
			mProgressDialog.dismiss();
		}

	}
}
