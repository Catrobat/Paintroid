/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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

import java.util.List;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class LoadBitmapListCommand implements Command {
	private List<Bitmap> loadedImageList;

	public LoadBitmapListCommand(List<Bitmap> newBitmapList) {
		loadedImageList = newBitmapList;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {

		int counter = 0;
		for (Bitmap current : loadedImageList) {
			Layer currentLayer = new Layer(current.copy(ARGB_8888, true));
			layerModel.addLayerAt(counter, currentLayer);

			counter++;
		}

		layerModel.setCurrentLayer(layerModel.getLayerAt(0));
	}

	@Override
	public void freeResources() {
	}
}
