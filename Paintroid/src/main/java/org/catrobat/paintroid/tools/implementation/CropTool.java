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

import java.util.Observable;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.CropCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CropTool extends BaseToolWithRectangleShape {

	private static final float START_ZOOM_FACTOR = 0.95f;
	private static final boolean ROTATION_ENABLED = false;
	private static final boolean RESPECT_IMAGE_BORDERS = true;
	private static final boolean RESIZE_POINTS_VISIBLE = false;

	private float mCropBoundWidthXLeft;
	private float mCropBoundWidthXRight = 0;
	private float mCropBoundHeightYTop;
	private float mCropBoundHeightYBottom = 0;
	private int mIntermediateCropBoundWidthXLeft;
	private int mIntermediateCropBoundWidthXRight;
	private int mIntermediateCropBoundHeightYTop;
	private int mIntermediateCropBoundHeightYBottom;

	private boolean mCropRunFinished = false;
	private static FindCroppingCoordinatesAsyncTask mFindCroppingCoordinates = null;

	public CropTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BORDERS);
		setResizePointsVisible(RESIZE_POINTS_VISIBLE);

		mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask();
		mFindCroppingCoordinates.execute();
		mBoxHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		mBoxWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		mToolPosition.x = mBoxWidth / 2f;
		mToolPosition.y = mBoxHeight / 2f;

	}

	@Override
	public void resetInternalState() {
		if (mFindCroppingCoordinates.getStatus() != AsyncTask.Status.RUNNING) {
			mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask();
			mFindCroppingCoordinates.execute();
		}
	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
		if (mCropRunFinished) {
			mLinePaint.setColor(mPrimaryShapeColor);
			mLinePaint.setStrokeWidth(mToolStrokeWidth * 2);
			initCropBounds();

			PointF rightTopPoint = new PointF(-mBoxWidth / 2, -mBoxHeight / 2);

			for (int lines = 0; lines < 4; lines++) {
				float cropLineLengthHeight = mBoxHeight / 10;
				float cropLineLengthWidth = mBoxWidth / 10;

				canvas.drawLine(rightTopPoint.x - mToolStrokeWidth / 2,
						rightTopPoint.y, rightTopPoint.x + cropLineLengthWidth,
						rightTopPoint.y, mLinePaint);

				canvas.drawLine(rightTopPoint.x, rightTopPoint.y
						- mToolStrokeWidth / 2, rightTopPoint.x,
						rightTopPoint.y + cropLineLengthHeight, mLinePaint);

				canvas.drawLine(rightTopPoint.x + mBoxWidth / 2
						- cropLineLengthWidth, rightTopPoint.y, rightTopPoint.x
						+ mBoxWidth / 2 + cropLineLengthWidth, rightTopPoint.y,
						mLinePaint);
				canvas.rotate(90);
				float tempX = rightTopPoint.x;
				rightTopPoint.x = rightTopPoint.y;
				rightTopPoint.y = tempX;
				float tempHeight = mBoxHeight;
				mBoxHeight = mBoxWidth;
				mBoxWidth = tempHeight;
			}
		}
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
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			if (mFindCroppingCoordinates.getStatus() != AsyncTask.Status.RUNNING) {
				mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask();
				mFindCroppingCoordinates.execute();
			}
			break;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			executeCropCommand();
			break;
		default:
			super.attributeButtonClick(buttonNumber);
		}
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return NO_BUTTON_RESOURCE;
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			return R.drawable.icon_menu_crop_adjust;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_crop_cut;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	private void initialiseCroppingState() {
		mCropRunFinished = false;
		mCropBoundWidthXRight = 0;
		mCropBoundHeightYBottom = 0;
		mCropBoundWidthXLeft = PaintroidApplication.drawingSurface
				.getBitmapWidth();
		mCropBoundHeightYTop = PaintroidApplication.drawingSurface
				.getBitmapHeight();
		mIntermediateCropBoundWidthXLeft = 0;
		mIntermediateCropBoundWidthXRight = PaintroidApplication.drawingSurface
				.getBitmapWidth();
		mIntermediateCropBoundHeightYTop = 0;
		mIntermediateCropBoundHeightYBottom = PaintroidApplication.drawingSurface
				.getBitmapHeight();
		PaintroidApplication.perspective.resetScaleAndTranslation();
		float zoomFactor = PaintroidApplication.perspective
				.getScaleForCenterBitmap() * START_ZOOM_FACTOR;
		PaintroidApplication.perspective.setScale(zoomFactor);

	}

	protected void displayCroppingInformation() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.image_toast_layout, (ViewGroup) ((Activity) mContext)
						.findViewById(R.id.image_toast_layout_root));

		if (areCropBordersValid() == false) {
			ImageView toastImage = (ImageView) layout
					.findViewById(R.id.toast_image);
			toastImage.setVisibility(View.GONE);

			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(mContext.getText(R.string.crop_nothing_to_corp));
		}

		Toast toast = new Toast(mContext);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	protected void executeCropCommand() {
		if (mCropRunFinished == true) {
			mCropRunFinished = false;
			initCropBounds();
			if (areCropBordersValid()) {
				IndeterminateProgressDialog.getInstance().show();
				Command command = new CropCommand((int) mCropBoundWidthXLeft,
						(int) mCropBoundHeightYTop,
						(int) Math.floor(mCropBoundWidthXRight),
						(int) Math.floor(mCropBoundHeightYBottom));

				((CropCommand) command).addObserver(this);
				PaintroidApplication.commandManager.commitCommand(command);
			} else {
				mCropRunFinished = true;
				displayCroppingInformation();
			}
		}
	}

	private boolean areCropBordersValid() {
		if (mCropBoundWidthXRight < mCropBoundWidthXLeft
				|| mCropBoundHeightYTop > mCropBoundHeightYBottom) {
			return false;
		}
		if (mCropBoundWidthXLeft <= 0f
				&& mCropBoundWidthXRight >= Float
						.valueOf(PaintroidApplication.drawingSurface
								.getBitmapWidth())
				&& mCropBoundHeightYTop <= 0f
				&& mCropBoundHeightYBottom >= Float
						.valueOf(PaintroidApplication.drawingSurface
								.getHeight())) {
			return false;
		}
		return true;

	}

	@Override
	public void update(Observable observable, Object data) {
		super.update(observable, data);
		if (data instanceof BaseCommand.NOTIFY_STATES) {
			if (BaseCommand.NOTIFY_STATES.COMMAND_DONE == data
					|| BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data) {
				initialiseCroppingState();
				mCropBoundWidthXRight = Float
						.valueOf(PaintroidApplication.drawingSurface
								.getBitmapWidth()) - 1f;
				mCropBoundHeightYBottom = Float
						.valueOf(PaintroidApplication.drawingSurface
								.getBitmapHeight()) - 1f;
				mCropBoundWidthXLeft = 0f;
				mCropBoundHeightYTop = 0f;
				setRectangle(new RectF(mCropBoundWidthXLeft,
						mCropBoundHeightYTop, mCropBoundWidthXRight,
						mCropBoundHeightYBottom));
				mCropRunFinished = true;
			}
		}
	}

	protected class FindCroppingCoordinatesAsyncTask extends
			AsyncTask<Void, Integer, Void> {

		private int mBitmapWidth = -1;
		private int mBitmapHeight = -1;
		private final int TRANSPARENT = Color.TRANSPARENT;

		FindCroppingCoordinatesAsyncTask() {
			initialiseCroppingState();
			mBitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
			mBitmapHeight = PaintroidApplication.drawingSurface
					.getBitmapHeight();
			mLinePaint = new Paint();
			mLinePaint.setDither(true);
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setStrokeJoin(Paint.Join.ROUND);

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if (PaintroidApplication.drawingSurface
					.isDrawingSurfaceBitmapValid()) {
				croppingAlgorithmSnail();
			}
			return null;
		}

		private void croppingAlgorithmSnail() {
			try {
				if (PaintroidApplication.drawingSurface
						.isDrawingSurfaceBitmapValid()) {
					searchTopToBottom();
					searchLeftToRight();
					searchBottomToTop();
					searchRightToLeft();
				}
			} catch (Exception ex) {
				Log.e(PaintroidApplication.TAG,
						"ERROR: Cropping->" + ex.getMessage());
			}

		}

		private void getBitmapPixelsLineWidth(int[] bitmapPixelsArray,
				int heightStartYLine) {
			PaintroidApplication.drawingSurface.getPixels(bitmapPixelsArray, 0,
					mBitmapWidth, 0, heightStartYLine, mBitmapWidth, 1);
		}

		private void getBitmapPixelsLineHeight(int[] bitmapPixelsArray,
				int widthXStartLine) {
			PaintroidApplication.drawingSurface.getPixels(bitmapPixelsArray, 0,
					1, widthXStartLine, 0, 1, mBitmapHeight);
		}

		private void searchTopToBottom() {
			int[] localBitmapPixelArray = new int[mBitmapWidth];
			for (mIntermediateCropBoundHeightYTop = 0; mIntermediateCropBoundHeightYTop < mBitmapHeight; mIntermediateCropBoundHeightYTop++) {
				getBitmapPixelsLineWidth(localBitmapPixelArray,
						mIntermediateCropBoundHeightYTop);
				setRectangle(new RectF(mIntermediateCropBoundWidthXLeft,
						mIntermediateCropBoundHeightYTop,
						mIntermediateCropBoundWidthXRight,
						mIntermediateCropBoundHeightYBottom));

				for (int indexWidth = 0; indexWidth < mBitmapWidth; indexWidth++) {
					if (localBitmapPixelArray[indexWidth] != TRANSPARENT) {
						updateCroppingBounds(indexWidth,
								mIntermediateCropBoundHeightYTop);
						return;
					}
				}
			}
		}

		private void searchLeftToRight() {
			int[] localBitmapPixelArray = new int[mBitmapHeight];
			for (mIntermediateCropBoundWidthXLeft = 0; mIntermediateCropBoundWidthXLeft < mBitmapWidth; mIntermediateCropBoundWidthXLeft++) {
				getBitmapPixelsLineHeight(localBitmapPixelArray,
						mIntermediateCropBoundWidthXLeft);

				setRectangle(new RectF(mIntermediateCropBoundWidthXLeft,
						mIntermediateCropBoundHeightYTop,
						mIntermediateCropBoundWidthXRight,
						mIntermediateCropBoundHeightYBottom));

				for (int indexHeight = mIntermediateCropBoundHeightYTop; indexHeight < mBitmapHeight; indexHeight++) {
					if (localBitmapPixelArray[indexHeight] != TRANSPARENT) {
						updateCroppingBounds(mIntermediateCropBoundWidthXLeft,
								indexHeight);
						return;
					}
				}

			}
		}

		private void searchBottomToTop() {
			int[] localBitmapPixelArray = new int[mBitmapWidth];
			for (mIntermediateCropBoundHeightYBottom = mBitmapHeight - 1; mIntermediateCropBoundHeightYBottom >= 0; mIntermediateCropBoundHeightYBottom--) {
				getBitmapPixelsLineWidth(localBitmapPixelArray,
						mIntermediateCropBoundHeightYBottom);

				setRectangle(new RectF(mIntermediateCropBoundWidthXLeft,
						mIntermediateCropBoundHeightYTop,
						mIntermediateCropBoundWidthXRight,
						mIntermediateCropBoundHeightYBottom));

				for (int indexWidth = mIntermediateCropBoundWidthXLeft; indexWidth < mBitmapWidth; indexWidth++) {
					if (localBitmapPixelArray[indexWidth] != TRANSPARENT) {
						updateCroppingBounds(indexWidth,
								mIntermediateCropBoundHeightYBottom);
						return;
					}
				}
			}
		}

		private void searchRightToLeft() {
			int[] localBitmapPixelArray = new int[mBitmapHeight];
			for (mIntermediateCropBoundWidthXRight = mBitmapWidth - 1; mIntermediateCropBoundWidthXRight >= 0; mIntermediateCropBoundWidthXRight--) {
				getBitmapPixelsLineHeight(localBitmapPixelArray,
						mIntermediateCropBoundWidthXRight);

				setRectangle(new RectF(mIntermediateCropBoundWidthXLeft,
						mIntermediateCropBoundHeightYTop,
						mIntermediateCropBoundWidthXRight,
						mIntermediateCropBoundHeightYBottom));

				for (int indexHeightTop = mIntermediateCropBoundHeightYTop; indexHeightTop <= mIntermediateCropBoundHeightYBottom; indexHeightTop++) {
					if (localBitmapPixelArray[indexHeightTop] != TRANSPARENT) {
						updateCroppingBounds(mIntermediateCropBoundWidthXRight,
								indexHeightTop);
						return;
					}
				}

			}
		}

		@Override
		protected void onPostExecute(Void nothing) {
			mCropRunFinished = true;
			displayCroppingInformation();
		}

	}

	private void updateCroppingBounds(int cropWidthXPosition,
			int cropHeightYPosition) {
		mCropBoundWidthXLeft = Math.min(cropWidthXPosition,
				mCropBoundWidthXLeft);
		mCropBoundWidthXRight = Math.max(cropWidthXPosition,
				mCropBoundWidthXRight);

		mCropBoundHeightYTop = Math.min(cropHeightYPosition,
				mCropBoundHeightYTop);
		mCropBoundHeightYBottom = Math.max(cropHeightYPosition,
				mCropBoundHeightYBottom);

		setRectangle(new RectF(mCropBoundWidthXLeft, mCropBoundHeightYTop,
				mCropBoundWidthXRight, mCropBoundHeightYBottom));

	}

	private void setRectangle(RectF rectangle) {
		mBoxWidth = rectangle.right - rectangle.left + 1f;
		mBoxHeight = rectangle.bottom - rectangle.top + 1f;
		mToolPosition.x = rectangle.left + mBoxWidth / 2f;
		mToolPosition.y = rectangle.top + mBoxHeight / 2f;
	}

	private void initCropBounds() {
		mCropBoundWidthXLeft = Math.max(0f, mToolPosition.x - mBoxWidth / 2f);
		mCropBoundHeightYTop = Math.max(0f, mToolPosition.y - mBoxHeight / 2f);
		mCropBoundWidthXRight = Math.min(
				PaintroidApplication.drawingSurface.getBitmapWidth(),
				mToolPosition.x + mBoxWidth / 2f);
		mCropBoundHeightYBottom = Math.min(
				PaintroidApplication.drawingSurface.getBitmapHeight(),
				mToolPosition.y + mBoxHeight / 2f);
	}

	@Override
	protected void onClickInBox() {
		executeCropCommand();
	}

}
