package org.catrobat.paintroid.test.integration.layercommands;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.PointF;
import android.widget.ListView;

public class ShowAndHideCommandTest extends LayerIntegrationTestClass {

	public ShowAndHideCommandTest() throws Exception {
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
	public final void hideAndShowSingleLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		PointF pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.eyeIcon));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 2);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("There shall be one hidden command on the first layer", getNumOfHiddenCommandsOfLayer(0) == 1);
		assertTrue("There shall be no visible commands on the first layer", getNumOfCommandsOfLayer(0) == 0);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.eyeIcon));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("There shall be one visible command on the first layer", getNumOfCommandsOfLayer(0) == 1);
		assertTrue("There shall be no hidden commands on the first layer", getNumOfHiddenCommandsOfLayer(0) == 1);
	}

	@Test
	public final void testDrawOnHiddenLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.eyeIcon));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("There shall be just one row", listview.getAdapter().getCount() == 1);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("There shall be no command on the first layer", getNumOfCommandsOfLayer(0) == 0);

		PointF pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		assertTrue("There shall be one hidden command on the first layer", getNumOfHiddenCommandsOfLayer(0) == 1);
		assertTrue("There shall be no visible commands on the first layer", getNumOfCommandsOfLayer(0) == 0);

	}

}
