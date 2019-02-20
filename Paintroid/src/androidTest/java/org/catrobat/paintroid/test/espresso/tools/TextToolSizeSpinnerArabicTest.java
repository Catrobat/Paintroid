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

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.rtl.util.RtlUiTestUtils.checkTextDirection;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getConfiguration;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withIndex;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TextToolSizeSpinnerArabicTest {
	private static final Locale ARABICLOCALE = new Locale("ar");

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule =
			new RtlActivityTestRule<>(MainActivity.class, "ar");

	@Test
	public void testArabicNumberFormatOfSizeSpinner() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), ARABICLOCALE.getDisplayLanguage());
		assertEquals(View.LAYOUT_DIRECTION_RTL, getConfiguration().getLayoutDirection());
		assertTrue(checkTextDirection(Locale.getDefault().getDisplayName()));

		onToolBarView()
				.performSelectTool(ToolType.TEXT);
		onView(withId(R.id.pocketpaint_text_tool_dialog_spinner_text_size)).perform(click());

		onView(withIndex(withId(android.R.id.text1), 0)).check(matches(withText("٢٠بكسل")));

		onView(withIndex(withId(android.R.id.text1), 1)).check(matches(withText("٣٠بكسل")));

		onView(withIndex(withId(android.R.id.text1), 2)).check(matches(withText("٤٠بكسل")));

		onView(withIndex(withId(android.R.id.text1), 3)).check(matches(withText("٦٠بكسل")));
	}
}
