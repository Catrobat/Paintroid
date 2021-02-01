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

package org.catrobat.paintroid.test.espresso.util.wrappers;

import android.view.Gravity;
import android.view.View;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.model.Layer;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.assertListViewCount;
import static org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.onBottomNavigationView;
import static org.hamcrest.Matchers.instanceOf;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public final class LayerMenuViewInteraction extends CustomViewInteraction {
	private LayerMenuViewInteraction() {
		super(onView(withId(R.id.pocketpaint_nav_view_layer)));
	}

	public static LayerMenuViewInteraction onLayerMenuView() {
		return new LayerMenuViewInteraction();
	}

	public ViewInteraction onButtonAdd() {
		return onView(withId(R.id.pocketpaint_layer_side_nav_button_add));
	}

	public ViewInteraction onButtonDelete() {
		return onView(withId(R.id.pocketpaint_layer_side_nav_button_delete));
	}

	public ViewInteraction onLayerList() {
		return onView(withId(R.id.pocketpaint_layer_side_nav_list));
	}

	public LayerMenuViewInteraction checkLayerCount(int count) {
		onLayerList()
				.check(assertListViewCount(count));
		return this;
	}

	public DataInteraction onLayerAt(int listPosition) {
		return onData(instanceOf(Layer.class))
				.inAdapterView(withId(R.id.pocketpaint_layer_side_nav_list))
				.atPosition(listPosition);
	}

	public LayerMenuViewInteraction performOpen() {
		onBottomNavigationView()
				.onLayersClicked();
		check(matches(isDisplayed()));
		return this;
	}

	public LayerMenuViewInteraction performClose() {
		check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_drawer_layout))
			.perform(DrawerActions.close(Gravity.END));
		return this;
	}

	public LayerMenuViewInteraction performSelectLayer(int listPosition) {
		check(matches(isDisplayed()));
		onLayerAt(listPosition)
				.perform(click());
		return this;
	}

	public LayerMenuViewInteraction performLongClickLayer(int listPosition) {
		check(matches(isDisplayed()));
		onLayerAt(listPosition)
				.perform(longClick());
		return this;
	}

	public LayerMenuViewInteraction performAddLayer() {
		check(matches(isDisplayed()));
		onButtonAdd()
				.perform(click());
		return this;
	}

	public LayerMenuViewInteraction performDeleteLayer() {
		check(matches(isDisplayed()));
		onButtonDelete()
				.perform(click());
		return this;
	}

	public LayerMenuViewInteraction perfomToggleLayerVisibility(int position) {
		check(matches(isDisplayed()));
		onView(withIndex(withId(R.id.pocketpaint_checkbox_layer), position)).perform(click());
		return this;
	}

	public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
		return new TypeSafeMatcher<View>() {
			int currentIndex = 0;

			@Override
			public void describeTo(Description description) {
				description.appendText("with index: ");
				description.appendValue(index);
				matcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				return matcher.matches(view) && currentIndex++ == index;
			}
		};
	}
}
