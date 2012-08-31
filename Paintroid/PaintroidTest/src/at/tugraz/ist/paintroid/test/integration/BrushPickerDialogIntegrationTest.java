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
package at.tugraz.ist.paintroid.test.integration;

import java.util.ArrayList;

import org.junit.Test;

import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.BaseTool;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class BrushPickerDialogIntegrationTest extends BaseIntegrationTestClass {

	public BrushPickerDialogIntegrationTest() throws Exception {
		super();
	}

	@Test
	public void testBrushPickerDialog() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonParameterTop1);
		mSolo.sleep(2000);
		TextView brushWidthTextView = mSolo.getText("25");
		String brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(25));

		ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
		assertEquals(progressBars.size(), 1);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		assertEquals(strokeWidthBar.getProgress(), 25);
		int newStrokeWidth = 80;
		int paintStrokeWidth = -1;
		Drawable originalDrawable = mButtonParameterTop1.getBackground();
		mSolo.setProgressBar(0, newStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCanvasPaint");
		paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);
		brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(newStrokeWidth));

		mSolo.clickOnImageButton(0);
		assertTrue("Waiting for set stroke cap SQUARE ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCanvasPaint");
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);

		mSolo.clickOnButton(mSolo.getString(R.string.button_accept));
		assertTrue("Waiting for Tool to be ready", mSolo.waitForActivity("MainActivity", TIMEOUT));
		strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCanvasPaint");
		paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);
		assertNotSame(originalDrawable, mButtonParameterTop1.getBackground());
	}

	@Test
	public void testBrushPickerDialogOnBackPressed() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int step = 0;
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.clickOnView(mButtonParameterTop1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.sleep(2000);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
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

		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCanvasPaint");
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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.clickOnView(mMenuBottomParameter1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.sleep(2000);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		int newStrokeWidth = 80;

		assertFalse("No progress bar found", mSolo.getCurrentProgressBars().isEmpty());
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.setProgressBar(0, newStrokeWidth);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		assertFalse("No imge buttons found", mSolo.getCurrentImageButtons().isEmpty());
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.clickOnImageButton(0);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for set cap SQUARE", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		mSolo.clickOnButton(getActivity().getString(R.string.button_accept));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for Tool to be ready", mSolo.waitForActivity("MainActivity", TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		mSolo.clickOnView(mMenuBottomTool);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		selectTool(ToolType.CURSOR);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for Tool to Change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		mSolo.clickOnView(mMenuBottomParameter1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.sleep(2000);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(progressBars.size(), 1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		String brushWidthText = (String) mSolo.getText("80").getText();
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals("Wrong brush width displayed", new Integer(brushWidthText), new Integer(newStrokeWidth));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);

		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCanvasPaint");
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(paintStrokeWidth, newStrokeWidth);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
	}

	@Test
	public void testBrushPickerDialogTestMinimumBrushWidth() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int step = 0;
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.clickOnView(mButtonParameterTop1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.sleep(2000);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		int newStrokeWidth = 0;
		int minStrokeWidth = 1;

		assertFalse("No progress bar found", mSolo.getCurrentProgressBars().isEmpty());
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		mSolo.setProgressBar(0, newStrokeWidth);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals(progressBars.size(), 1);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
		assertEquals("Should minimum stroke width be smaller than " + minStrokeWidth, strokeWidthBar.getProgress(),
				minStrokeWidth);
		Log.i(PaintroidApplication.TAG, "testBrushPickerDialogOnBackPressed " + step++);
	}
}
