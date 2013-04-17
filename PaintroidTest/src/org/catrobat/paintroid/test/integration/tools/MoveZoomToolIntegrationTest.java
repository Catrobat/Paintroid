/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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

package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Test;

import android.graphics.PointF;

public class MoveZoomToolIntegrationTest extends BaseIntegrationTestClass {

	public MoveZoomToolIntegrationTest() throws Exception {
		super();
	}

	@Test
	public void testZoomOut() {
		float scaleBeforeZoom = PaintroidApplication.perspective.getScale();
		selectTool(ToolType.MOVE);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(200);
		float scaleAfterZoom = PaintroidApplication.perspective.getScale();

		assertTrue("Zooming-out has not worked", scaleBeforeZoom > scaleAfterZoom);
	}

	@Test
	public void testZoomIn() {
		float scaleBeforeZoom = PaintroidApplication.perspective.getScale();
		selectTool(ToolType.MOVE);

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(200);
		float scaleAfterZoom = PaintroidApplication.perspective.getScale();

		assertTrue("Zooming-in has not worked", scaleBeforeZoom < scaleAfterZoom);
	}

	@Test
	public void testToolStaysTheSameAfterSwitch() {
		selectTool(ToolType.RECT);
		mSolo.sleep(500);
		PointF fromPoint = new PointF(((3 * mScreenWidth) / 5), ((3 * mScreenHeight) / 5));
		PointF toPoint = new PointF(((4 * mScreenWidth) / 5), ((4 * mScreenHeight) / 5));
		mSolo.drag(fromPoint.x, toPoint.x, fromPoint.y, toPoint.y, 1);
		mSolo.sleep(1000);

		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.RECT);
		Tool oldTool = PaintroidApplication.currentTool;
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.RECT);

		Tool newTool = PaintroidApplication.currentTool;
		assertTrue("Tool is a different object after switch", oldTool == newTool);
	}

	@Test
	public void testSwitchingBetweenZoomAndMoveTool() {
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
		selectTool(ToolType.ZOOM);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.ZOOM);
		selectTool(ToolType.ZOOM);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.ZOOM);
		selectTool(ToolType.MOVE);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		selectTool(ToolType.MOVE);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.BRUSH);
		selectTool(ToolType.RECT);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.RECT);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals(PaintroidApplication.currentTool.getToolType(), ToolType.RECT);
	}
}
