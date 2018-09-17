/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.contract.LayerContracts;

public class TextToolCommand implements Command {
	private final String[] multilineText;
	private final Paint textPaint;
	private final float boxOffset;
	private final float boxWidth;
	private final float boxHeight;
	private final PointF toolPosition;
	private final float rotationAngle;

	public TextToolCommand(String[] multilineText, Paint textPaint, float boxOffset,
			float boxWidth, float boxHeight, PointF toolPosition, float rotationAngle) {
		this.multilineText = multilineText.clone();
		this.textPaint = textPaint;
		this.boxOffset = boxOffset;
		this.boxWidth = boxWidth;
		this.boxHeight = boxHeight;
		this.toolPosition = toolPosition;
		this.rotationAngle = rotationAngle;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		canvas.save();

		canvas.translate(toolPosition.x, toolPosition.y);
		canvas.rotate(rotationAngle);

		float textDescent = textPaint.descent();
		float textAscent = textPaint.ascent();

		float textHeight = textDescent - textAscent;
		float textBoxHeight = textHeight * multilineText.length + 2 * boxOffset;

		float maxTextWidth = 0;
		for (String str : multilineText) {
			float textWidth = textPaint.measureText(str);
			if (textWidth > maxTextWidth) {
				maxTextWidth = textWidth;
			}
		}
		float textBoxWidth = maxTextWidth + 2 * boxOffset;

		Bitmap textBitmap = Bitmap.createBitmap((int) textBoxWidth, (int) textBoxHeight,
				Bitmap.Config.ARGB_8888);
		Canvas textCanvas = new Canvas(textBitmap);

		for (int i = 0; i < multilineText.length; i++) {
			textCanvas.drawText(multilineText[i], boxOffset, boxOffset - textAscent + textHeight * i, textPaint);
		}

		Rect srcRect = new Rect(0, 0, (int) textBoxWidth, (int) textBoxHeight);
		Rect dstRect = new Rect((int) (-boxWidth / 2.0f), (int) (-boxHeight / 2.0f),
				(int) (boxWidth / 2.0f), (int) (boxHeight / 2.0f));
		canvas.drawBitmap(textBitmap, srcRect, dstRect, textPaint);

		canvas.restore();
	}

	@Override
	public void freeResources() {
	}
}
