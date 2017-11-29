/**
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

package org.catrobat.paintroid.intro.listener;

import android.view.View;

import com.getkeepsafe.taptargetview.TapTargetView;

import static org.catrobat.paintroid.intro.helper.IntroAnimationHelper.fadeIn;

public class TapTargetListener extends TapTargetView.Listener {

	private View fadeView;

	public TapTargetListener(View fadeView) {
		this.fadeView = fadeView;
	}

	@Override
	public void onTargetClick(TapTargetView view) {
		super.onTargetClick(view);      // This call is optional
		fadeIn(fadeView);
	}

	@Override
	public void onTargetCancel(TapTargetView view) {
		super.onTargetCancel(view);
		fadeIn(fadeView);
	}

	@Override
	public void onOuterCircleClick(TapTargetView view) {
		onTargetClick(view);
	}
}
