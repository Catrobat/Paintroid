package org.catrobat.paintroid.test.junit.command;

import org.catrobat.paintroid.test.junit.stubs.DrawingSurfaceStub;
import org.junit.Before;

import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.command.implementation.FlipCommand;
import at.tugraz.ist.paintroid.command.implementation.FlipCommand.FlipDirection;

public class FlipCommandTest extends CommandTestSetup {

	private int mBitmapHeigt;
	private int mBitmapWidth;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mBitmapHeigt = mBitmapUnderTest.getHeight();
		mBitmapWidth = mBitmapUnderTest.getWidth();
		PaintroidApplication.DRAWING_SURFACE = new DrawingSurfaceStub();
	}

	public void testVerticalFlip() {
		mCommandUnderTest = new FlipCommand(FlipDirection.FLIP_VERTICAL);
		mBitmapUnderTest.setPixel(0, mBitmapHeigt / 2, PAINT_BASE_COLOR);
		mCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(mBitmapWidth - 1, mBitmapWidth / 2);
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

	public void testHorizontalFlip() {
		mCommandUnderTest = new FlipCommand(FlipDirection.FLIP_HORIZONTAL);
		mBitmapUnderTest.setPixel(mBitmapWidth / 2, 0, PAINT_BASE_COLOR);
		mCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(mBitmapWidth / 2, mBitmapWidth - 1);
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

}
