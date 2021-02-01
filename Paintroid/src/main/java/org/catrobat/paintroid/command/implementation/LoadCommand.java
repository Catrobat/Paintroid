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

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.Layer;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class LoadCommand implements Command {
	private Bitmap loadedImage;

	public LoadCommand(Bitmap newBitmap) {
		loadedImage = newBitmap;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		Layer currentLayer = new Layer(loadedImage.copy(ARGB_8888, true));
		layerModel.addLayerAt(0, currentLayer);
		layerModel.setCurrentLayer(currentLayer);
	}

	@Override
	public void freeResources() {
	}
}
