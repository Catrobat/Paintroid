package org.catrobat.paintroid.test.espresso

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.LandingPageActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.adapter.ProjectAdapter
import org.catrobat.paintroid.common.CATROBAT_IMAGE_ENDING
import org.catrobat.paintroid.common.PNG_IMAGE_ENDING
import org.catrobat.paintroid.data.local.dao.ProjectDao
import org.catrobat.paintroid.data.local.database.ProjectDatabase
import org.catrobat.paintroid.model.Project
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt
import org.catrobat.paintroid.test.espresso.util.UiInteractions.waitFor
import org.catrobat.paintroid.test.espresso.util.UiMatcher.atPosition
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.Rule
import org.junit.After
import org.junit.Before
import org.junit.Assert
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

@RunWith(AndroidJUnit4::class)
class LandingPageActivityIntegrationTest {

    private lateinit var database: ProjectDatabase
    private lateinit var dao: ProjectDao

    @get:Rule
    var launchActivityRule = ActivityTestRule(LandingPageActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private lateinit var activity: LandingPageActivity
    private lateinit var intent: Intent
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectAdapter

    companion object {
        private lateinit var deletionFileList: ArrayList<File?>
        private const val PROJECT_NAME = "projectName"
        private const val position = 0
        private val project = Project(
            "name",
            "catrobat/path",
            0,
            0,
            "0x0",
            "CATROBAT",
            0.0,
            "paintroid/path",
            1)
        private val projectList = ArrayList<Project>().apply { add(project) }
    }

    @Before
    fun setUp() {
        database = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, ProjectDatabase::class.java, "projects.db")
            .allowMainThreadQueries()
            .build()
        dao = database.dao
        deletionFileList = ArrayList()
        intent = Intent()
        activity = launchActivityRule.activity
    }

    @After
    fun tearDown() {
        database.dao.deleteAllProjects()
        database.clearAllTables()
        database.close()

        for (file in deletionFileList) {
            if (file != null && file.exists()) {
                Assert.assertTrue(file.delete())
            }
        }

        val projectsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val projectPathToFile = projectsDirectory + File.separator + PROJECT_NAME + "." + CATROBAT_IMAGE_ENDING
        val projectFile = File(projectPathToFile)
        if (projectFile.exists()) {
            projectFile.delete()
        }

        val imagesDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val imagePathToFile = imagesDirectory + File.separator + PROJECT_NAME + "." + PNG_IMAGE_ENDING
        val imageFile = File(imagePathToFile)
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }

