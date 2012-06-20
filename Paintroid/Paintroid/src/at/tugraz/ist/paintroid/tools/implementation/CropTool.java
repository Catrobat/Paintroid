package at.tugraz.ist.paintroid.tools.implementation;

import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
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

	protected ProgressBar mProgressbar;
	protected int mTotalPixelCount;
	protected ProgressDialog mCropProgressDialogue;
	DrawingSurface mDrawingSurface;
	protected int mCropBoundWidthXLeft;
	protected int mCropBoundWidthXRight = 0;
	protected int mCropBoundHeightYTop;
	protected int mCropBoundHeightYBottom = 0;
	protected Paint mLinePaint;
	protected final int mLineStrokeWidth = 5;
	protected static final float FAST_CROPPING_PERCENTAGE_TRYS = 4;
	protected int mCropExtraLinesLength = mLineStrokeWidth * 5;
	protected boolean mCropRunFinished = false;
	private FindCroppingCoordinatesAsyncTask mFindCroppingCoordinates = null;

	protected enum CROPPING_ALGORITHM_TYPES {
		RANDOM_FAST, SNAIL_CORRECT, RANDOM_AND_CORRECT, SLOW_CORRECT
	};

	protected static final CROPPING_ALGORITHM_TYPES CROPPING_ALGORITHM = CROPPING_ALGORITHM_TYPES.SNAIL_CORRECT;

	public CropTool(Context context, ToolType toolType, DrawingSurface drawingSurface) {
		super(context, toolType);
		mDrawingSurface = drawingSurface;
		mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask(context);
		mFindCroppingCoordinates.execute();
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void drawShape(Canvas canvas) {
		Rect frameRect = new Rect();
		int strokeWidthHalf = mLineStrokeWidth / 2;
		frameRect.set(mCropBoundWidthXLeft - strokeWidthHalf, mCropBoundHeightYTop - strokeWidthHalf,
				mCropBoundWidthXRight + strokeWidthHalf, mCropBoundHeightYBottom + strokeWidthHalf);
		mLinePaint.setColor(Color.YELLOW);
		mLinePaint.setStrokeWidth(mLineStrokeWidth);
		canvas.drawRect(frameRect, mLinePaint);

		float cropEdgesToDraw[] = {
				// top left lines
				mCropBoundWidthXLeft - strokeWidthHalf, mCropBoundHeightYTop - strokeWidthHalf,
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

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		drawShape(canvas);
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {
		if (buttonNumber == 1) {
			if (mFindCroppingCoordinates.getStatus() != AsyncTask.Status.RUNNING) {
				mFindCroppingCoordinates = new FindCroppingCoordinatesAsyncTask(context);
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
				initialiseCroppingState();
			} else {
				displayCroppingInformation();
			}
		}
	}

	protected class FindCroppingCoordinatesAsyncTask extends AsyncTask<Void, Integer, Void> {

		float mOnePercentOfBitmapPixel = 1;
		private int[] mBitmapPixelArray;
		private int mBitmapWidth = -1;
		private int mBitmapHeight = -1;
		private final int TRANSPARENT = Color.TRANSPARENT;
		private Context mContext;

		FindCroppingCoordinatesAsyncTask(Context context) {
			mContext = context;
			initialiseCroppingState();
			mBitmapWidth = mCropBoundWidthXLeft;
			mBitmapHeight = mCropBoundHeightYTop;
			mOnePercentOfBitmapPixel = Math.max(0.01f, (mTotalPixelCount / 100));
			mLinePaint = new Paint();
			mLinePaint.setDither(true);
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setStrokeJoin(Paint.Join.ROUND);
			int bitmapPixels = mBitmapHeight * mBitmapWidth;
			mBitmapPixelArray = new int[bitmapPixels];

			mDrawingSurface.getBitmap()
					.getPixels(mBitmapPixelArray, 0, mBitmapWidth, 0, 0, mBitmapWidth, mBitmapHeight);
		}

		@Override
		protected void onPreExecute() {
			mCropProgressDialogue = new ProgressDialog(mContext);
			mCropProgressDialogue.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mCropProgressDialogue.setMax(100);
			mCropProgressDialogue.setMessage(mContext.getString(R.string.crop_progress_text));
			mCropProgressDialogue.getWindow().setGravity(Gravity.BOTTOM);
			mCropProgressDialogue.show();
			mCropProgressDialogue.setProgress(0);
			mCropProgressDialogue.setSecondaryProgress(0);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			switch (CROPPING_ALGORITHM) {
				case RANDOM_FAST:
					croppingAlgorithmFast();
					break;
				case SNAIL_CORRECT:
					croppingAlgorithmSnail();
					break;
				case RANDOM_AND_CORRECT:
					croppingAlgorithmAlwayCorrectButFaster();
					break;
				case SLOW_CORRECT:
					croppingAlgorithmAlwaysCorrectSlow();
					break;
			}
			return null;
		}

		private void croppingAlgorithmSnail() {
			mCropProgressDialogue.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mCropProgressDialogue.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			int topDownHeight = topDown();
			int left_right = leftToRight(topDownHeight);
			int bottomUpHeight = downTop(left_right);
			rightToLeft(topDownHeight, bottomUpHeight);

		}

		private int topDown() {
			for (int indexHeight = 0; indexHeight < mBitmapHeight; indexHeight++) {
				int indexHeightMultiplayerInArray = indexHeight * mBitmapWidth;
				for (int indexWidth = 0; indexWidth < mBitmapWidth; indexWidth++) {
					int pixelInArrayPosition = indexWidth + indexHeightMultiplayerInArray;
					if (mBitmapPixelArray[pixelInArrayPosition] != TRANSPARENT) {
						updateCroppingBounds(indexWidth, indexHeight);
						return indexHeight;
					}
				}
			}
			return mBitmapHeight;// todo exit
		}

		private int leftToRight(int topHeight) {
			for (int indexWidth = 0; indexWidth < mBitmapWidth; indexWidth++) {
				for (int indexHeight = topHeight; indexHeight < mBitmapHeight; indexHeight++) {
					int pixelInArrayPosition = indexHeight * mBitmapWidth + indexWidth;
					if (mBitmapPixelArray[pixelInArrayPosition] != TRANSPARENT) {
						updateCroppingBounds(indexWidth, indexHeight);
						return indexWidth;
					}
				}

			}
			return mBitmapWidth;
		}

		private int downTop(int leftWidth) {
			for (int indexHeight = mBitmapHeight - 1; indexHeight >= 0; indexHeight--) {
				int indexHeightMultiplayerInArray = indexHeight * mBitmapWidth;
				for (int indexWidth = leftWidth; indexWidth < mBitmapWidth; indexWidth++) {
					int pixelInArrayPosition = indexWidth + indexHeightMultiplayerInArray;
					if (mBitmapPixelArray[pixelInArrayPosition] != TRANSPARENT) {
						updateCroppingBounds(indexWidth, indexHeight);
						return indexHeight;
					}
				}
			}
			return mBitmapHeight;// todo exit
		}

		private void rightToLeft(int topHeight, int bottomHeight) {
			for (int indexWidth = mBitmapWidth - 1; indexWidth >= 0; indexWidth--) {
				for (int indexHeight = topHeight; indexHeight < bottomHeight; indexHeight++) {
					int pixelInArrayPosition = indexHeight * mBitmapWidth + indexWidth;
					if (mBitmapPixelArray[pixelInArrayPosition] != TRANSPARENT) {
						updateCroppingBounds(indexWidth, indexHeight);
						return;
					}
				}

			}
		}

		private void croppingAlgorithmAlwayCorrectButFaster() {
			mCropProgressDialogue.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			croppingAlgorithmFast();
			mCropProgressDialogue.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			int percentDone = 0;
			for (int indexHeight = 0; indexHeight < mBitmapHeight; indexHeight++) {
				int indexHeightMultiplayerInArray = indexHeight * mBitmapWidth;
				for (int indexWidth = 0; indexWidth < mBitmapWidth; indexWidth++) {

					// ------- X
					// --___-- X
					// ..|_|.. O
					// ------- X
					// ------- X
					if (indexHeight <= mCropBoundHeightYTop || indexHeight >= mCropBoundHeightYBottom ||
					// ........ O
					// ..___... O
					// --|_|--- X
					// ........ O
					// ........ O
							((indexWidth <= mCropBoundWidthXLeft || indexWidth >= mCropBoundWidthXRight) && ((indexHeight >= mCropBoundHeightYTop) && (indexHeight <= mCropBoundHeightYBottom)))) {

						int pixelInArrayPosition = indexWidth + indexHeightMultiplayerInArray;
						if (mBitmapPixelArray[pixelInArrayPosition] != TRANSPARENT) {
							updateCroppingBounds(indexWidth, indexHeight);
						}
						if (percentDone < (int) (pixelInArrayPosition / mOnePercentOfBitmapPixel)) {
							percentDone = (int) (pixelInArrayPosition / mOnePercentOfBitmapPixel);
							publishProgress(percentDone);
						}
					}
				}
			}
		}

		private void croppingAlgorithmAlwaysCorrectSlow() {
			int percentDone = 0;
			for (int indexHeight = 0; indexHeight < mBitmapHeight; indexHeight++) {
				int indexHeightMultiplayerInArray = indexHeight * mBitmapWidth;
				for (int indexWidth = 0; indexWidth < mBitmapWidth; indexWidth++) {
					int pixelInArrayPosition = indexWidth + indexHeightMultiplayerInArray;
					if (mBitmapPixelArray[pixelInArrayPosition] != TRANSPARENT) {
						updateCroppingBounds(indexWidth, indexHeight);
					}
					if (percentDone < (int) (pixelInArrayPosition / mOnePercentOfBitmapPixel)) {
						percentDone = (int) (pixelInArrayPosition / mOnePercentOfBitmapPixel);
						publishProgress(percentDone);
					}
				}
			}
		}

		private void croppingAlgorithmFast() {
			Random randomNumbers = new Random();
			int tryLimit = (int) (mOnePercentOfBitmapPixel * FAST_CROPPING_PERCENTAGE_TRYS);
			int indexWidth = -1;
			int indexHeight = -1;
			int indexHeightMultiplayerInArray = indexHeight * mBitmapWidth;
			int pixelInArrayPosition = indexWidth + indexHeightMultiplayerInArray;
			int updateInterval = (int) Math.max(1, (tryLimit * 0.01f));

			for (int countOfRandomPositions = 0; countOfRandomPositions < tryLimit; countOfRandomPositions++) {
				indexWidth = randomNumbers.nextInt(mBitmapWidth);
				indexHeight = randomNumbers.nextInt(mBitmapHeight);
				indexHeightMultiplayerInArray = indexHeight * mBitmapWidth;
				pixelInArrayPosition = indexWidth + indexHeightMultiplayerInArray;
				if (mBitmapPixelArray[pixelInArrayPosition] != TRANSPARENT) {
					updateCroppingBounds(indexWidth, indexHeight);
				}

				if ((countOfRandomPositions % updateInterval) == 0) {
					publishProgress(countOfRandomPositions / updateInterval);
				}
			}
		}

		private void updateCroppingBounds(int cropWidthXPosition, int cropHeightYPosition) {
			mCropBoundWidthXLeft = Math.min(cropWidthXPosition, mCropBoundWidthXLeft);
			mCropBoundWidthXRight = Math.max(cropWidthXPosition, mCropBoundWidthXRight);

			mCropBoundHeightYTop = Math.min(cropHeightYPosition, mCropBoundHeightYTop);
			mCropBoundHeightYBottom = Math.max(cropHeightYPosition, mCropBoundHeightYBottom);
		}

		@Override
		protected void onProgressUpdate(Integer... pixelsDone) {
			Log.i(PaintroidApplication.TAG, "Percentage: " + pixelsDone[0]);
			mCropProgressDialogue.setProgress(pixelsDone[0]);
			if (pixelsDone.length > 1) {
				mCropProgressDialogue.setSecondaryProgress(pixelsDone[1]);
			}
		}

		@Override
		protected void onPostExecute(Void nothing) {
			mCropRunFinished = true;
			mCropProgressDialogue.dismiss();
			mBitmapPixelArray = null;
			displayCroppingInformation();
		}
	}

}
