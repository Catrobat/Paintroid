package org.catrobat.paintroid.test.splitscreen

import android.graphics.Color
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.drawable.DrawableShape

object ShapeToolEraseTestHelper {
    fun testEraseWithFilledShape() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.SHAPE)
            .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.TRANSPARENT)
        ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        ShapeToolOptionsViewInteraction.onShapeToolOptionsView()
            .performSelectShape(DrawableShape.HEART)
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }
}
