//package at.tugraz.ist.paintroid.test.integration;
//
//import junit.framework.AssertionFailedError;
//import android.graphics.Color;
//import android.view.KeyEvent;
//import at.tugraz.ist.paintroid.PaintroidApplication;
//import at.tugraz.ist.paintroid.R;
//import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;
//
//public class HideToolbarIntegrationTest extends BaseIntegrationTestClass {
//
//	private static final int TOOLBAR_BOTTOM_OFFSET = 20;
//
//	public HideToolbarIntegrationTest() throws Exception {
//		super();
//	}
//
//	public void testHideToolbar() {
//		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
//		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
//
//		int clickPointX = mScreenWidth / 2;
//		int clickPointY = mScreenHeight - TOOLBAR_BOTTOM_OFFSET;
//
//		mSolo.clickOnScreen(clickPointX, clickPointY);
//		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(clickPointX, clickPointY);
//		assertEquals("pixel should be black", Color.BLACK, pixel);
//	}
//
//	public void testHideStatusbarOnHideToolbar() {
//		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
//		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
//
//		mSolo.sleep(2000);
//
//		int clickPointX = mScreenWidth / 2;
//		int clickPointY = 1;
//
//		mSolo.clickOnScreen(clickPointX, clickPointY);
//		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(clickPointX, clickPointY);
//		assertEquals("pixel should be black", Color.BLACK, pixel);
//	}
//
//
//	// FIXME
//	// public void testShowToolbarOnBackPressed() {
//	// try {
//	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
//	// mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
//	// mSolo.goBack();
//	// mSolo.sleep(2000);
//	//
//	// int clickPointX = mScreenWidth / 2;
//	// int clickPointY = mScreenHeight - TOOLBAR_BOTTOM_OFFSET;
//	//
//	//
//	// mSolo.clickOnScreen(clickPointX, clickPointY);
//	// int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(clickPointX, clickPointY);
//	// assertEquals("pixel should be transparent", Color.TRANSPARENT, pixel);
//	// } catch (Exception allExceptionsGoHere) {
//	// fail(allExceptionsGoHere.toString());
//	// }
//	// }
//
//	public void testShowToolbarOnBackPressed() {
//		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
//		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
//		mSolo.goBack();
//
//		int clickPointX = mScreenWidth / 2;
//		int clickPointY = mScreenHeight - TOOLBAR_BOTTOM_OFFSET;
//
//		mSolo.clickOnScreen(clickPointX, clickPointY);
//		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(clickPointX, clickPointY);
//		assertEquals("pixel should be transparent", Color.TRANSPARENT, pixel);
//
//	}
//
//
//	public void testShowStatusbarOnBackPressed() {
//		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
//		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
//		mSolo.goBack();
//		mSolo.sleep(2000);
//
//		int clickPointX = mScreenWidth / 2;
//		int clickPointY = 0;
//
//		boolean assertionFailedErrorCaught = false;
//		try {
//			mSolo.clickOnScreen(clickPointX, clickPointY);
//		} catch (AssertionFailedError ex) {
//			assertionFailedErrorCaught = true;
//		} finally {
//			assertTrue("assertion failed error should have been caught", assertionFailedErrorCaught);
//		}
//
//	}
//
//
//	// public void testShowToolbarOnMenuPressed() {
//	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
//	// mSolo.clickOnMenuItem(mSolo.getString(R.string.hide_menu));
//	// mSolo.sendKey(KeyEvent.KEYCODE_MENU);
//	//
//	// int clickPointX = mScreenWidth / 2;
//	// int clickPointY = mScreenHeight - TOOLBAR_BOTTOM_OFFSET;
//	//
//	// mSolo.clickOnScreen(clickPointX, clickPointY);
//	// int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(clickPointX, clickPointY);
//	// assertEquals("pixel should be transparent", Color.TRANSPARENT, pixel);
//	// }
//
//	public void testShowToolbarOnMenuPressed() {
//		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
//		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
//		mSolo.sendKey(KeyEvent.KEYCODE_MENU);
//
//		int clickPointX = mScreenWidth / 2;
//		int clickPointY = mScreenHeight - TOOLBAR_BOTTOM_OFFSET;
//
//		mSolo.clickOnScreen(clickPointX, clickPointY);
//		int pixel = PaintroidApplication.DRAWING_SURFACE.getBitmap().getPixel(clickPointX, clickPointY);
//		assertEquals("pixel should be transparent", Color.TRANSPARENT, pixel);
//	}
//
//
//	public void testShowStatusbarOnMenuPressed() {
//		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
//		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu));
//		mSolo.sendKey(KeyEvent.KEYCODE_MENU);
//		mSolo.sleep(5000);
//
//		int clickPointX = mScreenWidth / 2;
//		int clickPointY = 0;
//
//		boolean assertionFailedErrorCaught = false;
//		try {
//			mSolo.clickOnScreen(clickPointX, clickPointY);
//		} catch (AssertionFailedError ex) {
//			assertionFailedErrorCaught = true;
//		} finally {
//			assertTrue("assertion failed error should have been caught", assertionFailedErrorCaught);
//		}
//	}
//
// }
