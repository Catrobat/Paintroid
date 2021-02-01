/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.dialog;

import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Build;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.test.annotation.UiThreadTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class IndeterminateProgressDialogIntegrationTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	private AlertDialog dialog;

	@UiThreadTest
	@Before
	public void setUp() {
		dialog = IndeterminateProgressDialog.newInstance(activityTestRule.getActivity());
		dialog.show();
	}

	@UiThreadTest
	@After
	public void tearDown() {
		dialog.dismiss();
	}

	@Test
	public void testDialogIsShown() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			onView(withId(R.id.pocketpaint_progress_bar))
					.check(matches(isDisplayed()));
		}
	}

	@RequiresApi(Build.VERSION_CODES.N)
	@Test
	public void testDialogIsNotCancelableOnBack() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			pressBack();

			onView(withId(R.id.pocketpaint_progress_bar))
					.check(matches(isDisplayed()));
		}
	}

	@RequiresApi(Build.VERSION_CODES.N)
	@Test
	public void testDialogIsNotCancelable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
			PointF point = new PointF((float) -metrics.widthPixels / 4, (float) -metrics.heightPixels / 4);

			onView(withId(R.id.pocketpaint_progress_bar))
					.perform(touchAt(point))
					.check(matches(isDisplayed()));
		}
	}
}
