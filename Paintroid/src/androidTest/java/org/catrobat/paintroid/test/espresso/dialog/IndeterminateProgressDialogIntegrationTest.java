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

package org.catrobat.paintroid.test.espresso.dialog;

import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;

@RunWith(AndroidJUnit4.class)
public class IndeterminateProgressDialogIntegrationTest {

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@UiThreadTest
	@Before
	public void setUp() throws Exception {
		IndeterminateProgressDialog.getInstance().show();

		ProgressBar progressBar = (ProgressBar) IndeterminateProgressDialog.getInstance().findViewById(R.id.progressBar);
		progressBar.setIndeterminateDrawable(new ColorDrawable(0xffff0000));
	}

	@After
	public void tearDown() throws Exception {
		IndeterminateProgressDialog.getInstance().dismiss();
	}

	@Test
	public void testDialogIsShown() {
		onView(withId(R.id.progressBar))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testDialogIsNotCancelableOnBack() {
		pressBack();

		onView(withId(R.id.progressBar))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testDialogIsNotCancelable() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		PointF point = new PointF(-metrics.widthPixels / 4, -metrics.heightPixels / 4);

		onView(withId(R.id.progressBar))
				.perform(touchAt(point))
				.check(matches(isDisplayed()));
	}
}
