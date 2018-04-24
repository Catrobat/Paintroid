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

package org.catrobat.paintroid;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
	// shared pref mode
	public static final int PRIVATE_MODE = 0;
	// Shared preferences file name
	public static final String PREF_NAME = "PocketPaint";
	public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	Context context;

	public Session(Context context) {
		this.context = context;
		pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public boolean isFirstTimeLaunch() {
		return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
	}

	public void setFirstTimeLaunch(boolean isFirstTime) {
		editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
		editor.commit();
	}
}
