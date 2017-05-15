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

package org.catrobat.paintroid.test.integration.tools;

import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;

public class FlipToolIntegrationTest extends BaseIntegrationTestClass {

	private static final int OFFSET = 150;

	public FlipToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		resetBrush();
	}

	public void testHorizontalFlip() {
		int xPoint = mScreenWidth / 2;
		int yPoint = OFFSET;
		mCurrentDrawingSurfaceBitmap.setPixel(xPoint, yPoint, Color.BLACK);
		mSolo.sleep(500);

		int pixel = PaintroidApplication.drawingSurface.getPixel(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixel);

		selectTool(ToolType.FLIP);
		flipHorizontal();
		yPoint = PaintroidApplication.drawingSurface.getBitmapHeight() - yPoint - 1;
		pixel = PaintroidApplication.drawingSurface.getPixel(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixel);
	}

	public void testVerticalFlip() {
		int xPoint = OFFSET;
		int yPoint = mScreenHeight / 2;

		mCurrentDrawingSurfaceBitmap.setPixel(xPoint, yPoint, Color.BLACK);
		mSolo.sleep(500);

		int pixelColor = PaintroidApplication.drawingSurface.getPixel(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixelColor);

		selectTool(ToolType.FLIP);
		flipVertical();
		xPoint = PaintroidApplication.drawingSurface.getBitmapWidth() - xPoint - 1;

		pixelColor = PaintroidApplication.drawingSurface.getPixel(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixelColor);
	}

	public void testFlipUndoOpenLayermenu() {

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.goBack();
		selectTool(ToolType.FLIP);
		flipHorizontal();
		mSolo.clickOnView(mButtonTopUndo);
		mSolo.clickOnView(mButtonTopRedo);
		mSolo.clickOnView(mButtonTopUndo);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.goBack();
		mSolo.clickOnView(mButtonTopUndo);
		mSolo.clickOnView(mButtonTopLayer);
	}

	private void flipHorizontal() {
		assertTrue("Tool options should be visible", toolOptionsAreShown());
		mSolo.clickOnView(mSolo.getView(R.id.flip_horizontal_btn));
		mSolo.sleep(500);
	}

	private void flipVertical() {
		assertTrue("Tool options should be visible", toolOptionsAreShown());
		mSolo.clickOnView(mSolo.getView(R.id.flip_vertical_btn));
		mSolo.sleep(500);
	}
}
