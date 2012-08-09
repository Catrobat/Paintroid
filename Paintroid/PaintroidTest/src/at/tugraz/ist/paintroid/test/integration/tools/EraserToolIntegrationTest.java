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

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.integration.BaseIntegrationTestClass;
import at.tugraz.ist.paintroid.test.integration.Utils;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.implementation.BaseTool;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class EraserToolIntegrationTest extends BaseIntegrationTestClass {

	public EraserToolIntegrationTest() throws Exception {
		super();
	}

	public void testEraseNothing() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		DrawingSurface drawingSurface = (DrawingSurfaceImplementation) getActivity().findViewById(
				R.id.drawingSurfaceView);

		int xCoord = 100;
		int yCoord = 60;

		int colorBeforeErase = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("Get transparent background color", Color.TRANSPARENT, colorBeforeErase);

		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_eraser);
		mSolo.clickOnScreen(xCoord, yCoord + Utils.getStatusbarHeigt(getActivity()));

		int colorAfterErase = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("Pixel should still be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	public void testEraseAfterBrushAndThenBrushAgain() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int xCoord = 100;
		int yCoord = 60;

		mSolo.clickOnScreen(xCoord, yCoord + Utils.getStatusbarHeigt(getActivity()));

		DrawingSurface drawingSurface = (DrawingSurfaceImplementation) getActivity().findViewById(
				R.id.drawingSurfaceView);

		int colorBeforeErase = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_eraser);
		mSolo.clickOnScreen(xCoord, yCoord + Utils.getStatusbarHeigt(getActivity()));
		int colorAfterErase = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);

		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_brush);
		mSolo.clickOnScreen(xCoord, yCoord + Utils.getStatusbarHeigt(getActivity()));
		int colorAfterBrush = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("Brushing after erase should be black again like before erasing", Color.BLACK, colorAfterBrush);
	}

	public void testChangeEraserBrushSize() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int xCoord = 100;
		int yCoord = 60;

		mSolo.clickOnScreen(xCoord, yCoord + Utils.getStatusbarHeigt(getActivity()));

		DrawingSurface drawingSurface = (DrawingSurfaceImplementation) getActivity().findViewById(
				R.id.drawingSurfaceView);

		int colorBeforeErase = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_eraser);
		mSolo.clickOnView(mToolBarButtonTwo);
		assertTrue("Waiting for Brush Picker Dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		TextView brushWidthTextView = mSolo.getText("25");
		String brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(25));

		ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
		assertEquals(progressBars.size(), 1);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		assertEquals(strokeWidthBar.getProgress(), 25);

		int newStrokeWidth = 80;
		mSolo.setProgressBar(0, newStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);
		mSolo.clickOnButton(mSolo.getString(R.string.button_accept));

		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCanvasPaint");
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);

		mSolo.clickOnScreen(xCoord, yCoord + Utils.getStatusbarHeigt(getActivity()));
		int colorAfterErase = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	public void testChangeEraserBrushForm() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int xCoord = 100;
		int yCoord = 60;

		mSolo.clickOnScreen(xCoord, yCoord + Utils.getStatusbarHeigt(getActivity()));

		DrawingSurface drawingSurface = (DrawingSurfaceImplementation) getActivity().findViewById(
				R.id.drawingSurfaceView);

		int colorBeforeErase = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_eraser);
		mSolo.clickOnView(mToolBarButtonTwo);
		assertTrue("Waiting for Brush Picker Dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));

		mSolo.clickOnImageButton(0);
		assertTrue("Waiting for set stroke cap SQUARE ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCanvasPaint");
		mSolo.clickOnButton(mSolo.getString(R.string.button_accept));
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);

		mSolo.clickOnScreen(xCoord, yCoord + Utils.getStatusbarHeigt(getActivity()));
		int colorAfterErase = drawingSurface.getBitmapColor(new PointF(xCoord, yCoord));
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	public void testRestorePreviousToolSettings() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		// / initialize brush settings
		mSolo.clickOnView(mToolBarButtonTwo);
		assertTrue("Waiting for Brush Picker Dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		TextView brushWidthTextView = mSolo.getText("25");
		String brushWidthText = (String) brushWidthTextView.getText();
		assertEquals("Wrong brush width displayed", Integer.valueOf(brushWidthText), Integer.valueOf(25));

		// set brush width
		ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
		assertEquals(progressBars.size(), 1);
		SeekBar strokeWidthBar = (SeekBar) progressBars.get(0);
		assertEquals(strokeWidthBar.getProgress(), 25);

		int newStrokeWidth = 80;
		mSolo.setProgressBar(0, newStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(strokeWidthBar.getProgress(), newStrokeWidth);

		Paint strokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCanvasPaint");
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals(paintStrokeWidth, newStrokeWidth);

		// set brush form
		int squarePictureButton = 0;
		mSolo.clickOnImageButton(squarePictureButton);
		assertTrue("Waiting for set stroke cap SQUARE ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.button_accept));
		assertEquals(strokePaint.getStrokeCap(), Cap.SQUARE);

		// / initialize eraser settings
		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_eraser);
		mSolo.clickOnView(mToolBarButtonTwo);

		// set brush width
		ArrayList<ProgressBar> eraserProgressBars = mSolo.getCurrentProgressBars();
		assertEquals(eraserProgressBars.size(), 1);
		SeekBar eraserStrokeWidthBar = (SeekBar) eraserProgressBars.get(0);
		assertEquals(eraserStrokeWidthBar.getProgress(), newStrokeWidth);

		int eraserStrokeWidth = 60;
		mSolo.setProgressBar(0, eraserStrokeWidth);
		assertTrue("Waiting for set stroke width ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		assertEquals(eraserStrokeWidthBar.getProgress(), eraserStrokeWidth);

		// set brush form
		int roundPictureButton = 1;
		mSolo.clickOnImageButton(roundPictureButton);
		assertTrue("Waiting for set stroke cap ROUND ", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.button_accept));
		Paint eraserStrokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class,
				PaintroidApplication.CURRENT_TOOL, "canvasPaint");
		assertEquals(eraserStrokePaint.getStrokeCap(), Cap.ROUND);

		// / check changes
		Utils.selectTool(mSolo, mToolBarButtonMain, R.string.button_brush);
		Paint lastStrokePaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.CURRENT_TOOL,
				"canvasPaint");
		assertEquals((int) lastStrokePaint.getStrokeWidth(), newStrokeWidth);
		assertEquals(lastStrokePaint.getStrokeCap(), Cap.SQUARE);

	}
}
