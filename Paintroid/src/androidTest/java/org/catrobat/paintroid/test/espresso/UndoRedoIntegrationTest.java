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

package org.catrobat.paintroid.test.espresso;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
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

	@Before
	public void setUp() {
		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		activityHelper.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@Test
	public void testUndoRedoIconsWhenSwitchToLandscapeMode() {
		assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityHelper.getScreenOrientation());

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo_disabled), not(isEnabled()))));
		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onTopBarView()
				.performUndo();

		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo), isEnabled())));

		activityHelper.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		assertEquals(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, activityHelper.getScreenOrientation());

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())));
		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo), isEnabled())))
				.perform(click())
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())))
				.perform(click());

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);
		onTopBarView()
				.performUndo();
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
		onTopBarView()
				.performUndo();
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onView(withId(R.id.btn_top_undo))
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo_disabled), not(isEnabled()))));
	}

	@Test
	public void testDisableEnableUndo() {
		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo_disabled), not(isEnabled()))));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo), isEnabled())));

		onTopBarView()
				.performUndo();

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_undo_disabled), not(isEnabled()))));
	}

	@Test
	public void testDisableEnableRedo() {
		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo_disabled), not(isEnabled()))));

		onTopBarView()
				.performUndo();

		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.icon_menu_redo), isEnabled())));
	}

	@Test
	public void testPreserveZoomAndMoveAfterUndo() {
		Perspective perspective = PaintroidApplication.perspective;

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		float scale = .5f;
		int translationX = 10;
		int translationY = 15;

		perspective.setScale(scale);
		perspective.setSurfaceTranslationX(translationX);
		perspective.setSurfaceTranslationY(translationY);

		onTopBarView()
				.performUndo();

		assertEquals(scale, perspective.getScale(), Float.MIN_VALUE);
		assertEquals(translationX, perspective.getSurfaceTranslationX(), Float.MIN_VALUE);
		assertEquals(translationY, perspective.getSurfaceTranslationY(), Float.MIN_VALUE);
	}

	@Test
	public void testPreserveZoomAndMoveAfterRedo() {
		Perspective perspective = PaintroidApplication.perspective;

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performUndo();

		float scale = .5f;
		int translationX = 10;
		int translationY = 15;

		perspective.setScale(scale);
		perspective.setSurfaceTranslationX(translationX);
		perspective.setSurfaceTranslationY(translationY);

		onTopBarView()
				.performRedo();

		assertEquals(scale, perspective.getScale(), Float.MIN_VALUE);
		assertEquals(translationX, perspective.getSurfaceTranslationX(), Float.MIN_VALUE);
		assertEquals(translationY, perspective.getSurfaceTranslationY(), Float.MIN_VALUE);
	}
}
