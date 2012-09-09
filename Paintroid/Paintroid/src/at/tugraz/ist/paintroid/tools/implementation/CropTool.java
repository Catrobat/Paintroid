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

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.command.implementation.CropCommand;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class CropTool extends BaseToolWithShape {

	protected ProgressBar mProgressBar;
	protected int mTotalPixelCount;
	DrawingSurface mDrawingSurface;
	protected float mCropBoundWidthXLeft;
	protected float mCropBoundWidthXRight = 0;
	protected float mCropBoundHeightYTop;
	protected float mCropBoundHeightYBottom = 0;
	protected int mIntermediateCropBoundWidthXLeft;
	protected int mIntermediateCropBoundWidthXRight;
	protected int mIntermediateCropBoundHeightYTop;
	protected int mIntermediateCropBoundHeightYBottom;
	protected Paint mLinePaint;
	protected final int mLineStrokeWidth = 5;
	protected static final float FAST_CROPPING_PERCENTAGE_TRYS = 4;
	protected int mCropExtraLinesLength = mLineStrokeWidth * 5;
	protected boolean mCropRunFinished = false;
	private static FindCroppingCoordinatesAsyncTask mFindCroppingCoordinates = null;
	private static final float START_ZOOM_FACTOR = 0.95f;
	private final int SLEEP_AFTER_COMMIT_CROP_COMMAND = 300;
	protected float mCropBoxWidth;
	protected float mCropBoxHeight;
	protected float mFrameTolerance = 30;
	protected ResizeAction mResizeAction;
	protected PointF mMovedDistance = new PointF(0, 0);
	protected PointF mPreviousEventCoordinate = null;
	private FloatingBoxAction mCurrentAction;

	protected enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}

	private enum FloatingBoxAction {
		NONE, MOVE, RESIZE;
	}

	public CropTool(Context context, ToolType toolType, DrawingSurface drawingSurface) {
		super(context, toolType);
		mDrawingSurface = drawingSurface;
		mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask();
		mFindCroppingCoordinates.execute();
		mResizeAction = ResizeAction.NONE;

	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		mMovedDistance.set(0, 0);
		mPreviousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		mCurrentAction = getAction(coordinate.x, coordinate.y);

		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (coordinate == null || mPreviousEventCoordinate == null) {
			return false;
		}
		PointF delta = new PointF(coordinate.x - mPreviousEventCoordinate.x, coordinate.y - mPreviousEventCoordinate.y);
		mMovedDistance.set(mMovedDistance.x + Math.abs(delta.x), mMovedDistance.y + Math.abs(delta.y));
		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);
		if (mCurrentAction == FloatingBoxAction.RESIZE && mCropRunFinished) {
			resize(delta.x, delta.y);
		} else if (mCurrentAction == FloatingBoxAction.MOVE && mCropRunFinished) {
			move(delta.x, delta.y);
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (coordinate == null || mPreviousEventCoordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void drawShape(Canvas canvas) {

		int strokeWidthHalf = mLineStrokeWidth / 2;
		mLinePaint.setColor(Color.YELLOW);
		mLinePaint.setStrokeWidth(mLineStrokeWidth);
		if (mCropRunFinished == false) {
			canvas.drawLine(0, mIntermediateCropBoundHeightYTop, mDrawingSurface.getBitmap().getWidth(),
					mIntermediateCropBoundHeightYTop, mLinePaint);
			canvas.drawLine(mIntermediateCropBoundWidthXLeft, 0, mIntermediateCropBoundWidthXLeft, mDrawingSurface
					.getBitmap().getHeight(), mLinePaint);
			canvas.drawLine(0, mIntermediateCropBoundHeightYBottom, mDrawingSurface.getBitmap().getWidth(),
					mIntermediateCropBoundHeightYBottom, mLinePaint);
			canvas.drawLine(mIntermediateCropBoundWidthXRight, 0, mIntermediateCropBoundWidthXRight, mDrawingSurface
					.getBitmap().getHeight(), mLinePaint);

		} else {

			Rect frameRect = new Rect();
			frameRect.set((int) mCropBoundWidthXLeft - strokeWidthHalf, (int) mCropBoundHeightYTop - strokeWidthHalf,
					(int) mCropBoundWidthXRight + strokeWidthHalf, (int) mCropBoundHeightYBottom + strokeWidthHalf);

			canvas.drawRect(frameRect, mLinePaint);

			float cropEdgesToDraw[] = {
					// top left lines
					mCropBoundWidthXLeft - strokeWidthHalf,
					mCropBoundHeightYTop - strokeWidthHalf,
					mCropBoundWidthXLeft - mCropExtraLinesLength - strokeWidthHalf,
					mCropBoundHeightYTop - strokeWidthHalf,
					mCropBoundWidthXLeft - strokeWidthHalf,
					mCropBoundHeightYTop - strokeWidthHalf,
					mCropBoundWidthXLeft - strokeWidthHalf,
					mCropBoundHeightYTop - mCropExtraLinesLength - strokeWidthHalf,
					// bottom right lines
					mCropBoundWidthXRight + strokeWidthHalf, mCropBoundHeightYBottom + strokeWidthHalf,
					mCropBoundWidthXRight + mCropExtraLinesLength + strokeWidthHalf,
					mCropBoundHeightYBottom + strokeWidthHalf, mCropBoundWidthXRight + strokeWidthHalf,
					mCropBoundHeightYBottom + strokeWidthHalf, mCropBoundWidthXRight + strokeWidthHalf,
					mCropBoundHeightYBottom + mCropExtraLinesLength + strokeWidthHalf };
			canvas.drawLines(cropEdgesToDraw, mLinePaint);
		}
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		drawShape(canvas);
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {
		if (buttonNumber == 1) {
			if (mFindCroppingCoordinates.getStatus() != AsyncTask.Status.RUNNING) {
				mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask();
				mFindCroppingCoordinates.execute();
			}
		} else if (buttonNumber == 2) {
			executeCropCommand();
			// todo: wait for finish execution than start auto cropping again
			// if (mFindCroppingCoordinates.getStatus() != AsyncTask.Status.RUNNING) {
			// mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask();
			// mFindCroppingCoordinates.execute();
			// }
		}
	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		if (buttonNumber == 0) {
			return R.drawable.ic_menu_more_crop_64;
		} else if (buttonNumber == 1) {
			return R.drawable.icon_crop;
		} else if (buttonNumber == 2) {
			return R.drawable.icon_content_cut;
		}
		return 0;
	}

	@Override
	public int getAttributeButtonColor(int buttonNumber) {
		return super.getAttributeButtonColor(buttonNumber);
	}

	private void initialiseCroppingState() {
		mCropRunFinished = false;
		mTotalPixelCount = mDrawingSurface.getBitmap().getWidth() * mDrawingSurface.getBitmap().getHeight();
		mCropBoundWidthXRight = 0;
		mCropBoundHeightYBottom = 0;
		mCropBoundWidthXLeft = mDrawingSurface.getBitmap().getWidth();
		mCropBoundHeightYTop = mDrawingSurface.getBitmap().getHeight();
		mIntermediateCropBoundWidthXLeft = 0;
		mIntermediateCropBoundWidthXRight = mDrawingSurface.getBitmap().getWidth();
		mIntermediateCropBoundHeightYTop = 0;
		mIntermediateCropBoundHeightYBottom = mDrawingSurface.getBitmap().getHeight();
		PaintroidApplication.CURRENT_PERSPECTIVE.resetScaleAndTranslation();
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(START_ZOOM_FACTOR);

	}

	protected void displayCroppingInformation() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.image_toast_layout,
				(ViewGroup) ((Activity) mContext).findViewById(R.id.image_toast_layout_root));

		if ((mCropBoundWidthXRight < mCropBoundWidthXLeft) || mCropBoundHeightYTop > mCropBoundHeightYBottom) {

			ImageView toastImage = (ImageView) layout.findViewById(R.id.toast_image);
			toastImage.setVisibility(View.GONE);

			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(mContext.getText(R.string.crop_nothing_to_corp));
		}

		Toast toast = new Toast(mContext);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	protected void executeCropCommand() {
		if (mCropRunFinished == true) {
			if ((mCropBoundWidthXRight >= mCropBoundWidthXLeft) || mCropBoundHeightYTop <= mCropBoundHeightYBottom) {
				Command command = new CropCommand(this.mCropBoundWidthXLeft, mCropBoundHeightYTop,
						mCropBoundWidthXRight, mCropBoundHeightYBottom);
				((CropCommand) command).addObserver(this);
				mProgressDialog.show();
				PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
			} else {
				displayCroppingInformation();
			}
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		super.update(observable, data);
		if (data instanceof BaseCommand.NOTIFY_STATES) {
			if (BaseCommand.NOTIFY_STATES.COMMAND_DONE == data || BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data) {
				initialiseCroppingState();
			}
		}
	}

	protected class FindCroppingCoordinatesAsyncTask extends AsyncTask<Void, Integer, Void> {

		private int mBitmapWidth = -1;
		private int mBitmapHeight = -1;
		private final int TRANSPARENT = Color.TRANSPARENT;

		FindCroppingCoordinatesAsyncTask() {
			initialiseCroppingState();
			mBitmapWidth = (int) mCropBoundWidthXLeft;
			mBitmapHeight = (int) mCropBoundHeightYTop;
			mLinePaint = new Paint();
			mLinePaint.setDither(true);
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setStrokeJoin(Paint.Join.ROUND);

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			croppingAlgorithmSnail();
			return null;
		}

		private void croppingAlgorithmSnail() {

			if (!mDrawingSurface.getBitmap().isRecycled()) {
				try {
					searchTopToBottom();
					searchLeftToRight();
					searchBottomToTop();
					searchRightToLeft();
				} catch (Exception ex) {
					Log.e(PaintroidApplication.TAG, ex.getMessage());
				}
			}

		}

		private void getBitmapPixelsLineWidth(int[] bitmapPixelsArray, int heightStartYLine) {
			mDrawingSurface.getBitmap().getPixels(bitmapPixelsArray, 0, mBitmapWidth, 0, heightStartYLine,
					mBitmapWidth, 1);
		}

		private void getBitmapPixelsLineHeight(int[] bitmapPixelsArray, int widthXStartLine) {
			mDrawingSurface.getBitmap().getPixels(bitmapPixelsArray, 0, 1, widthXStartLine, 0, 1, mBitmapHeight);
		}

		private void searchTopToBottom() {
			int[] localBitmapPixelArray = new int[mBitmapWidth];
			for (mIntermediateCropBoundHeightYTop = 0; mIntermediateCropBoundHeightYTop < mBitmapHeight; mIntermediateCropBoundHeightYTop++) {
				getBitmapPixelsLineWidth(localBitmapPixelArray, mIntermediateCropBoundHeightYTop);
				for (int indexWidth = 0; indexWidth < mBitmapWidth; indexWidth++) {
					if (localBitmapPixelArray[indexWidth] != TRANSPARENT) {
						updateCroppingBounds(indexWidth, mIntermediateCropBoundHeightYTop);
						return;
					}
				}
			}
		}

		private void searchLeftToRight() {
			int[] localBitmapPixelArray = new int[mBitmapHeight];
			for (mIntermediateCropBoundWidthXLeft = 0; mIntermediateCropBoundWidthXLeft < mBitmapWidth; mIntermediateCropBoundWidthXLeft++) {
				getBitmapPixelsLineHeight(localBitmapPixelArray, mIntermediateCropBoundWidthXLeft);
				for (int indexHeight = mIntermediateCropBoundHeightYTop; indexHeight < mBitmapHeight; indexHeight++) {
					if (localBitmapPixelArray[indexHeight] != TRANSPARENT) {
						updateCroppingBounds(mIntermediateCropBoundWidthXLeft, indexHeight);
						return;
					}
				}

			}
		}

		private void searchBottomToTop() {
			int[] localBitmapPixelArray = new int[mBitmapWidth];
			for (mIntermediateCropBoundHeightYBottom = mBitmapHeight - 1; mIntermediateCropBoundHeightYBottom >= 0; mIntermediateCropBoundHeightYBottom--) {
				getBitmapPixelsLineWidth(localBitmapPixelArray, mIntermediateCropBoundHeightYBottom);
				for (int indexWidth = mIntermediateCropBoundWidthXLeft; indexWidth < mBitmapWidth; indexWidth++) {
					if (localBitmapPixelArray[indexWidth] != TRANSPARENT) {
						updateCroppingBounds(indexWidth, mIntermediateCropBoundHeightYBottom);
						return;
					}
				}
			}
		}

		private void searchRightToLeft() {
			int[] localBitmapPixelArray = new int[mBitmapHeight];
			for (mIntermediateCropBoundWidthXRight = mBitmapWidth - 1; mIntermediateCropBoundWidthXRight >= 0; mIntermediateCropBoundWidthXRight--) {
				getBitmapPixelsLineHeight(localBitmapPixelArray, mIntermediateCropBoundWidthXRight);
				for (int indexHeightTop = mIntermediateCropBoundHeightYTop; indexHeightTop <= mIntermediateCropBoundHeightYBottom; indexHeightTop++) {
					if (localBitmapPixelArray[indexHeightTop] != TRANSPARENT) {
						updateCroppingBounds(mIntermediateCropBoundWidthXRight, indexHeightTop);
						return;
					}
				}

			}
		}

		@Override
		protected void onPostExecute(Void nothing) {
			Log.d("paintroid", "CROP 1");
			mCropRunFinished = true;
			Log.d("paintroid", "CROP 2");
			displayCroppingInformation();
			Log.d("paintroid", "CROP 3");
		}

	}

	private void updateCroppingBounds(int cropWidthXPosition, int cropHeightYPosition) {
		mCropBoundWidthXLeft = Math.min(cropWidthXPosition, mCropBoundWidthXLeft);
		mCropBoundWidthXRight = Math.max(cropWidthXPosition, mCropBoundWidthXRight);

		mCropBoundHeightYTop = Math.min(cropHeightYPosition, mCropBoundHeightYTop);
		mCropBoundHeightYBottom = Math.max(cropHeightYPosition, mCropBoundHeightYBottom);
	}

	protected FloatingBoxAction getAction(float clickCoordinatesX, float clickCoordinatesY) {
		mResizeAction = ResizeAction.NONE;

		// Move (within box)
		if (clickCoordinatesX < (mCropBoundWidthXRight - mFrameTolerance)
				&& clickCoordinatesX > (mCropBoundWidthXLeft + mFrameTolerance)
				&& clickCoordinatesY < (mCropBoundHeightYBottom - mFrameTolerance)
				&& clickCoordinatesY > (mCropBoundHeightYTop + mFrameTolerance)) {
			return FloatingBoxAction.MOVE;
		}

		// Resize (on frame)
		if (clickCoordinatesY > (mCropBoundHeightYTop - mFrameTolerance)
				&& clickCoordinatesY < (mCropBoundHeightYBottom + mFrameTolerance)
				&& clickCoordinatesX > (mCropBoundWidthXLeft - mFrameTolerance)
				&& clickCoordinatesX < (mCropBoundWidthXRight + mFrameTolerance)) {

			if (clickCoordinatesY < (mCropBoundHeightYTop + mFrameTolerance)) {
				if (clickCoordinatesX < (mCropBoundWidthXLeft + mFrameTolerance)) {
					mResizeAction = ResizeAction.TOPLEFT;
					return FloatingBoxAction.RESIZE;
				} else if (clickCoordinatesX > (mCropBoundWidthXRight - mFrameTolerance)) {
					mResizeAction = ResizeAction.TOPRIGHT;
					return FloatingBoxAction.RESIZE;
				} else {
					mResizeAction = ResizeAction.TOP;
					return FloatingBoxAction.RESIZE;
				}
			}

			if (clickCoordinatesY > (mCropBoundHeightYBottom - mFrameTolerance)) {
				if (clickCoordinatesX < (mCropBoundWidthXLeft + mFrameTolerance)) {
					mResizeAction = ResizeAction.BOTTOMLEFT;
					return FloatingBoxAction.RESIZE;
				} else if (clickCoordinatesX > (mCropBoundWidthXRight - mFrameTolerance)) {
					mResizeAction = ResizeAction.BOTTOMRIGHT;
					return FloatingBoxAction.RESIZE;
				} else {
					mResizeAction = ResizeAction.BOTTOM;
					return FloatingBoxAction.RESIZE;
				}
			}

			if (clickCoordinatesX < mCropBoundWidthXLeft + mFrameTolerance) {
				mResizeAction = ResizeAction.LEFT;
				return FloatingBoxAction.RESIZE;
			}
			if (clickCoordinatesX > mCropBoundWidthXRight - mFrameTolerance) {
				mResizeAction = ResizeAction.RIGHT;
				return FloatingBoxAction.RESIZE;
			}

		}

		// No valid click
		return FloatingBoxAction.NONE;

	}

	protected void resize(float delta_x, float delta_y) {

		switch (mResizeAction) {

			case LEFT:
				mCropBoundWidthXLeft += delta_x;
				if (mCropBoundWidthXLeft >= mCropBoundWidthXRight - mFrameTolerance) {
					mCropBoundWidthXLeft = (int) (mCropBoundWidthXRight - mFrameTolerance);
				}
				break;

			case RIGHT:
				mCropBoundWidthXRight += delta_x;
				if (mCropBoundWidthXRight <= mCropBoundWidthXLeft + mFrameTolerance) {
					mCropBoundWidthXRight = (int) (mCropBoundWidthXLeft + mFrameTolerance);
				}
				break;

			case TOP:
				mCropBoundHeightYTop += delta_y;
				if (mCropBoundHeightYTop >= mCropBoundHeightYBottom - mFrameTolerance) {
					mCropBoundHeightYTop = (int) (mCropBoundHeightYBottom - mFrameTolerance);
				}
				break;

			case TOPRIGHT:
				mCropBoundHeightYTop += delta_y;
				mCropBoundWidthXRight += delta_x;
				if (mCropBoundHeightYTop >= mCropBoundHeightYBottom - mFrameTolerance) {
					mCropBoundHeightYTop = (int) (mCropBoundHeightYBottom - mFrameTolerance);
				}
				if (mCropBoundWidthXRight <= mCropBoundWidthXLeft + mFrameTolerance) {
					mCropBoundWidthXRight = (int) (mCropBoundWidthXLeft + mFrameTolerance);
				}
				break;

			case TOPLEFT:
				mCropBoundHeightYTop += delta_y;
				mCropBoundWidthXLeft += delta_x;
				if (mCropBoundHeightYTop >= mCropBoundHeightYBottom - mFrameTolerance) {
					mCropBoundHeightYTop = (int) (mCropBoundHeightYBottom - mFrameTolerance);
				}
				if (mCropBoundWidthXLeft >= mCropBoundWidthXRight - mFrameTolerance) {
					mCropBoundWidthXLeft = (int) (mCropBoundWidthXRight - mFrameTolerance);
				}
				break;

			case BOTTOM:
				mCropBoundHeightYBottom += delta_y;
				if (mCropBoundHeightYBottom <= mCropBoundHeightYTop + mFrameTolerance) {
					mCropBoundHeightYBottom = (int) (mCropBoundHeightYTop + mFrameTolerance);
				}
				break;

			case BOTTOMLEFT:
				mCropBoundHeightYBottom += delta_y;
				mCropBoundWidthXLeft += delta_x;
				if (mCropBoundHeightYBottom <= mCropBoundHeightYTop + mFrameTolerance) {
					mCropBoundHeightYBottom = (int) (mCropBoundHeightYTop + mFrameTolerance);
				}
				if (mCropBoundWidthXLeft >= mCropBoundWidthXRight - mFrameTolerance) {
					mCropBoundWidthXLeft = (int) (mCropBoundWidthXRight - mFrameTolerance);
				}
				break;

			case BOTTOMRIGHT:
				mCropBoundHeightYBottom += delta_y;
				mCropBoundWidthXRight += delta_x;
				if (mCropBoundHeightYBottom <= mCropBoundHeightYTop + mFrameTolerance) {
					mCropBoundHeightYBottom = (int) (mCropBoundHeightYTop + mFrameTolerance);
				}
				if (mCropBoundWidthXRight <= mCropBoundWidthXLeft + mFrameTolerance) {
					mCropBoundWidthXRight = (int) (mCropBoundWidthXLeft + mFrameTolerance);
				}
				break;
			default:
				break;
		}
		if (mCropBoundWidthXLeft < 0) {
			mCropBoundWidthXLeft = 0;
		}
		if (mCropBoundWidthXRight > mDrawingSurface.getBitmap().getWidth()) {
			mCropBoundWidthXRight = mDrawingSurface.getBitmap().getWidth();
		}
		if (mCropBoundHeightYTop < 0) {
			mCropBoundHeightYTop = 0;
		}
		if (mCropBoundHeightYBottom > mDrawingSurface.getBitmap().getHeight()) {
			mCropBoundHeightYBottom = mDrawingSurface.getBitmap().getHeight();
		}

	}

	protected void move(float delta_x, float delta_y) {
		mCropBoundWidthXRight += delta_x;
		mCropBoundWidthXLeft += delta_x;
		mCropBoundHeightYBottom += delta_y;
		mCropBoundHeightYTop += delta_y;

		if (mCropBoundWidthXLeft <= 0) {
			mCropBoundWidthXLeft = 0;
			if (mCropBoundWidthXRight <= (mFrameTolerance * 2)) {
				mCropBoundWidthXRight = (mFrameTolerance * 2);
			}
		}
		if (mCropBoundHeightYTop <= 0) {
			mCropBoundHeightYTop = 0;
			if (mCropBoundHeightYBottom <= (mFrameTolerance * 2)) {
				mCropBoundHeightYBottom = (mFrameTolerance * 2);
			}
		}
		if (mCropBoundWidthXRight >= mDrawingSurface.getBitmap().getWidth()) {
			mCropBoundWidthXRight = mDrawingSurface.getBitmap().getWidth();
			if (mCropBoundWidthXLeft >= (mDrawingSurface.getBitmap().getWidth() - mFrameTolerance * 2)) {
				mCropBoundWidthXLeft = (mDrawingSurface.getBitmap().getWidth() - mFrameTolerance * 2);
			}
		}
		if (mCropBoundHeightYBottom >= mDrawingSurface.getBitmap().getHeight()) {
			mCropBoundHeightYBottom = mDrawingSurface.getBitmap().getHeight();
			if (mCropBoundHeightYTop >= (mDrawingSurface.getBitmap().getHeight() - mFrameTolerance * 2)) {
				mCropBoundHeightYTop = (mDrawingSurface.getBitmap().getHeight() - mFrameTolerance * 2);
			}
		}

	}

}
