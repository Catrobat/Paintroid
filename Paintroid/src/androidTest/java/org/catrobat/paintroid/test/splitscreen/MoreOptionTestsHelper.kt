package org.catrobat.paintroid.test.splitscreen

import android.graphics.Paint
import android.net.Uri
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.Workspace
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.Assert

object MoreOptionTestsHelper {
    private lateinit var activity: MainActivity
    private lateinit var workspace: Workspace
    private lateinit var toolPaint: ToolPaint
    private lateinit var model: MainActivityContracts.Model

    fun setupEnvironment(mainActivity: MainActivity) {
        activity = mainActivity
        workspace = mainActivity.workspace
        toolPaint = mainActivity.toolPaint
        model = mainActivity.model
    }

    fun testMoreOptionsMenuAboutClosesMoreOptions() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        while (true) {
            try {
                Espresso.onView(ViewMatchers.withText(R.string.pocketpaint_menu_about))
                    .perform(ViewActions.click())
                break
            } catch (e: Exception) {
                when (e) {
                    is PerformException,
                    is NoMatchingViewException ->
                        Espresso.onView(ViewMatchers.withText(R.string.menu_new_image))
                            .perform(ViewActions.swipeUp())
                    else -> throw e
                }
            }
        }
        Espresso.pressBack()
        Espresso.onView(ViewMatchers.withText(R.string.pocketpaint_menu_about))
            .check(ViewAssertions.doesNotExist())
    }

    fun testLoadImageDialog() {
        TopBarViewInteraction.onTopBarView().performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_load_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.menu_replace_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.dialog_warning_new_image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.save_button_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.discard_button_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun testWriteAndReadCatrobatImage() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_save_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(ViewActions.click())
        Espresso.onData(
            AllOf.allOf(
                Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Matchers.`is`<String>(FileIO.FileType.CATROBAT.value)
            )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText("fileName"))
        Espresso.onView(ViewMatchers.withText(R.string.save_button_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.overwrite_button_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())
        var uriFile: Uri = model.savedPictureUri!!
        Assert.assertNotNull(uriFile)

    }

    fun setAntiAliasingNotOnWhenCancelPressed() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        while (true) {
            try {
                Espresso.onView(ViewMatchers.withText(R.string.menu_advanced))
                    .perform(ViewActions.click())
                break
            } catch (e: Exception) {
                when (e) {
                    is PerformException,
                    is NoMatchingViewException ->
                        Espresso.onView(ViewMatchers.withText(R.string.menu_rate_us))
                            .perform(ViewActions.swipeUp())
                    else -> throw e
                }
            }
        }

        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_antialiasing))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.cancel_button_text))
            .perform(ViewActions.click())
        val bitmapPaint: Paint = getCurrentToolBitmapPaint()
        val canvasPaint: Paint = getCurrentToolCanvasPaint()
        Assert.assertTrue("BITMAP_PAINT antialiasing should be on", bitmapPaint.isAntiAlias)
        Assert.assertTrue("CANVAS_PAINT antialiasing should be on", canvasPaint.isAntiAlias)
    }

    private fun getCurrentToolBitmapPaint(): Paint =
        toolPaint.paint

    private fun getCurrentToolCanvasPaint(): Paint =
        toolPaint.previewPaint
}
