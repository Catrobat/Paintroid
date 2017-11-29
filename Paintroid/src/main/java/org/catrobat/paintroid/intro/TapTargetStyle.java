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

package org.catrobat.paintroid.intro;

import android.graphics.Typeface;

import org.catrobat.paintroid.R;

public enum TapTargetStyle {
	HEADER_STYLE(R.style.IntroHeader), TEXT_STYLE(R.style.IntroText);

	private int resourceId;
	private int textColor;
	private int textSize;
	private Typeface typeface;

	TapTargetStyle(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getResourceId() {
		return resourceId;
	}

	public Typeface getTypeface() {
		return typeface;
	}

	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}
}
