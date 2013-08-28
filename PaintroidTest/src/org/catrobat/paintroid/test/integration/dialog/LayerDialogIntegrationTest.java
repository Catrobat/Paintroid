package org.catrobat.paintroid.test.integration.dialog;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.layerchooser.LayerChooserDialog;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

public class LayerDialogIntegrationTest extends BaseIntegrationTestClass {

	public LayerDialogIntegrationTest() throws Exception {
		super();
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
	public void testLayerDialog() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertFalse("LayerChooserDialog is already visible", LayerChooserDialog.getInstance().isAdded());
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);
		assertTrue("LayerChooserDialog is not visible", LayerChooserDialog.getInstance().isAdded());
	}

	@Test
	public void testOpenLayerPickerOnClickOnLayerButton() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		View listview = mSolo.getView(R.id.mListView);
		assertTrue("LayerChooser Listview not opening", mSolo.waitForView(listview, 1000, false));

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);
		assertTrue("LayerChooserDialog is still visible", !LayerChooserDialog.getInstance().isAdded());
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

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Changing the layer with Buttons doesn't work properly",
				prev_layer + 1 == PaintroidApplication.currentLayer);
		prev_layer = PaintroidApplication.currentLayer;

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Changing the layer on touch doesn't work", prev_layer - 1 == PaintroidApplication.currentLayer);
	}

	@Test
	public void testCommandLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		int numOfCommands = getNumOfCommandsOfLayer(1);

		assertTrue("There is a illegal command in the commandlist", numOfCommands == 0);

		mSolo.clickOnScreen(point.x, point.y);

		numOfCommands = getNumOfCommandsOfLayer(1);

		assertTrue("Changing the layer with Buttons doesn't work properly,a wrong commandLayer stored",
				numOfCommands == 0);
	}

	private int getNumOfCommandsOfLayer(int i) {
		int counter = 0;
		for (int j = 0; j < PaintroidApplication.commandManager.getCommands().size(); j++) {
			if (PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer() == i
					&& PaintroidApplication.commandManager.getCommands().get(j).isDeleted() == false) {
				counter++;
			}
		}
		return counter;
	}

	@Test
	public void testMaxLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		for (int i = 0; i <= 30; i++) {
			mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		}
		assertTrue("More than 30 layers are possible",
				(((ListView) mSolo.getView(R.id.mListView)).getAdapter()).getCount() == 30);

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

		assertTrue("Less than one layers is possible", listview.getAdapter().getCount() == prev_num_layers);

	}

	@Test
	public void testDeleteLayer() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		assertTrue("It's possible to remove a single layer", listview.getAdapter().getCount() == prev_num_layers);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding a layer didn't work", listview.getAdapter().getCount() != prev_num_layers);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button2));
		mSolo.sleep(1000);

		assertTrue("Securityquestion didn't work", listview.getAdapter().getCount() != prev_num_layers);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		assertTrue("Removing a layer didn't work", listview.getAdapter().getCount() == prev_num_layers);
		assertTrue("Removing a layer and its command didn't work", getNumOfCommandsOfLayer(1) == 0);
	}

	@Test
	public void testDeleteLayerOnScreen() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);
		mSolo.sleep(1000);

		int colorOriginal = PaintroidApplication.drawingSurface.getPixel(point);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding a layer didn't work", listview.getAdapter().getCount() != prev_num_layers);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		assertTrue("Removing a layer didn't work", colorOriginal != PaintroidApplication.drawingSurface.getPixel(point));
	}

	@Test
	public void testAddLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);
		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding a layer didn't work", listview.getAdapter().getCount() == prev_num_layers + 1);

	}

	@Test
	public void testMoveLayerUp() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		String oldname = (LayerChooserDialog.layer_data.get(0).name);

		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.sleep(1000);

		mSolo.enterText(0, "test");
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_up));

		assertTrue("The first layer can move up", oldname != (LayerChooserDialog.layer_data.get(0).name));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		assertTrue("The first layer can't move down", oldname != (LayerChooserDialog.layer_data.get(1).name));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_up));
		assertTrue("The first layer can't move up", oldname != (LayerChooserDialog.layer_data.get(0).name));

	}

	@Test
	public void testSwitchLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		PointF point2 = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2 + 10);

		mSolo.clickOnScreen(point.x, point.y);
		mSolo.sleep(1000);

		int colorOriginal = PaintroidApplication.drawingSurface.getPixel(point);

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

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		mSolo.clickOnScreen(point2.x, point2.y);
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
		assertTrue("Delete the wrong layer, switching didn't work",
				colorOriginal == PaintroidApplication.drawingSurface.getPixel(point2));
		assertTrue("The first command shall still be a Bitmapcommand on the 0th layer",
				0 == PaintroidApplication.commandManager.getCommands().getFirst().getCommandLayer());
	}

	@Test
	public void testMoveLayerDown() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		String oldname = (LayerChooserDialog.layer_data.get(0).name);
		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.enterText(0, "test");

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		assertTrue("A sigle layer moved down", oldname != (LayerChooserDialog.layer_data.get(0).name));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));

		assertTrue("The first Layer didn't moved down", oldname == (LayerChooserDialog.layer_data.get(0).name));

	}

	@Test
	public void testChangeLayerName() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		String oldname = (LayerChooserDialog.layer_data.get(0).name);

		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.sleep(1000);

		mSolo.enterText(0, "test");
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		assertTrue("Layername didn't changed", oldname != (LayerChooserDialog.layer_data.get(0).name));
		oldname = (LayerChooserDialog.layer_data.get(0).name);

		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.sleep(1000);

		mSolo.enterText(0, "test");
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		assertTrue("Layername changed, but it shouldn't", oldname != (LayerChooserDialog.layer_data.get(0).name));

		oldname = (LayerChooserDialog.layer_data.get(0).name);

		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(android.R.id.button1));

		assertTrue("Layername can be empty", oldname == LayerChooserDialog.layer_data.get(0).name);
	}

	@Test
	public void testShowAndHideLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		int xCoord = mCurrentDrawingSurfaceBitmap.getWidth() / 2;
		int yCoord = mCurrentDrawingSurfaceBitmap.getHeight() / 2;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);
		int colorOriginal = PaintroidApplication.drawingSurface.getPixel(pointOnBitmap);

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(pointOnScreen);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.clickOnScreen(pointOnBitmap.x, pointOnBitmap.y);

		Log.i("my", " " + PaintroidApplication.drawingSurface.getPixel(pointOnBitmap));
		Log.i("my", " " + PaintroidApplication.drawingSurface.getPixel(pointOnScreen));
		mSolo.sleep(1000);
		assertTrue("Painting on the surface didn't work",
				colorOriginal != PaintroidApplication.drawingSurface.getPixel(pointOnBitmap));

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.sleep(1000);

		ImageButton eyeButton = (ImageButton) mSolo.getView(R.id.eyeIcon);
		Bitmap eyeBitmap = ((BitmapDrawable) eyeButton.getDrawable()).getBitmap();

		assertTrue("The layer is already hidden", LayerChooserDialog.layer_data.get(0).visible == false);
		mSolo.clickOnView(mSolo.getView(R.id.eyeIcon));
		mSolo.sleep(1000);

		assertTrue("The layer is not on visible", LayerChooserDialog.layer_data.get(0).visible == true);
		mSolo.clickOnView(mSolo.getView(R.id.eyeIcon));
		mSolo.sleep(1000);

		ImageButton eyeButton2 = (ImageButton) mSolo.getView(R.id.eyeIcon);
		Bitmap eyeBitmap2 = ((BitmapDrawable) eyeButton2.getDrawable()).getBitmap();

		assertTrue("Eye-symbols didn't change", !eyeBitmap.equals(eyeBitmap2));

		mSolo.clickOnView(mSolo.getButton(R.string.done));
		mSolo.sleep(1000);

		assertTrue("Painted point is still visible",
				colorOriginal == PaintroidApplication.drawingSurface.getPixel(pointOnBitmap));

	}
}