package org.catrobat.paintroid.test.integration.dialog;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.layerchooser.LayerChooserDialog;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Statusbar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.view.View;
import android.widget.ListView;

public class LayerDialogIntegrationTest extends BaseIntegrationTestClass {

	protected Statusbar mStatusbar;

	public LayerDialogIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		try {
			mStatusbar = (Statusbar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(), "mStatusbar");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testLayerDialog() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);
		assertTrue("LayerChooserDialog is not visible", LayerChooserDialog.getInstance().isShowing());
	}

	@Test
	public void testOpenLayerPickerOnClickOnLayerButton() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		View listview = mSolo.getView(R.id.mListView);
		assertTrue("LayerChooser Listview not opening", mSolo.waitForView(listview, 1000, false));

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_ok));
		mSolo.sleep(1000);
		assertFalse("LayerChooserDialog is still visible", LayerChooserDialog.getInstance().isShowing());
	}

	@Test
	public void testChangeLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		int prev_layer = PaintroidApplication.currentLayer;

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_ok));
		mSolo.sleep(1000);

		assertTrue("Changing the layer with Buttons doesn't work", prev_layer + 1 == PaintroidApplication.currentLayer);
		prev_layer = PaintroidApplication.currentLayer;

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_ok));
		mSolo.sleep(1000);

		assertTrue("Changing the layer on touch doesn't work", prev_layer - 1 == PaintroidApplication.currentLayer);
	}

	@Test
	public void testMaxLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		for (int i = 0; i <= 30; i++) {
			mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		}
		assertTrue("More than 30 layers are possible", mSolo.getCurrentListViews().get(0).getAdapter().getCount() == 30);

	}

	@Test
	public void testMinLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		assertTrue("Less than one layers is possible",
				mSolo.getCurrentListViews().get(0).getAdapter().getCount() == prev_num_layers);

	}

	@Test
	public void testDeleteLayer() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getCurrentButtons().get(2));
		mSolo.sleep(1000);

		assertTrue("Removing a layer didn't work", listview.getAdapter().getCount() == prev_num_layers);

	}

	@Test
	public void testAddLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);
		int prev_num_layers = mSolo.getCurrentListViews().get(0).getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding a layer didn't work",
				mSolo.getCurrentListViews().get(0).getAdapter().getCount() == prev_num_layers + 1);

	}

	@Test
	public void testMoveLayerUp() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		int prev_sel_layer = mSolo.getCurrentListViews().get(0).getSelectedItemPosition();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_up));

		assertTrue("The first layer can move up",
				mSolo.getCurrentListViews().get(0).getSelectedItemPosition() == prev_sel_layer + 1);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		prev_sel_layer = mSolo.getCurrentListViews().get(0).getSelectedItemPosition();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_up));
		assertTrue("The first layer can move up",
				mSolo.getCurrentListViews().get(0).getSelectedItemPosition() == prev_sel_layer - 1);

	}

	@Test
	public void testMoveLayerDown() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));

		assertTrue("The first layer can't move down", listview.getAdapter().getCount() == prev_num_layers + 1);
	}

	@Test
	public void testChangeLayerName() {

	}

}