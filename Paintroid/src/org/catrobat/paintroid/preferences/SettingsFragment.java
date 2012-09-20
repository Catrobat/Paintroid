package org.catrobat.paintroid.preferences;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@TargetApi(11)
public class SettingsFragment extends PreferenceFragment {
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String settings = getArguments().getString(
				PaintroidApplication.APPLICATION_CONTEXT
						.getString(R.string.preferences));
		if (settings.equals(PaintroidApplication.APPLICATION_CONTEXT
				.getString(R.string.preferences_tools))) {
			addPreferencesFromResource(R.xml.preferences_tools);
		}
	}
}
