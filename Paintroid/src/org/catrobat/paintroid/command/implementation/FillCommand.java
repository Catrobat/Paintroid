package org.catrobat.paintroid.command.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.helper.floodfill.QueueLinearFloodFiller;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class FillCommand extends BaseCommand {

	private static final float SELECTION_THRESHOLD = 50.0f;
	private Point mClickedPixel;

	public FillCommand(Point clickedPixel, Paint currentPaint) {
		super(currentPaint);
		mClickedPixel = clickedPixel;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mClickedPixel == null) {
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}

		if ((mClickedPixel.x > bitmap.getWidth())
				|| (mClickedPixel.y > bitmap.getHeight())) {
			Log.i(PaintroidApplication.TAG,
					"FillTool: ignoring click. click outside the bitmap");
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
		}

		int colorToReplace = bitmap.getPixel(mClickedPixel.x, mClickedPixel.y);

		int pixels[] = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());

		QueueLinearFloodFiller.floodFill(pixels, bitmap.getWidth(),
				bitmap.getHeight(), mClickedPixel, colorToReplace,
				mPaint.getColor(), SELECTION_THRESHOLD);

		bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
