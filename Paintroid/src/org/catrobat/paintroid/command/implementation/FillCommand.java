package org.catrobat.paintroid.command.implementation;

import org.catrobat.paintroid.tools.helper.floodfill.QueueLinearFloodFiller;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class FillCommand extends BaseCommand {

	private static final double SELECTION_THRESHOLD = 10.0;
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
		QueueLinearFloodFiller.floodFill(bitmap, mClickedPixel, colorToReplace,
				mPaint.getColor(), SELECTION_THRESHOLD);

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
