package org.catrobat.paintroid.eventlistener;

/**
 * Events related to changes in layer dialog.
 */
public interface RefreshLayerDialogEventListener {
    /**
     * Redraws layer dialog after events like add layer, remove layer, change layer visibility etc.
     * have taken place.
     */
    void onLayerDialogRefreshView();
}
