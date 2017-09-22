/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration;

import android.graphics.Color;
import android.graphics.PointF;
import android.media.Image;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

public class LayerIntegrationTest extends BaseIntegrationTestClass {

	private static final String NEW_LAYER = "New Layer";
	private static final String DELETE_LAYER = "Delete Layer";
	private static final String MERGE = "Merge";
	private static final String VISIBLE = "Visible";
	private static final String RENAME = "Rename";
	private static final String LOCK = "Lock";
	private static final String LAYER_ZERO = "Layer0";
	private static final String LAYER_ONE = "Layer1";
	private static final String LAYER_TWO = "Layer2";
	private static final String LAYER_SEVEN = "Layer7";


	public LayerIntegrationTest() throws Exception {
		super();
	}

	public void testShowLayerMenu() {
		mSolo.clickOnView(mButtonTopLayer);
		assertTrue("Layers dialog not visible",
				mSolo.waitForText(mSolo.getString(R.string.layers_title), 1, TIMEOUT, true));
	}

	public void testAddOneLayer() {
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		assertTrue("First Layer not visible", mSolo.searchText(LAYER_ZERO));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		assertTrue("New Layer not visible", mSolo.searchText(LAYER_ONE));
	}

	public void testDeleteEmptyLayer() {
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		assertTrue("New Layer not visible", mSolo.searchText(LAYER_ONE));
		mSolo.clickOnText(LAYER_ONE);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerDelete));
		assertFalse("New Layer should be deleted", mSolo.searchText(LAYER_ONE));
	}

	public void testDeleteFilledLayer() {
		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 3);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);

		mSolo.clickOnScreen(checkScreenPoint.x, checkScreenPoint.y);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		int colorLayerZero = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnText(LAYER_ZERO);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerDelete));
		assertFalse("Layer 1 should be deleted", mSolo.searchText(LAYER_ZERO));

		mSolo.goBack();
		int colorLayerOne = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertFalse("Pixel color should be transparent", colorLayerZero == colorLayerOne);

	}

	public void testSwitchBetweenFilledLayers() {
		PointF leftPointOnScreen = new PointF(20, mScreenHeight / 2);
		PointF rightPointOnScreen = new PointF(mScreenWidth - 20, mScreenHeight / 2);
		PointF UpperScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 3);
		PointF UpperCanvasPoint = Utils.getCanvasPointFromScreenPoint(UpperScreenPoint);
		PointF LowerScreenPoint = new PointF(mScreenWidth / 2, 2 * mScreenHeight / 3);
		PointF LowerCanvasPoint = Utils.getCanvasPointFromScreenPoint(LowerScreenPoint);

		mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 2);
		selectTool(ToolType.FILL);
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.goBack();

		selectTool(ToolType.BRUSH);
		mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 1);
		selectTool(ToolType.FILL);
		mSolo.clickOnScreen(mScreenWidth / 2, 2 * mScreenHeight / 3);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		int colorUpperSide = PaintroidApplication.drawingSurface.getPixel(UpperCanvasPoint);
		int colorLowerSide = PaintroidApplication.drawingSurface.getPixel(LowerCanvasPoint);
		assertTrue("Bitmap should show both layer", colorUpperSide == colorLowerSide);
	}

	public void testTryDeleteOnlyLayer() {
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerDelete));
		assertTrue("Layer 0 shouldn't be deleted", mSolo.searchText(LAYER_ZERO));
	}

	public void testMergeTwoEmptyLayers() {
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		assertTrue("New Layer not visible", mSolo.searchText(LAYER_ONE));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerMerge));
		mSolo.clickOnText(LAYER_ZERO);
		assertTrue("Merge two Layers didn't work", mSolo.searchText(LAYER_TWO));
	}

	public void testMergeTwoFilledLayers() {
		PointF leftPointOnScreen = new PointF(10, mScreenHeight / 2);
		PointF rightPointOnScreen = new PointF(mScreenWidth - 10, mScreenHeight / 2);
		mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 2);
		selectTool(ToolType.FILL);
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.goBack();

		selectTool(ToolType.BRUSH);
		mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 2);
		selectTool(ToolType.FILL);
		mSolo.clickOnScreen(mScreenWidth / 2, 2 * mScreenHeight / 3);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerMerge));
		mSolo.clickOnText(LAYER_ZERO);
		assertTrue("Merge two Layers didn't work", mSolo.searchText(LAYER_TWO));

		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);
		int colorLayerZero = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		checkScreenPoint = new PointF(mScreenWidth / 2, 2 * mScreenHeight / 3 + 100);
		checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);
		int colorLayerOne = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be the same.", colorLayerZero, colorLayerOne);
	}

	public void testRenameLayer() {
		String newName = "New Layer Name";
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerRename));
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		assertTrue("Rename Dialog should be shown", mSolo.searchText("Enter new layer name"));
		getInstrumentation().sendStringSync(newName);
		mSolo.clickOnText("OK");
		assertTrue("Rename Layer didn't work", mSolo.searchText(newName));
	}

	public void testRenameMergedLayer() {
		String newName = "Merged Layer";
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerMerge));
		mSolo.clickOnText(LAYER_ZERO);
		mSolo.clickOnText(LAYER_TWO);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerRename));
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		getInstrumentation().sendStringSync(newName);
		mSolo.clickOnText("OK");
		assertTrue("Rename Layer didn't work", mSolo.searchText(newName));
	}

	public void testLockLayer() {
		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 5);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);
		int colorTransparent = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerLock));

		mSolo.clickOnScreen(checkScreenPoint.x, checkScreenPoint.y);
		mSolo.goBack();
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		int colorAfterDraw = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be transparent.", colorTransparent, colorAfterDraw);

	}

	public void testSetLayerInvisible() {
		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 5);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);
		int colorTransparent = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		mSolo.clickOnScreen(checkScreenPoint.x, checkScreenPoint.y);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerVisible));
		mSolo.goBack();

		int colorAfterDraw = PaintroidApplication.drawingSurface.getPixel(checkScreenPoint);
		assertEquals("Pixel color should be transparent.", Color.TRANSPARENT, colorAfterDraw);

	}

	public void testLockLayerSetInvisible() {
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerVisible));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerLock));
		mSolo.goBack();

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerVisible));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerLock));
		mSolo.goBack();
	}


	public void testTrySetMoreLayersThanLimit() {

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));

		assertFalse("Should be max 7 Layers", mSolo.searchText(LAYER_SEVEN));
	}

	public void testMultipleLayersNewImageDiscardOld() {
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.goBack();

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForDialogToOpen();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
		mSolo.waitForDialogToClose();
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.clickOnView(mButtonTopLayer);
		assertTrue("Layers dialog not visible",
				mSolo.waitForText(mSolo.getString(R.string.layers_title), 1, TIMEOUT, true));
	}

	public void testMultipleLayersNewImageSaveOld() {
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerNew));
		mSolo.goBack();

		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForDialogToOpen();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));
		mSolo.waitForDialogToClose();
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.clickOnView(mButtonTopLayer);
		assertTrue("Layers dialog not visible",
				mSolo.waitForText(mSolo.getString(R.string.layers_title), 1, TIMEOUT, true));
	}

	public void testOpacityChange() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.sleep(30);
		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);
		selectTool(ToolType.BRUSH);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(10);
		int opacityPixel = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Checking Opaque Point", Color.BLACK, opacityPixel);
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.setProgressBar(0, 50);

		SeekBar opacitySeekbar = (SeekBar) mSolo.getView(R.id.seekbar_layer_opacity);
		assertEquals("SetOpacity Seekbar", opacitySeekbar.getProgress(), 50);
		mSolo.goBack();

		int comparePixel = Color.argb(255, 0, 0, 0);
		//TODO RIGHT PIXEL VALUES
		opacityPixel = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Checking transparent Point", comparePixel, opacityPixel);

	}

}
