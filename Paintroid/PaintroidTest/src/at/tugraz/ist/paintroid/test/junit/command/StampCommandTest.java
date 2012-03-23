package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.command.implementation.StampCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;

public class StampCommandTest extends AndroidTestCase {

	protected StampCommand mStampCommandUnderTest;
	protected StampCommand mStampCommandUnderTestNull;
	protected Paint mPaintUnderTest;
	protected Bitmap mCanvasBitmapUnderTest;
	protected Bitmap mStampBitmapUnderTest;
	protected Canvas mCanvasUnderTest;
	protected Point mPointUnderTest;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCanvasUnderTest = new Canvas();
		mPointUnderTest = new Point();
		mCanvasBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		mCanvasBitmapUnderTest.eraseColor(Color.RED);
		mStampBitmapUnderTest = mCanvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		mStampBitmapUnderTest.eraseColor(Color.CYAN);
		mCanvasUnderTest.setBitmap(mCanvasBitmapUnderTest);
		mPointUnderTest = new Point(mCanvasBitmapUnderTest.getWidth() / 2, mCanvasBitmapUnderTest.getHeight() / 2);
		mStampCommandUnderTest = new StampCommand(mStampBitmapUnderTest, mPointUnderTest,
				mCanvasBitmapUnderTest.getWidth(), mCanvasBitmapUnderTest.getHeight(), 0);
		mStampCommandUnderTestNull = new StampCommand(null, null, 0, 0, 0);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		mStampCommandUnderTest = null;
		mStampCommandUnderTestNull = null;
		mPaintUnderTest = null;
		mStampBitmapUnderTest.recycle();
		mCanvasBitmapUnderTest.recycle();
		mCanvasBitmapUnderTest = null;
		mStampBitmapUnderTest = null;
		mCanvasUnderTest = null;
		mPointUnderTest = null;
	}

	@Test
	public void testRun() {
		mStampCommandUnderTest.run(mCanvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(mStampBitmapUnderTest, mCanvasBitmapUnderTest);
		mStampCommandUnderTestNull.run(null, null);

		try {
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mStampCommandUnderTest, "mBitmap"));
			assertNotNull(PrivateAccess.getMemberValue(BaseCommand.class, mStampCommandUnderTest, "mStoredBitmap"));
		} catch (Exception e) {
			fail("Failed with exception " + e.toString());
		}
		mStampCommandUnderTest.run(mCanvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(mStampBitmapUnderTest, mCanvasBitmapUnderTest);
		mStampCommandUnderTestNull.run(null, null);
	}

	@Test
	public void testRunRotateStamp() {
		mStampBitmapUnderTest.setPixel(0, 0, Color.GREEN);
		mStampCommandUnderTest = new StampCommand(mStampBitmapUnderTest, mPointUnderTest,
				mCanvasBitmapUnderTest.getWidth(), mCanvasBitmapUnderTest.getHeight(), 180);
		mStampCommandUnderTest.run(mCanvasUnderTest, null);
		mStampBitmapUnderTest.setPixel(0, 0, Color.CYAN);
		mStampBitmapUnderTest.setPixel(mStampBitmapUnderTest.getWidth() - 1, mStampBitmapUnderTest.getHeight() - 1,
				Color.GREEN);
		PaintroidAsserts.assertBitmapEquals(mStampBitmapUnderTest, mCanvasBitmapUnderTest);
		try {
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mStampCommandUnderTest, "mBitmap"));
			assertNotNull(PrivateAccess.getMemberValue(BaseCommand.class, mStampCommandUnderTest, "mStoredBitmap"));
		} catch (Exception e) {
			fail("Failed with exception " + e.toString());
		}
	}
}
