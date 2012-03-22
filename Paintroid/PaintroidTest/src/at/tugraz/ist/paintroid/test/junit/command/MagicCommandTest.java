package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.command.implementation.MagicCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;

public class MagicCommandTest extends AndroidTestCase {

	protected MagicCommand mMagicCommandUnderTest;
	protected Paint mPaintUnderTest;
	protected PointF mPointUnderTest;
	protected Canvas mCanvasUnderTest;
	protected Bitmap mBitmapUnderTest;
	protected PrivateAccess mPrivateAccess;
	protected int mColorUnderTest = Color.BLUE;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mPrivateAccess = new PrivateAccess();
		mPaintUnderTest = new Paint();
		mPointUnderTest = new PointF();
		mPointUnderTest.x = 5;
		mPointUnderTest.y = 5;
		mBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		mBitmapUnderTest.eraseColor(mColorUnderTest - 1);
		mMagicCommandUnderTest = new MagicCommand(mPaintUnderTest, mPointUnderTest);
		mPaintUnderTest.setColor(mColorUnderTest);
	}

	@Test
	public void testRun() {
		try {
			mMagicCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
			Bitmap expectedBitmap = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
			expectedBitmap.eraseColor(mColorUnderTest);
			PaintroidAsserts.assertBitmapEquals(expectedBitmap, mBitmapUnderTest);
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}

	@Test
	public void testRunPointOutOfBitmapBounds() {
		try {
			mBitmapUnderTest = Bitmap.createBitmap((int) (mPointUnderTest.x - 1), (int) (mPointUnderTest.y - 1),
					Config.ARGB_8888);
			mMagicCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}

	@Test
	public void testMagicCommand() {
		try {
			Point pointToTest = new Point((Point) mPrivateAccess.getMemberValue(MagicCommand.class,
					mMagicCommandUnderTest, "mColorPixel"));
			assertNotNull(pointToTest);
			assertEquals((int) mPointUnderTest.x, pointToTest.x);
			assertEquals((int) mPointUnderTest.y, pointToTest.y);
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}
}
