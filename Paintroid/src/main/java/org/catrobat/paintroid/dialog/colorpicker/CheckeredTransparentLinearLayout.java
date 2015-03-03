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

package org.catrobat.paintroid.dialog.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CheckeredTransparentLinearLayout extends LinearLayout {

	private Paint mColorPaint = new Paint();

	public CheckeredTransparentLinearLayout(Context context) {
		super(context);
	}

	public CheckeredTransparentLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void updateBackground() {
		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap background = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);
		background.eraseColor(ColorPickerDialog.mNewColor);
		Canvas checkerdBackgroundCanvas = new Canvas(background);

		Rect colorRect = new Rect(0, 0, getWidth(), getHeight());
		if (ColorPickerDialog.mBackgroundPaint != null) {
			checkerdBackgroundCanvas.drawRect(colorRect,
					ColorPickerDialog.mBackgroundPaint);
		}
		mColorPaint.setColor(ColorPickerDialog.mNewColor);
		checkerdBackgroundCanvas.drawPaint(mColorPaint);
		setBackgroundDrawable(new BitmapDrawable(getResources(), background));
	}
}
