package org.catrobat.paintroid.test.espresso

import android.graphics.drawable.Drawable
import android.view.View
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.catrobat.paintroid.R

class LayerBackgroundTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var mainActivity: MainActivity
    private lateinit var layerAdapter: LayerContracts.Adapter

    private var singleSelected = "layer_item_single_selected"
    private var topSelected = "layer_item_top_selected"
    private var topUnselected = "layer_item_top_unselected"
    private var bottomSelected = "layer_item_btm_selected"
    private var bottomUnselected = "layer_item_btm_unselected"

    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        layerAdapter = mainActivity.layerAdapter
    }

    @Test
    fun testOneLayer() {
        var actualBackground = layerAdapter?.getViewHolderAt(0)?.getViewLayout()?.background
        Assert.assertEquals(actualBackground?.constantState, getExpectedBackground(singleSelected))
    }

    @Test
    fun testTwoLayersTopSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()

        var backgroundTop = getActualBackground(0)
        var backgroundBottom = getActualBackground(1)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topSelected))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testTwoLayersBottomSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performSelectLayer(1)

        var backgroundTop = getActualBackground(0)
        var backgroundBottom = getActualBackground(1)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomSelected))
    }

    @Test
    fun testThreeLayersTopSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()

        var backgroundTop = getActualBackground(0)
        var backgroundCenter = getActualBackground(1)
        var backgroundBottom = getActualBackground(2)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topSelected))
        Assert.assertEquals(backgroundCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testThreeLayersCenterSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performSelectLayer(1)

        var backgroundTop = getActualBackground(0)
        var backgroundCenter = getActualBackground(1)
        var backgroundBottom = getActualBackground(2)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundCenter, getExpectedBackground(true))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testThreeLayersBottomSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performScrollToPositionInLayerNavigation(2)
                .performSelectLayer(2)

        var backgroundTop = getActualBackground(0)
        var backgroundCenter = getActualBackground(1)
        var backgroundBottom = getActualBackground(2)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomSelected))
    }

    @Test
    fun testFourLayersTopSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundLowerCenter = getActualBackground(2)
        val backgroundBottom = getActualBackground(3)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topSelected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testFourLayersUpperCenterSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performSelectLayer(1)

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundLowerCenter = getActualBackground(2)
        val backgroundBottom = getActualBackground(3)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(true))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testFourLayersLowerCenterSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performScrollToPositionInLayerNavigation(2)
                .performSelectLayer(2)

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundLowerCenter = getActualBackground(2)
        val backgroundBottom = getActualBackground(3)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(true))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testFourLayersBottomSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performScrollToPositionInLayerNavigation(3)
                .performSelectLayer(3)

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundLowerCenter = getActualBackground(2)
        val backgroundBottom = getActualBackground(3)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomSelected))
    }

    @Test
    fun testFiveLayersTopSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundCenter = getActualBackground(2)
        val backgroundLowerCenter = getActualBackground(3)
        val backgroundBottom = getActualBackground(4)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topSelected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testFiveLayersUpperCenterSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performSelectLayer(1)

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundCenter = getActualBackground(2)
        val backgroundLowerCenter = getActualBackground(3)
        val backgroundBottom = getActualBackground(4)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(true))
        Assert.assertEquals(backgroundCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testFiveLayersCenterSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performScrollToPositionInLayerNavigation(2)
                .performSelectLayer(2)

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundCenter = getActualBackground(2)
        val backgroundLowerCenter = getActualBackground(3)
        val backgroundBottom = getActualBackground(4)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundCenter, getExpectedBackground(true))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testFiveLayersLowerCenterSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performScrollToPositionInLayerNavigation(3)
                .performSelectLayer(3)

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundCenter = getActualBackground(2)
        val backgroundLowerCenter = getActualBackground(3)
        val backgroundBottom = getActualBackground(4)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(true))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomUnselected))
    }

    @Test
    fun testFiveLayersBottomSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performAddLayer()
                .performScrollToPositionInLayerNavigation(4)
                .performSelectLayer(4)

        val backgroundTop = getActualBackground(0)
        val backgroundUpperCenter = getActualBackground(1)
        val backgroundCenter = getActualBackground(2)
        val backgroundLowerCenter = getActualBackground(3)
        val backgroundBottom = getActualBackground(4)

        Assert.assertEquals(backgroundTop, getExpectedBackground(topUnselected))
        Assert.assertEquals(backgroundUpperCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundLowerCenter, getExpectedBackground(false))
        Assert.assertEquals(backgroundBottom, getExpectedBackground(bottomSelected))
    }

    private fun getExpectedBackground(drawableName: String): Drawable.ConstantState? {
        val isRTL = mainActivity.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
        val suffix = if (isRTL) "_rtl" else "_ltr"
        val resourceName = drawableName + suffix
        val id = mainActivity.resources.getIdentifier(resourceName, "drawable", mainActivity.packageName)
        return mainActivity.getDrawable(id)!!.constantState
    }

    private fun getExpectedBackground(selected: Boolean): Drawable.ConstantState? {
        val selectedID = R.drawable.layer_item_center_selected
        val unselectedID = R.drawable.layer_item_center_unselected
        return if (selected) {
            mainActivity.getDrawable(selectedID)?.constantState
        } else {
            mainActivity.getDrawable(unselectedID)?.constantState
        }
    }

    private fun getActualBackground(position: Int): Drawable.ConstantState? {
        val layout = layerAdapter?.getViewHolderAt(position)?.getViewLayout()
        return layout?.background?.constantState
    }
}
