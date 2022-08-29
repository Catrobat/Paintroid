@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import java.io.File
import java.io.IOException
import java.util.Objects

@RunWith(AndroidJUnit4::class)
class MainActivityIntentTest {
    @get:Rule
    var launchActivityTestRule = ActivityTestRule(MainActivity::class.java)
    private var contentResolver: ContentResolver? = null
    @Before
    fun setUp() {
        deletionFileList = ArrayList()
        contentResolver = launchActivityTestRule.activity.contentResolver
    }

    @After
    fun tearDown() {
        for (file in deletionFileList) {
            if (file.exists()) {
                Assert.assertTrue(file.delete())
            }
        }
    }

    @Test
    fun testAppliedChangesAfterOrientationChangePersist() {
        MockitoAnnotations.initMocks(this)
        Intents.init()
        val testUri = createTestImageFile()
        val intent = Intent(Intent.ACTION_SEND)
            .setType("image/*")
            .putExtra(Intent.EXTRA_STREAM, testUri)
        launchActivityTestRule.launchActivity(intent)
        Assert.assertNull(launchActivityTestRule.activity.model.savedPictureUri)
        Assert.assertNull(launchActivityTestRule.activity.model.cameraImageUri)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.FILL)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        launchActivityTestRule.activity.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        Intents.release()
    }

    private fun createTestImageFile(): Uri? {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.jpeg")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val imageUri =
            contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            val fos = Objects.requireNonNull(imageUri)
                ?.let { contentResolver?.openOutputStream(it) }
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos))
            assert(fos != null)
            fos?.close()
        } catch (e: IOException) {
            throw AssertionError("Picture file could not be created.", e)
        }
        val imageFile = File(imageUri?.path, "testfile.jpeg")
        deletionFileList.add(imageFile)
        return imageUri
    }

    companion object {
        private lateinit var deletionFileList: ArrayList<File>
    }
}
