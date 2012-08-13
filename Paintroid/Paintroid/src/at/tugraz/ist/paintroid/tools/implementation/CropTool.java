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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
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
import at.tugraz.ist.paintroid.command.implementation.CropCommand;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class CropTool extends BaseToolWithShape {

	protected ProgressBar mProgressBar;
	protected int mTotalPixelCount;
	protected ProgressDialog mCropProgressDialogue;
	DrawingSurface mDrawingSurface;
	protected int mCropBoundWidthXLeft;
	protected int mCropBoundWidthXRight = 0;
	protected int mCropBoundHeightYTop;
	protected int mCropBoundHeightYBottom = 0;
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

	// ------------------------------------------------------------------------------
	// protected int mDefaultCropBoxWidth = 200;
	// protected int mDefaultCropBoxHeight = 200;
	protected int mCropBoxWidth;
	protected int mCropBoxHeight;
	// Tolerance that the resize action is performed if the frame is touched
	protected float mFrameTolerance = 30;
	protected ResizeAction mResizeAction;
	// protected Bitmap mCropBitmap = null;
	// protected Paint linePaint;
	protected PointF mMovedDistance = new PointF(0, 0);
	protected PointF mPreviousEventCoordinate = null;
	protected boolean mResize;

	// DrawingSurface drawingSurface;
	// protected final int toolStrokeWidth = 5;
	// protected FloatingBoxAction currentAction = null;

	protected enum ResizeAction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}

	// ------------------------------------------------------------------------------

	public CropTool(Context context, ToolType toolType, DrawingSurface drawingSurface) {
		super(context, toolType);
		mDrawingSurface = drawingSurface;
		mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask();
		mFindCroppingCoordinates.execute();
		// ----
		mResize = false;
		mResizeAction = ResizeAction.NONE;
		// ----

	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		mMovedDistance.set(0, 0);
		mPreviousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		mResize = false;

		// ----
		// Resize (on frame)
		/*
		 * if (coordinate.x < this.position.x + this.mCropBoxWidth / 2 + mFrameTolerance && clickCoordinatesRotatedX >
		 * this.position.x - this.width / 2 - frameTolerance && clickCoordinatesRotatedY < this.position.y + this.height
		 * / 2 + frameTolerance && clickCoordinatesRotatedY > this.position.y - this.height / 2 - frameTolerance) { if
		 * (clickCoordinatesRotatedX < this.position.x - this.width / 2 + frameTolerance) { resizeAction =
		 * ResizeAction.LEFT; } else if (clickCoordinatesRotatedX > this.position.x + this.width / 2 - frameTolerance) {
		 * resizeAction = ResizeAction.RIGHT; } if (clickCoordinatesRotatedY < this.position.y - this.height / 2 +
		 * frameTolerance) { if (resizeAction == ResizeAction.LEFT) { resizeAction = ResizeAction.TOPLEFT; } else if
		 * (resizeAction == ResizeAction.RIGHT) { resizeAction = ResizeAction.TOPRIGHT; } else { resizeAction =
		 * ResizeAction.TOP; } } else if (clickCoordinatesRotatedY > this.position.y + this.height / 2 - frameTolerance)
		 * { if (resizeAction == ResizeAction.LEFT) { resizeAction = ResizeAction.BOTTOMLEFT; } else if (resizeAction ==
		 * ResizeAction.RIGHT) { resizeAction = ResizeAction.BOTTOMRIGHT; } else { resizeAction = ResizeAction.BOTTOM; }
		 * }
		 */
		if (coordinate.x > mCropBoundWidthXLeft - mFrameTolerance
				&& coordinate.x < mCropBoundWidthXLeft + mFrameTolerance && coordinate.y > mCropBoundHeightYTop
				&& coordinate.y < mCropBoundHeightYBottom) {

			mResizeAction = ResizeAction.LEFT;
			mResize = true;

		}
		if (coordinate.x > mCropBoundWidthXRight - mFrameTolerance
				&& coordinate.x < mCropBoundWidthXRight + mFrameTolerance && coordinate.y > mCropBoundHeightYTop
				&& coordinate.y < mCropBoundHeightYBottom) {

			mResizeAction = ResizeAction.RIGHT;
			mResize = true;

		}
		if (coordinate.y > mCropBoundHeightYTop - mFrameTolerance
				&& coordinate.y < mCropBoundHeightYTop + mFrameTolerance && coordinate.x > mCropBoundWidthXLeft
				&& coordinate.x < mCropBoundWidthXRight) {

			mResizeAction = ResizeAction.TOP;
			mResize = true;
		}
		if (coordinate.y > mCropBoundHeightYBottom - mFrameTolerance
				&& coordinate.y < mCropBoundHeightYBottom + mFrameTolerance && coordinate.x > mCropBoundWidthXLeft
				&& coordinate.x < mCropBoundWidthXRight) {

			mResizeAction = ResizeAction.BOTTOM;
			mResize = true;
		}

		// todo: top and bottom
		// todo: combinations between top and left ect
		// ----

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
		if (mResize && mCropRunFinished) {
			resize(delta.x, delta.y);
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (coordinate == null || mPreviousEventCoordinate == null) {
			return false;
		}
		// mMovedDistance.set(mMovedDistance.x + Math.abs(coordinate.x - mPreviousEventCoordinate.x),
		// mMovedDistance.y + Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		// if (PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.x
		// && PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.y) {
		//
		// Command command = new CropCommand(stampBitmap, this.position, width, height, rotation);
		// PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
		//
		// }
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
			frameRect.set(mCropBoundWidthXLeft - strokeWidthHalf, mCropBoundHeightYTop - strokeWidthHalf,
					mCropBoundWidthXRight + strokeWidthHalf, mCropBoundHeightYBottom + strokeWidthHalf);

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
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.image_toast_layout,
				(ViewGroup) ((Activity) context).findViewById(R.id.image_toast_layout_root));

		if ((mCropBoundWidthXRight < mCropBoundWidthXLeft) || mCropBoundHeightYTop > mCropBoundHeightYBottom) {

			ImageView toastImage = (ImageView) layout.findViewById(R.id.toast_image);
			toastImage.setVisibility(View.GONE);

			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(context.getText(R.string.crop_nothing_to_corp));
		}

		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	protected void executeCropCommand() {
		if (mCropRunFinished == true) {
			if ((mCropBoundWidthXRight >= mCropBoundWidthXLeft) || mCropBoundHeightYTop <= mCropBoundHeightYBottom) {
				Command command = new CropCommand(this.mCropBoundWidthXLeft, mCropBoundHeightYTop,
						mCropBoundWidthXRight, mCropBoundHeightYBottom);
				PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
				try {
					Thread.sleep(SLEEP_AFTER_COMMIT_CROP_COMMAND);
				} catch (InterruptedException e) {
				}
				initialiseCroppingState();
			} else {
				displayCroppingInformation();
			}
		}
	}

	protected class FindCroppingCoordinatesAsyncTask extends AsyncTask<Void, Integer, Void> {

		private int mBitmapWidth = -1;
		private int mBitmapHeight = -1;
		private final int TRANSPARENT = Color.TRANSPARENT;

		FindCroppingCoordinatesAsyncTask() {
			initialiseCroppingState();
			mBitmapWidth = mCropBoundWidthXLeft;
			mBitmapHeight = mCropBoundHeightYTop;
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
			searchTopToBottom();
			searchLeftToRight();
			searchBottomToTop();
			searchRightToLeft();

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
			mCropRunFinished = true;
			// mBitmapPixelArray = null;
			displayCroppingInformation();
		}

	}

	private void updateCroppingBounds(int cropWidthXPosition, int cropHeightYPosition) {
		mCropBoundWidthXLeft = Math.min(cropWidthXPosition, mCropBoundWidthXLeft);
		mCropBoundWidthXRight = Math.max(cropWidthXPosition, mCropBoundWidthXRight);

		mCropBoundHeightYTop = Math.min(cropHeightYPosition, mCropBoundHeightYTop);
		mCropBoundHeightYBottom = Math.max(cropHeightYPosition, mCropBoundHeightYBottom);
	}

	protected void resize(float delta_x, float delta_y) {
		/*
		 * double rotationRadian = rotation * Math.PI / 180; double delta_x_corrected = Math.cos(-rotationRadian) *
		 * (delta_x) - Math.sin(-rotationRadian) * (delta_y); double delta_y_corrected = Math.sin(-rotationRadian) *
		 * (delta_x) + Math.cos(-rotationRadian) * (delta_y);
		 * 
		 * float resize_x_move_center_x = (float) ((delta_x_corrected / 2) * Math.cos(rotationRadian)); float
		 * resize_x_move_center_y = (float) ((delta_x_corrected / 2) * Math.sin(rotationRadian)); float
		 * resize_y_move_center_x = (float) ((delta_y_corrected / 2) * Math.sin(rotationRadian)); float
		 * resize_y_move_center_y = (float) ((delta_y_corrected / 2) * Math.cos(rotationRadian));
		 */

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
			case TOPLEFT:
				/*
				 * this.height -= (int) delta_y_corrected; this.position.x -= (int) resize_y_move_center_x;
				 * this.position.y += (int) resize_y_move_center_y; break;
				 */
			case BOTTOM:
				mCropBoundHeightYBottom += delta_y;
				if (mCropBoundHeightYBottom <= mCropBoundHeightYTop + mFrameTolerance) {
					mCropBoundHeightYBottom = (int) (mCropBoundHeightYTop + mFrameTolerance);
				}
				break;
			case BOTTOMLEFT:
			case BOTTOMRIGHT:
				/*
				 * this.height += (int) delta_y_corrected; this.position.x -= (int) resize_y_move_center_x;
				 * this.position.y += (int) resize_y_move_center_y; break;
				 */
			default:
				break;
		}
		// prevent that box gets too small
		if (this.mCropBoxWidth < mFrameTolerance) {
			this.mCropBoxWidth = (int) mFrameTolerance;
		}
		if (this.mCropBoxHeight < mFrameTolerance) {
			this.mCropBoxHeight = (int) mFrameTolerance;
		}

		/*
		 * // Width switch (resizeAction) { case LEFT: case TOPLEFT: case BOTTOMLEFT: this.width -= (int)
		 * delta_x_corrected; this.position.x += (int) resize_x_move_center_x; this.position.y += (int)
		 * resize_x_move_center_y; break; case RIGHT: case TOPRIGHT: case BOTTOMRIGHT: this.width += (int)
		 * delta_x_corrected; this.position.x += (int) resize_x_move_center_x; this.position.y += (int)
		 * resize_x_move_center_y; break; default: break; }
		 * 
		 * // prevent that box gets too small if (this.width < frameTolerance) { this.width = (int) frameTolerance; } if
		 * (this.height < frameTolerance) { this.height = (int) frameTolerance; }
		 */
	}

}
