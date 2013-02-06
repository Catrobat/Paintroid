package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
		fillAreaWithColor(bitmap, mClickedPixel, colorToReplace,
				mPaint.getColor());

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	private void fillAreaWithColor(Bitmap bmp, Point pt, int targetColor,
			int replacementColor) {
		Queue<Point> q = new LinkedList<Point>();
		q.add(pt);
		while (q.size() > 0) {
			Point n = q.poll();
			if (bmp.getPixel(n.x, n.y) != targetColor) {
				continue;
			}

			Point w = n, e = new Point(n.x + 1, n.y);
			while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
				bmp.setPixel(w.x, w.y, replacementColor);
				if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor)) {
					q.add(new Point(w.x, w.y - 1));
				}
				if ((w.y < bmp.getHeight() - 1)
						&& (bmp.getPixel(w.x, w.y + 1) == targetColor)) {
					q.add(new Point(w.x, w.y + 1));
				}
				w.x--;
			}
			while ((e.x < bmp.getWidth() - 1)
					&& (bmp.getPixel(e.x, e.y) == targetColor)) {
				bmp.setPixel(e.x, e.y, replacementColor);

				if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor)) {
					q.add(new Point(e.x, e.y - 1));
				}
				if ((e.y < bmp.getHeight() - 1)
						&& (bmp.getPixel(e.x, e.y + 1) == targetColor)) {
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

		double diff = Math
				.sqrt(Math.pow((red2 - red1), 2)
						+ Math.pow((green2 - green1), 2)
						+ Math.pow((blue2 - blue1), 2));
		return diff < SELECTION_THRESHOLD;

	}
}
