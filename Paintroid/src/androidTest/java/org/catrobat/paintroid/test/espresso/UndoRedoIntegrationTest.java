/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper;
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipeAccurate;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.catrobat.paintroid.test.utils.TestUtils.selectColorInDialog;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UndoRedoIntegrationTest {
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	private MainActivityHelper activityHelper;

	private Perspective perspective;

	@Before
	public void setUp() {
		activityHelper = new MainActivityHelper(launchActivityRule.getActivity());

		activityHelper.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		perspective = launchActivityRule.getActivity().perspective;

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@Test
	public void testUndoRedoIconsWhenSwitchToLandscapeMode() {
		assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityHelper.getScreenOrientation());

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_undo_disabled), not(isEnabled()))));
		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_redo_disabled), not(isEnabled()))));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT));
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_undo), isEnabled())));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT));
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT);

		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_redo_disabled), not(isEnabled()))));

		onTopBarView()
				.performUndo();

		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_redo), isEnabled())));

		activityHelper.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		assertEquals(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, activityHelper.getScreenOrientation());

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_undo), isEnabled())));
		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_redo), isEnabled())))
				.perform(click())
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_redo_disabled), not(isEnabled()))));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT);

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_undo), isEnabled())))
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

		onView(withId(R.id.pocketpaint_btn_top_undo))
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_undo_disabled), not(isEnabled()))));
	}

	@Test
	public void testDisableEnableUndo() {
		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_undo_disabled), not(isEnabled()))));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_undo), isEnabled())));

		onTopBarView()
				.performUndo();

		onTopBarView().onUndoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_undo_disabled), not(isEnabled()))));
	}

	@Test
	public void testDisableEnableRedo() {
		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_redo_disabled), not(isEnabled()))));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_redo_disabled), not(isEnabled()))));

		onTopBarView()
				.performUndo();

		onTopBarView().onRedoButton()
				.check(matches(allOf(withDrawable(R.drawable.ic_pocketpaint_redo), isEnabled())));
	}

	@Test
	public void testPreserveZoomAndMoveAfterUndo() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		float scale = .5f;
		int translationX = 10;
		int translationY = 15;

		perspective.setScale(scale);
		perspective.surfaceTranslationX = translationX;
		perspective.surfaceTranslationY = translationY;

		onTopBarView()
				.performUndo();

		assertEquals(scale, perspective.getScale(), Float.MIN_VALUE);
		assertEquals(translationX, perspective.surfaceTranslationX, Float.MIN_VALUE);
		assertEquals(translationY, perspective.surfaceTranslationY, Float.MIN_VALUE);
	}

	@Test
	public void testPreserveZoomAndMoveAfterRedo() {

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView()
				.performUndo();

		float scale = .5f;
		int translationX = 10;
		int translationY = 15;

		perspective.setScale(scale);
		perspective.surfaceTranslationX = translationX;
		perspective.surfaceTranslationY = translationY;

		onTopBarView()
				.performRedo();

		assertEquals(scale, perspective.getScale(), Float.MIN_VALUE);
		assertEquals(translationX, perspective.surfaceTranslationX, Float.MIN_VALUE);
		assertEquals(translationY, perspective.surfaceTranslationY, Float.MIN_VALUE);
	}

	@Test
	public void testUndoDoesNotResetLayerVisibility() {
		onDrawingSurfaceView()
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE));

		LayerMenuViewInteraction.onLayerMenuView()
				.performOpen()
				.performToggleLayerVisibility(0)
				.performClose();

		onTopBarView()
				.performUndo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE);

		onTopBarView()
				.performUndo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView()
				.performUndo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onTopBarView()
				.performUndo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);
	}

	@Test
	public void testUndoRedoDoesNotResetLayerVisibility() {
		onDrawingSurfaceView()
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE));

		LayerMenuViewInteraction.onLayerMenuView()
				.performOpen()
				.performToggleLayerVisibility(0)
				.performClose();

		onTopBarView()
				.performUndo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE);

		onTopBarView()
				.performUndo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView()
				.performUndo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onTopBarView()
				.performUndo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		onTopBarView()
				.performRedo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE);

		onTopBarView()
				.performRedo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView()
				.performRedo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onTopBarView()
				.performRedo();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);

		LayerMenuViewInteraction.onLayerMenuView()
				.performOpen()
				.performToggleLayerVisibility(0)
				.performClose();

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE);
	}

	@Test
	public void testChangeColorUndoRedo() {
		selectColorInDialog(0);
		selectColorInDialog(1);
		selectColorInDialog(2);

		onTopBarView().performUndo();
		onTopBarView().performUndo();
		onTopBarView().performUndo();
		onColorPickerView()
				.performOpenColorPicker();
		onColorPickerView().checkCurrentViewColor(Color.BLACK);
		onColorPickerView()
				.onNegativeButton()
				.perform(click());
		onTopBarView().performRedo();
		onTopBarView().performRedo();
		onTopBarView().performRedo();

		onDrawingSurfaceView().perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.parseColor("#FF078707"), BitmapLocationProvider.HALFWAY_TOP_MIDDLE);

		onTopBarView().performUndo();
		onTopBarView().performUndo();
		onTopBarView().performUndo();

		onDrawingSurfaceView().perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.parseColor("#FF0074CD"), BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE);

		onTopBarView().performUndo();
		onTopBarView().performUndo();
		onTopBarView().performRedo();

		onDrawingSurfaceView().perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.parseColor("#FF0074CD"), BitmapLocationProvider.HALFWAY_TOP_MIDDLE);
	}
}
