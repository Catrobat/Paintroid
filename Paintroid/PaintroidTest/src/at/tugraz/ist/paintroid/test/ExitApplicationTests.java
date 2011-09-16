/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test;

import java.util.Locale;

import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class ExitApplicationTests extends ActivityInstrumentationTestCase2<MainActivity> {
	static final String TAG = "PAINTROIDTEST";

	private Solo solo;
	private MainActivity mainActivity;
	private int[] toolbarButtonId;
	private int[] toolbarButtonNormalId;
	private int[] toolbarButtonActiveId;

	public ExitApplicationTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		Locale defaultLocale = new Locale("en");
		Locale.setDefault(defaultLocale);
		Configuration config_before = new Configuration();
		config_before.locale = defaultLocale;
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		toolbarButtonId = new int[] { R.id.ibtn_handTool, R.id.ibtn_zoomTool, R.id.ibtn_brushTool,
				R.id.ibtn_eyeDropperTool, R.id.ibtn_magicWandTool, R.id.ibtn_undoTool, R.id.ibtn_redoTool,
				R.id.ibtn_fileActivity };
		toolbarButtonNormalId = new int[] { R.drawable.ic_hand, R.drawable.ic_zoom, R.drawable.ic_brush,
				R.drawable.ic_eyedropper, R.drawable.ic_magicwand, R.drawable.ic_undo, R.drawable.ic_redo,
				R.drawable.ic_filemanager };
		toolbarButtonActiveId = new int[] { R.drawable.ic_hand_active, R.drawable.ic_zoom_active,
				R.drawable.ic_brush_active, R.drawable.ic_eyedropper_active, R.drawable.ic_magicwand_active };
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	/**
	 * 
	 */
	@Smoke
	public void testSecurityQuestionOnBackButton() throws Exception {
		assertTrue(solo.waitForActivity("MainActivity", 1000));
		assertTrue(Utils.viewIsVisible(solo, DrawingSurface.class));
		solo.goBack();
		solo.clickOnButton(1);
		assertTrue(Utils.viewIsVisible(solo, DrawingSurface.class));
		solo.assertCurrentActivity("MainActivity not visible!", MainActivity.class);
		solo.goBack();
		solo.clickOnButton(0);
		assertFalse(Utils.viewIsVisible(solo, DrawingSurface.class));
	}
}
