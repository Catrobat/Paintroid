/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.intro;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.paintroid.Session.IS_FIRST_TIME_LAUNCH;
import static org.catrobat.paintroid.Session.PREF_NAME;
import static org.catrobat.paintroid.Session.PRIVATE_MODE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SessionTest {

	private SharedPreferences.Editor editor;
	private Session session;

	@Before
	public void setUp() {
		Context context = InstrumentationRegistry.getTargetContext();
		SharedPreferences sharedPreferences = context
				.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		session = new Session(context);
		editor = sharedPreferences.edit();
	}

	@Test
	public void testIsFirstLaunch() throws InterruptedException {
		editor.putBoolean(IS_FIRST_TIME_LAUNCH, true);
		editor.commit();
		assertTrue(session.isFirstTimeLaunch());
	}

	@Test
	public void testNotFirstLaunch() {
		editor.putBoolean(IS_FIRST_TIME_LAUNCH, false);
		editor.commit();
		assertFalse(session.isFirstTimeLaunch());
	}

	@Test
	public void testSessionSetter() {
		session.setFirstTimeLaunch(true);
		assertTrue(session.isFirstTimeLaunch());

		session.setFirstTimeLaunch(false);
		assertFalse(session.isFirstTimeLaunch());
	}

	@Test
	public void testNoSharedPreference() {
		editor.clear().commit();
		assertTrue(session.isFirstTimeLaunch());
	}
}
