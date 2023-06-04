package org.catrobat.paintroid.test.espresso

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.LandingPageActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.junit.*
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LandingPageActivityIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(LandingPageActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private lateinit var activity: LandingPageActivity

    companion object {
        private lateinit var deletionFileList: ArrayList<File?>
    }

    @Before
    fun setUp() {
        deletionFileList = ArrayList()
        activity = launchActivityRule.activity
    }

    @After
    fun tearDown() {
        for (file in deletionFileList) {
            if (file != null && file.exists()) {
                Assert.assertTrue(file.delete())
            }
        }
    }

    @Test
    fun testTopAppBarDisplayed(){
        onView(ViewMatchers.isAssignableFrom(Toolbar::class.java))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testAppBarTitleDisplayPocketPaint() {
        onView(withText("Pocket Paint"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testTwoFABDisplayed(){
        onView(withId(R.id.pocketpaint_fab_load_image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_fab_new_image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testMyProjectsTextDisplayed(){
        onView(withText("My Projects"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testNewImage() {
        onView(withId(R.id.pocketpaint_fab_new_image)).perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        pressBack()
        onView(withText(R.string.discard_button_text)).perform(click())
        onView(withId(R.id.pocketpaint_fab_new_image)).perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testLoadImageIntentStarted() {
        Intents.init()
        val intent = Intent()
        intent.data = createTestImageFile()
        val resultOK = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK)
        onView(withId(R.id.pocketpaint_fab_load_image)).perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        Intents.release()
    }

    private fun createTestImageFile(): Uri? {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.jpg")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val resolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            val fos = imageUri?.let { resolver.openOutputStream(it) }
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos))
            assert(fos != null)
            fos?.close()
        } catch (e: IOException) {
            throw AssertionError("Picture file could not be created.", e)
        }
        val imageFile = File(imageUri?.path, "testfile.jpg")
        deletionFileList.add(imageFile)
        return imageUri
    }
}