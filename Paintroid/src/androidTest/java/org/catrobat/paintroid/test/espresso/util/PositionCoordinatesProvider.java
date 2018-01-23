/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util;

import android.support.test.espresso.action.CoordinatesProvider;
import android.view.View;

public class PositionCoordinatesProvider implements CoordinatesProvider {
	private final float xCoordinate;
	private final float yCoordinate;

	public PositionCoordinatesProvider(float x, float y) {
		this.xCoordinate = x;
		this.yCoordinate = y;
	}

	public static CoordinatesProvider at(float x, float y) {
		return new PositionCoordinatesProvider(x, y);
	}

	@Override
	public float[] calculateCoordinates(View view) {
		return calculateViewOffset(view, xCoordinate, yCoordinate);
	}

	public static float[] calculateViewOffset(View view, float x, float y) {
		final int[] screenLocation = new int[2];
		view.getLocationOnScreen(screenLocation);

		final float touchX = screenLocation[0] + x;
		final float touchY = screenLocation[1] + y;
		return new float[] {touchX, touchY};
	}
}
