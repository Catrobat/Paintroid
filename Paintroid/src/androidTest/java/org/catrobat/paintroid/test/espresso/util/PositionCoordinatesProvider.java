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

/**
 *
 */
public class PositionCoordinatesProvider implements CoordinatesProvider {
    private final int x;
    private final int y;

    public PositionCoordinatesProvider(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float[] calculateCoordinates(View view) {
        return new float[]{x, y};
    }

    public static CoordinatesProvider at(int x, int y) {
        return new PositionCoordinatesProvider(x, y);
    }
}
