package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class FillCommand extends BaseCommand {

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

		int replacementColor = bitmap
				.getPixel(mClickedPixel.x, mClickedPixel.y);
		fillAreaWithColor(bitmap, mClickedPixel, mPaint.getColor(),
				replacementColor);

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
}
