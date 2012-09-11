package org.catrobat.paintroid.command.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.Utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;

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
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mFileToStoredBitmap != null) {
			PaintroidApplication.DRAWING_SURFACE.setBitmap(Utils.getBitmapFromFile(mFileToStoredBitmap));
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_DONE);
			return;
		}
		try {

			if (mCropCoordinateXRight < mCropCoordinateXLeft) {
				Log.e(PaintroidApplication.TAG, "coordinate X is larger than coordinate X left");
				setChanged();
				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mCropCoordinateXRight < 0 || mCropCoordinateXLeft < 0 || mCropCoordinateXRight > bitmap.getWidth()
					|| mCropCoordinateXLeft > bitmap.getWidth()) {
				Log.e(PaintroidApplication.TAG, "coordinate X is out of bitmap scope");
				setChanged();
				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mCropCoordinateYBottom < mCropCoordinateYTop) {
				Log.e(PaintroidApplication.TAG, "coordinate Y bottom is larger than coordinate Y top");
				setChanged();
				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mCropCoordinateYBottom < 0 || mCropCoordinateYTop < 0 || mCropCoordinateYBottom > bitmap.getHeight()
					|| mCropCoordinateYTop > bitmap.getHeight()) {
				Log.e(PaintroidApplication.TAG, "coordinate Y is out of bitmap scope");
				setChanged();
				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mCropCoordinateXLeft == 0 && mCropCoordinateXRight == bitmap.getWidth() - 1
					&& mCropCoordinateYBottom == bitmap.getHeight() - 1 && mCropCoordinateYTop == 0) {
				Log.e(PaintroidApplication.TAG, " no need to crop ");
				setChanged();
				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}

			Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, (int) mCropCoordinateXLeft, (int) mCropCoordinateYTop,
					(int) (mCropCoordinateXRight - mCropCoordinateXLeft + 1), (int) (mCropCoordinateYBottom
							- mCropCoordinateYTop + 1));
			PaintroidApplication.DRAWING_SURFACE.setBitmap(croppedBitmap);

			if (mFileToStoredBitmap == null) {
				mBitmap = croppedBitmap.copy(Config.ARGB_8888, true);
				storeBitmap();
			}

		} catch (Exception e) {
			Log.e(PaintroidApplication.TAG, "failed to crop bitmap:" + e.getMessage());
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
		}
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
