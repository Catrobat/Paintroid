package at.tugraz.ist.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.Utils;

public class CropCommand extends BaseCommand {

	private final float mCropCoordinateXLeft;
	private final float mCropCoordinateYTop;
	private final float mCropCoordinateXRight;
	private final float mCropCoordinateYBottom;

	public CropCommand(float cropCoordinaetXLeft, float cropCoordinateYTop, float cropCoordinateXRight,
			float cropCoordinateYBottom) {
		mCropCoordinateXLeft = cropCoordinaetXLeft;
		mCropCoordinateYTop = cropCoordinateYTop;
		mCropCoordinateXRight = cropCoordinateXRight;
		mCropCoordinateYBottom = cropCoordinateYBottom;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		if (mFileToStoredBitmap != null) {
			canvas.setBitmap(Utils.getBitmapFromFile(mFileToStoredBitmap));
			return;
		}
		try {

			if (mCropCoordinateXRight < mCropCoordinateXLeft) {
				Log.e(PaintroidApplication.TAG, "coordinate X is larger than coordinate X left");
				return;
			}
			if (mCropCoordinateXRight < 0 || mCropCoordinateXLeft < 0 || mCropCoordinateXRight > bitmap.getWidth()
					|| mCropCoordinateXLeft > bitmap.getWidth()) {
				Log.e(PaintroidApplication.TAG, "coordinate X is out of bitmap scope");
				return;
			}
			if (mCropCoordinateYBottom < mCropCoordinateYTop) {
				Log.e(PaintroidApplication.TAG, "coordinate Y bottom is larger than coordinate Y top");
				return;
			}
			if (mCropCoordinateYBottom < 0 || mCropCoordinateYTop < 0 || mCropCoordinateYBottom > bitmap.getHeight()
					|| mCropCoordinateYTop > bitmap.getHeight()) {
				Log.e(PaintroidApplication.TAG, "coordinate Y is out of bitmap scope");
				return;
			}
			if (mCropCoordinateXLeft == 0 && mCropCoordinateXRight == bitmap.getWidth() - 1
					&& mCropCoordinateYBottom == bitmap.getHeight() - 1 && mCropCoordinateYTop == 0) {
				Log.e(PaintroidApplication.TAG, " no need to crop ");
				return;
			}

			Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, (int) mCropCoordinateXLeft, (int) mCropCoordinateYTop,
					(int) (mCropCoordinateXRight - mCropCoordinateXLeft + 1), (int) (mCropCoordinateYBottom
							- mCropCoordinateYTop + 1));

			if (PaintroidApplication.DRAWING_SURFACE != null) {
				PaintroidApplication.DRAWING_SURFACE.resetBitmap(croppedBitmap.copy(Config.ARGB_8888, true));
			}

			if (mFileToStoredBitmap == null) {
				mBitmap = croppedBitmap;
				storeBitmap();
			}

		} catch (Exception e) {
			Log.e(PaintroidApplication.TAG, "failed to crop bitmap:" + e.getMessage());
		}
	}
}
