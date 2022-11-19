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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.RecyclerViewMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.assertRecyclerViewCount;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.onBottomNavigationView;

import androidx.annotation.ColorInt;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
				.check(assertRecyclerViewCount(count));
		return this;
	}

	public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
		return new RecyclerViewMatcher(recyclerViewId);
	}

	public LayerMenuViewInteraction performOpen() {
		onBottomNavigationView()
				.onLayersClicked();
		check(matches(isDisplayed()));
		return this;
	}

	public LayerMenuViewInteraction performSetOpacityTo(int opacityPercentage, int listPosition) {
		onView(withRecyclerView(R.id.pocketpaint_layer_side_nav_list)
				.atPositionOnView(listPosition, R.id.pocketpaint_layer_opacity_seekbar))
				.perform(setProgress(opacityPercentage));
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
		onView(withRecyclerView(R.id.pocketpaint_layer_side_nav_list)
				.atPositionOnView(listPosition, R.id.pocketpaint_layer_preview_container))
				.perform(click());
		return this;
	}

	public LayerMenuViewInteraction performStartDragging(int listPosition) {
		check(matches(isDisplayed()));
		onView(withIndex(withId(org.catrobat.paintroid.R.id.pocketpaint_layer_drag_handle), listPosition)).perform(click());
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

	public LayerMenuViewInteraction performToggleLayerVisibility(int position) {
		check(matches(isDisplayed()));
		onView(withIndex(withId(R.id.pocketpaint_checkbox_layer), position)).perform(click());
		return this;
	}

	public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
		return new TypeSafeMatcher<>() {
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

	public LayerMenuViewInteraction checkLayerAtPositionHasTopLeftPixelWithColor(int listPosition, @ColorInt final int expectedColor) {
		onView(withIndex(withId(org.catrobat.paintroid.R.id.pocketpaint_item_layer_image), listPosition))
				.check(matches(new org.hamcrest.TypeSafeMatcher<View>() {
					@Override
					public void describeTo(Description description) {
						description.appendText("Color at coordinates is " + Integer.toHexString(expectedColor));
					}

					@Override
					protected boolean matchesSafely(View view) {
						Bitmap bitmap = getBitmap(((ImageView) view).getDrawable());
						int actualColor = bitmap.getPixel(0, 0);
						return actualColor == expectedColor;
					}
				}));
		return this;
	}

	private Bitmap getBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}
}
