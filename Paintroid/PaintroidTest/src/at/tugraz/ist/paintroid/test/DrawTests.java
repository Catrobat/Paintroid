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

import java.util.Arrays;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.utilities.Brush;

import com.jayway.android.robotium.solo.Solo;

public class DrawTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private String hsvTab;

	private int screenWidth;
	private int screenHeight;
	private Point screenCenter;

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

		screenWidth = drawingSurface.getWidth();
		screenHeight = drawingSurface.getHeight();
		screenCenter = new Point(screenWidth / 2, screenHeight / 2);
	}

	@Smoke
	public void testDrawTransparentOnCanvas() throws Exception {
		solo.waitForView(DrawingSurface.class, 1, 1000);

		int[] bitmap = Utils.bitmapToPixelArray(drawingSurface.getBitmap());
		int[] solidColor = new int[bitmap.length];
		assertEquals(bitmap.length, solidColor.length);
		Arrays.fill(solidColor, Color.TRANSPARENT);

		for (int i = 0; i < bitmap.length; i++) {
			if (bitmap[i] != solidColor[i])
				assertFalse(true);
		}

		solo.clickOnView(wandButton);
		solo.clickOnScreen(screenCenter.x, screenCenter.y);

		bitmap = Utils.bitmapToPixelArray(drawingSurface.getBitmap());
		assertEquals(bitmap.length, solidColor.length);
		Arrays.fill(solidColor, Color.BLACK);

		for (int i = 0; i < bitmap.length; i++) {
			if (bitmap[i] != solidColor[i])
				assertFalse(true);
		}

		solo.clickOnView(brushButton);
		Utils.selectColorFromPicker(solo, new int[] { 0, 0, 0, 0 });
		final int targetX = screenCenter.x + screenWidth / 4;
		final int middleX = (screenCenter.x + targetX) / 2;
		solo.drag(screenCenter.x, targetX, screenCenter.y, screenCenter.y, 0);
		solo.sleep(400);

		int pixel = drawingSurface.getBitmap().getPixel(middleX, screenCenter.y);
		assertEquals(Color.TRANSPARENT, pixel);
	}

	@Smoke
	public void testBrush() throws Exception {
		int[] bitmap = Utils.bitmapToPixelArray(drawingSurface.getBitmap());
		int[] solidColor = new int[bitmap.length];
		assertEquals(bitmap.length, solidColor.length);
		Arrays.fill(solidColor, Color.TRANSPARENT);

		for (int i = 0; i < bitmap.length; i++) {
			if (bitmap[i] != solidColor[i])
				assertFalse(true);
		}

		//		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		int pixel = drawingSurface.getBitmap().getPixel(screenCenter.x, screenCenter.y);
		//		solo.sleep(400);
		//		assertEquals(Color.BLACK, pixel);

		final int targetX = screenCenter.x + screenWidth / 4;
		final int middleX = (screenCenter.x + targetX) / 2;
		pixel = drawingSurface.getBitmap().getPixel(middleX, screenCenter.y);
		assertEquals(Color.TRANSPARENT, pixel);

		solo.drag(screenCenter.x, targetX, screenCenter.y, screenCenter.y, 0);
		solo.sleep(400);

		pixel = drawingSurface.getBitmap().getPixel(middleX, screenCenter.y);
		assertEquals(Color.BLACK, pixel);

		Utils.selectColorFromPicker(solo, red);

		solo.drag(screenCenter.x, targetX, screenCenter.y, screenCenter.y, 0);
		solo.sleep(400);

		pixel = drawingSurface.getBitmap().getPixel(middleX, screenCenter.y);
		assertEquals(Color.RED, pixel);
	}

	@Smoke
	public void testBrushSizes() throws Exception {
		solo.clickOnView(strokeButton);
		solo.clickOnImageButton(STROKERECT);
		solo.waitForDialogToClose(400);

		solo.clickOnView(strokeButton);
		solo.clickOnImageButton(STROKE1);
		solo.waitForDialogToClose(400);
		assertEquals(Cap.SQUARE, drawingSurface.getActiveBrush().cap);
		assertEquals(Brush.stroke1, drawingSurface.getActiveBrush().stroke);
		//		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		//		solo.sleep(400);
		//		int pixel = drawingSurface.getBitmap().getPixel(screenCenter.x, screenCenter.y);
		//		assertEquals(Color.BLACK, pixel);
		drawingSurface.newEmptyBitmap();

		solo.clickOnView(strokeButton);
		solo.clickOnImageButton(STROKE2);
		solo.waitForDialogToClose(400);
		assertEquals(Cap.SQUARE, drawingSurface.getActiveBrush().cap);
		assertEquals(Brush.stroke5, drawingSurface.getActiveBrush().stroke);
		//		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		//		solo.sleep(400);
		//		pixel = drawingSurface.getBitmap().getPixel(screenCenter.x, screenCenter.y);
		//		assertEquals(Color.BLACK, pixel);
		drawingSurface.newEmptyBitmap();

		solo.clickOnView(strokeButton);
		solo.clickOnImageButton(STROKE3);
		solo.waitForDialogToClose(400);
		assertEquals(Cap.SQUARE, drawingSurface.getActiveBrush().cap);
		assertEquals(Brush.stroke15, drawingSurface.getActiveBrush().stroke);
		//		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		//		solo.sleep(400);
		//		pixel = drawingSurface.getBitmap().getPixel(screenCenter.x, screenCenter.y);
		//		assertEquals(Color.BLACK, pixel);
		drawingSurface.newEmptyBitmap();

		solo.clickOnView(strokeButton);
		solo.clickOnImageButton(STROKE4);
		solo.waitForDialogToClose(400);
		assertEquals(Cap.SQUARE, drawingSurface.getActiveBrush().cap);
		assertEquals(Brush.stroke25, drawingSurface.getActiveBrush().stroke);
		//		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		//		solo.sleep(400);
		//		pixel = drawingSurface.getBitmap().getPixel(screenCenter.x, screenCenter.y);
		//		assertEquals(Color.BLACK, pixel);
		drawingSurface.newEmptyBitmap();
	}

	//	public void testMagicWand() throws Exception {

	//	}

	//	public void testEyeDropper() throws Exception {

	//	}

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
