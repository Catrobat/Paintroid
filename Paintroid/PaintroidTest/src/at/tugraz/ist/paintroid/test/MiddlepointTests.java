///*    Catroid: An on-device graphical programming language for Android devices
// *    Copyright (C) 2010  Catroid development team
// *    (<http://code.google.com/p/catroid/wiki/Credits>)
// *
// *    This program is free software: you can redistribute it and/or modify
// *    it under the terms of the GNU General Public License as published by
// *    the Free Software Foundation, either version 3 of the License, or
// *    (at your option) any later version.
// *
// *    This program is distributed in the hope that it will be useful,
// *    but WITHOUT ANY WARRANTY; without even the implied warranty of
// *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *    GNU General Public License for more details.
// *
// *    You should have received a copy of the GNU General Public License
// *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package at.tugraz.ist.paintroid.test;
//
//import java.io.File;
//import java.util.Locale;
//
//import android.content.res.Configuration;
//import android.graphics.Point;
//import android.os.Environment;
//import android.test.ActivityInstrumentationTestCase2;
//import at.tugraz.ist.paintroid.MainActivity;
//import at.tugraz.ist.paintroid.R;
//import at.tugraz.ist.paintroid.graphic.DrawingSurface;
//import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;
//
//import com.jayway.android.robotium.solo.Solo;
//
//public class MiddlepointTests extends ActivityInstrumentationTestCase2<MainActivity> {
//
//	private Solo solo;
//	private MainActivity mainActivity;
//	private DrawingSurface drawingSurface;
//	private int screenWidth;
//	private int screenHeight;
//
//	// Buttonindexes
//	final int COLORPICKER = 0;
//	final int STROKE = 0;
//	final int HAND = 1;
//	final int MAGNIFIY = 2;
//	final int BRUSH = 3;
//	final int EYEDROPPER = 4;
//	final int WAND = 5;
//	final int UNDO = 6;
//	final int REDO = 7;
//	final int FILE = 8;
//
//	final int STROKERECT = 0;
//	final int STROKECIRLCE = 1;
//	final int STROKE1 = 2;
//	final int STROKE2 = 3;
//	final int STROKE3 = 4;
//	final int STROKE4 = 5;
//
//	public MiddlepointTests() {
//		super("at.tugraz.ist.paintroid", MainActivity.class);
//	}
//
//	@Override
//	public void setUp() throws Exception {
//		solo = new Solo(getInstrumentation(), getActivity());
//		String languageToLoad_before = "en";
//		Locale locale_before = new Locale(languageToLoad_before);
//		Locale.setDefault(locale_before);
//
//		Configuration config_before = new Configuration();
//		config_before.locale = locale_before;
//
//		mainActivity = (MainActivity) solo.getCurrentActivity();
//		mainActivity.getBaseContext().getResources()
//				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());
//		drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);
//
//		screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
//		screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
//	}
//
//	/**
//	 * Check if the middle point mode is activated after pressing the menu button
//	 * and deactivated after pressing it again.
//	 * 
//	 */
//	public void testCenterPointModes() throws Exception {
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("New Drawing");
//
//		solo.clickOnMenuItem("Define Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.CENTERPOINT, drawingSurface.getMode());
//
//		solo.clickOnMenuItem("Save Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.DRAW, drawingSurface.getMode());
//	}
//
//	/**
//	 * Check if the middle point is set correctly in the drawing surface
//	 * 
//	 */
//	public void testCenterPoint() throws Exception {
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("New Drawing");
//
//		solo.clickOnMenuItem("Define Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.CENTERPOINT, drawingSurface.getMode());
//
//		solo.drag(200, 400, 100, 150, 10);
//
//		solo.clickOnMenuItem("Save Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.DRAW, drawingSurface.getMode());
//
//		Point centerpoint = new Point(0, 0);
//		centerpoint = drawingSurface.getCenter();
//
//		assertTrue(centerpoint.equals(screenWidth / 2 + 200, screenHeight / 2 + 50));
//	}
//
//	/**
//	 * Check if the middle point is saved and read correctly from the metadata
//	 * 
//	 */
//	public void testXmlMetafile() throws Exception {
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("New Drawing");
//
//		solo.clickOnMenuItem("Define Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.CENTERPOINT, drawingSurface.getMode());
//
//		solo.drag(200, 400, 100, 150, 10);
//
//		solo.clickOnMenuItem("Save Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.DRAW, drawingSurface.getMode());
//
//		Point centerpoint = drawingSurface.getCenter();
//
//		assertTrue(centerpoint.equals(screenWidth / 2 + 200, screenHeight / 2 + 50));
//
//		File file = new File(Environment.getExternalStorageDirectory().toString()
//				+ "/Paintroid/middlepint_test_save.png");
//
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("Save");
//		solo.enterText(0, "middlepint_test_save");
//		solo.clickOnButton("Done");
//
//		// Override
//		if (file.exists()) {
//			solo.clickOnButton("Yes");
//		}
//
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("New Drawing");
//
//		solo.sleep(1000);
//		centerpoint = drawingSurface.getCenter();
//
//		assertFalse(centerpoint.equals(screenWidth / 2 + 200, screenHeight / 2 + 50));
//
//		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
//				+ "/Paintroid/middlepint_test_save.png");
//
//		solo.sleep(1000);
//
//		centerpoint = drawingSurface.getCenter();
//
//		assertTrue(centerpoint.equals(screenWidth / 2 + 200, screenHeight / 2 + 50));
//
//		file.delete();
//	}
//
//	/**
//	 * Check if overriding an xml metadatafile works
//	 * 
//	 * @throws Exception
//	 */
//	public void testXmlMetafileOverride() throws Exception {
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("New Drawing");
//
//		solo.clickOnMenuItem("Define Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.CENTERPOINT, drawingSurface.getMode());
//
//		solo.drag(200, 400, 100, 150, 10);
//
//		solo.clickOnMenuItem("Save Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.DRAW, drawingSurface.getMode());
//
//		Point centerpoint = drawingSurface.getCenter();
//
//		assertTrue(centerpoint.equals(screenWidth / 2 + 200, screenHeight / 2 + 50));
//
//		File file = new File(Environment.getExternalStorageDirectory().toString()
//				+ "/Paintroid/centerpoint_test_save.png");
//
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("Save");
//		solo.enterText(0, "centerpoint_test_save");
//		solo.clickOnButton("Done");
//
//		// Override
//		if (file.exists()) {
//			solo.clickOnButton("Yes");
//		}
//
//		solo.clickOnMenuItem("Define Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.CENTERPOINT, drawingSurface.getMode());
//
//		solo.drag(300, 50, 100, 150, 10);
//
//		solo.clickOnMenuItem("Save Center Point");
//		solo.sleep(200);
//		assertEquals(Mode.DRAW, drawingSurface.getMode());
//
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("Save");
//		solo.enterText(0, "centerpoint_test_save");
//		solo.clickOnButton("Done");
//		solo.clickOnButton("Yes");
//
//		solo.clickOnImageButton(FILE);
//		solo.clickOnButton("New Drawing");
//
//		solo.sleep(1000);
//		centerpoint = drawingSurface.getCenter();
//
//		assertFalse(centerpoint.equals(screenWidth / 2 - 100, screenHeight / 2 + 50));
//
//		drawingSurface.addPng(Environment.getExternalStorageDirectory().toString()
//				+ "/Paintroid/centerpoint_test_save.png");
//
//		solo.sleep(1000);
//
//		centerpoint = drawingSurface.getCenter();
//
//		assertTrue(centerpoint.equals(screenWidth / 2 - 50, screenHeight / 2 + 100));
//
//		file.delete();
//	}
//
//	@Override
//	public void tearDown() throws Exception {
//		try {
//			solo.finalize();
//		} catch (Throwable e) {
//
//			e.printStackTrace();
//		}
//		getActivity().finish();
//		super.tearDown();
//	}
//}
