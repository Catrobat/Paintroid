package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.implementation.BitmapCommand;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.DrawingSurface;
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

		try {
			mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
		} catch (AssertionError er) {
			mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu_condensed));
		}

		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		Point bottomrightCanvasPoint = new Point(currentDrawingSurfaceBitmap.getWidth() - 1,
				currentDrawingSurfaceBitmap.getHeight() - 1);
		Point originalBottomrightScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				bottomrightCanvasPoint, PaintroidApplication.perspective);

		int widthOverflow = 10;
		int newBitmapHeight = 30;
		float canvasCenterTollerance = 100;

		final Bitmap widthOverflowedBitmap = Bitmap.createBitmap(originalBottomrightScreenPoint.x + widthOverflow,
				newBitmapHeight, Bitmap.Config.ALPHA_8);

		float surfaceScaleBeforeBitmapCommand = PaintroidApplication.perspective.getScale();

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				PaintroidApplication.commandManager.commitCommand(new BitmapCommand(widthOverflowedBitmap, true));
			}
		});

		mSolo.sleep(2000);

		float surfaceScaleAfterBitmapCommand = PaintroidApplication.perspective.getScale();

		assertTrue("Wrong Scale after setting new bitmap",
				surfaceScaleAfterBitmapCommand < surfaceScaleBeforeBitmapCommand);

		mSolo.drag(originalBottomrightScreenPoint.x / 2, originalBottomrightScreenPoint.x / 2,
				originalBottomrightScreenPoint.y / 2, originalBottomrightScreenPoint.y / 2 + canvasCenterTollerance, 1);
		PointF canvasCenter = new PointF((originalBottomrightScreenPoint.x + widthOverflow) / 2, newBitmapHeight / 2);

		mSolo.sleep(1000);
		assertTrue("Center not set",
				PaintroidApplication.drawingSurface.getBitmapColor(canvasCenter) != Color.TRANSPARENT);

	}

}
