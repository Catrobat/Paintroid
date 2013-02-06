package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;
import java.util.Queue;

import org.catrobat.paintroid.PaintroidApplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class FillCommand extends BaseCommand {

	private static final float THRESHOLD = 50f;

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

		fillAreaWithColor(pixels, bitmap.getWidth(), bitmap.getHeight(),
				mClickedPixel, colorToReplace, mPaint.getColor());

		bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	private void fillAreaWithColor(int[] pixels, int width, int height,
			Point pt, int targetColor, int replacementColor) {
		Queue<Point> q = new LinkedList<Point>();
		q.add(pt);
		while (q.size() > 0) {
			Point n = q.poll();
			if (pixels[width * n.y + n.x] != targetColor) {
				continue;
			}

			Point w = n, e = new Point(n.x + 1, n.y);
			while ((w.x > 0)
					&& (isSimilarColor(pixels[width * w.y + w.x], targetColor))) {
				pixels[width * w.y + w.x] = replacementColor;
				if ((w.y > 0)
						&& (isSimilarColor(pixels[width * (w.y - 1) + w.x],
								targetColor))) {
					q.add(new Point(w.x, w.y - 1));
				}
				if ((w.y < height - 1)
						&& (isSimilarColor(pixels[width * (w.y + 1) + w.x],
								targetColor))) {
					q.add(new Point(w.x, w.y + 1));
				}
				w.x--;
			}
			while ((e.x < width - 1)
					&& (isSimilarColor(pixels[width * e.y + e.x], targetColor))) {
				pixels[width * e.y + e.x] = replacementColor;

				if ((e.y > 0)
						&& (isSimilarColor(pixels[width * (e.y - 1) + e.x],
								targetColor))) {
					q.add(new Point(e.x, e.y - 1));
				}
				if ((e.y < height - 1)
						&& (isSimilarColor(pixels[width * (e.y + 1) + e.x],
								targetColor))) {
					q.add(new Point(e.x, e.y + 1));
				}
				e.x++;
			}
		}
	}

	private boolean isSimilarColor(int color1, int color2) {
		int red1 = Color.red(color1);
		int red2 = Color.red(color2);
		int green1 = Color.green(color1);
		int green2 = Color.green(color2);
		int blue1 = Color.blue(color1);
		int blue2 = Color.blue(color2);

		double averageDiff = Math
				.sqrt(Math.pow((red2 - red1), 2)
						+ Math.pow((green2 - green1), 2)
						+ Math.pow((blue2 - blue1), 2));

		return averageDiff < THRESHOLD;

	}
}