    @Test
    fun testTopAppBarDisplayed() {
        onView(isAssignableFrom(Toolbar::class.java))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAppBarTitleDisplayPocketPaint() {
        onView(withText("Pocket Paint"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTwoFABDisplayed() {
        onView(withId(R.id.pocketpaint_fab_load_image))
            .check(matches(isDisplayed()))
        onView(withId(R.id.pocketpaint_fab_new_image))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testMyProjectsTextDisplayed() {
        onView(withText("My Projects"))
            .check(matches(isDisplayed()))
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

    @Test
    fun testProjectListDisplayed() {
        onView(withId(R.id.pocketpaint_projects_list))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testProjectInsertedWithProjectName() {
        onView(withId(R.id.pocketpaint_fab_new_image))
            .perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_project))
            .perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(PROJECT_NAME))
        onView(withText(R.string.save_button_text))
            .perform(click())
        onView(isRoot()).perform(waitFor(1000))
        pressBack()
        launchActivityRule.launchActivity(intent)
        onView(isRoot()).perform(waitFor(300))
        onView(withId(R.id.pocketpaint_projects_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
        onView(withId(R.id.pocketpaint_projects_list))
            .check(matches(atPosition(position, hasDescendant(allOf(
                isAssignableFrom(TextView::class.java),
                withText(PROJECT_NAME)
            )))))
    }

    @Test
    fun testProjectInsertedWithImagePreview() {
        val testName = UUID.randomUUID().toString()
        onView(withId(R.id.pocketpaint_fab_new_image))
            .perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_project))
            .perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(testName))
        onView(withText(R.string.save_button_text))
            .perform(click())
        onView(isRoot()).perform(waitFor(1000))
        pressBack()
        launchActivityRule.launchActivity(intent)
        onView(isRoot()).perform(waitFor(300))
        onView(withId(R.id.pocketpaint_projects_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
        val imagesDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val imagePathToFile = imagesDirectory + File.separator + testName + "." + PNG_IMAGE_ENDING
        val imageFile = File(imagePathToFile)
        onView(withId(R.id.pocketpaint_projects_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                object : ViewAction {
                    override fun getDescription(): String {
                        return "Check if image is correctly displayed"
                    }

                    override fun getConstraints(): Matcher<View> {
                        return isAssignableFrom(ImageView::class.java)
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val imageView = view as? ImageView
                        val expectedBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        val actualBitmap = (imageView?.drawable as? BitmapDrawable)?.bitmap
                        actualBitmap?.sameAs(expectedBitmap)
                    }
                }
            )
        )
    }

    @Test
    fun testProjectOverFlowMenuDetailsDisplayed() {
        insertProjectIntoRecyclerView()
        onView(withId(R.id.pocketpaint_projects_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                object : ViewAction {
                    override fun getConstraints(): Matcher<View> {
                        return allOf(isAssignableFrom(View::class.java), isDisplayed())
                    }

                    override fun getDescription(): String {
                        return "Perform click on ImageView with ID: R.id.iv_pocket_paint_project_more"
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val itemView = view as? ViewGroup
                        val imageView = itemView?.findViewById<ImageView>(R.id.iv_pocket_paint_project_more)
                        imageView?.performClick()
                    }
                }
            )
        )
        onView(withText(R.string.menu_project_detail_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testProjectOverFlowMenuProjectDetail() {
        insertProjectIntoRecyclerView()
        onView(withId(R.id.pocketpaint_projects_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                object : ViewAction {
                    override fun getConstraints(): Matcher<View> {
                        return allOf(isAssignableFrom(View::class.java), isDisplayed())
                    }

                    override fun getDescription(): String {
                        return "Perform click on ImageView with ID: R.id.iv_pocket_paint_project_more"
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val itemView = view as? ViewGroup
                        val imageView = itemView?.findViewById<ImageView>(R.id.iv_pocket_paint_project_more)
                        imageView?.performClick()
                    }
                }
            )
        )
        onView(withText(R.string.menu_project_detail_title))
            .perform(click())
        onView(isRoot()).perform(waitFor(300))
        onView(withText(android.R.string.ok))
            .perform(click())
    }

    @Test
    fun testProjectOverFlowMenuDeleteDisplayed() {
        insertProjectIntoRecyclerView()
        onView(withId(R.id.pocketpaint_projects_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                object : ViewAction {
                    override fun getConstraints(): Matcher<View> {
                        return allOf(isAssignableFrom(View::class.java), isDisplayed())
                    }

                    override fun getDescription(): String {
                        return "Perform click on ImageView with ID: R.id.iv_pocket_paint_project_more"
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val itemView = view as? ViewGroup
                        val imageView = itemView?.findViewById<ImageView>(R.id.iv_pocket_paint_project_more)
                        imageView?.performClick()
                    }
                }
            )
        )
        onView(withText(R.string.menu_project_delete_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testProjectOverFlowMenuProjectDeleteCancel() {
        insertProjectIntoRecyclerView()
        onView(withId(R.id.pocketpaint_projects_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                object : ViewAction {
                    override fun getConstraints(): Matcher<View> {
                        return allOf(isAssignableFrom(View::class.java), isDisplayed())
                    }

                    override fun getDescription(): String {
                        return "Perform click on ImageView with ID: R.id.iv_pocket_paint_project_more"
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val itemView = view as? ViewGroup
                        val imageView = itemView?.findViewById<ImageView>(R.id.iv_pocket_paint_project_more)
                        imageView?.performClick()
                    }
                }
            )
        )
        onView(withText(R.string.menu_project_delete_title))
            .perform(click())
        onView(withText(R.string.cancel_button_text))
            .perform(click())
        onView(withId(R.id.pocketpaint_projects_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
        onView(withId(R.id.pocketpaint_projects_list))
            .check(matches(atPosition(position, hasDescendant(allOf(
                isAssignableFrom(TextView::class.java),
                withText(project.name)
            )))))
    }

    @Test
    fun testProjectOverFlowMenuProjectDelete() {
        val recyclerViewMatcher = withId(R.id.pocketpaint_projects_list)
        insertProjectIntoRecyclerView()
        dao.insertProject(project)
        onView(withId(R.id.pocketpaint_projects_list)).perform(
            actionOnItemAtPosition<ProjectAdapter.ItemViewHolder>(
                position,
                object : ViewAction {
                    override fun getConstraints(): Matcher<View> {
                        return allOf(isAssignableFrom(View::class.java), isDisplayed())
                    }

                    override fun getDescription(): String {
                        return "Perform click on ImageView with ID: R.id.iv_pocket_paint_project_more"
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val itemView = view as? ViewGroup
                        val imageView = itemView?.findViewById<ImageView>(R.id.iv_pocket_paint_project_more)
                        imageView?.performClick()
                    }
                }
            )
        )
        onView(withText(R.string.menu_project_delete_title))
            .perform(click())
        onView(isRoot()).perform(waitFor(100))
        onView(withId(android.R.id.button1))
            .perform(closeSoftKeyboard())
            .perform(scrollTo())
            .perform(click())
        onView(isRoot()).perform(waitFor(300))
        onView(withId(R.id.pocketpaint_projects_list))
            .perform(RecyclerViewActions.scrollToPosition<ProjectAdapter.ItemViewHolder>(position))
        onView(isRoot()).perform(waitFor(300))
        if (isRecyclerViewEmpty(recyclerViewMatcher)) {
            onView(withId(R.id.pocketpaint_projects_list))
                .check(matches(not(hasDescendant(withId(R.id.iv_pocket_paint_project_thumbnail_image)))))
        } else {
            onView(withId(R.id.pocketpaint_projects_list))
                .check(
                    matches(
                        atPosition(
                            position, hasDescendant(
                                allOf(
                                    isAssignableFrom(TextView::class.java),
                                    not(withText(project.name))
                                )
                            )
                        )
                    )
                )
        }
    }

    @Test
    fun testSavedProjectOpen() {
        onView(withId(R.id.pocketpaint_fab_new_image))
            .perform(click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_project))
            .perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(PROJECT_NAME))
        onView(withText(R.string.save_button_text))
            .perform(click())
        onView(isRoot()).perform(waitFor(1000))
        pressBack()
        launchActivityRule.launchActivity(intent)
        onView(isRoot()).perform(waitFor(1000))
        onView(withId(R.id.pocketpaint_projects_list))
            .perform(actionOnItemAtPosition<ProjectAdapter.ItemViewHolder>(position, click()))
        onView(
            allOf(
                withId(R.id.pocketpaint_toolbar),
                hasDescendant(
                    allOf(
                        isAssignableFrom(TextView::class.java),
                        withText(PROJECT_NAME)
                    )
                )
            )
        ).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun testImagePreviewAfterProjectInsert() {
        val testName = UUID.randomUUID().toString()
        onView(withId(R.id.pocketpaint_fab_new_image))
            .perform(click())
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_project))
            .perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(testName))
        onView(withText(R.string.save_button_text))
            .perform(click())
        onView(isRoot()).perform(waitFor(1000))
        pressBack()
        launchActivityRule.launchActivity(intent)
        onView(isRoot()).perform(waitFor(300))
        onView(withId(R.id.pocketpaint_projects_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
        val imagesDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val imagePathToFile = imagesDirectory + File.separator + testName + "." + PNG_IMAGE_ENDING
        val imageFile = File(imagePathToFile)
        val expectedBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        onView(withId(R.id.pocketpaint_image_preview)).check(matches(withBitmap(expectedBitmap)))
    }

    private fun insertProjectIntoRecyclerView() {
        runBlocking {
            recyclerView = activity.findViewById(R.id.pocketpaint_projects_list)
            adapter = ProjectAdapter(
                InstrumentationRegistry.getInstrumentation().targetContext,
                projectList,
                activity.supportFragmentManager
            )
            withContext(Main) {
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = adapter
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

    private fun withBitmap(expectedBitmap: Bitmap): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with bitmap: ")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is ImageView) return false
                val actualBitmap = view.drawable.toBitmap()
                return expectedBitmap.sameAs(actualBitmap)
            }
        }
    }

    private fun isRecyclerViewEmpty(recyclerViewMatcher: Matcher<View>): Boolean {
        val itemCount = getRecyclerViewItemCount(recyclerViewMatcher)
        return itemCount == 0
    }

    private fun getRecyclerViewItemCount(recyclerViewMatcher: Matcher<View>): Int {
        var itemCount = 0
        onView(recyclerViewMatcher).check { view, _ ->
            if (view is RecyclerView) {
                itemCount = view.adapter?.itemCount ?: 0
            }
        }
        return itemCount
    }
}
