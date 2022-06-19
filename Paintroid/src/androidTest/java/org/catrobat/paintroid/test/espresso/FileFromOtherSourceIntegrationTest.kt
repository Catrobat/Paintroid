@file:Suppress("DEPRECATION", "UNNECESSARY_SAFE_CALL")

package org.catrobat.paintroid.test.espresso

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.catrobat.paintroid.FileIO.getBitmapFromUri
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.IOException
import java.lang.AssertionError
import java.lang.Exception
import java.util.*

class FileFromOtherSourceIntegrationTest {
    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var resolver: ContentResolver? = null
    private var activity: MainActivity? = null
    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
        deletionFileList = ArrayList()
        activity = launchActivityRule.activity
        resolver = launchActivityRule.activity.contentResolver
    }
    @Test
    fun testGetSharedPictureFromOtherApp() {
        val intent = Intent()
        val receivedUri = createTestImageFile()
        var receivedBitmap: Bitmap? = null
        try {
            receivedBitmap = resolver?.let {
                activity?.let { it1 ->
                    receivedUri?.let { it2 ->
                        getBitmapFromUri(it, it2, it1)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Can't read", "Can't get Bitmap from File")
        }
        Objects.requireNonNull(receivedBitmap)
        intent.data = receivedUri
        intent.type = "image/png"
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, receivedUri)
        launchActivityRule.launchActivity(intent)
        val mainActivityIntent = launchActivityRule.activity.intent
        val intentAction = intent.action
        val intentType = intent.type
        val intentBundle = intent.extras
        Objects.requireNonNull(intentBundle)
        val intentUri = intentBundle!![Intent.EXTRA_STREAM] as Uri?
        val mainActivityIntentAction = mainActivityIntent.action
        val mainActivityIntentType = mainActivityIntent.type
        val mainActivityIntentBundle = mainActivityIntent.extras
        Objects.requireNonNull(mainActivityIntentBundle)
        val mainActivityIntentUri = mainActivityIntentBundle!![Intent.EXTRA_STREAM] as Uri?
        var mainActivityIntentBitmap: Bitmap? = null
        Objects.requireNonNull(mainActivityIntentUri)
        try {
            mainActivityIntentBitmap =
                mainActivityIntentUri?.let {
                    resolver?.let { it1 ->
                        activity?.let { it2 ->
                            getBitmapFromUri(it1, it, it2)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("Can't read", "Can't get Bitmap from File")
        }
        Objects.requireNonNull(mainActivityIntentBitmap)
        Assert.assertEquals(intentAction, mainActivityIntentAction)
        Assert.assertEquals(intentType, mainActivityIntentType)
        Assert.assertEquals(intentUri, mainActivityIntentUri)
        Assert.assertEquals(
            receivedBitmap?.width?.toLong(),
            mainActivityIntentBitmap?.width?.toLong()
        )
        Assert.assertEquals(
            receivedBitmap?.height?.toLong(),
            mainActivityIntentBitmap?.height?.toLong()
        )
    }

    @After
    fun tearDown() {
        deletionFileList?.forEach { file ->
            if (file.exists()) {
                Assert.assertTrue(file.delete())
            }
        }
    }

    private fun createTestImageFile(): Uri? {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.jpg")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val imageUri =
            resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            val requireNonNull = Objects.requireNonNull(imageUri)
            val fos = requireNonNull.let {
                it?.let { it1 -> resolver?.openOutputStream(it1) }
            }
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos))
            assert(fos != null)
            fos!!.close()
        } catch (e: IOException) {
            throw AssertionError("Picture file could not be created.", e)
        }
        val imageFile = File(imageUri?.path, "testfile.jpg")
        deletionFileList?.add(imageFile)
        return imageUri
    }

    companion object {
        private var deletionFileList: ArrayList<File>? = null

        @ClassRule
        var grantPermissionRule: GrantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck()
    }
}
