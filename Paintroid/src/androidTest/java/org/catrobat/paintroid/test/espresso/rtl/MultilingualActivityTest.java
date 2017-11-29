/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.rtl;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.rtl.util.RtlUiTestUtils.checkTextDirection;
import static org.catrobat.paintroid.test.espresso.rtl.util.RtlUiTestUtils.openMultilingualActivity;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.closeNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getConfiguration;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openNavigationDrawer;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MultilingualActivityTest {
	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();
	private static final Locale ARABICLOCALE = new Locale("ar");
	private static final String ARABIC_SAVE_IMAGE = "حفظ الصورة";
	private static final String ARABIC_LOAD_IMAGE = "استيراد صورة";
	private static final Locale URDULOCALE = new Locale("ur");
	private static final String URDU_NEW_IMAGE = "نئی تصویر";
	private static final String URDU_FULLSCREEN = "پورا پردہ";
	private static final Locale FARSILOCALE = new Locale("fa");
	private static final String FARSI_SAVE_COPY = "ذخیره رونوشت";
	private static final String FARSI_TERMS_OF_USE = "شرایط استفاده";
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new RtlActivityTestRule<>(MainActivity.class);

	@Test
	public void switchLanguageToArabic() throws Exception {
		openMultilingualActivity();
		onData(hasToString(startsWith(ARABICLOCALE.getDisplayName(ARABICLOCALE)))).perform(click());
		assertEquals(Locale.getDefault().getDisplayLanguage(), ARABICLOCALE.getDisplayLanguage());
		assertEquals(View.LAYOUT_DIRECTION_RTL, getConfiguration().getLayoutDirection());
		assertTrue(checkTextDirection(Locale.getDefault().getDisplayName()));

		openNavigationDrawer();
		onView(withText(R.string.menu_save_image))
				.check(matches(withText(ARABIC_SAVE_IMAGE)));
		onView(withText(R.string.menu_load_image))
				.check(matches(withText(ARABIC_LOAD_IMAGE)));
		closeNavigationDrawer();
	}

	@Test
	public void switchLanguageToUrdu() throws Exception {
		openMultilingualActivity();

		onData(hasToString(startsWith(URDULOCALE.getDisplayName(URDULOCALE)))).perform(click());
		assertEquals(Locale.getDefault().getDisplayLanguage(), URDULOCALE.getDisplayLanguage());
		assertEquals(View.LAYOUT_DIRECTION_RTL, getConfiguration().getLayoutDirection());
		assertTrue(checkTextDirection(Locale.getDefault().getDisplayName()));

		openNavigationDrawer();
		onView(withText(R.string.menu_new_image))
				.check(matches(withText(URDU_NEW_IMAGE)));
		onView(withText(R.string.menu_hide_menu))
				.check(matches(withText(URDU_FULLSCREEN)));
		closeNavigationDrawer();
	}

	@Test
	public void switchLanguageToFarsi() throws Exception {
		openMultilingualActivity();
		onData(hasToString(startsWith(FARSILOCALE.getDisplayName(FARSILOCALE)))).perform(click());
		assertEquals(Locale.getDefault().getDisplayLanguage(), FARSILOCALE.getDisplayLanguage());
		assertEquals(View.LAYOUT_DIRECTION_RTL, getConfiguration().getLayoutDirection());
		assertTrue(checkTextDirection(Locale.getDefault().getDisplayName()));

		openNavigationDrawer();
		onView(withText(R.string.menu_save_copy))
				.check(matches(withText(FARSI_SAVE_COPY)));
		onView(withText(R.string.menu_terms_of_use_and_service))
				.check(matches(withText(FARSI_TERMS_OF_USE)));
		closeNavigationDrawer();
	}
}
