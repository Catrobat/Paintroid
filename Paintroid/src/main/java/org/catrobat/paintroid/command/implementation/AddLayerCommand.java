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
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.Layer;

public class AddLayerCommand implements Command {
	private CommonFactory commonFactory;

	public AddLayerCommand(CommonFactory commonFactory) {
		this.commonFactory = commonFactory;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		Layer layer = new Layer(commonFactory.createBitmap(layerModel.getWidth(), layerModel.getHeight(),
				Bitmap.Config.ARGB_8888));
		layerModel.addLayerAt(0, layer);
		layerModel.setCurrentLayer(layer);
	}

	@Override
	public void freeResources() {
	}
}
