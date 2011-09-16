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
import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class FloatingBoxTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private String hsvTab;
	private int screenWidth;
	private int screenHeight;
	private TextView toolButton;
	private TextView parameterButton1;

	public FloatingBoxTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		String languageToLoad_before = "en";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);

		Configuration config_before = new Configuration();
		config_before.locale = locale_before;

		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);
		hsvTab = mainActivity.getResources().getString(R.string.color_hsv);
		toolButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		parameterButton1 = (TextView) mainActivity.findViewById(R.id.btn_Parameter1);

		screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
	}

	/**
	 * Check if the floating box mode is activated after pressing the menu button
	 * and deactivated after pressing it again.
	 * 
	 */
	public void testFloatingBoxModes() throws Exception {
		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
	}

	/**
	 * Check if the floating box is working correctly
	 * 
	 */
	public void testFloatingBox() throws Exception {
		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Point initToolCoordinates = drawingSurface.getToolCoordinates();

		solo.drag(screenWidth / 2, screenWidth / 2 + 200, screenHeight / 2, screenHeight / 2 + 50, 10);

		Point coordinates = drawingSurface.getToolCoordinates();

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		assertEquals(initToolCoordinates.x + 200, coordinates.x);
		assertEquals(initToolCoordinates.y + 50, coordinates.y);
	}

	/**
	 * Check if the floating box stamp function is working correctly
	 * 
	 */
	public void testFloatingBoxStamp() throws Exception {
		solo.clickOnScreen(screenWidth / 2 - 100, screenHeight / 2);
		solo.sleep(500);
		PointF clickCoordinates = drawingSurface.getDrawingSurfaceListener().getLastClickCoordinates();
		assertEquals(Color.BLACK, drawingSurface.getPixelFromScreenCoordinates(clickCoordinates.x, clickCoordinates.y));

		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());
		Point initToolCoordinates = drawingSurface.getToolCoordinates();

		solo.drag(screenWidth / 2, screenWidth / 2 - 100, screenHeight / 2, screenHeight / 2, 10);

		solo.sleep(500);

		solo.clickOnScreen(screenWidth / 2 - 100, screenHeight / 2);

		solo.sleep(500);

		solo.drag(screenWidth / 2 - 100, screenWidth / 2 + 100, screenHeight / 2, screenHeight / 2 + 50, 10);

		Point toolCoordinates = drawingSurface.getToolCoordinates();

		solo.clickOnScreen(screenWidth / 2 + 100, screenHeight / 2 + 50);

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		assertEquals(initToolCoordinates.x + 100, toolCoordinates.x);
		assertEquals(initToolCoordinates.y + 50, toolCoordinates.y);

		assertEquals(Color.BLACK,
				drawingSurface.getPixelFromScreenCoordinates(clickCoordinates.x + 200, clickCoordinates.y + 50));
	}

	/**
	 * Check if the floating box triggers a drag if moved to the edge of the screen
	 * 
	 */
	public void testFloatingBoxDrag() throws Exception {
		solo.waitForActivity("MainActivity", 500);

		float scrollX = DrawingSurface.Perspective.scroll.x;
		float scrollY = DrawingSurface.Perspective.scroll.y;

		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		solo.drag(screenWidth / 2, screenWidth / 2 + 500, screenHeight / 2, screenHeight / 2, 10);

		assertTrue(scrollX != DrawingSurface.Perspective.scroll.x);
		assertEquals(scrollY, DrawingSurface.Perspective.scroll.y);

		solo.drag(screenWidth - 10, screenWidth - 200, screenHeight / 2, screenHeight / 2, 10);

		scrollX = DrawingSurface.Perspective.scroll.x;

		solo.drag(screenWidth - 200, screenWidth - 200, screenHeight / 2, screenHeight / 2 + 500, 10);

		assertEquals(scrollX, DrawingSurface.Perspective.scroll.x);
		assertTrue(scrollY != DrawingSurface.Perspective.scroll.y);
	}

	/**
	 * Check if the floating box works if activated outside of image
	 * 
	 */
	public void testFloatingBoxOutsideImage() throws Exception {
		solo.waitForActivity("MainActivity", 500);

		float scrollX = DrawingSurface.Perspective.scroll.x;
		float scrollY = DrawingSurface.Perspective.scroll.y;

		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		solo.drag(screenWidth / 2, screenWidth / 2 + 500, screenHeight / 2, screenHeight / 2, 10);

		assertTrue(scrollX != DrawingSurface.Perspective.scroll.x);
		assertEquals(scrollY, DrawingSurface.Perspective.scroll.y);

		solo.clickOnScreen(screenWidth - 10, screenHeight / 2);
	}

	public void testFloatingBoxResize() throws Exception {
		int robotiumMistake = 25;

		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Point boxSize1 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize1);
		Point coordinates = new Point(drawingSurface.getToolCoordinates());
		assertNotNull(coordinates);

		//right
		solo.drag(coordinates.x + boxSize1.x / 2 + 10, coordinates.x + boxSize1.x / 2 + 110, coordinates.y,
				coordinates.y, 10);
		Point boxSize2 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize2);
		assertEquals(boxSize1.y, boxSize2.y);
		assertTrue(boxSize1.x < boxSize2.x);

		//left
		solo.drag(coordinates.x - boxSize1.x / 2 - 10, coordinates.x - boxSize1.x / 2 - 110, coordinates.y,
				coordinates.y, 10);
		Point boxSize3 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize3);
		assertEquals(boxSize1.y, boxSize3.y);
		assertTrue(boxSize2.x < boxSize3.x);
		assertTrue(boxSize1.x < boxSize3.x);

		//top
		solo.drag(coordinates.x, coordinates.x, coordinates.y - boxSize1.y / 2 - 10 + robotiumMistake, coordinates.y
				- boxSize1.y / 2 - 100, 10);
		Point boxSize4 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize4);
		assertEquals(boxSize3.x, boxSize4.x);
		assertTrue(boxSize3.y < boxSize4.y);
		assertTrue(boxSize1.y < boxSize4.y);

		//bottom
		solo.drag(coordinates.x, coordinates.x, coordinates.y + boxSize1.y / 2 + 10 + robotiumMistake, coordinates.y
				+ boxSize1.y / 2 + 100, 10);
		Point boxSize5 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize5);
		assertEquals(boxSize3.x, boxSize5.x);
		assertTrue(boxSize4.y < boxSize5.y);
		assertTrue(boxSize1.y < boxSize5.y);
	}

	public void testFloatingBoxRotate() throws Exception {
		int roationSymbolDistance = 30;
		int robotiumMistake = 25;

		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		Point boxSize1 = drawingSurface.getFloatingBoxSize();
		assertNotNull(boxSize1);
		Point coordinates = new Point(drawingSurface.getToolCoordinates());
		assertNotNull(coordinates);

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		solo.sleep(500);

		float rotation = drawingSurface.getFloatingBoxRotation();

		//left
		solo.drag(coordinates.x - boxSize1.x / 2 - roationSymbolDistance - 10, coordinates.x - roationSymbolDistance
				- boxSize1.x / 2 - 110, coordinates.y - boxSize1.y / 2 - roationSymbolDistance - 10 + robotiumMistake,
				coordinates.y - boxSize1.y / 2 - roationSymbolDistance - 10 + robotiumMistake, 10);
		float rotation_after_1 = drawingSurface.getFloatingBoxRotation();
		assertTrue(rotation > rotation_after_1);

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());

		Utils.selectTool(solo, toolButton, R.string.button_floating_box);
		assertEquals(ToolType.FLOATINGBOX, drawingSurface.getToolType());

		solo.clickOnScreen(screenWidth / 2, screenHeight / 2);

		solo.sleep(500);

		//right
		solo.drag(coordinates.x - boxSize1.x / 2 - roationSymbolDistance - 10, coordinates.x - roationSymbolDistance
				- boxSize1.x / 2 + 110, coordinates.y - boxSize1.y / 2 - roationSymbolDistance - 10 + robotiumMistake,
				coordinates.y - boxSize1.y / 2 - roationSymbolDistance - 10 + robotiumMistake, 10);
		float rotation_after_2 = drawingSurface.getFloatingBoxRotation();
		assertTrue(rotation < rotation_after_2);
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
