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

package org.catrobat.paintroid.intro;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.intro.listener.IntroTargetSequence;

import static org.catrobat.paintroid.intro.helper.IntroAnimationHelper.fadeOut;

public class TapTargetTopBar extends TapTargetBase {
	@VisibleForTesting
	public static boolean firsTimeSequence = true;
	private TapTargetSequence sequence;

	public TapTargetTopBar(LinearLayout tapTargetView, View fadeView, WelcomeActivity activity,
			int bottomBarResourceId) {
		super(tapTargetView, fadeView, activity, bottomBarResourceId);
		sequence = new TapTargetSequence(activity);
		sequence.continueOnCancel(true);
		sequence.considerOuterCircleCanceled(true);
		Log.d(TAG, "Create TapTargetTopBar");
	}

	public static void resetSequenceState() {
		firsTimeSequence = true;
	}

	@Override
	public void initTargetView() {
		super.initTargetView();

		initSequence();
		Log.d("Seq", String.valueOf(firsTimeSequence));
		if (firsTimeSequence) {
			firsTimeSequence = false;
			fadeOut(fadeView);
			sequence.start();
		}
	}

	private void initSequence() {
		for (TapTarget target : tapTargetMap.values()) {
			sequence.target(target);
		}

		sequence.listener(new IntroTargetSequence(fadeView));
	}
}
