/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.preferences;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

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

			if (PaintroidApplication.IS_OPENED_FROM_CATROID == false) {
				PreferenceScreen toolsPreferences = getPreferenceScreen();
				PreferenceManager manager = getPreferenceManager();
				toolsPreferences.removePreference(manager
						.findPreference(getActivity().getString(
								R.string.button_redo)));
				toolsPreferences.removePreference(manager
						.findPreference(getActivity().getString(
								R.string.button_undo)));
			}
		}
	}
}
