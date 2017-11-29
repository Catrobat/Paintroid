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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ImageButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCanvasPointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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

		selectTool(ToolType.BRUSH);

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

	@After
	public void tearDown() {
		launchActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Test
	public void testUndoRedoIconsWhenSwitchToLandscapeMode() {
		assertEquals("Wrong screen orientation",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityHelper.getScreenOrientation());

		ImageButton undoButton = UndoRedoManager.getInstance().getTopBar().getUndoButton();
		Bitmap undoButtonDisabled = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();

		ImageButton redoButton = UndoRedoManager.getInstance().getTopBar().getRedoButton();
		Bitmap redoButtonDisabled = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();

		onView(isRoot()).perform(touchAt(pointOnScreenLeft.x, pointOnScreenLeft.y));
		assertEquals("Wrong pixel color", Color.BLACK, PaintroidApplication.drawingSurface.getPixel(pointOnCanvasLeft));

		Bitmap undoButtonEnabled = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		assertNotEquals("Undo button should be enabled", undoButtonEnabled, undoButtonDisabled);

		onView(isRoot()).perform(touchAt(pointOnScreenMiddle.x, pointOnScreenMiddle.y));
		assertEquals("Wrong pixel color", Color.BLACK, PaintroidApplication.drawingSurface.getPixel(pointOnCanvasMiddle));

		onView(isRoot()).perform(touchAt(pointOnScreenRight.x, pointOnScreenRight.y));
		assertEquals("Wrong pixel color", Color.BLACK, PaintroidApplication.drawingSurface.getPixel(pointOnCanvasRight));

		assertEquals("Redo button should still be disabled",
				redoButtonDisabled, ((BitmapDrawable) redoButton.getDrawable()).getBitmap());

		onView(withId(R.id.btn_top_undo)).perform(click());
		Bitmap redoButtonEnabled = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
		assertNotEquals("Redo button should be enabled", redoButtonEnabled, redoButtonDisabled);

		activityHelper.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		assertEquals("Wrong screen orientation",
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, activityHelper.getScreenOrientation());

		EspressoUtils.waitForIdleSync();

		undoButton = UndoRedoManager.getInstance().getTopBar().getUndoButton();
		redoButton = UndoRedoManager.getInstance().getTopBar().getRedoButton();

		assertTrue("Undo button should be enabled",
				undoButtonEnabled.sameAs(((BitmapDrawable) undoButton.getDrawable()).getBitmap()));
		assertTrue("Redo button should be enabled",
				redoButtonEnabled.sameAs(((BitmapDrawable) redoButton.getDrawable()).getBitmap()));

		onView(withId(R.id.btn_top_redo)).perform(click());

		assertTrue("Redo button should be disabled",
				redoButtonDisabled.sameAs(((BitmapDrawable) redoButton.getDrawable()).getBitmap()));

		onView(withId(R.id.btn_top_undo)).perform(click());
		assertEquals("Wrong pixel color", Color.TRANSPARENT, PaintroidApplication.drawingSurface.getPixel(pointOnCanvasRight));
		onView(withId(R.id.btn_top_undo)).perform(click());
		assertEquals("Wrong pixel color", Color.TRANSPARENT, PaintroidApplication.drawingSurface.getPixel(pointOnCanvasMiddle));
		onView(withId(R.id.btn_top_undo)).perform(click());
		assertEquals("Wrong pixel color", Color.TRANSPARENT, PaintroidApplication.drawingSurface.getPixel(pointOnCanvasLeft));

		assertTrue("Undo button should be disabled",
				undoButtonDisabled.sameAs(((BitmapDrawable) undoButton.getDrawable()).getBitmap()));

		activityHelper.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}
