/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.paintroid.test.integration.tools;

import android.graphics.Color;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.integration.BaseIntegrationTestClass;
import at.tugraz.ist.paintroid.test.integration.Utils;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class EraserToolIntegrationTest extends BaseIntegrationTestClass {

	public EraserToolIntegrationTest() throws Exception {
		super();
	}

	public void testEraseAfterBrushAndThenBrushAgain() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		mSolo.clickOnScreen(100, 100);

		DrawingSurface drawingSurface = (DrawingSurfaceImplementation) getActivity().findViewById(
				R.id.drawingSurfaceView);

		int colorBeforeErase = drawingSurface.getBitmapColor(new PointF(100, 60));
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_eraser);
		mSolo.clickOnScreen(100, 100);
		int colorAfterErase = drawingSurface.getBitmapColor(new PointF(100, 100));
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);

		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_brush);
		mSolo.clickOnScreen(100, 100);
		int colorAfterBrush = drawingSurface.getBitmapColor(new PointF(100, 60));
		assertEquals("Brushing after erase should be black again like before erasing", Color.BLACK, colorAfterBrush);
	}
}
