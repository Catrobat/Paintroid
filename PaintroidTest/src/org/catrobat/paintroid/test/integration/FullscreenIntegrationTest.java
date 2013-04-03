/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import junit.framework.AssertionFailedError;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.ui.DrawingSurface;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.KeyEvent;

public class FullscreenIntegrationTest extends BaseIntegrationTestClass {

	private static final int TOOLBAR_BOTTOM_OFFSET = 20;

	public FullscreenIntegrationTest() throws Exception {
		super();
	}

	@Override
	public void setUp() {
		super.setUp();
		// resetBrush();
		PaintroidApplication.currentTool.changePaintStrokeWidth(500);
	}

	public void testHideToolbar() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		switchToFullscreen();

		int clickPointX = mScreenWidth / 2;
		int clickPointY = mScreenHeight / 2 - (int) Utils.getStatusbarHeigt(getActivity());
		Point bitmapPixelPosition = new Point();
		try {
			bitmapPixelPosition = Utils.convertFromCanvasToScreen(new Point(clickPointX, clickPointY),
					PaintroidApplication.perspective);
		} catch (Exception whatever) {
			// TODO Auto-generated catch block
			whatever.printStackTrace();
		}
		mSolo.clickOnScreen(clickPointX, clickPointY);
		int pixelColor = PaintroidApplication.drawingSurface.getPixel(new PointF(bitmapPixelPosition.x,
				bitmapPixelPosition.y - (int) Utils.getStatusbarHeigt(getActivity()) * 2));
		assertEquals("pixel should be black", Color.BLACK, pixelColor);
	}

	public void testHideStatusbarOnHideToolbar() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		switchToFullscreen();

		int clickPointX = mScreenWidth / 2;
		int clickPointY = getActivity().getSupportActionBar().getHeight();

		mSolo.clickOnScreen(clickPointX, clickPointY);
		Point bitmapPixelPosition = new Point();
		try {
			bitmapPixelPosition = Utils.convertFromCanvasToScreen(new Point(clickPointX, clickPointY),
					PaintroidApplication.perspective);
		} catch (Exception whatever) {
			// TODO Auto-generated catch block
			whatever.printStackTrace();
		}
		int pixelColor = PaintroidApplication.drawingSurface.getPixel(new PointF(bitmapPixelPosition.x,
				bitmapPixelPosition.y));
		assertEquals("pixel should be black", Color.BLACK, pixelColor);
	}

	public void testShowToolbarOnBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		switchToFullscreen();

		int clickPointX = mScreenWidth / 2;
		int clickPointY = mScreenHeight - TOOLBAR_BOTTOM_OFFSET;
		mSolo.goBack();
		mSolo.sleep(1000);
		mSolo.clickOnScreen(clickPointX, clickPointY);
		mSolo.sleep(1000);
		int pixel = PaintroidApplication.drawingSurface.getPixel(new PointF(clickPointX, clickPointY
				- (int) Utils.getStatusbarHeigt(getActivity()) * 2));
		assertEquals("pixel should be transparent", Color.TRANSPARENT, pixel);
		mSolo.goBack();
	}

	public void testShowStatusbarOnBackPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		switchToFullscreen();
		mSolo.goBack();
		mSolo.sleep(1000);

		int clickPointX = mScreenWidth / 2;
		int clickPointY = 0;

		boolean assertionFailedErrorCaught = false;
		try {
			mSolo.clickOnScreen(clickPointX, clickPointY);
		} catch (AssertionFailedError ex) {
			assertionFailedErrorCaught = true;
		} finally {
			assertTrue("assertion failed error should have been caught", assertionFailedErrorCaught);
		}

	}

	public void testShowToolbarOnMenuPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		switchToFullscreen();
		mSolo.sendKey(KeyEvent.KEYCODE_MENU);
		mSolo.sleep(500);

		int clickPointX = mScreenWidth / 2;
		int clickPointY = mScreenHeight - TOOLBAR_BOTTOM_OFFSET;

		try {
			mSolo.searchText(mSolo.getString(R.string.menu_save_image), 1, true, true);
			mSolo.goBack();
			mSolo.sleep(1000);
		} catch (AssertionFailedError assertion) {
			;// compatibility check for older versions
		}
		mSolo.clickOnScreen(clickPointX, clickPointY);
		mSolo.sleep(1000);
		int pixel = PaintroidApplication.drawingSurface.getPixel(new PointF(clickPointX, clickPointY));
		assertEquals("pixel should be transparent", Color.TRANSPARENT, pixel);
	}

	public void testShowStatusbarOnMenuPressed() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		switchToFullscreen();
		mSolo.sendKey(KeyEvent.KEYCODE_MENU);
		mSolo.sleep(500);

		int clickPointX = mScreenWidth / 2;
		int clickPointY = 0;

		boolean assertionFailedErrorCaught = false;
		try {
			mSolo.clickOnScreen(clickPointX, clickPointY);
		} catch (AssertionFailedError ex) {
			assertionFailedErrorCaught = true;
		} finally {
			assertTrue("assertion failed error should have been caught", assertionFailedErrorCaught);
		}
	}

	private void switchToFullscreen() {
		try {
			mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
		} catch (AssertionFailedError assertion) {
			mSolo.goBack();
			mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu_condensed), true);
		}
		mSolo.sleep(2000);
		PaintroidApplication.perspective.resetScaleAndTranslation();
		assertFalse("SupportActionBarStillVisible", getActivity().getSupportActionBar().isShowing());
	}

}
