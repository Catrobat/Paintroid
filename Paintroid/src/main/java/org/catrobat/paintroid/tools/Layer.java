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

package org.catrobat.paintroid.tools;

import android.graphics.Bitmap;

import org.catrobat.paintroid.PaintroidApplication;

public class Layer {
	private final int layerID;
	private Bitmap bitmap;
	private boolean isSelected;

	public Layer(int layerID, Bitmap bitmap) {
		this.layerID = layerID;
		this.bitmap = bitmap;
		isSelected = false;
	}

	public boolean getSelected() {
		return isSelected;
	}

	public void setSelected(boolean value) {
		isSelected = value;
	}

	public int getLayerID() {
		return layerID;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;

		if (isSelected && PaintroidApplication.drawingSurface != null) {
			PaintroidApplication.drawingSurface.setBitmap(bitmap);
		}
	}
}
