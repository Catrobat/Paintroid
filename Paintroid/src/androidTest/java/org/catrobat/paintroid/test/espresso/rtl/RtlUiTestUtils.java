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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;

import org.catrobat.paintroid.MultilingualActivity;
import org.catrobat.paintroid.R;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.MultilingualActivity.LANGUAGE_TAG_KEY;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getResources;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openNavigationDrawer;

final class RtlUiTestUtils {
	static Configuration config = getResources().getConfiguration();
	private static Locale defaultLocale = Locale.getDefault();

	private RtlUiTestUtils() {
		throw new AssertionError();
	}

	static boolean checkTextDirection(String string) {
		return Character.getDirectionality(string.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT
				|| Character.getDirectionality(string.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}

	static void resetToDefaultLanguage() {
		SharedPreferences sharedPreferences = InstrumentationRegistry.getTargetContext().getSharedPreferences("For_language", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(LANGUAGE_TAG_KEY, defaultLocale.getLanguage());
		editor.commit();
		MultilingualActivity.updateLocale(InstrumentationRegistry.getTargetContext(), defaultLocale.getLanguage(), null);
	}

	static void openMultilingualActivity() {
		openNavigationDrawer();
		onView(withText(R.string.menu_language)).perform(click());
	}
}
