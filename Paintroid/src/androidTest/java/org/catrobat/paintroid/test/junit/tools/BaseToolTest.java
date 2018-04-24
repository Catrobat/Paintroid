/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.CommandManagerStub;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public abstract class BaseToolTest {
	static final float MOVE_TOLERANCE = BaseTool.MOVE_TOLERANCE;
	private static final int DEFAULT_BRUSH_WIDTH = 25;
	private static final Cap DEFAULT_BRUSH_CAP = Cap.ROUND;
	private static final int DEFAULT_COLOR = Color.BLACK;

	Tool toolToTest;
	Paint paint;
	CommandManagerStub commandManagerStub;

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	BaseToolTest() {
	}

	@UiThreadTest
	@Before
	public void setUp() throws Exception {
		commandManagerStub = new CommandManagerStub();
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeWidth(BaseTool.STROKE_25);
		PaintroidApplication.commandManager = commandManagerStub;
	}

	@UiThreadTest
	@After
	public void tearDown() throws Exception {
		PaintroidApplication.drawingSurface.setBitmap(Bitmap.createBitmap(1, 1, Config.ALPHA_8));
		Thread.sleep(100);
		BaseTool.CANVAS_PAINT.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
		BaseTool.CANVAS_PAINT.setStrokeCap(DEFAULT_BRUSH_CAP);
		BaseTool.CANVAS_PAINT.setColor(DEFAULT_COLOR);

		BaseTool.BITMAP_PAINT.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
		BaseTool.BITMAP_PAINT.setStrokeCap(DEFAULT_BRUSH_CAP);
		BaseTool.BITMAP_PAINT.setColor(DEFAULT_COLOR);
	}

	int getAttributeButtonColor() {
		return BaseTool.BITMAP_PAINT.getColor();
	}

	public Activity getActivity() {
		return activityTestRule.getActivity();
	}
}
