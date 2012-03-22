package at.tugraz.ist.paintroid.test.junit.command;

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
	protected Bitmap mBitmapUnderTest;
	protected PrivateAccess mPrivateAccess;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mPrivateAccess = new PrivateAccess();
		mBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		mBitmapUnderTest.eraseColor(Color.BLACK);
		mBitmapCommandUnderTest = new BitmapCommand(mBitmapUnderTest);
	}

	@Test
	public void testRun() {
		try {
			Canvas canvasUnderTest = new Canvas();
			Bitmap originalBitmap = mBitmapUnderTest.copy(Config.ARGB_8888, false);
			Bitmap canvasBitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
			canvasUnderTest.setBitmap(canvasBitmapUnderTest);
			assertNull(mPrivateAccess.getMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mStoredBitmap"));

			mBitmapCommandUnderTest.run(canvasUnderTest, mBitmapUnderTest);

			// assertNull(mPrivateAccess.getMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mBitmap"));
			PaintroidAsserts.assertBitmapEquals(canvasBitmapUnderTest, originalBitmap);
			assertNotNull(mPrivateAccess.getMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mStoredBitmap"));

			mPrivateAccess.setMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mBitmap", null);
			canvasUnderTest.drawColor(Color.BLACK - 1);
			mBitmapCommandUnderTest.run(canvasUnderTest, null);

			PaintroidAsserts.assertBitmapEquals(originalBitmap, canvasBitmapUnderTest);

		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}

	@Test
	public void testBitmapCommand() {
		try {
			assertEquals(mBitmapUnderTest,
					mPrivateAccess.getMemberValue(BaseCommand.class, mBitmapCommandUnderTest, "mBitmap"));
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}

}
