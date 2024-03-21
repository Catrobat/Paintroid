package org.catrobat.paintroid.test.splitscreen

import android.graphics.Color
import android.graphics.PointF
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.junit.Assert

object ClippingToolTestHelper {
    private lateinit var activity: MainActivity
    private lateinit var workspace: Workspace
    private lateinit var toolReference: ToolReference

    private lateinit var middle: PointF
    private lateinit var middleLeft: PointF
    private lateinit var middleTop: PointF
    private lateinit var middleBot: PointF
    private lateinit var middleRight: PointF

    private lateinit var middlePoint1: PointF
    private lateinit var middlePoint2: PointF
    private lateinit var middlePoint3: PointF
    private lateinit var middlePoint4: PointF

    fun setupEnvironment(mainActivity: MainActivity) {
        activity = mainActivity
        workspace = mainActivity.workspace
        toolReference = mainActivity.toolReference

        middle = PointF((workspace.width / 2).toFloat(), (workspace.height / 2).toFloat())
        middleLeft = PointF(middle.x - workspace.width / 4, middle.y)
        middleTop = PointF(middle.x, middle.y - workspace.height / 4)
        middleBot = PointF(middle.x, middle.y + workspace.height / 4)
        middleRight = PointF(middle.x + workspace.width / 4, middle.y)

        middlePoint1 = PointF(middle.x - workspace.width / 4, middle.y - workspace.height / 4)
        middlePoint2 = PointF(middle.x + workspace.width / 4, middle.y - workspace.height / 4)
        middlePoint3 = PointF(middle.x + workspace.width / 4, middle.y + workspace.height / 4)
        middlePoint4 = PointF(middle.x - workspace.width / 4, middle.y + workspace.height / 4)
    }

    fun testClipOnBlackBitmap() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)

        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIP)

        ToolPropertiesInteraction.onToolProperties().setColor(Color.YELLOW)

        toolReference.tool?.handleDown(middleLeft)
        toolReference.tool?.handleMove(middlePoint1)
        toolReference.tool?.handleMove(middleTop)
        toolReference.tool?.handleMove(middlePoint2)
        toolReference.tool?.handleMove(middleRight)
        toolReference.tool?.handleMove(middlePoint3)
        toolReference.tool?.handleMove(middleBot)
        toolReference.tool?.handleMove(middlePoint4)
        toolReference.tool?.handleUp(middleLeft)

        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()

        val inAreaX = middle.x - 10
        val inAreaY = middle.y - 10

        val outOfAreaX = workspace.width - 10
        val outOfAreaY = workspace.height - 10

        val colorInArea =
            workspace.bitmapOfCurrentLayer?.getPixel(inAreaX.toInt(), inAreaY.toInt())
        val colorOutOfArea = workspace.bitmapOfCurrentLayer?.getPixel(outOfAreaX, outOfAreaY)

        Assert.assertEquals(colorInArea, Color.BLACK)
        Assert.assertEquals(colorOutOfArea, Color.TRANSPARENT)
    }
}
