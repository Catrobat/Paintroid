package org.catrobat.paintroid.test.integration.layercommands;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.widget.ListView;

public class DeleteLayerCommandTest extends LayerIntegrationTestClass {

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

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 3);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		mSolo.clickOnScreen(pf.x, pf.y);

		mSolo.sleep(1000);
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		assertTrue("Painting on the layer didn't work", getNumOfCommandsOfLayer(0) == 1);
		assertTrue("There shall be no deleted commands yet", getNumOfDeletedCommandsOfLayer(0)
				+ getNumOfDeletedCommandsOfLayer(1) + getNumOfDeletedCommandsOfLayer(2) == 0);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		assertTrue(
				"Removing a layer and its commands didn't work. There shall be no not-deleted command. The Pointcommand shall has a deleted flag",
				getNumOfCommandsOfLayer(0) == 0);
		assertTrue(
				"Removing a layer and its commands didn't work. There shall be one deleted command. The Pointcommand with the deleted flag",
				getNumOfDeletedCommandsOfLayer(0) == 1);
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

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 3);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		mSolo.clickOnScreen(pf.x, pf.y);

		mSolo.sleep(1000);
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 1);

		assertTrue("Painting on the layer didn't work", getNumOfCommandsOfLayer(1) == 1);
		assertTrue("There shall be no deleted commands yet", getNumOfDeletedCommandsOfLayer(0)
				+ getNumOfDeletedCommandsOfLayer(1) + getNumOfDeletedCommandsOfLayer(2) == 0);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);

		assertTrue(
				"Removing a layer and its commands didn't work. There shall be no not-deleted command. The Pointcommand shall has a deleted flag",
				getNumOfCommandsOfLayer(1) == 0);
		assertTrue(
				"Removing a layer and its commands didn't work. There shall be one deleted command. The Pointcommand with the deleted flag",
				getNumOfDeletedCommandsOfLayer(1) == 1);
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

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 3);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		mSolo.clickOnScreen(pf.x, pf.y);

		mSolo.sleep(1000);
		assertTrue("Current Layer should be 2", PaintroidApplication.currentLayer == 2);

		assertTrue("Painting on the layer didn't work", getNumOfCommandsOfLayer(2) == 1);
		assertTrue("There shall be no deleted commands yet", getNumOfDeletedCommandsOfLayer(0)
				+ getNumOfDeletedCommandsOfLayer(1) + getNumOfDeletedCommandsOfLayer(2) == 0);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);

		assertTrue(
				"Removing a layer and its commands didn't work. There shall be no not-deleted command. The Pointcommand shall has a deleted flag",
				getNumOfCommandsOfLayer(2) == 0);
		assertTrue(
				"Removing a layer and its commands didn't work. There shall be one deleted command. The Pointcommand with the deleted flag",
				getNumOfDeletedCommandsOfLayer(2) == 1);
	}

	@Test
	public void testSingleLayerDeletionUndo() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 2);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		mSolo.clickOnScreen(pf.x, pf.y);

		mSolo.sleep(1000);
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		assertTrue("Painting on the layer didn't work", getNumOfCommandsOfLayer(0) == 1);
		assertTrue("There shall be no deleted commands yet", getNumOfDeletedCommandsOfLayer(0)
				+ getNumOfDeletedCommandsOfLayer(1) + getNumOfDeletedCommandsOfLayer(2) == 0);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("There shall be one point-command from the formally layer 1", getNumOfCommandsOfLayer(0) == 1);
		assertTrue("There shall be no command ", getNumOfCommandsOfLayer(1) == 0);
		assertTrue("There shall be no deleted command", getNumOfDeletedCommandsOfLayer(1) == 0);
		assertTrue("There shall be one deleted command", getNumOfDeletedCommandsOfLayer(0) == 1);

		mSolo.clickOnView(mButtonTopUndo);

		assertTrue("There shall be one point-command from the old layer 0", getNumOfCommandsOfLayer(0) == 1);
		assertTrue("There shall be one point-command back at the layer 1", getNumOfCommandsOfLayer(1) == 1);
		assertTrue("There shall be no deleted command", getNumOfDeletedCommandsOfLayer(0) == 0);
		assertTrue("There shall be no deleted command", getNumOfDeletedCommandsOfLayer(1) == 0);

	}

	public void testMultiLayerDeletionUndo() {

	}

}
