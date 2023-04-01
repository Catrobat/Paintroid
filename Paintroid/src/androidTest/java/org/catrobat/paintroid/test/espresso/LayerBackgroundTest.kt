package org.catrobat.paintroid.test.espresso

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.ui.LayerAdapter.Companion.getBottomBackground
import org.catrobat.paintroid.ui.LayerAdapter.Companion.getCenterBackground
import org.catrobat.paintroid.ui.LayerAdapter.Companion.getSingleBackground
import org.catrobat.paintroid.ui.LayerAdapter.Companion.getTopBackground
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Locale

@RunWith(Parameterized::class)
class LayerBackgroundTest(private val language: String) {

    private lateinit var mainActivity: MainActivity
    private lateinit var layerAdapter: LayerContracts.Adapter

    companion object {
        private const val ENGLISH = "en"
        private const val ARABIC = "ar"

        @JvmStatic
        @Parameterized.Parameters(name = "Language: {0}")
        fun data() = arrayOf(
                arrayOf(ARABIC),
                arrayOf(ENGLISH)
        )
    }

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        layerAdapter = mainActivity.layerAdapter
        setLanguage(language)
    }

    @Test
    fun testOneLayer() {
        var actualBackground = getActualBackground(0)
        Assert.assertEquals(actualBackground, getSingleBackground()?.constantState)
    }

    @Test
    fun testTwoLayersTopSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()

        var backgroundTop = getActualBackground(0)
        var backgroundBottom = getActualBackground(1)

        Assert.assertEquals(backgroundTop, getTopBackground(true)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(false)?.constantState)
    }

    @Test
    fun testTwoLayersBottomSelected() {
        LayerMenuViewInteraction.onLayerMenuView()
                .performOpen()
                .performAddLayer()
                .performSelectLayer(1)

        var backgroundTop = getActualBackground(0)
        var backgroundBottom = getActualBackground(1)

        Assert.assertEquals(backgroundTop, getTopBackground(false)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(true)?.constantState)
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

        Assert.assertEquals(backgroundTop, getTopBackground(true)?.constantState)
        Assert.assertEquals(backgroundCenter, getCenterBackground(false)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(false)?.constantState)
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

        Assert.assertEquals(backgroundTop, getTopBackground(false)?.constantState)
        Assert.assertEquals(backgroundCenter, getCenterBackground(true)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(false)?.constantState)
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

        Assert.assertEquals(backgroundTop, getTopBackground(false)?.constantState)
        Assert.assertEquals(backgroundCenter, getCenterBackground(false)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(true)?.constantState)
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

        Assert.assertEquals(backgroundTop, getTopBackground(true)?.constantState)
        Assert.assertEquals(backgroundUpperCenter, getCenterBackground(false)?.constantState)
        Assert.assertEquals(backgroundLowerCenter, getCenterBackground(false)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(false)?.constantState)
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

        Assert.assertEquals(backgroundTop, getTopBackground(false)?.constantState)
        Assert.assertEquals(backgroundUpperCenter, getCenterBackground(true)?.constantState)
        Assert.assertEquals(backgroundLowerCenter, getCenterBackground(false)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(false)?.constantState)
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

        Assert.assertEquals(backgroundTop, getTopBackground(false)?.constantState)
        Assert.assertEquals(backgroundUpperCenter, getCenterBackground(false)?.constantState)
        Assert.assertEquals(backgroundLowerCenter, getCenterBackground(true)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(false)?.constantState)
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

        Assert.assertEquals(backgroundTop, getTopBackground(false)?.constantState)
        Assert.assertEquals(backgroundUpperCenter, getCenterBackground(false)?.constantState)
        Assert.assertEquals(backgroundLowerCenter, getCenterBackground(false)?.constantState)
        Assert.assertEquals(backgroundBottom, getBottomBackground(true)?.constantState)
    }

    private fun getActualBackground(position: Int): Drawable.ConstantState? {
        val layout = layerAdapter?.getViewHolderAt(position)?.getViewLayout()
        return layout?.background?.constantState
    }

    private fun setLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config: Configuration = mainActivity.resources.configuration
        config.setLocale(locale)
        mainActivity.resources.updateConfiguration(config, mainActivity.resources.displayMetrics)
    }
}
