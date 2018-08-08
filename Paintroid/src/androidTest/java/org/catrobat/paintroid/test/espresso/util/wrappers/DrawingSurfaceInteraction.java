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

package org.catrobat.paintroid.test.espresso.util.wrappers;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.test.InstrumentationRegistry;
import android.support.v4.content.ContextCompat;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.hamcrest.Matcher;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public final class DrawingSurfaceInteraction extends CustomViewInteraction {
	private DrawingSurfaceInteraction() {
		super(onView(withId(R.id.pocketpaint_drawing_surface_view)));
	}

	public static DrawingSurfaceInteraction onDrawingSurfaceView() {
		return new DrawingSurfaceInteraction();
	}

	private static void assertColorEquals(@ColorInt int expected, @ColorInt int actual) {
		String message = Integer.toHexString(expected) + " != " + Integer.toHexString(actual);
		assertEquals(message, expected, actual);
	}

	public DrawingSurfaceInteraction checkPixelColor(@ColorInt int expectedColor, BitmapLocationProvider coordinateProvider) {
		DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
		float[] coordinates = coordinateProvider.calculateCoordinates(drawingSurface);
		int actualColor = drawingSurface.getPixel(new PointF(coordinates[0], coordinates[1]));
		assertColorEquals(expectedColor, actualColor);
		return this;
	}

	public DrawingSurfaceInteraction checkPixelColorResource(@ColorRes int expectedColorRes, BitmapLocationProvider coordinateProvider) {
		int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getTargetContext(), expectedColorRes);
		return checkPixelColor(expectedColor, coordinateProvider);
	}

	public DrawingSurfaceInteraction checkBitmapDimension(int expectedWidth, int expectedHeight) {
		assertBitmapDimensions(getWorkingBitmap(), expectedWidth, expectedHeight);
		return this;
	}

	public DrawingSurfaceInteraction checkLayerDimensions(int expectedWidth, int expectedHeight) {
		assertLayerDimensions(expectedWidth, expectedHeight);
		return this;
	}

	public DrawingSurfaceInteraction checkThatLayerDimensions(Matcher<Integer> matchesWidth, Matcher<Integer> matchesHeight) {
		List<LayerContracts.Layer> layers = PaintroidApplication.layerModel.getLayers();
		for (LayerContracts.Layer layer : layers) {
			Bitmap bitmap = layer.getBitmap();
			assertThat(bitmap.getWidth(), matchesWidth);
			assertThat(bitmap.getHeight(), matchesHeight);
		}
		return this;
	}

	private void assertBitmapDimensions(Bitmap bitmap, int expectedWidth, int expectedHeight) {
		assertEquals(expectedWidth, bitmap.getWidth());
		assertEquals(expectedHeight, bitmap.getHeight());
	}

	private void assertLayerDimensions(int expectedWidth, int expectedHeight) {
		List<LayerContracts.Layer> layers = PaintroidApplication.layerModel.getLayers();
		for (LayerContracts.Layer layer : layers) {
			assertBitmapDimensions(layer.getBitmap(), expectedWidth, expectedHeight);
		}
	}
}
