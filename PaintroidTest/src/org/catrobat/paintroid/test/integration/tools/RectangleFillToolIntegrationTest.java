package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.ui.Statusbar;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.widget.Button;
import android.widget.TableRow;

public class RectangleFillToolIntegrationTest extends BaseIntegrationTestClass {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mStatusbar";

	private static final String TOOL_MEMBER_WIDTH = "mBoxWidth";
	private static final String TOOL_MEMBER_HEIGHT = "mBoxHeight";
	private static final String TOOL_MEMBER_POSITION = "mToolPosition";
	private static final String TOOL_MEMBER_BITMAP = "mDrawingBitmap";
	protected Statusbar mStatusbar;

	public RectangleFillToolIntegrationTest() throws Exception {
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

	@Test
	public void testFilledRectIsCreated() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.RECT);
		Tool mRectangleFillTool = mStatusbar.getCurrentTool();
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
		Tool mRectangleFillTool = mStatusbar.getCurrentTool();

		PointF point = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mRectangleFillTool,
				TOOL_MEMBER_POSITION);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool,
				TOOL_MEMBER_HEIGHT);
		PointF pointOnBitmap = new PointF(point.x, (point.y + (rectHeight / 4.0f)));
		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.CURRENT_PERSPECTIVE.convertFromScreenToCanvas(pointOnScreen);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y); // to draw rectangle

		mSolo.sleep(50);
		mSolo.goBack();
		mSolo.sleep(50);

		int colorAfterDrawing = PaintroidApplication.DRAWING_SURFACE.getBitmapColor(pointOnBitmap);
		int colorPickerColor = mStatusbar.getCurrentTool().getDrawPaint().getColor();
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor,
				colorAfterDrawing);
	}

	@Test
	public void testRectOnBitmapHasSameColorAsInColorPickerAfterColorChange() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int colorPickerColorBeforeChange = mStatusbar.getCurrentTool().getDrawPaint().getColor();
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.done), 1, TIMEOUT * 2));

		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(50);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.done));

		int colorPickerColorAfterChange = mStatusbar.getCurrentTool().getDrawPaint().getColor();
		assertTrue("Colors should not be the same", colorPickerColorAfterChange != colorPickerColorBeforeChange);

		selectTool(ToolType.RECT);
		int colorInRectangleTool = mStatusbar.getCurrentTool().getDrawPaint().getColor();
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangleTool);

		Tool mRectangleFillTool = mStatusbar.getCurrentTool();
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
	public void testFilledRectChangesColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.RECT);
		Tool mRectangleFillTool = mStatusbar.getCurrentTool();
		int colorInRectangleTool = mStatusbar.getCurrentTool().getDrawPaint().getColor();
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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForText(mSolo.getString(R.string.done), 1, TIMEOUT * 2));

		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(50);
		mSolo.clickOnButton(getActivity().getResources().getString(R.string.done));
		mSolo.sleep(50);

		int colorInRectangleToolAfter = mStatusbar.getCurrentTool().getDrawPaint().getColor();
		Bitmap drawingBitmapAfter = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				mRectangleFillTool, TOOL_MEMBER_BITMAP);
		int colorInRectangleAfter = drawingBitmapAfter.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertTrue("Colors should have changed", colorInRectangle != colorInRectangleAfter);
		assertTrue("Colors should have changed", colorInRectangleTool != colorInRectangleToolAfter);
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);
	}
}
