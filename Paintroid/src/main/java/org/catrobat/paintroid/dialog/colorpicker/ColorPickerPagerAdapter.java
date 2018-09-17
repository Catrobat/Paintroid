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

package org.catrobat.paintroid.dialog.colorpicker;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter,
		ColorPickerViewPager.ColorPickerPagerAdapter {

	private List<FragmentBuilder> fragmentBuilders = new ArrayList<>();
	private List<Integer> iconResources = new ArrayList<>();

	private SparseArray<Fragment> fragments = new SparseArray<>();

	public ColorPickerPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@DrawableRes
	@Override
	public int getPageIcon(int position) {
		return iconResources.get(position);
	}

	public void addItem(FragmentBuilder fragmentBuilder, @DrawableRes int iconResource) {
		fragmentBuilders.add(fragmentBuilder);
		iconResources.add(iconResource);
	}

	@NonNull
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = (Fragment) super.instantiateItem(container, position);
		fragments.put(position, fragment);
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		fragments.delete(position);
	}

	@Override
	public View getPageView(int position) {
		Fragment fragment = fragments.get(position);
		return fragment != null ? fragment.getView() : null;
	}

	@Override
	public Fragment getItem(int position) {
		FragmentBuilder fragmentBuilder = fragmentBuilders.get(position);
		return fragmentBuilder.create();
	}

	@Override
	public int getCount() {
		return fragmentBuilders.size();
	}

	public interface FragmentBuilder {
		Fragment create();
	}
}
