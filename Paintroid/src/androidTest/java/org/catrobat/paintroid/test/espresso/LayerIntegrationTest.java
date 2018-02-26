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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.BLACK_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.GREEN_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.WHITE_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LayerIntegrationTest {
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private DialogHiddenIdlingResource dialogWait;
	private int bitmapHeight;
	private int bitmapWidth;

	@Before
	public void setUp() throws Exception {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		IdlingRegistry.getInstance().register(dialogWait);

		Bitmap image = LayerListener.getInstance().getCurrentLayer().getImage();
		bitmapHeight = image.getHeight();
		bitmapWidth = image.getWidth();
	}

	@After
	public void tearDown() throws Exception {
		IdlingRegistry.getInstance().unregister(dialogWait);
	}

	@Test
	public void testShowLayerMenu() {
		onLayerMenuView()
				.performOpen()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testInitialSetup() {
		onLayerMenuView()
				.check(matches(not(isDisplayed())));
		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_new))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_delete_disabled))));
	}

	@Test
	public void testAddOneLayer() {
		onLayerMenuView()
				.checkLayerCount(1)
				.performOpen()
				.performAddLayer()
				.checkLayerCount(2);
	}

	@Test
	public void testTryAddMoreLayersThanLimit() {
		onLayerMenuView()
				.checkLayerCount(1)
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performAddLayer()
				.checkLayerCount(4)
				.performAddLayer()
				.checkLayerCount(4);
	}

	@Test
	public void testButtonsAddOneLayer() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.checkLayerCount(2);

		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_new))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_delete))));

		onLayerMenuView()
				.performAddLayer()
				.performAddLayer()
				.checkLayerCount(4);

		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_new_disabled))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_delete))));

		onLayerMenuView()
				.performDeleteLayer()
				.performDeleteLayer()
				.performDeleteLayer()
				.checkLayerCount(1);

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
				.performClose()
				.checkLayerCount(4);

		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_new_disabled))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_delete))));

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
				.performAddLayer()
				.performClose()
				.checkLayerCount(2);

		onTopBarView()
				.performUndo();

		onLayerMenuView()
				.checkLayerCount(1);

		onTopBarView()
				.performRedo();

		onLayerMenuView()
				.checkLayerCount(2);
	}

	@Test
	public void testDeleteEmptyLayer() {
		onLayerMenuView()
				.checkLayerCount(1)
				.performOpen()
				.performAddLayer()
				.checkLayerCount(2)
				.performDeleteLayer()
				.checkLayerCount(1);
	}

	@Test
	public void testDeleteFilledLayer() {
		onLayerMenuView()
				.checkLayerCount(1)
				.performOpen()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkColor(Color.TRANSPARENT);

		onToolBarView()
				.performSelectTool(ToolType.FILL);
		selectColorPickerPresetSelectorColor(BLACK_COLOR_PICKER_BUTTON_POSITION);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkColor(Color.BLACK);

		onLayerMenuView()
				.checkLayerCount(2)
				.performOpen()
				.performDeleteLayer()
				.performClose()
				.checkLayerCount(1);

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkColor(Color.TRANSPARENT);
	}

	@Test
	public void testTryDeleteOnlyLayer() {
		onLayerMenuView()
				.checkLayerCount(1)
				.performOpen()
				.performDeleteLayer()
				.checkLayerCount(1);
	}

	@Test
	public void testSwitchBetweenFilledLayers() {
		onToolBarView()
				.performSelectTool(ToolType.FILL);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkColor(Color.BLACK);

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.FILL);
		selectColorPickerPresetSelectorColor(WHITE_COLOR_PICKER_BUTTON_POSITION);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkColor(Color.WHITE);

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(1)
				.performClose();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkColor(Color.WHITE);
	}

	@Test
	public void testMultipleLayersNewImageDiscardOld() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performAddLayer()
				.checkLayerCount(4)
				.performClose();

		onNavigationDrawer()
				.performOpen();
		onView(withText(R.string.menu_new_image))
				.perform(click());
		onView(withText(R.string.discard_button_text))
				.perform(click());
		onView(withText(R.string.menu_new_image_empty_image))
				.perform(click());

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkColor(Color.TRANSPARENT);
		onLayerMenuView()
				.checkLayerCount(1);
	}

	@Test
	public void testMultipleLayersNewImageSaveOld() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performAddLayer()
				.checkLayerCount(4)
				.performClose();

		onNavigationDrawer()
				.performOpen();
		onView(withText(R.string.menu_new_image))
				.perform(click());
		onView(withText(R.string.save_button_text))
				.perform(click());
		onView(withText(R.string.menu_new_image_empty_image))
				.perform(click());

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolProperties()
				.checkColor(Color.TRANSPARENT);

		onLayerMenuView()
				.checkLayerCount(1);
	}

	@Test
	public void testResizingThroughAllLayers() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performAutoCrop();
		onToolBarView()
				.performCloseToolOptions();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkThatLayerDimensions(lessThan(bitmapWidth), lessThan(bitmapHeight));

		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapWidth, bitmapHeight);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkThatLayerDimensions(lessThan(bitmapWidth), lessThan(bitmapHeight));

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(2)
				.performClose();

		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapWidth, bitmapHeight);

		onLayerMenuView()
				.performOpen()
				.performSelectLayer(3)
				.performClose();

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkThatLayerDimensions(lessThan(bitmapWidth), lessThan(bitmapHeight));

		onLayerMenuView()
				.performOpen()
				.performDeleteLayer()
				.performAddLayer()
				.performClose();

		onTopBarView()
				.performUndo()
				.performUndo()
				.performUndo();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapWidth, bitmapHeight);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkThatLayerDimensions(lessThan(bitmapWidth), lessThan(bitmapHeight));
	}

	@Test
	public void testRotatingThroughAllLayers() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.FILL);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapHeight, bitmapWidth);

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolProperties()
				.checkColor(Color.BLACK);

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateCounterClockwise()
				.performRotateCounterClockwise();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapHeight, bitmapWidth);

		onToolBarView()
				.performSelectTool(ToolType.PIPETTE);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onToolProperties()
				.checkColor(Color.BLACK);
	}

	@Test
	public void testReflectingOnlyCurrentLayer() {
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE));

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performClose();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performFlipVertical();

		onToolBarView().performSelectTool(ToolType.PIPETTE);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));
		onToolProperties()
				.checkColor(Color.TRANSPARENT);

		onTopBarView()
				.performUndo();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));
		onToolProperties()
				.checkColor(Color.BLACK);

		onTopBarView()
				.performRedo();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE));
		onToolProperties()
				.checkColor(Color.TRANSPARENT);
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

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.checkLayerCount(2)
				.performClose();

		selectColorPickerPresetSelectorColor(GREEN_COLOR_PICKER_BUTTON_POSITION);
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

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
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onToolProperties()
				.checkColorResource(R.color.color_chooser_green1);
	}

	@Test
	public void testUndoRedoLayerRotate() {
		onLayerMenuView()
				.performOpen()
				.performAddLayer()
				.performAddLayer()
				.performAddLayer()
				.performClose();

		onToolBarView()
				.performSelectTool(ToolType.TRANSFORM);
		onTransformToolOptionsView()
				.performRotateClockwise();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapHeight, bitmapWidth);

		onToolBarView()
				.performCloseToolOptions();
		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapWidth, bitmapHeight);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapHeight, bitmapWidth);

		onLayerMenuView()
				.performOpen()
				.performDeleteLayer()
				.performAddLayer()
				.performSelectLayer(3)
				.performClose();

		onTopBarView()
				.performUndo()
				.performUndo()
				.performUndo();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapWidth, bitmapHeight);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkLayerDimensions(bitmapHeight, bitmapWidth);
	}
}
