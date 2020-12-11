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

package org.catrobat.paintroid.presenter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.contract.LayerContracts.LayerViewHolder;
import org.catrobat.paintroid.contract.LayerContracts.Model;
import org.catrobat.paintroid.controller.DefaultToolController;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.dragndrop.DragAndDropPresenter;
import org.catrobat.paintroid.ui.dragndrop.ListItemLongClickHandler;
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder;

import java.util.ArrayList;
import java.util.List;

public class LayerPresenter implements LayerContracts.Presenter, DragAndDropPresenter {
	private static final String TAG = LayerPresenter.class.getSimpleName();
	private static final int MAX_LAYERS = 4;
	private final CommandManager commandManager;
	private final CommandFactory commandFactory;
	private final Model model;
	private ListItemLongClickHandler listItemLongClickHandler;
	private LayerContracts.LayerMenuViewHolder layerMenuViewHolder;
	private LayerContracts.Adapter adapter;
	private List<LayerContracts.Layer> layers;
	private LayerContracts.Navigator navigator;
	private DrawingSurface drawingSurface;
	private DefaultToolController defaultToolController;
	private BottomNavigationViewHolder bottomNavigationViewHolder;

	public LayerPresenter(Model model, ListItemLongClickHandler listItemLongClickHandler, LayerContracts.LayerMenuViewHolder layerMenuViewHolder, CommandManager commandManager, CommandFactory commandFactory, LayerContracts.Navigator navigator) {
		this.model = model;
		this.listItemLongClickHandler = listItemLongClickHandler;
		this.layerMenuViewHolder = layerMenuViewHolder;
		this.commandManager = commandManager;
		this.commandFactory = commandFactory;
		this.layers = new ArrayList<>(model.getLayers());
		this.navigator = navigator;
	}

	@Override
	public LayerPresenter getPresenter() {
		return this;
	}

