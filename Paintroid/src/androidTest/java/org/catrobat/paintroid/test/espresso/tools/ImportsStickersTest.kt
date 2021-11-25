package org.catrobat.paintroid.test.espresso.tools

import android.net.Uri
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.*
import java.io.File

class ImportsStickersTest {
    @get:Rule
    val launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val screenshotOnFailRule = ScreenshotOnFailRule()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck()

    private lateinit var uriFile: Uri
    private lateinit var activity: MainActivity

    companion object {
        private const val IMAGE_NAME = "fileName"
    }
    @Before
    fun setUp() {
        activity = launchActivityRule.activity
    }

    @After
    fun tearDown() {
        with(File(uriFile.path!!)) {
            if (exists()) {
                delete()
            }
        }
    }

    @Test
    fun testWriteAndReadCatrobatImage() {
        ToolBarViewInteraction.onToolBarView()
                .performSelectTool(ToolType.IMPORTPNG)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_dialog_import_stickers))
                .perform(ViewActions.click())

        Thread.sleep(10_000)
        /*DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        TopBarViewInteraction.onTopBarView()
                .performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_save_image))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_save_dialog_spinner))
                .perform(ViewActions.click())
        Espresso.onData(
                AllOf.allOf(
                        Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)),
                        Matchers.`is`<String>(CATROBAT_IMAGE_ENDING)
                )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_image_name_save_text))
                .perform(ViewActions.replaceText(IMAGE_NAME))
        Espresso.onView(ViewMatchers.withText(R.string.save_button_text))
                .perform(ViewActions.click())
        uriFile = activity.model.savedPictureUri!!
        Assert.assertNotNull(uriFile)
        Assert.assertNotNull(activity.workspace.getCommandSerializationHelper().readFromFile(uriFile))*/
    }
}
