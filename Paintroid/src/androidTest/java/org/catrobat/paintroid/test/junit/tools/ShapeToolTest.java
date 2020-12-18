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

import android.util.DisplayMetrics;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.implementation.ShapeTool;
import org.catrobat.paintroid.tools.options.ShapeToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ShapeToolTest {
	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();
	@Parameter
	public DrawableShape shape;
	@Mock
	private CommandManager commandManager;
	@Mock
	private ShapeToolOptionsView shapeToolOptions;
	@Mock
	private ToolOptionsVisibilityController toolOptionsViewController;
	@Mock
	private ContextCallback contextCallback;
	@Mock
	private Workspace workspace;
	@Mock
	private ToolPaint toolPaint;
	@Mock
	private DisplayMetrics displayMetrics;
	private ShapeTool shapeTool;

	@Parameters(name = "{0}")
	public static Iterable<DrawableShape> data() {
		return Arrays.asList(
				DrawableShape.RECTANGLE,
				DrawableShape.OVAL,
				DrawableShape.HEART,
				DrawableShape.STAR
		);
	}

	@Before
	public void setUp() {
		when(workspace.getWidth()).thenReturn(100);
		when(workspace.getHeight()).thenReturn(100);
		when(workspace.getScale()).thenReturn(1f);
		when(workspace.getPerspective()).thenReturn(new Perspective(100, 100));

		when(contextCallback.getDisplayMetrics()).thenReturn(displayMetrics);
		displayMetrics.widthPixels = 100;
		displayMetrics.heightPixels = 100;

		shapeTool = new ShapeTool(shapeToolOptions, contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
		shapeTool.setBaseShape(shape);
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = shapeTool.getToolType();
		assertEquals(ToolType.SHAPE, toolType);
	}

	@Test
	public void testShouldReturnCorrectBaseShape() {
		DrawableShape baseShape = shapeTool.getBaseShape();
		assertEquals(shape, baseShape);
	}
}
