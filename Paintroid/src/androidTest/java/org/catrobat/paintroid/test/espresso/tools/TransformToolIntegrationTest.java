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

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ImageButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.addNewLayer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.closeLayerMenu;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openLayerMenu;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectLayer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class TransformToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;
	private IdlingResource dialogWait;


	@Before
	public void setUp() {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		Espresso.registerIdlingResources(dialogWait);

		activityHelper = new ActivityHelper(launchActivityRule.getActivity());
	}

	@After
	public void tearDown() {
		Espresso.unregisterIdlingResources(dialogWait);

		activityHelper = null;
	}

	@Test
	public void testRotateMultipleLayers() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		int bitmapHeightOnStartup = layers.get(0).getImage().getHeight();
		int bitmapWidthOnStartup = layers.get(0).getImage().getWidth();

		openLayerMenu();
		onView(withId(R.id.layer_side_nav_button_add)).perform(click());
		onView(withId(R.id.layer_side_nav_button_add)).perform(click());
		closeLayerMenu();

		selectTool(ToolType.TRANSFORM);
		onView(withId(R.id.transform_rotate_right_btn)).perform(click());

		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}

		onView(withId(R.id.transform_rotate_left_btn)).perform(click());

		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after rotating", bitmapWidthOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after rotating", bitmapHeightOnStartup, layer.getImage().getHeight());
		}
	}

	@Test
	public void testRotateMultipleLayersUndoRedo() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		int bitmapHeightOnStartup = layers.get(0).getImage().getHeight();
		int bitmapWidthOnStartup = layers.get(0).getImage().getWidth();

		openLayerMenu();
		addNewLayer();
		addNewLayer();
		closeLayerMenu();

		selectTool(ToolType.TRANSFORM);

		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}

		EspressoUtils.clickSelectedToolButton();
		onView(withId(R.id.btn_top_undo)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after undo rotating", bitmapWidthOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after undo rotating", bitmapHeightOnStartup, layer.getImage().getHeight());
		}

		onView(withId(R.id.btn_top_redo)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after redo rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after redo rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}
	}

	@Test
	public void testRotateMultipleLayersUndoRedoWhenRotatingWasNotLastCommand() throws NoSuchFieldException, IllegalAccessException {
		ImageButton undoButton = UndoRedoManager.getInstance().getTopBar().getUndoButton();
		Bitmap undoButtonDisabled = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		ImageButton redoButton = UndoRedoManager.getInstance().getTopBar().getRedoButton();
		Bitmap redoButtonDisabled = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();

		int displayWidth  = activityHelper.getDisplayWidth();
		int displayHeight = activityHelper.getDisplayHeight();
		PointF pointOnScreenLeft = new PointF(displayWidth*0.25f, displayHeight*0.5f);
		PointF pointOnScreenRight = new PointF(displayWidth*0.75f, displayHeight*0.5f);
		PointF pointOnScreenMiddle = new PointF(displayWidth*0.5f, displayHeight*0.5f);

		ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
		int bitmapHeightOnStartup = layers.get(0).getImage().getHeight();
		int bitmapWidthOnStartup = layers.get(0).getImage().getWidth();

		openLayerMenu();
		addNewLayer();
		addNewLayer();
		closeLayerMenu();

		selectTool(ToolType.TRANSFORM);

		onView(withId(R.id.transform_rotate_right_btn)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}

		selectTool(ToolType.BRUSH);
		onView(isRoot()).perform(touchAt(pointOnScreenLeft));
		onView(isRoot()).perform(touchAt(pointOnScreenRight));

		openLayerMenu();
		int layerListPositionForUndoRotation = 1;
		selectLayer(layerListPositionForUndoRotation);
		closeLayerMenu();

		onView(withId(R.id.btn_top_undo)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after undo rotating", bitmapWidthOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after undo rotating", bitmapHeightOnStartup, layer.getImage().getHeight());
		}

		assertTrue("Undo button should be disabled", undoButtonDisabled.sameAs(((BitmapDrawable) undoButton.getDrawable()).getBitmap()));
		assertFalse("Redo button should be enabled", redoButtonDisabled.sameAs(((BitmapDrawable) redoButton.getDrawable()).getBitmap()));
		openLayerMenu();
		selectLayer(0);
		assertFalse("Undo button should be enabled", undoButtonDisabled.sameAs(((BitmapDrawable) undoButton.getDrawable()).getBitmap()));
		assertTrue("Redo button should be disabled", redoButtonDisabled.sameAs(((BitmapDrawable) redoButton.getDrawable()).getBitmap()));

		int layerListPositionForDeletingRedoCommands = 1;
		selectLayer(layerListPositionForDeletingRedoCommands);
		closeLayerMenu();
		onView(isRoot()).perform(touchAt(pointOnScreenMiddle));

		int layerListPositionForRedoRotation = 2;
		openLayerMenu();
		selectLayer(layerListPositionForRedoRotation);
		closeLayerMenu();

		onView(withId(R.id.btn_top_redo)).perform(click());
		for (Layer layer : layers) {
			assertEquals("Wrong bitmap width after redo rotating", bitmapHeightOnStartup, layer.getImage().getWidth());
			assertEquals("Wrong bitmap height after redo rotating", bitmapWidthOnStartup, layer.getImage().getHeight());
		}
	}

}
