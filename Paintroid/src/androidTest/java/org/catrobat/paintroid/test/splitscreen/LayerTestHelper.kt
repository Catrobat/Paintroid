package org.catrobat.paintroid.test.splitscreen

import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction

object LayerTestHelper {
    fun testAddOneLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
    }
}
