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
import android.graphics.PointF;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.command.implementation.MagicCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;

public class MagicCommandTest extends AndroidTestCase {

	protected MagicCommand mMagicCommandUnderTest;
	protected MagicCommand mMagicCommandUnderTestNull;
	protected Paint mPaintUnderTest;
	protected PointF mPointUnderTest;
	protected Canvas mCanvasUnderTest;
	protected Bitmap mBitmapUnderTest;
	// protected PrivateAccess PrivateAccess;
	protected int mColorUnderTest = Color.BLUE;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		// PrivateAccess = new PrivateAccess();
		mPaintUnderTest = new Paint();
		mPointUnderTest = new PointF();
		mCanvasUnderTest = null;
		mBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		mBitmapUnderTest.eraseColor(mColorUnderTest - 1);
		// mCanvasUnderTest.setBitmap(mBitmapUnderTest);
		mPointUnderTest.x = mBitmapUnderTest.getWidth() / 2;
		mPointUnderTest.y = mBitmapUnderTest.getHeight() / 2;
		mPaintUnderTest.setColor(mColorUnderTest);
		mMagicCommandUnderTest = new MagicCommand(mPaintUnderTest, mPointUnderTest);
		mMagicCommandUnderTestNull = new MagicCommand(null, null);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		mBitmapUnderTest.recycle();
		mBitmapUnderTest = null;
		mMagicCommandUnderTest = null;
		mMagicCommandUnderTestNull = null;
		mPaintUnderTest = null;
		mPointUnderTest = null;
		mCanvasUnderTest = null;
	}

	@Test
	public void testRun() {
		try {
			// PrivateAccess.setMemberValue(BaseCommand.class, mMagicCommandUnderTest, "mPaint", mPaintUnderTest);
			Bitmap expectedBitmap = mBitmapUnderTest.copy(Config.ARGB_8888, true);
			expectedBitmap.eraseColor(mColorUnderTest);
			mMagicCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
			mMagicCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
			PaintroidAsserts.assertBitmapEquals(expectedBitmap, mBitmapUnderTest);
			mMagicCommandUnderTestNull.run(null, null);
			mMagicCommandUnderTestNull.run(null, mBitmapUnderTest);
			mMagicCommandUnderTestNull.run(mCanvasUnderTest, null);
			mMagicCommandUnderTestNull.run(mCanvasUnderTest, mBitmapUnderTest);
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
			mMagicCommandUnderTestNull.run(null, null);
			mMagicCommandUnderTestNull.run(null, mBitmapUnderTest);
			mMagicCommandUnderTestNull.run(mCanvasUnderTest, null);
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}

	@Test
	public void testRunReplaceAllExceptOne() {
		mBitmapUnderTest.setPixel(0, 0, mColorUnderTest + 1);
		Bitmap expectedBitmap = Bitmap.createBitmap(mBitmapUnderTest.getWidth(), mBitmapUnderTest.getHeight(),
				Config.ARGB_8888);
		expectedBitmap.eraseColor(mColorUnderTest);
		expectedBitmap.setPixel(0, 0, mColorUnderTest + 1);
		mMagicCommandUnderTest.run(null, mBitmapUnderTest);
		PaintroidAsserts.assertBitmapEquals(expectedBitmap, mBitmapUnderTest);

	}

	@Test
	public void testMagicCommand() {
		try {
			Point pointToTest = new Point((Point) PrivateAccess.getMemberValue(MagicCommand.class,
					mMagicCommandUnderTest, "mColorPixel"));
			assertNotNull(pointToTest);
			assertEquals((int) mPointUnderTest.x, pointToTest.x);
			assertEquals((int) mPointUnderTest.y, pointToTest.y);
			assertNotNull(PrivateAccess.getMemberValue(MagicCommand.class, mMagicCommandUnderTestNull, "mColorPixel"));
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}
}
