/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso

import android.app.Activity
import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.UserPreferences
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.serialization.CommandSerializer
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.contract.MainActivityContracts.Interactor
import org.catrobat.paintroid.contract.MainActivityContracts.MainView
import org.catrobat.paintroid.controller.ToolController
import org.catrobat.paintroid.presenter.MainActivityPresenter
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.ui.Perspective
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.io.File

@RunWith(AndroidJUnit4::class)
class MainActivityIntegrationTest {
    @Mock
    private val view: MainView? = null

    @Mock
    private val model: MainActivityContracts.Model? = null

    @Mock
    private val navigator: MainActivityContracts.Navigator? = null

    @Mock
    private val interactor: Interactor? = null

    @Mock
    private val topBarViewHolder: MainActivityContracts.TopBarViewHolder? = null

    @Mock
    private val drawerLayoutViewHolder: MainActivityContracts.DrawerLayoutViewHolder? = null

    @Mock
    private val workspace: Workspace? = null

    @Mock
    private val perspective: Perspective? = null

    @Mock
    private val toolController: ToolController? = null

    @Mock
    private val commandFactory: CommandFactory? = null

    @Mock
    private val commandManager: CommandManager? = null

    @Mock
    private val bottomBarViewHolder: MainActivityContracts.BottomBarViewHolder? = null

    @Mock
    private val bottomNavigationViewHolder: MainActivityContracts.BottomNavigationViewHolder? = null

    @Mock
    private val sharedPreferences: UserPreferences? = null

    @Mock
    private val context: Context? = null

    @Mock
    private val commandSerializer: CommandSerializer? = null

    @Mock
    private val internalMemoryPath: File? = null
    private var presenter: MainActivityPresenter? = null

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        val idlingResource = launchActivityRule.activity.idlingResource
        presenter = internalMemoryPath?.let {
            context?.let { it1 ->
                MainActivityPresenter(
                    launchActivityRule.activity,
                    view!!,
                    model!!,
                    workspace!!,
                    navigator!!,
                    interactor!!,
                    topBarViewHolder!!,
                    bottomBarViewHolder!!,
                    drawerLayoutViewHolder!!,
                    bottomNavigationViewHolder!!,
                    commandFactory!!,
                    commandManager!!,
                    perspective!!,
                    toolController!!, sharedPreferences!!, idlingResource, it1, it,
                    commandSerializer!!
                )
            }
        }
    }

    @Test
    fun testMoreOptionsMenuAboutTextIsCorrect() {
        onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.pocketpaint_menu_about))
            .perform(click())
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val aboutTextExpected: String = context.getString(
            R.string.pocketpaint_about_content,
            context.getString(R.string.pocketpaint_about_license)
        )
        onView(withText(aboutTextExpected))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testMoreOptionsMenuAboutClosesMoreOptions() {
        onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.pocketpaint_menu_about))
            .perform(click())
        pressBack()
        onView(withText(R.string.pocketpaint_menu_about))
            .check(doesNotExist())
    }

    @Test
    fun testHandleActivityResultWhenIntentIsNull() {
        launchActivityRule.activity.onActivityResult(0, Activity.RESULT_OK, null)
        presenter?.handleActivityResult(0, Activity.RESULT_OK, null)
        verify(view)?.superHandleActivityResult(0, Activity.RESULT_OK, null)
    }
}
