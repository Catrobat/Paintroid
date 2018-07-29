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

public class MergeLayersCommand implements Command {
	private final int position;
	private final int mergeWith;

	public MergeLayersCommand(int position, int mergeWith) {
		this.position = position;
		this.mergeWith = mergeWith;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		LayerContracts.Layer sourceLayer = layerModel.getLayerAt(position);
		LayerContracts.Layer destinationLayer = layerModel.getLayerAt(mergeWith);

		Bitmap destinationBitmap = destinationLayer.getBitmap();
		Bitmap copyBitmap = destinationBitmap.copy(destinationBitmap.getConfig(), true);

		Canvas copyCanvas = new Canvas(copyBitmap);
		copyCanvas.drawBitmap(sourceLayer.getBitmap(), 0, 0, null);
		destinationLayer.setBitmap(copyBitmap);
		layerModel.removeLayerAt(position);

		if (sourceLayer == layerModel.getCurrentLayer()) {
			layerModel.setCurrentLayer(destinationLayer);
		}
	}

	@Override
	public void freeResources() {
	}
}
