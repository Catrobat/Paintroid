/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.ui;

import android.graphics.Color;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.listener.LayerListener;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class LayerTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@UiThreadTest
	@Test
	public void testCreateManyLayers() {
		for (int i = 0; i < 100; i++) {
			LayerListener.getInstance().createLayer();
			LayerListener.getInstance().deleteLayer();
		}
	}

	@UiThreadTest
	@Test
	public void testMoveLayer() {
		LayerListener.getInstance().createLayer();
		int idLayerOne = LayerListener.getInstance().getAdapter().getLayer(0).getLayerID();
		int idLayerTwo = LayerListener.getInstance().getAdapter().getLayer(1).getLayerID();
		int posLayerOne = LayerListener.getInstance().getAdapter().getPosition(idLayerOne);
		int posLayerTwo = LayerListener.getInstance().getAdapter().getPosition(idLayerTwo);

		assertTrue("Layer One should be at an higher position", posLayerOne < posLayerTwo);
		LayerListener.getInstance().moveLayer(0, 1);

		posLayerOne = LayerListener.getInstance().getAdapter().getPosition(idLayerOne);
		posLayerTwo = LayerListener.getInstance().getAdapter().getPosition(idLayerTwo);
		assertTrue("Positions should have been switched", posLayerOne > posLayerTwo);
	}

	@UiThreadTest
	@Test
	public void testMergeLayers() {
		int idFirstLayer = LayerListener.getInstance().getCurrentLayer().getLayerID();
		LayerListener.getInstance().getCurrentLayer().getImage().setPixel(1, 1, Color.BLACK);
		LayerListener.getInstance().getCurrentLayer().getImage().setPixel(1, 2, Color.BLACK);
		LayerListener.getInstance().createLayer();
		int idSecondLayer = LayerListener.getInstance().getCurrentLayer().getLayerID();
		assertNotEquals("New Layer should be selected", idFirstLayer, idSecondLayer);

		LayerListener.getInstance().getCurrentLayer().getImage().setPixel(1, 1, Color.BLUE);
		LayerListener.getInstance().getCurrentLayer().getImage().setPixel(2, 1, Color.BLUE);
		LayerListener.getInstance().mergeLayer(0, 1);
		int numLayersAfterMerge = LayerListener.getInstance().getAdapter().getCount();

		assertEquals("Only one Layer should exist", 1, numLayersAfterMerge);
		assertEquals("Color should be black", Color.BLACK, LayerListener.getInstance().getCurrentLayer().getImage().getPixel(1, 2));
		assertEquals("Color should be blue", Color.BLUE, LayerListener.getInstance().getCurrentLayer().getImage().getPixel(2, 1));
		assertEquals("Color should be blue", Color.BLUE, LayerListener.getInstance().getCurrentLayer().getImage().getPixel(1, 1));

		UndoRedoManager.getInstance().performUndo();
		int numLayersAfterUndo = LayerListener.getInstance().getAdapter().getCount();

		assertEquals("Two Layers should exist", 2, numLayersAfterUndo);
		assertEquals("Color should be blue", Color.BLUE, LayerListener.getInstance().getAdapter().getLayer(0).getImage().getPixel(2, 1));
		assertEquals("Color should be blue", Color.BLUE, LayerListener.getInstance().getAdapter().getLayer(0).getImage().getPixel(1, 1));
		assertEquals("Color should be black", Color.BLACK, LayerListener.getInstance().getAdapter().getLayer(1).getImage().getPixel(1, 2));
		assertEquals("Color should be blue", Color.BLACK, LayerListener.getInstance().getAdapter().getLayer(1).getImage().getPixel(1, 1));

		UndoRedoManager.getInstance().performRedo();
		int numLayersAfterRedo = LayerListener.getInstance().getAdapter().getCount();

		assertEquals("Only one Layer should exist", 1, numLayersAfterRedo);
		assertEquals("Color should be black", Color.BLACK, LayerListener.getInstance().getCurrentLayer().getImage().getPixel(1, 2));
		assertEquals("Color should be blue", Color.BLUE, LayerListener.getInstance().getCurrentLayer().getImage().getPixel(2, 1));
		assertEquals("Color should be blue", Color.BLUE, LayerListener.getInstance().getCurrentLayer().getImage().getPixel(1, 1));
	}
}
