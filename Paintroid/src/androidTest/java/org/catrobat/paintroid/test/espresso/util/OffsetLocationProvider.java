/*
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

package org.catrobat.paintroid.test.espresso.util;

import android.view.View;

import androidx.test.espresso.action.CoordinatesProvider;

public class OffsetLocationProvider implements CoordinatesProvider {
	private final CoordinatesProvider locationProvider;
	private final int xOffset;
	private final int yOffset;

	public OffsetLocationProvider(CoordinatesProvider locationProvider, int xOffset, int yOffset) {

		this.locationProvider = locationProvider;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public static CoordinatesProvider withOffset(CoordinatesProvider locationProvider, int xOffset, int yOffset) {
		return new OffsetLocationProvider(locationProvider, xOffset, yOffset);
	}

	@Override
	public float[] calculateCoordinates(View view) {
		float[] coordinates = locationProvider.calculateCoordinates(view);
		coordinates[0] += xOffset;
		coordinates[1] += yOffset;
		return coordinates;
	}
}
