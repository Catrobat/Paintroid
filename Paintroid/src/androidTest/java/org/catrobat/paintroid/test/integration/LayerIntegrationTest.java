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
import android.view.View;
import android.widget.ListView;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;

public class LayerIntegrationTest extends BaseIntegrationTestClass {

	private static final String TOO_MANY_LAYERS = "Too many layers";


	public LayerIntegrationTest() throws Exception {
		super();
	}

	public void testShowLayerMenu() {

		mSolo.clickOnView(mButtonTopLayer);
		assertEquals("Layer menu not visible", mSolo.getView(R.id.nav_view_layer).getVisibility(), View.VISIBLE);
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);

		float clickCoordinateX = mScreenWidth - 5;
		float clickCoordinateY = mScreenHeight / 2;
		mSolo.drag(clickCoordinateX, clickCoordinateX - mScreenWidth / 2, clickCoordinateY, clickCoordinateY, 20);
		assertEquals("Layer menu not visible", mSolo.getView(R.id.nav_view_layer).getVisibility(), View.VISIBLE);
	}

	public void testAddOneLayer() {
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		int heightOneLayer = mSolo.getView(R.id.nav_view_layer).getHeight();

		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		int heightTwoLayer = mSolo.getView(R.id.nav_view_layer).getHeight();
		assertTrue("No Layer added", heightOneLayer < heightTwoLayer);
	}

	public void testDeleteEmptyLayer() {
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		int heightOneLayer = mSolo.getView(R.id.nav_view_layer).getHeight();

		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		int heightAddedLayer = mSolo.getView(R.id.nav_view_layer).getHeight();
		assertTrue("No Layer added", heightOneLayer < heightAddedLayer);

		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_delete));
		int heightDeletedLayer = mSolo.getView(R.id.nav_view_layer).getHeight();
		assertTrue("No Layer added", heightDeletedLayer < heightAddedLayer);
	}

	public void testDeleteFilledLayer() {
		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 3);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);

		mSolo.clickOnScreen(checkScreenPoint.x, checkScreenPoint.y);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		int colorLayerZero = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		int heightAddedLayer = mSolo.getView(R.id.nav_view_layer).getHeight();

		int[] location = new int[2];
		mSolo.getView(R.id.nav_view_layer).getLocationOnScreen(location);

		ListView layerList = (ListView) mSolo.getView(R.id.nav_layer_list);
		View oldLayer = layerList.getChildAt(1);
		mSolo.clickOnView(oldLayer);

		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_delete));
		int heightDeletedLayer = mSolo.getView(R.id.nav_view_layer).getHeight();
		assertTrue("No Layer added", heightDeletedLayer < heightAddedLayer);

		mSolo.goBack();
		int colorLayerOne = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertFalse("Pixel color should be transparent", colorLayerZero == colorLayerOne);

	}

	public void testSwitchBetweenFilledLayers() {
		PointF leftPointOnScreen = new PointF(40, mScreenHeight / 2);
		PointF rightPointOnScreen = new PointF(mScreenWidth - 10, mScreenHeight / 2);
		PointF UpperScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 3);
		PointF UpperCanvasPoint = Utils.getCanvasPointFromScreenPoint(UpperScreenPoint);
		PointF LowerScreenPoint = new PointF(mScreenWidth / 2, 2 * mScreenHeight / 3);
		PointF LowerCanvasPoint = Utils.getCanvasPointFromScreenPoint(LowerScreenPoint);

		mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 2);
		selectTool(ToolType.FILL);
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		int colorUpperSide = PaintroidApplication.drawingSurface.getPixel(UpperCanvasPoint);
		int colorLowerSide = PaintroidApplication.drawingSurface.getPixel(LowerCanvasPoint);
		assertTrue("Bitmap should be half transparent", colorUpperSide != colorLowerSide);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		mSolo.goBack();

		selectTool(ToolType.BRUSH);
		mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 1);
		selectTool(ToolType.FILL);
		mSolo.clickOnScreen(mScreenWidth / 2, 2 * mScreenHeight / 3);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);

		colorUpperSide = PaintroidApplication.drawingSurface.getPixel(UpperCanvasPoint);
		colorLowerSide = PaintroidApplication.drawingSurface.getPixel(LowerCanvasPoint);
		assertTrue("Bitmap should show both layer", colorUpperSide == colorLowerSide);
	}

	public void testTryDeleteOnlyLayer() {
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		int heightOneLayer = mSolo.getView(R.id.nav_view_layer).getHeight();
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_delete));
		int heightDeletedLayer = mSolo.getView(R.id.nav_view_layer).getHeight();
		assertTrue("Layer 0 shouldn't be deleted", heightOneLayer == heightDeletedLayer);
	}

	public void testMergeTwoEmptyLayers() {
		//TODO: implement merge or convert to junit
	}

	public void testMergeTwoFilledLayers() {
		//TODO: implement merge or convert to junit
	}


	public void testSetLayerInvisible() {
		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 5);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);
		mSolo.clickOnScreen(checkScreenPoint.x, checkScreenPoint.y);

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		//TODO: call visibility button of Layer
		//mSolo.clickOnView(mSolo.getView(R.id.mButtonLayerVisible));
		mSolo.goBack();

		int colorAfterDraw = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Pixel color should be transparent.", Color.TRANSPARENT, colorAfterDraw);

	}

	public void testTrySetMoreLayersThanLimit() {

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));

		assertFalse("Should be max 4 Layers", mSolo.searchText(TOO_MANY_LAYERS));

	}

	public void testMultipleLayersNewImageDiscardOld() {
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		int heightOneLayer = mSolo.getView(R.id.nav_view_layer).getHeight();
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));

		openMenu();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
		mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
		mSolo.clickOnText(mSolo.getString(R.string.menu_new_image_empty_image));
		mSolo.waitForDialogToClose();

		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));

		int heightNewLayerDialog = mSolo.getView(R.id.nav_view_layer).getHeight();
		assertTrue("Layers dialog reset",	heightOneLayer == heightNewLayerDialog);
		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 3);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);
		int colorAfterDraw = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Drawing surface is empty", Color.TRANSPARENT, colorAfterDraw);
	}

	public void testMultipleLayersNewImageSaveOld() {
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
		mSolo.clickOnView(mButtonTopLayer);
		mSolo.waitForView(mSolo.getView(R.id.nav_view_layer));
		int heightOneLayer = mSolo.getView(R.id.nav_view_layer).getHeight();
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));
		mSolo.clickOnView(mSolo.getView(R.id.layer_side_nav_button_add));

		openMenu();
		mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
		mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
		mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));
		mSolo.waitForText(mSolo.getString(R.string.saved));
		mSolo.clickOnText(mSolo.getString(R.string.menu_new_image_empty_image));
		mSolo.waitForDialogToClose();

		int heightNewLayerDialog = mSolo.getView(R.id.nav_view_layer).getHeight();
		assertTrue("Layers dialog reset",	heightOneLayer == heightNewLayerDialog);
		PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 3);
		PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);
		int colorAfterDraw = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
		assertEquals("Drawing surface is empty", Color.TRANSPARENT, colorAfterDraw);
	}

	public void testResizingThroughAllLayers() {
		//TODO
	}

}
