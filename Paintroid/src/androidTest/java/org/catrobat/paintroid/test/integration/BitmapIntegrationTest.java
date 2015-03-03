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

package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.implementation.BitmapCommand;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.Display;

public class BitmapIntegrationTest extends BaseIntegrationTestClass {

	public BitmapIntegrationTest() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testCenterBitmapSimulateLoad() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
		mSolo.sleep(SHORT_TIMEOUT);


		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		Point bottomrightCanvasPoint = new Point(currentDrawingSurfaceBitmap.getWidth() - 1,
				currentDrawingSurfaceBitmap.getHeight() - 1);

		int widthOverflow = 250;
		int newBitmapHeight = 30;
		float canvasCenterTollerance = 100;

		final Bitmap widthOverflowedBitmap = Bitmap.createBitmap(bottomrightCanvasPoint.x + widthOverflow,
				newBitmapHeight, Bitmap.Config.ALPHA_8);

		float surfaceScaleBeforeBitmapCommand = PaintroidApplication.perspective.getScale();


		PaintroidApplication.commandManager.commitCommand(new BitmapCommand(widthOverflowedBitmap, true));

        mSolo.sleep(MEDIUM_TIMEOUT);

		float surfaceScaleAfterBitmapCommand = PaintroidApplication.perspective.getScale();

		assertTrue("Wrong Scale after setting new bitmap",
				surfaceScaleAfterBitmapCommand < surfaceScaleBeforeBitmapCommand);

		mSolo.drag(bottomrightCanvasPoint.x / 2, bottomrightCanvasPoint.x / 2, bottomrightCanvasPoint.y / 2,
				bottomrightCanvasPoint.y / 2 + canvasCenterTollerance, 1);
		PointF canvasCenter = new PointF((bottomrightCanvasPoint.x + widthOverflow) / 2, newBitmapHeight / 2);

		mSolo.sleep(SHORT_SLEEP);
		assertTrue("Center not set", PaintroidApplication.drawingSurface.getPixel(canvasCenter) != Color.TRANSPARENT);

	}

	public void testDrawingSurfaceBitmapIsScreenSize() {
		float bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		float bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		float displayWidth = display.getWidth();
		float displayHeight = display.getHeight();

		assertEquals("bitmap height should be screen height", bitmapHeight, displayHeight);
		assertEquals("bitmap width should be screen width", bitmapWidth, displayWidth);

	}

}
