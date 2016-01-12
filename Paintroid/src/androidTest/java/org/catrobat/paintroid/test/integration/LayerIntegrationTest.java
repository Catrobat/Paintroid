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

import android.graphics.Color;
import android.graphics.PointF;
import android.widget.SeekBar;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

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

    public void testTrySetMoreLayersThanLimit() {

    }

    public void testDrawAtAllLayers() {

    }

    public void testMultipleLayersNewImageDiscardOld() {
        mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickLongOnText(LAYER_ZERO);

        mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
        mSolo.waitForDialogToOpen();
        mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
        mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
        mSolo.clickOnButton(mSolo.getString(R.string.discard_button_text));
        mSolo.waitForDialogToClose();
        mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
        mSolo.clickOnView(mMenuBottomLayer);
        assertTrue("Layers dialog not visible",
                mSolo.waitForText(mSolo.getString(R.string.layers_title), 1, TIMEOUT, true));
    }

    public void testMultipleLayersNewImageSaveOld() {
        mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.waitForDialogToOpen(SHORT_TIMEOUT);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickOnText(NEW_LAYER);
        mSolo.clickLongOnText(LAYER_ZERO);

        mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image));
        mSolo.waitForDialogToOpen();
        mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_new_image_empty_image));
        mSolo.waitForText(mSolo.getString(R.string.dialog_warning_new_image), 1, TIMEOUT, true);
        mSolo.clickOnButton(mSolo.getString(R.string.save_button_text));
        mSolo.waitForDialogToClose();
        mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 3);
        mSolo.clickOnView(mMenuBottomLayer);
        assertTrue("Layers dialog not visible",
                mSolo.waitForText(mSolo.getString(R.string.layers_title), 1, TIMEOUT, true));
    }

    public void testMergeOrderOverlappingObjects() {

    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void setUp()
    {
        super.setUp();
        mSolo.clickOnMenuItem(getActivity().getString(R.string.menu_new_image));
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.menu_new_image_empty_image));
        mSolo.sleep(30);
    }

    public void testLayersOpen() {
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(30);

        String layers_title = getActivity().getString(R.string.layers_title);

        assertTrue("Layer title not found. Couldn't open Layers menu",
                mSolo.waitForText(layers_title, 1, TIMEOUT, true, false));

        mSolo.goBack();
    }

    public void testCreateSelectDeleteLayer() {
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(30);
        String new_layer_name = "Layer 1";
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_new));
        mSolo.sleep(30);
        assertTrue("New created Layer 'Layer 1' not found",
                mSolo.waitForText(new_layer_name, 1, TIMEOUT, true, false));

        mSolo.clickOnMenuItem(new_layer_name);
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_delete));
        mSolo.sleep(30);
        assertFalse("Couldn't Delete Layer 'Layer 1'",
                mSolo.waitForText(new_layer_name, 1, TIMEOUT, true, false));
        mSolo.goBack();
    }
    public void testRenamingLayer() {
        String testName = "RenamedLayer";
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_new));
        mSolo.sleep(30);
        mSolo.clickOnMenuItem("Layer 1");
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_rename));
        mSolo.sleep(30);
        mSolo.typeText(0, testName);
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_ok));
        mSolo.sleep(30);
        assertTrue("Couldn't rename Layer", mSolo.waitForText(testName));
        mSolo.sleep(30);
        mSolo.goBack();
    }

    public void testLockingLayer() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {
        assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

        PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
        PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

        selectTool(ToolType.BRUSH);
        mSolo.sleep(30);
        mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
        mSolo.sleep(30);

        int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
        assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_lock));
        mSolo.sleep(30);
        mSolo.goBack();

        selectTool(ToolType.ERASER);

        mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
        mSolo.sleep(SHORT_SLEEP);

        int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
        assertEquals("Because of lock, erase should not work", Color.BLACK, colorAfterErase);

        mSolo.sleep(30);
        mSolo.goBack();
    }

    public void testVisibleLayer() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {
        assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
        mSolo.clickOnMenuItem(getActivity().getString(R.string.menu_new_image));
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.menu_new_image_empty_image));
        mSolo.sleep(30);

        PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
        PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);

        selectTool(ToolType.BRUSH);
        mSolo.sleep(30);
        mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
        mSolo.sleep(30);

        int colorLayerVisible = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
        assertEquals("After painting black, pixel should be black", Color.BLACK, colorLayerVisible);

        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_visible));
        mSolo.sleep(30);
        mSolo.goBack();

        int colorLayerInvisible = PaintroidApplication.drawingSurface.getVisiblePixel(canvasPoint);
        assertEquals("After Layer set to invisible color should be transparent", Color.TRANSPARENT, colorLayerInvisible);

        mSolo.sleep(30);
        mSolo.goBack();
    }

    public void testMergeLayers() {
        assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_new));
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_merge));
        mSolo.sleep(30);
        mSolo.clickOnMenuItem("Layer 1");
        mSolo.sleep(30);
        assertTrue(mSolo.searchText("Layer 0/Layer 1"));
    }

    public void testMergeButtonBugfix() {
        assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_new));
        mSolo.sleep(30);
        mSolo.clickOnMenuItem(getActivity().getString(R.string.layer_merge));
        mSolo.sleep(30);
        mSolo.goBack();
        mSolo.sleep(30);
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(30);
        mSolo.clickOnMenuItem("Layer 1");
        mSolo.sleep(30);
        assertFalse(mSolo.searchText("Layer 0/Layer 1"));
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
        mSolo.sleep(10);
        mSolo.clickOnView(mMenuBottomLayer);
        mSolo.sleep(10);
        mSolo.setProgressBar(0, 50);
        mSolo.sleep(10);
        SeekBar opacitySeekbar = (SeekBar) mSolo.getView(R.id.seekbar_layer_opacity);
        mSolo.sleep(10);
        assertEquals("SetOpacity Seekbar", opacitySeekbar.getProgress(), 50);
        mSolo.goBack();
        mSolo.sleep(50);
        int comparePixel = Color.argb(255,0,0,0);
        //TODO RIGHT PIXEL VALUES
        opacityPixel = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
        assertEquals("Checking transparent Point", comparePixel, opacityPixel);
        mSolo.sleep(30);
        mSolo.goBack();
    }

}
