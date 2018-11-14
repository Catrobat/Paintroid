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

package org.catrobat.paintroid;

import org.catrobat.paintroid.contract.LayerContracts;

import java.util.List;

public class LayerModelWrapper {

	public int getHeight() {
		return PaintroidApplication.layerModel.getHeight();
	}

	public int getWidth() {
		return PaintroidApplication.layerModel.getWidth();
	}

	public List<LayerContracts.Layer> getLayers() {
		return PaintroidApplication.layerModel.getLayers();
	}

	public LayerContracts.Layer getCurrentLayer() {
		return PaintroidApplication.layerModel.getCurrentLayer();
	}

	public void reset() {
		PaintroidApplication.layerModel.reset();
	}

	public void addLayerAt(int index, LayerContracts.Layer layer) {
		PaintroidApplication.layerModel.addLayerAt(index, layer);
	}

	public void setCurrentLayer(LayerContracts.Layer layer) {
		PaintroidApplication.layerModel.setCurrentLayer(layer);
	}
}
