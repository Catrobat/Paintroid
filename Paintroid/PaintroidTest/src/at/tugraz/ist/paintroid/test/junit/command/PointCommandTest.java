package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.command.implementation.PointCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;

public class PointCommandTest extends AndroidTestCase {

	protected PointCommand mPointCommandUnderTest;
	protected PointCommand mPointCommandUnderTestNull;
	protected PointF mPointUnderTest;
	protected Paint mPaintUnderTest;
	protected Bitmap mCanvasBitmapUnderTest;
	protected Canvas mCanvasUnderTest;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCanvasUnderTest = new Canvas();
		mCanvasBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		mCanvasUnderTest.setBitmap(mCanvasBitmapUnderTest);
		mPaintUnderTest = new Paint();
		mPaintUnderTest.setColor(Color.BLUE);
		mPaintUnderTest.setStrokeWidth(0);
		mPaintUnderTest.setStrokeCap(Cap.BUTT);
		mPointUnderTest = new PointF(mCanvasBitmapUnderTest.getWidth() / 2, mCanvasBitmapUnderTest.getHeight() / 2);
		mPointCommandUnderTest = new PointCommand(mPaintUnderTest, mPointUnderTest);
		mPointCommandUnderTestNull = new PointCommand(null, null);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		mPointCommandUnderTest = null;
		mPointCommandUnderTestNull = null;
		mPointUnderTest = null;
		mPaintUnderTest = null;
		mCanvasBitmapUnderTest.recycle();
		mCanvasBitmapUnderTest = null;
		mCanvasUnderTest = null;
	}

	@Test
	public void testRun() {
		Bitmap expectedBitmap = mCanvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		expectedBitmap.setPixel((int) mPointUnderTest.x, (int) mPointUnderTest.y, mPaintUnderTest.getColor());
		mPointCommandUnderTest.run(mCanvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(expectedBitmap, mCanvasBitmapUnderTest);
		mPointCommandUnderTestNull.run(null, null);
		mPointCommandUnderTestNull.run(mCanvasUnderTest, null);
		expectedBitmap.recycle();
		expectedBitmap = null;
	}
	/*
	 * @Test public void testPointCommand() { fail("Not yet implemented"); }
	 */
}
