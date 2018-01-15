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

import android.graphics.Color;
import android.graphics.PointF;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction;
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.Layer;
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
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LayerIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private int displayWidth;
	private int displayHeight;

	@Before
	public void setUp() throws Exception {
		ActivityHelper activityHelper = new ActivityHelper(launchActivityRule.getActivity());
		displayWidth = activityHelper.getDisplayWidth();
		displayHeight = activityHelper.getDisplayHeight();
	}

	@Test
	public void testShowLayerMenu() {
		onLayerMenuView().performOpen();
	}

	@Test
	public void testInitialSetup() {
		onLayerMenuView().onButtonAdd()
				.check(matches(allOf(isEnabled(), withDrawable(R.drawable.icon_layers_new))));
		onLayerMenuView().onButtonDelete()
				.check(matches(allOf(not(isEnabled()), withDrawable(R.drawable.icon_layers_delete_disabled))));
	}

	@Test
	public void testAddOneLayer() {
		int numLayersBefore = LayerListener.getInstance().getAdapter().getCount();
		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();
		int numLayersAfter = LayerListener.getInstance().getAdapter().getCount();
		assertTrue("One Layer should have been added", numLayersBefore + 1 == numLayersAfter);
	}

	@Test
	public void testTryAddMoreLayersThanLimit() {
		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();

		assertEquals("Only four layers should exist", 4, LayerListener.getInstance().getAdapter().getCount());
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
	public void testDeleteEmptyLayer() {
		int numLayersBefore = LayerListener.getInstance().getAdapter().getCount();
		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		int numLayersAfter = LayerListener.getInstance().getAdapter().getCount();
		assertTrue("One Layer should have been added", numLayersBefore + 1 == numLayersAfter);

		numLayersBefore = LayerListener.getInstance().getAdapter().getCount();
		onLayerMenuView().performDeleteLayer();
		numLayersAfter = LayerListener.getInstance().getAdapter().getCount();
		assertTrue("One Layer should have been deleted", numLayersBefore - 1 == numLayersAfter);
	}

	@Test
	public void testDeleteFilledLayer() {
		int numLayersBefore = LayerListener.getInstance().getAdapter().getCount();
		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();
		int numLayersAfter = LayerListener.getInstance().getAdapter().getCount();
		assertTrue("One Layer should have been added", numLayersBefore + 1 == numLayersAfter);

		PointF screenPoint = new PointF(displayWidth / 2 - 10, displayHeight / 2 - 5);
		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		int colorBeforeFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.FILL);
		final int positionBlackColorButton = 16;
		selectColorPickerPresetSelectorColor(positionBlackColorButton);
		onView(isRoot()).perform(touchAt(screenPoint));

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		int colorAfterFill = PaintroidApplication.currentTool.getDrawPaint().getColor();

		assertEquals("Color should be black after fill", Color.BLACK, colorAfterFill);

		numLayersBefore = LayerListener.getInstance().getAdapter().getCount();
		onLayerMenuView().performOpen();
		onLayerMenuView().performDeleteLayer();
		onLayerMenuView().performClose();
		numLayersAfter = LayerListener.getInstance().getAdapter().getCount();
		assertTrue("One Layer should have been deleted", numLayersBefore - 1 == numLayersAfter);

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		int colorAfterDelete = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Black Layer should be deleted", colorBeforeFill, colorAfterDelete);
	}

	@Test
	public void testTryDeleteOnlyLayer() {
		int numLayers = LayerListener.getInstance().getAdapter().getCount();
		assertEquals("Only one Layer should exist", 1, numLayers);

		onLayerMenuView().performOpen();
		onLayerMenuView().performDeleteLayer();
		onLayerMenuView().performClose();

		numLayers = LayerListener.getInstance().getAdapter().getCount();
		assertEquals("Still only one Layer should exist", 1, numLayers);
	}

	@Test
	public void testSwitchBetweenFilledLayers() {
		PointF screenPoint = new PointF(displayWidth / 2 - 10, displayHeight / 2 - 5);
		final int positionWhiteColorButton = 18;

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.FILL);
		onView(isRoot()).perform(touchAt(screenPoint));
		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		int colorAfterFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should be black after fill", Color.BLACK, colorAfterFill);

		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.FILL);
		selectColorPickerPresetSelectorColor(positionWhiteColorButton);
		onView(isRoot()).perform(touchAt(screenPoint));
		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		colorAfterFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should be white after fill", Color.WHITE, colorAfterFill);

		onLayerMenuView().performOpen();
		onLayerMenuView().performSelectLayer(1);
		onLayerMenuView().performClose();
		onView(isRoot()).perform(touchAt(screenPoint));
		colorAfterFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should still be white after select another Layer", Color.WHITE, colorAfterFill);
	}

	@Test
	public void testMultipleLayersNewImageDiscardOld() {
		PointF screenPoint = new PointF(displayWidth / 2 - 10, displayHeight / 2 - 5);
		onView(isRoot()).perform(touchAt(screenPoint));

		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();

		onView(withId(R.id.toolbar)).perform(click());
		NavigationDrawerInteraction.onNavigationDrawer().performOpen();
		onView(withText(R.string.menu_new_image)).perform(click());
		onView(withText(R.string.discard_button_text)).perform(click());
		onView(withText(R.string.menu_new_image_empty_image)).perform(click());

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		int colorAfterNewImage = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should be white after fill", Color.TRANSPARENT, colorAfterNewImage);

		int numLayers = LayerListener.getInstance().getAdapter().getCount();
		assertEquals("Only one empty Layer should exist", 1, numLayers);
	}

	@Test
	public void testMultipleLayersNewImageSaveOld() {
		PointF screenPoint = new PointF(displayWidth / 2 - 10, displayHeight / 2 - 5);
		onView(isRoot()).perform(touchAt(screenPoint));

		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();

		onView(withId(R.id.toolbar)).perform(click());
		NavigationDrawerInteraction.onNavigationDrawer().performOpen();
		onView(withText(R.string.menu_new_image)).perform(click());
		onView(withText(R.string.save_button_text)).perform(click());
		onView(withText(R.string.menu_new_image_empty_image)).perform(click());

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		int colorAfterNewImage = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should be white after fill", Color.TRANSPARENT, colorAfterNewImage);

		int numLayers = LayerListener.getInstance().getAdapter().getCount();
		assertEquals("Only one empty Layer should exist", 1, numLayers);
	}

	@Test
	public void testResizingThroughAllLayers() {
		int bitmapHeight = LayerListener.getInstance().getCurrentLayer().getImage().getHeight();
		int bitmapWidth = LayerListener.getInstance().getCurrentLayer().getImage().getWidth();
		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();

		PointF screenPoint = new PointF(displayWidth / 2 - 10, displayHeight / 3 - 5);
		onView(isRoot()).perform(touchAt(screenPoint));

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_auto_crop_btn)).perform(click());
		onView(isRoot()).perform(touchAt(screenPoint));
		onView(isRoot()).perform(touchAt(screenPoint));

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertFalse("Bitmap should be cropped - wrong Height", bitmapHeight == layer.getImage().getHeight());
			assertFalse("Bitmap should be cropped - wrong Width", bitmapWidth == layer.getImage().getWidth());
		}

		onView(withId(R.id.btn_top_undo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertTrue("Bitmap should be cropped - wrong Height", bitmapHeight == layer.getImage().getHeight());
			assertTrue("Bitmap should be cropped - wrong Width", bitmapWidth == layer.getImage().getWidth());
		}

		onView(withId(R.id.btn_top_redo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertFalse("Bitmap should be cropped - wrong Height", bitmapHeight == layer.getImage().getHeight());
			assertFalse("Bitmap should be cropped - wrong Width", bitmapWidth == layer.getImage().getWidth());
		}

		onLayerMenuView().performOpen();
		onLayerMenuView().performSelectLayer(2);
		onLayerMenuView().performClose();
		onView(withId(R.id.btn_top_undo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertTrue("Bitmap should be cropped - wrong Height", bitmapHeight == layer.getImage().getHeight());
			assertTrue("Bitmap should be cropped - wrong Width", bitmapWidth == layer.getImage().getWidth());
		}

		onLayerMenuView().performOpen();
		onLayerMenuView().performSelectLayer(3);
		onLayerMenuView().performClose();
		onView(withId(R.id.btn_top_redo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertFalse("Bitmap should be cropped - wrong Height", bitmapHeight == layer.getImage().getHeight());
			assertFalse("Bitmap should be cropped - wrong Width", bitmapWidth == layer.getImage().getWidth());
		}

		onLayerMenuView().performOpen();
		onLayerMenuView().performDeleteLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();

		onView(withId(R.id.btn_top_undo)).perform(click());
		onView(withId(R.id.btn_top_undo)).perform(click());
		onView(withId(R.id.btn_top_undo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertTrue("Bitmap should be cropped - wrong Height", bitmapHeight == layer.getImage().getHeight());
			assertTrue("Bitmap should be cropped - wrong Width", bitmapWidth == layer.getImage().getWidth());
		}

		onView(withId(R.id.btn_top_redo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertFalse("Bitmap should be cropped - wrong Height", bitmapHeight == layer.getImage().getHeight());
			assertFalse("Bitmap should be cropped - wrong Width", bitmapWidth == layer.getImage().getWidth());
		}
	}

	@Test
	public void testRotatingThroughAllLayers() {
		int bitmapHeight = LayerListener.getInstance().getCurrentLayer().getImage().getHeight();
		int bitmapWidth = LayerListener.getInstance().getCurrentLayer().getImage().getWidth();
		PointF screenPoint = new PointF(displayWidth / 2 - 10, displayHeight / 3 - 5);

		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.FILL);
		onView(isRoot()).perform(touchAt(screenPoint));

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertEquals("Bitmap should have been rotated - wrong height", bitmapWidth, layer.getImage().getHeight());
			assertEquals("Bitmap should have been rotated - wrong width", bitmapHeight, layer.getImage().getWidth());
		}
		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		int colorAfterRotate = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should still be black after rotate", Color.BLACK, colorAfterRotate);

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_left_btn)).perform(click());
		onView(withId(R.id.transform_rotate_left_btn)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertEquals("Bitmap should have been rotated - wrong height", bitmapWidth, layer.getImage().getHeight());
			assertEquals("Bitmap should have been rotated - wrong width", bitmapHeight, layer.getImage().getWidth());
		}
		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPoint));
		colorAfterRotate = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should still be black after rotate", Color.BLACK, colorAfterRotate);
	}

	@Test
	public void testReflectingOnlyCurrentLayer() {
		PointF screenPointLeft = new PointF(displayWidth / 2 - 10, displayHeight / 2 - 5);
		PointF screenPointRight = new PointF(displayWidth / 2 + 10, displayHeight / 2 - 5);
		onView(isRoot()).perform(touchAt(screenPointLeft));

		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();
		onView(isRoot()).perform(touchAt(screenPointRight));

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.TRANSFORM);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			Log.e("LayerIntegrationTest", e.getMessage());
		}
		onView(withId(R.id.transform_flip_vertical_btn)).perform(click());

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE);
		onView(isRoot()).perform(touchAt(screenPointRight));
		int color = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should be transparent", Color.TRANSPARENT, color);

		onView(withId(R.id.btn_top_undo)).perform(click());
		onView(isRoot()).perform(touchAt(screenPointRight));
		color = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should be black", Color.BLACK, color);

		onView(withId(R.id.btn_top_redo)).perform(click());
		onView(isRoot()).perform(touchAt(screenPointRight));
		color = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color should be transparent", Color.TRANSPARENT, color);
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

	@Test
	public void testUndoRedoLayerRotate() {
		onLayerMenuView().performOpen();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performClose();

		int bitmapHeight = LayerListener.getInstance().getCurrentLayer().getImage().getHeight();
		int bitmapWidth = LayerListener.getInstance().getCurrentLayer().getImage().getWidth();

		ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertEquals("All Bitmaps should have been rotated - wrong height", bitmapWidth, layer.getImage().getHeight());
			assertEquals("All Bitmaps should have been rotated - wrong width", bitmapHeight, layer.getImage().getWidth());
		}

		PointF screenPoint = new PointF(displayWidth / 2 - 10, displayHeight / 3 - 5);
		onView(isRoot()).perform(touchAt(screenPoint));
		onView(withId(R.id.btn_top_undo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertEquals("All Bitmaps should have been rotated back - wrong height", bitmapHeight, layer.getImage().getHeight());
			assertEquals("All Bitmaps should have been rotated back - wrong width", bitmapWidth, layer.getImage().getWidth());
		}

		onView(withId(R.id.btn_top_redo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertEquals("All Bitmaps should have been rotated - wrong height", bitmapWidth, layer.getImage().getHeight());
			assertEquals("All Bitmaps should have been rotated - wrong width", bitmapHeight, layer.getImage().getWidth());
		}

		onLayerMenuView().performOpen();
		onLayerMenuView().performDeleteLayer();
		onLayerMenuView().performAddLayer();
		onLayerMenuView().performSelectLayer(3);
		onLayerMenuView().performClose();

		onView(withId(R.id.btn_top_undo)).perform(click());
		onView(withId(R.id.btn_top_undo)).perform(click());
		onView(withId(R.id.btn_top_undo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertEquals("All Bitmaps should have been rotated back - wrong height", bitmapHeight, layer.getImage().getHeight());
			assertEquals("All Bitmaps should have been rotated back - wrong width", bitmapWidth, layer.getImage().getWidth());
		}

		onView(withId(R.id.btn_top_redo)).perform(click());

		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			assertEquals("All Bitmaps should have been rotated - wrong height", bitmapWidth, layer.getImage().getHeight());
			assertEquals("All Bitmaps should have been rotated - wrong width", bitmapHeight, layer.getImage().getWidth());
		}
	}
}
