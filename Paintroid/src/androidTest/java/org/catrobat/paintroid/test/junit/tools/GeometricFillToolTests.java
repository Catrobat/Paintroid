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

import android.graphics.Color;
import android.graphics.Paint;
import android.support.test.annotation.UiThreadTest;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeometricFillToolTests extends BaseToolTest {

	private GeometricFillTool rectangleShapeTool;
	private GeometricFillTool ovalShapeTool;
	private GeometricFillTool heartShapeTool;
	private GeometricFillTool starShapeTool;

	public GeometricFillToolTests() {
		super();
	}

	@UiThreadTest
	@Override
	@Before
	public void setUp() throws Exception {
		rectangleShapeTool = new GeometricFillTool(getActivity(), ToolType.SHAPE);
		rectangleShapeTool.baseShape = GeometricFillTool.BaseShape.RECTANGLE;
		ovalShapeTool = new GeometricFillTool(getActivity(), ToolType.SHAPE);
		ovalShapeTool.baseShape = GeometricFillTool.BaseShape.OVAL;
		heartShapeTool = new GeometricFillTool(getActivity(), ToolType.SHAPE);
		heartShapeTool.baseShape = GeometricFillTool.BaseShape.HEART;
		starShapeTool = new GeometricFillTool(getActivity(), ToolType.SHAPE);
		starShapeTool.baseShape = GeometricFillTool.BaseShape.STAR;
		super.setUp();
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
