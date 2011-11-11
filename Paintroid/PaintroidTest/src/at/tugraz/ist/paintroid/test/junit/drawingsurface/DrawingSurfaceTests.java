package at.tugraz.ist.paintroid.test.junit.drawingsurface;

import static org.easymock.EasyMock.createMock;
import junit.framework.TestCase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import at.tugraz.ist.paintroid.test.junit.Utils;
import at.tugraz.ist.ui.DrawingSurface;

public class DrawingSurfaceTests extends TestCase {
	protected DrawingSurface drawingSurface;

	@Override
	public void setUp() {
		drawingSurface = createMock(DrawingSurface.class);
	}

	public void testSetAndGetBitmap() {
		Bitmap testBitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
		drawingSurface.setBitmap(testBitmap);
		Bitmap testBitmap2 = drawingSurface.getBitmap();
		assertTrue(Utils.bitmapEquals(testBitmap, testBitmap2));
	}
}
