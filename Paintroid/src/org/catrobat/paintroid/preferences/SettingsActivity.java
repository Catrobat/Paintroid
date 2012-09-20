package org.catrobat.paintroid.preferences;

import java.util.List;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String action = getIntent().getAction();
		if (action != null
				&& action.equals(PaintroidApplication.APPLICATION_CONTEXT
						.getString(R.string.preferences_tools))) {
			addPreferencesFromResource(R.xml.preferences_tools);
		} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.preferences_legacy);
		}
	}

	@TargetApi(11)
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preferences_headers, target);
	}
}
