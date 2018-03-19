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

package org.catrobat.catroid.paintroid.test.espresso;

import android.graphics.Point;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Display;

import org.catrobat.catroid.paintroid.MainActivity;
import org.catrobat.catroid.paintroid.PaintroidApplication;
import org.catrobat.catroid.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.catroid.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BitmapIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() {
		PaintroidApplication.drawingSurface.destroyDrawingCache();

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@Test
	public void drawingSurfaceBitmapIsDisplaySize() {
		final int bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		final int bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();

		Display display = launchActivityRule.getActivity().getWindowManager().getDefaultDisplay();

		Point displaySize = new Point();
		display.getSize(displaySize);

		final int displayWidth = displaySize.x;
		final int displayHeight = displaySize.y;

		assertEquals("bitmap width should be display width", bitmapWidth, displayWidth);
		assertEquals("bitmap height should be display height", bitmapHeight, displayHeight);
	}
}
