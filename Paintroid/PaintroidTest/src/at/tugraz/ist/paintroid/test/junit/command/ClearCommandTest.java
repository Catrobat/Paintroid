package at.tugraz.ist.paintroid.test.junit.command;

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
	protected int mEraseColor = Color.CYAN;
	protected PrivateAccess mPrivateAccess = new PrivateAccess();

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mClearCommandUnderTestTransparent = new ClearCommand();
		mClearCommandUnderTestColored = new ClearCommand(mEraseColor);
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
	}

	@Test
	public void testClearCommand() {
		try {
			assertEquals(Color.TRANSPARENT,
					mPrivateAccess.getMemberValue(ClearCommand.class, mClearCommandUnderTestTransparent, "mColor"));
			assertEquals(mEraseColor,
					mPrivateAccess.getMemberValue(ClearCommand.class, mClearCommandUnderTestColored, "mColor"));
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}
}
