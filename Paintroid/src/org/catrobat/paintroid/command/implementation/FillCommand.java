package org.catrobat.paintroid.command.implementation;

import org.catrobat.paintroid.tools.helper.floodfill.QueueLinearFloodFiller;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

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
