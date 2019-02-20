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

import android.graphics.Color;
import android.graphics.Paint;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GeometricFillToolTests {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	private CommandManager commandManager;

	private GeometricFillTool rectangleShapeTool;
	private GeometricFillTool ovalShapeTool;
	private GeometricFillTool heartShapeTool;
	private GeometricFillTool starShapeTool;

	@UiThreadTest
	@Before
	public void setUp() {
		MainActivity activity = activityTestRule.getActivity();
		Workspace workspace = activity.workspace;
		ToolPaint toolPaint = activity.toolPaint;

		rectangleShapeTool = new GeometricFillTool(activity, toolPaint, workspace, commandManager);
		rectangleShapeTool.baseShape = GeometricFillTool.BaseShape.RECTANGLE;

		ovalShapeTool = new GeometricFillTool(activity, toolPaint, workspace, commandManager);
		ovalShapeTool.baseShape = GeometricFillTool.BaseShape.OVAL;

		heartShapeTool = new GeometricFillTool(activity, toolPaint, workspace, commandManager);
		heartShapeTool.baseShape = GeometricFillTool.BaseShape.HEART;

		starShapeTool = new GeometricFillTool(activity, toolPaint, workspace, commandManager);
		starShapeTool.baseShape = GeometricFillTool.BaseShape.STAR;
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
