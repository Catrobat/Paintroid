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
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.ShapeTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ShapeToolTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	private CommandManager commandManager;

	private ShapeTool rectangleShapeTool;
	private ShapeTool ovalShapeTool;
	private ShapeTool heartShapeTool;
	private ShapeTool starShapeTool;

	@UiThreadTest
	@Before
	public void setUp() {
		MainActivity activity = activityTestRule.getActivity();
		Workspace workspace = activity.workspace;
		ToolPaint toolPaint = activity.toolPaint;

		rectangleShapeTool = new ShapeTool(activity, toolPaint, workspace, commandManager);
		rectangleShapeTool.baseShape = ShapeTool.BaseShape.RECTANGLE;

		ovalShapeTool = new ShapeTool(activity, toolPaint, workspace, commandManager);
		ovalShapeTool.baseShape = ShapeTool.BaseShape.OVAL;

		heartShapeTool = new ShapeTool(activity, toolPaint, workspace, commandManager);
		heartShapeTool.baseShape = ShapeTool.BaseShape.HEART;

		starShapeTool = new ShapeTool(activity, toolPaint, workspace, commandManager);
		starShapeTool.baseShape = ShapeTool.BaseShape.STAR;
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

		ShapeTool.BaseShape rectangleShape = rectangleShapeTool.getBaseShape();
		assertEquals(ShapeTool.BaseShape.RECTANGLE, rectangleShape);
		ShapeTool.BaseShape ovalShape = ovalShapeTool.getBaseShape();
		assertEquals(ShapeTool.BaseShape.OVAL, ovalShape);
		ShapeTool.BaseShape heartShape = heartShapeTool.getBaseShape();
		assertEquals(ShapeTool.BaseShape.HEART, heartShape);
		ShapeTool.BaseShape starShape = starShapeTool.getBaseShape();
		assertEquals(ShapeTool.BaseShape.STAR, starShape);
	}

	@UiThreadTest
	@Test
	public void testColorChangeWorks() {
		Paint red = new Paint();
		red.setColor(Color.RED);
		rectangleShapeTool.setDrawPaint(red);
		MainActivity activity = activityTestRule.getActivity();
		Tool currentTool = activity.toolReference.get();
		int color = currentTool.getDrawPaint().getColor();
		assertEquals("Red colour expected", Color.RED, color);
	}
}
