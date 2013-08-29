package org.catrobat.paintroid.test.integration.layercommands;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.PointF;
import android.util.Log;
import android.widget.ListView;

public class DeleteLayerCommandTest extends BaseIntegrationTestClass {

	public DeleteLayerCommandTest() throws Exception {
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

	// Deleted Flag
	// Switching der andereren Ebenen
	// Fälle: Erste - Ebene löschen, 2. Ebene löschen, letzte Ebene löschen

	@Test
	public final void testDeleteFirstLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 3);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		PointF pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		assertTrue("Painting on the layer didn't work", getNumOfCommandsOfLayer(0) == 1);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("deleting a layer and its commands didn't work", getNumOfCommandsOfLayer(0) == 0);
		assertTrue(
				"Removing a layer and its commands didn't work. There shall be one deleted command. The Pointcommand, which moved to the very last layer",
				getNumOfDeletedCommandsOfLayer(2) == 1);
	}

	@Test
	public final void testDeleteSecondLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 3);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		PointF pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		assertTrue("Painting on the layer didn't work", getNumOfCommandsOfLayer(0) == 1);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("deleting a layer and its commands didn't work", getNumOfCommandsOfLayer(1) == 0);
		assertTrue(
				"Removing a layer and its commands didn't work. There shall be one deleted command: The Pointcommand, which moved to the very last layer",
				getNumOfDeletedCommandsOfLayer(2) == 1);
	}

	@Test
	public final void testDeleteLastLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 3);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		PointF pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		assertTrue("Painting on the layer didn't work", getNumOfCommandsOfLayer(0) == 1);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("deleting a layer and its commands didn't work", getNumOfCommandsOfLayer(2) == 0);
		assertTrue(
				"Removing a layer and its commands didn't work. There shall be one deleted command: The Pointcommand, which moved to the very last layer",
				getNumOfDeletedCommandsOfLayer(2) == 1);
	}

	@Test
	public void testSingleLayerDeletionUndo() {

	}

	@Test
	public void testSingleLayerDeletionUndoOnFirstLayer() {

	}

	public void testMultiLayerDeletionUndo() {

	}

	private int getNumOfCommandsOfLayer(int i) {
		int counter = 0;
		for (int j = 1; j < PaintroidApplication.commandManager.getCommands().size(); j++) {
			if (PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer() == i
					&& PaintroidApplication.commandManager.getCommands().get(j).isDeleted() == false) {
				counter++;
			}
		}
		return counter;
	}

	private int getNumOfDeletedCommandsOfLayer(int i) {
		int counter = 0;
		for (int j = 1; j < PaintroidApplication.commandManager.getCommands().size(); j++) {
			Log.i(PaintroidApplication.TAG, PaintroidApplication.commandManager.getCommands().get(j).toString() + " "
					+ String.valueOf(PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer()));
			if (PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer() == i
					&& PaintroidApplication.commandManager.getCommands().get(j).isDeleted() == true) {
				counter++;
			}
		}
		return counter;
	}
}
