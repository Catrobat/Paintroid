/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.integration;

import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.deprecated.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class FileManagerTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MainActivity mainActivity;

	private DrawingSurface drawingSurface;
	private TextView toolbarMainButton;

	public FileManagerTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		Utils.setLocale(solo, Locale.ENGLISH);

		toolbarMainButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		//		drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);
	}

	private void openFileManager() {
		solo.clickOnView(toolbarMainButton);
		assertTrue(solo.waitForActivity("MenuTabActivity", 1000));
		solo.clickOnText("File"); // TODO: should be in resources
		assertTrue(solo.waitForActivity("FileActivity", 1000));
	}

	@Smoke
	public void testControlsAreVisible() throws Exception {
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));

		openFileManager();
		assertTrue(solo.waitForView(Button.class, 4, 1000));
		solo.clickOnButton(0); // new drawing
		assertTrue(solo.waitForView(Button.class, 3, 1000));
		solo.clickOnButton(2); // cancel
		assertTrue(solo.waitForView(Button.class, 4, 1000));
		solo.clickOnButton(3); // cancel
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));
	}

	@Smoke
	public void testNewDrawingButton() throws Exception {
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));

		Bitmap bitmap0 = drawingSurface.getBitmap().copy(Config.ARGB_8888, false);
		int[] array0 = Utils.bitmapToPixelArray(bitmap0);
		assertFalse(Utils.containsValue(array0, Color.BLACK));

		Utils.clickOnScreen(solo, 50, 50);
		solo.sleep(500);
		Bitmap bitmap1 = drawingSurface.getBitmap().copy(Config.ARGB_8888, false);
		int[] array1 = Utils.bitmapToPixelArray(bitmap1);
		assertTrue(Utils.containsValue(array1, Color.BLACK));

		openFileManager();
		assertTrue(solo.waitForView(Button.class, 4, 1000));
		solo.clickOnButton(0); // new drawing
		assertTrue(solo.waitForView(Button.class, 3, 1000));
		solo.clickOnButton(2); // cancel
		assertTrue(solo.waitForView(Button.class, 4, 1000));
		solo.clickOnButton(3); // cancel
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));

		Bitmap bitmap2 = drawingSurface.getBitmap().copy(Config.ARGB_8888, false);
		int[] array2 = Utils.bitmapToPixelArray(bitmap2);
		Utils.assertArrayEquals(array1, array2);

		openFileManager();
		assertTrue(solo.waitForView(Button.class, 4, 1000));
		solo.clickOnButton(0); // new drawing
		assertTrue(solo.waitForView(Button.class, 3, 1000));
		solo.clickOnButton(0); // empty drawing
		assertTrue(solo.waitForView(DrawingSurface.class, 1, 1000));

		Bitmap bitmap3 = drawingSurface.getBitmap().copy(Config.ARGB_8888, false);
		int[] array3 = Utils.bitmapToPixelArray(bitmap3);
		Utils.assertArrayEquals(array0, array3);

		bitmap0.recycle();
		bitmap1.recycle();
		bitmap2.recycle();
		bitmap3.recycle();
	}
}