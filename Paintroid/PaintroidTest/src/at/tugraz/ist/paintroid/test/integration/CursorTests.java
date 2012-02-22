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

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.deprecated.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.deprecated.graphic.utilities.Tool.ToolState;

import com.jayway.android.robotium.solo.Solo;

public class CursorTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private DrawingSurface drawingSurface;
	private TextView toolButton;

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

		//		drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);

		toolButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);

		screenWidth = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = mainActivity.getWindowManager().getDefaultDisplay().getHeight();
		screenCenter = new Point(screenWidth / 2, screenHeight / 2);
	}

	@Smoke
	public void testCursorStates() throws Exception {
		Utils.selectTool(solo, toolButton, R.string.button_cursor);

		assertEquals(ToolType.CURSOR, drawingSurface.getToolType());
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());
		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());
		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);
		assertEquals(ToolState.DRAW, drawingSurface.getToolState());

		Utils.selectTool(solo, toolButton, R.string.button_brush);
		solo.sleep(400);
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());
	}

	@Smoke
	public void testCursorDraw() throws Exception {
		assertEquals(ToolType.BRUSH, drawingSurface.getToolType());
		assertEquals(ToolState.INACTIVE, drawingSurface.getToolState());

		Utils.selectTool(solo, toolButton, R.string.button_cursor);

		assertEquals(ToolState.ACTIVE, drawingSurface.getToolState());

		solo.clickOnScreen(screenCenter.x, screenCenter.y);
		solo.sleep(400);

		assertEquals(ToolState.DRAW, drawingSurface.getToolState());

		final int targetX = screenCenter.x + screenWidth / 4;

		Point toolCoordinates = drawingSurface.getToolCoordinates();

		int pixel = drawingSurface
				.getPixelFromScreenCoordinates(toolCoordinates.x + screenWidth / 4, toolCoordinates.y);
		assertEquals(Color.TRANSPARENT, pixel);

		solo.drag(screenCenter.x, targetX, screenCenter.y, screenCenter.y, 0);

		solo.sleep(400);

		pixel = drawingSurface.getPixelFromScreenCoordinates(toolCoordinates.x + screenWidth / 4, toolCoordinates.y);
		assertEquals(Color.BLACK, pixel);
	}
}