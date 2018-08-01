/**
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

import android.graphics.Color;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager.CommandListener;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.contract.LayerContracts;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.catrobat.paintroid.PaintroidApplication.commandManager;
import static org.catrobat.paintroid.PaintroidApplication.layerModel;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class LayerTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
	private CommandFactory commandFactory;

	@Before
	public void setUp() {
		commandFactory = new DefaultCommandFactory();
	}

	@UiThreadTest
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

		activityTestRule.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				commandManager.addCommandListener(listener);
				commandManager.addCommand(commandFactory.createAddLayerCommand());
			}
		});

		verify(listener, timeout(1000)).commandPostExecute();
		assertThat(layerModel.getLayerCount(), is(2));

		LayerContracts.Layer firstLayer = layerModel.getLayerAt(0);
		LayerContracts.Layer secondLayer = layerModel.getLayerAt(1);

		reset(listener);

		activityTestRule.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				commandManager.addCommand(commandFactory.createReorderLayersCommand(0, 1));
			}
		});

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

		activityTestRule.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				commandManager.addCommandListener(listener);
				commandManager.addCommand(commandFactory.createAddLayerCommand());
			}
		});

		verify(listener, timeout(1000)).commandPostExecute();

		LayerContracts.Layer secondLayer = layerModel.getLayerAt(0);
		assertThat(layerModel.getCurrentLayer(), is(secondLayer));

		secondLayer.getBitmap().setPixel(1, 1, Color.BLUE);
		secondLayer.getBitmap().setPixel(2, 1, Color.BLUE);

		reset(listener);

		activityTestRule.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				commandManager.addCommand(commandFactory.createMergeLayersCommand(0, 1));
			}
		});

		verify(listener, timeout(1000)).commandPostExecute();

		assertThat(layerModel.getLayerCount(), is(1));
		assertThat(layerModel.getCurrentLayer(), is(firstLayer));
		assertThat(layerModel.getLayerAt(0), is(firstLayer));

		assertThat(firstLayer.getBitmap().getPixel(1, 2), is(Color.BLACK));
		assertThat(firstLayer.getBitmap().getPixel(2, 1), is(Color.BLUE));
		assertThat(firstLayer.getBitmap().getPixel(1, 1), is(Color.BLUE));
	}
}
