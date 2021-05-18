/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.test.espresso;

import android.app.Activity;
import android.content.Context;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.presenter.MainActivityPresenter;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.mockito.Mockito.verify;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntegrationTest {

	@Mock
	private MainActivityContracts.MainView view;

	@InjectMocks
	private MainActivityPresenter presenter;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	@Test
	public void testMoreOptionsMenuAboutTextIsCorrect() {

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.pocketpaint_menu_about))
				.perform(click());

		Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
		String aboutTextExpected = context.getString(R.string.pocketpaint_about_content,
				context.getString(R.string.pocketpaint_about_license));

		onView(withText(aboutTextExpected))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testMoreOptionsMenuAboutClosesMoreOptions() {

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.pocketpaint_menu_about))
				.perform(click());

		pressBack();

		onView(withText(R.string.pocketpaint_menu_about))
				.check(doesNotExist());
	}

	@Test
	public void testHandleActivityResultWhenIntentIsNull() {
		launchActivityRule.getActivity().onActivityResult(0, Activity.RESULT_OK, null);
		MockitoAnnotations.initMocks(this);
		presenter.handleActivityResult(0, Activity.RESULT_OK, null);
		verify(view).superHandleActivityResult(0, Activity.RESULT_OK, null);
	}
}
