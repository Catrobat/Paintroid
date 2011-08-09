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
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class DrawTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private String hsvTab;

	private ImageButton colorButton;
	private ImageButton strokeButton;
	private ImageButton handButton;
	private ImageButton brushButton;
	private ImageButton eyeButton;
	private ImageButton wandButton;
	private ImageButton fileButton;

	final static int STROKERECT = 0;
	final static int STROKECIRLCE = 1;
	final static int STROKE1 = 2;
	final static int STROKE2 = 3;
	final static int STROKE3 = 4;
	final static int STROKE4 = 5;

	static final int[] red = new int[] { 255, 255, 0, 0 };
	static final int[] green = new int[] { 255, 0, 255, 0 };
	static final int[] blue = new int[] { 255, 0, 0, 255 };
	static final int[] transparent = new int[] { 0, 0, 0, 0 };

	public DrawTests() {
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
		drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);
		hsvTab = mainActivity.getResources().getString(R.string.color_hsv);

		colorButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_Color);
		strokeButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_brushStroke);
		handButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_handTool);
		brushButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_brushTool);
		eyeButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_eyeDropperTool);
		wandButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_magicWandTool);
		fileButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_fileActivity);
	}

	//	@Smoke
	//	public void testDrawTRANSPARENTOnCanvas() throws Exception {
	//		solo.clickOnView(fileButton);
	//		solo.clickOnButton("New Drawing");
	//		assertTrue(solo.waitForActivity("MainActivity", 500));
	//
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		drawingSurface.setAntiAliasing(false);
	//
	//		selectTransparentColorFromPicker();
	//
	//		solo.clickOnView(brushButton);
	//
	//		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
	//		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
	//
	//		int halfScreenWidth = (int) (screenWidth * 0.5);
	//		int halfScreenHeight = (int) (screenHeight * 0.5);
	//
	//		solo.clickOnScreen(halfScreenWidth, halfScreenHeight);
	//
	//		Bitmap currentImage = drawingSurface.getBitmap();
	//
	//		assertNotNull(currentImage);
	//
	//		float[] coordinatesOfLastClick = new float[2];
	//		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
	//		solo.sleep(500);
	//		int pixel = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
	//
	//		// 2.1 returns not completely transparent pixels here
	//		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ECLAIR_MR1) {
	//			assertEquals(1, Color.alpha(pixel));
	//		} else {
	//			assertEquals(Color.TRANSPARENT, pixel);
	//		}
	//
	//		solo.clickOnView(fileButton);
	//		solo.clickOnButton("Save");
	//		solo.enterText(0, "test_drawTransparent");
	//		solo.clickOnButton("Done");
	//
	//		File file = new File(Environment.getExternalStorageDirectory().toString()
	//				+ "/Paintroid/test_drawTransparent.png");
	//
	//		if (file.exists()) {
	//			solo.clickOnButton("Yes");
	//			Log.d("PaintroidTest", "File has been overwriten");
	//		}
	//
	//		assertTrue(solo.waitForActivity("MainActivity", 500));
	//
	//		file.delete();
	//	}

	/**
	 * Test if Brush function works
	 * 
	 */
	//	public void testBrush() throws Exception {
	//		solo.clickOnView(fileButton);
	//		solo.clickOnButton("New Drawing");
	//		assertTrue(solo.waitForActivity("MainActivity", 500));
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		drawingSurface.setAntiAliasing(false);
	//		solo.clickOnView(brushButton);
	//
	//		Utils.selectColorFromPicker(solo, green);
	//
	//		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
	//
	//		int[] locationstrokePickerButton = new int[2];
	//		strokeButton.getLocationOnScreen(locationstrokePickerButton);
	//		locationstrokePickerButton[0] += strokeButton.getMeasuredWidth();
	//		int[] locationHandButton = new int[2];
	//		handButton.getLocationOnScreen(locationHandButton);
	//		locationHandButton[1] -= handButton.getMeasuredHeight();
	//
	//		ArrayList<View> actual_views = solo.getViews();
	//		View surfaceView = null;
	//		for (View view : actual_views) {
	//			if (view instanceof DrawingSurface) {
	//				surfaceView = view;
	//			}
	//		}
	//		assertNotNull(surfaceView);
	//		int[] coords = new int[2];
	//		surfaceView.getLocationOnScreen(coords);
	//
	//		float min_x = locationstrokePickerButton[0];
	//		float min_y = coords[1];
	//		float max_x = screenWidth;
	//		float max_y = locationHandButton[1];
	//		// Get coordinates of begin of drag
	//		solo.clickOnScreen(min_x, min_y);
	//		float[] coordinatesOfFirstClick = new float[2];
	//		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfFirstClick);
	//		solo.sleep(500);
	//		solo.drag(min_x, max_x, min_y, max_y, 50);
	//		float[] coordinatesOfLastClick = new float[2];
	//		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
	//
	//		//Change coordinates to real clicked ones
	//		min_x = coordinatesOfFirstClick[0];
	//		max_x = coordinatesOfLastClick[0];
	//		min_y = coordinatesOfFirstClick[1];
	//		max_y = coordinatesOfLastClick[1];
	//
	//		float ratioYX = (max_y - min_y) / (max_x - min_x);
	//
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//
	//		int testPixel1 = drawingSurface.getPixelFromScreenCoordinates(min_x + 20, min_y + Math.round(20 * ratioYX));
	//		int testPixel2 = drawingSurface.getPixelFromScreenCoordinates(max_x - 20, max_y - Math.round(20 * ratioYX));
	//		int testPixel3 = drawingSurface.getPixelFromScreenCoordinates(min_x + (max_x - min_x) / 2,
	//				min_y + Math.round((max_x - min_x) / 2 * ratioYX));
	//		int testPixel4 = drawingSurface.getPixelFromScreenCoordinates(min_x + 20, min_y + max_y / 2);
	//		int testPixel5 = drawingSurface.getPixelFromScreenCoordinates(min_x + max_x / 2,
	//				min_y + Math.round(20 * ratioYX));
	//
	//		assertEquals(testPixel1, Color.GREEN);
	//		assertEquals(testPixel2, Color.GREEN);
	//		assertEquals(testPixel3, Color.GREEN);
	//		assertTrue(testPixel4 != Color.GREEN);
	//		assertTrue(testPixel5 != Color.GREEN);
	//
	//		Utils.selectColorFromPicker(solo, red);
	//
	//		solo.clickOnScreen(35, 400);
	//		solo.sleep(500);
	//		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
	//		int pixel = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
	//		assertEquals(Color.RED, pixel);
	//
	//	}

	/**
	 * Test different brush sizes
	 * 
	 */
	//	public void testBrushSizes() throws Exception {
	//		solo.clickOnView(fileButton);
	//		solo.clickOnButton("New Drawing");
	//		assertTrue(solo.waitForActivity("MainActivity", 500));
	//		solo.clickOnView(brushButton);
	//
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		drawingSurface.setAntiAliasing(false);
	//
	//		Utils.selectColorFromPicker(solo, red);
	//
	//		Vector<int[]> strokesToTest = new Vector<int[]>();
	//		int[] stroke = new int[2];
	//		stroke[0] = STROKE1;
	//		stroke[1] = 1;
	//		strokesToTest.add(stroke);
	//		stroke = new int[2];
	//		stroke[0] = STROKE2;
	//		stroke[1] = 5;
	//		strokesToTest.add(stroke);
	//		stroke = new int[2];
	//		stroke[0] = STROKE3;
	//		stroke[1] = 15;
	//		strokesToTest.add(stroke);
	//		stroke = new int[2];
	//		stroke[0] = STROKE4;
	//		stroke[1] = 25;
	//		strokesToTest.add(stroke);
	//
	//		int coordinatesIncrement = 100;
	//		int yDrawCoordinates = 100;
	//
	//		for (int[] strokeWidthType : strokesToTest) {
	//			solo.clickOnView(strokeButton);
	//			solo.clickOnImageButton(STROKERECT);
	//			solo.clickOnView(strokeButton);
	//			solo.clickOnImageButton(strokeWidthType[0]);
	//			solo.waitForDialogToClose(200);
	//			solo.clickOnScreen(100, yDrawCoordinates);
	//			float[] coordinatesOfLastClick = new float[2];
	//			drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
	//			Brush brush = drawingSurface.getActiveBrush();
	//			assertEquals(strokeWidthType[1], brush.stroke);
	//			assertEquals(Cap.SQUARE, brush.cap);
	//			solo.sleep(500);
	//			int halfBrushWidth = (brush.stroke - 1) / 2;
	//			Point pixelCoordinates = drawingSurface.getPixelCoordinates(coordinatesOfLastClick[0],
	//					coordinatesOfLastClick[1]);
	//			for (int count_x = -halfBrushWidth - 5; count_x <= halfBrushWidth + 5; count_x++) {
	//				for (int count_y = -halfBrushWidth - 5; count_y <= halfBrushWidth + 5; count_y++) {
	//					if (count_x >= -halfBrushWidth && count_x <= halfBrushWidth && count_y >= -halfBrushWidth
	//							&& count_y <= halfBrushWidth) {
	//						assertEquals(
	//								Color.RED,
	//								drawingSurface.getBitmap().getPixel(pixelCoordinates.x + count_x,
	//										pixelCoordinates.y + count_y));
	//					} else {
	//						assertTrue(Color.RED != drawingSurface.getBitmap().getPixel(pixelCoordinates.x + count_x,
	//								pixelCoordinates.y + count_y));
	//					}
	//				}
	//			}
	//
	//			// Not drawn as circle if stroke width too small
	//			if (strokeWidthType[1] > 5) {
	//				solo.clickOnView(strokeButton);
	//				solo.clickOnImageButton(STROKECIRLCE);
	//				solo.waitForDialogToClose(200);
	//				solo.clickOnScreen(200, yDrawCoordinates);
	//				drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
	//				brush = drawingSurface.getActiveBrush();
	//				assertEquals(strokeWidthType[1], brush.stroke);
	//				assertEquals(Cap.ROUND, brush.cap);
	//				solo.sleep(500);
	//				halfBrushWidth = (brush.stroke - 1) / 2;
	//				pixelCoordinates = drawingSurface.getPixelCoordinates(coordinatesOfLastClick[0],
	//						coordinatesOfLastClick[1]);
	//
	//				// midllepoint
	//				assertEquals(Color.RED, drawingSurface.getBitmap().getPixel(pixelCoordinates.x, pixelCoordinates.y));
	//				// top, bottom, left and right
	//				assertEquals(Color.RED,
	//						drawingSurface.getBitmap().getPixel(pixelCoordinates.x, pixelCoordinates.y - halfBrushWidth));
	//				assertEquals(Color.RED,
	//						drawingSurface.getBitmap()
	//								.getPixel(pixelCoordinates.x, pixelCoordinates.y + halfBrushWidth - 1));
	//				assertEquals(Color.RED,
	//						drawingSurface.getBitmap().getPixel(pixelCoordinates.x - halfBrushWidth, pixelCoordinates.y));
	//				assertEquals(Color.RED,
	//						drawingSurface.getBitmap()
	//								.getPixel(pixelCoordinates.x + halfBrushWidth - 1, pixelCoordinates.y));
	//				// Edges
	//				assertTrue(Color.RED != drawingSurface.getBitmap().getPixel(pixelCoordinates.x - halfBrushWidth,
	//						pixelCoordinates.y - halfBrushWidth));
	//				assertTrue(Color.RED != drawingSurface.getBitmap().getPixel(pixelCoordinates.x + halfBrushWidth - 1,
	//						pixelCoordinates.y - halfBrushWidth));
	//				assertTrue(Color.RED != drawingSurface.getBitmap().getPixel(pixelCoordinates.x - halfBrushWidth,
	//						pixelCoordinates.y + halfBrushWidth - 1));
	//				assertTrue(Color.RED != drawingSurface.getBitmap().getPixel(pixelCoordinates.x + halfBrushWidth - 1,
	//						pixelCoordinates.y + halfBrushWidth - 1));
	//			}
	//			yDrawCoordinates += coordinatesIncrement;
	//		}
	//
	//	}

	/**
	 * Test if MagicWand function works
	 * 
	 */
	//	public void testMagicWand() throws Exception {
	//		solo.clickOnView(fileButton);
	//		solo.clickOnButton("New Drawing");
	//		assertTrue(solo.waitForActivity("MainActivity", 500));
	//
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		drawingSurface.setAntiAliasing(false);
	//
	//		Utils.selectColorFromPicker(solo, blue);
	//
	//		solo.clickOnView(wandButton);
	//
	//		solo.clickOnScreen(35, 400);
	//
	//		int testPixel1 = drawingSurface.getPixelFromScreenCoordinates(35, 350);
	//		int testPixel2 = drawingSurface.getPixelFromScreenCoordinates(25, 255);
	//		int testPixel3 = drawingSurface.getPixelFromScreenCoordinates(40, 360);
	//
	//		assertEquals(testPixel1, Color.BLUE);
	//		assertEquals(testPixel2, Color.BLUE);
	//		assertEquals(testPixel3, Color.BLUE);
	//
	//		Utils.selectColorFromPicker(solo, red);
	//
	//		solo.clickOnView(brushButton);
	//		solo.clickOnScreen(35, 400);
	//		float[] coordinatesOfLastClick = new float[2];
	//		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
	//		solo.sleep(500);
	//		int testPixelRed = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0],
	//				coordinatesOfLastClick[1]);
	//		assertEquals(Color.RED, testPixelRed);
	//
	//		Utils.selectColorFromPicker(solo, red);
	//		solo.clickOnView(wandButton);
	//
	//		solo.clickOnScreen(200, 200);
	//
	//		testPixel1 = drawingSurface.getPixelFromScreenCoordinates(200, 200);
	//		testPixel2 = drawingSurface.getPixelFromScreenCoordinates(150, 200);
	//		testPixel3 = drawingSurface.getPixelFromScreenCoordinates(10, 10);
	//
	//		assertEquals(testPixel1, Color.RED);
	//		assertEquals(testPixel2, Color.RED);
	//		assertEquals(testPixel3, Color.RED);
	//		assertEquals(testPixelRed, Color.RED);
	//
	//	}

	/**
	 * Test if EyeDropper function works
	 * 
	 */
	//	public void testEyeDropper() throws Exception {
	//		solo.clickOnView(fileButton);
	//		solo.clickOnButton("New Drawing");
	//		assertTrue(solo.waitForActivity("MainActivity", 500));
	//		solo.clickOnView(eyeButton);
	//
	//		mainActivity = (MainActivity) solo.getCurrentActivity();
	//		drawingSurface.setAntiAliasing(false);
	//
	//		solo.clickOnScreen(35, 400);
	//
	//		float[] coordinatesOfClick = new float[2];
	//		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfClick);
	//
	//		int testPixel = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfClick[0], coordinatesOfClick[1]);
	//
	//		assertEquals(drawingSurface.getActiveColor(), testPixel);
	//	}

	/**
	 * Test if program crashes when any drawing tool is used outside of the bitmap
	 */
	public void testDrawingOutsideBitmap() {
		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		solo.clickOnView(handButton);
		solo.drag(0, 0, (screenHeight - 200), 100, 10);
		solo.drag(0, 0, (screenHeight - 200), 100, 10);
		solo.clickOnView(brushButton);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.clickOnView(wandButton);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		assertEquals(mainActivity, solo.getCurrentActivity());

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
