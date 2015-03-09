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

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Color;
import android.graphics.Paint;

public class GeometricFillToolTests extends BaseToolTest {

	Tool rectangleShapeTool;
	Tool ovalShapeTool;

	public GeometricFillToolTests() {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		rectangleShapeTool = new GeometricFillTool(getActivity(), ToolType.RECT);
		ovalShapeTool = new GeometricFillTool(getActivity(), ToolType.ELLIPSE);
		super.setUp();
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolTypeRect = rectangleShapeTool.getToolType();
		assertEquals(ToolType.RECT, toolTypeRect);
		ToolType toolTypeOval = ovalShapeTool.getToolType();
		assertEquals(ToolType.ELLIPSE, toolTypeOval);
	}

	@Test
	public void testColorChangeWorks() {
		Paint red = new Paint();
		red.setColor(Color.RED);
		rectangleShapeTool.setDrawPaint(red);
		int color = rectangleShapeTool.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Red colour expected", Color.RED, color);
	}

	@Test
	public void testShouldReturnCorrectResourceForBottomButtonOne() {
		int resource = rectangleShapeTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
		assertEquals("Transparent should be displayed", R.drawable.icon_menu_no_icon, resource);

		resource = ovalShapeTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
		assertEquals("Transparent should be displayed", R.drawable.icon_menu_no_icon, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForBottomButtonTwo() {
		int resource = rectangleShapeTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
		assertEquals("Color picker should be displayed", R.drawable.icon_menu_color_palette, resource);

		resource = ovalShapeTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
		assertEquals("Color picker should be displayed", R.drawable.icon_menu_color_palette, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = rectangleShapeTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Rectangle tool icon should be displayed", R.drawable.icon_menu_rectangle, resource);

		resource = ovalShapeTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Ellipse tool icon should be displayed", R.drawable.icon_menu_ellipse, resource);
	}
}
