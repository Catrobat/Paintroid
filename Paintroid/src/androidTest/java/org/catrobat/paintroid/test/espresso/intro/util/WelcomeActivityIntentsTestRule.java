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

package org.catrobat.paintroid.test.espresso.intro.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.util.LayoutDirection;

import org.catrobat.paintroid.MultilingualActivity;
import org.catrobat.paintroid.Session;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;

import static org.junit.Assert.assertEquals;

public class WelcomeActivityIntentsTestRule extends IntentsTestRule<WelcomeActivity> {
	private final boolean rtl;
	private final boolean startSequence;

	public WelcomeActivityIntentsTestRule(boolean startSequence, boolean rtl) {
		super(WelcomeActivity.class);

		this.rtl = rtl;
		this.startSequence = startSequence;
	}

	public WelcomeActivityIntentsTestRule(boolean startSequence) {
		this(startSequence, false);
	}

	public WelcomeActivityIntentsTestRule() {
		this(true, false);
	}

	public int[] getLayouts() {
		return getActivity().layouts;
	}

	public int getColorActive() {
		return getActivity().colorActive;
	}

	public int getColorInactive() {
		return getActivity().colorInactive;
	}

	@Override
	protected void beforeActivityLaunched() {
		super.beforeActivityLaunched();

		EspressoUtils.shouldStartSequence(startSequence);

		String languageTagKey = rtl ? "ar" : "";
		Context targetContext = InstrumentationRegistry.getTargetContext();
		targetContext.getSharedPreferences(MultilingualActivity.SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE)
				.edit()
				.putString(MultilingualActivity.LANGUAGE_TAG_KEY, languageTagKey)
				.commit();

		Session session = new Session(targetContext);
		session.setFirstTimeLaunch(true);
	}

	@Override
	protected void afterActivityLaunched() {
		super.afterActivityLaunched();

		assertEquals(rtl, isRTL(getActivity()));
		assertEquals(rtl, isRTL(InstrumentationRegistry.getTargetContext()));
	}

	private boolean isRTL(Context context) {
		return context.getResources().getConfiguration().getLayoutDirection() == LayoutDirection.RTL;
	}

	@Override
	protected void afterActivityFinished() {
		super.afterActivityFinished();

		getActivity().getSharedPreferences(MultilingualActivity.SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE)
				.edit()
				.remove(MultilingualActivity.LANGUAGE_TAG_KEY)
				.commit();
	}
}
