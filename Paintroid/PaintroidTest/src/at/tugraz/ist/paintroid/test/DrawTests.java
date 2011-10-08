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
import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.utilities.Brush;

import com.jayway.android.robotium.solo.Solo;

public class DrawTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;

	private int screenWidth;
	private int screenHeight;
	private Point screenCenter;

	private TextView parameterButton1;
	private TextView parameterButton2;
	private TextView toolButton;

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
		drawingSurface.antialiasingFlag = false;

		parameterButton1 = (TextView) mainActivity.findViewById(R.id.btn_Parameter1);
		parameterButton2 = (TextView) mainActivity.findViewById(R.id.btn_Parameter2);
		toolButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);

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

		Utils.selectTool(solo, toolButton, R.string.button_magic);
		solo.clickOnScreen(screenCenter.x, screenCenter.y);

		bitmap = Utils.bitmapToPixelArray(drawingSurface.getBitmap());
		assertEquals(bitmap.length, solidColor.length);
		Arrays.fill(solidColor, Color.BLACK);

		for (int i = 0; i < bitmap.length; i++) {
			if (bitmap[i] != solidColor[i])
				assertFalse(true);
		}

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		Utils.selectColorFromPicker(solo, new int[] { 0, 0, 0, 0 }, parameterButton1);
		final int targetX = screenCenter.x + screenWidth / 4;
		solo.drag(screenCenter.x, targetX, screenCenter.y, screenCenter.y, 0);
		solo.sleep(400);

		PointF lastClickedCoordinates = drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates();
		int pixel = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x, lastClickedCoordinates.y);
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

		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		PointF lastClickedCoordinates = drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates();
		int pixel = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x, lastClickedCoordinates.y);
		assertEquals(Color.BLACK, pixel);

		final int targetX = screenCenter.x + screenWidth / 4;
		final int middleX = (screenCenter.x + targetX) / 2;
		pixel = drawingSurface.getPixelFromScreenCoordinates(middleX, screenCenter.y);
		assertEquals(Color.TRANSPARENT, pixel);

		solo.drag(screenCenter.x, targetX, screenCenter.y, screenCenter.y, 0);
		solo.sleep(400);
		lastClickedCoordinates = drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates();
		pixel = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x, lastClickedCoordinates.y);
		assertEquals(Color.BLACK, pixel);

		Utils.selectColorFromPicker(solo, red, parameterButton1);

		solo.drag(screenCenter.x, targetX, screenCenter.y, screenCenter.y, 0);
		solo.sleep(400);

		lastClickedCoordinates = drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates();
		pixel = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x, lastClickedCoordinates.y);
		assertEquals(Color.RED, pixel);
	}

	@Smoke
	public void testBrushSizes() throws Exception {
		Cap[] capsToTest = new Cap[] { Cap.SQUARE, Cap.ROUND };
		for (Cap capToTest : capsToTest) {
			solo.clickOnView(parameterButton2);
			if (capToTest == Cap.SQUARE) {
				solo.clickOnImageButton(STROKERECT);
			} else if (capToTest == Cap.ROUND) {
				solo.clickOnImageButton(STROKECIRLCE);
			}
			solo.waitForDialogToClose(400);
			solo.clickOnView(parameterButton2);
			solo.clickOnImageButton(STROKE1);
			solo.waitForDialogToClose(400);
			assertTrue(testBrushSize(Brush.stroke1, capToTest));

			drawingSurface.getBitmap().eraseColor(Color.TRANSPARENT);

			solo.clickOnView(parameterButton2);
			solo.clickOnImageButton(STROKE2);
			solo.waitForDialogToClose(400);
			assertTrue(testBrushSize(Brush.stroke5, capToTest));

			drawingSurface.getBitmap().eraseColor(Color.TRANSPARENT);

			solo.clickOnView(parameterButton2);
			solo.clickOnImageButton(STROKE3);
			solo.waitForDialogToClose(400);
			assertTrue(testBrushSize(Brush.stroke15, capToTest));

			drawingSurface.getBitmap().eraseColor(Color.TRANSPARENT);

			solo.clickOnView(parameterButton2);
			solo.clickOnImageButton(STROKE4);
			solo.waitForDialogToClose(400);
			assertTrue(testBrushSize(Brush.stroke25, capToTest));

			drawingSurface.getBitmap().eraseColor(Color.TRANSPARENT);
		}
	}

	private boolean testBrushSize(int stroke, Cap cap) {
		assertEquals(cap, drawingSurface.getActiveBrush().cap);
		assertEquals(stroke, drawingSurface.getActiveBrush().stroke);
		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		PointF lastClickedCoordinates = drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates();
		int brushSize = drawingSurface.getActiveBrush().stroke;
		int borderValue = (int) Math.ceil(brushSize / 2.0);
		for (int x = -borderValue + 1; x < borderValue; x++) {
			for (int y = -borderValue + 1; y < borderValue; y++) {
				if (cap != Cap.ROUND
						|| (Math.pow((x + 0.5), 2) + Math.pow((y + 0.5), 2) < Math
								.pow(Math.floor(brushSize / 2 - 0), 2))) {
					int pixel = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x + x,
							lastClickedCoordinates.y + y);
					if (Color.BLACK != pixel) {
						Log.e("testBrushSize", "Stroke: " + stroke + " Cap: " + cap.name() + " Coordinate x=" + x
								+ " y=" + y + " has wrong color");
						return false;
					}
				}
			}
		}
		int pixelBorder1 = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x - borderValue,
				lastClickedCoordinates.y - borderValue);
		int pixelBorder2 = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x + borderValue,
				lastClickedCoordinates.y - borderValue);
		int pixelBorder3 = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x - borderValue,
				lastClickedCoordinates.y + borderValue);
		int pixelBorder4 = drawingSurface.getPixelFromScreenCoordinates(lastClickedCoordinates.x + borderValue,
				lastClickedCoordinates.y + borderValue);
		if (Color.TRANSPARENT != pixelBorder1 || Color.TRANSPARENT != pixelBorder2 || Color.TRANSPARENT != pixelBorder3
				|| Color.TRANSPARENT != pixelBorder4) {
			Log.e("testBrushSize", "Stroke: " + stroke + " Cap: " + cap.name() + " has wrong color on border");
			return false;
		}
		return true;
	}

	//	public void testMagicWand() throws Exception {

	//	}

	//	public void testEyeDropper() throws Exception {

	//	}

	public void testDrawingOutsideBitmap() {
		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
		Utils.selectTool(solo, toolButton, R.string.button_choose);
		solo.drag(0, 0, (screenHeight - 200), 100, 10);
		solo.drag(0, 0, (screenHeight - 200), 100, 10);
		Utils.selectTool(solo, toolButton, R.string.button_brush);
		solo.clickOnView(toolButton);
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		Utils.selectTool(solo, toolButton, R.string.button_magic);
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
