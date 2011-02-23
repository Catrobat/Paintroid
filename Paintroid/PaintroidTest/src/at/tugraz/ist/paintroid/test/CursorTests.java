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

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.utilities.Cursor.CursorState;

import com.jayway.android.robotium.solo.Solo;

public class CursorTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private int screenWidth;
	private int screenHeight;

	// Buttonindexes
	final int COLORPICKER = 0;
	final int STROKE = 0;
	final int HAND = 1;
	final int MAGNIFIY = 2;
	final int BRUSH = 3;
	final int EYEDROPPER = 4;
	final int WAND = 5;
	final int UNDO = 6;
	final int REDO = 7;
	final int FILE = 8;
	
	final int STROKERECT = 0;
	final int STROKECIRLCE = 1;
	final int STROKE1 = 2;
	final int STROKE2 = 3;
	final int STROKE3 = 4;
	final int STROKE4 = 5;
	
	final int CursorStateINACTIVE = 0;
	final int CursorStateACTIVE = 1;
	final int CursorStateDRAW = 2;

	public CursorTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		String languageToLoad_before  = "en";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);
		
		Configuration config_before = new Configuration();
		config_before.locale = locale_before;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());
		
		screenWidth = solo.getCurrentActivity().getWindowManager()
		.getDefaultDisplay().getWidth();
		screenHeight = solo.getCurrentActivity().getWindowManager()
		.getDefaultDisplay().getHeight();
	}

	public void testCursorStates() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		
		solo.clickOnImageButton(BRUSH);
		
		assertEquals(CursorState.INACTIVE, mainActivity.getCursorState());

		solo.clickOnScreen(screenWidth/2, screenHeight/2-200);
		solo.clickOnScreen(screenWidth/2, screenHeight/2-200);
		
		assertEquals(CursorState.ACTIVE, mainActivity.getCursorState());
		
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		
		assertEquals(CursorState.DRAW, mainActivity.getCursorState());
		
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		
		assertEquals(CursorState.ACTIVE, mainActivity.getCursorState());
		
		solo.clickOnScreen(screenWidth/2, screenHeight/2-200);
		solo.clickOnScreen(screenWidth/2, screenHeight/2-200);
		
		assertEquals(CursorState.INACTIVE, mainActivity.getCursorState());
		
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		
		assertEquals(CursorState.ACTIVE, mainActivity.getCursorState());
		
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		
		assertEquals(CursorState.DRAW, mainActivity.getCursorState());
		
		solo.clickOnScreen(screenWidth/2, screenHeight/2-200);
		solo.clickOnScreen(screenWidth/2, screenHeight/2-200);
		
		assertEquals(CursorState.INACTIVE, mainActivity.getCursorState());
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

}
