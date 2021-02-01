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

package org.catrobat.paintroid.test.utils;

import android.graphics.PointF;

import org.mockito.ArgumentMatcher;

import static org.mockito.ArgumentMatchers.argThat;

public final class PointFMatcher implements ArgumentMatcher<PointF> {
	private final float pointX;
	private final float pointY;

	public PointFMatcher(float x, float y) {
		this.pointX = x;
		this.pointY = y;
	}

	@Override
	public boolean matches(PointF argument) {
		return argument.x == pointX && argument.y == pointY;
	}

	@Override
	public String toString() {
		return "PointF(" + pointX + ", " + pointY + ")";
	}

	public static PointF pointFEquals(float x, float y) {
		return argThat(new PointFMatcher(x, y));
	}
}
