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

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LayerIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Test
	public void testInitialSetup() {
		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_new))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_delete_disabled))));
	}

	@Test
	public void testButtonsAddOneLayer() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer();

		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_new))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_delete))));

		onLayerMenuView()
				.performAddLayer()
				.performAddLayer();

		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_new_disabled))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_delete))));

		onLayerMenuView()
				.performDeleteLayer()
				.performDeleteLayer()
				.performDeleteLayer();

		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_new))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_delete_disabled))));
	}

	@Test
	public void testButtonsAfterNewImage() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_new_disabled))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_delete))));
		onLayerMenuView()
				.checkLayerCount(4);

		onNavigationDrawer()
				.performOpen();
		onView(withText(R.string.menu_new_image))
				.perform(click());
		onView(withText(R.string.discard_button_text))
				.perform(click());
		onView(withText(R.string.menu_new_image_empty_image))
				.perform(click());

		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_new))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_delete_disabled))));
		onLayerMenuView()
				.checkLayerCount(1);
	}

	@Test
	public void testUndoRedoLayerAdd() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer();

		onLayerMenuView()
				.checkLayerCount(2);

		onLayerMenuView()
				.performClose();

		onTopBarView()
				.performUndo();

		onLayerMenuView()
				.performOpen();

		onLayerMenuView()
				.checkLayerCount(1);

		onLayerMenuView()
				.performClose();

		onTopBarView()
				.performRedo();

		onLayerMenuView()
				.performOpen();

		onLayerMenuView()
				.checkLayerCount(2);
	}

	@Test
	public void testUndoRedoLayerDelete() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.checkLayerCount(2)
				.performDeleteLayer()
				.checkLayerCount(1)
				.performClose();

		onTopBarView()
				.performUndo();

		onLayerMenuView()
				.checkLayerCount(2);

		onTopBarView()
				.performRedo();

		onLayerMenuView()
				.checkLayerCount(1);
	}

	@Test
	public void testLayerOrderUndoDelete() {
		onToolBarView()
				.performSelectTool(ToolType.FILL);

		onView(isRoot())
				.perform(click());

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.checkLayerCount(2)
				.performClose();

		final int buttonPosition = 2;
		selectColorPickerPresetSelectorColor(buttonPosition);
		int colorSecondLayer = PaintroidApplication.currentTool.getDrawPaint().getColor();

		onView(isRoot())
				.perform(click());

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(1)
				.performDeleteLayer()
				.performClose()
				.checkLayerCount(1);

		onTopBarView()
				.performUndo();

		onLayerMenuView()
				.checkLayerCount(2);

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onView(isRoot())
				.perform(click());
		int colorAfterUndo = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Second layer should be in foreground", colorSecondLayer, colorAfterUndo);
	}
}
