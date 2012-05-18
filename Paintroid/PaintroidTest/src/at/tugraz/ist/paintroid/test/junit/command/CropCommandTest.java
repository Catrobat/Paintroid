package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.tugraz.ist.paintroid.command.implementation.CropCommand;

public class CropCommandTest extends CommandTestSetup {

	private int mCropCoordinateXLeft;
	private int mCropCoordinateYTop;
	private int mCropCoordinateXRight;
	private int mCropCoordinateYBottom;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCropCoordinateXLeft = 1;
		mCropCoordinateYTop = 1;
		mCropCoordinateXRight = mBitmapUnderTest.getWidth() - 1;
		mCropCoordinateYBottom = mBitmapUnderTest.getHeight() - 1;
		mCommandUnderTest = new CropCommand(mCropCoordinateXLeft, mCropCoordinateYTop, mCropCoordinateXRight,
				mCropCoordinateYBottom);
		mCommandUnderTestNull = new CropCommand(1, 1, 2, 2);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testIfBitmapIsCropped() {
		int widthOriginal = mBitmapUnderTest.getWidth();
		int heightOriginal = mBitmapUnderTest.getHeight();
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		assertEquals("Cropping failed width not correct ", widthOriginal - mCropCoordinateXLeft
				- (widthOriginal - mCropCoordinateXRight), mCanvasBitmapUnderTest.getWidth());
		assertEquals("Cropping failed height not correct ", heightOriginal - mCropCoordinateYTop
				- (widthOriginal - mCropCoordinateYBottom), mCanvasBitmapUnderTest.getWidth());

	}

}
