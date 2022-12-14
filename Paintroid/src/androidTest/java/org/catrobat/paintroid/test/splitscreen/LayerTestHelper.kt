package org.catrobat.paintroid.test.splitscreen

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.hamcrest.Matchers

object LayerTestHelper {
    fun testAddOneLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
    }

    fun testButtonsAddOneLayer() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .checkLayerCount(2)
        LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
                .check(
                        assertIfAddLayerButtonIsEnabled()
                )
        assertIfDeleteLayerButtonIsDisabled()
        LayerMenuViewInteraction.onLayerMenuView()
                .performAddLayer()
                .performAddLayer()
                .checkLayerCount(4)
        LayerMenuViewInteraction.onLayerMenuView()
                .performDeleteLayer()
                .performDeleteLayer()
                .performDeleteLayer()
                .checkLayerCount(1)
    }

    private fun assertIfDeleteLayerButtonIsDisabled() {
        LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
                .check(
                        ViewAssertions.matches(
                                Matchers.allOf(
                                        ViewMatchers.isEnabled(),
                                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete)
                                )
                        )
                )
    }

    private fun assertIfAddLayerButtonIsEnabled() = ViewAssertions.matches(
            Matchers.allOf(
                    ViewMatchers.isEnabled(),
                    UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add)
            )
    )
}
