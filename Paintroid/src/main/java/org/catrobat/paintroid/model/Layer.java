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

package org.catrobat.paintroid.model;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.catrobat.paintroid.contract.LayerContracts;

public class Layer implements LayerContracts.Layer {
	private Bitmap bitmap;
	private Bitmap transparentBitmap;
	private boolean checkBox;

	public Layer(Bitmap bitmap) {
		this.bitmap = bitmap;
		if (bitmap != null) {
			this.transparentBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
					Bitmap.Config.ARGB_8888);
		}
		this.checkBox = true;
	}

	public void setCheckBox(boolean setTo) {
		this.checkBox = setTo;
	}

	public boolean getCheckBox() {
		return checkBox;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public Bitmap getTransparentBitmap() {
		return transparentBitmap;
	}

	public void switchBitmaps(boolean isUnhide) {
		Bitmap tmpBitmap = transparentBitmap.copy(transparentBitmap.getConfig(), transparentBitmap.isMutable());
		this.transparentBitmap = bitmap;
		this.bitmap = tmpBitmap;
		if (isUnhide) {
			transparentBitmap.eraseColor(Color.TRANSPARENT);
		}
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
