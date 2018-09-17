/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GeometricFillToolTests {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	private GeometricFillTool rectangleShapeTool;
	private GeometricFillTool ovalShapeTool;
	private GeometricFillTool heartShapeTool;
	private GeometricFillTool starShapeTool;

	@UiThreadTest
	@Before
	public void setUp() {
		rectangleShapeTool = new GeometricFillTool(activityTestRule.getActivity(), ToolType.SHAPE);
		rectangleShapeTool.baseShape = GeometricFillTool.BaseShape.RECTANGLE;
		ovalShapeTool = new GeometricFillTool(activityTestRule.getActivity(), ToolType.SHAPE);
		ovalShapeTool.baseShape = GeometricFillTool.BaseShape.OVAL;
		heartShapeTool = new GeometricFillTool(activityTestRule.getActivity(), ToolType.SHAPE);
		heartShapeTool.baseShape = GeometricFillTool.BaseShape.HEART;
		starShapeTool = new GeometricFillTool(activityTestRule.getActivity(), ToolType.SHAPE);
		starShapeTool.baseShape = GeometricFillTool.BaseShape.STAR;
	}

	@UiThreadTest
	@After
	public void tearDown() {
		PaintroidApplication.drawingSurface.setBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8));
		BaseTool.reset();
	}

	@UiThreadTest
	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolTypeRect = rectangleShapeTool.getToolType();
		assertEquals(ToolType.SHAPE, toolTypeRect);
		toolTypeRect = ovalShapeTool.getToolType();
		assertEquals(ToolType.SHAPE, toolTypeRect);
		toolTypeRect = heartShapeTool.getToolType();
		assertEquals(ToolType.SHAPE, toolTypeRect);
		toolTypeRect = starShapeTool.getToolType();
		assertEquals(ToolType.SHAPE, toolTypeRect);

		GeometricFillTool.BaseShape rectangleShape = rectangleShapeTool.getBaseShape();
		assertEquals(GeometricFillTool.BaseShape.RECTANGLE, rectangleShape);
		GeometricFillTool.BaseShape ovalShape = ovalShapeTool.getBaseShape();
		assertEquals(GeometricFillTool.BaseShape.OVAL, ovalShape);
		GeometricFillTool.BaseShape heartShape = heartShapeTool.getBaseShape();
		assertEquals(GeometricFillTool.BaseShape.HEART, heartShape);
		GeometricFillTool.BaseShape starShape = starShapeTool.getBaseShape();
		assertEquals(GeometricFillTool.BaseShape.STAR, starShape);
	}

	@UiThreadTest
	@Test
	public void testColorChangeWorks() {
		Paint red = new Paint();
		red.setColor(Color.RED);
		rectangleShapeTool.setDrawPaint(red);
		int color = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Red colour expected", Color.RED, color);
	}
}
