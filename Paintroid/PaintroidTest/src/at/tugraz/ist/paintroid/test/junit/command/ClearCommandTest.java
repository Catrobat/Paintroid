package at.tugraz.ist.paintroid.test.junit.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.command.implementation.ClearCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;

public class ClearCommandTest extends AndroidTestCase {

	protected ClearCommand mClearCommandUnderTestTransparent;
	protected ClearCommand mClearCommandUnderTestColored;
	protected ClearCommand mClearCommandUnderTestNull;
	protected int mEraseColor = Color.CYAN;

	// protected PrivateAccess PrivateAccess = new PrivateAccess();

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mClearCommandUnderTestTransparent = new ClearCommand();
		mClearCommandUnderTestColored = new ClearCommand(mEraseColor);
		mClearCommandUnderTestNull = new ClearCommand(0);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		mClearCommandUnderTestTransparent = null;
		mClearCommandUnderTestColored = null;
		mClearCommandUnderTestNull = null;
	}

	@Test
	public void testRun() {
		Bitmap bitmapToCompare = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		bitmapToCompare.eraseColor(Color.TRANSPARENT);
		Bitmap bitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		bitmapUnderTest.eraseColor(mEraseColor - 1);
		mClearCommandUnderTestTransparent.run(null, bitmapUnderTest);
		PaintroidAsserts.assertBitmapEquals(bitmapToCompare, bitmapUnderTest);
		mClearCommandUnderTestColored.run(null, bitmapUnderTest);
		bitmapToCompare.eraseColor(mEraseColor);
		PaintroidAsserts.assertBitmapEquals(bitmapToCompare, bitmapUnderTest);
		mClearCommandUnderTestNull.run(null, null);

		bitmapToCompare.recycle();
		bitmapUnderTest.recycle();

		bitmapToCompare = null;
		bitmapUnderTest = null;

	}

	@Test
	public void testClearCommand() {
		try {
			assertEquals(Color.TRANSPARENT,
					PrivateAccess.getMemberValue(ClearCommand.class, mClearCommandUnderTestTransparent, "mColor"));
			assertEquals(mEraseColor,
					PrivateAccess.getMemberValue(ClearCommand.class, mClearCommandUnderTestColored, "mColor"));
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}
}
