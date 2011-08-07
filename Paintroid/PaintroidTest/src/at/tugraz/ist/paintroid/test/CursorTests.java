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

import java.util.ArrayList;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;

import com.jayway.android.robotium.solo.Solo;

public class CursorTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private ImageButton brushButton;
	private ImageButton handButton;
	private ImageButton strokeButton;

	private int screenWidth;
	private int screenHeight;

	public CursorTests() {
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

		brushButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_brushTool);
		handButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_handTool);
		strokeButton = (ImageButton) mainActivity.findViewById(R.id.ibtn_brushStroke);

		screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = mainActivity.getWindowManager().getDefaultDisplay().getHeight();
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

	public void testCursorStates() throws Exception {
		solo.clickOnView(brushButton);
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		solo.sleep(400);
		assertEquals(Mode.CURSOR, drawingSurface.getMode());
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		solo.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		solo.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		solo.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());

	}

	public void testCursorDraw() throws Exception {
		solo.clickOnView(brushButton);
		// double tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.drag(screenWidth / 2, screenWidth / 2 + 1, screenHeight / 2, screenHeight / 2, 50);
		solo.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		float[] coordinatesOfLastClick = new float[2];
		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		solo.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());

		int testPixel1 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0],
				coordinatesOfLastClick[1]);
		int testPixel2 = drawingSurface.getPixelFromScreenCoordinates(coordinatesOfLastClick[0] + 30,
				coordinatesOfLastClick[1]);

		assertEquals(testPixel1, drawingSurface.getActiveColor());
		assertEquals(testPixel2, Color.TRANSPARENT);
	}

	public void testCursorDrawPath() throws Exception {
		solo.clickOnView(brushButton);

		int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();

		int[] locationstrokePickerButton = new int[2];
		strokeButton.getLocationOnScreen(locationstrokePickerButton);
		locationstrokePickerButton[0] += strokeButton.getMeasuredWidth();
		int[] locationHandButton = new int[2];
		handButton.getLocationOnScreen(locationHandButton);
		locationHandButton[1] -= handButton.getMeasuredHeight();

		ArrayList<View> actual_views = solo.getViews();
		View surfaceView = null;
		for (View view : actual_views) {
			if (view instanceof DrawingSurface) {
				surfaceView = view;
			}
		}
		assertNotNull(surfaceView);
		int[] coords = new int[2];
		surfaceView.getLocationOnScreen(coords);

		float min_x = locationstrokePickerButton[0];
		float min_y = coords[1];
		float max_x = screenWidth;
		float max_y = locationHandButton[1];
		// double tap
		solo.clickOnScreen(min_x, min_y);
		float[] coordinatesOfFirstClick = new float[2];
		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfFirstClick);
		solo.drag(min_x, min_x + 1, min_y, min_y, 50);
		solo.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);
		solo.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		solo.sleep(400);
		solo.drag(min_x, max_x, min_y, max_y, 50);
		float[] coordinatesOfLastClick = new float[2];
		drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);

		//Change coordinates to real clicked ones
		min_x = coordinatesOfFirstClick[0];
		max_x = coordinatesOfLastClick[0];
		min_y = coordinatesOfFirstClick[1];
		max_y = coordinatesOfLastClick[1];

		float ratioYX = (max_y - min_y) / (max_x - min_x);

		int testPixel1 = drawingSurface.getPixelFromScreenCoordinates(min_x + 20, min_y + Math.round(20 * ratioYX));
		int testPixel2 = drawingSurface.getPixelFromScreenCoordinates(max_x - 20, max_y - Math.round(20 * ratioYX));
		int testPixel3 = drawingSurface.getPixelFromScreenCoordinates(min_x + (max_x - min_x) / 2,
				min_y + Math.round((max_x - min_x) / 2 * ratioYX));
		int testPixel4 = drawingSurface.getPixelFromScreenCoordinates(min_x + 20, min_y + max_y / 2);
		int testPixel5 = drawingSurface.getPixelFromScreenCoordinates(min_x + max_x / 2,
				min_y + Math.round(20 * ratioYX));

		assertEquals(testPixel1, Color.BLACK);
		assertEquals(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);
		assertEquals(testPixel4, Color.TRANSPARENT);
		assertEquals(testPixel5, Color.TRANSPARENT);

	}
}