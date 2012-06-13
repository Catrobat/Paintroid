package at.tugraz.ist.paintroid.test.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.GridView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class CropToolIntegrationTest extends BaseIntegrationTestClass {

	public CropToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testIfOnePixelIsFound() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		currentDrawingSurfaceBitmap.setPixel(mScreenWidth / 2, mScreenHeight / 2, Color.BLUE);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = 0;
		int maximumCroppingTimeoutCounts = 30;
		for (; croppingTimeoutCounter < maximumCroppingTimeoutCounts; croppingTimeoutCounter++) {
			if (mSolo.getCurrentProgressBars().size() > 0 || croppingTimeoutCounter <= 1) {
				if (croppingTimeoutCounter != 0)
					if (mSolo.searchText(mSolo.getString(R.string.crop_progress_text), 1, true) == false)
						break;
				mSolo.sleep(TIMEOUT);
			} else {
				break;
			}

		}
		if (croppingTimeoutCounter >= maximumCroppingTimeoutCounts) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(200);
		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", 1, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", 1, currentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, currentDrawingSurfaceBitmap.getPixel(0, 0));
	}

	@Test
	public void testIfMultiplePixelAreFound() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		int originalWidth = currentDrawingSurfaceBitmap.getWidth();
		int originalHeight = currentDrawingSurfaceBitmap.getHeight();
		currentDrawingSurfaceBitmap.setPixel(1, 1, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth - 2, originalHeight - 2, Color.BLUE);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = 0;
		int maximumCroppingTimeoutCounts = 30;
		for (; croppingTimeoutCounter < maximumCroppingTimeoutCounts; croppingTimeoutCounter++) {
			if (mSolo.getCurrentProgressBars().size() > 0 || croppingTimeoutCounter <= 1) {
				if (croppingTimeoutCounter != 0)
					if (mSolo.searchText(mSolo.getString(R.string.crop_progress_text), 1, true) == false)
						break;
				mSolo.sleep(TIMEOUT);
			} else {
				break;
			}

		}
		if (croppingTimeoutCounter >= maximumCroppingTimeoutCounts) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(200);
		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", originalWidth - 2, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", originalHeight - 2, currentDrawingSurfaceBitmap.getHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE, currentDrawingSurfaceBitmap.getPixel(0, 0));
	}

	@Test
	public void testIfDrawingSurfaceBoundsAreFound() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		int originalWidth = currentDrawingSurfaceBitmap.getWidth();
		int originalHeight = currentDrawingSurfaceBitmap.getHeight();
		currentDrawingSurfaceBitmap.setPixel(originalWidth / 2, 0, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(0, originalHeight / 2, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth - 1, originalHeight / 2, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth / 2, originalHeight - 1, Color.BLUE);

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = 0;
		int maximumCroppingTimeoutCounts = 30;
		for (; croppingTimeoutCounter < maximumCroppingTimeoutCounts; croppingTimeoutCounter++) {
			if (mSolo.getCurrentProgressBars().size() > 0 || croppingTimeoutCounter <= 1) {
				if (croppingTimeoutCounter != 0)
					if (mSolo.searchText(mSolo.getString(R.string.crop_progress_text), 1, true) == false)
						break;
				mSolo.sleep(TIMEOUT);
			} else {
				break;
			}

		}
		if (croppingTimeoutCounter >= maximumCroppingTimeoutCounts) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnView(mToolBarButtonTwo);
		mSolo.sleep(200);
		assertEquals(currentDrawingSurfaceBitmap, PaintroidApplication.DRAWING_SURFACE.getBitmap());

		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", originalWidth, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Wrong height after cropping ", originalHeight, currentDrawingSurfaceBitmap.getHeight());
	}

	@Test
	public void testIfClickOnCanvasDoesNothing() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		currentDrawingSurfaceBitmap.eraseColor(Color.BLACK);
		int drawingSurfaceOriginalWidth = currentDrawingSurfaceBitmap.getWidth();
		int drawingSurfaceOriginalHeight = currentDrawingSurfaceBitmap.getHeight();
		for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
			currentDrawingSurfaceBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Waiting for the ToolMenu to open", mSolo.waitForView(GridView.class, 1, TIMEOUT));

		mSolo.clickOnText(mMainActivity.getString(R.string.button_crop));
		assertTrue("Waiting for tool to change -> MainActivity", mSolo.waitForActivity("MainActivity", TIMEOUT));
		assertEquals("Switching to another tool", PaintroidApplication.CURRENT_TOOL.getToolType(), ToolType.CROP);

		int croppingTimeoutCounter = 0;
		int maximumCroppingTimeoutCounts = 30;
		for (; croppingTimeoutCounter < maximumCroppingTimeoutCounts; croppingTimeoutCounter++) {
			if (mSolo.getCurrentProgressBars().size() > 0 || croppingTimeoutCounter <= 1) {
				if (croppingTimeoutCounter != 0)
					if (mSolo.searchText(mSolo.getString(R.string.crop_progress_text), 1, true) == false)
						break;
				mSolo.sleep(TIMEOUT);
			} else {
				break;
			}

		}
		if (croppingTimeoutCounter >= maximumCroppingTimeoutCounts) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Width changed:", drawingSurfaceOriginalWidth, currentDrawingSurfaceBitmap.getWidth());
		assertEquals("Height changed:", drawingSurfaceOriginalHeight, currentDrawingSurfaceBitmap.getHeight());
	}
}
