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

package org.catrobat.paintroid.test.espresso;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Display;
import android.view.Gravity;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.implementation.BitmapCommand;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitMillis;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class BitmapIntegrationTest {

	private static final String PRIVATE_ACCESS_WORKING_BITMAP_NAME  = "mWorkingBitmap";

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() {
		PaintroidApplication.drawingSurface.destroyDrawingCache();

		selectTool(ToolType.BRUSH);
	}

	//TODO: Fails now and then, because swipe action is not drawing
	@Test
	public void centerBitmap_SimulateLoad() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		onView(withId(R.id.drawer_layout))
				.check(matches(isClosed(Gravity.LEFT)))
				.perform(open());

		onView(withId(R.id.nav_view))
				.perform(navigateTo(R.id.nav_fullscreen_mode));

		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface, PRIVATE_ACCESS_WORKING_BITMAP_NAME);

		Point bottomrightCanvasPoint = new Point(
			currentDrawingSurfaceBitmap.getWidth() - 1,
			currentDrawingSurfaceBitmap.getHeight() - 1
		);

		int widthOverflow = 250;
		int newBitmapHeight = 30;

		final Bitmap widthOverflowedBitmap = Bitmap.createBitmap(
			bottomrightCanvasPoint.x + widthOverflow,
			newBitmapHeight,
			Bitmap.Config.ALPHA_8
		);

		float surfaceScaleBeforeBitmapCommand = PaintroidApplication.perspective.getScale();

		Layer layer = LayerListener.getInstance().getCurrentLayer();
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), new BitmapCommand(widthOverflowedBitmap, true));

		float surfaceScaleAfterBitmapCommand = PaintroidApplication.perspective.getScale();

		assertThat("Wrong Scale after setting new bitmap", surfaceScaleBeforeBitmapCommand, Matchers.is(Matchers.greaterThan(surfaceScaleAfterBitmapCommand)));

		// FAILS without wait now and then (commit command)
		waitMillis(1000);

		onView(withId(R.id.drawingSurfaceView)).perform(
			actionWithAssertions(
				new GeneralSwipeAction(
					Swipe.FAST,
					GeneralLocation.CENTER,
					GeneralLocation.BOTTOM_CENTER,
					Press.FINGER
				)
			)
		);

		PointF canvasCenter = new PointF((bottomrightCanvasPoint.x + widthOverflow) / 2, newBitmapHeight / 2);

		assertNotEquals("Center not set", Color.TRANSPARENT, PaintroidApplication.drawingSurface.getPixel(canvasCenter));
	}

	@Test
	public void drawingSurface_Bitmap_IsDisplaySize() {
		final int bitmapWidth  = PaintroidApplication.drawingSurface.getBitmapWidth();
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
