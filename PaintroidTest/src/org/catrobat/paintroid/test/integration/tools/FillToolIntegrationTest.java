package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Statusbar;
import org.junit.Before;

import android.graphics.PointF;
import android.widget.Button;
import android.widget.TableRow;

public class FillToolIntegrationTest extends BaseIntegrationTestClass {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mStatusbar";

	protected Statusbar mStatusbar;

	public FillToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		resetBrush();
		try {
			mStatusbar = (Statusbar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(),
					PRIVATE_ACCESS_STATUSBAR_NAME);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void testBitmapIsFilled() throws InterruptedException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		selectTool(ToolType.FILL);

		int colorToFill = mStatusbar.getCurrentTool().getDrawPaint().getColor();
		DrawingSurface drawingSurface = (DrawingSurface) getActivity().findViewById(
				R.id.drawingSurfaceView);
		int xCoord = 100;
		int yCoord = 200;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to fill the bitmap
		mSolo.sleep(5000);
		int colorAfterFill = drawingSurface.getBitmapColor(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);
	}

	public void testOnlyFillInnerArea() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		DrawingSurface drawingSurface = (DrawingSurface) getActivity().findViewById(
				R.id.drawingSurfaceView);

		assertEquals("BrushTool should be selected", ToolType.BRUSH, mStatusbar.getCurrentTool().getToolType());
		int colorToDrawBorder = mStatusbar.getCurrentTool().getDrawPaint().getColor();

		int checkPointXCoord = 300;
		int checkPointYCoord = 500;
		PointF pointOnBitmap = new PointF(checkPointXCoord, checkPointYCoord);
		int checkPointStartColor = drawingSurface.getBitmapColor(pointOnBitmap);
		assertFalse(colorToDrawBorder == checkPointStartColor);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);

		PointF leftPointOnBitmap = new PointF(checkPointXCoord - 150, checkPointYCoord);
		PointF leftPointOnScreen = new PointF(leftPointOnBitmap.x, leftPointOnBitmap.y);
		PointF upperPointOnScreen = new PointF(checkPointXCoord, checkPointYCoord - 150);
		PointF rightPointOnScreen = new PointF(checkPointXCoord + 150, checkPointYCoord);
		PointF bottomPointOnScreen = new PointF(checkPointXCoord, checkPointYCoord + 150);

		PaintroidApplication.perspective.convertFromScreenToCanvas(leftPointOnScreen);
		PaintroidApplication.perspective.convertFromScreenToCanvas(upperPointOnScreen);
		PaintroidApplication.perspective.convertFromScreenToCanvas(rightPointOnScreen);
		PaintroidApplication.perspective.convertFromScreenToCanvas(bottomPointOnScreen);

		mSolo.drag(leftPointOnScreen.x, upperPointOnScreen.x, leftPointOnScreen.y, upperPointOnScreen.y, 1);
		mSolo.drag(upperPointOnScreen.x, rightPointOnScreen.x, upperPointOnScreen.y, rightPointOnScreen.y, 1);
		mSolo.drag(rightPointOnScreen.x, bottomPointOnScreen.x, rightPointOnScreen.y, bottomPointOnScreen.y, 1);
		mSolo.drag(bottomPointOnScreen.x, leftPointOnScreen.x, bottomPointOnScreen.y, leftPointOnScreen.y, 1);

		selectTool(ToolType.FILL);
		// change color
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for Color Chooser", mSolo.waitForText(mSolo.getString(R.string.done), 1, TIMEOUT * 2));

		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(50);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.done));

		int colorToFill = mStatusbar.getCurrentTool().getDrawPaint().getColor();
		assertFalse(colorToDrawBorder == colorToFill);
		assertFalse(checkPointStartColor == colorToFill);

		// to fill the bitmap
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.sleep(5000);

		int colorAfterFill = drawingSurface.getBitmapColor(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);

		int outsideColorAfterFill = drawingSurface.getBitmapColor(new PointF(leftPointOnBitmap.x - 30,
				leftPointOnBitmap.y));
		assertFalse("Pixel color should be different", colorToFill == outsideColorAfterFill);
	}
}
