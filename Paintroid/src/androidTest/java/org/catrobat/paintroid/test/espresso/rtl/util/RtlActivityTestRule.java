/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.rtl.util;

import android.app.Activity;
import android.content.Context;

import org.catrobat.paintroid.test.espresso.util.LanguageSupport;

import java.util.Locale;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

public class RtlActivityTestRule<T extends Activity> extends ActivityTestRule<T> {
	private final String language;

	public RtlActivityTestRule(Class<T> activityClass, String language) {
		super(activityClass);
		this.language = language;
	}

	@Override
	protected void beforeActivityLaunched() {
		super.beforeActivityLaunched();

		Locale locale = new Locale(language);
		Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		LanguageSupport.setLocale(targetContext, locale);
	}

	@Override
	protected void afterActivityFinished() {
		super.afterActivityFinished();

		Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		LanguageSupport.setLocale(targetContext, new Locale("en"));
	}
}
