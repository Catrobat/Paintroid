package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableRow;

public class RectangleFillToolIntegrationTest extends BaseIntegrationTestClass {

	private static final String TOOL_MEMBER_WIDTH = "mBoxWidth";
	private static final String TOOL_MEMBER_HEIGHT = "mBoxHeight";
	private static final String TOOL_MEMBER_POSITION = "mToolPosition";
	private static final String TOOL_MEMBER_BITMAP = "mDrawingBitmap";
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
	public void testFilledRectIsCreated() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
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

		PointF point = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mRectangleFillTool,
				TOOL_MEMBER_POSITION);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		PointF pointOnBitmap = new PointF(point.x, (point.y + (rectHeight / 4.0f)));
		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.CURRENT_PERSPECTIVE.convertFromScreenToCanvas(pointOnScreen);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to draw rectangle
		Log.i(PaintroidApplication.TAG, "testFilledRectIsDrawnOnBitmap x:" + pointOnScreen.x);
		Log.i(PaintroidApplication.TAG, "testFilledRectIsDrawnOnBitmap y:" + pointOnScreen.y);

		Log.i(PaintroidApplication.TAG, "testFilledRectIsDrawnOnBitmap x:" + pointOnBitmap.x);
		Log.i(PaintroidApplication.TAG, "testFilledRectIsDrawnOnBitmap y:" + pointOnBitmap.y);

		mSolo.sleep(2000);
		mSolo.goBack();
		mSolo.sleep(2000);

		int colorAfterDrawing = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(pointOnBitmap);
		int colorPickerColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor,
				colorAfterDrawing);
	}

	@Test
	public void testRectOnBitmapHasSameColorAsInColorPickerAfterColorChange() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int colorPickerColorBeforeChange = mToolbar.getCurrentTool().getDrawPaint().getColor();
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.ok), 1, TIMEOUT * 2));

		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(50);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.ok));

		int colorPickerColorAfterChange = mToolbar.getCurrentTool().getDrawPaint().getColor();
		assertTrue("Colors should not be the same", colorPickerColorAfterChange != colorPickerColorBeforeChange);

		selectTool(ToolType.RECT);
		int colorInRectangleTool = mToolbar.getCurrentTool().getDrawPaint().getColor();
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangleTool);

		Tool mRectangleFillTool = mToolbar.getCurrentTool();
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				mRectangleFillTool, TOOL_MEMBER_BITMAP);
		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangle);
	}

	@Test
	public void testFilledRectColorIsBlackIfSetToTransparent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int colorPickerColorBeforeChange = mToolbar.getCurrentTool().getDrawPaint().getColor();
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.ok), 1, TIMEOUT * 2));

		String tabRgbName = mSolo.getString(R.string.color_rgb).substring(0, 5);
		mSolo.clickOnText(tabRgbName);
		mSolo.sleep(500);
		ProgressBar alphaBar = (ProgressBar) mSolo.getView(R.id.color_rgb_seekAlpha);
		mSolo.setProgressBar(alphaBar, 0);
		mSolo.sleep(1000);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.ok));

		int colorPickerColorAfterChange = mToolbar.getCurrentTool().getDrawPaint().getColor();
		assertTrue("Colors should not be the same", colorPickerColorAfterChange != colorPickerColorBeforeChange);
		assertEquals("Color should be transparent", colorPickerColorAfterChange, Color.TRANSPARENT);

		selectTool(ToolType.RECT);
		int colorInRectangleTool = mToolbar.getCurrentTool().getDrawPaint().getColor();
		assertEquals("Colors should be black", colorInRectangleTool, Color.BLACK);

		Tool mRectangleFillTool = mToolbar.getCurrentTool();
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				mRectangleFillTool, TOOL_MEMBER_BITMAP);
		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be black", colorInRectangle, Color.BLACK);
	}

	@Test
	public void testFilledRectChangesColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.RECT);
		Tool mRectangleFillTool = mToolbar.getCurrentTool();
		int colorInRectangleTool = mToolbar.getCurrentTool().getDrawPaint().getColor();
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				mRectangleFillTool, TOOL_MEMBER_BITMAP);
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);

		// change color and check
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.ok), 1, TIMEOUT * 2));

		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(50);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.ok));
		mSolo.sleep(2000);

		int colorInRectangleToolAfter = mToolbar.getCurrentTool().getDrawPaint().getColor();
		Bitmap drawingBitmapAfter = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				mRectangleFillTool, TOOL_MEMBER_BITMAP);
		int colorInRectangleAfter = drawingBitmapAfter.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertTrue("Colors should have changed", colorInRectangle != colorInRectangleAfter);
		assertTrue("Colors should have changed", colorInRectangleTool != colorInRectangleToolAfter);
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);
	}

}
