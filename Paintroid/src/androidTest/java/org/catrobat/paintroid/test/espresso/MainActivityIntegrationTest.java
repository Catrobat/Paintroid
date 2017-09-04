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
import org.catrobat.paintroid.R;
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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.longClickOnTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() {
		selectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
//		closeNavigationDrawer();
	}

	@Test
	public void navigationDrawer_menu_termsOfUseAndServiceTextIsCorrect() {
		openNavigationDrawer();

		onView(withText(R.string.menu_terms_of_use_and_service)).perform(click());

		onView(withText(R.string.terms_of_use_and_service_title)).check(matches(isDisplayed()));
		onView(withText(R.string.terms_of_use_and_service_content)).check(matches(isDisplayed()));

		pressBack();

		onView(withId(R.id.nav_view)).check(matches(not(isDisplayed())));
	}

	@Test
	public void navigationDrawer_menu_menuAboutTextIsCorrect() {

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

	private void toolHelpTest(ToolType toolToClick, int expectedHelpTextResourceId) {
		longClickOnTool(toolToClick);

		onView(withText(expectedHelpTextResourceId)).check(matches(isDisplayed()));
		onView(withText(android.R.string.ok)).check(matches(isDisplayed()));
		onView(withText(toolToClick.getNameResource())).check(matches(isDisplayed()));

		onView(withText(android.R.string.ok)).perform(click());
	}
}
