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

package org.catrobat.paintroid.intro.listener;

import android.util.Log;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import static org.catrobat.paintroid.intro.helper.IntroAnimationHelper.fadeIn;

public class IntroTargetSequence implements TapTargetSequence.Listener {

	private static final String TAG = "Sequence Listener";
	private View fadeView;

	public IntroTargetSequence(View fadeView) {
		this.fadeView = fadeView;
	}

	@Override
	public void onSequenceFinish() {
		Log.d(TAG, "Possibilities Sequence Finished");
		fadeIn(fadeView);
	}

	@Override
	public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
	}

	@Override
	public void onSequenceCanceled(TapTarget lastTarget) {
	}
}
