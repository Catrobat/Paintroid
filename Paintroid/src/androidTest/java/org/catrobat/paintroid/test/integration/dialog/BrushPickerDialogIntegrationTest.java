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

package org.catrobat.paintroid.test.integration.dialog;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class BrushPickerDialogIntegrationTest extends BaseIntegrationTestClass {

	public BrushPickerDialogIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testBrushPickerDialog() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(2000);
		TextView brushWidthTextView = mSolo.getText("25");
		String brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(25));

		ArrayList<ProgressBar> progressBars = mSolo.getCurrentViews(ProgressBar.class);
		assertEquals(progressBars.size(), 1);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		assertEquals(strokeWidthBar.getProgress(), 25);
		int newStrokeWidth = 80;
		int paintStrokeWidth = -1;
		mSolo.setProgressBar(0, newStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		Logger.getLogger("PAINTROID").fine("strokePaint width :" + strokePaint.getStrokeWidth());
		paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);
		brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(newStrokeWidth));

		mSolo.clickOnImageButton(0);
		assertTrue("Waiting for set stroke cap SQUARE ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);

		mSolo.clickOnButton(mSolo.getString(R.string.done));
		assertTrue("Waiting for Tool to be ready", mSolo.waitForActivity("MainActivity", TIMEOUT));
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);
	}

	@Test
	public void testBrushPickerDialogOnBackPressed() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int step = 0;
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.clickOnView(mMenuBottomParameter1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.sleep(2000);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		ArrayList<ProgressBar> progressBars = mSolo.getCurrentViews(ProgressBar.class);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(progressBars.size(), 1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(strokeWidthBar.getProgress(), 25);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		int newStrokeWidth = 80;
		int paintStrokeWidth = -1;

		mSolo.setProgressBar(0, newStrokeWidth);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		mSolo.clickOnImageButton(0);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for set cap SQUARE", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		mSolo.goBack();
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for Tool to be ready", mSolo.waitForActivity("MainActivity", TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertNotNull("mCanvasPaint is null", strokePaint);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(paintStrokeWidth, newStrokeWidth);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
	}

	@Test
	public void testBrushPickerDialogKeepStrokeOnToolChange() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int step = 0;
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		mSolo.clickOnView(mMenuBottomParameter1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		mSolo.sleep(2000);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		int newStrokeWidth = 80;

		assertFalse("No progress bar found", mSolo.getCurrentViews(ProgressBar.class).isEmpty());
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		mSolo.setProgressBar(0, newStrokeWidth);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);

		assertFalse("No imge buttons found", mSolo.getCurrentViews(ImageButton.class).isEmpty());
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		mSolo.clickOnImageButton(0);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		assertTrue("Waiting for set cap SQUARE", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);

		mSolo.clickOnButton(getActivity().getString(R.string.done));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		assertTrue("Waiting for Tool to be ready", mSolo.waitForActivity("MainActivity", TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);

		selectTool(ToolType.CURSOR);
		// mSolo.clickOnView(mMenuBottomParameter1);
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// mSolo.sleep(2000);
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		//
		// ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// assertEquals(progressBars.size(), 1);
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// String brushWidthText = (String) mSolo.getText("80").getText();
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// assertEquals("Wrong brush width displayed", new Integer(brushWidthText), new Integer(newStrokeWidth));
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		//
		// Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
		// "mCanvasPaint");
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// assertEquals(paintStrokeWidth, newStrokeWidth);
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
		// assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);
		// Log.i(PaintroidApplication.TAG, "testBrushPickerDialogKeepStrokeOnToolChange " + step++);
	}

	@Test
	public void testBrushPickerDialogTestMinimumBrushWidth() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(2000);
		int newStrokeWidth = 0;
		int minStrokeWidth = 1;

		assertFalse("No progress bar found", mSolo.getCurrentViews(ProgressBar.class).isEmpty());
		mSolo.setProgressBar(0, newStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		ArrayList<ProgressBar> progressBars = mSolo.getCurrentViews(ProgressBar.class);
		assertEquals(progressBars.size(), 1);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		assertEquals("Should minimum stroke width be smaller than " + minStrokeWidth, strokeWidthBar.getProgress(),
				minStrokeWidth);
	}

	public void testAntiAliasingOffAtBrushSize1() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.waitForText(mSolo.getString(R.string.stroke_title), 1, TIMEOUT);
		mSolo.setProgressBar(0, 1);
		mSolo.goBack();

		BaseTool currentTool = (BaseTool) PaintroidApplication.currentTool;

		Paint bitmapPaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, currentTool, "mBitmapPaint");
		Paint canvasPaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, currentTool, "mCanvasPaint");

		assertFalse("bitmapPaint antialiasing should be off", bitmapPaint.isAntiAlias());
		assertFalse("canvasPaint antialiasing should be off", canvasPaint.isAntiAlias());
	}

	@Test
	public void testBrushPickerDialogRadioButtons() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(2000);

		assertFalse("No progress bar found", mSolo.getCurrentViews(ProgressBar.class).isEmpty());

		mSolo.clickOnRadioButton(0);
		assertTrue("Waiting for set stroke cap SQUARE ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);

		mSolo.clickOnButton(mSolo.getString(R.string.done));
		assertTrue("Waiting for Tool to be ready", mSolo.waitForActivity("MainActivity", TIMEOUT));
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);

		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(2000);

		assertFalse("No progress bar found", mSolo.getCurrentViews(ProgressBar.class).isEmpty());

		mSolo.clickOnRadioButton(1);
		assertTrue("Waiting for set stroke cap ROUND ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		assertEquals(strokePaint.getStrokeCap(), Cap.ROUND);

		mSolo.clickOnButton(mSolo.getString(R.string.done));
		assertTrue("Waiting for Tool to be ready", mSolo.waitForActivity("MainActivity", TIMEOUT));
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool,
				"mCanvasPaint");
		assertEquals(strokePaint.getStrokeCap(), Cap.ROUND);
	}
}
