package org.catrobat.paintroid.test.splitscreen

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType

object ImportToolTestHelper {
    fun testImportDialogShownOnImportToolSelected() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.IMPORTPNG)
        Espresso.onView(withId(R.id.pocketpaint_dialog_import_stickers)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        Espresso.onView(withId(R.id.pocketpaint_dialog_import_gallery)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
}
