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

package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.After;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.command.Command;

public class CommandTestSetup extends AndroidTestCase {

	protected Command mCommandUnderTest;
	protected Command mCommandUnderTestNull;
	protected Paint mPaintUnderTest;
	protected PointF mPointUnderTest;
	protected Canvas mCanvasUnderTest;
	protected Bitmap mBitmapUnderTest;
	protected Bitmap mCanvasBitmapUnderTest;
	protected final int BITMAP_BASE_COLOR = Color.GREEN;
	protected final int BITMAP_REPLACE_COLOR = Color.CYAN;
	protected final int PAINT_BASE_COLOR = Color.BLUE;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCanvasUnderTest = new Canvas();
		mCanvasBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		mCanvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR);
		mBitmapUnderTest = mCanvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		mCanvasUnderTest.setBitmap(mCanvasBitmapUnderTest);
		mPaintUnderTest = new Paint();
		mPaintUnderTest.setColor(PAINT_BASE_COLOR);
		mPaintUnderTest.setStrokeWidth(0);
		mPaintUnderTest.setStyle(Paint.Style.FILL);
		mPaintUnderTest.setStrokeCap(Cap.BUTT);
		mPointUnderTest = new PointF(mCanvasBitmapUnderTest.getWidth() / 2, mCanvasBitmapUnderTest.getHeight() / 2);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		mCanvasUnderTest = null;
		mCanvasBitmapUnderTest.recycle();
		mCanvasBitmapUnderTest = null;
		mBitmapUnderTest.recycle();
		mBitmapUnderTest = null;
		mPaintUnderTest = null;
		mPointUnderTest = null;
	}

}