	@Override
	public void setAdapter(LayerContracts.Adapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void setDrawingSurface(DrawingSurface drawingSurface) {
		this.drawingSurface = drawingSurface;
	}

	@Override
	public void setDefaultToolController(DefaultToolController defaultToolController) {
		this.defaultToolController = defaultToolController;
	}

	@Override
	public void setbottomNavigationViewHolder(BottomNavigationViewHolder bottomNavigationViewHolder) {
		this.bottomNavigationViewHolder = bottomNavigationViewHolder;
	}

	@Override
	public void onBindLayerViewHolderAtPosition(int position, LayerViewHolder viewHolder) {
		LayerContracts.Layer layer = getLayerItem(position);

		if (layer == model.getCurrentLayer()) {
			viewHolder.setSelected(position, bottomNavigationViewHolder, defaultToolController);
		} else {
			viewHolder.setDeselected();
		}
		if (!layers.get(position).getCheckBox()) {
			viewHolder.setBitmap(layer.getTransparentBitmap());
			viewHolder.setCheckBox(false);
		} else {
			viewHolder.setBitmap(layer.getBitmap());
			viewHolder.setCheckBox(true);
		}
	}

	@Override
	public void refreshLayerMenuViewHolder() {
		if (getLayerCount() < MAX_LAYERS) {
			layerMenuViewHolder.enableAddLayerButton();
		} else {
			layerMenuViewHolder.disableAddLayerButton();
		}

		if (getLayerCount() > 1) {
			layerMenuViewHolder.enableRemoveLayerButton();
		} else {
			layerMenuViewHolder.disableRemoveLayerButton();
		}
	}

	@Override
	public int getLayerCount() {
		return layers.size();
	}

	@Override
	public LayerContracts.Layer getLayerItem(int position) {
		return layers.get(position);
	}

	@Override
	public long getLayerItemId(int position) {
		return position;
	}

	@Override
	public void addLayer() {
		if (getLayerCount() < MAX_LAYERS) {
			commandManager.addCommand(commandFactory.createAddLayerCommand());
		} else {
			navigator.showToast(R.string.layer_too_many_layers, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void removeLayer() {
		if (getLayerCount() > 1) {
			LayerContracts.Layer layerToDelete = model.getCurrentLayer();
			int index = model.getLayerIndexOf(layerToDelete);
			commandManager.addCommand(commandFactory.createRemoveLayerCommand(index));
		}
	}

	@Override
	public void hideLayer(int position) {
		LayerContracts.Layer destinationLayer = model.getLayerAt(position);
		Bitmap bitmapCopy = destinationLayer.getTransparentBitmap();
		destinationLayer.switchBitmaps(false);
		destinationLayer.setBitmap(bitmapCopy);
		destinationLayer.setCheckBox(false);

		drawingSurface.refreshDrawingSurface();

		if (model.getCurrentLayer().equals(destinationLayer)) {
			defaultToolController.switchTool(ToolType.HAND, false);
			bottomNavigationViewHolder.showCurrentTool(ToolType.HAND);
		}
	}

	@Override
	public void unhideLayer(int position, LayerViewHolder viewHolder) {
		LayerContracts.Layer destinationLayer = model.getLayerAt(position);
		destinationLayer.switchBitmaps(true);
		Bitmap bitmapToAdd = destinationLayer.getBitmap();
		destinationLayer.setBitmap(bitmapToAdd);
		destinationLayer.setCheckBox(true);

		viewHolder.setBitmap(bitmapToAdd);

		drawingSurface.refreshDrawingSurface();

		if (model.getCurrentLayer().equals(destinationLayer)) {
			defaultToolController.switchTool(ToolType.BRUSH, false);
			bottomNavigationViewHolder.showCurrentTool(ToolType.BRUSH);
		}
	}

	@Override
	public int swapItemsVisually(int position, int swapWith) {
		LayerContracts.Layer tempLayer = layers.get(position);
		LayerContracts.Layer swapWithLayer = layers.get(swapWith);
		layers.set(position, swapWithLayer);
		layers.set(swapWith, tempLayer);
		return swapWith;
	}

	@Override
	public void mergeItems(int position, int mergeWith) {
		LayerContracts.Layer actualLayer = layers.get(mergeWith);
		int actualPosition = model.getLayerIndexOf(actualLayer);
		if (position != actualPosition) {
			commandManager.addCommand(commandFactory.createMergeLayersCommand(position, actualPosition));

			navigator.showToast(R.string.layer_merged, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void reorderItems(int position, int targetPosition) {
		if (position != targetPosition) {
			commandManager.addCommand(commandFactory.createReorderLayersCommand(position, targetPosition));
		}
	}

	@Override
	public void markMergeable(int position, int mergeWith) {
		if (!isPositionValid(position) || !isPositionValid(mergeWith)) {
			Log.e(TAG, "onLongClickLayerAtPosition at invalid position");
			return;
		}
		adapter.getViewHolderAt(mergeWith).setMergable();
	}

	@Override
	public void onLongClickLayerAtPosition(int position, View view) {
		if (!isPositionValid(position)) {
			Log.e(TAG, "onLongClickLayerAtPosition at invalid position");
			return;
		}
		boolean isAllowedToLongclick = true;
		for (int i = 0; i < layers.size(); i++) {
			if (!layers.get(i).getCheckBox()) {
				isAllowedToLongclick = false;
			}
		}
		if (isAllowedToLongclick) {
			if (getLayerCount() > 1) {
				listItemLongClickHandler.handleOnItemLongClick(position, view);
			}
		} else {
			navigator.showToast(R.string.no_longclick_on_hidden_layer, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void onClickLayerAtPosition(int position, View view) {
		if (!isPositionValid(position)) {
			Log.e(TAG, "onClickLayerAtPosition at invalid position");
			return;
		}

		if (position != model.getLayerIndexOf(model.getCurrentLayer())) {
			commandManager.addCommand(commandFactory.createSelectLayerCommand(position));
		}
	}

	private boolean isPositionValid(int position) {
		return position >= 0 && position < layers.size();
	}

	@Override
	public void invalidate() {
		synchronized (model) {
			layers.clear();
			layers.addAll(model.getLayers());
		}
		refreshLayerMenuViewHolder();
		adapter.notifyDataSetChanged();

		listItemLongClickHandler.stopDragging();
	}
}
