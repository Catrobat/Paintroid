/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.helper.FillAlgorithm;

public class FillCommand extends BaseCommand {
	private static final int EMPTY_COMMAND_LIST_LENGTH = 1;
	private float mColorTolerance;
	public static final int COLOR_TOLERANCE = 50;

	private Point mClickedPixel;

	public FillCommand(Point clickedPixel, Paint currentPaint, float colorTolerance) {
		super(currentPaint);
		mClickedPixel = clickedPixel;
		mColorTolerance = colorTolerance;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {

		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mClickedPixel == null) {
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}

		Bitmap emptyImage = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		if (bitmap.sameAs(emptyImage)) {
			canvas.drawColor(mPaint.getColor());
			Log.w(PaintroidApplication.TAG,
					"Fill Command color: " + mPaint.getColor());
		} else {
			int replacementColor = bitmap.getPixel(mClickedPixel.x, mClickedPixel.y);
			int targetColor = mPaint.getColor();
			FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, mClickedPixel, targetColor, replacementColor, mColorTolerance);
			fillAlgorithm.performFilling();
		}

		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
