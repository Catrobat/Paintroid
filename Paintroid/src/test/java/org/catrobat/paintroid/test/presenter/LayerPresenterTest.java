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

package org.catrobat.paintroid.test.presenter;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.contract.LayerContracts.LayerViewHolder;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.presenter.LayerPresenter;
import org.catrobat.paintroid.ui.dragndrop.ListItemDragHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LayerPresenterTest {

	@Mock
	private ListItemDragHandler listItemDragHandler;

	@Mock
	private LayerContracts.LayerMenuViewHolder layerMenuViewHolder;

	@Mock
	private LayerContracts.Adapter layerAdapter;

	@Mock
	private LayerContracts.Navigator navigator;

	@Mock
	private CommandManager commandManager;

	@Mock
	private CommandFactory commandFactory;

	private LayerModel layerModel;

	private LayerPresenter layerPresenter;

	@Before
	public void setUp() {
		layerModel = new LayerModel();
	}

	private void createPresenter() {
		layerPresenter = new LayerPresenter(layerModel, listItemDragHandler,
				layerMenuViewHolder, commandManager, commandFactory, navigator);
		layerPresenter.setAdapter(layerAdapter);
	}

	@Test
	public void testSetUp() {
		createPresenter();
		verifyZeroInteractions(layerAdapter, layerMenuViewHolder, listItemDragHandler,
				commandManager, commandFactory, navigator);
	}

	@Test
	public void testRefreshLayerMenuViewHolder() {
		layerModel.addLayerAt(0, mock(Layer.class));
		layerModel.addLayerAt(1, mock(Layer.class));

		createPresenter();
		layerPresenter.refreshLayerMenuViewHolder();

		verify(layerMenuViewHolder).enableAddLayerButton();
		verify(layerMenuViewHolder).enableRemoveLayerButton();
		verifyNoMoreInteractions(layerMenuViewHolder);
		verifyZeroInteractions(commandManager, commandFactory, layerAdapter, listItemDragHandler);
	}

	@Test
	public void testRefreshLayerMenuViewHolderOnAddDisabled() {
		for (int i = 0; i < 100; i++) {
			layerModel.addLayerAt(i, mock(Layer.class));
		}

		createPresenter();
		layerPresenter.refreshLayerMenuViewHolder();

		verify(layerMenuViewHolder).disableAddLayerButton();
		verify(layerMenuViewHolder).enableRemoveLayerButton();
		verifyNoMoreInteractions(layerMenuViewHolder);
		verifyZeroInteractions(commandManager, commandFactory, layerAdapter, listItemDragHandler);
	}

	@Test
	public void testRefreshLayerMenuViewHolderOnRemoveDisabled() {
		layerModel.addLayerAt(0, mock(Layer.class));

		createPresenter();
		layerPresenter.refreshLayerMenuViewHolder();

		verify(layerMenuViewHolder).enableAddLayerButton();
		verify(layerMenuViewHolder).disableRemoveLayerButton();
		verifyNoMoreInteractions(layerMenuViewHolder);
		verifyZeroInteractions(commandManager, commandFactory, layerAdapter, listItemDragHandler);
	}

	@Test
	public void testGetLayerCount() {
		layerModel.addLayerAt(0, mock(Layer.class));
		layerModel.addLayerAt(1, mock(Layer.class));
		layerModel.addLayerAt(2, mock(Layer.class));
		layerModel.addLayerAt(3, mock(Layer.class));

		createPresenter();
		int result = layerPresenter.getLayerCount();

		assertEquals(4, result);
	}

	@Test
	public void testGetLayerItem() {
		layerModel.addLayerAt(0, mock(Layer.class));
		layerModel.addLayerAt(1, mock(Layer.class));
		layerModel.addLayerAt(2, mock(Layer.class));
		layerModel.addLayerAt(3, mock(Layer.class));

		createPresenter();
		LayerContracts.Layer result = layerPresenter.getLayerItem(3);

		assertEquals(layerModel.getLayerAt(3), result);
	}

	@Test
	public void testGetLayerItemId() {
		createPresenter();
		assertEquals(3, layerPresenter.getLayerItemId(3));
		assertEquals(0, layerPresenter.getLayerItemId(0));
	}

	@Test
	public void testAddLayer() {
		layerModel.addLayerAt(0, mock(Layer.class));
		layerModel.addLayerAt(1, mock(Layer.class));
		Command command = mock(Command.class);
		when(commandFactory.createAddEmptyLayerCommand()).thenReturn(command);

		createPresenter();
		layerPresenter.addLayer();

		verify(commandManager).addCommand(command);
		verifyZeroInteractions(layerMenuViewHolder, layerAdapter, listItemDragHandler);
	}

	@Test
	public void testAddLayerWhenTooManyLayers() {
		for (int i = 0; i < 100; i++) {
			layerModel.addLayerAt(i, mock(Layer.class));
		}

		createPresenter();
		layerPresenter.addLayer();

		verify(navigator).showToast(R.string.layer_too_many_layers, Toast.LENGTH_SHORT);
		verifyZeroInteractions(commandManager, layerMenuViewHolder, layerAdapter, listItemDragHandler);
	}

	@Test
	public void testRemoveLayer() {
		Layer selectedLayer = mock(Layer.class);
		layerModel.addLayerAt(0, mock(Layer.class));
		layerModel.addLayerAt(1, mock(Layer.class));
		layerModel.setCurrentLayer(layerModel.getLayerAt(1));
		Command command = mock(Command.class);
		when(commandFactory.createRemoveLayerCommand(1)).thenReturn(command);

		createPresenter();
		layerPresenter.removeLayer();

		verify(commandManager).addCommand(command);
		verifyZeroInteractions(selectedLayer, layerMenuViewHolder, layerAdapter, listItemDragHandler);
	}

	@Test
	public void testRemoveLayerWhenOnlyOneLayer() {
		layerModel.addLayerAt(0, mock(Layer.class));

		createPresenter();
		layerPresenter.removeLayer();

		verifyZeroInteractions(commandManager, commandFactory, layerMenuViewHolder, layerAdapter, listItemDragHandler);
	}

	@Test
	public void testOnDragLayerAtPosition() {
		View view = mock(View.class);
		Layer firstLayer = new Layer(mock(Bitmap.class));
		Layer secondLayer = new Layer(mock(Bitmap.class));
		assertTrue(firstLayer.isVisible());
		assertTrue(secondLayer.isVisible());
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
		createPresenter();
		layerPresenter.onStartDragging(0, view);

		verify(listItemDragHandler).startDragging(0, view);
		verifyZeroInteractions(commandManager, commandFactory, layerMenuViewHolder, layerAdapter);
	}

	@Test
	public void testOnDragLayerAtPositionWhenOnlyOneLayer() {
		View view = mock(View.class);
		layerModel.addLayerAt(0, mock(Layer.class));

		createPresenter();
		layerPresenter.onStartDragging(0, view);

		verifyZeroInteractions(listItemDragHandler, commandManager, commandFactory, layerMenuViewHolder, layerAdapter);
	}

	@Test
	public void testOnDragLayerAtPositionWhenPositionOutOfBounds() {
		View view = mock(View.class);
		layerModel.addLayerAt(0, mock(Layer.class));
		layerModel.addLayerAt(1, mock(Layer.class));

		createPresenter();
		layerPresenter.onStartDragging(2, view);

		verifyZeroInteractions(listItemDragHandler, commandManager, commandFactory, layerMenuViewHolder, layerAdapter);
	}

	@Test
	public void testOnClickLayerAtPositionWhenDeselected() {
		layerModel.addLayerAt(0, mock(Layer.class));
		layerModel.addLayerAt(1, mock(Layer.class));
		layerModel.setCurrentLayer(layerModel.getLayerAt(1));
		Command command = mock(Command.class);
		when(commandFactory.createSelectLayerCommand(0)).thenReturn(command);

		createPresenter();
		layerPresenter.setLayerSelected(0);

		verify(commandManager).addCommand(command);
	}

	@Test
	public void testOnClickLayerAtPositionWhenAlreadySelected() {
		Layer layer = mock(Layer.class);
		layerModel.addLayerAt(0, layer);
		layerModel.setCurrentLayer(layer);

		createPresenter();
		layerPresenter.setLayerSelected(0);

		verifyZeroInteractions(commandManager, commandFactory, layerMenuViewHolder, layerAdapter);
	}

	@Test
	public void testOnClickLayerAtPositionWhenPositionOutOfBounds() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
		layerModel.setCurrentLayer(firstLayer);

		createPresenter();
		layerPresenter.setLayerSelected(2);

		verifyZeroInteractions(commandManager, commandFactory, layerMenuViewHolder, layerAdapter);
	}

	@Test
	public void testSwapItemsVisuallyDoesNotModifyModel() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);

		createPresenter();
		layerPresenter.swapItemsVisually(0, 1);

		assertEquals(2, layerModel.getLayerCount());
		assertEquals(0, layerModel.getLayerIndexOf(firstLayer));
		assertEquals(1, layerModel.getLayerIndexOf(secondLayer));
		verifyZeroInteractions(commandManager, layerMenuViewHolder, firstLayer, secondLayer);
	}

	@Test
	public void testSwapItemsVisually() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);

		createPresenter();
		layerPresenter.swapItemsVisually(0, 1);

		assertEquals(2, layerPresenter.getLayerCount());
		assertEquals(firstLayer, layerPresenter.getLayerItem(1));
		assertEquals(secondLayer, layerPresenter.getLayerItem(0));

		verifyZeroInteractions(commandManager, layerMenuViewHolder, firstLayer, secondLayer);
	}

	@Test
	public void testMergeItems() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);

		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
		Command command = mock(Command.class);
		when(commandFactory.createMergeLayersCommand(0, 1)).thenReturn(command);

		createPresenter();
		layerPresenter.mergeItems(0, 1);

		verify(commandManager).addCommand(command);
		verify(navigator).showToast(R.string.layer_merged, Toast.LENGTH_SHORT);
		verifyNoMoreInteractions(commandManager);
		verifyZeroInteractions(layerMenuViewHolder, firstLayer, secondLayer, command);
	}

	@Test
	public void testMergeItemsWhenAtSamePosition() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);

		createPresenter();
		layerPresenter.mergeItems(0, 0);

		verifyZeroInteractions(commandManager, layerMenuViewHolder, firstLayer, secondLayer, navigator);
	}

	@Test
	public void testMergeItemsAfterVisualSwap() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		Layer thirdLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
		layerModel.addLayerAt(2, thirdLayer);
		Command command = mock(Command.class);
		when(commandFactory.createMergeLayersCommand(0, 1)).thenReturn(command);

		createPresenter();
		layerPresenter.swapItemsVisually(0, 1);
		layerPresenter.mergeItems(0, 0);
		verify(commandManager).addCommand(command);
		verify(navigator).showToast(R.string.layer_merged, Toast.LENGTH_SHORT);
		verifyNoMoreInteractions(commandManager, firstLayer, secondLayer, thirdLayer);
		verifyZeroInteractions(layerMenuViewHolder, command);
	}

	@Test
	public void testReorderItems() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
		Command command = mock(Command.class);
		when(commandFactory.createReorderLayersCommand(1, 0)).thenReturn(command);

		createPresenter();
		layerPresenter.reorderItems(1, 0);

		verify(commandManager).addCommand(command);
		verifyNoMoreInteractions(commandManager);
		verifyZeroInteractions(layerMenuViewHolder, firstLayer, secondLayer, command);
	}

	@Test
	public void testReorderItemsAfterVisualSwap() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		Layer thirdLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
		layerModel.addLayerAt(2, thirdLayer);
		Command command = mock(Command.class);
		when(commandFactory.createReorderLayersCommand(2, 0)).thenReturn(command);

		createPresenter();
		layerPresenter.swapItemsVisually(2, 1);
		layerPresenter.swapItemsVisually(1, 0);
		layerPresenter.reorderItems(2, 0);

		verify(commandManager).addCommand(command);
		verifyNoMoreInteractions(commandManager);
		verifyZeroInteractions(layerMenuViewHolder, firstLayer, secondLayer, command);
	}

	@Test
	public void testReorderItemsWhenAlreadyAtThisPosition() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);

		createPresenter();
		layerPresenter.reorderItems(1, 1);

		verifyZeroInteractions(commandManager, layerMenuViewHolder, firstLayer, secondLayer);
	}

	@Test
	public void testMarkMergeable() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);
		LayerViewHolder layerViewHolder = mock(LayerViewHolder.class);
		when(layerAdapter.getViewHolderAt(1)).thenReturn(layerViewHolder);

		createPresenter();
		layerPresenter.markMergeable(0, 1);

		verify(layerViewHolder).setMergable();
		verifyZeroInteractions(commandManager, layerMenuViewHolder, listItemDragHandler);
	}

	@Test
	public void testMarkMergeableWhenPositionInvalidDoesNothing() {
		createPresenter();
		layerPresenter.markMergeable(0, 1);

		verifyZeroInteractions(commandManager, layerMenuViewHolder, listItemDragHandler);
	}

	@Test
	public void testCommandPostExecuteResetsInternalModel() {
		Layer firstLayer = mock(Layer.class);
		Layer secondLayer = mock(Layer.class);
		layerModel.addLayerAt(0, firstLayer);
		layerModel.addLayerAt(1, secondLayer);

		createPresenter();
		layerPresenter.swapItemsVisually(0, 1);

		assertEquals(secondLayer, layerPresenter.getLayerItem(0));
		assertEquals(firstLayer, layerPresenter.getLayerItem(1));

		layerPresenter.invalidate();

		assertEquals(firstLayer, layerPresenter.getLayerItem(0));
		assertEquals(secondLayer, layerPresenter.getLayerItem(1));
	}

	@Test
	public void testCommandPostExecute() {
		layerModel.addLayerAt(0, mock(Layer.class));
		layerModel.addLayerAt(1, mock(Layer.class));

		createPresenter();
		layerPresenter.invalidate();

		verify(layerAdapter).notifyDataSetChanged();
		verify(layerMenuViewHolder).enableAddLayerButton();
		verify(layerMenuViewHolder).enableRemoveLayerButton();
	}
}
