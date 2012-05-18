package at.tugraz.ist.paintroid.tools.implementation;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.Toast;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class CropTool extends BaseToolWithShape {

	protected ProgressBar mProgressbar;
	protected int mTotalPixelCount;
	protected ProgressDialog mCorpProgressDialogue;
	DrawingSurface mDrawingSurface;
	protected int mCropBoundWidthXLeft;
	protected int mCropBoundWidthXRight = 0;
	protected int mCropBoundHeightYTop;
	protected int mCropBoundHeightYBottom = 0;
	protected Paint mLinePaint;
	protected final int mLineStrokeWidth = 5;

	public CropTool(Context context, ToolType toolType, DrawingSurface drawingSurface) {
		super(context, toolType);
		mDrawingSurface = drawingSurface;
		mTotalPixelCount = mDrawingSurface.getBitmap().getWidth() * mDrawingSurface.getBitmap().getHeight();
		mCropBoundWidthXLeft = mDrawingSurface.getBitmap().getWidth();
		mCropBoundHeightYTop = mDrawingSurface.getBitmap().getHeight();
		mCorpProgressDialogue = new ProgressDialog(context);
		mCorpProgressDialogue.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mCorpProgressDialogue.setMax(100);
		mCorpProgressDialogue.setMessage(context.getString(R.string.crop_progress_text));
		mCorpProgressDialogue.getWindow().setGravity(Gravity.BOTTOM);

		new FindCroppingCoordinatesAsyncTask(drawingSurface.getBitmap()).execute();
	}

	protected class FindCroppingCoordinatesAsyncTask extends AsyncTask<Void, Integer, Void> {

		float mOnePercentOfBitmapPixel = 1;
		private int[] mBitmapPixelArray;
		private int mBitmapWidth = -1;
		private int mBitmapHeight = -1;

		// private Vector mPixelsFoundPosition = new Vector();

		FindCroppingCoordinatesAsyncTask(Bitmap originalBitmap) {
			mOnePercentOfBitmapPixel = Math.max(0.01f, (mTotalPixelCount / 100));

			mLinePaint = new Paint();
			mLinePaint.setDither(true);
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setStrokeJoin(Paint.Join.ROUND);
			mBitmapWidth = originalBitmap.getWidth();
			mBitmapHeight = originalBitmap.getHeight();
			int bitmapPixels = mBitmapHeight * mBitmapWidth;
			mBitmapPixelArray = new int[bitmapPixels];

			originalBitmap.getPixels(mBitmapPixelArray, 0, mBitmapWidth, 0, 0, mBitmapWidth, mBitmapHeight);
		}

		@Override
		protected void onPreExecute() {
			mCorpProgressDialogue.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			int percentDone = 0;
			final int TRANSPARENT = Color.TRANSPARENT;
			for (int indexHeight = 0; indexHeight < mBitmapHeight; indexHeight++) {
				int indexHeightMultiplayerInArray = indexHeight * mBitmapWidth;

				for (int indexWidth = 0; indexWidth < mBitmapWidth; indexWidth++) {

					int pixelPosition = indexWidth + indexHeightMultiplayerInArray;
					if (mBitmapPixelArray[pixelPosition] != TRANSPARENT) {
						// mPixelsFoundPosition.add(pixelPosition);
						Log.i(PaintroidApplication.TAG, "pixel found at (indexWidth,indexHeight):" + indexWidth + ","
								+ indexHeight + " Pixel Value: " + mBitmapPixelArray[pixelPosition]);
						mCropBoundWidthXLeft = Math.min(indexWidth, mCropBoundWidthXLeft);
						mCropBoundWidthXRight = Math.max(indexWidth, mCropBoundWidthXRight);

						mCropBoundHeightYTop = Math.min(indexHeight, mCropBoundHeightYTop);
						mCropBoundHeightYBottom = Math.max(indexHeight, mCropBoundHeightYBottom);
					}

					if (percentDone < (int) (pixelPosition / mOnePercentOfBitmapPixel)) {
						percentDone = (int) (pixelPosition / mOnePercentOfBitmapPixel);
						publishProgress(percentDone);
					}
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... pixelsDone) {
			// int percentage = (int) (pixelsDone[0] / mOnePercentOfBitmapPixel);
			Log.i(PaintroidApplication.TAG, "Percentage: " + pixelsDone[0]);
			mCorpProgressDialogue.setProgress(pixelsDone[0]);
			// mCorpProgressDialogue.show();
		}

		@Override
		protected void onPostExecute(Void nothing) {
			mCorpProgressDialogue.dismiss();
			Log.i(PaintroidApplication.TAG, " XLeft: " + mCropBoundWidthXLeft + " XRight: " + mCropBoundWidthXRight
					+ " YTop: " + mCropBoundHeightYTop + " YBottom: " + mCropBoundHeightYBottom);
			CharSequence text = " XLeft: " + mCropBoundWidthXLeft + " XRight: " + mCropBoundWidthXRight + " YTop: "
					+ mCropBoundHeightYTop + " YBottom: " + mCropBoundHeightYBottom;

			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			// Log.i(PaintroidApplication.TAG, mPixelsFoundPosition.toString());
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetInternalState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawShape(Canvas canvas) {
		Rect frameRect = new Rect();
		frameRect.set(mCropBoundWidthXLeft - mLineStrokeWidth, mCropBoundHeightYTop - mLineStrokeWidth,
				mCropBoundWidthXRight + mLineStrokeWidth, mCropBoundHeightYBottom + mLineStrokeWidth);
		mLinePaint.setColor(Color.YELLOW);
		mLinePaint.setStrokeWidth(mLineStrokeWidth);
		canvas.drawRect(frameRect, mLinePaint);
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		drawShape(canvas);

	}

	@Override
	public void attributeButtonClick(int buttonNumber) {

	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		if (buttonNumber == 0) {
			return R.drawable.ic_crop;
		} else if (buttonNumber == 1) {
			return R.drawable.ic_crop;
		}
		return 0;
	}

	@Override
	public int getAttributeButtonColor(int buttonNumber) {
		if (buttonNumber == 2) {
			return Color.TRANSPARENT;
		}
		return super.getAttributeButtonColor(buttonNumber);
	}

}
