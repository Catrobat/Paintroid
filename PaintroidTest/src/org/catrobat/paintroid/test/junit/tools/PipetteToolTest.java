package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.junit.stubs.DrawingSurfaceStub;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.PipetteTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.PointF;

public class PipetteToolTest extends BaseToolTest {

	private final int X_COORDINATE_RED = 1;
	private final int X_COORDINATE_GREEN = 3;
	private final int X_COORDINATE_BLUE = 5;
	private final int X_COORDINATE_PART_TRANSPARENT = 7;
	private DrawingSurface mOriginalDrawingSurface = null;

	public PipetteToolTest() {
		super();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		mToolToTest = new PipetteTool(getActivity(), Tool.ToolType.PIPETTE);
		super.setUp();
		DrawingSurfaceStub drawingSurfaceStub = new DrawingSurfaceStub();
		drawingSurfaceStub.mBitmap = Bitmap.createBitmap(10, 1, Config.ARGB_8888);
		drawingSurfaceStub.mBitmap.setPixel(X_COORDINATE_RED, 0, Color.RED);
		drawingSurfaceStub.mBitmap.setPixel(X_COORDINATE_GREEN, 0, Color.GREEN);
		drawingSurfaceStub.mBitmap.setPixel(X_COORDINATE_BLUE, 0, Color.BLUE);
		drawingSurfaceStub.mBitmap.setPixel(X_COORDINATE_PART_TRANSPARENT, 0, 0xAAAAAAAA);
		mOriginalDrawingSurface = PaintroidApplication.DRAWING_SURFACE;
		PaintroidApplication.DRAWING_SURFACE = drawingSurfaceStub;
	}

	@Override
	@After
	public void tearDown() {
		DrawingSurfaceStub drawingSurfaceStub = (DrawingSurfaceStub) PaintroidApplication.DRAWING_SURFACE;
		PaintroidApplication.DRAWING_SURFACE = mOriginalDrawingSurface;
		drawingSurfaceStub.mBitmap.recycle();
		drawingSurfaceStub.mBitmap = null;
		Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();
		try {
			super.tearDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testHandleDown() {
		mToolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		assertEquals("Paint color has not changed", Color.RED, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@Test
	public void testHandleMove() {
		mToolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		assertEquals("Paint color has not changed", Color.RED, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_RED + 1, 0));
		assertEquals("Paint color has not changed", Color.TRANSPARENT, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_GREEN, 0));
		assertEquals("Paint color has not changed", Color.GREEN, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@Test
	public void testHandleUp() {
		mToolToTest.handleUp(new PointF(X_COORDINATE_BLUE, 0));
		assertEquals("Paint color has not changed", Color.BLUE, mToolToTest.getDrawPaint().getColor());
		mToolToTest.handleUp(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));
		assertEquals("Paint color has not changed", 0xAAAAAAAA, mToolToTest.getDrawPaint().getColor());
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();
		assertEquals(ToolType.PIPETTE, toolType);
	}

	@Test
	public void testShouldReturnCorrectResourceForForTopButtonFourIfColorIsTransparent() {
		mToolToTest.handleUp(new PointF(0, 0));
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Transparend shuld be displayed", R.drawable.checkeredbg_repeat, resource);
	}

	@Test
	public void testShouldReturnCorrectColorForForTopButtonFourIfColorIsTransparent() {
		mToolToTest.handleUp(new PointF(0, 0));
		int color = mToolToTest.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Transparent colour expected", Color.TRANSPARENT, color);
	}

	@Test
	public void testShouldReturnCorrectColorForForTopButtonFourIfColorIsRed() {
		mToolToTest.handleUp(new PointF(X_COORDINATE_RED, 0));
		int color = mToolToTest.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Red colour expected", Color.RED, color);
	}

	@Test
	public void testShouldReturnCorrectResourceForForBottomButtonOne() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
		assertEquals("Transparend shuld be displayed", R.drawable.icon_menu_no_icon, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForForBottomButtonTwo() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
		assertEquals("Transparend shuld be displayed", R.drawable.icon_menu_no_icon, resource);
	}

}
