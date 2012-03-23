package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.command.implementation.PathCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;

public class PathCommandTest extends AndroidTestCase {

	protected PathCommand mPathCommandUnderTest;
	protected PathCommand mPathCommandUnderTestNull;
	protected Paint mPaintUnderTest;
	protected Path mPathUnderTest;
	protected Canvas mCanvasUnderTest;
	protected Bitmap mCanvasBitmapUnderTest;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCanvasUnderTest = new Canvas();
		mPaintUnderTest = new Paint();
		mPathUnderTest = new Path();
		mCanvasBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		mCanvasUnderTest.setBitmap(mCanvasBitmapUnderTest);
		mPaintUnderTest.setColor(Color.BLUE);
		mPaintUnderTest.setStrokeWidth(0);
		mPathUnderTest.moveTo(0, 0);
		mPathUnderTest.lineTo(0, 9);
		// mPathCommandUnderTest = new PathCommand(null, null);
		mPathCommandUnderTest = new PathCommand(mPaintUnderTest, mPathUnderTest);
		mPathCommandUnderTestNull = new PathCommand(null, null);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		mPathCommandUnderTest = null;
		mPathCommandUnderTestNull = null;
		mPaintUnderTest = null;
		mPathUnderTest = null;
		mCanvasUnderTest = null;
		mCanvasBitmapUnderTest.recycle();
		mCanvasBitmapUnderTest = null;
	}

	@Test
	public void testRun() {
		Bitmap expectedBitmap = mCanvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		int color = mPaintUnderTest.getColor();
		int width = expectedBitmap.getWidth();
		int height = expectedBitmap.getHeight();
		for (int y = 0; y < height; y++)
			expectedBitmap.setPixel(y, 0, color);
		mPathCommandUnderTest.run(mCanvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(expectedBitmap, mCanvasBitmapUnderTest);
		mPathCommandUnderTestNull.run(null, null);
		mPathCommandUnderTestNull.run(mCanvasUnderTest, null);
		expectedBitmap.recycle();
		expectedBitmap = null;
	}

}
