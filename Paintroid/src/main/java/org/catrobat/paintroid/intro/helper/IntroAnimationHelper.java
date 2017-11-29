/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.intro.helper;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public final class IntroAnimationHelper {
	private IntroAnimationHelper() {
	}

	public static void fadeOut(final View view) {
		fadeAnimation(new AlphaAnimation(1, 0), view, View.INVISIBLE);
	}

	public static void fadeIn(final View view) {
		fadeAnimation(new AlphaAnimation(0, 1), view, View.VISIBLE);
	}

	private static void fadeAnimation(Animation animation, final View view, final int viability) {

		animation.setInterpolator(new AccelerateInterpolator());
		animation.setDuration(500);

		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(viability);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});

		view.startAnimation(animation);
	}
}
