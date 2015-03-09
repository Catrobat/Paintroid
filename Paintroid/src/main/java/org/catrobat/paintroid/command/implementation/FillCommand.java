/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	private static final int EMPTY_COMMAND_LIST_LENGTH = 1;
	private Point mClickedPixel;

	public FillCommand(Point clickedPixel, Paint currentPaint) {
		super(currentPaint);
		mClickedPixel = clickedPixel;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {

		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mClickedPixel == null) {
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}

		if (PaintroidApplication.savedPictureUri == null
				&& PaintroidApplication.commandManager.getNumberOfCommands() == EMPTY_COMMAND_LIST_LENGTH + 1) {
			canvas.drawColor(mPaint.getColor());
			Log.w(PaintroidApplication.TAG,
					"Fill Command color: " + mPaint.getColor());
		} else {
			int colorToReplace = bitmap.getPixel(mClickedPixel.x,
					mClickedPixel.y);
			int pixels[] = new int[bitmap.getWidth() * bitmap.getHeight()];
			bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0,
					bitmap.getWidth(), bitmap.getHeight());

			QueueLinearFloodFiller.floodFill(pixels, bitmap.getWidth(),
					bitmap.getHeight(), mClickedPixel, colorToReplace,
					mPaint.getColor(), SELECTION_THRESHOLD);

			bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0,
					bitmap.getWidth(), bitmap.getHeight());
		}

		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
