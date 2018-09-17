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

package org.catrobat.paintroid.test.junit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.catrobat.paintroid.dialog.colorpicker.ColorPickerPagerAdapter;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerPagerAdapter.FragmentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ColorPickerPagerAdapterTest {
	private ColorPickerPagerAdapter adapter;

	@Before
	public void setUp() {
		adapter = new ColorPickerPagerAdapter(mock(FragmentManager.class));
	}

	@Test
	public void testSetUp() {
		assertThat(adapter.getCount(), is(0));
	}

	@Test
	public void testAddOneItem() {
		FragmentBuilder builder = mock(FragmentBuilder.class);
		Fragment fragment = mock(Fragment.class);
		when(builder.create()).thenReturn(fragment);

		adapter.addItem(builder, 7);

		assertThat(adapter.getCount(), is(1));
		assertThat(adapter.getPageIcon(0), is(7));
		assertThat(adapter.getItem(0), is(fragment));
	}

	@Test
	public void testAddMultipleItems() {
		FragmentBuilder firstBuilder = mock(FragmentBuilder.class);
		FragmentBuilder secondBuilder = mock(FragmentBuilder.class);
		Fragment firstFragment = mock(Fragment.class);
		Fragment secondFragment = mock(Fragment.class);
		when(firstBuilder.create()).thenReturn(firstFragment);
		when(secondBuilder.create()).thenReturn(secondFragment);

		adapter.addItem(firstBuilder, 3);
		adapter.addItem(secondBuilder, 5);

		assertThat(adapter.getCount(), is(2));
		assertThat(adapter.getPageIcon(0), is(3));
		assertThat(adapter.getPageIcon(1), is(5));
		assertThat(adapter.getItem(0), is(firstFragment));
		assertThat(adapter.getItem(1), is(secondFragment));
	}
}
