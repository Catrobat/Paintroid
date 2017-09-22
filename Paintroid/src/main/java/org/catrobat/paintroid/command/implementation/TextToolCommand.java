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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

public class TextToolCommand extends BaseCommand {
	protected final String[] mMultilineText;
	protected final Paint mTextPaint;
	protected final float mBoxOffset;
	protected final float mBoxWidth;
	protected final float mBoxHeight;
	protected final PointF mToolPosition;
	protected final float mRotationAngle;

	public TextToolCommand(String[] multilineText, Paint textPaint, float boxOffset, float boxWidth,
	                       float boxHeight, PointF toolPosition, float rotationAngle) {
		super(new Paint());

		mMultilineText = multilineText;
		mTextPaint = textPaint;
		mBoxOffset = boxOffset;
		mBoxWidth = boxWidth;
		mBoxHeight = boxHeight;
		mToolPosition = toolPosition;
		mRotationAngle = rotationAngle;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);

		canvas.save();

		canvas.translate(mToolPosition.x, mToolPosition.y);
		canvas.rotate(mRotationAngle);

		float textDescent = mTextPaint.descent();
		float textAscent = mTextPaint.ascent();

		float textHeight = textDescent - textAscent;
		float textBoxHeight = textHeight * mMultilineText.length + 2*mBoxOffset;

		float maxTextWidth = 0;
		for (String str : mMultilineText) {
			float textWidth = mTextPaint.measureText(str);
			if (textWidth > maxTextWidth) {
				maxTextWidth = textWidth;
			}
		}
		float textBoxWidth = maxTextWidth + 2*mBoxOffset;

		Bitmap textBitmap = Bitmap.createBitmap((int) textBoxWidth, (int) textBoxHeight,
				Bitmap.Config.ARGB_8888);
		Canvas textCanvas = new Canvas(textBitmap);

		for (int i = 0; i < mMultilineText.length; i++) {
			textCanvas.drawText(mMultilineText[i], mBoxOffset, mBoxOffset - textAscent + textHeight*i, mTextPaint);
		}

		Rect srcRect = new Rect(0, 0, (int) textBoxWidth,(int) textBoxHeight);
		Rect dstRect = new Rect((int)(-mBoxWidth/2.0f), (int)(-mBoxHeight/2.0f),
				(int)(mBoxWidth/2.0f), (int)(mBoxHeight/2.0f));
		canvas.drawBitmap(textBitmap, srcRect, dstRect, mTextPaint);

		canvas.restore();

		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

}
