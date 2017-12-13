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

package org.catrobat.paintroid.test.espresso;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCanvasPointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UndoRedoIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;

	private int displayWidth;
	private int displayHeight;
	private PointF pointOnScreenLeft;
	private PointF pointOnScreenMiddle;
	private PointF pointOnScreenRight;

	private PointF pointOnCanvasLeft;
	private PointF pointOnCanvasMiddle;
	private PointF pointOnCanvasRight;

	@Before
	public void setUp() {
		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		activityHelper.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		displayWidth = activityHelper.getDisplayWidth();
		displayHeight = activityHelper.getDisplayHeight();

		pointOnScreenLeft = new PointF(displayWidth / 4f, displayHeight / 4f);
		pointOnScreenMiddle = new PointF(displayWidth / 2f, displayHeight / 2f);
		pointOnScreenRight = new PointF(displayWidth * 0.75f, displayHeight * 0.75f);

		pointOnCanvasLeft = getCanvasPointFromScreenPoint(pointOnScreenLeft);
		pointOnCanvasMiddle = getCanvasPointFromScreenPoint(pointOnScreenMiddle);
		pointOnCanvasRight = getCanvasPointFromScreenPoint(pointOnScreenRight);
	}

	@Test
	public void testUndoRedoIconsWhenSwitchToLandscapeMode() {
		assertEquals("Wrong screen orientation",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityHelper.getScreenOrientation());

		onView(withId(R.id.btn_top_undo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo_disabled), not(isEnabled()))));
		onView(withId(R.id.btn_top_redo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onView(isRoot())
				.perform(touchAt(pointOnScreenLeft.x, pointOnScreenLeft.y));
		assertPixel(Color.BLACK, pointOnCanvasLeft);

		onView(withId(R.id.btn_top_undo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())));

		onView(isRoot())
				.perform(touchAt(pointOnScreenMiddle.x, pointOnScreenMiddle.y));
		assertPixel(Color.BLACK, pointOnCanvasMiddle);

		onView(isRoot())
				.perform(touchAt(pointOnScreenRight.x, pointOnScreenRight.y));
		assertPixel(Color.BLACK, pointOnCanvasRight);

		onView(withId(R.id.btn_top_redo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onView(withId(R.id.btn_top_undo))
				.perform(click());

		onView(withId(R.id.btn_top_redo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo), isEnabled())));

		activityHelper.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		assertEquals("Wrong screen orientation",
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, activityHelper.getScreenOrientation());

		onView(withId(R.id.btn_top_undo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())));
		onView(withId(R.id.btn_top_redo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo), isEnabled())))
				.perform(click())
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onView(withId(R.id.btn_top_undo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())))
				.perform(click());

		assertPixel(Color.TRANSPARENT, pointOnCanvasRight);
		onView(withId(R.id.btn_top_undo))
				.perform(click());
		assertPixel(Color.TRANSPARENT, pointOnCanvasMiddle);
		onView(withId(R.id.btn_top_undo))
				.perform(click());
		assertPixel(Color.TRANSPARENT, pointOnCanvasLeft);

		onView(withId(R.id.btn_top_undo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo_disabled), not(isEnabled()))));
	}

	private static void assertPixel(int expectedColor, PointF position) {
		assertEquals("Wrong pixel color", expectedColor,
				PaintroidApplication.drawingSurface.getPixel(position));
	}
}
