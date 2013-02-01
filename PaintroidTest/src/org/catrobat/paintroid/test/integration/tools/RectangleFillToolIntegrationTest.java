package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.ui.Toolbar;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;
import org.junit.Before;
import org.junit.Test;

import android.graphics.PointF;
import android.util.Log;

public class RectangleFillToolIntegrationTest extends BaseIntegrationTestClass {

	private static final String TOOL_MEMBER_WIDTH = "mBoxWidth";
	private static final String TOOL_MEMBER_HEIGHT = "mBoxHeight";
	private static final String TOOL_MEMBER_POSITION = "mToolPosition";
	protected Toolbar mToolbar;

	public RectangleFillToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		resetBrush();
		try {
			mToolbar = (Toolbar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(), "mToolbar");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testFilledRectExists() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.RECT);
		Tool mRectangleFillTool = mToolbar.getCurrentTool();
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mRectangleFillTool,
				TOOL_MEMBER_POSITION);

		assertTrue("Width should not be zero", rectWidth != 0.0f);
		assertTrue("Width should not be zero", rectHeight != 0.0f);
		assertNotNull("Position should not be NULL", rectPosition);
	}

	@Test
	public void testFilledRectIsDrawnOnBitmap() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		selectTool(ToolType.RECT);
		Tool mRectangleFillTool = mToolbar.getCurrentTool();

		PointF pointOnBitmap = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mRectangleFillTool,
				TOOL_MEMBER_POSITION);
		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.CURRENT_PERSPECTIVE.convertFromScreenToCanvas(pointOnScreen);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to draw rectangle
		Log.i(PaintroidApplication.TAG, "testFilledRectIsDrawnOnBitmap x:" + pointOnScreen.x);
		Log.i(PaintroidApplication.TAG, "testFilledRectIsDrawnOnBitmap y:" + pointOnScreen.y);

		Log.i(PaintroidApplication.TAG, "testFilledRectIsDrawnOnBitmap x:" + pointOnBitmap.x);
		Log.i(PaintroidApplication.TAG, "testFilledRectIsDrawnOnBitmap y:" + pointOnBitmap.y);

		mSolo.sleep(3000);

		int colorAfterDrawing = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(pointOnBitmap);
		int colorPickerColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor,
				colorAfterDrawing);
	}

	@Test
	public void testFilledRectHasSameColorAsInColorPickerAfterChange() {

	}

	@Test
	public void testFilledRectColorIsBlackIfSetToTransparent() {
	}

	@Test
	public void testFilledRectChangesColor() {
	}

}
