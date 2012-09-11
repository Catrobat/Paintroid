package at.tugraz.ist.paintroid.test.integration.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.test.integration.BaseIntegrationTestClass;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
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
		try {
			Bitmap drawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
			drawingSurfaceBitmap.setPixel(xPoint, yPoint, Color.BLACK);
			PrivateAccess.setMemberValue(DrawingSurfaceImplementation.class, PaintroidApplication.DRAWING_SURFACE,
					"mWorkingBitmap", drawingSurfaceBitmap);
		} catch (Exception whatever) {
			whatever.printStackTrace();
			fail("exception: " + whatever.toString());
		}
		// mSolo.clickOnScreen(xPoint, yPoint + Utils.getStatusbarHeigt(getActivity()));
		// mSolo.sleep(500);

		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixel);

		selectTool(ToolType.FLIP);
		mSolo.clickOnView(mMenuBottomParameter1);
		yPoint = PaintroidApplication.DRAWING_SURFACE.getBitmapHeight() - yPoint - 1;
		mSolo.sleep(500);
		pixel = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixel);
	}

	public void testVerticalFlip() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int xPoint = OFFSET;
		int yPoint = mScreenHeight / 2;

		try {
			Bitmap drawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
			drawingSurfaceBitmap.setPixel(xPoint, yPoint, Color.BLACK);
			PrivateAccess.setMemberValue(DrawingSurfaceImplementation.class, PaintroidApplication.DRAWING_SURFACE,
					"mWorkingBitmap", drawingSurfaceBitmap);
		} catch (Exception whatever) {
			whatever.printStackTrace();
			fail("exception: " + whatever.toString());
		}
		// mSolo.clickOnScreen(xPoint, yPoint + Utils.getStatusbarHeigt(getActivity()));

		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(xPoint, yPoint));
		assertEquals("pixel should be black", Color.BLACK, pixel);

		selectTool(ToolType.FLIP);
		mSolo.clickOnView(mMenuBottomParameter2);
		xPoint = PaintroidApplication.DRAWING_SURFACE.getBitmapWidth() - xPoint - 1;
		mSolo.sleep(200);
		pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(xPoint, yPoint);
		assertEquals("pixel should be black", Color.BLACK, pixel);
	}
}
