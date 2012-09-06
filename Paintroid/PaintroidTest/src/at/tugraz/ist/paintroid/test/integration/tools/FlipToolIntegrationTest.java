package at.tugraz.ist.paintroid.test.integration.tools;

import android.graphics.Color;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.test.integration.BaseIntegrationTestClass;
import at.tugraz.ist.paintroid.test.integration.Utils;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class FlipToolIntegrationTest extends BaseIntegrationTestClass {

	private static final int OFFSET = 150;

	public FlipToolIntegrationTest() throws Exception {
		super();
	}

	public void testHorizontalFlip() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int xPoint = mScreenWidth / 2;
		int yPoint = OFFSET;
		mSolo.clickOnScreen(xPoint, yPoint + Utils.getStatusbarHeigt(getActivity()));

		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(xPoint, yPoint);
		assertEquals("pixel should be black", Color.BLACK, pixel);

		selectTool(ToolType.FLIP);
		mSolo.clickOnView(mMenuBottomParameter1);
		yPoint = mScreenHeight - yPoint;
		mSolo.sleep(200);
		pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(xPoint, yPoint);
		assertEquals("pixel should be black", Color.BLACK, pixel);
	}

	public void testVerticalFlip() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int xPoint = OFFSET;
		int yPoint = mScreenHeight / 2;
		mSolo.clickOnScreen(xPoint, yPoint + Utils.getStatusbarHeigt(getActivity()));

		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(xPoint, yPoint);
		assertEquals("pixel should be black", Color.BLACK, pixel);

		selectTool(ToolType.FLIP);
		mSolo.clickOnView(mMenuBottomParameter2);
		xPoint = mScreenWidth - xPoint;
		mSolo.sleep(200);
		pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(xPoint, yPoint);
		assertEquals("pixel should be black", Color.BLACK, pixel);
	}
}
