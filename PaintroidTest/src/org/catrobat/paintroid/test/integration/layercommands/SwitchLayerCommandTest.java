package org.catrobat.paintroid.test.integration.layercommands;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.PointF;
import android.widget.ListView;

public class SwitchLayerCommandTest extends LayerIntegrationTestClass {

	public SwitchLayerCommandTest() throws Exception {
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
	public final void testSwitchLayer() {
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

		PointF pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.clickOnScreen(pf.x, pf.y);

		mSolo.sleep(1000);
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		assertTrue("Painting on the layer didn't work", getNumOfCommandsOfLayer(0) == 1);
		assertTrue("Painting on the correct layer didn't work", getNumOfCommandsOfLayer(1) == 0);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Switching didn't work", getNumOfCommandsOfLayer(0) == 0);
		assertTrue("Switching didn't work", getNumOfCommandsOfLayer(1) == 1);

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);

	}
}
