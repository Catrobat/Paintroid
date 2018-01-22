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

package org.catrobat.paintroid.test.espresso.util;

import android.os.SystemClock;
import android.support.test.espresso.UiController;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.espresso.action.Swiper;
import android.view.MotionEvent;

public enum CustomSwiper implements Swiper {
	ACCURATE {
		@Override
		public Status sendSwipe(UiController uiController, float[] startCoordinates, float[] endCoordinates, float[] precision) {
			final float[][] steps = interpolate(startCoordinates, endCoordinates, STEPS);
			final int delayBetweenMovements = DURATION / steps.length;

			MotionEvent downEvent = MotionEvents.sendDown(uiController, startCoordinates, precision).down;
			try {
				for (int i = 0; i < steps.length; i++) {
					MotionEvents.sendMovement(uiController, downEvent, steps[i]);
					long desiredTime = downEvent.getDownTime() + delayBetweenMovements * i;
					long timeUntilDesired = desiredTime - SystemClock.uptimeMillis();
					if (timeUntilDesired > 10) {
						uiController.loopMainThreadForAtLeast(timeUntilDesired);
					}
				}
				MotionEvents.sendUp(uiController, downEvent, endCoordinates);
			} finally {
				downEvent.recycle();
			}
			return Status.SUCCESS;
		}
	};

	private static final int STEPS = 10;
	private static final int DURATION = 500;

	private static float[][] interpolate(float[] start, float[] end, int steps) {
		float[][] res = new float[steps][2];

		res[0][0] = start[0];
		res[0][1] = start[1];

		for (int i = 2; i < steps; i++) {
			res[i - 1][0] = start[0] + (end[0] - start[0]) * i / (steps + 2f);
			res[i - 1][1] = start[1] + (end[1] - start[1]) * i / (steps + 2f);
		}

		res[steps - 1][0] = end[0];
		res[steps - 1][1] = end[1];

		return res;
	}
}
