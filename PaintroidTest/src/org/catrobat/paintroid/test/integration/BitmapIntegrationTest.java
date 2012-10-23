package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.BitmapCommand;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;

public class BitmapIntegrationTest extends BaseIntegrationTestClass {

	public BitmapIntegrationTest() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testCenterBitmapSimulateLoad() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
				PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");

		Point topleftCanvasPoint = new Point(0, 0);
		Point bottomrightCanvasPoint = new Point(currentDrawingSurfaceBitmap.getWidth() - 1,
				currentDrawingSurfaceBitmap.getHeight() - 1);
		Point originalTopleftScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				topleftCanvasPoint, PaintroidApplication.CURRENT_PERSPECTIVE);
		Point originalBottomrightScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				bottomrightCanvasPoint, PaintroidApplication.CURRENT_PERSPECTIVE);

		assertEquals("Canvas and screen topleft coordinates are not the same", topleftCanvasPoint,
				originalTopleftScreenPoint);
		assertEquals("Canvas and screen bottomright coordinates are not the same ", bottomrightCanvasPoint,
				originalBottomrightScreenPoint);

		int widthOverflow = 10;
		int newBitmapHeight = 30;
		float canvasCenterTollerance = 100;

		Bitmap widthOverflowedBitmap = Bitmap.createBitmap(originalBottomrightScreenPoint.x + widthOverflow,
				newBitmapHeight, Bitmap.Config.ALPHA_8);

		float surfaceScaleBeforeBitmapCommand = PaintroidApplication.CURRENT_PERSPECTIVE.getScale();

		PaintroidApplication.COMMAND_MANAGER.commitCommand(new BitmapCommand(widthOverflowedBitmap, true));
		mSolo.sleep(2000);

		float surfaceScaleAfterBitmapCommand = PaintroidApplication.CURRENT_PERSPECTIVE.getScale();

		assertTrue("Wrong Scale after setting new bitmap",
				surfaceScaleAfterBitmapCommand < surfaceScaleBeforeBitmapCommand);

		mSolo.drag(originalBottomrightScreenPoint.x / 2, originalBottomrightScreenPoint.x / 2,
				originalBottomrightScreenPoint.y / 2, originalBottomrightScreenPoint.y / 2 + canvasCenterTollerance, 1);
		PointF canvasCenter = new PointF((originalBottomrightScreenPoint.x + widthOverflow) / 2, newBitmapHeight / 2);

		mSolo.sleep(1000);
		assertTrue("Center not set",
				PaintroidApplication.DRAWING_SURFACE.getBitmapColor(canvasCenter) != Color.TRANSPARENT);

	}

}
