package org.catrobat.paintroid;

import android.content.SharedPreferences;

import static org.catrobat.paintroid.common.Constants.SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG;

public class UserPreferences {
	SharedPreferences preferences;

	public UserPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public boolean getBoolean(String tag, boolean defaultReturnValue) {
		return preferences.getBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, false);
	}

	public void setBoolean(String tag, boolean value) {
		preferences
			.edit()
			.putBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, true)
			.apply();
	}
}
