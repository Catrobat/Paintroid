/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration;

import android.graphics.PointF;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;

public class LayerIntegrationTest extends BaseIntegrationTestClass{

    private static final String NEW_LAYER = "New Layer";
    private static final String DELETE_LAYER = "Delete Layer";
    private static final String MERGE = "Merge";
    private static final String VISIBLE = "Visible";
    private static final String RENAME = "Rename";
    private static final String LOCK = "Lock";
    private static final String LAYER_ZERO = "Layer 0";
    private static final String LAYER_ONE = "Layer 1";
    private static final String LAYER_TWO = "Layer 2";


    public LayerIntegrationTest() throws Exception {
        super();
    }

    public void testShowLayerMenu () {
        mSolo.clickOnView(mMenuBottomLayer);
        assertTrue("Layers dialog not visible",
                mSolo.waitForText(mSolo.getString(R.string.layers_title), 1, TIMEOUT, true));
    }

    public void testAddOneLayer () {
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        assertTrue("First Layer not visible", mSolo.searchText(LAYER_ZERO));
        mSolo.clickOnText(NEW_LAYER);
        assertTrue("New Layer not visible", mSolo.searchText(LAYER_ONE));
    }

    public void testDeleteEmptyLayer() {
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        assertTrue("New Layer not visible", mSolo.searchText(LAYER_ONE));
        mSolo.clickOnText(LAYER_ONE);
        mSolo.clickOnText(DELETE_LAYER);
        assertFalse("New Layer should be deleted", mSolo.searchText(LAYER_ONE));
    }

    public void testDeleteFilledLayer() {
        PointF checkScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 3);
        PointF checkCanvasPoint = Utils.getCanvasPointFromScreenPoint(checkScreenPoint);

        mSolo.clickOnScreen(checkScreenPoint.x, checkScreenPoint.y);
        mSolo.waitForDialogToClose(SHORT_TIMEOUT);
        int colorLayerZero = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);

        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickOnText(DELETE_LAYER);

        assertFalse("Layer 0 should be deleted", mSolo.searchText(LAYER_ZERO));
        mSolo.clickLongOnText(LAYER_ONE);
        int colorLayerOne = PaintroidApplication.drawingSurface.getPixel(checkCanvasPoint);
        assertFalse("Pixel color should be transparent", colorLayerZero == colorLayerOne);
    }

    public void testSwitchBetweenFilledLayers() {
        PointF leftPointOnScreen = new PointF(10, mScreenHeight / 2);
        PointF rightPointOnScreen = new PointF(mScreenWidth-10, mScreenHeight / 2);
        PointF UpperScreenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 3);
        PointF UpperCanvasPoint = Utils.getCanvasPointFromScreenPoint(UpperScreenPoint);
        PointF LowerScreenPoint = new PointF(mScreenWidth / 2, 2 * mScreenHeight / 3);
        PointF LowerCanvasPoint = Utils.getCanvasPointFromScreenPoint(LowerScreenPoint);

        mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 1);
        selectTool(ToolType.FILL);
        mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
        mSolo.waitForDialogToClose(SHORT_TIMEOUT);

        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickLongOnText(LAYER_ONE);

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
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(DELETE_LAYER);
        assertTrue("Layer 0 shouldn't be deleted", mSolo.searchText(LAYER_ZERO));
    }

    public void testMergeTwoEmptyLayers() {
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        assertTrue("New Layer not visible", mSolo.searchText(LAYER_ONE));
        mSolo.clickOnText(MERGE);
        mSolo.clickOnText(LAYER_ONE);
        assertTrue("Merge two Layers didn't work", mSolo.searchText(LAYER_ZERO + "/" + LAYER_ONE));
    }

    public void testMergeTwoFilledLayers() {
        PointF leftPointOnScreen = new PointF(10, mScreenHeight / 2);
        PointF rightPointOnScreen = new PointF(mScreenWidth-10, mScreenHeight / 2);
        mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 1);
        selectTool(ToolType.FILL);
        mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
        mSolo.waitForDialogToClose(SHORT_TIMEOUT);

        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickLongOnText(LAYER_ONE);

        selectTool(ToolType.BRUSH);
        mSolo.drag(leftPointOnScreen.x, rightPointOnScreen.x, leftPointOnScreen.y, rightPointOnScreen.y, 1);
        selectTool(ToolType.FILL);
        mSolo.clickOnScreen(mScreenWidth / 2, 2 * mScreenHeight / 3);
        mSolo.waitForDialogToClose(SHORT_TIMEOUT);

        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(MERGE);
        mSolo.clickOnText(LAYER_ZERO);
        assertTrue("Merge two Layers didn't work", mSolo.searchText(LAYER_ONE + "/" + LAYER_ZERO));

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
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(RENAME);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        assertTrue("Rename Dialog should be shown", mSolo.searchText("Enter new layer name"));
        getInstrumentation().sendStringSync(newName);
        mSolo.clickOnText("OK");
        assertTrue("Rename Layer didn't work", mSolo.searchText(newName));
    }

    public void testRenameMergedLayer() {
        String newName = "Merged Layer";
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickOnText(MERGE);
        mSolo.clickOnText(LAYER_ONE);
        mSolo.clickOnText(RENAME);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        getInstrumentation().sendStringSync(newName);
        mSolo.clickOnText("OK");
        assertTrue("Rename Layer didn't work", mSolo.searchText(newName));
    }

    public void testMergeRenamedLayer() {

    }

    public void testLockLayer() {

    }

    public void testSetLayerInvisible() {

    }

    public void testLockLayerSetInvisible() { //and unlock

    }

    public void testChangeLayerOpacity() {

    }

    public void testMergeLayerWithLowerOpacity() {

    }

    public void testSetManyLayers() {

    }

    public void testDrawAtAllLayers() {

    }

    public void testMultipleLayersNewImage() {
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickLongOnText(LAYER_TWO);

        mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
        mSolo.waitForDialogToOpen();
        mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));

        mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);

        mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
        mSolo.waitForDialogToClose();

        mSolo.clickOnView(mMenuBottomLayer);
        assertTrue("Layers dialog not visible",
                mSolo.waitForText(mSolo.getString(R.string.layers_title), 1, TIMEOUT, true));


    }
}
