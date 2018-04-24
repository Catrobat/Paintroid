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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.addNewLayer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.closeLayerMenu;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getSurfacePointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.longClickOnTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openLayerMenu;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectLayer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntegrationTest {

	public static final int ARRAY_COLOR_RED = -3865074;
	public static final int ARRAY_POSITION_RED = 12;
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);
	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();
	private PointF pointOnScreenMiddle;
	private ActivityHelper activityHelper;

	@Before
	public void setUp() {
		activityHelper = new ActivityHelper(launchActivityRule.getActivity());
		int displayWidth = activityHelper.getDisplayWidth();
		int displayHeight = activityHelper.getDisplayHeight();
		pointOnScreenMiddle = new PointF(displayWidth / 2, displayHeight / 2);
		selectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void navigationDrawerMenuTermsOfUseAndServiceTextIsCorrect() {
		openNavigationDrawer();

		onView(withText(R.string.menu_terms_of_use_and_service)).perform(click());

		onView(withText(R.string.terms_of_use_and_service_title)).check(matches(isDisplayed()));
		onView(withText(R.string.terms_of_use_and_service_content)).check(matches(isDisplayed()));

		pressBack();

		onView(withId(R.id.nav_view)).check(matches(not(isDisplayed())));
	}

	@Test
	public void navigationDrawerMenuMenuAboutTextIsCorrect() {

		openNavigationDrawer();

		onView(withText(R.string.menu_about)).perform(click());

		String aboutTextExpected = launchActivityRule.getActivity().getString(R.string.about_content);
		String licenseText = launchActivityRule.getActivity().getString(R.string.license_type_paintroid);
		aboutTextExpected = String.format(aboutTextExpected, licenseText);

		onView(withText(aboutTextExpected)).check(matches(isDisplayed()));

		pressBack();

		onView(withId(R.id.nav_view)).check(matches(not(isDisplayed())));
	}

	@Test
	public void testHelpDialogForBrush() {
		toolHelpTest(ToolType.BRUSH, R.string.help_content_brush);
	}

	@Test
	public void testHelpDialogForCursor() {
		toolHelpTest(ToolType.CURSOR, R.string.help_content_cursor);
	}

	@Test
	public void testHelpDialogForPipette() {
		toolHelpTest(ToolType.PIPETTE, R.string.help_content_eyedropper);
	}

	@Test
	public void testHelpDialogForStamp() {
		toolHelpTest(ToolType.STAMP, R.string.help_content_stamp);
	}

	@Test
	public void testHelpDialogForBucket() {
		toolHelpTest(ToolType.FILL, R.string.help_content_fill);
	}

	@Test
	public void testHelpDialogForShape() {
		toolHelpTest(ToolType.SHAPE, R.string.help_content_shape);
	}

	@Test
	public void testHelpDialogForTransform() {
		toolHelpTest(ToolType.TRANSFORM, R.string.help_content_transform);
	}

	@Test
	public void testHelpDialogForEraser() {
		toolHelpTest(ToolType.ERASER, R.string.help_content_eraser);
	}

	@Test
	public void testHelpDialogForImportImage() {
		toolHelpTest(ToolType.IMPORTPNG, R.string.help_content_import_png);
	}

	@Test
	public void testHelpDialogForText() {
		toolHelpTest(ToolType.TEXT, R.string.help_content_text);
	}

	@Test
	public void testSessionArtefactsHelpTest() {
		openLayerMenu();
		addNewLayer();
		closeLayerMenu();
		selectTool(ToolType.BRUSH);

		PointF pointOnSurface = getSurfacePointFromScreenPoint(pointOnScreenMiddle);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnSurface);

		int currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, currentColor);

		onView(isRoot()).perform(touchAt(pointOnScreenMiddle));

		currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing point has to be black", Color.BLACK, currentColor);

		selectColorPickerPresetSelectorColor(ARRAY_POSITION_RED);
	}

	@Test
	public void testSessionArtefactsReopen() {
		//testSessionArtefactsHelpTest() should be called first

		int selectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color after orientation changed has to be black", Color.BLACK, selectedColor);

		PointF pointOnSurface = getSurfacePointFromScreenPoint(pointOnScreenMiddle);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnSurface);
		int currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Bitmap Point Color after restart has to be transparent", Color.TRANSPARENT, currentColor);

		openLayerMenu();
		selectLayer(0);
		closeLayerMenu();
	}

	@Test
	public void testSessionArtefactsChangeOrientation() {
		openLayerMenu();
		addNewLayer();
		closeLayerMenu();
		selectTool(ToolType.BRUSH);

		PointF pointOnSurface = getSurfacePointFromScreenPoint(pointOnScreenMiddle);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnSurface);

		int currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, currentColor);

		onView(isRoot()).perform(touchAt(pointOnScreenMiddle));

		currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing point has to be black", Color.BLACK, currentColor);

		selectColorPickerPresetSelectorColor(ARRAY_POSITION_RED);

		launchActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		launchActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		int selectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Color after orientation changed has to be red", ARRAY_COLOR_RED, selectedColor);

		currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Bitmap Point Color after orientation changed has to be black", Color.BLACK, currentColor);

		openLayerMenu();
		selectLayer(0);
		selectLayer(1);
		closeLayerMenu();
	}

	private void toolHelpTest(ToolType toolToClick, int expectedHelpTextResourceId) {
		longClickOnTool(toolToClick);

		onView(withText(expectedHelpTextResourceId)).check(matches(isDisplayed()));
		onView(withText(android.R.string.ok)).check(matches(isDisplayed()));
		onView(withText(toolToClick.getNameResource())).check(matches(isDisplayed()));

		onView(withText(android.R.string.ok)).perform(click());
	}
}
