package org.catrobat.paintroid.test.integration.layercommands;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageButton;
import android.widget.ListView;

public class ChangeLayerCommandTest extends LayerIntegrationTestClass {

	public ChangeLayerCommandTest() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public final void testChangeCurrentLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 2);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);
	}

	@Test
	public final void testUndoSymbolAfterChange() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		ImageButton undoButton = (ImageButton) mSolo.getView(R.id.btn_top_undo);
		Bitmap bitmap1 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();

		mSolo.clickOnView(mButtonTopUndo);
		Bitmap bitmap2 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		assertEquals("The Undo-Symbol should stay diabled", bitmap1, bitmap2);
		mSolo.sleep(1000);

		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		Bitmap bitmap3 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		mSolo.sleep(1000);
		assertTrue("The Undo-Symbol should change", bitmap1 != bitmap3);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 2);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		bitmap3 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);
		assertTrue("There shall be one undo left", bitmap1 != bitmap3);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		bitmap3 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.sleep(1000);
		assertSame("There shall be no undo left", bitmap1, bitmap3);
	}

	@Test
	public final void testRedoSymbolAfterChange() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		ImageButton redoButton = (ImageButton) mSolo.getView(R.id.btn_top_redo);
		Bitmap bitmap1 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();

		mSolo.clickOnView(mButtonTopRedo);
		Bitmap bitmap2 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
		assertEquals("The redo-Symbol should stay diabled", bitmap1, bitmap2);
		mSolo.sleep(1000);

		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		mSolo.clickOnView(mButtonTopUndo);
		mSolo.sleep(1000);

		Bitmap bitmap3 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
		mSolo.sleep(1000);
		assertTrue("The Redo-Symbol should change", bitmap1 != bitmap3);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 2);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		bitmap3 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);
		assertNotSame("There shall be one redo left", bitmap1, bitmap3);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		bitmap3 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.sleep(1000);
		assertSame("There shall be no redo left", bitmap1, bitmap3);
	}

}
