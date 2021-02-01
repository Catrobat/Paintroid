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
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.LayerContracts;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.paintroid.test.espresso.util.MainActivityHelper.getMainActivityFromView;
import static org.hamcrest.Matchers.is;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public final class DrawingSurfaceInteraction extends CustomViewInteraction {

	private DrawingSurfaceInteraction() {
		super(onView(withId(R.id.pocketpaint_drawing_surface_view)));
	}

	public static DrawingSurfaceInteraction onDrawingSurfaceView() {
		return new DrawingSurfaceInteraction();
	}

	public DrawingSurfaceInteraction checkPixelColor(@ColorInt final int expectedColor, final CoordinatesProvider coordinateProvider) {
		check(matches(new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Color at coordinates is " + Integer.toHexString(expectedColor));
			}

			@Override
			protected boolean matchesSafely(View view) {
				MainActivity activity = getMainActivityFromView(view);
				LayerContracts.Layer currentLayer = activity.layerModel.getCurrentLayer();
				float[] coordinates = coordinateProvider.calculateCoordinates(view);
				int actualColor = currentLayer.getBitmap().getPixel((int) coordinates[0], (int) coordinates[1]);
				return expectedColor == actualColor;
			}
		}));
		return this;
	}

	public DrawingSurfaceInteraction checkPixelColor(@ColorInt final int expectedColor, final float x, final float y) {
		check(matches(new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Color at coordinates is " + Integer.toHexString(expectedColor));
			}

			@Override
			protected boolean matchesSafely(View view) {
				MainActivity activity = getMainActivityFromView(view);
				LayerContracts.Layer currentLayer = activity.layerModel.getCurrentLayer();
				int actualColor = currentLayer.getBitmap().getPixel((int) x, (int) y);
				return expectedColor == actualColor;
			}
		}));
		return this;
	}

	public DrawingSurfaceInteraction checkPixelColorResource(@ColorRes int expectedColorRes, CoordinatesProvider coordinateProvider) {
		int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().getTargetContext(), expectedColorRes);
		return checkPixelColor(expectedColor, coordinateProvider);
	}

	public DrawingSurfaceInteraction checkBitmapDimension(final int expectedWidth, final int expectedHeight) {
		check(matches(new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Bitmap has is size "
						+ expectedWidth + "x and "
						+ expectedHeight + "y");
			}

			@Override
			protected boolean matchesSafely(View view) {
				MainActivity activity = getMainActivityFromView(view);
				LayerContracts.Model layerModel = activity.layerModel;
				Bitmap bitmap = layerModel.getCurrentLayer().getBitmap();
				return expectedWidth == bitmap.getWidth() && expectedHeight == bitmap.getHeight();
			}
		}));
		return this;
	}

	public DrawingSurfaceInteraction checkLayerDimensions(int expectedWidth, int expectedHeight) {
		checkThatLayerDimensions(is(expectedWidth), is(expectedHeight));
		return this;
	}

	public DrawingSurfaceInteraction checkThatLayerDimensions(final Matcher<Integer> matchesWidth, final Matcher<Integer> matchesHeight) {
		check(matches(new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("All layers have expected size");
			}

			@Override
			protected boolean matchesSafely(View view) {
				MainActivity activity = getMainActivityFromView(view);
				LayerContracts.Model layerModel = activity.layerModel;
				for (LayerContracts.Layer layer : layerModel.getLayers()) {
					Bitmap bitmap = layer.getBitmap();
					if (!matchesWidth.matches(bitmap.getWidth()) || !matchesHeight.matches(bitmap.getHeight())) {
						return false;
					}
				}
				return true;
			}
		}));

		return this;
	}
}
