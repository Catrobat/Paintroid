package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.command.implementation.BitmapCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;

public class BitmapCommandTest extends AndroidTestCase {

	// private final BaseCommandStub mBaseCommandStub = new BaseCommandStub();
	protected BitmapCommand mBitmapCommandUnderTest;
	protected BitmapCommand mBitmapCommandUnderNullTest;
	protected Bitmap mBitmapUnderTest;

	// protected PrivateAccess PrivateAccess;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		// PrivateAccess = new PrivateAccess();
		mBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		mBitmapUnderTest.eraseColor(Color.BLACK);
		mBitmapCommandUnderTest = new BitmapCommand(mBitmapUnderTest);
		mBitmapCommandUnderNullTest = new BitmapCommand(null);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		mBitmapUnderTest.recycle();
		mBitmapUnderTest = null;
	}

	@Test
	public void testRun() {
		try {
			Canvas canvasUnderTest = new Canvas();
			Bitmap originalBitmap = mBitmapUnderTest.copy(Config.ARGB_8888, false);
			Bitmap canvasBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
			canvasUnderTest.setBitmap(canvasBitmapUnderTest);
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mStoredBitmap"));

			mBitmapCommandUnderTest.run(canvasUnderTest, mBitmapUnderTest);
			mBitmapCommandUnderNullTest.run(null, null);

			// assertNull(mPrivateAccess.getMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mBitmap"));
			PaintroidAsserts.assertBitmapEquals(canvasBitmapUnderTest, originalBitmap);
			assertNotNull(PrivateAccess.getMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mStoredBitmap"));

			PrivateAccess.setMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mBitmap", null);
			canvasUnderTest.drawColor(Color.BLACK - 1);
			mBitmapCommandUnderTest.run(canvasUnderTest, null);
			mBitmapCommandUnderNullTest.run(null, null);

			PaintroidAsserts.assertBitmapEquals(originalBitmap, canvasBitmapUnderTest);

			originalBitmap.recycle();
			canvasBitmapUnderTest.recycle();

			originalBitmap = null;
			canvasBitmapUnderTest = null;

		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}

	@Test
	public void testBitmapCommand() {
		try {
			assertEquals(mBitmapUnderTest,
					PrivateAccess.getMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mBitmap"));
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}

}
