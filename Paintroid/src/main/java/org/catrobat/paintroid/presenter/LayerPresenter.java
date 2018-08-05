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

import android.widget.Toast;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.contract.LayerContracts.LayerViewHolder;
import org.catrobat.paintroid.contract.LayerContracts.Model;
import org.catrobat.paintroid.ui.dragndrop.DragAndDropPresenter;
import org.catrobat.paintroid.ui.dragndrop.ListItemLongClickHandler;

import java.util.ArrayList;
import java.util.List;

public class LayerPresenter implements LayerContracts.Presenter, DragAndDropPresenter {
	private static final int MAX_LAYERS = 4;
	private final CommandManager commandManager;
	private final CommandFactory commandFactory;
	private final Model model;
	private ListItemLongClickHandler listItemLongClickHandler;
	private LayerContracts.LayerMenuViewHolder layerMenuViewHolder;
	private LayerContracts.Adapter adapter;
	private List<LayerContracts.Layer> layers;
	private LayerContracts.Navigator navigator;

	public LayerPresenter(Model model, ListItemLongClickHandler listItemLongClickHandler, LayerContracts.LayerMenuViewHolder layerMenuViewHolder, CommandManager commandManager, CommandFactory commandFactory, LayerContracts.Navigator navigator) {
		this.model = model;
		this.listItemLongClickHandler = listItemLongClickHandler;
		this.layerMenuViewHolder = layerMenuViewHolder;
		this.commandManager = commandManager;
		this.commandFactory = commandFactory;
		this.layers = new ArrayList<>(model.getLayers());
		this.navigator = navigator;
	}

	public void setAdapter(LayerContracts.Adapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void onBindLayerViewHolderAtPosition(int position, LayerViewHolder viewHolder) {
		LayerContracts.Layer layer = getLayerItem(position);

		if (layer == model.getCurrentLayer()) {
			viewHolder.setSelected();
		} else {
			viewHolder.setDeselected();
		}

		viewHolder.setBitmap(layer.getBitmap());
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
	public void onLongClickLayerAtPosition(int position, LayerViewHolder viewHolder) {
		if (getLayerCount() > 1) {
			listItemLongClickHandler.handleOnItemLongClick(position, viewHolder.getView());
		}
	}

	@Override
	public void onClickLayerAtPosition(int position, LayerViewHolder viewHolder) {
		if (position != model.getLayerIndexOf(model.getCurrentLayer())) {
			commandManager.addCommand(commandFactory.createSelectLayerCommand(position));
		}
	}

	@Override
	public int swapItemsVisually(int position, int swapWith) {
		LayerContracts.Layer tempLayer = layers.get(position);
		layers.set(position, layers.get(swapWith));
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
		adapter.getViewHolderAt(mergeWith).setMergable();
	}

	@Override
	public void invalidate() {
		synchronized (model) {
			layers.clear();
			layers.addAll(model.getLayers());
		}
		refreshLayerMenuViewHolder();
		adapter.notifyDataSetChanged();
	}
}
