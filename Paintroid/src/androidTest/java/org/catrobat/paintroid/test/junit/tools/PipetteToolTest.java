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

package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.junit.stubs.DrawingSurfaceStub;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.PipetteTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.PointF;
import android.test.UiThreadTest;

public class PipetteToolTest extends BaseToolTest {

	private final int X_COORDINATE_RED = 1;
	private final int X_COORDINATE_GREEN = 3;
	private final int X_COORDINATE_BLUE = 5;
	private final int X_COORDINATE_PART_TRANSPARENT = 7;
	private DrawingSurface mOriginalDrawingSurface = null;

	public PipetteToolTest() {
		super();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		mToolToTest = new PipetteTool(getActivity(), ToolType.PIPETTE);
		super.setUp();
		DrawingSurfaceStub drawingSurfaceStub = new DrawingSurfaceStub(getActivity());
		drawingSurfaceStub.mBitmap = Bitmap.createBitmap(10, 1, Config.ARGB_8888);
		drawingSurfaceStub.mBitmap.setPixel(X_COORDINATE_RED, 0, Color.RED);
		drawingSurfaceStub.mBitmap.setPixel(X_COORDINATE_GREEN, 0, Color.GREEN);
		drawingSurfaceStub.mBitmap.setPixel(X_COORDINATE_BLUE, 0, Color.BLUE);
		drawingSurfaceStub.mBitmap.setPixel(X_COORDINATE_PART_TRANSPARENT, 0, 0xAAAAAAAA);
		mOriginalDrawingSurface = PaintroidApplication.drawingSurface;
		PaintroidApplication.drawingSurface = drawingSurfaceStub;
	}

	@Override
	@After
	public void tearDown() {
		DrawingSurfaceStub drawingSurfaceStub = (DrawingSurfaceStub) PaintroidApplication.drawingSurface;
		PaintroidApplication.drawingSurface = mOriginalDrawingSurface;
		drawingSurfaceStub.mBitmap.recycle();
		drawingSurfaceStub.mBitmap = null;
		try {
			super.tearDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@UiThreadTest
	public void testHandleDown() {
		mToolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		assertEquals("Paint color has not changed", Color.RED, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@UiThreadTest
	public void testHandleMove() {
		mToolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		assertEquals("Paint color has not changed", Color.RED, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_RED + 1, 0));
		assertEquals("Paint color has not changed", Color.TRANSPARENT, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_GREEN, 0));
		assertEquals("Paint color has not changed", Color.GREEN, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@UiThreadTest
	public void testHandleUp() {
		mToolToTest.handleUp(new PointF(X_COORDINATE_BLUE, 0));
		assertEquals("Paint color has not changed", Color.BLUE, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleUp(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();
		assertEquals(ToolType.PIPETTE, toolType);
	}

	@UiThreadTest
	public void testShouldReturnCorrectResourceForForTopButtonFourIfColorIsTransparent() {
		mToolToTest.handleUp(new PointF(0, 0));
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Transparend should be displayed", R.drawable.checkeredbg_repeat, resource);
	}

	@UiThreadTest
	public void testShouldReturnCorrectColorForForTopButtonFourIfColorIsTransparent() {
		mToolToTest.handleUp(new PointF(0, 0));
		int color = mToolToTest.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Transparent colour expected", Color.TRANSPARENT, color);
	}

	@UiThreadTest
	public void testShouldReturnCorrectColorForForTopButtonFourIfColorIsRed() {
		mToolToTest.handleUp(new PointF(X_COORDINATE_RED, 0));
		int color = mToolToTest.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Red colour expected", Color.RED, color);
	}

	@Test
	public void testShouldReturnCorrectResourceForForBottomButtonOne() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
		assertEquals("Transparent should be displayed", R.drawable.icon_menu_no_icon, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForForBottomButtonTwo() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
		assertEquals("Transparent should be displayed", R.drawable.icon_menu_no_icon, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Pipette tool icon should be displayed", R.drawable.icon_menu_pipette, resource);
	}

}
