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

	@Test
	public void testSwitchLayerOnScreen() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		PointF point2 = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2 + 10);

		int colorOriginal = PaintroidApplication.drawingSurface.getPixel(point);
		PaintroidApplication.currentTool.changePaintColor(R.color.abs__primary_text_holo_light);

		mSolo.clickOnScreen(point.x, point.y);
		mSolo.sleep(1000);

		assertTrue("Painting didn't work properly",
				colorOriginal != PaintroidApplication.drawingSurface.getPixel(point));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding a layer didn't work",
				mSolo.getCurrentListViews().get(0).getAdapter().getCount() == prev_num_layers + 1);

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		PaintroidApplication.currentTool.changePaintColor(R.color.abs__bright_foreground_holo_light);
		mSolo.clickOnScreen(point2.x, point2.y);
		mSolo.sleep(1000);

		assertTrue("The colors are not combinated",
				colorOriginal != PaintroidApplication.drawingSurface.getPixel(point));
		assertTrue("The colors are not combinated",
				colorOriginal != PaintroidApplication.drawingSurface.getPixel(point2));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Delete the wrong layer, switching didn't work",
				colorOriginal != PaintroidApplication.drawingSurface.getPixel(point));

		assertTrue(
				"Delete the wrong layer, switching didn't work: ",
				getActivity().getResources().getColor(R.color.abs__bright_foreground_holo_light) != PaintroidApplication.drawingSurface
						.getPixel(point));

		assertTrue("The first command shall still be a Bitmapcommand on the 0th layer",
				0 == PaintroidApplication.commandManager.getCommands().getFirst().getCommandLayer());
	}
}
