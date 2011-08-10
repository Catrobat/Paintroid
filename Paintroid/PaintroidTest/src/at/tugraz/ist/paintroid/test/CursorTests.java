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
import android.graphics.Color;
import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
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
	private Point screenCenter;

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
		screenCenter = new Point(screenWidth / 2, screenHeight / 2);
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

	private void doubleTap(Point p) {
		solo.clickOnScreen(p.x, p.y);
		solo.drag(p.x, p.y, p.x, p.y + 1, 50);
	}

	public void testCursorStates() throws Exception {
		solo.clickOnView(brushButton);
		doubleTap(screenCenter);
		solo.sleep(400);
		assertEquals(Mode.CURSOR, drawingSurface.getMode());
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		doubleTap(screenCenter);
		solo.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());
		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());
		doubleTap(screenCenter);
		solo.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		doubleTap(screenCenter);
		solo.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());
	}

	public void testCursorDraw() throws Exception {
		solo.clickOnView(brushButton);

		assertEquals(Mode.DRAW, drawingSurface.getMode());
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());

		doubleTap(screenCenter);

		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());

		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);

		assertEquals(ToolState.DRAW, drawingSurface.getToolState());

		final int targetX = screenCenter.x + screenWidth / 4;
		final int middleX = (screenCenter.x + targetX) / 2;
		int pixel = drawingSurface.getBitmap().getPixel(middleX, screenCenter.y);
		assertEquals(Color.TRANSPARENT, pixel);

		solo.drag(screenCenter.x, targetX, screenCenter.y, screenCenter.y, 0);
		solo.sleep(400);

		pixel = drawingSurface.getBitmap().getPixel(middleX, screenCenter.y);
		assertEquals(Color.BLACK, pixel);
	}
}