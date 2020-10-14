package org.catrobat.paintroid;

import android.content.SharedPreferences;

import static org.catrobat.paintroid.common.Constants.IMAGE_NUMBER_SHARED_PREFERENCES_TAG;
import static org.catrobat.paintroid.common.Constants.SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG;

public class UserPreferences {
	SharedPreferences preferences;

	public UserPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public boolean getPreferenceLikeUsDialogValue() {
		return preferences.getBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, false);
	}

	public int getPreferenceImageNumber() {
		return preferences.getInt(IMAGE_NUMBER_SHARED_PREFERENCES_TAG, 0);
	}

	public void setPreferenceImageNumber(int value) {
		preferences
			.edit()
			.putInt(IMAGE_NUMBER_SHARED_PREFERENCES_TAG, value)
			.apply();
	}

	public void setPreferenceLikeUsDialogValue() {
		preferences
			.edit()
			.putBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, true)
			.apply();
	}
}
