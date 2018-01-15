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
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IntroPageViewAdapter extends PagerAdapter {
	@VisibleForTesting
	public int[] layouts;

	public IntroPageViewAdapter(int[] layouts) {
		this.layouts = new int[layouts.length];
		System.arraycopy(layouts, 0, this.layouts, 0, this.layouts.length);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());

		View view = layoutInflater.inflate(layouts[position], container, false);
		container.addView(view);
		return view;
	}

	@Override
	public int getCount() {
		return layouts.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
		container.removeView(view);
	}
}
