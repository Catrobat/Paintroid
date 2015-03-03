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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.CommandManagerStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.junit.After;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.test.ActivityInstrumentationTestCase2;

public class BaseToolTest extends ActivityInstrumentationTestCase2<MainActivity> {
	protected static final float MOVE_TOLERANCE = BaseTool.MOVE_TOLERANCE;
	private static final int DEFAULT_BRUSH_WIDTH = 25;
	private static final Cap DEFAULT_BRUSH_CAP = Cap.ROUND;
	private static final int DEFAULT_COLOR = Color.BLACK;

	protected Tool mToolToTest;
	protected Paint mPaint;
	protected CommandManagerStub mCommandManagerStub;

	public BaseToolTest() {

		super(MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		System.gc();
		mCommandManagerStub = new CommandManagerStub();
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setStrokeWidth(Tool.stroke25);
		PaintroidApplication.commandManager = mCommandManagerStub;
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		PaintroidApplication.drawingSurface.setBitmap(Bitmap.createBitmap(1, 1, Config.ALPHA_8));
		Thread.sleep(100);
		// Bitmap drawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
		// PaintroidApplication.drawingSurface, "mWorkingBitmap");
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
				.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
				.setStrokeCap(DEFAULT_BRUSH_CAP);
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
				.setColor(DEFAULT_COLOR);

		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
				.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
				.setStrokeCap(DEFAULT_BRUSH_CAP);
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
				.setColor(DEFAULT_COLOR);
		super.tearDown();
		// if (drawingSurfaceBitmap != null && !drawingSurfaceBitmap.isRecycled()) {
		// drawingSurfaceBitmap.recycle();
		// Log.i(PaintroidApplication.TAG, "drawing surface recycling");
		// }
		// drawingSurfaceBitmap = null;
		System.gc();
	}

}
