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

package org.catrobat.paintroid.test.junit.model;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.CommandManager.CommandListener;
import org.catrobat.paintroid.command.implementation.AsyncCommandManager;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.command.implementation.DefaultCommandManager;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LayerTest {
	private CommandFactory commandFactory;
	private CommandManager commandManager;
	private LayerContracts.Model layerModel;

	@Before
	public void setUp() {
		commandFactory = new DefaultCommandFactory();
		layerModel = new LayerModel();
		layerModel.setWidth(200);
		layerModel.setHeight(200);
		Layer layer = new Layer(Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888));
		layerModel.addLayerAt(0, layer);
		layerModel.setCurrentLayer(layer);

		commandManager = new AsyncCommandManager(new DefaultCommandManager(new CommonFactory(), layerModel), layerModel);
	}

	@Test
	public void testCreateManyLayers() {
		for (int i = 0; i < 100; i++) {
			commandManager.addCommand(commandFactory.createAddLayerCommand());
			commandManager.addCommand(commandFactory.createRemoveLayerCommand(1));
		}
	}

	@Test
	public void testMoveLayer() {
		final CommandListener listener = mock(CommandListener.class);

		commandManager.addCommandListener(listener);
		commandManager.addCommand(commandFactory.createAddLayerCommand());

		verify(listener, timeout(1000)).commandPostExecute();
		assertThat(layerModel.getLayerCount(), is(2));

		LayerContracts.Layer firstLayer = layerModel.getLayerAt(0);
		LayerContracts.Layer secondLayer = layerModel.getLayerAt(1);

		reset(listener);

		commandManager.addCommand(commandFactory.createReorderLayersCommand(0, 1));

		verify(listener, timeout(1000)).commandPostExecute();
		assertThat(layerModel.getLayerCount(), is(2));
		assertThat(layerModel.getLayerAt(0), is(secondLayer));
		assertThat(layerModel.getLayerAt(1), is(firstLayer));
	}

	@Test
	public void testMergeLayers() {
		final CommandListener listener = mock(CommandListener.class);

		LayerContracts.Layer firstLayer = layerModel.getLayerAt(0);
		firstLayer.getBitmap().setPixel(1, 1, Color.BLACK);
		firstLayer.getBitmap().setPixel(1, 2, Color.BLACK);

		commandManager.addCommandListener(listener);
		commandManager.addCommand(commandFactory.createAddLayerCommand());

		verify(listener, timeout(1000)).commandPostExecute();

		LayerContracts.Layer secondLayer = layerModel.getLayerAt(0);
		assertThat(layerModel.getCurrentLayer(), is(secondLayer));

		secondLayer.getBitmap().setPixel(1, 1, Color.BLUE);
		secondLayer.getBitmap().setPixel(2, 1, Color.BLUE);

		reset(listener);

		commandManager.addCommand(commandFactory.createMergeLayersCommand(0, 1));

		verify(listener, timeout(1000)).commandPostExecute();

		assertThat(layerModel.getLayerCount(), is(1));
		assertThat(layerModel.getCurrentLayer(), is(firstLayer));
		assertThat(layerModel.getLayerAt(0), is(firstLayer));

		assertThat(firstLayer.getBitmap().getPixel(1, 2), is(Color.BLACK));
		assertThat(firstLayer.getBitmap().getPixel(2, 1), is(Color.BLUE));
		assertThat(firstLayer.getBitmap().getPixel(1, 1), is(Color.BLUE));
	}

	@Test
	public void testHideThenUnhideLayer() {
		LayerContracts.Layer layerToHide = layerModel.getLayerAt(0);

		layerToHide.getBitmap().setPixel(1, 1, Color.BLACK);
		layerToHide.getBitmap().setPixel(2, 1, Color.BLACK);
		layerToHide.getBitmap().setPixel(3, 1, Color.BLACK);
		layerToHide.getBitmap().setPixel(4, 1, Color.BLACK);

		Bitmap bitmapCopy = layerToHide.getTransparentBitmap();

		layerToHide.switchBitmaps(false);
		layerToHide.setBitmap(bitmapCopy);

		assertThat(layerToHide.getBitmap().getPixel(1, 1), is(Color.TRANSPARENT));
		assertThat(layerToHide.getBitmap().getPixel(2, 1), is(Color.TRANSPARENT));
		assertThat(layerToHide.getBitmap().getPixel(3, 1), is(Color.TRANSPARENT));
		assertThat(layerToHide.getBitmap().getPixel(4, 1), is(Color.TRANSPARENT));

		layerToHide.switchBitmaps(true);

		assertThat(layerToHide.getBitmap().getPixel(1, 1), is(Color.BLACK));
		assertThat(layerToHide.getBitmap().getPixel(2, 1), is(Color.BLACK));
		assertThat(layerToHide.getBitmap().getPixel(3, 1), is(Color.BLACK));
		assertThat(layerToHide.getBitmap().getPixel(4, 1), is(Color.BLACK));
	}
}
