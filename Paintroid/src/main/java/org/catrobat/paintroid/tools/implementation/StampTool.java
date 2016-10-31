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

package org.catrobat.paintroid.tools.implementation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class StampTool extends BaseToolWithRectangleShape {

	protected static final boolean ROTATION_ENABLED = true;
	protected static final boolean RESPECT_IMAGE_BOUNDS = false;

	protected static CreateAndSetBitmapAsyncTask mCreateAndSetBitmapAsync = null;

	private static final long LONG_CLICK_THRESHOLD_MILLIS = 1000;

	protected boolean mReadyForPaste = false;
	protected boolean mLongClickAllowed = true;

	private Toast mCopyHintToast;
	private CountDownTimer mDownTimer;
	private boolean mLongClickPerformed = false;

	public StampTool(Activity activity, ToolType toolType) {
		super(activity, toolType);
		mReadyForPaste = false;
		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BOUNDS);

		mDrawingBitmap = Bitmap.createBitmap((int) mBoxWidth, (int) mBoxHeight,
				Config.ARGB_8888);

		mCreateAndSetBitmapAsync = new CreateAndSetBitmapAsyncTask();
	}

	public void setBitmapFromFile(Bitmap bitmap) {
		super.setBitmap(bitmap);
		mReadyForPaste = true;
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

		canvasDraw.drawBitmap(tmpBitmap, rectSourceResult, rectDestResult, null);

		tmpCanvas = null;
		tmpBitmap.recycle();
		tmpBitmap = null;

		mReadyForPaste = true;
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
			mReadyForPaste = true;

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
	public boolean handleDown(PointF coordinate) {
		super.handleDown(coordinate);

		mLongClickPerformed = false;
		if (mLongClickAllowed) {
			mDownTimer = new CountDownTimer(LONG_CLICK_THRESHOLD_MILLIS, LONG_CLICK_THRESHOLD_MILLIS * 2) {
				@Override
				public void onTick(long millisUntilFinished) {
				}

				@Override
				public void onFinish() {
					if (CLICK_IN_BOX_MOVE_TOLERANCE >= mMovedDistance.x && CLICK_IN_BOX_MOVE_TOLERANCE >= mMovedDistance.y
							&& isCoordinateInsideBox(mPreviousEventCoordinate)) {
						mLongClickPerformed = true;
						onLongClickInBox();
					}
				}
			}.start();
		}
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (mLongClickPerformed) {
			return true;
		}
		return super.handleMove(coordinate);
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (mLongClickPerformed) {
			return true;
		}

		if (mLongClickAllowed) {
			mDownTimer.cancel();
		}

		return super.handleUp(coordinate);
	}

	@Override
	protected void onClickInBox() {
		if (!mReadyForPaste) {
			if (mCopyHintToast != null) {
				mCopyHintToast.cancel();
			}
			mCopyHintToast = Toast.makeText(mContext, mContext.getResources().getString(R.string.stamp_tool_copy_hint), Toast.LENGTH_SHORT);
			mCopyHintToast.show();
		} else if (mDrawingBitmap != null && !mDrawingBitmap.isRecycled()) {
			paste();
		}
	}

	protected void onLongClickInBox() {
		copy();
	}

	private void copy() {
		int bitmapHeight = PaintroidApplication.drawingSurface
				.getBitmapHeight();
		int bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		if (!((mToolPosition.x - mBoxWidth / 2 > bitmapWidth) || (mToolPosition.y - mBoxHeight / 2 > bitmapHeight)
				|| (mToolPosition.x + mBoxWidth / 2 < 0) || (mToolPosition.y + mBoxHeight / 2 < 0))) {

			if (mCreateAndSetBitmapAsync.getStatus() != AsyncTask.Status.RUNNING) {
				mCreateAndSetBitmapAsync = new CreateAndSetBitmapAsyncTask();
				mCreateAndSetBitmapAsync.execute();
			}
		}
	}

	private void paste() {
		Point intPosition = new Point((int) mToolPosition.x,
				(int) mToolPosition.y);

		int bitmapHeight = PaintroidApplication.drawingSurface
				.getBitmapHeight();
		int bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		if (!((mToolPosition.x - mBoxWidth / 2 > bitmapWidth) || (mToolPosition.y - mBoxHeight / 2 > bitmapHeight)
				|| (mToolPosition.x + mBoxWidth / 2 < 0) || (mToolPosition.y + mBoxHeight / 2 < 0))) {

			Command command = new StampCommand(mDrawingBitmap, intPosition,
					mBoxWidth, mBoxHeight, mBoxRotation);

			((StampCommand) command).addObserver(this);
			IndeterminateProgressDialog.getInstance().show();
			Layer layer = LayerListener.getInstance().getCurrentLayer();
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
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

			IndeterminateProgressDialog.getInstance().show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Log.e(PaintroidApplication.TAG, "------------doInBackground");
			createAndSetBitmap();
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing) {
			IndeterminateProgressDialog.getInstance().dismiss();
		}

	}

	protected void setOnLongClickAllowed(boolean longClickAllowed) {
		mLongClickAllowed = longClickAllowed;
	}
}
