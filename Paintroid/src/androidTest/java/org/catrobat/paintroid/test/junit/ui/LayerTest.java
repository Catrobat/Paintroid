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

package org.catrobat.paintroid.test.junit.ui;

import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.listener.LayerListener;
import org.junit.Rule;
import org.junit.Test;

public class LayerTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@UiThreadTest
	@Test
	public void testCreateManyLayers() {
		for (int i = 0; i < 100; i++) {
			LayerListener.getInstance().createLayer();
			LayerListener.getInstance().deleteLayer();
		}
	}
}
