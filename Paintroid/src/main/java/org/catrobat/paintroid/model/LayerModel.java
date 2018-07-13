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
import android.graphics.Canvas;

import org.catrobat.paintroid.contract.LayerContracts;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class LayerModel implements LayerContracts.Model {
	private final List<LayerContracts.Layer> layers;
	private LayerContracts.Layer currentLayer;
	private int width;
	private int height;

	public LayerModel() {
		this.layers = new ArrayList<>();
		this.currentLayer = null;
	}

	@Override
	public List<LayerContracts.Layer> getLayers() {
		return layers;
	}

	@Override
	public LayerContracts.Layer getCurrentLayer() {
		return currentLayer;
	}

	@Override
	public void setCurrentLayer(LayerContracts.Layer layer) {
		this.currentLayer = layer;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void reset() {
		layers.clear();
	}

	@Override
	public int getLayerCount() {
		return layers.size();
	}

	@Override
	public LayerContracts.Layer getLayerAt(int index) {
		return layers.get(index);
	}

	@Override
	public int getLayerIndexOf(LayerContracts.Layer layer) {
		return layers.indexOf(layer);
	}

	@Override
	public void addLayerAt(int index, LayerContracts.Layer layer) {
		layers.add(index, layer);
	}

	@Override
	public ListIterator<LayerContracts.Layer> listIterator(int index) {
		return layers.listIterator(index);
	}

	@Override
	public void setLayerAt(int position, LayerContracts.Layer layer) {
		layers.set(position, layer);
	}

	@Override
	public void removeLayerAt(int position) {
		layers.remove(position);
	}

	public static Bitmap getBitmapOfAllLayersToSave(List<LayerContracts.Layer> layers) {
		if (layers.size() == 0) {
			return null;
		}
		Bitmap referenceBitmap = layers.get(0).getBitmap();
		Bitmap bitmap = Bitmap.createBitmap(referenceBitmap.getWidth(), referenceBitmap.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		ListIterator<LayerContracts.Layer> layerListIterator = layers.listIterator(layers.size());

		while (layerListIterator.hasPrevious()) {
			LayerContracts.Layer layer = layerListIterator.previous();
			canvas.drawBitmap(layer.getBitmap(), 0, 0, null);
		}

		return bitmap;
	}
}
